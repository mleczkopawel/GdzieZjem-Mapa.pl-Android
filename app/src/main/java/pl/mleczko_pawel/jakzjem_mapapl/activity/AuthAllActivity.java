package pl.mleczko_pawel.jakzjem_mapapl.activity;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;

import pl.mleczko_pawel.jakzjem_mapapl.MyApplication;
import pl.mleczko_pawel.jakzjem_mapapl.R;
import pl.mleczko_pawel.jakzjem_mapapl.authenticator.AccountGeneral;
import pl.mleczko_pawel.jakzjem_mapapl.classes.OwnAccountAuthenticatorActivity;

import static pl.mleczko_pawel.jakzjem_mapapl.authenticator.AccountGeneral.sServerAuthenticate;

public class AuthAllActivity extends OwnAccountAuthenticatorActivity implements GoogleApiClient.OnConnectionFailedListener {

    private static final int REQUEST_CODE = 9001;
    public final static String PARAM_USER_PASS = "USER_PASS";
    private GoogleApiClient googleApiClient;
    private String authTokenType;
    public static final String KEY_ERROR_MESSAGE = "ERR_MSG";
    public final static String ARG_IS_ADDING_NEW_ACCOUNT = "IS_ADDING_ACCOUNT";
    private AccountManager accountManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth_all);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        Button gzmButton = (Button) findViewById(R.id.gzm_button);
        final SignInButton signInButton = (SignInButton) findViewById(R.id.sign_in_button);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();

        googleApiClient = new GoogleApiClient.Builder(this).enableAutoManage(this, this).addApi(Auth.GOOGLE_SIGN_IN_API, gso).build();

        signInButton.setSize(SignInButton.SIZE_WIDE);

        gzmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), AuthEmailActivity.class);
                startActivity(intent);
                finish();
            }
        });

        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signIn();
            }
        });
        accountManager = AccountManager.get(getBaseContext());
    }

    private void signIn() {
        Intent intent = Auth.GoogleSignInApi.getSignInIntent(googleApiClient);
        startActivityForResult(intent, REQUEST_CODE);
    }

    /**
     * Dispatch incoming result to the correct fragment.
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE) {
            GoogleSignInResult googleSignInResult = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleResult(googleSignInResult);
        }
    }

    private void handleResult(GoogleSignInResult googleSignInResult) {
        if (googleSignInResult.isSuccess()) {
            GoogleSignInAccount googleSignInAccount = googleSignInResult.getSignInAccount();
            String name = googleSignInAccount != null ? googleSignInAccount.getDisplayName() : null;
            String email = googleSignInAccount != null ? googleSignInAccount.getEmail() : null;
            if (name != null || email != null) {
                logIn(name, email, "google");
            }
        } else {
        }
    }

    private void logIn(final String name, final String email, final String provider) {
        Toast.makeText(getApplicationContext(), name, Toast.LENGTH_LONG).show();
        Toast.makeText(getApplicationContext(), email, Toast.LENGTH_LONG).show();
        Toast.makeText(getApplicationContext(), provider, Toast.LENGTH_LONG).show();

        final String accountType = "pl.mleczko_pawel.jakzjem_mapapl";
        authTokenType = AccountGeneral.AUTHTOKEN_TYPE_FULL_ACCESS;

        new AsyncTask<String, Void, Intent>() {

            @Override
            protected Intent doInBackground(String... params) {
                Bundle data = new Bundle();
                String[] userData;
                try {
                    userData = sServerAuthenticate.token(getApplicationContext(), email, "", authTokenType, provider, name);
                    if (userData.length > 2) {
                        data.putString(AccountManager.KEY_ACCOUNT_NAME, userData[2]);
                        data.putString(AccountManager.KEY_ACCOUNT_TYPE, accountType);
                        data.putString(AccountManager.KEY_AUTHTOKEN, userData[0]);
                        data.putString(PARAM_USER_PASS, userData[0]);
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


    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}
//
//
//package pl.mleczko_pawel.jakzjem_mapapl.activity;
//
//        import android.accounts.Account;
//        import android.accounts.AccountManager;
//        import android.content.Intent;
//        import android.os.Bundle;
//        import android.support.annotation.NonNull;
//        import android.support.annotation.Nullable;
//        import android.support.v7.app.AlertDialog;
//        import android.support.v7.app.AppCompatActivity;
//        import android.view.View;
//        import android.widget.Button;
//        import android.widget.ImageView;
//        import android.widget.LinearLayout;
//        import android.widget.TextView;
//
//        import com.bumptech.glide.Glide;
//        import com.google.android.gms.auth.api.Auth;
//        import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
//        import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
//        import com.google.android.gms.auth.api.signin.GoogleSignInResult;
//        import com.google.android.gms.common.ConnectionResult;
//        import com.google.android.gms.common.SignInButton;
//        import com.google.android.gms.common.api.GoogleApiClient;
//        import com.google.android.gms.common.api.ResultCallback;
//        import com.google.android.gms.common.api.Status;
//
//        import pl.mleczko_pawel.jakzjem_mapapl.R;
//
//public class LoginActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {
//
//    private LinearLayout linearLayout;
//    private TextView userName;
//    private TextView userEmail;
//    private ImageView profilePicture;
//    private GoogleApiClient googleApiClient;
//    private static final int REQUEST_CODE = 9001;
//
//    @Override
//    protected void onCreate(@Nullable Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_login);
//
//        linearLayout = (LinearLayout) findViewById(R.id.profile);
//        Button signOutButton = (Button) findViewById(R.id.logout);
//        SignInButton signInButton = (SignInButton) findViewById(R.id.sign_in_button);
//        userName = (TextView) findViewById(R.id.user_name);
//        userEmail = (TextView) findViewById(R.id.user_email);
//        profilePicture = (ImageView) findViewById(R.id.profile_picture);
//
//        GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
//        googleApiClient = new GoogleApiClient.Builder(this).enableAutoManage(this, this).addApi(Auth.GOOGLE_SIGN_IN_API, googleSignInOptions).build();
//
//        linearLayout.setVisibility(View.GONE);
//
//        signInButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                signIn();
//            }
//        });
//        signOutButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                signOut();
//            }
//        });
//    }
//
//    @Override
//    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
//
//    }
//
//    private void signIn() {
//        Intent intent = Auth.GoogleSignInApi.getSignInIntent(googleApiClient);
//        startActivityForResult(intent, REQUEST_CODE);
//    }
//
//    private void signOut() {
//        Auth.GoogleSignInApi.signOut(googleApiClient).setResultCallback(new ResultCallback<Status>() {
//            @Override
//            public void onResult(@NonNull Status status) {
//                updateUi(false);
//            }
//        });
//    }
//
//    private void handleResult(GoogleSignInResult googleSignInResult) {
//        if (googleSignInResult.isSuccess()) {
//            GoogleSignInAccount googleSignInAccount = googleSignInResult.getSignInAccount();
//            String name;
//            if (googleSignInAccount != null) {
//                name = googleSignInAccount.getDisplayName();
//                userName.setText(name);
//            }
//            String email = googleSignInAccount != null ? googleSignInAccount.getEmail() : null;
//            if (email != null) {
//                userEmail.setText(email);
//            }
//            if ((googleSignInAccount != null ? googleSignInAccount.getPhotoUrl() : null) != null) {
//                String img_url = googleSignInAccount.getPhotoUrl().toString();
//                Glide.with(this).load(img_url).into(profilePicture);
//            }
//
//            updateUi(true);
//        } else {
//            updateUi(false);
//        }
//    }
//
//    private void updateUi(boolean isLoggedIn) {
//        if (isLoggedIn) {
//            linearLayout.setVisibility(View.VISIBLE);
//        } else {
//            linearLayout.setVisibility(View.INVISIBLE);
//        }
//    }
//
//    /**
//     * Dispatch incoming result to the correct fragment.
//     *
//     * @param requestCode
//     * @param resultCode
//     * @param data
//     */
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//
//        if (requestCode == REQUEST_CODE) {
//            GoogleSignInResult googleSignInResult = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
//            handleResult(googleSignInResult);
//        }
//    }
//}
//

