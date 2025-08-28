package com.example.nfcmuseum;
import android.app.Activity;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.transition.AutoTransition;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidx.cardview.widget.CardView;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class MapActivity extends Activity {
    RelativeLayout rl;
    ImageView mapImg;
    Button backBtn;
    ImageButton exhibitNode;
    MuseumExhibit exhibit;
    CheckBox periodCheck;
    CheckBox typeCheck;
    CheckBox areaCheck;
    CheckBox artistCheck;
    int[] filters = new int[4];
    CardView info;
    Button exitInfo;
    TextView currentAttr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().requestFeature(Window.FEATURE_ACTIVITY_TRANSITIONS);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        getWindow().getDecorView().setBackgroundColor(Color.BLACK);
        // Set an exit transition
        getWindow().setExitTransition(new AutoTransition());

        rl = findViewById(R.id.rl);
        mapImg = findViewById(R.id.mapImage);
        mapImg.setImageResource(R.drawable.testmap);

        backBtn = findViewById(R.id.backButton);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switchActivities();
            }
        });

        periodCheck = findViewById(R.id.periodCheck);
        periodCheck.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    filters[0] = 1;
                    resetNodes();
                    checkBoxes(filters);
                } else {
                    filters[0] = 0;
                    resetNodes();
                    checkBoxes(filters);
                }
            }
        });

        artistCheck = findViewById(R.id.artistCheck);
        artistCheck.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    filters[1] = 1;
                    resetNodes();
                    checkBoxes(filters);
                } else {
                    filters[1] = 0;
                    resetNodes();
                    checkBoxes(filters);
                }
            }
        });

        exitInfo = findViewById(R.id.exitInfo);
        exitInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                info.setVisibility(View.INVISIBLE);
            }
        });

        typeCheck = findViewById(R.id.typeCheck);
        typeCheck.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    filters[2] = 1;
                    resetNodes();
                    checkBoxes(filters);
                } else {
                    filters[2] = 0;
                    resetNodes();
                    checkBoxes(filters);
                }
            }
        });

        areaCheck = findViewById(R.id.areaCheck);
        areaCheck.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    filters[3] = 1;
                    resetNodes();
                    checkBoxes(filters);
                } else {
                    filters[3] = 0;
                    resetNodes();
                    checkBoxes(filters);
                }
            }
        });

        Intent intent = getIntent();
        exhibit = (MuseumExhibit) intent.getSerializableExtra("exhibitMap");
        initializeBtn(Integer.parseInt(exhibit.getExhibitID()));
        exhibitNode.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("red")));
        checkBoxes(filters);
        currentAttr = findViewById(R.id.currentAttributes);
        currentAttr.setText("You are currently at the exhibit " + exhibit.getTitle() + " which has these attributes:\nPeriod: " + exhibit.getPeriod()
                + "\nArtist: " + artistReader(exhibit.getArtistID()) + "\nType: " + exhibit.getType() + "\nArea: " + exhibitReader(exhibit.getExhibitID()).getLocation());


    }

    // Code to run when you click on a node (circle on map)
    // The clicked node will be marked as orange
    public void nodeClick(View view){
        resetNodes();
        checkBoxes(filters);
        view.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#FF8222")));
        info = findViewById(R.id.infoCard);
        String s = (String) view.getTag();
        info.setVisibility(View.VISIBLE);
        ((TextView) findViewById(R.id.body)).setText(similarExhibitsReader(s) + "Located at: " + exhibitReader(String.valueOf(Integer.parseInt(s, 16))).getLocation());
        ((TextView) findViewById(R.id.title)).setText(exhibitReader(String.valueOf(Integer.parseInt(s, 16))).getTitle());
    }

    // Fetch the exhibits that are similar and mark them on the map as green.
    private void checkBoxes(int[] b) {
        MuseumExhibit simExhibit;
        ArrayList<MuseumExhibit> similar = new ArrayList<MuseumExhibit>();
        for (String s : exhibit.getInfoSimilarExhibits()) {
            System.out.println(s);
            simExhibit = exhibitReader(s);

            if (Arrays.equals(b, new int[]{0, 0, 0, 0})) {
                similar.add(simExhibit);
                initializeBtn(Integer.parseInt(simExhibit.getExhibitID()));
                exhibitNode.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("green")));
            } else if (Arrays.equals(b, new int[]{0, 0, 0, 1})) {
                if (simExhibit.getLocation().equals(exhibit.getLocation())) {
                    similar.add(simExhibit);
                    initializeBtn(Integer.parseInt(simExhibit.getExhibitID()));
                    exhibitNode.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("green")));
                }
            } else if (Arrays.equals(b, new int[]{0, 0, 1, 0})) {
                if (simExhibit.getType().equals(exhibit.getType())) {
                    similar.add(simExhibit);
                    initializeBtn(Integer.parseInt(simExhibit.getExhibitID()));
                    exhibitNode.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("green")));
                }
            } else if (Arrays.equals(b, new int[]{0, 0, 1, 1})) {
                if (simExhibit.getLocation().equals(exhibit.getLocation()) && simExhibit.getType().equals(exhibit.getType())) {
                    similar.add(simExhibit);
                    initializeBtn(Integer.parseInt(simExhibit.getExhibitID()));
                    exhibitNode.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("green")));
                }
            } else if (Arrays.equals(b, new int[]{0, 1, 0, 0})) {
                if (simExhibit.getArtistID().equals(exhibit.getArtistID())) {
                    similar.add(simExhibit);
                    initializeBtn(Integer.parseInt(simExhibit.getExhibitID()));
                    exhibitNode.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("green")));
                }
            } else if (Arrays.equals(b, new int[]{0, 1, 0, 1})) {
                if (simExhibit.getLocation().equals(exhibit.getLocation()) && simExhibit.getArtistID().equals(exhibit.getArtistID())) {
                    similar.add(simExhibit);
                    initializeBtn(Integer.parseInt(simExhibit.getExhibitID()));
                    exhibitNode.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("green")));
                }
            } else if (Arrays.equals(b, new int[]{0, 1, 1, 0})) {
                if (simExhibit.getArtistID().equals(exhibit.getArtistID()) && simExhibit.getType().equals(exhibit.getType())) {
                    similar.add(simExhibit);
                    initializeBtn(Integer.parseInt(simExhibit.getExhibitID()));
                    exhibitNode.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("green")));
                }
            } else if (Arrays.equals(b, new int[]{0, 1, 1, 1})) {
                if (simExhibit.getLocation().equals(exhibit.getLocation()) && simExhibit.getArtistID().equals(exhibit.getArtistID()) && simExhibit.getType().equals(exhibit.getType())) {
                    similar.add(simExhibit);
                    initializeBtn(Integer.parseInt(simExhibit.getExhibitID()));
                    exhibitNode.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("green")));
                }
            } else if (Arrays.equals(b, new int[]{1, 0, 0, 0})) {
                if (simExhibit.getPeriod().equals(exhibit.getPeriod())) {
                    similar.add(simExhibit);
                    initializeBtn(Integer.parseInt(simExhibit.getExhibitID()));
                    exhibitNode.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("green")));
                }
            } else if (Arrays.equals(b, new int[]{1, 0, 0, 1})) {
                if (simExhibit.getPeriod().equals(exhibit.getPeriod()) && simExhibit.getLocation().equals(exhibit.getLocation())) {
                    similar.add(simExhibit);
                    initializeBtn(Integer.parseInt(simExhibit.getExhibitID()));
                    exhibitNode.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("green")));
                }
            } else if (Arrays.equals(b, new int[]{1, 0, 1, 0})) {
                if (simExhibit.getPeriod().equals(exhibit.getPeriod()) && simExhibit.getType().equals(exhibit.getType())) {
                    similar.add(simExhibit);
                    initializeBtn(Integer.parseInt(simExhibit.getExhibitID()));
                    exhibitNode.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("green")));
                }
            } else if (Arrays.equals(b, new int[]{1, 0, 1, 1})) {
                if (simExhibit.getLocation().equals(exhibit.getLocation()) && simExhibit.getType().equals(exhibit.getType()) && simExhibit.getPeriod().equals(exhibit.getPeriod())) {
                    similar.add(simExhibit);
                    initializeBtn(Integer.parseInt(simExhibit.getExhibitID()));
                    exhibitNode.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("green")));
                }
            } else if (Arrays.equals(b, new int[]{1, 1, 0, 0})) {
                if (simExhibit.getPeriod().equals(exhibit.getPeriod()) && simExhibit.getArtistID().equals(exhibit.getArtistID())) {
                    similar.add(simExhibit);
                    initializeBtn(Integer.parseInt(simExhibit.getExhibitID()));
                    exhibitNode.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("green")));
                }
            } else if (Arrays.equals(b, new int[]{1, 1, 0, 1})) {
                if (simExhibit.getLocation().equals(exhibit.getLocation()) && simExhibit.getPeriod().equals(exhibit.getPeriod()) && simExhibit.getArtistID().equals(exhibit.getArtistID())) {
                    similar.add(simExhibit);
                    initializeBtn(Integer.parseInt(simExhibit.getExhibitID()));
                    exhibitNode.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("green")));
                }
            } else if (Arrays.equals(b, new int[]{1, 1, 1, 0})) {
                if (simExhibit.getType().equals(exhibit.getType()) && simExhibit.getPeriod().equals(exhibit.getPeriod()) && simExhibit.getArtistID().equals(exhibit.getArtistID())) {
                    similar.add(simExhibit);
                    initializeBtn(Integer.parseInt(simExhibit.getExhibitID()));
                    exhibitNode.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("green")));
                }
            } else if (Arrays.equals(b, new int[]{1, 1, 1, 1})) {
                if (simExhibit.getLocation().equals(exhibit.getLocation()) && simExhibit.getPeriod().equals(exhibit.getPeriod()) && simExhibit.getArtistID().equals(exhibit.getArtistID()) && simExhibit.getType().equals(exhibit.getType())) {
                    similar.add(simExhibit);
                    initializeBtn(Integer.parseInt(simExhibit.getExhibitID()));
                    exhibitNode.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("green")));
                }
            }
        }
    }

    // Select the node on the map using its tag
    // (change the reference object for exhibitNode)
    private void initializeBtn(int id){
        String tag = Integer.toHexString(id);
        exhibitNode = (ImageButton) rl.findViewWithTag(tag);
    }

    // Reset nodes to initial colors
    private void resetNodes () {
        for (int i = 1; i <= 15; i++) {
            initializeBtn(i);
            exhibitNode.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#666666")));
        }
        initializeBtn(Integer.parseInt(exhibit.getExhibitID()));
        exhibitNode.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("red")));
    }

    private String similarExhibitsReader(String targetExhibitIds) {
        String fileName = "similar_exhibits_database.txt";
        StringBuilder result = new StringBuilder();
        BufferedReader reader = null;

        try {
            reader = new BufferedReader(new InputStreamReader(getAssets().open(fileName)));
            String line;
            String currentExhibitId = null;
            StringBuilder currentSimilarExhibits = new StringBuilder();

            while ((line = reader.readLine()) != null) {
                if (line.startsWith("exhibit_id")) {
                    // Extract the exhibit ID
                    currentExhibitId = line.split("=")[1].trim();

                    // Check if the current exhibit ID is one of the target exhibit IDs
                    if (targetExhibitIds.contains(currentExhibitId)) {
                        // Check if we have similar exhibits for the previous exhibit
                        if (currentSimilarExhibits.length() > 0) {
                            result.append(currentSimilarExhibits);
                            currentSimilarExhibits = new StringBuilder();
                        }
                    }
                } else if (line.startsWith("similar_exhibit")) {
                    // Append the similar exhibits information only if the current exhibit ID matches a target ID
                    if (currentExhibitId != null && targetExhibitIds.contains(currentExhibitId)) {
                        currentSimilarExhibits.append(line.split("=")[1].trim()).append("\n" + "\n");
                    }
                } else if (line.equals("-")) {
                    // End of an exhibit, add parameters to the result
                    if (currentExhibitId != null && targetExhibitIds.contains(currentExhibitId)) {
                        result.append(currentSimilarExhibits);
                        currentSimilarExhibits = new StringBuilder();
                    }
                }
            }

            // Check for the last exhibit
            if (currentExhibitId != null && targetExhibitIds.contains(currentExhibitId)) {
                result.append(currentSimilarExhibits);
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
        return result.toString();
    }

    private String artistReader(String artistID) {
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
            if (artistParameters.size() >= 1) {

                return artistParameters.get(1);  // artist name

            }
        }
        return null;
    }

    private MuseumExhibit exhibitReader(String targetExhibitId) {
        String fileName = "exhibit_database.txt";
        Map<String, List<String>> exhibitsMap = new HashMap<>();
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new InputStreamReader(getAssets().open(fileName)));
            String line;
            String currentExhibitId = null;
            List<String> currentExhibitParameters = new ArrayList<>();
            while ((line = reader.readLine()) != null) {
                if (line.equals("-")) {
                    // End of an exhibit, add parameters to the map
                    if (currentExhibitId != null) {
                        exhibitsMap.put(currentExhibitId, new ArrayList<>(currentExhibitParameters));
                        currentExhibitParameters.clear();
                    }
                } else {
                    // Split the line into key and value
                    String[] parts = line.split("=");
                    if (parts.length == 2) {
                        String key = parts[0].trim();
                        String value = parts[1].trim();
                        if (key.equals("exhibit_id")) {
                            currentExhibitId = value;
                        }
                        currentExhibitParameters.add(value);
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
        // Ensure the map contains the target exhibit
        if (exhibitsMap.containsKey(targetExhibitId)) {
            List<String> exhibitParameters = exhibitsMap.get(targetExhibitId);
            // Check if there are enough parameters
            if (exhibitParameters.size() >= 5) {
                List<String> objectList = new ArrayList<>(Arrays.asList(exhibitParameters.get(7).split(",")));
                MuseumExhibit exhibit = new MuseumExhibit(
                        exhibitParameters.get(0),  // exhibit id
                        exhibitParameters.get(1),  // exhibit title
                        exhibitParameters.get(2),  // artist year
                        exhibitParameters.get(3),  // artist id
                        exhibitParameters.get(4),  //image file
                        exhibitParameters.get(8),  //period
                        exhibitParameters.get(9),  //type
                        exhibitParameters.get(10), // location
                        objectList // list of similar exhibits, strings
                );
                return exhibit;
            }
        }
        return null;
    }

    private void switchActivities() {
        finish();
    }
}
