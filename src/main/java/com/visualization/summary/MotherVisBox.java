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
import com.visualization.State;
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
    NativeSelect phenotypeSelector = new NativeSelect("Phenotype");
    Label plotInfo;

    public MotherVisBox(Controller controller) {
        super(controller);
        
        String [] tables = extractor.getTables();        
        phenotypeSelector.setItems(Arrays.asList(tables));
        phenotypeSelector.addValueChangeListener(event -> selectPhenotype(event));
        phenotypeSelector.setEmptySelectionAllowed(false);
        
        plotInfo = new Label("", ContentMode.HTML);
        plotInfo.setSizeUndefined();
        
        box.addComponent(phenotypeSelector);
        box.addComponent(plotInfo);
        box.setComponentAlignment(plotInfo, Alignment.MIDDLE_CENTER);       
        plotBox.setSizeFull();
        box.addComponent(plotBox);
        box.setExpandRatio(phenotypeSelector, 1);
        box.setExpandRatio(plotBox, 7);  
        
        phenotypeSelector.setValue("multiple_birth");
    }
        
    private void selectPhenotype(HasValue.ValueChangeEvent event) {
        String selectedPhenotype = (String) event.getValue();
        
        plotBox.removeAllComponents();
        
        //int [] tableData = extractor.getTableData(selectedPhenotype);
        Table table = extractor.getTable(selectedPhenotype);
        int [] tableData = table.getData();
        List <String> tableLabels = table.getLabels();
        
        
        System.out.println("data for this phenotype: " + Arrays.toString(table.getData()));
        System.out.println("Labels for this phenotype: " + extractor.getTableLabels(selectedPhenotype));
        
        //List <String> tableLabels = extractor.getTableLabels(selectedPhenotype);
        
        plotInfo.setValue(htmlHelper.bold(selectedPhenotype.replace("_", " ")));
        
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
    
}
