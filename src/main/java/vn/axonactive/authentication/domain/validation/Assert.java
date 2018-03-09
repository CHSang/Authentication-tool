package vn.axonactive.authentication.domain.validation;

import java.util.Optional;

import org.apache.commons.lang3.StringUtils;

public final class Assert {
	
	private Assert() {}
	
	public static void assertNotEmpty(String param) {
		assertNotEmpty(param, "The param must not be empty: ");
	}
	
	public static void assertNotEmpty(String param, String message) {
		if(StringUtils.isBlank(param)) {
			throw new IllegalArgumentException(message + " " + param);
		}
	}
	
	public static void assertNotEmpty(Optional<?> param, String message) {
		if(!param.isPresent()) {
			throw new IllegalArgumentException(message + param);
		}
	}
}
