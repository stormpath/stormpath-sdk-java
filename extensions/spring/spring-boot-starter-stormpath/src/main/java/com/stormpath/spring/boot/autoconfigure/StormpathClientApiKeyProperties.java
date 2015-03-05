/*
 * Copyright 2015 Stormpath, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.stormpath.spring.boot.autoconfigure;

import com.stormpath.sdk.api.ApiKey;
import com.stormpath.sdk.impl.api.ClientApiKey;
import com.stormpath.sdk.impl.api.ClientApiKeyBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Properties;

/**
 * @since 1.0.RC4
 */
@ConfigurationProperties(prefix = "stormpath.apiKey")
public class StormpathClientApiKeyProperties {

    private static final Logger log = LoggerFactory.getLogger(StormpathClientApiKeyProperties.class);

    private String id;

    private String secret;

    private Resource file;

    private String fileIdPropertyName = "apiKey.id";

    private String fileSecretPropertyName = "apiKey.secret";

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    public Resource getFile() {
        return file;
    }

    public void setFile(Resource file) {
        this.file = file;
    }

    public String getFileIdPropertyName() {
        return fileIdPropertyName;
    }

    public void setFileIdPropertyName(String fileIdPropertyName) {
        this.fileIdPropertyName = fileIdPropertyName;
    }

    public String getFileSecretPropertyName() {
        return fileSecretPropertyName;
    }

    public void setFileSecretPropertyName(String fileSecretPropertyName) {
        this.fileSecretPropertyName = fileSecretPropertyName;
    }

    public ApiKey resolveApiKey() {

        String apiKeyId = null;
        String apiKeySecret = null;
        String apiKeyIdPropertyName = getFileIdPropertyName();
        String apiKeySecretPropertyName = getFileSecretPropertyName();

        //read the default $HOME/.stormpath/apiKey.properties file first:
        Resource resource = new FileSystemResource(ClientApiKeyBuilder.DEFAULT_API_KEY_PROPERTIES_FILE_LOCATION);

        if (resource.exists()) {
            try {
                Properties properties = toProperties(resource);
                apiKeyId = properties.getProperty(apiKeyIdPropertyName);
                apiKeySecret = properties.getProperty(apiKeySecretPropertyName);
            } catch (IOException e) {
                String msg = "Unable to read default apiKey.properties file " + resource +
                             ".  Ignoring and continuing with default spring boot properties resolution heuristics. " +
                             "This exception can be safely ignored.";
                log.debug(msg, e);
            }
        }

        resource = getFile();

        if (resource != null) {

            try {
                Properties properties = toProperties(resource);
                apiKeyId = properties.getProperty(apiKeyIdPropertyName);
                apiKeySecret = properties.getProperty(apiKeySecretPropertyName);
            } catch (IOException e) {
                String msg = "Cannot read specified stormpath.apiKey.file " + resource + ": " + e.getMessage();
                throw new IllegalArgumentException(msg, e);
            }
        }

        if (StringUtils.hasText(getId())) {
            apiKeyId = getId();
        }

        if (StringUtils.hasText(getSecret())) {
            apiKeySecret = getSecret();
        }

        if (!StringUtils.hasText(apiKeyId)) {
            String msg = "Unable to find a 'stormpath.apiKey.id' property value in any known spring boot " +
                         "configuration location as documented here:\n\n" +
                         "http://docs.spring.io/spring-boot/docs/current/reference/htmlsingle/#boot-features-external-config\n\n" +
                         "or an 'apiKey.id' property value in the default fallback location:\n\n" +
                         ClientApiKeyBuilder.DEFAULT_API_KEY_PROPERTIES_FILE_LOCATION + "\n\n" +
                         "Please ensure you configure a 'stormpath.apiKey.id' property value per spring boot " +
                         "configuration guidelines or ensure that an 'apiKey.id' property value exists in the " +
                         "default fallback location.";
            throw new IllegalStateException(msg);
        }

        if (!StringUtils.hasText(apiKeyId)) {
            String msg = "Unable to find a 'stormpath.apiKey.secret' property value in any known spring boot " +
                         "configuration location as documented here:\n\n" +
                         "http://docs.spring.io/spring-boot/docs/current/reference/htmlsingle/#boot-features-external-config\n\n" +
                         "or an 'apiKey.secret' property value in the default fallback location:\n\n" +
                         ClientApiKeyBuilder.DEFAULT_API_KEY_PROPERTIES_FILE_LOCATION + "\n\n" +
                         "Please ensure you configure a 'stormpath.apiKey.secret' property value per spring boot " +
                         "configuration guidelines or ensure that an 'apiKey.secret' property value exists in the " +
                         "default fallback location.";
            throw new IllegalStateException(msg);
        }

        return new ClientApiKey(apiKeyId, apiKeySecret);
    }

    private Properties toProperties(Resource resource) throws IOException {
        InputStream is = resource.getInputStream();
        Reader reader = new InputStreamReader(is, "ISO-8859-1");
        Properties properties = new Properties();
        properties.load(reader);
        return properties;
    }
}
