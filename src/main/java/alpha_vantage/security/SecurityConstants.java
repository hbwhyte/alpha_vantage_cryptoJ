package alpha_vantage.security;

public class SecurityConstants {
    public static final String SECRET = "SpiceCodesForLife";
    public static final long EXPIRATION_TIME = 604_800_000; // 7 days in milli
    public static final String TOKEN_PREFIX = "Bearer ";
    public static final String HEADER_STRING = "Authorization";
    public static final String REGISTRATION_URL = "/users/registration";
}
