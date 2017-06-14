package pl.mleczko_pawel.jakzjem_mapapl.httpclient;

import android.content.Context;
import android.widget.Toast;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;
import org.json.JSONArray;

/**
 * Created by mlecz on 29.04.2017.
 */

public class ApiSearchStats {
    private Context context;
    private String url = "searchStats";

    public ApiSearchStats(Context context) {
        this.context = context;
    }

    public void sendSearchStats(String searchName) {
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
