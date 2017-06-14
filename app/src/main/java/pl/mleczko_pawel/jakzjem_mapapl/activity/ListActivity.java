package pl.mleczko_pawel.jakzjem_mapapl.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;

import pl.mleczko_pawel.jakzjem_mapapl.MyApplication;
import pl.mleczko_pawel.jakzjem_mapapl.R;
import pl.mleczko_pawel.jakzjem_mapapl.adapters.CustomAdapter;
import pl.mleczko_pawel.jakzjem_mapapl.adapters.CustomAdapterLogged;
import pl.mleczko_pawel.jakzjem_mapapl.model.Categories;
import pl.mleczko_pawel.jakzjem_mapapl.model.CategoriesToPoints;
import pl.mleczko_pawel.jakzjem_mapapl.model.Localization;

public class ListActivity extends AppCompatActivity {

    private String searchText;
    private String category;
    private boolean isLogged;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        MyApplication myApplication = (MyApplication) getApplicationContext();
        isLogged = myApplication.isLogged();

        MobileAds.initialize(getApplicationContext(), "ca-app-pub-4571255084527146~5427470719");
        AdView mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        if (getIntent().hasExtra("searchText")) {
            searchText = getIntent().getExtras().getString("searchText");
        }
        toolbar.setTitle(searchText);

        if (getIntent().hasExtra("category") && !getIntent().getExtras().get("category").equals("Wszystkie")) {
            category = getIntent().getExtras().getString("category");
            java.util.List<Categories> categoriesList = Categories.find(Categories.class, "pl_name = ?", category);
            if (!categoriesList.isEmpty()) {
                java.util.List<CategoriesToPoints> categoriesToPointsList = CategoriesToPoints.find(CategoriesToPoints.class, "category_id = ?", String.valueOf(categoriesList.get(0).getRemoteId()));
                if (!categoriesToPointsList.isEmpty()) {
                    java.util.List<Localization> localizations = Localization.listAll(Localization.class);
                    localizations.clear();
                    for (int i = 0; i < categoriesToPointsList.size(); i++) {
                        String[] conditions = new String[2];
                        conditions[0] = String.valueOf(categoriesToPointsList.get(i).getPointId());
                        conditions[1] = "%" + searchText + "%";
                        java.util.List<Localization> localizationList = Localization.find(Localization.class, "remote_id = ? and name LIKE ?", conditions);
                        if (!localizationList.isEmpty()) {
                            localizations.add(localizationList.get(0));
                        }
                    }
                    if (!localizations.isEmpty()) {
                        if (localizations.size() > 1) {
                            final ListView listView = (ListView) findViewById(R.id.point_view);
                            ListAdapter listAdapter;
                            if (isLogged) {
                                listAdapter = new CustomAdapterLogged(this, R.layout.row_logged, localizations);
                            } else {
                                listAdapter = new CustomAdapter(this, R.layout.row, localizations);
                            }
                            listView.setAdapter(listAdapter);
                        } else {
                            Intent intent = new Intent(getApplicationContext(), PointActivity.class);
                            intent.putExtra("id", localizations.get(0).getId());
                            startActivity(intent);
                            finish();
                        }
                    } else {
                        Intent intent = new Intent(getApplicationContext(), SearchActivity.class);
                        intent.putExtra("toastEmpty", "Brak wyników");
                        startActivity(intent);
                        finish();
                    }
                }
                else {
                    Intent intent = new Intent(getApplicationContext(), SearchActivity.class);
                    intent.putExtra("toastEmpty", "Brak wyników");
                    startActivity(intent);
                    finish();
                }
            } else {
                Intent intent = new Intent(getApplicationContext(), SearchActivity.class);
                intent.putExtra("toastEmpty", "Brak wyników");
                startActivity(intent);
                finish();
            }
        } else {
            java.util.List<Localization> localizationList = Localization.find(Localization.class, "name LIKE ?", "%" + searchText + "%");
            if (!localizationList.isEmpty()) {
                if (localizationList.size() > 1) {
                    final ListView listView = (ListView) findViewById(R.id.point_view);
                    ListAdapter listAdapter;
                    if (isLogged) {
                        listAdapter = new CustomAdapterLogged(this, R.layout.row_logged, localizationList);
                    } else {
                        listAdapter = new CustomAdapter(this, R.layout.row, localizationList);
                    }
                    listView.setAdapter(listAdapter);
                } else {
                    Intent intent = new Intent(getApplicationContext(), PointActivity.class);
                    intent.putExtra("id", localizationList.get(0).getId());
                    startActivity(intent);
                    finish();
                }
            } else {
                Intent intent = new Intent(getApplicationContext(), SearchActivity.class);
                intent.putExtra("toastEmpty", "Brak wyników");
                startActivity(intent);
                finish();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.show_on_map: {
                Intent intent = new Intent(getApplicationContext(), MapActivity.class);
                intent.putExtra("searchText", searchText);
                if (category != null) {
                    if (!category.isEmpty()) {
                        intent.putExtra("category", category);
                    }
                }
                startActivity(intent);
            } break;
            case android.R.id.home: {
                onBackPressed();
            } break;
        }

        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

}
