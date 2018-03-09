package vn.axonactive.authentication.api;

import java.util.Optional;

import javax.ejb.Singleton;
import javax.inject.Inject;

import vn.axonactive.authentication.login.LoginService;
import vn.axonactive.authentication.user.UserDTO;

@Singleton
public class AuthenticationService {    
    @Inject
    private LoginService loginService;
    
    public Optional<UserDTO> getUser(String ip) {
        return loginService.getUser(ip);
    }
    
    public void logout(String ip) {
        loginService.logoutByUser(ip);
    }    
    
    public boolean restartIPDestroyer(String ip) {
        return loginService.restartIpDestroyer(ip);
    }
}
