package io.jmagero.securecapita.service;

import io.jmagero.securecapita.domain.Role;
import io.jmagero.securecapita.domain.User;
import io.jmagero.securecapita.dto.UserDTO;
import io.jmagero.securecapita.dtomapper.UserDTOMapper;
import io.jmagero.securecapita.repository.RoleRepository;
import io.jmagero.securecapita.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.springframework.stereotype.Service;

import java.util.Date;

import static io.jmagero.securecapita.dtomapper.UserDTOMapper.fromUser;
import static org.apache.commons.lang3.RandomStringUtils.randomAlphabetic;
import static org.apache.commons.lang3.time.DateUtils.addSeconds;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService{
    private final UserRepository<User> userRepository;
    private final RoleRepository<Role> roleRepository;
    private static String DATE_FORMAT = "yyyy-MM-dd hh:mm:ss";
    @Override
    public UserDTO createUser(User user) {
        return mapToUserDTO(userRepository.create(user));
    }

    @Override
    public UserDTO getUserByEmail(String email) {
       return mapToUserDTO(userRepository.getUserByEmail(email));
    }

    @Override
    public void sendVerificationCode(UserDTO userDTO) {
        String expirationDate = DateFormatUtils.format(addSeconds(new Date(), 60), DATE_FORMAT);
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

    private UserDTO mapToUserDTO(User user){
        return fromUser(user, roleRepository.getRoleByUserId(user.getId()));
    }
}
