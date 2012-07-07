/*
 * Copyright 2012 Stormpath, Inc.
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
package com.stormpath.sdk.impl.ds;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig;
import org.codehaus.jackson.type.TypeReference;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @since 0.1
 */
public class JacksonMapMarshaller implements MapMarshaller {

    private ObjectMapper objectMapper;

    private boolean prettyPrint = false;

    public JacksonMapMarshaller() {
        this.objectMapper = new ObjectMapper();
    }

    public ObjectMapper getObjectMapper() {
        return this.objectMapper;
    }

    public void setObjectMapper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public boolean isPrettyPrint() {
        return this.objectMapper.getSerializationConfig().isEnabled(SerializationConfig.Feature.INDENT_OUTPUT);
    }

    public void setPrettyPrint(boolean prettyPrint) {
        this.objectMapper.setSerializationConfig(
                this.objectMapper.getSerializationConfig().with(SerializationConfig.Feature.INDENT_OUTPUT)
        );
    }

    @Override
    public String marshal(Map map) {
        try {
            return this.objectMapper.writeValueAsString(map);
        } catch (IOException e) {
            throw new MarshalingException("Unable to convert Map to JSON String.", e);
        }
    }

    @Override
    public Map unmarshal(String marshalled) {
        try {
            TypeReference<LinkedHashMap<String,Object>> typeRef = new TypeReference<LinkedHashMap<String,Object>>(){};
            return this.objectMapper.readValue(marshalled, typeRef);
        } catch (IOException e) {
            throw new MarshalingException("Unable to convert JSON String to Map.", e);
        }
    }
}
