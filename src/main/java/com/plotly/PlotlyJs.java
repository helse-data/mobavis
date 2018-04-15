package com.plotly;

import com.vaadin.annotations.JavaScript;
import com.vaadin.ui.AbstractJavaScriptComponent;
import elemental.json.JsonObject;
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
    boolean booleanSizeVersion = initialBooleanVersion;
    
   public PlotlyJs() {
       //setSizeUndefined();
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
         getState().setData(data);
    }
    public void sendData(Map data) {
         getState().setMapData(data);
    }
    
    public void setSize(JsonObject size) {
        size.put("boolean version", booleanSizeVersion);
        booleanSizeVersion = !booleanSizeVersion;
        getState().setSize(size);
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
}