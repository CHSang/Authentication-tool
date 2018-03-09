package vn.axonactive.authentication.sso;

import java.util.Optional;
import java.util.AbstractMap.SimpleEntry;

import org.json.JSONArray;

import vn.axonactive.authentication.domain.utils.ApiUtils;
import vn.axonactive.authentication.domain.utils.ConfigPropertiesUtils;
import vn.axonactive.authentication.domain.ConfigurationEnum;

public class SSOService {
    
    private static final String SSO_API_GET                  = ConfigPropertiesUtils.getProperty(ConfigurationEnum.CONFIG_PROPERTIES.getValue(), "sso.api.get");  
    private static final String SSO_API_ADD                  = ConfigPropertiesUtils.getProperty(ConfigurationEnum.CONFIG_PROPERTIES.getValue(), "sso.api.add");  
    private static final String SSO_API_DELETE_BY_SESSION_ID = ConfigPropertiesUtils.getProperty(ConfigurationEnum.CONFIG_PROPERTIES.getValue(), "sso.api.deleteBySession");  
    private static final String SSO_API_DELETE_BY_USER       = ConfigPropertiesUtils.getProperty(ConfigurationEnum.CONFIG_PROPERTIES.getValue(), "sso.api.deleteByUser");  

	private static final String SSO_PARAMETER_TOKEN = "tokenKey";
	private static final String ERROR_CODE = "error";
	private static final String STATUS = "status";
	private static final String USERNAME = "username";
	private static final String OK = "ok";
	private static final String STRING_NULL = "null";

	private JSONArray addAPI(String ip, String username, String sessionID, String token) {
		return ApiUtils.getJsonArrayFromPostAPI(SSO_API_ADD,
				new SimpleEntry<>("ip", this.convertIPToRightFormat(ip)), new SimpleEntry<>("userName", username),
				new SimpleEntry<>("sessionId", sessionID), new SimpleEntry<>(SSO_PARAMETER_TOKEN, token))
				.orElse(new JSONArray());
	}

	private JSONArray getAPI(String ip, String token) {
		return ApiUtils.getJsonArrayFromGetAPI(SSO_API_GET,
				new SimpleEntry<>("ip", this.convertIPToRightFormat(ip)), new SimpleEntry<>(SSO_PARAMETER_TOKEN, token))
				.orElse(new JSONArray());
	}

	private JSONArray deleteByUserApi(String username, String token) {
		return ApiUtils
				.getJsonArrayFromPostAPI(SSO_API_DELETE_BY_USER,
						new SimpleEntry<>("name", username), new SimpleEntry<>(SSO_PARAMETER_TOKEN, token))
				.orElse(new JSONArray());
	}

	private JSONArray deleteBySessionID(String sessionId, String token) {
		return ApiUtils
				.getJsonArrayFromPostAPI(SSO_API_DELETE_BY_SESSION_ID,
						new SimpleEntry<>("id", sessionId), new SimpleEntry<>(SSO_PARAMETER_TOKEN, token))
				.orElse(new JSONArray());
	}

	private String getUserNameResultFromGetApi(JSONArray jsonFromGetApi) {
		return Optional.ofNullable(jsonFromGetApi).map(jsarrHasLength -> jsarrHasLength.getJSONObject(0))
				.filter(jo -> jo.has(USERNAME)).map(joHaveUsername -> joHaveUsername.getString(USERNAME))
				.filter(us -> !us.equals(STRING_NULL)).orElse(ERROR_CODE);
	}

	private String getStatusResultFromApi(JSONArray jsonFromAddApi) {
		return Optional.ofNullable(jsonFromAddApi).map(jsarrHasLength -> jsarrHasLength.getJSONObject(0))
				.filter(jo -> jo.has(STATUS)).map(joHaveUsername -> joHaveUsername.getString(STATUS))
				.orElse(ERROR_CODE);
	}

	private String convertIPToRightFormat(String clientIPAddress) {
		return clientIPAddress.replace(".", "d");
	}

	/**
	 * update session id with the username, the IP through API to server
	 * 
	 * @param ip
	 *            IP of target PC
	 * @param username
	 *            user's username LDAP account
	 * @param sessionId
	 *            user's session ID
	 * @param token
	 *            token from config
	 * @return true when success, fail only when API add fail
	 */
	public boolean addSession(String ip, String username, String sessionId, String token) {		
		JSONArray result = addAPI(this.convertIPToRightFormat(ip), username, sessionId, token);
		return OK.equals(getStatusResultFromApi(result));
	}

	/**
	 * Delete the session by session ID
	 * 
	 * @param sessionId
	 *            user's session ID
	 * @param token
	 *            token from config
	 * @deprecated this method is old or not correct anymore
	 * @return true if success, otherwise false
	 */
	@Deprecated
	public boolean deleteSession(String sessionId, String token) {
		JSONArray jsonFromDeleteBySessionApi = deleteBySessionID(sessionId, token);
		return OK.equals(getStatusResultFromApi(jsonFromDeleteBySessionApi));
	}

	/**
	 * Call API to get username that logged in by this ip
	 * 
	 * @param ip
     *            IP of target PC
     * @param token
     *            token from config
     * @return username, if not logged in will return Optional.emtpy
	 */
	public Optional<String> getUserNameLoggedInByThisIp(String ip, String token) {
		JSONArray jsonFromGetApi = getAPI(ip, token);		
		return Optional.of(getUserNameResultFromGetApi(jsonFromGetApi)).filter(username -> !ERROR_CODE.equals(username));
	}

	/**
	 * Logout from server by delete session on server
	 * 
	 * @param username
	 *            user's username LDAP account
	 * @param token
	 *            token from Config
	 * @return true when delete successfully, otherwise false
	 */
	public boolean logout(String username, String token) {
		JSONArray jsonFromDeleteByUserApi = deleteByUserApi(username, token);
		return OK.equals(getStatusResultFromApi(jsonFromDeleteByUserApi));
	}
	
}
