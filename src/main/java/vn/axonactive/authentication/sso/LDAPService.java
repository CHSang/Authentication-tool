package vn.axonactive.authentication.sso;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Logger;

import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;

import vn.axonactive.authentication.domain.ConfigurationEnum;
import vn.axonactive.authentication.domain.utils.ConfigPropertiesUtils;

public class LDAPService {
    private static final String LDAP_INIT_USERNAME               = ConfigPropertiesUtils.getProperty(ConfigurationEnum.LDAP_PROPERTIES.getValue(), "ldap.init.username");  
    private static final String LDAP_INIT_PASSWORD               = ConfigPropertiesUtils.getProperty(ConfigurationEnum.LDAP_PROPERTIES.getValue(), "ldap.init.password");  
    private static final String LDAP_INIT_SECURITYAUTHENTICATION = ConfigPropertiesUtils.getProperty(ConfigurationEnum.LDAP_PROPERTIES.getValue(), "ldap.init.securityauthentication");  
    private static final String LDAP_URL                         = ConfigPropertiesUtils.getProperty(ConfigurationEnum.LDAP_PROPERTIES.getValue(), "ldap.url");  
    private static final String LDAP_CONTEXTFACTORY              = ConfigPropertiesUtils.getProperty(ConfigurationEnum.LDAP_PROPERTIES.getValue(), "ldap.contextfactory");  
    private static final String LDAP_SEARCHBASE                  = ConfigPropertiesUtils.getProperty(ConfigurationEnum.LDAP_PROPERTIES.getValue(), "ldap.searchbase");  
    private static final String LDAP_SEARCHNAME                  = ConfigPropertiesUtils.getProperty(ConfigurationEnum.LDAP_PROPERTIES.getValue(), "ldap.searchname");  
    private static final String LDAP_DISTINGUISHEDNAME           = ConfigPropertiesUtils.getProperty(ConfigurationEnum.LDAP_PROPERTIES.getValue(), "ldap.distinguishedname");  
    
	private static final int INDEX_OF_FIRST_NAME = 0;
	private DirContext mainLDAPContext;
	private Map<String, String> environment = new HashMap<>();
	
	private static void logger(Exception e) {
		Logger.getLogger(e.toString());
	}

	
	private void initConnection() {
		environment.put(Context.INITIAL_CONTEXT_FACTORY,  LDAP_CONTEXTFACTORY);
		environment.put(Context.PROVIDER_URL,             LDAP_URL);
		environment.put(Context.SECURITY_AUTHENTICATION,  LDAP_INIT_SECURITYAUTHENTICATION);
		environment.put(Context.SECURITY_PRINCIPAL,       LDAP_INIT_USERNAME);
		environment.put(Context.SECURITY_CREDENTIALS,     LDAP_INIT_PASSWORD);
		try {
			this.mainLDAPContext = new InitialDirContext(new Hashtable<>(environment));
		} catch (NamingException e){
			throw new LDAPConnectionException(e);
		}
	}

	/**
	 * authenticate username and password from LDAP server
	 * @param username (Ex: mndang)
	 * @param password 
	 * @return true if username and password valid in LDAP server, otherwise return fail
	 * @throws LDAPConnectionException when cannot connect into LDAP server
	 */
	public boolean authenticate(String username, String password) {
		if (password == null || password.isEmpty() || username == null || username.isEmpty()) {
			return false;
		}
		Optional<String> userInfo = this.getUserInformationFromLDAP(username);
		if (userInfo.isPresent()) {
			this.environment.put(Context.SECURITY_PRINCIPAL  , userInfo.get());
			this.environment.put(Context.SECURITY_CREDENTIALS, password);
			try {
				this.mainLDAPContext = new InitialDirContext(new Hashtable<>(environment));
			} catch (NamingException e) {
				logger(e);
				return false;
			}
			return true;
		}
		return false;
	}
	
	/**
	 * get Employee Name in Vietnamese format 
	 * @param username (Ex: nmdang)
	 * @return employee name in Vietnamese format (Nguyen Manh Dang) or Optional.empty() if LDAP not contain that username
	 * @throws LDAPConnectionException when cannot connect into LDAP server
	 */
	public Optional<String> getEmployeeName(String username) {
		// Example of userInfo: CN=Dang Nguyen Manh,OU=Junior Class,OU=Users,OU=AAVN_HCM,DC=aavn,DC=local
	    Optional<String> userInfo = this.getUserInformationFromLDAP(username);
		if (userInfo.isPresent()) {
			String fullname = userInfo.get().split(",")[0].substring(3);
			String[] partsOfEmployeeName = fullname.split("\\s+");
			StringBuilder employeename = new StringBuilder();
			for (int i = 1; i < partsOfEmployeeName.length; i++) {
				employeename.append(partsOfEmployeeName[i]);
				employeename.append(" ");
			}
			return Optional.of(employeename.append(partsOfEmployeeName[INDEX_OF_FIRST_NAME]).toString());
		}
		return Optional.empty();
	}

	private Optional<String> getUserInformationFromLDAP(String username) {
		this.initConnection();
		NamingEnumeration<SearchResult> searchResults;
		SearchControls controls = new SearchControls();
		controls.setSearchScope(SearchControls.SUBTREE_SCOPE);
		controls.setCountLimit(1);
		controls.setTimeLimit(5000);

		StringBuilder searchString = new StringBuilder();
		searchString.append(LDAP_SEARCHNAME);
		searchString.append("=");
		searchString.append(username);
		try {
			searchResults = this.mainLDAPContext.search(LDAP_SEARCHBASE, searchString.toString(), controls);
			if (searchResults.hasMoreElements()) {
				SearchResult searchResult = searchResults.next();
				return Optional.of(searchResult.getAttributes()
				                               .get(LDAP_DISTINGUISHEDNAME)
				                               .get()
				                               .toString());
			} else {
				return Optional.empty();
			}
		} catch( NamingException e){
			logger(e);
			return Optional.empty();
		}
	}
}

