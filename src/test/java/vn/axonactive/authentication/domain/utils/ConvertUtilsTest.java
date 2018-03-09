package vn.axonactive.authentication.domain.utils;

import org.junit.Before;
import org.junit.Test;

import vn.axonactive.authentication.domain.utils.ConvertUtils;

import org.junit.Assert;

/**
 * ConvertUtf8ToNormalUtilTest
 * @author thsang
 * Remark: Test case for ConvertUtf8ToNormalUtil class
 */
public class ConvertUtilsTest {
	
	String partern;
	
	@Before
	public void setup(){
		partern = "C\u00f4nv\u00eart String";
	}
	
	@Test
	public void convertUtf8ToNormal_Should_ReturnTrue_When_ConvertSuccessfully(){
		String afterConvert = ConvertUtils.getInstance().convertUft8String(partern);
		Assert.assertEquals(true, "Convert String".equals(afterConvert));
	}
	
	@Test
	public void convertUtf8ToNormal_Should_ReturnFalse_When_ConvertFail() {
		String afterConvert = ConvertUtils.getInstance().convertUft8String(partern);
		Assert.assertEquals(false, "Convert".equals(afterConvert));
	}
	
	@Test 
	public void getInstance_Should_ReturnInstance_When_FristTimeCall() {
		ConvertUtils instance = ConvertUtils.getInstance();
		Assert.assertNotNull(instance);
	}

	@Test
	public void getInstance_Should_ReturnInstance_When_LaterCall() {
		ConvertUtils instance = ConvertUtils.getInstance();
		ConvertUtils instance2 = ConvertUtils.getInstance();
		Assert.assertEquals(instance, instance2);
	}
	
	@Test
	public void encodeBase64ToString_Should_ReturnCorrectString_When_OriginalStringNotEmpty() {
	    String originalString = "592";
	    String encodedBase64String = ConvertUtils.getInstance().encodeBase64ToString(originalString);
	    Assert.assertEquals("NTky", encodedBase64String);
	}
	
	@Test
    public void encodeBase64ToString_Should_ReturnCorrectString_When_OriginalStringIsEmpty() {
        String originalString = "";
        String encodedBase64String = ConvertUtils.getInstance().encodeBase64ToString(originalString);
        Assert.assertEquals("", encodedBase64String);
    }
	
	@Test(expected = IllegalArgumentException.class)
    public void encodeBase64ToString_Should_ReturnNullString_When_OriginalStringnull() {
        String originalString = null;
        ConvertUtils.getInstance().encodeBase64ToString(originalString);
    }
	
	@Test
    public void decodeBase64ToString_Should_ReturnCorrectString_When_OriginalStringNotEmpty() {
        String originalString = "YXhvbmFjdGl2ZQ==";
        String decodedBase64String = ConvertUtils.getInstance().decodeBase64ToString(originalString);
        Assert.assertEquals("axonactive", decodedBase64String);
    }
	
	@Test
    public void decodeBase64ToString_Should_ReturnCorrectString_When_OriginalStringIsEmpty() {
	    String originalString = "";
        String decodedBase64String = ConvertUtils.getInstance().decodeBase64ToString(originalString);
        Assert.assertEquals("", decodedBase64String);
    }
	
	@Test(expected = IllegalArgumentException.class)
    public void decodeBase64ToString_Should_ReturnNullString_When_OriginalStringNull() {
        String originalString = null;
        ConvertUtils.getInstance().decodeBase64ToString(originalString);
    }
	
}
