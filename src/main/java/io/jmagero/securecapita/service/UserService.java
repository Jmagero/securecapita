package io.jmagero.securecapita.service;

import io.jmagero.securecapita.domain.User;
import io.jmagero.securecapita.dto.UserDTO;

public interface UserService {

    UserDTO createUser(User user);

}
