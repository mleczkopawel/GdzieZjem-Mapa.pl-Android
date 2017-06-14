package pl.mleczko_pawel.jakzjem_mapapl.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import pl.mleczko_pawel.jakzjem_mapapl.R;
import pl.mleczko_pawel.jakzjem_mapapl.activity.SearchActivity;
import pl.mleczko_pawel.jakzjem_mapapl.classes.CategoriesViewHolder;

/**
 * Created by mlecz on 02.05.2017.
 */

public class CustomCategoriesListSearchAdapter extends ArrayAdapter<String> {

    private Context cont;

    public CustomCategoriesListSearchAdapter(@NonNull Context context, @NonNull String[] objects) {
        super(context, R.layout.row_categories_search, objects);
        this.cont = context;
    }

    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        CategoriesViewHolder mainViewHolder;
        final String category = getItem(position);
        if (convertView == null) {
            LayoutInflater layoutInflater = LayoutInflater.from(getContext());
            convertView = layoutInflater.inflate(R.layout.row_categories_search, parent, false);
            final CategoriesViewHolder viewHolder = new CategoriesViewHolder();
            viewHolder.name = (TextView) convertView.findViewById(R.id.category_name);

            viewHolder.name.setText(category);
            convertView.setTag(viewHolder);
        } else {
            mainViewHolder = (CategoriesViewHolder) convertView.getTag();
            mainViewHolder.name.setText(category);
        }

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), SearchActivity.class);
                intent.putExtra("category", category);
                cont.startActivity(intent);
            }
        });

        return convertView;
    }
}
