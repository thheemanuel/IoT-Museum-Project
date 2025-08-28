package com.example.nfcmuseum;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.transition.AutoTransition;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.cardview.widget.CardView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ExhibitActivity extends Activity {

    Button backButton;
    Button artistButton;
    Button historyButton;
    Button descButton;
    Button similarButton;
    Button exitArtist;
    CardView infoCard;
    ImageView exhibitImg;
    MuseumExhibit exhibit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().requestFeature(Window.FEATURE_ACTIVITY_TRANSITIONS);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exhibit);
        Intent intent = getIntent();
        exhibit = (MuseumExhibit) intent.getSerializableExtra("exhibit");

        // BUTTONS & CARDS
        infoCard = findViewById(R.id.infoCard);
        backButton = findViewById(R.id.exhibit_back_button);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                controlButton(1);
                switchActivities();
            }
        });

        artistButton = findViewById(R.id.artistButton);
        artistButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                infoCard.setVisibility(View.VISIBLE);
                controlButton(0);
                ((TextView) findViewById(R.id.title)).setText("Artist Information");
                String artistID = exhibit.getArtistID();
                ((TextView) findViewById(R.id.body)).setText(artistReader(artistID, 2));
            }
        });

        exitArtist = findViewById(R.id.exitInfo);
        exitArtist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                infoCard.setVisibility(View.INVISIBLE);
                controlButton(1);
            }
        });

        historyButton = findViewById(R.id.historyButton);
        historyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                infoCard.setVisibility(View.VISIBLE);
                controlButton(0);
                Intent intent = getIntent();
                ExhibitInfo info = (ExhibitInfo) intent.getSerializableExtra("exhibitInfo");
                ((TextView) findViewById(R.id.title)).setText("History");
                ((TextView) findViewById(R.id.body)).setText(info.getHistoryInfoCard());
            }
        });

        descButton = findViewById(R.id.descButton);
        descButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                infoCard.setVisibility(View.VISIBLE);
                controlButton(0);
                Intent intent = getIntent();
                ExhibitInfo info = (ExhibitInfo) intent.getSerializableExtra("exhibitInfo");
                ((TextView) findViewById(R.id.title)).setText("Description");
                ((TextView) findViewById(R.id.body)).setText(info.getDescriptionInfoCard());
            }
        });

        similarButton = findViewById(R.id.similarButton);
        similarButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                switchToMap();

            }
        });

        // Set an exit transition
        getWindow().setExitTransition(new AutoTransition());
        exhibitImg = findViewById(R.id.exhibitImage);
        // display the title string on textView
        ((TextView) findViewById(R.id.exhibitTitle)).setText(exhibit.getTitle());
        ((TextView) findViewById(R.id.exhibitYear)).setText(exhibit.getYear());

        String name = exhibit.getImg();
        int id = getResources().getIdentifier(name, "drawable", getPackageName());
        exhibitImg.setImageResource(id);

        String artistID = exhibit.getArtistID();
        ((TextView) findViewById(R.id.exhibitArtist)).setText(artistReader(artistID, 1));
    }

    private void controlButton(int i){
        similarButton = findViewById(R.id.similarButton);
        descButton = findViewById(R.id.descButton);
        historyButton = findViewById(R.id.historyButton);
        exitArtist = findViewById(R.id.exitInfo);
        artistButton = findViewById(R.id.artistButton);
        backButton = findViewById(R.id.exhibit_back_button);

        if (i == 0){
            similarButton.setClickable(false);
            descButton.setClickable(false);
            historyButton.setClickable(false);
            artistButton.setClickable(false);
        }else if (i == 1){
            similarButton.setClickable(true);
            descButton.setClickable(true);
            historyButton.setClickable(true);
            artistButton.setClickable(true);
        }

    }

    // This methods takes the artist ID and puts the correct artist under the title via the database.
    // as well as the description of the artist if the integer value is correct. (set to 2)
    public String artistReader(String artistID, int i) {
        String fileName = "artist_database.txt";
        Map<String, List<String>> artistMap = new HashMap<>();

        BufferedReader reader = null;

        try {
            reader = new BufferedReader(new InputStreamReader(getAssets().open(fileName)));
            String line;
            String currentArtistId = null;
            List<String> currentArtistParameters = new ArrayList<>();

            while ((line = reader.readLine()) != null) {
                if (line.equals("-")) {
                    if (currentArtistId != null) {
                        artistMap.put(currentArtistId, new ArrayList<>(currentArtistParameters));
                        currentArtistParameters.clear();
                    }
                } else {
                    String[] parts = line.split("=");
                    if (parts.length == 2) {
                        String key = parts[0].trim();
                        String value = parts[1].trim();

                        if ((key.equals("artist_id"))) {
                            currentArtistId = value;
                        }

                        currentArtistParameters.add(value);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        if (artistMap.containsKey(artistID)) {
            List<String> artistParameters = artistMap.get(artistID);
            if (artistParameters.size() >= 3) {
                //artistParameters.get(0);  // artist id
                //artistParameters.get(1);  // artist name
                //artistParameters.get(2); // artist description
                if(i == 1) {
                    return artistParameters.get(1);
                }else if (i == 2){
                    return artistParameters.get(2);
                }
            }
        }
        return null;
    }

    private void switchActivities() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        finish();
        startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(this).toBundle());
    }

    private void switchToMap() {
        Intent intent = new Intent(this, MapActivity.class);
        intent.putExtra("exhibitMap", exhibit); //This is where payload should be used to deliver the ID
        intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(this).toBundle());
    }
}
