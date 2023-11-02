package io.jmagero.securecapita.repository.implementation;

import io.jmagero.securecapita.domain.Role;
import io.jmagero.securecapita.exception.ApiException;
import io.jmagero.securecapita.repository.RoleRepository;
import io.jmagero.securecapita.rowMapper.RoleRowMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.Map;

import static io.jmagero.securecapita.enumeration.RoleType.ROLE_USER;
import static io.jmagero.securecapita.query.RoleQuery.*;

@Slf4j
@RequiredArgsConstructor
@Repository
public class RoleRepositoryImpl implements RoleRepository {
    private final NamedParameterJdbcTemplate jdbc;
    @Override
    public Role create(Role role) {
        return null;
    }

    @Override
    public Collection list(int page, int pageSize) {
        return null;
    }

    @Override
    public Role get(Long id) {
        return null;
    }

    @Override
    public Role update(Role role) {
        return null;
    }

    @Override
    public Boolean delete(Long id) {
        return null;
    }

    @Override
    public void addRoleToUser(Long userId, String roleName) {
        log.info("Adding a role {} to user id  {}",roleName, userId);
        try {
        Role role = jdbc.queryForObject(SELECT_ROLE_BY_NAME_QUERY, Map.of("name",roleName),
                new RoleRowMapper());
        jdbc.update(INSERT_ROLE_TO_USER_QUERY, Map.of("userId",userId, "roleId", role.getId()));

        } catch (EmptyResultDataAccessException exception){
            throw new ApiException("No role found by name: " + ROLE_USER.name());

        }catch (Exception exception){
            log.error(exception.getMessage());
            throw new ApiException("An error occurred. Please try again");
        }

    }

    @Override
    public Role getRoleByUserId(Long userId) {
        try {
            Role role = jdbc.queryForObject(SELECT_ROLE_BY_USERID, Map.of("userId", userId), new RoleRowMapper());
            return role;
        } catch (EmptyResultDataAccessException exception){
            throw new ApiException("No role found by name: " + ROLE_USER.name());
        } catch (Exception exception){
            log.error(exception.getMessage());
            throw new ApiException("An error occurred. Please try again");
        }
    }
    @Override
    public Role getRoleByUserEmail(String email) {
        return null;
    }
    @Override
    public void updateUserRole(Long userId, String roleName) {

    }
}
