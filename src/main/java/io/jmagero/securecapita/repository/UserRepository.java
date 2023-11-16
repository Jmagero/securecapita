package io.jmagero.securecapita.repository;

import io.jmagero.securecapita.domain.User;

import java.util.Collection;

public interface UserRepository <T extends User> {
//    Basic operation
    T create(T data, String verificationUrl);
    Collection<T>  list(int page, int pageSize);
    T get(Long id);
    T update(T data);
    Boolean delete(Long id);

    User getUserByEmail(String email);

    void sendVerificationCode(Long id, String verificationCode, String expirationDate);

    User verifyCode(String email, String code);

    void resetPassword(String email, String urlPasswordReset, String urlExpirationDate);

    User verifyPasswordKey(String key, String url);

    void renewPassword(String password, String renewPasswordUrl);

//    More complex Operations

}
