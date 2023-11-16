package io.jmagero.securecapita.service;

import io.jmagero.securecapita.domain.Role;
import io.jmagero.securecapita.domain.User;
import io.jmagero.securecapita.dto.UserDTO;
import io.jmagero.securecapita.exception.ApiException;
import io.jmagero.securecapita.repository.RoleRepository;
import io.jmagero.securecapita.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.Date;
import java.util.UUID;

import static io.jmagero.securecapita.dtomapper.UserDTOMapper.fromUser;
import static io.jmagero.securecapita.enumeration.VerificationType.ACCOUNT;
import static io.jmagero.securecapita.enumeration.VerificationType.PASSWORD;
import static org.apache.commons.lang3.RandomStringUtils.randomAlphabetic;
import static org.apache.commons.lang3.time.DateUtils.addDays;
import static org.apache.commons.lang3.time.DateUtils.addSeconds;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService{
    private final UserRepository<User> userRepository;
    private final RoleRepository<Role> roleRepository;
    private static String DATE_FORMAT = "yyyy-MM-dd hh:mm:ss";
    @Override
    public UserDTO createUser(User user) {
        String verificationUrl = getVerificationUrl(UUID.randomUUID().toString(), ACCOUNT.getType());
        return mapToUserDTO(userRepository.create(user,verificationUrl ));
    }

    @Override
    public UserDTO getUserByEmail(String email) {
       return mapToUserDTO(userRepository.getUserByEmail(email));
    }

    @Override
    public void sendVerificationCode(UserDTO userDTO) {
        String expirationDate = DateFormatUtils.format(addDays(new Date(), 1), DATE_FORMAT);
        String verificationCode = randomAlphabetic(6).toUpperCase();
        userRepository.sendVerificationCode(userDTO.getId(), verificationCode, expirationDate);
//        sendSMS(userDTO.getPhone(), "From : SecureCapita \nVerification code \n " + verificationCode);
    }

    public User getUser(String email){
        return userRepository.getUserByEmail(email);
    }

    @Override
    public UserDTO verifyCode(String email, String code) {
        return mapToUserDTO(userRepository.verifyCode(email, code));
    }

    @Override
    public void resetPassword(String email) {
        String urlExpirationDate = DateFormatUtils.format(addDays(new Date(), 1), DATE_FORMAT);
        String passwordResetUrl =  getVerificationUrl(UUID.randomUUID().toString(), PASSWORD.getType());
        userRepository.resetPassword(email,urlExpirationDate,passwordResetUrl);
    }

    @Override
    public UserDTO resetPasswordKey(String key) {
        String passwordUrl = getVerificationUrl(key, PASSWORD.getType());
        return mapToUserDTO(userRepository.verifyPasswordKey(key, passwordUrl));
    }

    @Override
    public void renewPassword(String key, String password, String confirmPassword) {
        if(!password.equals(confirmPassword)) throw new ApiException("Password don't match");
        String renewPasswordUrl = getVerificationUrl(key, PASSWORD.getType());
        userRepository.renewPassword(password, renewPasswordUrl);
    }

    private UserDTO mapToUserDTO(User user){
        return fromUser(user, roleRepository.getRoleByUserId(user.getId()));
    }

    private String getVerificationUrl(String key, String type) {
        return ServletUriComponentsBuilder.fromCurrentContextPath().path("/users/verify/" +type +"/" + key).toUriString();
    }
}
