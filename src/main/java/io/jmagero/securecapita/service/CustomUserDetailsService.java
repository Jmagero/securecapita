package io.jmagero.securecapita.service;

import io.jmagero.securecapita.domain.User;
import io.jmagero.securecapita.domain.UserPrincipal;
import io.jmagero.securecapita.repository.implementation.RoleRepositoryImpl;
import io.jmagero.securecapita.repository.implementation.UserRepositoryImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
@Component
@Slf4j
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {
    private final UserRepositoryImpl userRepository;
    private final RoleRepositoryImpl roleRepository;
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        try {
         User user = userRepository.getUserByEmail(email);
         log.info("User found in the database: {}", email);
         return new UserPrincipal(user, roleRepository.getRoleByUserId(user.getId()).getPermissions());
        } catch (UsernameNotFoundException exception){
            log.error("User by {} not found", email);
            throw  new UsernameNotFoundException("User by " + email + "not found");
        }
    }
}
