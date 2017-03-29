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
package com.stormpath.sdk.impl.ds;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @since 0.1
 */
public class JacksonMapMarshaller implements MapMarshaller {

    private ObjectMapper objectMapper;

    private boolean prettyPrint = false;

    public JacksonMapMarshaller() {
        this.objectMapper = new ObjectMapper();
        this.objectMapper.configure(DeserializationFeature.USE_BIG_DECIMAL_FOR_FLOATS, true);
    }

    public ObjectMapper getObjectMapper() {
        return this.objectMapper;
    }

    public void setObjectMapper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public boolean isPrettyPrint() {
        return this.objectMapper.getSerializationConfig().isEnabled(SerializationFeature.INDENT_OUTPUT);
    }

    public void setPrettyPrint(boolean prettyPrint) {
        this.objectMapper.configure(SerializationFeature.INDENT_OUTPUT, true);
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

    @SuppressWarnings("unchecked")
    @Override
    public Map<String, Object> unmarshall(InputStream marshalled) {
        try {
            Object resolvedObj = this.objectMapper.readValue(marshalled, Object.class);
            if (resolvedObj instanceof Map) {
                return (Map<String, Object>) resolvedObj;
            } else if (resolvedObj instanceof List) {
                List list = (List) resolvedObj;
                Map<String, Object> ret = new LinkedHashMap<>();
                ret.put("items", list);
                ret.put("offset", 0);
                ret.put("limit", 100);
                ret.put("size", list.size());
                ret.put("href", "local");
                return ret;
            }
            throw new MarshalingException("Unable to convert InputStream String to Map. " +
                "Resolved Object is neither a Map or a List: " + resolvedObj.getClass());
        } catch (IOException e) {
            throw new MarshalingException("Unable to convert InputStream String to Map.", e);
        }
    }
}
