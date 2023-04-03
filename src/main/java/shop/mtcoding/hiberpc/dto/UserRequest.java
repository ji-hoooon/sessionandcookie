package shop.mtcoding.hiberpc.dto;

import lombok.Getter;
import lombok.Setter;
import shop.mtcoding.hiberpc.model.User;

public class UserRequest {

    @Getter @Setter
    public static class JoinDto{
        private String username;
        private String password;
        private String email;

        // insert, update 할 때 보통 만들어준다.
        public User toEntity(){
            return User.builder()
                    .username(username)
                    .password(password)
                    .email(email)
                    .build();
        }
    }

    @Getter @Setter
    public static class LoginDto{
        private String username;
        private String password;
    }
}
