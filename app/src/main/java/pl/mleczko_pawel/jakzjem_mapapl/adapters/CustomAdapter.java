package pl.mleczko_pawel.jakzjem_mapapl.adapters;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;
import java.util.Objects;

import pl.mleczko_pawel.jakzjem_mapapl.R;
import pl.mleczko_pawel.jakzjem_mapapl.activity.PointActivity;
import pl.mleczko_pawel.jakzjem_mapapl.classes.PointsViewHolder;
import pl.mleczko_pawel.jakzjem_mapapl.model.LikedPoints;
import pl.mleczko_pawel.jakzjem_mapapl.model.Localization;

/**
 * Created by mlecz on 23.04.2017.
 */

public class CustomAdapter extends ArrayAdapter<Localization> {

    private Context cont = null;

    public CustomAdapter(Context context, int resource, List<Localization> localizations) {
        super(context, resource, localizations);
        cont = context;
    }



    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        final PointsViewHolder viewHolder;
        final Localization localization = getItem(position);
        if (convertView == null) {
            LayoutInflater layoutInflater = LayoutInflater.from(getContext());
            convertView = layoutInflater.inflate(R.layout.row, parent, false);
            viewHolder = new PointsViewHolder();
            viewHolder.name = (TextView) convertView.findViewById(R.id.row_name);
            viewHolder.address = (TextView) convertView.findViewById(R.id.row_address);

            viewHolder.name.setText(localization.getName());
            viewHolder.address.setText(localization.getAddress());
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (PointsViewHolder) convertView.getTag();
            viewHolder.name.setText(localization.getName());
            viewHolder.address.setText(localization.getAddress());
        }

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), PointActivity.class);
                intent.putExtra("id", localization.getId());
                cont.startActivity(intent);
            }
        });

        return convertView;
    }
}
