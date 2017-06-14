package pl.mleczko_pawel.jakzjem_mapapl.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import pl.mleczko_pawel.jakzjem_mapapl.authenticator.Authenticator;

/**
 * Created by mlecz on 13.05.2017.
 */

public class AuthenticatorService extends Service {
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Authenticator authenticator = new Authenticator(this);
        return authenticator.getIBinder();
    }
}
