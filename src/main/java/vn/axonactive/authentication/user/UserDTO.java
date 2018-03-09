package vn.axonactive.authentication.user;

import java.util.HashSet;
import java.util.Set;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserDTO {
    String userName;
    String fullName;
    String position;
    Set<String> sessionSet;
    
    public static UserDTO of(EmployeeInfoDTO employeeInfo) {
        UserDTO userInfo = new UserDTO();
        
        userInfo.userName = employeeInfo.account;
        userInfo.fullName = employeeInfo.fullName;
        userInfo.position = employeeInfo.position;
        userInfo.sessionSet = new HashSet<>();
        
        return userInfo;
    }
    
    public UserDTO(UserDTO from) {
        this.userName   = from.userName;
        this.fullName   = from.fullName;
        this.position   = from.position;
        this.sessionSet = from.sessionSet;
    }
}
