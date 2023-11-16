package io.jmagero.securecapita.service;

import io.jmagero.securecapita.domain.User;
import io.jmagero.securecapita.dto.UserDTO;

public interface UserService {

    UserDTO createUser(User user);


    UserDTO getUserByEmail(String email);

    void sendVerificationCode(UserDTO userDTO);

    User getUser(String email);

    UserDTO verifyCode(String email, String code);

    void resetPassword(String email);

    UserDTO resetPasswordKey(String key);

    void renewPassword(String key, String password, String confirmPassword);
}
