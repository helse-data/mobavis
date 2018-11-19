package com.plotly;

import com.vaadin.annotations.JavaScript;
import com.vaadin.ui.AbstractJavaScriptComponent;
import elemental.json.Json;
import elemental.json.JsonObject;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 
 * Implements a Vaadin component for plotly.js plots.
 *
 * @author Christoffer Hjeltnes Støle
 */
@JavaScript({
    "vaadin://plotly/plotlyPlot.js",
    "vaadin://plotly/connector.js",
    "vaadin://plotly/plotly-latest.min.js"
    //"https://cdn.plot.ly/plotly-latest.min.js"
})
public class PlotlyJs extends AbstractJavaScriptComponent {
    boolean initialBooleanVersion = false;
    Map <String, Boolean> booleanVersionsMap = new HashMap();
    
   /**
    * 
    * @param setup - setup data
    */
   public PlotlyJs(JsonObject setup) {
       if (setup != null) {
           getState().setSetup(setup);
       }
       String [] versionedVariables = {"data", "options", "resize"};
       for (String variable : versionedVariables) {
           booleanVersionsMap.put(variable, initialBooleanVersion);
       }
   }
   
   public PlotlyJs() {
       this(null);   
   }
   

   /**
    * Sets the setup data in the state object.
    * 
    * @param setupData 
    */
    public void setUp(JsonObject setupData) {
         getState().setSetupData(setupData);
    }
   
    /**
     * Returns the setup data stored in the state object.
     * 
     * @return 
     */
    public JsonObject getSetupData() {
         return getState().getSetupData();
    }
   
    /**
     * Sets the data to plot in the state object.
     * 
     * @param data 
     */
    public void sendData(JsonObject data) {
        version(data, "data");
        getState().setData(data);
    }
    /**
     * Sets the  options for the plot in the state object.
     * 
     * @param data 
     */
    public void sendOptions(JsonObject options) {
        version(options, "options");
        getState().setOptions(options);
    }
    
    /**
     * Request a resizing of the plot.
     */
    public void resize() {
        JsonObject resizeDummyObject = Json.createObject();
        version(resizeDummyObject, "resize");
        getState().resize(resizeDummyObject);
    }
   
    @Override
    public JsonObject getData() {
        return getState().getData();
    }
    
    @Override
    public PlotlyJsState getState() {
        return (PlotlyJsState) super.getState();
    }
    
    /**
     * Manage the versioning of the JSON objects sent to the JavaScript code.
     * 
     * @param data - the data object that needs a version
     * @param variable - name of the data
     */
    private void version(JsonObject data, String variable) {
        data.put("boolean version", booleanVersionsMap.get(variable));
        booleanVersionsMap.put(variable, !booleanVersionsMap.get(variable));
    }
    
}