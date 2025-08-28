package com.example.nfcmuseum;

import android.media.Image;
import java.io.Serializable;
import java.util.List;

public class MuseumExhibit implements Serializable {

    private String title;
    private String year;
    private String img;
    private String artistID;
    private String exhibitID;
    private String period;
    private String location;
    private String type;
    private List<String> infoSimilarExhibits;


    public MuseumExhibit(String exhibitID, String title, String year, String artistID, String  img, String period, String type, String location, List<String> infoSimilarExhibits) {
        this.title = title;
        this.year = year;
        this.img = img;
        this.artistID = artistID;
        this.exhibitID = exhibitID;
        this.period = period;
        this.location = location;
        this.type = type;
        this.infoSimilarExhibits = infoSimilarExhibits;

    }
    public String getTitle() {
        return title;
    }

    public String getYear() {
        return year;
    }

    public String getImg() {
        return img;
    }

    public String getArtistID() {
        return artistID;
    }

    public String getExhibitID() {
        return exhibitID;
    }

    public String getType() {
        return type;
    }

    public String getPeriod() {
        return period;
    }

    public String getLocation() {
        return location;
    }

    public List<String> getInfoSimilarExhibits() {
        return infoSimilarExhibits;
    }
}
