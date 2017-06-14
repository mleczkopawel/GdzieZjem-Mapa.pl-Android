package pl.mleczko_pawel.jakzjem_mapapl.adapters;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

import pl.mleczko_pawel.jakzjem_mapapl.R;

/**
 * Created by mlecz on 18.05.2017.
 */

public class CustomInfoWindowAdapter implements GoogleMap.InfoWindowAdapter {

    private final View myContentsView;

    @SuppressLint("InflateParams")
    public CustomInfoWindowAdapter(LayoutInflater layoutInflater) {
        myContentsView = layoutInflater.inflate(R.layout.custom_info_marker, null);
    }

    @Override
    public View getInfoWindow(Marker marker) {
        return null;
    }

    @Override
    public View getInfoContents(Marker marker) {
        TextView name = ((TextView)myContentsView.findViewById(R.id.name_textView));
        TextView address = ((TextView)myContentsView.findViewById(R.id.address_textView));

        name.setText(marker.getTitle());
        address.setText(marker.getSnippet());

        return myContentsView;
    }
}