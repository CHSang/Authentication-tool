package vn.axonactive.authentication.user;

import java.util.Optional;
import java.util.AbstractMap.SimpleEntry;

import org.json.JSONObject;

import com.google.gson.Gson;

import vn.axonactive.authentication.domain.ConfigurationEnum;
import vn.axonactive.authentication.domain.utils.ApiUtils;
import vn.axonactive.authentication.domain.utils.ConfigPropertiesUtils;
import vn.axonactive.authentication.domain.utils.ConvertUtils;

public class UserService {
    private static final String GET_INFO_BY_USERNAME = ConfigPropertiesUtils.getProperty(ConfigurationEnum.CONFIG_PROPERTIES.getValue(), "api.getinfo.byusername");
    private static final String API_TOKEN     = "api.token";
    private String apiToken = ConfigPropertiesUtils.getProperty(ConfigurationEnum.CONFIG_PROPERTIES.getValue(), API_TOKEN);
    
    private Optional<EmployeeInfoDTO> getEmployeeAPI(String username) {
        String usernameEncoded = ConvertUtils.getInstance().encodeBase64ToString(username);
        
        Optional<JSONObject> jsonGetUser = ApiUtils.getJsonObjectFromPostAPI(GET_INFO_BY_USERNAME,
                new SimpleEntry<String, String> ("value", usernameEncoded),
                new SimpleEntry<String, String> ("tokenKey", apiToken));
        
        Gson gsonParser = new Gson();
        
        return jsonGetUser.filter(json -> json.length() > 0).map(json -> gsonParser.fromJson(json.toString(), EmployeeInfoDTO.class));
    }
    
    public Optional<UserDTO> getUserInfo(String username) {        
        return getEmployeeAPI(username).map(UserDTO::of);
    }
}
