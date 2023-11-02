package io.jmagero.securecapita.service;

import io.jmagero.securecapita.domain.User;
import io.jmagero.securecapita.dto.UserDTO;
import io.jmagero.securecapita.dtomapper.UserDTOMapper;
import io.jmagero.securecapita.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.springframework.stereotype.Service;

import java.util.Date;

import static io.jmagero.securecapita.utils.SmsUtils.sendSMS;
import static org.apache.commons.lang3.RandomStringUtils.randomAlphabetic;
import static org.apache.commons.lang3.time.DateUtils.addDays;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService{
    private final UserRepository<User> userRepository;
    private static String DATE_FORMAT = "yyyy-MM-dd hh:mm:ss";
    @Override
    public UserDTO createUser(User user) {
        return UserDTOMapper.fromUser(userRepository.create(user));
    }

    @Override
    public UserDTO getUserByEmail(String email) {
       return UserDTOMapper.fromUser(userRepository.getUserByEmail(email));
    }

    @Override
    public void sendVerificationCode(UserDTO userDTO) {
        String expirationDate = DateFormatUtils.format(addDays(new Date(), 1), DATE_FORMAT);
        String verificationCode = randomAlphabetic(6).toUpperCase();
        userRepository.sendVerificationCode(userDTO.getId(), verificationCode, expirationDate);
//        sendSMS(userDTO.getPhone(), "From : SecureCapita \nVerification code \n " + verificationCode);
    }


}
