package vn.axonactive.authentication;

import java.util.TimerTask;

import vn.axonactive.authentication.login.LoginService;

public class IpDestroyerTask  extends TimerTask {
    String ip;
    
    LoginService loginService;
    
    public IpDestroyerTask(LoginService loginService, String ip) {
        super();
        this.ip = ip;
        this.loginService = loginService;
    }
    
    @Override
    public void run() {
        this.loginService.logoutbySession(this.ip);
    }
}
