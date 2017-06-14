package pl.mleczko_pawel.jakzjem_mapapl.httpclient;

import android.content.Context;
import android.widget.Toast;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;
import org.json.JSONArray;

/**
 * Created by mlecz on 13.05.2017.
 */

public class ApiUser {
    private String url = "user";


    public String tokenTruncate(Context context, String name, String password, String authType) {
        final String[] authToken = {null};
        RequestParams requestParams = new RequestParams();
        requestParams.put("name", name);
        ApiClient.post(context, url, requestParams, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                authToken[0] = "1";
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                authToken[0] = "0";
//                displayMessage(context, "Błąd");
            }
        });

        return authToken[0];
    }

    public void register(Context context, String searchName) {
        RequestParams requestParams = new RequestParams();
        requestParams.put("name", searchName);
        ApiClient.post(context, url, requestParams, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
//                displayMessage(context, "Błąd");
            }
        });
    }

    private void displayMessage(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }
}
