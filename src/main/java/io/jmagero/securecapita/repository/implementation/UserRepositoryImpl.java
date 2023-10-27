package io.jmagero.securecapita.repository.implementation;

import io.jmagero.securecapita.domain.User;
import io.jmagero.securecapita.exception.ApiException;
import io.jmagero.securecapita.repository.RoleRepository;
import io.jmagero.securecapita.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

import static io.jmagero.securecapita.enumeration.RoleType.ROLE_USER;
import static io.jmagero.securecapita.enumeration.VerificationType.ACCOUNT;
import static io.jmagero.securecapita.query.UserQuery.*;
import static java.util.Objects.requireNonNull;

@Repository
@RequiredArgsConstructor
@Slf4j
public class UserRepositoryImpl implements UserRepository {
    private final NamedParameterJdbcTemplate jdbc;
    private final RoleRepository roleRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public User create(User user) {
        //        check the email is unique
        if(getEmailCount(user.getEmail().trim().toLowerCase()) > 0){
            throw new ApiException("Email already exists");
        }
        try {
            // Save new user
            log.info("Creating the user");
            KeyHolder holder = new GeneratedKeyHolder();
            SqlParameterSource  parameters = getSQLParameterSource(user);
            jdbc.update(INSERT_USER_QUERY, parameters,holder);
            user.setId(Objects.requireNonNull(holder.getKey().longValue()));
            //        Add role to the user
            roleRepository.addRoleToUser(user.getId(), ROLE_USER.name());
    //        Send verification url
            String verificationUrl = getVerificationUrl(UUID.randomUUID().toString(), ACCOUNT.getType());
    //        save url in verification table
            jdbc.update(INSERT_ACCOUNT_VERIFICATION_URL_QUERY,Map.of("userId", user.getId(), "url", verificationUrl));
    //        Send email to user with verification URL
//            emailService.sendVerificationUrl(user.getFirstName(), user.getEmail(), verificationUrl, ACCOUNT);
            user.setEnabled(false);
            user.setNotLocked(true);
            // return the newly created user
            return user;
    //        if any errors, throw exception with proper message
        }catch (Exception exception){
            log.error(exception.getMessage());
            throw new ApiException("An error occurred. Please try again");
        }
    }




    @Override
    public Collection list(int page, int pageSize) {
        return null;
    }

    @Override
    public User get(Long id) {
        return null;
    }

    @Override
    public User update(User data) {
        return null;
    }

    @Override
    public Boolean delete(Long id) {
        return null;
    }

    private Integer getEmailCount(String email) {
        return jdbc.queryForObject(COUNT_USER_EMAIL_QUERY, Map.of("email",email),Integer.class);
    }

    private SqlParameterSource  getSQLParameterSource(User user) {
       return new MapSqlParameterSource()
               .addValue("firstName", user.getFirstName())
               .addValue("lastName", user.getLastName())
               .addValue("email", user.getEmail())
               .addValue("password", passwordEncoder.encode(user.getPassword()));
    }

    private String getVerificationUrl(String key, String type) {
        return ServletUriComponentsBuilder.fromCurrentContextPath().path("/users/verify" +type +"/" + key).toString();
    }
}
