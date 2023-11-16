package io.jmagero.securecapita.query;

public class UserQuery {
    public static final   String INSERT_USER_QUERY ="INSERT INTO Users (first_name, last_name, email, password) VALUES(:firstName, :lastName, :email, :password)";
    public static  final String COUNT_USER_EMAIL_QUERY = "SELECT COUNT(*) FROM Users WHERE email = :email";
    public static final  String INSERT_ACCOUNT_VERIFICATION_URL_QUERY ="INSERT INTO AccountVerifications(user_id, url) VALUES (:userId, :url)";
    public static final String SELECT_USER_BY_EMAIL_QUERY = "SELECT * FROM Users  WHERE email = :email";
    public static final String SELECT_USER_BY_USER_CODE_QUERY = "SELECT * FROM Users  WHERE id = (SELECT user_id FROM TwoFactorVerifications WHERE code = :code)";
    public static final String SELECT_IS_CODE_EXPIRED = "SELECT expiration_date < NOW() AS isExpired FROM TwoFactorVerifications WHERE code = :code";
    public static final String DELETE_PASSWORD_VERIFICATION_BY_USER_ID_QUERY = "DELETE FROM ResetPasswordVerifications where  user_id = :userId";
    public static final String INSERT_PASSWORD_VERIFICATION_QUERY = "INSERT INTO  ResetPasswordVerifications (user_id, url, expiration_date) VALUES(:userId, :url, :expirationDate)";
    public static final String SELECT_EXPIRATION_BY_URL = "SELECT expiration_date < NOW() AS isExpired FROM ResetPasswordVerifications WHERE url = :url";
    public static final String SELECT_USER_BY_PASSWORD_URL_QUERY ="SELECT * FROM users WHERE id = (SELECT user_id FROM ResetPasswordVerifications WHERE url = :url)";
    public static final String DELETE_USER_FROM_PASSWORD_VERIFICATION_QUERY =" DELETE FROM ResetPasswordVerifications where  id = :userId";
    public static final String UPDATE_USER_PASSWORD_BY_URL_QUERY = "UPDATE Users set password = :password WHERE id = (SELECT user_id FROM ResetPasswordVerifications WHERE url = :url)";
    public static final String DELETE_VERIFICATION_BY_URL_QUERY = "DELETE FROM ResetPasswordVerifications where  url = :url";
}
