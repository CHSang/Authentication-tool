package vn.axonactive.authentication.domain.utils;

import java.util.AbstractMap.SimpleEntry;
import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.util.Optional;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import vn.axonactive.authentication.domain.utils.ApiUtils;

public class ApiUtilsTest {
	private String GET_ALL_EMPLOYEE_API = "";
	
	private String CHECK_AUTO_LOGIN_API = "";
	
	private String GET_MANAGER = "";

	private Optional<JSONArray> resultJsonArray;
	private Optional<JSONObject> resultJSONObject;

	
	@Before
	public void setup() {
		GET_ALL_EMPLOYEE_API = "http://test-employeecontact.axonactive.vn.local/api/employee/basic/getAll";
		CHECK_AUTO_LOGIN_API = "http://test-employeecontact.axonactive.vn.local/api/singleSignOn/get";
		GET_MANAGER = "http://test-employeecontact.axonactive.vn.local/api/employee/getManagersByEmployeeIndex";
	}

	@Test
    public void should_Create_Constructor() throws Exception {
        Constructor<ApiUtils> constructor = ApiUtils.class.getDeclaredConstructor();
        Assert.assertTrue(Modifier.isPrivate(constructor.getModifiers()));
        constructor.setAccessible(true);
        constructor.newInstance((Object[]) null);
    }


	@Test
	public void getJsonArrayFromPostAPI_Should_ReturnTrue_When_CallGetAmployeeAPISuccessfully() {
		resultJsonArray = ApiUtils.getJsonArrayFromPostAPI(GET_ALL_EMPLOYEE_API, new SimpleEntry<>("tokenKey", "123456"));
		Assert.assertEquals(true, resultJsonArray.get().length() > 0);
	}
	
	@Test(expected = RuntimeException.class)
	public void getJsonArrayFromPostAPI_Should_ReturnEmptyJSONArray_When_CallGetAmployeeAPIWithWrongURL() {
		resultJsonArray = ApiUtils.getJsonArrayFromPostAPI("", new SimpleEntry<>("tokenKey", "12345"));
	}

	@Test
	public void getJsonArrayFromPostAPI_Should_ReturnOptionalEmpty_When_CallGetAmployeeAPIWithWrongTokenKey() {
		resultJsonArray = ApiUtils.getJsonArrayFromPostAPI(GET_ALL_EMPLOYEE_API, new SimpleEntry<>("tokenKey", "000000"));
		Assert.assertEquals(true, resultJsonArray.get().length() == 1);
	}	
	
	@Test
    public void getJsonObjectFromPostAPI_Should_ReturnTrue_When_CallGetAmployeeAPISuccessfully() {
	    resultJSONObject = ApiUtils.getJsonObjectFromPostAPI(GET_MANAGER, new SimpleEntry<>("value", "Njg2"), new SimpleEntry<>("tokenKey", "123456"));
        Assert.assertEquals(true, resultJSONObject.get().length() > 0);
    }
	
	@Test
    public void getJsonObjectFromPostAPI_Should_ReturnEmptyJSONObject_When_CallGetAmployeeAPIWithWrongURL() {
        resultJSONObject = ApiUtils.getJsonObjectFromPostAPI("http://test-employeecontact.axonactive.vn.local/api/employee/getManagersBy", new SimpleEntry<>("value", "Njg2"), new SimpleEntry<>("tokenKey", "123456"));
        Assert.assertFalse(resultJSONObject.isPresent());
    }
	
	@Test
    public void getJsonObjectFromPostAPI_Should_ReturnEmptyJSONObject_When_CallGetAmployeeAPIWithWrongValue() {
        resultJSONObject = ApiUtils.getJsonObjectFromPostAPI(GET_MANAGER, new SimpleEntry<>("value", "jg"), new SimpleEntry<>("tokenKey", "123456"));
        Assert.assertTrue(resultJSONObject.get().length() == 0);
    }
	
	@Test
    public void getJsonObjectFromPostAPI_Should_ReturnEmptyJSONObject_When_CallGetAmployeeAPIWithWrongTokenKey() {
        resultJSONObject = ApiUtils.getJsonObjectFromPostAPI(GET_MANAGER, new SimpleEntry<>("value", "Njg2"), new SimpleEntry<>("tokenKey", "000000"));
        Assert.assertTrue(resultJSONObject.get().length() == 0);
    }
	
	
	@Test
    public void getJsonObjectFromPostAPI_Should_ReturnEmptyJSONObject_When_CallGetAmployeeAPIWithMissingParam() {
        resultJSONObject = ApiUtils.getJsonObjectFromPostAPI(GET_MANAGER, new SimpleEntry<>("tokenKey", "123456"));
        Assert.assertFalse(resultJSONObject.isPresent());
    }
	
	
	@Test
	public void postSimpleMethod_Should_ThrowException_When_CallAutoLogInAPIFail() {
		resultJsonArray = ApiUtils.getJsonArrayFromPostAPI(CHECK_AUTO_LOGIN_API, new SimpleEntry<>("tokenKey", "12345"));
		Assert.assertEquals(Optional.empty(), resultJsonArray);
	}
	
	@Test
	public void getJsonArrayFromGetAPI_Should_ReturnNotEmpty_When_CallSuccessfully() {
		Optional<JSONArray> result = ApiUtils.getJsonArrayFromGetAPI(CHECK_AUTO_LOGIN_API,
				new SimpleEntry<>("ip", "192d168d73d57"), new SimpleEntry<>("tokenKey", "123456"));
		Assert.assertTrue(result.isPresent());
	}

	@Test
	public void getJsonArrayFromGetAPI_Should_ReturnEmpty_When_CallWrongAPI() {
		String apiURL = "http://test-employeecontact.axonactive.vn.local/api/singleSignOn/get/get";
		Optional<JSONArray> result = ApiUtils.getJsonArrayFromGetAPI(apiURL, new SimpleEntry<>("ip", "192d168d73d163"),
				new SimpleEntry<>("tokenKey", "123456"));
		Assert.assertFalse(result.isPresent());
	}
	
	@Test
	public void getJsonArrayFromGetAPI_Should_ReturnEmpty_When_CallWrongTokenKey() {
		Optional<JSONArray> result = ApiUtils.getJsonArrayFromGetAPI(CHECK_AUTO_LOGIN_API, new SimpleEntry<>("ip", "192d168d73d163"),
				new SimpleEntry<>("tokenKey", "0000"));
		Assert.assertTrue(result.isPresent());
	}		
}
