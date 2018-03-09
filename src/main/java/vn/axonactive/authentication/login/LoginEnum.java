package vn.axonactive.authentication.login;

public enum LoginEnum {
	LOGIN_PATH("/login"),
	HOME_PATH("/authentication/dashboard"),
	ERROR_PATH("/error"),
	USERNAME_ATTRIBUTE("username"),
	EMPLOYEE_NAME("employeename"),
	IP("ip");
	
	private String value;
	
	LoginEnum(String value) {
		this.value = value;
	}
	
	public String getValue() {
		return value;
	}
}
