package io.jmagero.securecapita.query;

public class RoleQuery {
    public static final String INSERT_ROLE_TO_USER_QUERY = "INSERT INTO UserRoles (user_id, role_id) VALUES (:userId, :roleId)";
    public static final String SELECT_ROLE_BY_NAME_QUERY = "SELECT * FROM Roles  WHERE name = :name";
    public static final String SELECT_ROLE_BY_USERID = "SELECT * FROM Roles r WHERE r.id = (SELECT role_id FROM UserRoles ur WHERE user_id = :userId)";
//    alternative query
    public static final String SELECT_ROLE_BY_USER_ID = "SELECT * FROM Roles r JOIN UserRoles ur ON ur.role_id = r.id JOIN Users u ON u.id = ur.user_id WHERE u.id = :userId";
}
