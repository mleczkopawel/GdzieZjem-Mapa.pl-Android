package pl.mleczko_pawel.jakzjem_mapapl.httpclient;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;
import android.widget.Toast;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import pl.mleczko_pawel.jakzjem_mapapl.activity.PointsActivity;
import pl.mleczko_pawel.jakzjem_mapapl.model.Categories;
import pl.mleczko_pawel.jakzjem_mapapl.model.CategoriesToPoints;
import pl.mleczko_pawel.jakzjem_mapapl.model.Localization;

/**
 * Created by mlecz on 23.04.2017.
 */

public class ApiLocalization {
    private Context context;
    private String url = "point";

    public ApiLocalization(Context context) {
        this.context = context;
    }

    public void getAll() {
        RequestParams requestParams = new RequestParams();
        ApiClient.get(this.context, url, requestParams, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject jsonObject) {
                try {
                    ObjectMapper objectMapper = new ObjectMapper();
                    JsonNode jsonNode = objectMapper.readTree(jsonObject.toString());
                    JsonNode localizationNode = jsonNode.path("points");

                    Iterator<JsonNode> localIterator = localizationNode.elements();
                    while (localIterator.hasNext()) {
                        JsonNode localNode = localIterator.next();
                        String name = localNode.path("name").textValue();
                        Double lat = localNode.path("lat").asDouble();
                        Double lon = localNode.path("lon").asDouble();
                        String address = localNode.path("address").textValue();
                        String url = localNode.path("url").textValue();
                        String phone = localNode.path("telephone").textValue();
                        Integer remoteId = Integer.parseInt(localNode.path("id").toString());

                        JsonNode categoriesNode = localNode.path("categories");
                        Iterator<JsonNode> categoriesIterator = categoriesNode.elements();
                        while (categoriesIterator.hasNext()) {
                            JsonNode categoryNode = categoriesIterator.next();
                            Integer remoteCategoryId = Integer.parseInt(categoryNode.path("id").toString());
                            String categoryName = categoryNode.path("name").textValue();
                            String plName = categoryNode.path("plName").textValue();
                            Log.d("catName", plName);

                            List<Categories> categoriesList = Categories.find(Categories.class, "remote_id = " + remoteCategoryId.toString());
                            Categories category;
                            CategoriesToPoints categoriesToPoints;
                            if (!categoriesList.isEmpty()) {
                                category = categoriesList.get(0);
                            } else {
                                category = new Categories();
                                category.setName(categoryName);
                                category.setPlName(plName);
                                category.setRemoteId(remoteCategoryId);
                                category.save();
                            }

                            List<CategoriesToPoints> categoriesToPointsList = CategoriesToPoints.find(CategoriesToPoints.class, "category_id = ? AND point_id = ?", remoteCategoryId.toString(), remoteId.toString());
                            if (categoriesToPointsList.isEmpty()) {
                                categoriesToPoints = new CategoriesToPoints();
                                categoriesToPoints.setCategoryId(remoteCategoryId);
                                categoriesToPoints.setPointId(remoteId);
                                categoriesToPoints.save();
                            }
                        }

                        List<Localization> localizationList = Localization.find(Localization.class, "remote_id = " + remoteId.toString());

                        Localization localization;
                        if (!localizationList.isEmpty()) {
                            localization = localizationList.get(0);
                        } else {
                            localization = new Localization();
                            localization.setRemoteId(remoteId);
                        }

                        localization.setName(name);
                        localization.setLat(lat);
                        localization.setLon(lon);
                        localization.setAddress(address);
                        localization.setPhone(phone);
                        localization.setUrl(url);
                        localization.setLiked(false);
                        localization.save();
                    }
                    context.startActivity(new Intent(context, PointsActivity.class));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable e, JSONObject response) {
                displayErrorMessage(context);
                displayMessage(context, "Błąd!!!");
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
//                displayErrorMessage(context);
                displayMessage(context, "Błąd!!!");
                displayMessage(context, "" + statusCode);
                displayMessage(context, responseString);
            }
        });
    }

    public void get(Integer id) {
        Log.d("Start", "get");
        RequestParams requestParams = new RequestParams();
        ApiClient.get(this.context, url + "/" + id, requestParams, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                displayMessage(context, "Błąd");
            }
        });
    }

    private void displayErrorMessage(Context context) {
        Toast.makeText(context, "Błąd połączenia z Internetem.", Toast.LENGTH_SHORT).show();
    }

    private void displayMessage(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show();
    }
}