package vn.axonactive.authentication.sso;

import javax.ejb.ApplicationException;

@ApplicationException
public class LDAPConnectionException extends RuntimeException {

	private static final long serialVersionUID = -81432063676120355L;

	public LDAPConnectionException() {
		super("Connection to LDAP Server fail");
	}

	public LDAPConnectionException(Exception variable) {
		super("Connection to LDAP Server fail, root cause is " + variable.toString());
	}

}
