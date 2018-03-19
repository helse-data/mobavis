package com.plotting;

import com.locuszoom.LocusZoom;
import com.plotly.SNPPlot;
import com.main.Database;
import com.main.SNP;
import com.utils.Alphanumerical;
import com.utils.Constants;
import com.utils.UtilFunctions;
import com.utils.HtmlHelper;
import com.utils.JsonHelper;
import com.utils.Option;
import com.vaadin.event.selection.SelectionEvent;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.server.Sizeable;
import com.vaadin.shared.ui.ContentMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.CheckBoxGroup;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.NativeSelect;
import com.vaadin.ui.RadioButtonGroup;
import com.vaadin.ui.Window;
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
public class SNPPlotBox {
    HtmlHelper html = new HtmlHelper();
    
    JsonObject dataObject;
    JsonHelper jsonHelper = new JsonHelper();
    JsonObject showStatusObject;
        
    GridLayout box = new GridLayout(100, 100);
    HorizontalLayout plotBox = new HorizontalLayout();
    
    SNPPlot femaleChart;
    SNPPlot maleChart;
    PlotDataWindow plotDataWindow = new PlotDataWindow();
    GridLayout SNPRightGrid = new GridLayout(10, 4);
    HorizontalLayout SNPShowOptionsSelectorBox = new HorizontalLayout();
    HorizontalLayout topBox = new HorizontalLayout();
    CheckBoxGroup <Option> SNPShowOptionsSelector;
    Set <Option> previousSNPShowOptions = new HashSet();
    Label message;
    Label SNPInformation = new Label();
    
    List <String> phenotypeOptions = new ArrayList();
    
    String currentPhenotype;
    SNP currentSNP;
    
    List <String> SNPOptions;
    
    ComboBox <String> SNPInput;
    String currentSNPInputValue;
    boolean SNPInputActive = false;
    NativeSelect <String> phenotypeSelector;
    
    Button locusZoomButton = new Button("Show LocusZoom plot");
    Window locusZoomWindow;
    LocusZoom locusZoom;
    
    Database database = new Database();
    
    UtilFunctions converter = new UtilFunctions();
    Constants constants = new Constants();
    
    boolean mediansShown = true;
    boolean SEMShown = true;
    boolean percentilesShown = false;
    
    
    public SNPPlotBox() {        
        femaleChart = new SNPPlot();
        maleChart = new SNPPlot();
        
        plotBox.addComponent(femaleChart);
        plotBox.addComponent(maleChart);
        
        int n10 = (int) ((box.getColumns()-1)*0.1);
        
        int plotStartY = 7;
        box.addComponent(topBox, 1, 0, 70, plotStartY-1);
        box.addComponent(plotBox, 1, plotStartY, 82, 99);
        box.addComponent(SNPRightGrid, 83, plotStartY+1, 99, 99);
        
        // phenotype selector
        phenotypeOptions.addAll(Arrays.asList(new String[]{
            "height", "weight", "BMI"}));
        phenotypeSelector = new NativeSelect("Phenotype");
        phenotypeSelector.setItems(phenotypeOptions);        
        phenotypeSelector.addValueChangeListener(event -> selectPhenotype(String.valueOf(
                event.getValue())));  
        phenotypeSelector.setIcon(VaadinIcons.CLIPBOARD_PULSE);
        phenotypeSelector.setEmptySelectionAllowed(false);
        
        locusZoomButton.addClickListener(event -> openLocusZoomWindow());
        
        Button viewPlotDataButton = new Button("View plot data");
        viewPlotDataButton.addClickListener(event -> viewPlotData());

        // changeShowStatus options
        List <Option> SNPShowOptions = new ArrayList();
        Option medians = new Option("medians", "medians", true);
        Option SEM = new Option("SEM", "SEM", true);
        Option percentiles = new Option("percentiles", "2.5th and 97.5th percentiles", false);
        SNPShowOptions.add(medians);
        SNPShowOptions.add(SEM);
        SNPShowOptions.add(percentiles);
        SNPShowOptionsSelector = new CheckBoxGroup("Show");
        SNPShowOptionsSelector.setItems(SNPShowOptions);

        
        SNPInput = new ComboBox("SNP");

        SNPInput.addFocusListener(event -> clearSNPInput(true));
        SNPInput.addBlurListener(event -> clearSNPInput(false));
        
//         if (userVersion){
//             SNPOptions = new ArrayList(Arrays.asList(new String[]{
//            "rs9996", "21_10915988_A_C"}));
//        }
//        else {
            SNPOptions = new ArrayList(Arrays.asList(new String[]{"rs13046557", "rs775977022", "rs553763040", 
            "rs9996", "rs2767486 [big difference]", "rs117845375 [female plunge]", "rs41301756 [female plunge x2]", "21_10915988_A_C", "rs12627379", "rs28720096 [AA-only]", "rs62033413", "rs375583050 [BB-only]",
        "rs147446959", "1_154729900_T_G [large chromosome]"}));
                        
        //}
        
        // rs72970193 (good spread between genotypes)
        // rs17649232 (female below, male above)
        // rs16861872 (male below, female above)
        SNPInput.setItems(SNPOptions);        
        SNPInput.addValueChangeListener(event -> searchSNP(String.valueOf(
                event.getValue())));
        SNPInput.setNewItemHandler(inputString -> addSNP(inputString));
        SNPInput.setIcon(VaadinIcons.CUBES);
        SNPInput.setEmptySelectionAllowed(false);
        
        currentPhenotype = "BMI";
        phenotypeSelector.setValue(currentPhenotype);
        
        SNPInput.setValue(SNPOptions.get(0));  
        
        SNPShowOptionsSelector.select(medians, SEM);
        previousSNPShowOptions = SNPShowOptionsSelector.getValue();
        SNPShowOptionsSelector.addSelectionListener(event -> changeSNPShowSettings(event));
        
        SNPShowOptionsSelectorBox.addComponent(SNPShowOptionsSelector);
        SNPRightGrid.addComponent(SNPShowOptionsSelectorBox, 1, 0, 9, 0);
        
        topBox.addComponent(SNPInput);
        topBox.addComponent(phenotypeSelector);
        topBox.addComponent(locusZoomButton);
        topBox.setComponentAlignment(locusZoomButton, Alignment.BOTTOM_CENTER);
        topBox.addComponent(viewPlotDataButton);
        topBox.setComponentAlignment(viewPlotDataButton, Alignment.BOTTOM_CENTER);
        
        box.setSizeFull();
        topBox.setSizeFull();
        plotBox.setSizeFull();
        femaleChart.setSizeFull();
        maleChart.setSizeFull();
        SNPRightGrid.setSizeFull();
        SNPShowOptionsSelectorBox.setSizeFull();        
        SNPInput.setSizeFull();
        phenotypeSelector.setSizeFull();
        
    }
       
