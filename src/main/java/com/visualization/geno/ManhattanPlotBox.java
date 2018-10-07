package com.visualization.geno;

import com.files.PValueReader;
import com.locuszoom.LocusZoom;
import com.database.Database;
import com.main.Main;
import com.snp.SNP;
import com.plotly.ManhattanPlot;
import com.utils.VaadinUtils;
import com.vaadin.event.selection.SelectionEvent;
import com.vaadin.server.Sizeable;
import com.vaadin.shared.ui.ContentMode;
import com.vaadin.ui.AbstractComponent;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.CheckBoxGroup;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.RadioButtonGroup;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.main.Controller;
import com.main.Controller.Visualization;
import com.vaadin.ui.themes.ValoTheme;
import elemental.json.Json;
import elemental.json.JsonObject;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author Christoffer Hjeltnes Støle
 */
public class ManhattanPlotBox extends GenoView {
    //Controller controller;
    Window window;
    VerticalLayout box = new VerticalLayout();
    HorizontalLayout plotContainer = new HorizontalLayout();
    ManhattanPlot manhattanPlot;
    Database database = new Database();
    Window plotOptionsWindow;
    Set <String> thresholdSet;
    CheckBoxGroup <String> tresholdSelector;
    Set <String> previousOptions = new HashSet();
    VaadinUtils vaadinUtils = new VaadinUtils();
    
    Map <String, String> SNPInformation = new HashMap();
    
    //public ManhattanPlotBox (Main main) {
    public ManhattanPlotBox (Controller controller) {
        super(controller);

        box.addComponent(getController().getSNPInputField());
        
        PValueReader pValueReader = new PValueReader();
        JsonObject pValueDataObject = pValueReader.read();
        manhattanPlot = new ManhattanPlot();
        manhattanPlot.sendData(pValueDataObject);
        manhattanPlot.setSizeFull();
        
        manhattanPlot.addValueChangeListener(new ManhattanPlot.ValueChangeListener() {
                @Override
                public void valueChange() {
                    //SNPclicked();
                    String clickedSNP = manhattanPlot.getClickedSNP();
                    System.out.println("Data received in view box: " + clickedSNP);
                    SNPclicked(clickedSNP);
                }
            });
        
        Button loadManhattanPlotButton = new Button("Load Manhattan plot.");
        loadManhattanPlotButton.addStyleName(ValoTheme.BUTTON_LINK);
        loadManhattanPlotButton.setSizeUndefined();
        loadManhattanPlotButton.addClickListener(event -> loadManhattanPlot());
                
        plotContainer.addComponent(loadManhattanPlotButton);
        plotContainer.setComponentAlignment(loadManhattanPlotButton, Alignment.MIDDLE_CENTER);
        plotContainer.setSizeFull();
        
        // options
        Button optionsButton = new Button("Options");
        optionsButton.addClickListener(event -> openOptionsWindow());
        
        box.addComponent(optionsButton);
        box.setComponentAlignment(optionsButton, Alignment.TOP_CENTER);
        box.setExpandRatio(optionsButton, 1);
        box.addComponent(plotContainer);
        box.setExpandRatio(plotContainer, 26);
        box.setSizeFull();
        
    }
    
