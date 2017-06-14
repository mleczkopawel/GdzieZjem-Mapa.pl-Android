package pl.mleczko_pawel.jakzjem_mapapl.fragment;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import pl.mleczko_pawel.jakzjem_mapapl.R;
import pl.mleczko_pawel.jakzjem_mapapl.adapters.CustomAdapter;
import pl.mleczko_pawel.jakzjem_mapapl.adapters.CustomAdapterLogged;
import pl.mleczko_pawel.jakzjem_mapapl.model.Categories;
import pl.mleczko_pawel.jakzjem_mapapl.model.CategoriesToPoints;
import pl.mleczko_pawel.jakzjem_mapapl.model.Localization;
import pl.mleczko_pawel.jakzjem_mapapl.service.LocationService;

/**
 * Created by mlecz on 01.05.2017.
 */

public class ClosestFragmentLogged extends Fragment {

    View rootView;
    private static String titleName;
    private Context mainContext;
    private static final String BROADCAST_ACTION = "pl.paml.gzm.location";
    IntentFilter intentFilter = new IntentFilter(BROADCAST_ACTION);
    BroadcastReceiver pointReceiver;
    PendingIntent pendingIntent;
    List<Localization> localizationList;
    private boolean unregistered;

    public void setTitleName(String titleName) {
        ClosestFragmentLogged.titleName = titleName;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d("paml", "ocCreateView");
        rootView = inflater.inflate(R.layout.fragment_closest, container, false);
        final TextView textView = (TextView) rootView.findViewById(R.id.textView);
        final ProgressBar progressBar = (ProgressBar) rootView.findViewById(R.id.progressBar);
        textView.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.VISIBLE);


        if (titleName == null) {
            titleName = "Wszystkie";
        }

        if (titleName.equals("Wszystkie")) {
            this.localizationList = Localization.listAll(Localization.class);
        } else {
            List<Categories> categoriesList = Categories.find(Categories.class, "pl_name = ?", titleName);
            if (!categoriesList.isEmpty()) {
                List<CategoriesToPoints> categoriesToPointsList = CategoriesToPoints.find(CategoriesToPoints.class, "category_id = ?", String.valueOf(categoriesList.get(0).getRemoteId()));
                if (!categoriesToPointsList.isEmpty()) {
                    this.localizationList = Localization.listAll(Localization.class);
                    this.localizationList.clear();
                    for (int i = 0; i < categoriesToPointsList.size(); i++) {
                        List<Localization> localizationList = Localization.find(Localization.class, "remote_id = ?", String.valueOf(categoriesToPointsList.get(i).getPointId()));
                        if (!localizationList.isEmpty()) {
                            this.localizationList.add(localizationList.get(0));
                        }
                    }
                }
            }
        }

        pointReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                mainContext.unregisterReceiver(pointReceiver);
                unregistered = true;
                mainContext.stopService(new Intent(mainContext, LocationService.class));
                Location myLocations = new Location("my");
                myLocations.setLatitude((Double) intent.getExtras().get("Latitude"));
                myLocations.setLongitude((Double) intent.getExtras().get("Longitude"));
                List<PointCacheLocator> pointCacheLocators = new ArrayList<>();
                List<Localization> closestList = new ArrayList<>();
                if (!localizationList.isEmpty()) {
                    for (int i = 0; i < localizationList.size(); i++) {
                        Location pointLocations = new Location("point");
                        pointLocations.setLatitude(localizationList.get(i).getLat());
                        pointLocations.setLongitude(localizationList.get(i).getLon());
                        PointCacheLocator pointCacheLocator = new PointCacheLocator();
                        pointCacheLocator.setPointId(localizationList.get(i).getId());
                        pointCacheLocator.setDistance(myLocations.distanceTo(pointLocations));
                        pointCacheLocators.add(pointCacheLocator);
                    }

                    Collections.sort(pointCacheLocators);

                    for (int i = 0; i < pointCacheLocators.size(); i++) {
                        if (i < 10) {
                            Localization point = Localization.findById(Localization.class, pointCacheLocators.get(i).getPointId());
                            closestList.add(point);
                        } else {
                            break;
                        }
                    }

                    if (!localizationList.isEmpty()) {
                        final ListView listView = (ListView) rootView.findViewById(R.id.closest_view);
                        ListAdapter listAdapter;
                        listAdapter = new CustomAdapterLogged(getActivity(), R.layout.row_logged, closestList);
                        listView.setAdapter(listAdapter);
                        textView.setVisibility(View.GONE);
                        progressBar.setVisibility(View.GONE);
                    } else {
                        Toast.makeText(rootView.getContext(), "Brak wyników...", Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(rootView.getContext(), "Brak wyników...", Toast.LENGTH_LONG).show();
                }
            }
        };

        mainContext.registerReceiver(pointReceiver, intentFilter);
        pendingIntent = PendingIntent.getBroadcast(mainContext, 0, new Intent("pl.paml.gzm.location"), 0);

        return rootView;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Log.d("paml", "onAttach");
        mainContext = context;
        mainContext.startService(new Intent(mainContext, LocationService.class));
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d("paml", "onPause");
        mainContext.stopService(new Intent(mainContext, LocationService.class));
        if (!unregistered) {
            mainContext.unregisterReceiver(pointReceiver);
        }
        unregistered = true;
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d("paml", "onResume");
        Log.d("paml", "registered " + unregistered);
        if (unregistered) {
            mainContext.startService(new Intent(mainContext, LocationService.class));
            mainContext.registerReceiver(pointReceiver, intentFilter);
        }
    }
//
//    @Override
//    public void onDetach() {
//        super.onDetach();
//        Log.d("paml", "onFetach");
//        mainContext.stopService(new Intent(mainContext, LocationService.class));
//        if (!unregistered) {
//            mainContext.unregisterReceiver(pointReceiver);
//            unregistered = true;
//        }
//    }

    private class PointCacheLocator implements Comparable<PointCacheLocator> {
        private double distance;
        private Long pointId;

        public double getDistance() {
            return distance;
        }

        public void setDistance(double distance) {
            this.distance = distance;
        }

        public Long getPointId() {
            return pointId;
        }

        public void setPointId(Long pointId) {
            this.pointId = pointId;
        }

        @Override
        public int compareTo(@NonNull PointCacheLocator pointCacheLocator) {
            return distance < pointCacheLocator.getDistance() ? -1 : distance > pointCacheLocator.getDistance() ? 1 : 0;
        }
    }
}
