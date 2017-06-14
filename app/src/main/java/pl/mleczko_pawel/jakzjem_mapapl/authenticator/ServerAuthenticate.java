package pl.mleczko_pawel.jakzjem_mapapl.authenticator;

import android.content.Context;

import java.io.UnsupportedEncodingException;

/**
 * Created by mlecz on 13.05.2017.
 */

public interface ServerAuthenticate {
    public String[] token(Context context, String email, String password, String authType, String provider, String name) throws UnsupportedEncodingException;
}
