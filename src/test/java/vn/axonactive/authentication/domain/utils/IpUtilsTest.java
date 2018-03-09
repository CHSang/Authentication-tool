package vn.axonactive.authentication.domain.utils;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;

import javax.servlet.http.HttpServletRequest;

import org.junit.Test;
import org.mockito.Mockito;

import vn.axonactive.authentication.domain.utils.IpUtils;

import org.junit.Assert;
import org.junit.Ignore;

public class IpUtilsTest {
	
	@Test
    public void should_Create_Constructor() throws Exception {
        Constructor<IpUtils> constructor = IpUtils.class.getDeclaredConstructor();
        Assert.assertTrue(Modifier.isPrivate(constructor.getModifiers()));
        constructor.setAccessible(true);
        constructor.newInstance((Object[]) null);
    }

	@Ignore
	@Test
	public void getClientIPAddress_ShouldReturnAIp_When_RequesttIsValid() {		
		HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
		Mockito.when(request.getHeader("X-Forwarded-For")).thenReturn("192.168.1.1");
		
		String result = IpUtils.getClientIPAddress(request);
		
		Assert.assertEquals(true, "192.168.1.1".equals(result));
	}
	@Ignore
	@Test
	public void getClientIPAddress_Should_ReturnNull_When_RequestIsInvalid() {		
		HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
		Mockito.when(request.getHeader("X-Forwarded-For")).thenReturn(null);
		
		String result = IpUtils.getClientIPAddress(request);
				
		Assert.assertNull(result);
	}
	
	@Test
	public void toIpUrlFixedFormat_Should_ReturnNewStringFixed_When_StringIsValid() {
		String result = IpUtils.toIpUrlFixedFormat("192.168.1.1");
		
		Assert.assertEquals(true, "192d168d1d1".equals(result));
	}
}