    public void setDatasets(SNP snp, String phenotype) {
        for (String sex : new String[] {"female", "male"}) {
            dataObject = Json.createObject();
            
            dataObject.put("sex", sex);
            dataObject.put("SNP ID", snp.getID());
            dataObject.put("phenotype", phenotype);            
                        
            int overallMaxN = 0;
            SNPPlot chart = null;
            String indexString = "";
            if (sex.equals("female")) {
                indexString = "1";
                chart = femaleChart;                
            }
            else if (sex.equals("male")) {
                chart = maleChart;
                indexString = "2";
            }
            
            for (String genotype : new String[] {"AA", "AB", "BB"}) { 
                JsonObject snpDataObject = snp.getDataObject();
                System.out.println("phenotype: " + phenotype);
                //System.out.println("phenotype: " + phenotype + ", object: " + snpDataObject.getObject(phenotype).getObject(sex));
                JsonObject genotypeObject = snpDataObject.getObject(phenotype).getObject(sex).getObject(genotype);
                
                if (snpDataObject.getObject(phenotype).getBoolean("longitudinal")) {
                    List <String> nData = converter.jsonArrayToList(genotypeObject.get("N"));
                    String nMin = converter.minInteger(nData);
                    String nMax = converter.maxInteger(nData);

                    String info  = "";

                    if (!nMax.equals("<5")) {
                        int currMax = Integer.parseInt(nMax);
                        if (currMax > overallMaxN) {
                            overallMaxN = currMax;    
                        }                    
                    }                
                    String separator = "";
                    if (nMin.equals("0") && nMax.equals("0")) {
                        info = "(no individuals)";
                        separator = " ";
                    }
                    else if (nMin.equals("<5") && nMax.equals("<5")) {
                        info = "(less than 5 individuals)";
                        separator = " ";
                    }
                    else {
                        info = "n ∈ [" + nMin + ", " + nMax + "]";
                        separator = ", ";
                    }
                    jsonHelper.put(genotypeObject, "labels", Arrays.asList(new String[] {genotype + separator + info}));
                }
                dataObject.put(genotype, genotypeObject);    
                
            }
            chart.sendData(dataObject);
            plotDataWindow.setTab(indexString, dataObject.toJson(), sex);
        }        
    }
    
