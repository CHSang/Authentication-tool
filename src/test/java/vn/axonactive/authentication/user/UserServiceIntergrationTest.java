package vn.axonactive.authentication.user;

import java.util.Optional;

import org.junit.Assert;
import org.junit.Test;

public class UserServiceIntergrationTest {
    
    UserService userService = new UserService();
    
    @Test
    public void getUserInfo_Should_Return_Infomation_When_UserNameIsCorrect() {
        Optional<UserDTO> userInfo = userService.getUserInfo("tshen");

        Assert.assertTrue(userInfo.isPresent());
        Assert.assertNotNull(userInfo.get().getFullName());
        Assert.assertNotNull(userInfo.get().getUserName());
        Assert.assertNotNull(userInfo.get().getPosition());
        Assert.assertNotNull(userInfo.get().getSessionSet());
    }    

    @Test
    public void getUserInfo_Should_Return_Infomation_When_UserNameIsNotCorrect() {
        Optional<UserDTO> userInfo = userService.getUserInfo("some_username_that_I_used_to_know");

        Assert.assertFalse(userInfo.isPresent());
    }
}
