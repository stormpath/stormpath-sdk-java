package com.stormpath.sdk.impl.http.authc

import com.stormpath.sdk.client.AuthenticationScheme

import java.lang.reflect.Field
import java.lang.reflect.Modifier

import static org.junit.Assert.*
import org.junit.Test

/**
 * @since 0.9.3
 */
class DefaultRequestAuthenticatorFactoryTest {

    @Test
    public void test() {
        def requestAuthenticatorFactory = new DefaultRequestAuthenticatorFactory();

        assertTrue(requestAuthenticatorFactory.create(AuthenticationScheme.BASIC) instanceof BasicRequestAuthenticator)
        assertTrue(requestAuthenticatorFactory.create(AuthenticationScheme.SAUTHC1) instanceof SAuthc1RequestAuthenticator)
        assertTrue(requestAuthenticatorFactory.create(null) instanceof SAuthc1RequestAuthenticator)
    }

    @Test
    public void testException() {
        def authenticationScheme = AuthenticationScheme.BASIC

        //Let's remove the final modifier for requestAuthenticatorClassName attribute
        setNewValue(authenticationScheme, "requestAuthenticatorClassName", "this.package.does.not.exist.FooClass")

        def requestAuthenticatorFactory = new DefaultRequestAuthenticatorFactory();

        try {
            requestAuthenticatorFactory.create(authenticationScheme) instanceof BasicRequestAuthenticator
            fail("Should have thrown RuntimeException")
        } catch (RuntimeException ex) {
            assertEquals(ex.getMessage(), "There was an error instantiating this.package.does.not.exist.FooClass")
        }
    }

    //Set new value to final field
    private void setNewValue(Object object, String fieldName, Object value){
        Field field = object.class.getDeclaredField(fieldName);
        field.setAccessible(true);
        int modifiers = field.getModifiers();
        Field modifierField = field.getClass().getDeclaredField("modifiers");
        modifiers = modifiers & ~Modifier.FINAL;
        modifierField.setAccessible(true);
        modifierField.setInt(field, modifiers);
        field.set(object, value);
    }

}
