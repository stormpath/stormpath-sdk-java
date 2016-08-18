package com.stormpath.spring.boot.autoconfigure

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import com.stormpath.sdk.impl.io.ClasspathResource
import com.stormpath.sdk.impl.io.Resource
import org.springframework.beans.factory.annotation.Value
import org.testng.annotations.Test

import java.lang.annotation.Annotation
import java.lang.reflect.Field

import static org.testng.Assert.assertEquals

/**
 * This test is designed to compare {@link StormpathAutoConfiguration} with additional-spring-configuration-metadata.json
 * to make sure all properties are documented.
 */

class StormpathAutoConfigurationVersusMetadata {

    @Test
    def void verifyPropertiesInValuesAreInMetadata() {
        Set metaNames = getPropertyNamesFromMetadata()
        Set valueNames = getPropertiesFromConfig()

        def diff = valueNames.findAll {
            metaNames.contains(it) ? null : it
        }

        assertEquals diff.size(), 0, "Missing @Value annotations in metadata: ${diff}"
    }

    @Test
    public void verifyPropertiesInMetadataAreInValues() {
        Set metaNames = getPropertyNamesFromMetadata()
        Set valueNames = getPropertiesFromConfig()

        def diff = metaNames.findAll {
            valueNames.contains(it) ? null : it
        }

        assertEquals diff.size(), 3, "Missing metadata properties in @Values: ${diff}"
        assertEquals diff.sort().toString(), "[stormpath.client.cacheManager.defaultTti, stormpath.client.cacheManager.defaultTtl, stormpath.enabled]"
    }

    @Test
    public void generatePropertiesTableForDocumentation() {
        StringBuilder sb = new StringBuilder()

        def props = getPropertiesFromMetadata()
        props.each {
            sb.append(it.name + "\n")
            // for each character in the name, provide an underline for a level-2 heading
            it.name.each {
                sb.append("~")
            }
            sb.append("\n**Default Value:** ``" + it.defaultValue + "``\n")
            sb.append("\n")
            sb.append(it.description + "\n\n")
        }

        File file = new File("../docs/source/appendix/spring-boot-core-properties.rst");
        file.write sb.toString()
    }

    static def Set getPropertyNamesFromMetadata() throws IOException {
        def props = getPropertiesFromMetadata()
        def names = new HashSet()
        props.each {
            names << it.name
        }

        names
    }

    static def getPropertiesFromMetadata() throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        Resource from = new ClasspathResource("META-INF/additional-spring-configuration-metadata.json")

        TypeReference<HashMap<String,Object>> typeRef = new TypeReference<HashMap<String,Object>>() {};

        HashMap<String,Object> o = mapper.readValue(from.inputStream, typeRef);
        def props = o.get("properties")

        props
    }

    static def Set getPropertiesFromConfig() {
        Set<Field> valueFields = findFields(StormpathAutoConfiguration.class, Value)
        def names = new HashSet()
        for (Field field : valueFields) {
            Value value = field.getAnnotation(Value.class);
            def match = value.value() =~ /'([^']*)'/
            match.each {
                //println it[1]
                names << it[1]
            }
        }

        names
    }

    /**
     * @return null safe set
     */
    public static Set<Field> findFields(Class<?> clazz, Class<? extends Annotation> ann) {
        Set<Field> set = new HashSet<>();
        Class<?> c = clazz;
        while (c != null) {
            for (Field field : c.getDeclaredFields()) {
                if (field.isAnnotationPresent(ann)) {
                    set.add(field);
                }
            }
            c = c.getSuperclass();
        }
        return set;
    }


}
