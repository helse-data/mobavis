package com.visualization.summary;

import com.main.Controller;
import com.mobaextraction.Extractor;
import com.mobaextraction.Table;
import com.plotly.BarPlot;
import com.utils.HtmlHelper;
import com.utils.JsonHelper;
import com.vaadin.data.HasValue;
import com.vaadin.shared.ui.ContentMode;
import com.vaadin.ui.AbstractComponent;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.NativeSelect;
import com.vaadin.ui.VerticalLayout;
import com.visualization.MoBaVisualization;
import elemental.json.Json;
import elemental.json.JsonArray;
import elemental.json.JsonObject;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 *
 * @author Christoffer Hjeltnes Støle
 */
public class MotherVisBox extends MoBaVisualization {
    VerticalLayout box = new VerticalLayout();
    JsonHelper jsonHelper = new JsonHelper();
    HtmlHelper htmlHelper = new HtmlHelper();
    Extractor extractor = new Extractor();
    //BarPlot chart;
    HorizontalLayout plotBox = new HorizontalLayout();
    NativeSelect variableSelector = new NativeSelect("Variable");
    Label plotInfo;

    public MotherVisBox(Controller controller) {
        super(controller);
        
        String [] tables = extractor.getTables();        
        variableSelector.setItems(Arrays.asList(tables));
        variableSelector.addValueChangeListener(event -> selectVariable(event));
        variableSelector.setEmptySelectionAllowed(false);
        
        plotInfo = new Label("", ContentMode.HTML);
        plotInfo.setSizeUndefined();
        
        box.addComponent(variableSelector);
        box.addComponent(plotInfo);
        box.setComponentAlignment(plotInfo, Alignment.MIDDLE_CENTER);       
        plotBox.setSizeFull();
        box.addComponent(plotBox);
        box.setExpandRatio(variableSelector, 1);
        box.setExpandRatio(plotBox, 7);  
        
        variableSelector.setValue("multiple_birth");
    }
        
    private void selectVariable(HasValue.ValueChangeEvent event) {
        String selectedVariable = (String) event.getValue();
        
        System.out.println("Selected variable: " + selectedVariable);
        
        plotBox.removeAllComponents();
        
        //int [] tableData = extractor.getTableData(selectedPhenotype);
        Table table = extractor.getTable(selectedVariable);
        int [] tableData = table.getData();
        List <String> tableLabels = table.getLabels();
        
        
        System.out.println("data for this variable: " + Arrays.toString(table.getData()));
        System.out.println("Labels for this variable: " + extractor.getTableLabels(selectedVariable));
        
        //List <String> tableLabels = extractor.getTableLabels(selectedVariable);
        
        plotInfo.setValue(htmlHelper.bold(selectedVariable.replace("_", " ")));
        
        if (table.isStratifiedBySex()) {            
            for (String sex : new String [] {"female", "male"}) {
                
                List <String> labels = table.getLabels(sex);
                List <Integer> data = table.getData(sex);
                
                JsonObject setupData = Json.createObject();
                jsonHelper.put(setupData, "x", labels);
                setupData.put("title",sex + "s");
                setupData.put("x-axis", "case");
                setupData.put("y-axis", "number of cases");

                JsonArray yDataArray = Json.createArray();

                for (int i = 0; i < labels.size(); i++) {
                    yDataArray.set(i, data.get(i));
                }

                setupData.put("y", yDataArray);

                BarPlot chart = new BarPlot();
                chart.setUp(setupData);
                chart.setSizeFull();
                plotBox.addComponent(chart);
            }          
        }
        else {
            JsonObject setupData = Json.createObject();
            jsonHelper.put(setupData, "x", tableLabels);
            setupData.put("x-axis", "case");
            setupData.put("y-axis", "number of cases");

            JsonArray yDataArray = Json.createArray();

            for (int i = 0; i < tableLabels.size(); i++) {
                yDataArray.set(i, tableData[i]);
            }

            setupData.put("y", yDataArray);

            BarPlot chart = new BarPlot();
            chart.setUp(setupData);
            chart.setSizeFull();
            plotBox.addComponent(chart);
        }      
    }
    
    @Override
    public AbstractComponent getComponent() {
        return box;
    }

    @Override
    public void resizePlots() {
        
    }

    @Override
    public void handOver() {
    }
    
}
