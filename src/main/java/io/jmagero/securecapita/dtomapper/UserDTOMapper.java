package io.jmagero.securecapita.dtomapper;

import io.jmagero.securecapita.domain.Role;
import io.jmagero.securecapita.domain.User;
import io.jmagero.securecapita.dto.UserDTO;
import org.springframework.beans.BeanUtils;


public class UserDTOMapper {
    public  static UserDTO fromUser(User user){
        UserDTO userDTO = new UserDTO();
        BeanUtils.copyProperties(user,userDTO);
        return userDTO;
    };
    public  static UserDTO fromUser(User user, Role role){
        UserDTO userDTO = new UserDTO();
        BeanUtils.copyProperties(user,userDTO);
        userDTO.setRole(role.getName());
        userDTO.setPermissions(role.getPermissions());
        return userDTO;
    };

    public static User toUser(UserDTO userDTO){
        User user = new User();
        BeanUtils.copyProperties(userDTO, user);
        return user;
    }
}
