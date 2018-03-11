package com.plotting;

import com.plotly.ParameterisedPlot;
import com.utils.JsonHelper;
import com.vaadin.ui.Component;
import elemental.json.Json;
import elemental.json.JsonArray;
import elemental.json.JsonObject;
import java.util.List;

/**
 *
 * @author Christoffer Hjeltnes Støle
 */
public class ParameterisedPlotComponent {
    ParameterisedPlot plot;
    JsonHelper jsonHelper = new JsonHelper();  
    JsonArray phenotypeArray = Json.createArray();
    
    public ParameterisedPlotComponent() {
        plot = new ParameterisedPlot();
    }
    
    public void setUserData(List <String> data1, List <String> data2) {
        JsonObject dataObject = Json.createObject(); // create a new object each time 
        
        jsonHelper.put(dataObject, "data 1", data1);
        jsonHelper.put(dataObject, "data 2", data2);
        plot.sendUserData(dataObject);
    }
    
    public void setPhenotype(String[] phenotypes, List <List <String>> percentileDataList1, List <List <String>> percentileDataList2) {
        JsonObject percentileData = Json.createObject(); 
        createPhenotypeArray(phenotypes);
        
        percentileData.put("phenotypes", phenotypeArray);
        jsonHelper.putDoubleList(percentileData, "data 1", percentileDataList1);
        jsonHelper.putDoubleList(percentileData, "data 2", percentileDataList2);
        
        plot.sendPercentileData(percentileData);
    }
    
    private void createPhenotypeArray(String[] phenotypes) {
        phenotypeArray = Json.createArray();
        phenotypeArray.set(0, phenotypes[0]);
        phenotypeArray.set(1, phenotypes[1]);
    }
    
    public void show(String statistic, boolean show) {
        plot.sendShowStatus(show);     
    }
   
    public Component getComponent() {
        return plot;
    }
    
}
