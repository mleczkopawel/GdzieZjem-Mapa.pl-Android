package pl.mleczko_pawel.jakzjem_mapapl.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.util.Linkify;
import android.view.HapticFeedbackConstants;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.analytics.FirebaseAnalytics;

import pl.mleczko_pawel.jakzjem_mapapl.R;
import pl.mleczko_pawel.jakzjem_mapapl.adapters.CustomPointListAdapter;
import pl.mleczko_pawel.jakzjem_mapapl.httpclient.ApiLocalization;
import pl.mleczko_pawel.jakzjem_mapapl.model.Categories;
import pl.mleczko_pawel.jakzjem_mapapl.model.CategoriesToPoints;
import pl.mleczko_pawel.jakzjem_mapapl.model.Localization;

public class PointActivity extends AppCompatActivity implements OnMapReadyCallback {

    private Localization point;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_point);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        MobileAds.initialize(getApplicationContext(), "ca-app-pub-4571255084527146~5427470719");
        AdView mAdView = (AdView) findViewById(R.id.adView_point);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        TextView singleName = (TextView) findViewById(R.id.single_name);
        TextView singleAddress = (TextView) findViewById(R.id.single_address);
        Button singlePhone = (Button) findViewById(R.id.single_phone);
        Button singleUrl = (Button) findViewById(R.id.single_url);

        if (getIntent().hasExtra("id")) {
            final Long position = getIntent().getExtras().getLong("id", -1);
            point = Localization.findById(Localization.class, position);
        } else if (getIntent().hasExtra("address")) {
            String address = getIntent().getExtras().getString("address");
            java.util.List<Localization> pointFirst = Localization.find(Localization.class, "address = ?", address);
            point = pointFirst.get(0);
        }

        singleName.setText(point.getName());
        singleAddress.setText(point.getAddress());

        singlePhone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent callIntent = new Intent(Intent.ACTION_CALL);
                callIntent.setData(Uri.parse("tel:" + point.getPhone()));
                if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                startActivity(callIntent);
            }
        });

        singleUrl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Uri uri = Uri.parse(point.getUrl());
                startActivity(new Intent(Intent.ACTION_VIEW, uri));
            }
        });

        Linkify.addLinks(singlePhone, Linkify.PHONE_NUMBERS);
        Linkify.addLinks(singleUrl, Linkify.WEB_URLS);

        java.util.List<CategoriesToPoints> categoriesToPointsList = CategoriesToPoints.find(CategoriesToPoints.class, "point_id = ?", point.getRemoteId().toString());
        if (!categoriesToPointsList.isEmpty()) {
            final ListView listView = (ListView) findViewById(R.id.categories_list);
            String[] categories = new String[categoriesToPointsList.size()];
            for (int i = 0; i < categoriesToPointsList.size(); i++) {
                java.util.List<Categories> category = Categories.find(Categories.class, "remote_id = ?", String.valueOf(categoriesToPointsList.get(i).getCategoryId()));
                categories[i] = category.get(0).getPlName();
            }
            ListAdapter listAdapter = new CustomPointListAdapter(this, categories);
            listView.setAdapter(listAdapter);
            setListViewHeightBasedOnItems(listView);
        }

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(), "Przytrzymaj dłużej... \nAby ta funkcja działała poprawnie, aplikacja Jakdoaje MUSI działać w tle.", Toast.LENGTH_LONG).show();
            }
        });

        fab.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                view.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS);
                Uri uri = Uri.parse("http://krakow.jakdojade.pl/?tc=" + point.getLat() + ":" + point.getLon() + "&as=true");
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
                return false;
            }
        });

        MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map_single);
        mapFragment.getMapAsync(this);

        ApiLocalization apiLocalization = new ApiLocalization(PointActivity.this);
        apiLocalization.get(point.getRemoteId());
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
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

    @Override
    public void onMapReady(GoogleMap map) {
        LatLng pointOnMap = new LatLng(point.getLat(), point.getLon());

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
//        map.setMyLocationEnabled(true);
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(pointOnMap, 13));

        map.addMarker(new MarkerOptions().position(pointOnMap).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
    }

    public static boolean setListViewHeightBasedOnItems(ListView listView) {

        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter != null) {

            int numberOfItems = listAdapter.getCount();

            int totalItemsHeight = 0;
            for (int itemPos = 0; itemPos < numberOfItems; itemPos++) {
                View item = listAdapter.getView(itemPos, null, listView);
                float px = 500 * (listView.getResources().getDisplayMetrics().density);
                item.measure(View.MeasureSpec.makeMeasureSpec((int)px, View.MeasureSpec.AT_MOST), View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
                totalItemsHeight += item.getMeasuredHeight();
            }

            int totalDividersHeight = listView.getDividerHeight() * (numberOfItems - 1);
            int totalPadding = listView.getPaddingTop() + listView.getPaddingBottom();

            ViewGroup.LayoutParams params = listView.getLayoutParams();
            params.height = totalItemsHeight + totalDividersHeight + totalPadding;
            listView.setLayoutParams(params);
            listView.requestLayout();
            return true;
        } else {
            return false;
        }
    }
}
