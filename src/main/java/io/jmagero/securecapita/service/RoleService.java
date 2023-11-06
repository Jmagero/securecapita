package io.jmagero.securecapita.service;

import io.jmagero.securecapita.domain.Role;

public interface RoleService {
    Role getRoleByUserId(Long id);
}
