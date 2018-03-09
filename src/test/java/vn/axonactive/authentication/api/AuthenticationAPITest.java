package vn.axonactive.authentication.api;

import java.util.Optional;

import javax.inject.Inject;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.Archive;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

import vn.axonactive.authentication.base.ArchiveBuilder;
import vn.axonactive.authentication.domain.ConfigurationEnum;
import vn.axonactive.authentication.domain.utils.ConfigPropertiesUtils;
import vn.axonactive.authentication.login.LoginService;
import vn.axonactive.authentication.sso.SSOService;

@RunWith(Arquillian.class)
@Ignore
public class AuthenticationAPITest {
    private String apiToken = ConfigPropertiesUtils.getProperty(ConfigurationEnum.CONFIG_PROPERTIES.getValue(), "api.token");
    private SSOService ssoService;
    
    @Inject
    AuthenticationAPI authenticationAPI;
    
    @Deployment
    public static Archive<?> createDeployment() {
        return ArchiveBuilder.create();
    }
    
    @Before
    public void logout() {
    	ssoService = new SSOService();
    	authenticationAPI.logout("192d168d73d166");
    }
    
    @Test
    public void getUser_Should_ReturnNullJSON_When_UserNotLoggedIn() {
        JSONObject objectAfterReturn = new JSONObject(authenticationAPI.getUser("192d168d73d166"));
        Assert.assertEquals(0, objectAfterReturn.length());
    }
    
    @Test
    public void addUser_Should_ReturnEmptyJSON_When_UserLoggedInSSOButNotLoginedInServer() {
    	
    	Assert.assertTrue(ssoService.addSession("192.168.73.166", "thchuong", "chuongID", apiToken));
    	
        JSONObject objectAfterReturn = new JSONObject(authenticationAPI.getUser("192d168d73d166"));
                
        Assert.assertTrue(objectAfterReturn.length() == 0);
        
        ssoService.logout("pknguyen", apiToken);
    }
    
    @Test
    public void notifyAlive_ShouldRestartDestroyIP_When_UserCallNotifyAlive() {
    	
    }
    
    @Test
    public void logout_Should_ReturnSuccess_When_InCaseLoggedIn() {
        SSOService ssoService = new SSOService();
        
        ssoService.addSession("192.168.73.166", "pknguyen", "fakeSessionID", apiToken);
        
        JSONObject objectAfterReturn = new JSONObject(authenticationAPI.logout("192d168d73d166"));
        Assert.assertTrue(objectAfterReturn.has("status"));
        Assert.assertTrue("ok".equals(objectAfterReturn.getString("status")));
        
        Optional<String> username = ssoService.getUserNameLoggedInByThisIp("192.168.73.166", apiToken);
        
        Assert.assertFalse(username.isPresent());

        ssoService.logout("pknguyen", apiToken);
    }

    @Test
    public void logout_Should_ReturnSuccess_When_InCaseNotLoggedIn() {
        SSOService ssoService = new SSOService();
        
        ssoService.logout("pknguyen", apiToken);
        
        JSONObject objectAfterReturn = new JSONObject(authenticationAPI.logout("192d168d73d166"));
        Assert.assertTrue(objectAfterReturn.has("status"));
        Assert.assertTrue("ok".equals(objectAfterReturn.getString("status")));

        ssoService.logout("pknguyen", apiToken);
    }
    
    @Test
    public void notifyAlive_Should_ReturnFail_When_NotLoggedIn() {
        JSONObject failStatus = new JSONObject();
        failStatus.put("status", "fail");
        
    	String result = authenticationAPI.notifyAlive("192d168d73d166");
    	Assert.assertEquals(failStatus.toString(), result);
    }
    
    @Test
    public void notifyAlive_Should_ReturnOK_When_LoggedIn() {
    	JSONObject successStatus = new JSONObject();
        successStatus.put("status", "ok");
        
        ssoService.addSession("192.168.73.166", "thchuong", "chuongID", apiToken);
        LoginService loginService = new LoginService();
        loginService.autoLogin("192.168.73.166", "chuongID");
        
    	String result = authenticationAPI.notifyAlive("192d168d73d166");
    	Assert.assertEquals(successStatus.toString(), result);
    }
    
    @Test
    public void addCallback_Should_Return() {
        JSONObject successStatus = new JSONObject();
        successStatus.put("status", "ok");
        
        String result = authenticationAPI.addCallback("192d168d73d166", "http://localhost:8080/hrtool/ip=192.168.73.166");
        Assert.assertEquals(successStatus.toString(), result);
    }
}
