package com.plotly;

import elemental.json.JsonObject;

/**
 * 
 * State class for the plot that allows the user to overlay data.
 * 
 * @author Christoffer Hjeltnes Støle
 * 
 */
public class OverlayCommonState extends PlotlyJsState {
    
    private JsonObject percentileData;
    private JsonObject userData;
    private JsonObject userAges;
    private JsonObject metaData;
    private Boolean showStatus;
    
    public void setPercentileData(JsonObject data) {
        //System.out.println("percentileData set");
        percentileData = data;
    }
    
    public JsonObject getPercentileData() {
        return percentileData;
    }
    
    public void setUserData(JsonObject data) {
        //System.out.println("userData set");
        userData = data;
    }
    
    public JsonObject getUserData() {
        return userData;
    }
    
    public void setUserAges(JsonObject data) {
        userAges = data;
    }
    
    public JsonObject getUserAges() {
        return userAges;
    }
    
    public void setMetaData(JsonObject data) {
        metaData = data;
    }
    
    public JsonObject getMetaData() {
        return metaData;
    }
    
    public void setShowStatus(Boolean show) {
        //System.out.println("showStatus set");
        showStatus = show;
    }
    
    public Boolean getShowStatus() {
        return showStatus;
    }
    
}
