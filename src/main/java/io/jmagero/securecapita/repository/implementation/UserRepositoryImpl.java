package io.jmagero.securecapita.repository.implementation;

import io.jmagero.securecapita.domain.User;
import io.jmagero.securecapita.enumeration.VerificationType;
import io.jmagero.securecapita.exception.ApiException;
import io.jmagero.securecapita.repository.RoleRepository;
import io.jmagero.securecapita.repository.UserRepository;
import io.jmagero.securecapita.rowMapper.UserRowMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.Map;
import java.util.Objects;

import static io.jmagero.securecapita.enumeration.RoleType.ROLE_USER;
import static io.jmagero.securecapita.enumeration.VerificationType.PASSWORD;
import static io.jmagero.securecapita.query.UserQuery.*;
import static io.jmagero.securecapita.query.VerificationQuery.DELETE_VERIFICATION_CODE_BY_USER_ID;
import static io.jmagero.securecapita.query.VerificationQuery.INSERT_VERIFICATION_CODE_QUERY;
import static java.util.Map.*;


@Repository
@RequiredArgsConstructor
@Slf4j
public class UserRepositoryImpl implements UserRepository {
    private final NamedParameterJdbcTemplate jdbc;
    private final RoleRepository roleRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private static String DATE_FORMAT = "yyyy-MM-dd hh:mm:ss";

    @Override
    @Transactional
    public User create(User user, String verificationUrl) {
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
//            String verificationUrl = getVerificationUrl(UUID.randomUUID().toString(), ACCOUNT.getType());
    //        save url in verification table
            jdbc.update(INSERT_ACCOUNT_VERIFICATION_URL_QUERY, of("userId", user.getId(), "url", verificationUrl));
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

    @Override
    public User getUserByEmail(String email) {
        try{
            User user = jdbc.queryForObject(SELECT_USER_BY_EMAIL_QUERY, of("email", email), new UserRowMapper());
            return user;
        } catch (EmptyResultDataAccessException exception){
            throw new ApiException("No User found by email: " + email);
        } catch (Exception exception){
            log.error(exception.getMessage());
            throw new ApiException("An error occurred. Please try again");
        }
    }

    @Transactional
    @Override
    public void sendVerificationCode(Long userId, String verificationCode, String expirationDate) {
        try{
            jdbc.update(DELETE_VERIFICATION_CODE_BY_USER_ID, of("id",userId));
            jdbc.update(INSERT_VERIFICATION_CODE_QUERY, of("userId", userId, "code", verificationCode, "expirationDate", expirationDate));
            log.info("Verification Code: {}", verificationCode);
        }
        catch (Exception exception){
            log.error(exception.getMessage());
            throw new ApiException("An error occurred. Please try again");
        }
    }

    @Override
    public User verifyCode(String email, String code) {
        if(isVerificationCodeExpired(code)) throw new ApiException("This code has expired. Please login again.");
        try {
            User userByCode = jdbc.queryForObject(SELECT_USER_BY_USER_CODE_QUERY, of("code", code), new UserRowMapper());
            User userByEmail = jdbc.queryForObject(SELECT_USER_BY_EMAIL_QUERY, of("email", email), new UserRowMapper());
            if (userByCode.getEmail().equalsIgnoreCase(userByEmail.getEmail())){
                jdbc.update(DELETE_VERIFICATION_CODE_BY_USER_ID, of("id", userByEmail.getId()));
                log.info("User by code {}", userByCode);
                return userByCode;
            }else {
                throw new ApiException("Code is invalid. Please try again.");
            }
        } catch (EmptyResultDataAccessException exception){
            throw  new ApiException("Could not find record");
        } catch (Exception exception){
            throw  new ApiException("An error occurred. Please try again");
        }
    }

    @Override
    public void resetPassword(String email, String urlExpirationDate, String passwordResetUrl) {
        if(getEmailCount(email.trim().toLowerCase()) <= 0) throw new ApiException(" There is no account for this email address");
        try {
            User user = getUserByEmail(email);
            jdbc.update(DELETE_PASSWORD_VERIFICATION_BY_USER_ID_QUERY, of("userId", user.getId()));
            jdbc.update(INSERT_PASSWORD_VERIFICATION_QUERY, of("userId", user.getId(), "url", passwordResetUrl, "expirationDate",urlExpirationDate));
            // TODO send email with url to use
            log.info("verification URl: {}", passwordResetUrl);
        }
        catch (Exception exception){
            log.error("error: {} ", exception.getMessage());
            throw  new ApiException("An error occurred. Please try again");
        }
    }

    @Override
    public User verifyPasswordKey(String key, String passwordUrl) {
        if(isLinkExpired(key, passwordUrl)) throw new ApiException("The link is not valid. Please reset your password again.");
        try {
            User user =  jdbc.queryForObject(SELECT_USER_BY_PASSWORD_URL_QUERY, of("url", passwordUrl), new UserRowMapper());
            //jdbc.update("DELETE_USER_FROM_PASSWORD_VERIFICATION_QUERY", Map.of("id", user.getId())); //Depends on use case, business logic
            return user;
        } catch (EmptyResultDataAccessException exception){
            log.error(exception.getMessage());
            throw  new ApiException("The link is not valid. Please reset your password again.");
        } catch (Exception exception){
            log.error(exception.getMessage());
            throw  new ApiException("An error occurred. Please try again");
        }

    }

    @Override
    public void renewPassword(String password, String renewPasswordUrl) {
        try{
            jdbc.update(UPDATE_USER_PASSWORD_BY_URL_QUERY, of("password", passwordEncoder.encode(password), "url", renewPasswordUrl));
            jdbc.update(DELETE_VERIFICATION_BY_URL_QUERY, of("url", renewPasswordUrl));
        } catch (EmptyResultDataAccessException exception){
            log.error(exception.getMessage());
            throw  new ApiException("Passwords don't match. Please try again");
        } catch (Exception exception){
            log.error(exception.getMessage());
            throw  new ApiException("Passwords don't match. Please try again");
        }

    }

    private Boolean isLinkExpired(String key, String passwordUrl) {
        try {
            return jdbc.queryForObject(SELECT_EXPIRATION_BY_URL, of("url",passwordUrl), Boolean.class);
        } catch (EmptyResultDataAccessException exception){
            log.error(exception.getMessage());
            throw  new ApiException("The link is not valid. Please reset your password again.");
        } catch (Exception exception){
            log.error(exception.getMessage());
            throw  new ApiException("An error occurred. Please try again");
        }
    }

    private Boolean isVerificationCodeExpired(String code) {
        try {
            return jdbc.queryForObject(SELECT_IS_CODE_EXPIRED, of("code", code), Boolean.class);
        } catch (EmptyResultDataAccessException exception){
            throw  new ApiException("The code has expired, Please log in again");
        } catch (Exception exception){
            throw  new ApiException("An error occurred. Please try again");
        }
    }

    private Integer getEmailCount(String email) {
        return jdbc.queryForObject(COUNT_USER_EMAIL_QUERY, of("email",email),Integer.class);
    }

    private SqlParameterSource  getSQLParameterSource(User user) {
       return new MapSqlParameterSource()
               .addValue("firstName", user.getFirstName())
               .addValue("lastName", user.getLastName())
               .addValue("email", user.getEmail())
               .addValue("password", passwordEncoder.encode(user.getPassword()));
    }



}
