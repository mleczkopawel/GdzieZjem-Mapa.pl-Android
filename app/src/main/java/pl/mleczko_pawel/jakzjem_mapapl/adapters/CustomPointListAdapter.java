package pl.mleczko_pawel.jakzjem_mapapl.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.HapticFeedbackConstants;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import pl.mleczko_pawel.jakzjem_mapapl.R;
import pl.mleczko_pawel.jakzjem_mapapl.activity.SearchActivity;
import pl.mleczko_pawel.jakzjem_mapapl.classes.CategoriesViewHolder;

/**
 * Created by mlecz on 02.05.2017.
 */

public class CustomPointListAdapter extends ArrayAdapter<String> {

    private Context cont;

    public CustomPointListAdapter(@NonNull Context context, @NonNull String[] objects) {
        super(context, R.layout.row_point_list, objects);
        cont = context;
    }

    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        CategoriesViewHolder mainViewHolder;
        final String category = getItem(position);
        if (convertView == null) {
            LayoutInflater layoutInflater = LayoutInflater.from(getContext());
            convertView = layoutInflater.inflate(R.layout.row_point_list, parent, false);
            final CategoriesViewHolder viewHolder = new CategoriesViewHolder();
            viewHolder.name = (TextView) convertView.findViewById(R.id.name);

            viewHolder.name.setText(category);
            convertView.setTag(viewHolder);
        } else {
            mainViewHolder = (CategoriesViewHolder) convertView.getTag();
            mainViewHolder.name.setText(category);
        }

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getContext(), "Przytrzymaj dłużej a przeniesie Cię do wyszukiwania w tej kategorii.", Toast.LENGTH_LONG).show();
            }
        });

        convertView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                view.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS);
                Intent intent = new Intent(getContext(), SearchActivity.class);
                intent.putExtra("category", category);
                cont.startActivity(intent);
                return false;
            }
        });

        return convertView;
    }
}