package com.stormpath.sdk.impl.config;

import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.testng.PowerMockTestCase;
import org.testng.annotations.Test;

import java.util.HashMap;
import java.util.Map;

import static org.easymock.EasyMock.expect;
import static org.powermock.api.easymock.PowerMock.mockStaticPartial;
import static org.powermock.api.easymock.PowerMock.replayAll;
import static org.testng.Assert.assertEquals;

/**
 * @since 1.0.RC9
 *
 * NOTE: This *exact* same test was not working in groovy, which is why it's implemented in Java
 */
@PrepareForTest(EnvironmentVariablesPropertiesSource.class)
public class EnvironmentVariablesPropertiesSourceTest extends PowerMockTestCase {

    @Test
    public void testGetProperties() {

        Map<String, String> mockProps = new HashMap<String, String>();
        mockProps.put("my_special_key", "my_special_value");

        mockStaticPartial(System.class, "getenv");
        expect(System.getenv()).andReturn(mockProps);

        replayAll();

        Map<String, String> props = new EnvironmentVariablesPropertiesSource().getProperties();

        assertEquals(props.get("my_special_key"), "my_special_value");
    }

    @Test
    public void testGetPropertiesEmpty() {

        mockStaticPartial(System.class, "getenv");
        expect(System.getenv()).andReturn(null);

        replayAll();

        Map<String, String> props = new EnvironmentVariablesPropertiesSource().getProperties();

        assertEquals(props.size(), 0);
    }

}
