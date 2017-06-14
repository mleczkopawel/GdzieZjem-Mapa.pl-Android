package pl.mleczko_pawel.jakzjem_mapapl.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import pl.mleczko_pawel.jakzjem_mapapl.R;
import pl.mleczko_pawel.jakzjem_mapapl.adapters.CustomInfoWindowAdapter;
import pl.mleczko_pawel.jakzjem_mapapl.model.Categories;
import pl.mleczko_pawel.jakzjem_mapapl.model.CategoriesToPoints;
import pl.mleczko_pawel.jakzjem_mapapl.model.Localization;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback {

    private java.util.List<Localization> localizationList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        MobileAds.initialize(getApplicationContext(), "ca-app-pub-4571255084527146~5427470719");
        AdView mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        String searchText = getIntent().getExtras().getString("searchText");
        if (getIntent().hasExtra("category")) {
            String category = getIntent().getExtras().getString("category");
            java.util.List<Categories> categoriesList = Categories.find(Categories.class, "pl_name = ?", category);
            if (!categoriesList.isEmpty()) {
                java.util.List<CategoriesToPoints> categoriesToPointsList = CategoriesToPoints.find(CategoriesToPoints.class, "category_id = ?", String.valueOf(categoriesList.get(0).getRemoteId()));
                if (!categoriesToPointsList.isEmpty()) {
                    this.localizationList = Localization.listAll(Localization.class);
                    this.localizationList.clear();
                    for (int i = 0; i < categoriesToPointsList.size(); i++) {
                        String[] conditions = new String[2];
                        conditions[0] = String.valueOf(categoriesToPointsList.get(i).getPointId());
                        conditions[1] = "%" + searchText + "%";
                        java.util.List<Localization> localizationList = Localization.find(Localization.class, "remote_id = ? and name LIKE ?", conditions);
                        if (!localizationList.isEmpty()) {
                            this.localizationList.add(localizationList.get(0));
                        }
                    }
                    if (this.localizationList != null) {
                        if (this.localizationList.isEmpty()) {
                            Intent intent = new Intent(getApplicationContext(), SearchActivity.class);
                            intent.putExtra("toastEmpty", "Brak wyników");
                            startActivity(intent);
                        }
                    } else {
                        Intent intent = new Intent(getApplicationContext(), SearchActivity.class);
                        intent.putExtra("toastEmpty", "Brak wyników");
                        startActivity(intent);
                    }
                }
            }
        } else {
            this.localizationList = Localization.find(Localization.class, "name LIKE ?", "%" + searchText + "%");
        }
        toolbar.setTitle(searchText);
        MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_other, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: {
                onBackPressed();
            } break;
        }

        return true;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onMapReady(GoogleMap map) {
        MarkerOptions options = new MarkerOptions();
        CustomInfoWindowAdapter customInfoWindowAdapter = new CustomInfoWindowAdapter(getLayoutInflater());
        map.setInfoWindowAdapter(customInfoWindowAdapter);

        for (int i = 0; i < localizationList.size(); i++) {
            options.position(new LatLng(localizationList.get(i).getLat(), localizationList.get(i).getLon()));
            options.title(localizationList.get(i).getName());
            options.snippet(localizationList.get(i).getAddress());
            options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));
            map.addMarker(options);
        }

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        map.setMyLocationEnabled(true);
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        Location location = locationManager.getLastKnownLocation(locationManager.getBestProvider(criteria, false));
        if (location != null)
        {
            map.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), 13));
            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(new LatLng(location.getLatitude(), location.getLongitude()))
                    .zoom(11)
                    .build();
            map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        }

        map.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                Intent intent = new Intent(getApplicationContext(), PointActivity.class);
                intent.putExtra("address", marker.getSnippet());
                startActivity(intent);
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }



}
