package pl.mleczko_pawel.jakzjem_mapapl.activity;

import android.Manifest;
import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;

import java.util.LinkedList;
import java.util.List;

import pl.mleczko_pawel.jakzjem_mapapl.MyApplication;
import pl.mleczko_pawel.jakzjem_mapapl.R;
import pl.mleczko_pawel.jakzjem_mapapl.httpclient.ApiLocalization;

/**
 * Created by Paweł Mleczko on 24.04.2017.
 */

public class Splash extends AppCompatActivity {

    private static boolean onRun = false;
    private LocationManager locationManager;
    private static final int INITIAL_REQUEST = 1337;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_activity);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.CALL_PHONE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, INITIAL_REQUEST);
        }

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            buildAlertMessageNoGps();
        } else if (!isInternetConnected(this)) {
            buildAlertMessageNoNetwork();
        } else {
            checkLogged();
            if (!onRun) {
                ApiLocalization apiLocalization = new ApiLocalization(Splash.this);
                apiLocalization.getAll();
            } else {
                startActivity(new Intent(getApplicationContext(), SearchActivity.class));
            }
            onRun = true;
        }
    }

    public void checkLogged() {
        AccountManager manager = AccountManager.get(this);
        Account[] accounts = manager.getAccountsByType("pl.mleczko_pawel.jakzjem_mapapl");
        List<String> possibleEmails = new LinkedList<>();

        for (Account account : accounts) {
            possibleEmails.add(account.name);
        }

        final MyApplication myApplication = (MyApplication) getApplicationContext();
        if (!possibleEmails.isEmpty() && possibleEmails.get(0) != null) {
            myApplication.setLogged(true);
        } else {
            myApplication.setLogged(false);
        }
    }

    public static boolean isInternetConnected(Context ctx) {
        ConnectivityManager connectivityMgr = (ConnectivityManager) ctx
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo wifi = connectivityMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        NetworkInfo mobile = connectivityMgr.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        if (wifi != null) {
            if (wifi.isConnected()) {
                return true;
            }
        }
        if (mobile != null) {
            if (mobile.isConnected()) {
                return true;
            }
        }
        return false;
    }

    private void buildAlertMessageNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Masz wyłączony GPS, chcesz go teraz włączyć?")
                .setCancelable(false)
                .setPositiveButton("Tak", new DialogInterface.OnClickListener() {
                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                })
                .setNegativeButton("Nie", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        dialog.cancel();
                        finish();
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }

    private void buildAlertMessageNoNetwork() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Połączenie z internetem nie jest aktywne, aktywować teraz?")
                .setCancelable(false)
                .setPositiveButton("WiFi", new DialogInterface.OnClickListener() {
                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
                    }
                })
                .setNeutralButton("Nie", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        dialog.cancel();
                        finish();
                    }
                })
                .setNegativeButton("Dane pakietowe", new DialogInterface.OnClickListener() {
                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        startActivity(new Intent(android.provider.Settings.ACTION_WIRELESS_SETTINGS));
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }
}
