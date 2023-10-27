package io.jmagero.securecapita.service;

import io.jmagero.securecapita.domain.User;
import io.jmagero.securecapita.dto.UserDTO;
import io.jmagero.securecapita.dtomapper.UserDTOMapper;
import io.jmagero.securecapita.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService{
    private final UserRepository<User> userRepository;
    @Override
    public UserDTO createUser(User user) {
        User user1 = userRepository.create(user);
        UserDTO userDTO = UserDTOMapper.fromUser(user1);
        return  userDTO;
    }
}
