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
import pl.mleczko_pawel.jakzjem_mapapl.model.LikedPoints;
import pl.mleczko_pawel.jakzjem_mapapl.model.Localization;

/**
 * Created by mlecz on 01.05.2017.
 */

public class LikedFragment extends Fragment {

    java.util.List<Localization> localizationList;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_liked, container, false);

        localizationList = Localization.find(Localization.class, "liked = ?", String.valueOf(true));

        if (this.localizationList != null) {
            if (!this.localizationList.isEmpty()) {
                final ListView listView = (ListView) rootView.findViewById(R.id.liked_view_list);
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
