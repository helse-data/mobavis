package com.plotly;

import elemental.json.JsonObject;


public class OverlayCommon extends PlotlyJs {
    boolean booleanPercentileVersion = initialBooleanVersion; // use a boolean version to avoid unnecessary counting
    boolean booleanUserDataVersion = initialBooleanVersion;
    boolean booleanUserAgeVersion = initialBooleanVersion;
    boolean booleanMetaVersion = initialBooleanVersion;
    
    Boolean showStatus;
    String phenotypeID;     
    
    public OverlayCommon(String phenotypeID) {
        this.phenotypeID = phenotypeID;
    }
    
    public void sendPercentileData(JsonObject data, Boolean booleanVersion) {
        if (booleanVersion != null) {
            booleanPercentileVersion = booleanVersion;
        }
        data.put("boolean version", booleanPercentileVersion);
        //System.out.println("Sent boolean version: " + booleanPercentileVersion);
        booleanPercentileVersion = !booleanPercentileVersion;
        System.out.println("sendPercentileData(), data: " + data);
        getState().setPercentileData(data);
    }
    
    public void sendPercentileData(JsonObject data) {
        sendPercentileData(data, null);
    }

    public JsonObject getPercentileData() {
        return getState().getPercentileData();
    }
    
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
    
    public void sendUserData(JsonObject userData) {
        sendUserData(userData, null);
    }
    
    public JsonObject getUserData() {
        return getState().getUserData();
    }
    
    public void sendUserAges(JsonObject userAges) {
        userAges.put("boolean version", booleanUserAgeVersion);
        booleanUserAgeVersion = !booleanUserAgeVersion;
        getState().setUserAges(userAges);
    }
    
    public void sendMetaData(JsonObject data, Boolean booleanVersion) {
        if (booleanVersion != null) {
            booleanMetaVersion = booleanVersion;
        }
        data.put("boolean version", booleanMetaVersion);
        booleanMetaVersion = !booleanMetaVersion;
        //System.out.println("setting metaData: " + phenotypeID);
        getState().setMetaData(data);
    }
    
    public void sendMetaData(JsonObject data) {
        sendMetaData(data, null);
    }

    public JsonObject getMetaData() {
        return getState().getMetaData();
    }

    public boolean getInitialBooleanVersion() {
        return initialBooleanVersion;
    }
    
    public void sendShowStatus(Boolean show) {
        //System.out.println("setting showStatus: " + phenotypeID);
        getState().setShowStatus(show);
    }

    public Boolean getShowStatus() {
        return getState().getShowStatus();
    }

    @Override
    public OverlayCommonState getState() {
        return (OverlayCommonState) super.getState();
    }
}