    private boolean searchSNP(String option) {
        if (SNPInputActive) {
            return false;
        }
        //viewSelector.setEnabled(false); // TODO: check effects
        SNPInputActive = true;
        //SNPInformation.setCaption("");
        System.out.println("searchSNP(): " + option);
        //System.out.println(currentSNP == null);
        SNPInput.setValue(option.replaceFirst(" \\[your input\\]$", ""));
        option = option.replaceFirst(" \\[.*?\\]$", "");
        
        if (option.equals("null") || option.equals("") || (currentSNP != null && option.equals(currentSNP.getID()))
                || option.contains("(not found)")) {
            SNPInputActive = false;
            //SNPRightGrid.removeComponent(SNPInformation);
            //SNPInformation = new Label("");
            //SNPRightGrid.addComponent(SNPInformation);
             //viewSelector.setEnabled(true);// TODO: check effects
            return false;
        }
        
       
        phenotypeSelector.setEnabled(false);
        //System.out.println("disabled");
        //Notification.changeShowStatus("disabled", Notification.Type.TRAY_NOTIFICATION);
        currentSNPInputValue = option;
        
        SNP snp = null;
        if (option.contains(":")) {
            String [] split = option.split(":");
            String chromosome = split[0];
            String position = split[1];
            List <String> resultList = database.getNearestSNPs(chromosome, Integer.parseInt(position));
            System.out.println("resultList: " + resultList);
            
            List <String> snpOptions = new ArrayList();
            for (String snpID : resultList) {
                snpOptions.add(snpID);
            }
            
            RadioButtonGroup <String> selection = new RadioButtonGroup("Select SNP", snpOptions);
            selection.addValueChangeListener(event -> searchSNP(event.getValue()));
            String windowCaption = "SNPs nearest to position " + position + " on chromosome " + chromosome + ":";
            Window window = new Window(windowCaption);
            window.center();
            window.setContent(selection);
            window.setWidth(Math.round(windowCaption.length()*9.7), Sizeable.Unit.PIXELS);
            window.setHeight(10, Sizeable.Unit.PERCENTAGE);
            getComponent().getUI().getUI().addWindow(window);
        }
        else {
            snp = database.getSNP(option);   
        }
        //viewSelector.setEnabled(true);
        phenotypeSelector.setEnabled(true);
        //Notification.changeShowStatus("enabled", Notification.Type.TRAY_NOTIFICATION);
        //System.out.println("enabled");
        
        String SNPinformationString = "";
        if (snp != null) {
            SNPinformationString = 
                "SNP: " + html.floatRight(html.bold(snp.getID())) + "<br>" +
                "Chromosome: " +  html.floatRight(html.bold(snp.getChromosome()))+ "<br>" +
                "Position: " +  html.floatRight(html.bold(new Alphanumerical(snp.getPosition()).toNonBreakingString())) + "<br>" +
                html.floatRight(" (" + constants.getGenomeBuild() + ")");
        }

        if (snp != null) {
            currentSNP = snp; 
            locusZoomButton.setEnabled(true);

            if (locusZoom != null) {
                JsonObject region = Json.createObject();
                region.put("position", snp.getPosition());
                region.put("chromosome", snp.getChromosome());
                locusZoom.setRegion(region);
            }  
        }
        else {
            currentSNP = null;
            if (locusZoomWindow != null) {
                locusZoomWindow.close();
            }
            locusZoomButton.setEnabled(false);
        }
        
        if (snp == null || !snp.hasData()) {          
            plotBox.removeAllComponents();
            
            SNPShowOptionsSelectorBox.removeComponent(SNPShowOptionsSelector);            
            phenotypeSelector.setEnabled(false);
            //currentSNP = null;

            SNPInputActive = false;
            SNPRightGrid.removeComponent(SNPInformation);
            if (snp != null) {
                SNPinformationString += "<br><br>" + html.bold("No phenotype data could be found.");
                message = new Label(html.bold("No phenotype data could be found for the SNP " + html.italics(option) + "."), ContentMode.HTML);
            }
            else {
                message = new Label(html.bold("The SNP " + html.italics(option) + " could not be found."), ContentMode.HTML);
            }
            
            message.setSizeFull();
            plotBox.addComponent(message);
            
            plotBox.setComponentAlignment(message, Alignment.MIDDLE_CENTER);
            SNPInformation = new Label(SNPinformationString, ContentMode.HTML);

            SNPRightGrid.addComponent(SNPInformation, 1, 1, 8, 3);
            return false;
        }
        else {
            setDatasets(snp, currentPhenotype);
            SNPRightGrid.removeComponent(SNPInformation);
            SNPInformation = new Label(SNPinformationString, ContentMode.HTML);
            SNPInformation.setSizeFull();
            SNPRightGrid.addComponent(SNPInformation, 1, 1, 8, 3);

            if (plotBox.getComponentCount() < 2) {
                plotBox.removeAllComponents();
                plotBox.addComponent(femaleChart);
                plotBox.addComponent(maleChart);
            }

            phenotypeSelector.setEnabled(true);
            SNPShowOptionsSelectorBox.addComponent(SNPShowOptionsSelector);     
            SNPInputActive = false;
            return true;   
        }
        
    }
    
