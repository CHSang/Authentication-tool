package vn.axonactive.authentication.domain.utils;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.util.Properties;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Spy;

public class ConfigPropertiesUtilsTest {

    @Spy
    Properties propertiesFile;
    
    @Test
    public void should_Create_Constructor() throws Exception {
        Constructor<ConfigPropertiesUtils> constructor = ConfigPropertiesUtils.class.getDeclaredConstructor();
        Assert.assertTrue(Modifier.isPrivate(constructor.getModifiers()));
        constructor.setAccessible(true);
        constructor.newInstance((Object[]) null);
    }
    
    @Test
    public void getProperty_Should_ReturnCorrectPropertyValue_When_InputCorrectPathAndKey() {
        Assert.assertNotNull("123456",                           ConfigPropertiesUtils.getProperty("test.properties", "test.api.token"));
        Assert.assertNotNull("com.sun.jndi.ldap.LdapCtxFactory", ConfigPropertiesUtils.getProperty("test.properties", "test.ldap.contextFactory"));
        Assert.assertNotNull("/dashboard",                       ConfigPropertiesUtils.getProperty("test.properties", "test.authentication.homepage.url"));
    }
    
    @Test
    public void getProperty_Should_ReturnCorrectPropertyValue_When_InputCorrectPathAndKeyAndParams(){
        String[] params1 = {"14"};
        String[] params2 = {"02/08/2017", "8:00", "06/08/2017", "17:00"};
        Assert.assertEquals("This request is unplanned. It should be sent 14 days before leave day.", ConfigPropertiesUtils.getProperty("test.properties", "test.warning.leaveRequestPage.annualLeave.email", params1));
        Assert.assertEquals("Your leave is from 02/08/2017 8:00 to 06/08/2017 17:00.",                ConfigPropertiesUtils.getProperty("test.properties", "test.info.leaveInfomation", params2));
    }
    
    @Test(expected=IllegalArgumentException.class)
    public void getProperty_Should_ReturnNull_When_InputCorrectPathAndWrongKey() {
        ConfigPropertiesUtils.getProperty("test.properties", "abcxyz");
    }
    
    @Test(expected=IllegalStateException.class)
    public void getProperty_Should_ThrowIllegalStateException_When_InputIncorrectPath() {
        ConfigPropertiesUtils.getProperty("some_code_that_i_used_to_know.properties", "api.token");
    }
    
    @Test
    public void getProperty_Should_NotLoadAgain_When_GetPropertyValueDoubletime() {
        Assert.assertEquals("123456",       ConfigPropertiesUtils.getProperty("test.properties", "test.api.token"));
        Assert.assertEquals("Scrum Master", ConfigPropertiesUtils.getProperty("test.properties", "test.employee.role.sm"));
    }
    
}
