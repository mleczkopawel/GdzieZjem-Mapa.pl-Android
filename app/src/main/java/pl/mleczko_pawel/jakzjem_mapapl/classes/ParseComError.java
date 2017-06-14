package pl.mleczko_pawel.jakzjem_mapapl.classes;

import java.io.Serializable;

/**
 * Created by mlecz on 13.05.2017.
 */

public class ParseComError implements Serializable {
    private int code;
    private String error;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }
}
