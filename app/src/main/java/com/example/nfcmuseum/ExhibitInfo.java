package com.example.nfcmuseum;

import java.io.Serializable;
import java.util.List;

public class ExhibitInfo implements Serializable {
    private String description;
    private String history;

    public ExhibitInfo(String description, String history) {
        this.description = description;
        this.history = history;
    }

    public String getDescriptionInfoCard() {return description;}

    public String getHistoryInfoCard() {return history;}

}