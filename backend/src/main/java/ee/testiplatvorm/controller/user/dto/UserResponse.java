package ee.testiplatvorm.controller.user.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
public class UserResponse {

    private Integer userId;
    private String firstName;
    private String lastName;
    private String email;
    private List<String> groupNames = new ArrayList<>();
    private String roleName;
    private String status;

    public UserResponse(Integer userId, String firstName, String lastName, String email, String roleName, String status) {
        this.userId = userId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.roleName = roleName;
        this.status = status;
    }
}
