package pl.mleczko_pawel.jakzjem_mapapl.httpclient;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;

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
 * Created by mlecz on 15.05.2017.
 */

public class ApiLogin implements ServerAuthenticate {

    private static String getAbsoluteUrl(Context context, String relativeUrl) {
        String baseUrl = "http://gzm.mleczko-pawel.pl/api/";
        return baseUrl + relativeUrl;
    }

    @Override
    public String[] token(Context context, String email, String pass, String authType, String provider, String name) throws UnsupportedEncodingException {
        Log.d("paml", "startTruncate");
        String url = getAbsoluteUrl(context, "users");
        DefaultHttpClient httpClient = new DefaultHttpClient();
        HttpPost httpPost = new HttpPost(url);

        httpPost.addHeader("token", "a61b5fb80f18f85c58ff9d24644f1f64");
        httpPost.addHeader("apptype", "mobile");
        httpPost.addHeader("action", "tokenTruncate");
        httpPost.addHeader("Content-Type", "application/json");

        httpPost.setHeader("email", email);
        if (!pass.equals("")) {
            httpPost.setHeader("password", pass);
        } else {
            httpPost.setHeader("provider", provider);
            httpPost.setHeader("name", name);
        }

        String[] userData = null;
        try {
            HttpResponse response = httpClient.execute(httpPost);
            String responseString = EntityUtils.toString(response.getEntity());
            JSONObject jsonObject = new JSONObject(responseString);

            if (response.getStatusLine().getStatusCode() != 200) {
                ParseComError parseComError = new Gson().fromJson(responseString, ParseComError.class);
                Toast.makeText(context, "Error", Toast.LENGTH_LONG).show();
            }

            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(jsonObject.toString());
            if (jsonNode.path("code").asInt() == 1) {
                JsonNode userNode = jsonNode.path("user");
                userData = new String[3];
                userData[0] = userNode.path("token").textValue();
                userData[1] = userNode.path("email").textValue();
                userData[2] = userNode.path("name").textValue();
            } else {
                userData = new String[1];
                userData[0] = "Użytkownik nie istnieje";
            }
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }

        return userData;
    }
}
