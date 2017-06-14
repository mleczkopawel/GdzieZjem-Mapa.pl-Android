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

public class CustomAdapterLogged extends ArrayAdapter<Localization> {

    private Context cont = null;

    public CustomAdapterLogged(Context context, int resource, List<Localization> localizations) {
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
            convertView = layoutInflater.inflate(R.layout.row_logged, parent, false);
            viewHolder = new PointsViewHolder();
            viewHolder.name = (TextView) convertView.findViewById(R.id.row_name);
            viewHolder.address = (TextView) convertView.findViewById(R.id.row_address);
            viewHolder.likedImage = (ImageView) convertView.findViewById(R.id.star_image);

            viewHolder.name.setText(localization.getName());
            viewHolder.address.setText(localization.getAddress());
            if (localization.getLiked() != null && localization.getLiked()) {
                viewHolder.likedImage.setImageResource(R.drawable.ic_star);
            }
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (PointsViewHolder) convertView.getTag();
            viewHolder.name.setText(localization.getName());
            viewHolder.address.setText(localization.getAddress());
            if (localization.getLiked() != null && localization.getLiked()) {
                viewHolder.likedImage.setImageResource(R.drawable.ic_star);
            }
        }

        viewHolder.likedImage.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onClick(View v) {
                if (localization.getLiked()) {
                    Toast.makeText(cont, "Usunięto z ulubionych", Toast.LENGTH_LONG).show();
                    localization.setLiked(false);
                    viewHolder.likedImage.setImageResource(R.drawable.ic_star_not_iked);
                } else {
                    Toast.makeText(cont, "Dodano do ulubionych", Toast.LENGTH_LONG).show();
                    localization.setLiked(true);
                    viewHolder.likedImage.setImageResource(R.drawable.ic_star);
                }
                localization.save();
            }
        });

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
