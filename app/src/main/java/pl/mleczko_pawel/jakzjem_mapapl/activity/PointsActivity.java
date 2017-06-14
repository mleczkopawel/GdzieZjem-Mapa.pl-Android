package pl.mleczko_pawel.jakzjem_mapapl.activity;

import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import pl.mleczko_pawel.jakzjem_mapapl.MyApplication;
import pl.mleczko_pawel.jakzjem_mapapl.R;
import pl.mleczko_pawel.jakzjem_mapapl.adapters.CustomCategoriesListPointsAdapter;
import pl.mleczko_pawel.jakzjem_mapapl.adapters.ViewPagerAdapter;
import pl.mleczko_pawel.jakzjem_mapapl.fragment.AllFragment;
import pl.mleczko_pawel.jakzjem_mapapl.fragment.AllFragmentLogged;
import pl.mleczko_pawel.jakzjem_mapapl.fragment.ClosestFragment;
import pl.mleczko_pawel.jakzjem_mapapl.fragment.ClosestFragmentLogged;
import pl.mleczko_pawel.jakzjem_mapapl.fragment.ClosestMapFragment;
import pl.mleczko_pawel.jakzjem_mapapl.fragment.LikedFragment;
import pl.mleczko_pawel.jakzjem_mapapl.model.Categories;

public class PointsActivity extends AppCompatActivity {

    private boolean isLogged;

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_points);
        String titleName = "Wszystkie";

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        MyApplication myApplication = (MyApplication) getApplicationContext();
        isLogged = myApplication.isLogged();

        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        String[] titles;
        Fragment[] fragments;
        int count;
        if (isLogged) {
            count = 4;
            titles = new String[count];
            titles[0] = "Wszystkie";
            titles[1] = "Najbliższe";
            titles[2] = "Mapa";
            titles[3] = "Ulubione";

            fragments = new Fragment[count];

            AllFragmentLogged allFragment = new AllFragmentLogged();
            allFragment.setTitleName(titleName);
            ClosestFragmentLogged closestFragment = new ClosestFragmentLogged();
            closestFragment.setTitleName(titleName);
            ClosestMapFragment closestMapFragment = new ClosestMapFragment();
            closestMapFragment.setTitleName(titleName);
            LikedFragment likedFragment = new LikedFragment();

            fragments[0] = allFragment;
            fragments[1] = closestFragment;
            fragments[2] = closestMapFragment;
            fragments[3] = likedFragment;
        } else {
            count = 2;
            titles = new String[count];
            titles[0] = "Wszystkie";
            titles[1] = "Najbliższe";

            fragments = new Fragment[count];
            AllFragment allFragment = new AllFragment();
            allFragment.setTitleName(titleName);
            ClosestFragment closestFragment = new ClosestFragment();
            closestFragment.setTitleName(titleName);
            fragments[0] = allFragment;
            fragments[1] = closestFragment;
        }
        ViewPager mViewPager = (ViewPager) findViewById(R.id.container);

        for (int i = 0; i < count; i++) {
            viewPagerAdapter.addFragment(fragments[i], titles[i]);
            mViewPager.setAdapter(viewPagerAdapter);
        }
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

        if (getIntent().hasExtra("category")) {
            titleName = getIntent().getExtras().getString("category");
        }
        toolbar.setTitle(titleName);
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (isLogged) {
            getMenuInflater().inflate(R.menu.menu_logged, menu);
        } else {
            getMenuInflater().inflate(R.menu.menu, menu);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (isLogged) {
            switch (item.getItemId()) {
                case R.id.about: {
                    startActivity(new Intent(getApplicationContext(), About.class));
                }
                break;
                case R.id.account: {
                    startActivity(new Intent(getApplicationContext(), UserAccountActivity.class));
                }
                break;
                case R.id.addNew: {
                    startActivity(new Intent(getApplicationContext(), AddNewPointActivity.class));
                }
                break;
                case R.id.search: {
                    startActivity(new Intent(getApplicationContext(), SearchActivity.class));
                }
                break;
                case R.id.show_categories: {
                    showDialog();
                }
                break;
                case android.R.id.home: {
                    onBackPressed();
                }
                break;
            }
        } else {
            switch (item.getItemId()) {
                case R.id.about: {
                    startActivity(new Intent(getApplicationContext(), About.class));
                }
                break;
                case R.id.login: {
                    startActivity(new Intent(getApplicationContext(), AuthAllActivity.class));
                }
                break;
                case R.id.search: {
                    startActivity(new Intent(getApplicationContext(), SearchActivity.class));
                }
                break;
                case R.id.show_categories: {
                    showDialog();
                }
                break;
                case android.R.id.home: {
                    onBackPressed();
                }
                break;
            }
        }

        return super.onOptionsItemSelected(item);
    }

    private void showDialog() {
        java.util.List<Categories> categoriesList = Categories.listAll(Categories.class);
        String[] categories = new String[categoriesList.size() + 1];
        int iter;
        for (iter = 0; iter < categoriesList.size(); iter++) {
            categories[iter] = categoriesList.get(iter).getPlName();
            Log.d("category " + iter, categories[iter]);
        }
        categories[iter] = "Wszystkie";
        ListAdapter listAdapter = new CustomCategoriesListPointsAdapter(this, categories);
        ListView categoriesListView = new ListView(this);
        categoriesListView.setAdapter(listAdapter);
        AlertDialog.Builder builder = new AlertDialog.Builder(PointsActivity.this);
        builder.setView(categoriesListView);
        AlertDialog dialog = builder.create();
        dialog.show();
    }
}
