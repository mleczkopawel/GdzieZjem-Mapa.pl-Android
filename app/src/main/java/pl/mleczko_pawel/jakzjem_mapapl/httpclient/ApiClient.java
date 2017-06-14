package pl.mleczko_pawel.jakzjem_mapapl.httpclient;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import pl.mleczko_pawel.jakzjem_mapapl.authenticator.ServerAuthenticate;
import pl.mleczko_pawel.jakzjem_mapapl.classes.ParseComError;

/**
 * Created by mlecz on 23.04.2017.
 */

public class ApiClient {
    private static AsyncHttpClient asyncHttpClient = new AsyncHttpClient();

    private static String getAbsoluteUrl(Context context, String relativeUrl) {
        String baseUrl = "http://gzm.mleczko-pawel.pl/api/";
        return baseUrl + relativeUrl;
    }

    public static void get(Context context, String url, RequestParams requestParams, AsyncHttpResponseHandler asyncHttpResponseHandler) {
        asyncHttpClient.addHeader("token", "a61b5fb80f18f85c58ff9d24644f1f64");
        asyncHttpClient.addHeader("apptype", "mobile");
        asyncHttpClient.get(getAbsoluteUrl(context, url), requestParams, asyncHttpResponseHandler);
    }

    public static void post(Context context, String url, RequestParams requestParams, AsyncHttpResponseHandler asyncHttpResponseHandler) {
        String absoluteUrl = getAbsoluteUrl(context, url);
        asyncHttpClient.addHeader("token", "a61b5fb80f18f85c58ff9d24644f1f64");
        asyncHttpClient.addHeader("apptype", "mobile");
        asyncHttpClient.post(absoluteUrl, requestParams, asyncHttpResponseHandler);
    }
}

