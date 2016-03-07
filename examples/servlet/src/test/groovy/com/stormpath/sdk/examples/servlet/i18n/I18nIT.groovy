package com.stormpath.sdk.examples.servlet.i18n

import com.jayway.restassured.RestAssured
import com.jayway.restassured.config.DecoderConfig
import org.testng.annotations.Test

import static com.jayway.restassured.RestAssured.given
import static org.hamcrest.Matchers.equalTo

/**
 *
 */
class I18nIT {
    @Test
    void testLoginPage() {
        given()
                .header("Accept-Language", "es")
                .when()
                    .get("http://localhost:8080/login")
                .then()
                    .body(
                        "html.head.title", equalTo("Iniciar Sesión"),
                        "**.findAll { it.@class == 'header' }.span.a", equalTo("Crear Cuenta"),
                        "**.findAll { it.@type == 'submit' }", equalTo("Iniciar Sesión"),
                        "**.findAll { it.@class == 'to-login' }", equalTo("Se te olvidó tu contraseña?")
                    )
    }

    @Test
    void testRegisterPage() {
        given()
                .header("Accept-Language", "es")
                .when()
                .get("http://localhost:8080/register")
                .then()
                .body(
                "html.head.title", equalTo("Crear Cuenta"),
                "**.findAll { it.@type == 'submit' }", equalTo("Crear Cuenta"),
                "**.findAll { it.@class == 'to-login' }", equalTo("Regresar a Iniciar Sesión")
        )
    }
}
