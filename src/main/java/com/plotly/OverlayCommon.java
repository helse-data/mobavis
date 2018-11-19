package com.plotly;

import elemental.json.JsonObject;

/**
 * Implements a Vaadin component for a plot that allows the user to overlay data.
 * 
 * @author Christoffer Hjeltnes Støle
 */
public class OverlayCommon extends PlotlyJs {
    boolean booleanPercentileVersion = initialBooleanVersion; // use a boolean version to avoid unnecessary counting
    boolean booleanUserDataVersion = initialBooleanVersion;
    boolean booleanUserAgeVersion = initialBooleanVersion;
    boolean booleanMetaVersion = initialBooleanVersion;
    
    Boolean showStatus;
    String phenotypeID;     
    
    /**
     * 
     * @param phenotypeID 
     */
    public OverlayCommon(String phenotypeID) {
        this.phenotypeID = phenotypeID;
    }
    
    /**
     * Sets the percentile data in the state object.
     * 
     * @param data
     * @param booleanVersion 
     */
    public void sendPercentileData(JsonObject data, Boolean booleanVersion) {
        if (booleanVersion != null) {
            booleanPercentileVersion = booleanVersion;
        }
        data.put("boolean version", booleanPercentileVersion);
        //System.out.println("Sent boolean version: " + booleanPercentileVersion);
        booleanPercentileVersion = !booleanPercentileVersion;
        //System.out.println("sendPercentileData(), data: " + data);
        getState().setPercentileData(data);
    }
    /**
     * 
     * @param data 
     */
    public void sendPercentileData(JsonObject data) {
        sendPercentileData(data, null);
    }
    
    /**
     * Returns the percentile data currently plotted.
     * 
     * @return 
     */
    public JsonObject getPercentileData() {
        return getState().getPercentileData();
    }
    
    /**
     * Sets the user data in the state object.
     * 
     * @param userData
     * @param booleanVersion 
     */
    public void sendUserData(JsonObject userData, Boolean booleanVersion) {    
        //System.out.println("sending: " + userData.toJson());
        if (booleanVersion != null) {
            booleanUserDataVersion = booleanVersion;
        }
        userData.put("boolean version", booleanUserDataVersion);
        if (phenotypeID != null) {
                userData.put("phenotype ID", phenotypeID);
        }
        //System.out.println("Sent boolean version: " + booleanUserDataVersion);
        booleanUserDataVersion = !booleanUserDataVersion;
        //System.out.println("setting userData: " + phenotypeID);
        getState().setUserData(userData);
    }
    /**
     * 
     * @param userData 
     */
    public void sendUserData(JsonObject userData) {
        sendUserData(userData, null);
    }
    /**
     * Returns the user data currently plotted.
     * 
     * @return 
     */
    public JsonObject getUserData() {
        return getState().getUserData();
    }
    
    /**
     * 
     * Sets the user-provided ages in the state object.
     * 
     * @param userAges 
     */
    public void sendUserAges(JsonObject userAges) {
        userAges.put("boolean version", booleanUserAgeVersion);
        booleanUserAgeVersion = !booleanUserAgeVersion;
        getState().setUserAges(userAges);
    }
    
    /**
     * Sets the meta data in the state object.
     * 
     * @param data
     * @param booleanVersion 
     */
    public void sendMetaData(JsonObject data, Boolean booleanVersion) {
        if (booleanVersion != null) {
            booleanMetaVersion = booleanVersion;
        }
        data.put("boolean version", booleanMetaVersion);
        booleanMetaVersion = !booleanMetaVersion;
        //System.out.println("setting metaData: " + phenotypeID);
        getState().setMetaData(data);
    }
    
    /**
     * 
     * @param data 
     */
    public void sendMetaData(JsonObject data) {
        sendMetaData(data, null);
    }

    /**
     * Returns the meta data stored in the state object.
     * 
     * @return 
     */
    public JsonObject getMetaData() {
        return getState().getMetaData();
    }

    /**
     * 
     * Returns the initial boolean version.
     * 
     * @return 
     */
    public boolean getInitialBooleanVersion() {
        return initialBooleanVersion;
    }
    /**
     * Sets the status in the state object.
     * 
     * @param show 
     */
    public void sendShowStatus(Boolean show) {
        //System.out.println("setting showStatus: " + phenotypeID);
        getState().setShowStatus(show);
    }
    
    /**
     * Gets the show status stored in the state object.
     * 
     * @return 
     */
    public Boolean getShowStatus() {
        return getState().getShowStatus();
    }

    @Override
    public OverlayCommonState getState() {
        return (OverlayCommonState) super.getState();
    }
}