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
   
//   @Override
//   public void setWidth(float width, Unit unit) {
//       System.out.println("setWidth() called");
//       System.out.println("width: " + width + ", unit: " + unit);
//   }
   
    public void setUp(JsonObject setupData) {
         getState().setSetupData(setupData);
    }
   
    public JsonObject getSetupData() {
         return getState().getSetupData();
    }
   
    public void sendData(JsonObject data) {
        version(data, "data");
        getState().setData(data);
    }
    public void sendOptions(JsonObject options) {
        version(options, "options");
        getState().setOptions(options);
    }
    public void sendData(Map data) {
         getState().setMapData(data);
    }
    

    public void resize() {
        JsonObject resizeDummyObject = Json.createObject();
        version(resizeDummyObject, "resize");
        getState().resize(resizeDummyObject);
    }
   
    @Override
    public JsonObject getData() {
        return getState().getData();
    }
   
   public boolean allNull(List list) {
       for (Object element : list) {
           if (element != null) {
               return false;
           }
       }
       return true;
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