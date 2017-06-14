package pl.mleczko_pawel.jakzjem_mapapl;

import com.orm.SugarApp;

/**
 * Created by mlecz on 14.05.2017.
 */

public class MyApplication extends SugarApp {
    private static boolean isLogged;

    public boolean isLogged() {
        return isLogged;
    }

    public void setLogged(boolean logged) {
        isLogged = logged;
    }
}