    private void addSNP(String option) {
        //System.out.println(option);
        //System.out.println(currentSNP == null);
        
        option = option.trim();

        //System.out.println("addSNP(): " + option);
        
        if (option.equals("null") || option.equals("") || (currentSNP != null && option.equals(currentSNP.getID()))
                || option.contains("(not found)") || option.matches(".*?\\[.*?\\]$")) {
            return;
        }
        //System.out.println(option);
        
        if (searchSNP(option)) {
            SNPOptions.add(option + " [your input]");
            SNPInput.setValue(option);
        }
        else {
            if (!SNPOptions.contains(option + " (not found)") && !SNPOptions.contains(option)) {
                SNPOptions.add(option + " (not found)");
            }            
            SNPInput.setValue(option + " (not found)");
            currentSNPInputValue = option + " (not found)";
            System.out.println("Setting 'not found'.");
        }      
    }
    
    private void selectPhenotype(String option) {
        if (option.equals("null") || option.equals(currentPhenotype)){
            return;
        }        
        
        setDatasets(currentSNP, option);
        currentPhenotype = option;        
    }
    
    private void changeSNPShowSettings(SelectionEvent event) {
        //System.out.println("Changed");
        Set <Option> currentlySelected = event.getAllSelectedItems();
        
        Set <Option> unselected = new HashSet();
        unselected.addAll(previousSNPShowOptions);
        unselected.removeAll(currentlySelected);
        
        Set <Option> newlySelected = new HashSet();
        newlySelected.addAll(currentlySelected);
        newlySelected.removeAll(previousSNPShowOptions);
        
        Set <Option> changed = new HashSet();
        changed.addAll(newlySelected);
        changed.addAll(unselected);
        
        
//        System.out.println("previously selected: " + previousSNPShowOptions);
//        System.out.println("unselected: " + unselected);
//        System.out.println("newly selected: " + newlySelected);
//        System.out.println("changed: " + changed);
 
        changeShowStatus(changed);
        previousSNPShowOptions = SNPShowOptionsSelector.getValue();
    }
        
    public void changeShowStatus(Set <Option> options) {
        showStatusObject = Json.createObject(); // create a new object each time
        for (Option option : options) {
            option.setVisible(!option.isVisible());
            showStatusObject.put(option.getName(), option.isVisible());
        }        
        for (SNPPlot chart : new SNPPlot[] {femaleChart, maleChart}) {
            chart.sendShowStatus(showStatusObject);
        }         
    }
    
    private void clearSNPInput(boolean clear) {
        if (clear) {
            //SNPInput.setValue("");
        }
        else {
            SNPInput.setValue(currentSNPInputValue);
        } 
    }
    
    private void openLocusZoomWindow() {
        if (currentSNP == null) {
            return;
        }
        if (locusZoomWindow == null) {
            locusZoom = new LocusZoom();
            locusZoomWindow = new Window("LocusZoom.js below - drag window here", locusZoom);
        }
        locusZoomWindow.setWidth(85, Sizeable.Unit.PERCENTAGE);
        locusZoomWindow.setHeight(70, Sizeable.Unit.PERCENTAGE);
        locusZoomWindow.center();
        JsonObject region = Json.createObject();
        region.put("position", currentSNP.getPosition());
        region.put("chromosome", currentSNP.getChromosome());
        locusZoom.setRegion(region);
        getComponent().getUI().getUI().addWindow(locusZoomWindow);
    }
    
    private void toggleWindowVisibility(Window window) {
        if (!window.isAttached()) { // is the window already open?
            getComponent().getUI().getUI().addWindow(window);
        }
        else{
            window.close();
        }
    }
    
    private void viewPlotData() {
        Window window = (Window) plotDataWindow.getComponent();
        toggleWindowVisibility(window);
    }
    
    public SNPPlot getChart1() {
        return femaleChart;
    }
    public SNPPlot getChart2() {
        return maleChart;
    }    
    public Component getComponent() {
        return box;
    }
}
