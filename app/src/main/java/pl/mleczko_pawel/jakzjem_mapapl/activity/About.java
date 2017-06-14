package pl.mleczko_pawel.jakzjem_mapapl.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.util.Linkify;
import android.widget.TextView;

import pl.mleczko_pawel.jakzjem_mapapl.R;

/**
 * Created by mlecz on 25.04.2017.
 */

public class About extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.about_activity);
        TextView authorUrl = (TextView) findViewById(R.id.about_app_author_url);
        authorUrl.setText("https://mleczko-pawel.pl");
        Linkify.addLinks(authorUrl, Linkify.WEB_URLS);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