    private void SNPclicked(String data) {        
        window = new Window("SNP clicked");

        String [] splitData = data.split("<br>");
        String SNPname = splitData[0].replace("SNP: ", "");
        
        if (SNPname.equals("-")) {
            SNPname = "N/A";
        }
        
        String chromosome = splitData[2].replace("chromosome: ", "");
        String position = splitData[3].replace("position: ", "");
        
        SNPInformation.put("ID", SNPname);
        SNPInformation.put("chromosome", chromosome);
        SNPInformation.put("position", position);
        
        Label description = new Label(
                "ID: \t\t" + SNPname + 
                        "\nChromosome: \t" + chromosome + 
                        "\nPosition: \t" + position, ContentMode.PREFORMATTED);        
        
        Button locusZoomButton = new Button("LocusZoom plot");
        locusZoomButton.addClickListener(event -> goToVisualization(Visualization.LOCUS_ZOOM));
        Button variantButton = new Button("Variant plot");
        variantButton.addClickListener(event -> goToVisualization(Controller.Visualization.VARIANT_PLOT));
        HorizontalLayout buttonBox = new HorizontalLayout();
        buttonBox.addComponent(locusZoomButton);
        buttonBox.addComponent(variantButton);
        
        VerticalLayout windowContent = new VerticalLayout();
        windowContent.addComponent(description);
        windowContent.addComponent(buttonBox);
        
        window.center();
        window.setResizable(false);
        window.setContent(windowContent);
        window.setWidth(310, Sizeable.Unit.PIXELS);
        window.setHeight(220, Sizeable.Unit.PIXELS);
        getComponent().getUI().addWindow(window);
    }
    
    
    private void goToVisualization(Visualization visualization) {
        //String query = "";
        //if (SNPInformation.get("ID").equals("N/A") || option.equals(Controller.Visualization.LOCUS_ZOOM)) {
        //    query  = SNPInformation.get("chromosome") + ":" + SNPInformation.get("position");
        //}
        //else {
        //    query = SNPInformation.get("ID");
        //}
        //System.out.println("query: " + query);
        //main.setViewWithSNP(visualization, query);        
        //System.out.println("Selected option for SNP click: " + visualization);
        window.close();
        getController().setVisualization(visualization);
        
    }
    
    private void openOptionsWindow() {
        if (plotOptionsWindow == null) {            
            
            // thresholds
            //CheckBox suggestiveThreshold = new CheckBox("Show ", false);
            
            tresholdSelector = new CheckBoxGroup("Show thresholds");
            
            thresholdSet = new HashSet (Arrays.asList(new String [] {"suggestive genome-wide significance threshold",
            "genome-wide significance threshold"}));

            tresholdSelector.setItems(thresholdSet);
            tresholdSelector.setValue(thresholdSet);
            tresholdSelector.addSelectionListener(event -> setOptions(event));

            
            //suggestiveThreshold.addValueChangeListener(event -> changeYaxisToZero(event));
            
            
            VerticalLayout content = new VerticalLayout();
            content.addComponent(tresholdSelector);
            plotOptionsWindow = new Window("Options for Manhattan plot", content);
            plotOptionsWindow.setWidth(30, Sizeable.Unit.PERCENTAGE);
            plotOptionsWindow.setHeight(70, Sizeable.Unit.PERCENTAGE);
            plotOptionsWindow.center();
        }
        toggleWindowVisibility(plotOptionsWindow);
        
        //getComponent().getUI().getUI().addWindow(plotOptionsWindow);
    }
    
    private void toggleWindowVisibility(Window window) {
        if (!window.isAttached()) { // is the window already open?
            getComponent().getUI().addWindow(window);
        }
        else{
            window.close();
        }
    }
    
    private void setOptions(SelectionEvent event) {
        Set <String> currentlySelected = event.getAllSelectedItems();
        Set <String> changed = vaadinUtils.getChangedOptions(currentlySelected, previousOptions);
        
        JsonObject optionsObject = Json.createObject();
        for (String option : thresholdSet) {
            optionsObject.put(option, currentlySelected.contains(option));
        }
        
        System.out.println("options: " + optionsObject.toJson());

        manhattanPlot.sendOptions(optionsObject);
        
        previousOptions = tresholdSelector.getValue();
    }
    
    private void loadManhattanPlot() {
        plotContainer.removeAllComponents();
        plotContainer.addComponent(manhattanPlot);
    }
    
    @Override
    public AbstractComponent getComponent() {
        return box;
    }
    
    @Override
    public void SNPChanged() {
        // TODO: implement
    } 

    @Override
    public void resizePlots() {
        manhattanPlot.resize();
    }
    
}
