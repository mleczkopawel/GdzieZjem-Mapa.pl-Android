package pl.mleczko_pawel.jakzjem_mapapl.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;

import pl.mleczko_pawel.jakzjem_mapapl.R;
import pl.mleczko_pawel.jakzjem_mapapl.adapters.CustomCategoriesListSearchAdapter;
import pl.mleczko_pawel.jakzjem_mapapl.httpclient.ApiSearchStats;
import pl.mleczko_pawel.jakzjem_mapapl.model.Categories;
import pl.mleczko_pawel.jakzjem_mapapl.model.CategoriesToPoints;
import pl.mleczko_pawel.jakzjem_mapapl.model.Localization;

public class SearchActivity extends AppCompatActivity {

    AutoCompleteTextView autocomplete_search;
    String titleName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
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

        if (getIntent().hasExtra("toastEmpty")) {
            String errorMessage = getIntent().getExtras().getString("toastEmpty");
            Toast.makeText(getApplicationContext(), errorMessage, Toast.LENGTH_SHORT).show();
        }

        autocomplete_search = (AutoCompleteTextView) findViewById(R.id.autocomplete_search);
        String[] names = null;
        if (getIntent().hasExtra("category")) {
            String titleNameModal = getIntent().getExtras().getString("category");
            titleName = titleNameModal;
            if (!titleNameModal.equals("Wszystkie")) {
                java.util.List<Categories> category = Categories.find(Categories.class, "pl_name = ?", titleNameModal);
                if (!category.isEmpty()) {
                    java.util.List<CategoriesToPoints> categoriesToPointsList = CategoriesToPoints.find(CategoriesToPoints.class, "category_id = ?", String.valueOf(category.get(0).getRemoteId()));
                    if (!categoriesToPointsList.isEmpty()) {
                        names = new String[categoriesToPointsList.size()];
                        for (int i = 0; i < categoriesToPointsList.size(); i++) {
                            java.util.List<Localization> point = Localization.find(Localization.class, "remote_id = ?", String.valueOf(categoriesToPointsList.get(i).getPointId()));
                            if (!point.isEmpty()) {
                                names[i] = point.get(0).getName();
                            }
                        }
                    }
                }
            } else {
                java.util.List<Localization> localizationList = Localization.listAll(Localization.class);
                int count = localizationList.size();
                names = new String[count];

                for (int i = 0; i < count; i++) {
                    names[i] = localizationList.get(i).getName();
                }
            }
        } else {
            titleName = "Wszystkie";
            java.util.List<Localization> localizationList = Localization.listAll(Localization.class);
            int count = localizationList.size();
            names = new String[count];

            for (int i = 0; i < count; i++) {
                names[i] = localizationList.get(i).getName();
            }
        }

        ArrayAdapter<String> adapter = null;
        if (names != null) {
            adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, names);
        }

        autocomplete_search.setAdapter(adapter);
        autocomplete_search.setThreshold(1);

        toolbar.setTitle(titleName);

        onClickSearchButton();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_search, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.show_categories: {
                showDialog();
            } break;
            case android.R.id.home: {
                onBackPressed();
            } break;
        }

        return true;
    }

    private void showDialog() {
        java.util.List<Categories> categoriesList = Categories.listAll(Categories.class);
        String[] categories = new String[categoriesList.size() + 1];
        int iter;
        for (iter = 0; iter < categoriesList.size(); iter++) {
            categories[iter] = categoriesList.get(iter).getPlName();
        }
        categories[iter] = "Wszystkie";
        ListAdapter listAdapter = new CustomCategoriesListSearchAdapter(this, categories);
        ListView categoriesListView = new ListView(this);
        categoriesListView.setAdapter(listAdapter);
        AlertDialog.Builder builder = new AlertDialog.Builder(SearchActivity.this);
        builder.setView(categoriesListView);
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void onClickSearchButton() {
        final Button searchButton = (Button) findViewById(R.id.searchButton);
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String searchText = autocomplete_search.getText().toString().trim();
                if (searchText.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Proszę wpisać przynajmniej jeden znak...", Toast.LENGTH_LONG).show();
                } else {
                    ApiSearchStats apiSearchStats = new ApiSearchStats(SearchActivity.this);
                    apiSearchStats.sendSearchStats(searchText);
                    Intent intent = new Intent(getApplicationContext(), ListActivity.class);
                    if (!titleName.equals("wszystkie")) {
                        intent.putExtra("category", titleName);
                    }
                    intent.putExtra("searchText", searchText);
                    startActivity(intent);
                    finish();
                }
            }
        });
    }

}
