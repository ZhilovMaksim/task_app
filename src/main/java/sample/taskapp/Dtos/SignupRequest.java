package sample.taskapp.Dtos;

import lombok.Data;

@Data
public class SignupRequest {
    private String username;
    private String email;
    private String password;
    private String role = "USER";

}
