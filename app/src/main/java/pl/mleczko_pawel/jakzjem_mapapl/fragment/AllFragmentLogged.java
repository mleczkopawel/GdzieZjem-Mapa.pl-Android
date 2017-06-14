package pl.mleczko_pawel.jakzjem_mapapl.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

import pl.mleczko_pawel.jakzjem_mapapl.R;
import pl.mleczko_pawel.jakzjem_mapapl.adapters.CustomAdapter;
import pl.mleczko_pawel.jakzjem_mapapl.adapters.CustomAdapterLogged;
import pl.mleczko_pawel.jakzjem_mapapl.model.Categories;
import pl.mleczko_pawel.jakzjem_mapapl.model.CategoriesToPoints;
import pl.mleczko_pawel.jakzjem_mapapl.model.Localization;

/**
 * Created by mlecz on 01.05.2017.
 */

public class AllFragmentLogged extends Fragment {

    private static String titleName;
    java.util.List<Localization> localizationList;

    public void setTitleName(String titleName) {
        AllFragmentLogged.titleName = titleName;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_all, container, false);

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

        if (this.localizationList != null) {
            if (!this.localizationList.isEmpty()) {
                final ListView listView = (ListView) rootView.findViewById(R.id.all_view);
                ListAdapter listAdapter = new CustomAdapterLogged(getActivity(), R.layout.row_logged, this.localizationList);
                listView.setAdapter(listAdapter);
            } else {
                Toast.makeText(rootView.getContext(), "Brak wyników...", Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(rootView.getContext(), "Brak wyników...", Toast.LENGTH_LONG).show();
        }

        return rootView;
    }


}
