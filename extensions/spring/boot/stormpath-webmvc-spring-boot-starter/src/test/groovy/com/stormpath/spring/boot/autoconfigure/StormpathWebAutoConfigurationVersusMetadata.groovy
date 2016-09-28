package com.stormpath.spring.boot.autoconfigure

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import com.stormpath.sdk.impl.io.ClasspathResource
import com.stormpath.sdk.impl.io.Resource
import com.stormpath.spring.config.AccessTokenCookieProperties
import com.stormpath.spring.config.RefreshTokenCookieProperties
import com.stormpath.spring.mvc.*
import org.springframework.beans.factory.annotation.Value
import org.testng.annotations.Test

import java.lang.annotation.Annotation
import java.lang.reflect.Field

import static org.testng.Assert.assertEquals
/**
 * This test is designed to compare {@link StormpathWebMvcAutoConfiguration} with additional-spring-configuration-metadata.json
 * to make sure all properties are documented.
 */

class StormpathWebAutoConfigurationVersusMetadata {

    @Test
    def void verifyPropertiesInValuesAreInMetadata() {
        Set metaNames = getPropertyNamesFromMetadata()
        Set valueNames = getPropertiesFromConfig()

        def diff = valueNames.findAll {
            metaNames.contains(it) ? null : it
        }

        diff.each {
            println it
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

        // stormpath.application.href is in AbstractStormpathConfiguration.java
        // idSite.resultUri in StormpathWebSecurityConfigurer.java
        // *.fieldOrder used in AbstractControllerConfig, but not in @Value annotation
        assertEquals diff.size(), 4, "Missing metadata properties in @Values:"
        assertEquals diff.sort().toString(), "[stormpath.application.href, stormpath.web.idSite.resultUri, stormpath.web.login.form.fieldOrder, stormpath.web.register.form.fieldOrder]"
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

        File file = new File("../docs/source/appendix/spring-boot-web-properties.rst");
        file.write sb.toString()
    }

    static def Set getPropertyNamesFromMetadata() throws IOException {
        def props = getPropertiesFromMetadata()
        def names = new HashSet()
        props.each {
            if (!it.name.contains("form.fields") && !it.name.contains("me.expand")) {
                names << it.name
            }
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
        Set<Field> valueFields = findFields(StormpathWebMvcAutoConfiguration.class, Value)
        // add additional fields from other configuration classes
        //valueFields.addAll(findFields(AbstractStormpathConfiguration.class, Value))
        valueFields.addAll(findFields(AccessTokenCookieProperties.class, Value))
        valueFields.addAll(findFields(ChangePasswordControllerConfig.class, Value))
        valueFields.addAll(findFields(ForgotPasswordControllerConfig.class, Value))
        valueFields.addAll(findFields(LoginControllerConfig.class, Value))
        valueFields.addAll(findFields(LogoutControllerConfig.class, Value))
        valueFields.addAll(findFields(RefreshTokenCookieProperties.class, Value))
        valueFields.addAll(findFields(RegisterControllerConfig.class, Value))
        valueFields.addAll(findFields(VerifyControllerConfig.class, Value))
        def names = new HashSet()
        for (Field field : valueFields) {
            Value value = field.getAnnotation(Value.class);
            def match = value.value() =~ /'([^']*)'/
            match.each {
                // 1. if key doesn't start with stormpath, ignore it
                // 2. if key contains 'form.fields', ignore it
                // 3. if key contains 'me.expand', ignore it
                // it should be possible to do this with the above regex, but I was unable to make it work
                if (it[1].startsWith("stormpath") && !it[1].contains("form.fields") && !it[1].contains("me.expand")) {
                    // the regex above captures some values instead of keys, so ignore thoses
                    if (!it[1].contains("stormpath/")) {
                        names << it[1]
                    }
                }
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
