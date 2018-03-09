package vn.axonactive.authentication.api;

import java.util.Optional;

import javax.inject.Inject;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.json.JSONObject;

import com.google.gson.Gson;

import vn.axonactive.authentication.IpStorage;
import vn.axonactive.authentication.user.UserDTO;

@Path("/authentication")
public class AuthenticationAPI {
    
    private static final String IP_INVALID = "Invalid IP";
    private static final String IP_NOT_NULL = "must contain \"ip\" param";
    
    private static final String URL_INVALID = "Invalid URL";
    private static final String URL_NOT_NULL = "must contain \"url\" param";
    
    @Inject
    AuthenticationService authenticationService;
    
    private JSONObject getSuccessJSON() {
        JSONObject successStatus = new JSONObject();
        successStatus.put("status", "ok");
        return successStatus;
    }
    
    private JSONObject getFailJSON() {
        JSONObject successStatus = new JSONObject();
        successStatus.put("status", "fail");
        return successStatus;
    }
    
    @GET
    @Path("/getUser")
    @Produces(MediaType.APPLICATION_JSON)
    public String getUser(@NotNull(message = IP_NOT_NULL)
                          @Pattern(message = IP_INVALID, regexp = "(\\d+)[d](\\d+)[d](\\d+)[d](\\d+)")
                          @QueryParam(value = "ip") 
                          String ipParam) {
        String ip = ipParam.replace('d', '.');        
        
        Optional<UserDTO> userInfo = authenticationService.getUser(ip);
        
        if (!userInfo.isPresent()) {
            return new JSONObject().toString();
        }
        
        Gson gson = new Gson();
        
        return gson.toJson(userInfo.get());
    }
    
    @GET
    @Path("/logout")
    @Produces(MediaType.APPLICATION_JSON)
    public String logout(@NotNull(message = IP_NOT_NULL)
                         @Pattern(message = IP_INVALID, regexp = "(\\d+)[d](\\d+)[d](\\d+)[d](\\d+)")
                         @QueryParam(value = "ip") 
                         String ipParam) {
        String ip = ipParam.replace('d', '.');        
        
        authenticationService.logout(ip);
                
        return getSuccessJSON().toString();
    }
    
    @GET
    @Path("/notifyAlive")
    @Produces(MediaType.APPLICATION_JSON)
    public String notifyAlive(@NotNull(message = IP_NOT_NULL)
                              @Pattern(message = IP_INVALID, regexp = "(\\d+)[d](\\d+)[d](\\d+)[d](\\d+)")
                              @QueryParam(value = "ip") 
                              String ipParam) {
        String ip = ipParam.replace('d', '.');        
        
        boolean isRestart = authenticationService.restartIPDestroyer(ip);
        
        if (isRestart) {
            return getSuccessJSON().toString();
        }
        
        return getFailJSON().toString();
    }
    
    @GET
    @Path("/isTimeout")
    @Produces(MediaType.APPLICATION_JSON)
    public String isTimeout(@NotNull(message = IP_NOT_NULL)
                              @Pattern(message = IP_INVALID, regexp = "(\\d+)[d](\\d+)[d](\\d+)[d](\\d+)")
                              @QueryParam(value = "ip") 
                              String ipParam) {
        String ip = ipParam.replace('d', '.');
        
        boolean isTimeout = IpStorage.getIpTimeoutMap().contains(ip);

        if (isTimeout) {
        	return getSuccessJSON().toString();
        }
        
        return getFailJSON().toString();
    }

    @GET
    @Path("/addCallbackGetOnLogout")
    @Produces(MediaType.APPLICATION_JSON)
    public String addCallback(@NotNull(message = IP_NOT_NULL)
                              @Pattern(message = IP_INVALID, regexp = "(\\d+)[d](\\d+)[d](\\d+)[d](\\d+)")
                              @QueryParam(value = "ip") 
                              String ipParam,
                              @NotNull(message = URL_NOT_NULL)
                              @Pattern(message = URL_INVALID, regexp = "^(?:(http[s]?|ftp[s]):\\/\\/)?([^:\\/\\s]+)(:[0-9]+)?((?:\\/\\w+)*\\/)([\\w\\-\\.]+[^#?\\s]+)([^#\\s]*)?(#[\\w\\-]+)?$")
                              @QueryParam(value = "url") 
                              String callback) {
        String ip = ipParam.replace('d', '.');
        
        boolean isSuccess = IpStorage.addCallBack(ip, callback);
        
        if (!isSuccess) {
            return getFailJSON().toString();
        }
        
        return getSuccessJSON().toString();
    }
}
