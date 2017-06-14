package pl.mleczko_pawel.jakzjem_mapapl.fragment;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

import pl.mleczko_pawel.jakzjem_mapapl.R;
import pl.mleczko_pawel.jakzjem_mapapl.activity.PointActivity;
import pl.mleczko_pawel.jakzjem_mapapl.adapters.CustomInfoWindowAdapter;
import pl.mleczko_pawel.jakzjem_mapapl.model.Categories;
import pl.mleczko_pawel.jakzjem_mapapl.model.CategoriesToPoints;
import pl.mleczko_pawel.jakzjem_mapapl.model.Localization;

/**
 * Created by Paweł Mleczko on 01.05.2017.
 */

public class ClosestMapFragment extends Fragment implements OnMapReadyCallback {

    private static String titleName;
    java.util.List<Localization> localizationList;
    CustomInfoWindowAdapter customInfoWindowAdapter;
    SupportMapFragment mapFragment;
    View rootView;

    public void setTitleName(String titleName) {
        ClosestMapFragment.titleName = titleName;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (rootView!= null) {
            ViewGroup parent = (ViewGroup) rootView.getParent();
            if (parent != null) {
                parent.removeView(rootView);
            }
        }
        try {
            rootView = inflater.inflate(R.layout.fragment_closest_map, container, false);
        } catch (android.view.InflateException ie) {
            Log.e("paml", ie.getMessage());
        }
        if (titleName == null) {
            titleName = "Wszystkie";
        }

        if (titleName.equals("Wszystkie")) {
            this.localizationList = Localization.listAll(Localization.class);
        } else {
            java.util.List<Categories> categoriesList = Categories.find(Categories.class, "pl_name = ?", titleName);
            if (!categoriesList.isEmpty()) {
                java.util.List<CategoriesToPoints> categoriesToPointsList = CategoriesToPoints.find(CategoriesToPoints.class, "category_id = ?", String.valueOf(categoriesList.get(0).getRemoteId()));
                if (!categoriesToPointsList.isEmpty()) {
                    this.localizationList = new ArrayList<>();
                    for (int i = 0; i < categoriesToPointsList.size(); i++) {
                        java.util.List<Localization> localizationList = Localization.find(Localization.class, "remote_id = ?", String.valueOf(categoriesToPointsList.get(i).getPointId()));
                        if (!localizationList.isEmpty()) {
                            this.localizationList.add(localizationList.get(0));
                        }
                    }
                }
            }
        }
        customInfoWindowAdapter = new CustomInfoWindowAdapter(inflater);

        try {
            mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map_fragment);
            mapFragment.getMapAsync(this);
        } catch (java.lang.NullPointerException e) {
            Log.e("paml", "messageeeee" + e.getMessage());
        }

        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onMapReady(GoogleMap map) {
        MarkerOptions options = new MarkerOptions();

        map.setInfoWindowAdapter(customInfoWindowAdapter);

        for (int i = 0; i < localizationList.size(); i++) {
            options.position(new LatLng(localizationList.get(i).getLat(), localizationList.get(i).getLon()));
            options.title(localizationList.get(i).getName());
            options.snippet(localizationList.get(i).getAddress());
            options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));
            map.addMarker(options);
        }

        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        map.setMyLocationEnabled(true);
        map.getUiSettings().setZoomControlsEnabled(true);
        LocationManager locationManager = (LocationManager) getContext().getSystemService(Context.LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        Location location = locationManager.getLastKnownLocation(locationManager.getBestProvider(criteria, false));
        if (location != null)
        {
            map.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), 13));
            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(new LatLng(location.getLatitude(), location.getLongitude()))
                    .zoom(16)
                    .build();
            map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        }

        map.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                Intent intent = new Intent(getContext(), PointActivity.class);
                intent.putExtra("address", marker.getSnippet());
                startActivity(intent);
            }
        });
    }
}
