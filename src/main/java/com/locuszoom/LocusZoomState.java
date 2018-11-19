package com.locuszoom;

import com.vaadin.shared.ui.JavaScriptComponentState;
import elemental.json.JsonArray;
import elemental.json.JsonObject;

public class LocusZoomState extends JavaScriptComponentState {
    private JsonObject region;
    private String clickedSNP;

    /**
     * 
     * Get the SNP that was clicked.
     * 
     * @return 
     */
    public String getClickedSNP() {
        return clickedSNP;
    }

    /**
     * 
     * Stores the SNP that was clicked.
     * 
     * @param clickedSNP 
     */
    public void setClickedSNP(String clickedSNP) {
        this.clickedSNP = clickedSNP;
        //setRegion(region);
    }
    
    /**
     * Sets the region for LocusZoom.js.
     * 
     * @param region 
     */
    public void setRegion(JsonObject region) {
        this.region = region;
        System.out.println("Region: " + region);
    }
    
    /**
     * 
     * @return 
     */
    public JsonObject getRegion() {
        return region;
    }    
}