package pl.mleczko_pawel.jakzjem_mapapl.authenticator;

import pl.mleczko_pawel.jakzjem_mapapl.httpclient.ApiLogin;

/**
 * Created by mlecz on 13.05.2017.
 */

public class AccountGeneral {
    /**
     * Auth token types
     */
    public static final String AUTHTOKEN_TYPE_READ_ONLY = "Read only";
    public static final String AUTHTOKEN_TYPE_READ_ONLY_LABEL = "Read only access to an GZM account";

    public static final String AUTHTOKEN_TYPE_FULL_ACCESS = "Full access";
    public static final String AUTHTOKEN_TYPE_FULL_ACCESS_LABEL = "Full access to an GZM account";

    public static final ServerAuthenticate sServerAuthenticate = new ApiLogin();
}
