package pl.mleczko_pawel.jakzjem_mapapl.authenticator;

import android.accounts.*;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import java.io.UnsupportedEncodingException;

import pl.mleczko_pawel.jakzjem_mapapl.activity.AuthAllActivity;
import pl.mleczko_pawel.jakzjem_mapapl.activity.AuthEmailActivity;

import static android.accounts.AccountManager.KEY_BOOLEAN_RESULT;
import static pl.mleczko_pawel.jakzjem_mapapl.authenticator.AccountGeneral.*;

/**
 * Created by mlecz on 13.05.2017.
 */

//TODO: MAKE SECOND AUTHENTICATOR FOR G+ AND FB

public class Authenticator extends AbstractAccountAuthenticator {

    private Context context;

    public Authenticator(Context context) {
        super(context);
        this.context = context;
    }

    @Override
    public Bundle editProperties(AccountAuthenticatorResponse accountAuthenticatorResponse, String s) {
        return null;
    }

    @Override
    public Bundle addAccount(AccountAuthenticatorResponse accountAuthenticatorResponse, String accountType, String authTokenType, String[] requiredFeatures, Bundle options) throws NetworkErrorException {
        Intent intent = new Intent(context, AuthAllActivity.class);
        intent.putExtra(AuthEmailActivity.ARG_ACCOUNT_TYPE, accountType);
        intent.putExtra(AuthEmailActivity.ARG_AUTH_TYPE, authTokenType);
        intent.putExtra(AuthEmailActivity.ARG_IS_ADDING_NEW_ACCOUNT, true);
        intent.putExtra(AccountManager.KEY_ACCOUNT_AUTHENTICATOR_RESPONSE, accountAuthenticatorResponse);

        Bundle bundle = new Bundle();
        bundle.putParcelable(AccountManager.KEY_INTENT, intent);
        return bundle;
    }

    @Override
    public Bundle confirmCredentials(AccountAuthenticatorResponse accountAuthenticatorResponse, Account account, Bundle bundle) throws NetworkErrorException {
        return null;
    }

    @Override
    public Bundle getAuthToken(AccountAuthenticatorResponse accountAuthenticatorResponse, Account account, String authTokenType, Bundle options) throws NetworkErrorException {
        AccountManager accountManager = AccountManager.get(context);
        String[] userData = new String[3];
        userData[0] = accountManager.peekAuthToken(account, authTokenType);

        if (userData.length < 1) {
            String password = accountManager.getPassword(account);
            if (password != null) {
                try {
                    userData = sServerAuthenticate.token(context, account.name, password, authTokenType, "", "");
                } catch (UnsupportedEncodingException e) {
                    Log.d("paml", "error3");
                    e.printStackTrace();
                }
            }
        }

        if (userData.length > 1) {
            final Bundle result = new Bundle();
            result.putString(AccountManager.KEY_ACCOUNT_NAME, userData[2]);
            result.putString(AccountManager.KEY_ACCOUNT_TYPE, account.type);
            result.putString(AccountManager.KEY_AUTHTOKEN, userData[0]);
            return result;
        }

        final Intent intent = new Intent(context, AuthEmailActivity.class);
        intent.putExtra(AccountManager.KEY_ACCOUNT_AUTHENTICATOR_RESPONSE, accountAuthenticatorResponse);
        intent.putExtra(AuthEmailActivity.ARG_ACCOUNT_TYPE, account.type);
        intent.putExtra(AuthEmailActivity.ARG_AUTH_TYPE, authTokenType);
        intent.putExtra(AuthEmailActivity.ARG_ACCOUNT_NAME, account.name);
        final Bundle bundle = new Bundle();
        bundle.putParcelable(AccountManager.KEY_INTENT, intent);
        return bundle;
    }

    @Override
    public String getAuthTokenLabel(String authTokenType) {
        if (AUTHTOKEN_TYPE_FULL_ACCESS.equals(authTokenType))
            return AUTHTOKEN_TYPE_FULL_ACCESS_LABEL;
        else if (AUTHTOKEN_TYPE_READ_ONLY.equals(authTokenType))
            return AUTHTOKEN_TYPE_READ_ONLY_LABEL;
        else
            return authTokenType + " (Label)";
    }

    @Override
    public Bundle updateCredentials(AccountAuthenticatorResponse accountAuthenticatorResponse, Account account, String s, Bundle bundle) throws NetworkErrorException {
        return null;
    }

    @Override
    public Bundle hasFeatures(AccountAuthenticatorResponse accountAuthenticatorResponse, Account account, String[] strings) throws NetworkErrorException {
        final Bundle result = new Bundle();
        result.putBoolean(KEY_BOOLEAN_RESULT, false);
        return result;
    }
}
