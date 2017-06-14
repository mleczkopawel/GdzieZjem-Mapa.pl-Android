package pl.mleczko_pawel.jakzjem_mapapl.activity;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import pl.mleczko_pawel.jakzjem_mapapl.MyApplication;
import pl.mleczko_pawel.jakzjem_mapapl.R;
import pl.mleczko_pawel.jakzjem_mapapl.authenticator.AccountGeneral;
import pl.mleczko_pawel.jakzjem_mapapl.classes.OwnAccountAuthenticatorActivity;

import static pl.mleczko_pawel.jakzjem_mapapl.authenticator.AccountGeneral.*;

public class AuthEmailActivity extends OwnAccountAuthenticatorActivity {

    public final static String ARG_ACCOUNT_TYPE = "ACCOUNT_TYPE";
    public final static String ARG_AUTH_TYPE = "AUTH_TYPE";
    public final static String ARG_ACCOUNT_NAME = "ACCOUNT_NAME";
    public final static String ARG_IS_ADDING_NEW_ACCOUNT = "IS_ADDING_ACCOUNT";

    public static final String KEY_ERROR_MESSAGE = "ERR_MSG";

    public final static String PARAM_USER_PASS = "USER_PASS";

    private AccountManager accountManager;
    private String authTokenType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth_email);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        accountManager = AccountManager.get(getBaseContext());
        String accountName = getIntent().getStringExtra(ARG_ACCOUNT_NAME);
        authTokenType = getIntent().getStringExtra(ARG_AUTH_TYPE);

        if (authTokenType == null) {
            authTokenType = AccountGeneral.AUTHTOKEN_TYPE_FULL_ACCESS;
        }

        if (accountName != null) {
            ((EditText)findViewById(R.id.email)).setText(accountName);
        }

        findViewById(R.id.email_sign_in_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                submit();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        int REQ_SIGNUP = 1;
        if (requestCode == REQ_SIGNUP && resultCode == RESULT_OK) {
            finishLogin(data);
        } else
            super.onActivityResult(requestCode, resultCode, data);
    }

    public void submit() {
        final String userName = ((EditText) findViewById(R.id.email)).getText().toString();
        final String userPass = ((EditText) findViewById(R.id.password)).getText().toString();

        final String accountType = "pl.mleczko_pawel.jakzjem_mapapl";

        new AsyncTask<String, Void, Intent>() {

            @Override
            protected Intent doInBackground(String... params) {
                Bundle data = new Bundle();
                String[] userData;
                try {
                    userData = sServerAuthenticate.token(getApplicationContext(), userName, userPass, authTokenType, "", "");
                    if (userData.length > 2) {
                        data.putString(AccountManager.KEY_ACCOUNT_NAME, userData[2]);
                        data.putString(AccountManager.KEY_ACCOUNT_TYPE, accountType);
                        data.putString(AccountManager.KEY_AUTHTOKEN, userData[0]);
                        data.putString(PARAM_USER_PASS, userPass);
                    } else {
                        data.putString("NONE", userData[0]);
                    }
                } catch (Exception e) {
                    Log.e("paml", e.getMessage() + " " + e.getLocalizedMessage());
                    data.putString(KEY_ERROR_MESSAGE, e.getMessage());
                }

                final Intent res = new Intent();
                res.putExtras(data);
                return res;
            }

            @Override
            protected void onPostExecute(Intent intent) {
                if (intent.hasExtra(KEY_ERROR_MESSAGE)) {
                    Toast.makeText(getBaseContext(), intent.getStringExtra(KEY_ERROR_MESSAGE), Toast.LENGTH_SHORT).show();
                } else {
                    finishLogin(intent);
                }
            }
        }.execute();
    }

    private void finishLogin(Intent intent) {
        if (intent.hasExtra(AccountManager.KEY_ACCOUNT_NAME)) {
            String accountName = intent.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
            String accountPassword = intent.getStringExtra(PARAM_USER_PASS);
            final Account account = new Account(accountName, intent.getStringExtra(AccountManager.KEY_ACCOUNT_TYPE));

            if (getIntent().getBooleanExtra(ARG_IS_ADDING_NEW_ACCOUNT, true)) {
                String authtoken = intent.getStringExtra(AccountManager.KEY_AUTHTOKEN);
                String authtokenType = authTokenType;

                accountManager.addAccountExplicitly(account, accountPassword, null);
                accountManager.setAuthToken(account, authtokenType, authtoken);
            } else {
                accountManager.setPassword(account, accountPassword);
            }

            MyApplication myApplication = (MyApplication) getApplicationContext();
            myApplication.setLogged(true);
            setAccountAuthenticatorResult(intent.getExtras());
            setResult(RESULT_OK, intent);
            finish();
        } else {
            String message = intent.getStringExtra("NONE");
            Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
        }
    }
}
