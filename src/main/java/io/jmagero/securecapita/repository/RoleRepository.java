package io.jmagero.securecapita.repository;

import io.jmagero.securecapita.domain.Role;
import org.springframework.stereotype.Repository;

import java.util.Collection;

@Repository
public interface RoleRepository<T extends Role> {
    //    Basic operation
    T create(T role);
    Collection<T> list(int page, int pageSize);
    T get(Long id);
    T update(T role);
    Boolean delete(Long id);

    void addRoleToUser(Long userId, String roleName);
    Role getRoleByUserId(Long userId);
    Role getRoleByUserEmail(String email);
    void updateUserRole(Long userId, String roleName);
}
