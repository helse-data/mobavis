package com.plotting;

import com.litemol.LiteMol;
import com.locuszoom.LocusZoom;
import com.plotly.SNPPlot;
import com.main.Database;
import com.main.SNP;
import com.plotly.SNPPlot;
import com.utils.Alphanumerical;
import com.utils.Constants;
import com.utils.UtilFunctions;
import com.utils.HtmlHelper;
import com.utils.JsonHelper;
import com.utils.Option;
import com.vaadin.data.HasValue.ValueChangeEvent;
import com.vaadin.event.ShortcutAction;
import com.vaadin.event.ShortcutListener;
import com.vaadin.event.selection.SelectionEvent;
import com.vaadin.event.selection.SingleSelectionEvent;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.server.Sizeable;
import com.vaadin.shared.ui.ContentMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.CheckBoxGroup;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.NativeSelect;
import com.vaadin.ui.RadioButtonGroup;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
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
public class SNPPlotBox {
    HtmlHelper html = new HtmlHelper();
    
    JsonObject dataObject;
    JsonHelper jsonHelper = new JsonHelper();
    JsonObject plotOptionsObject;
        
    GridLayout box = new GridLayout(100, 100);
    HorizontalLayout plotBox = new HorizontalLayout();
    
    SNPPlot femaleChart;
    SNPPlot maleChart;
    PlotDataWindow plotDataWindow = new PlotDataWindow();
    GridLayout rightGrid = new GridLayout(10, 10);
    VerticalLayout showOptionsSelectorBox = new VerticalLayout();
    Button morePlotOptionsButton = new Button("More options");
    Window morePlotOptionsWindow;
    HorizontalLayout topBox = new HorizontalLayout();
    CheckBoxGroup <Option <Boolean>> showOptionsSelector;
    Map <String, Option <Boolean>> booleanOptions = new HashMap();
    Map <String, Option <String>> stringOptions = new HashMap();
    Set <Option <Boolean>> previousShowOptions = new HashSet();
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
    
    Button liteMolButton = new Button("Protein 3D structure");
    Window liteMolWindow;
    LiteMol liteMol;
    
    Database database = new Database();
    
    UtilFunctions converter = new UtilFunctions();
    Constants constants = new Constants();
    
    boolean mediansShown = true;
    boolean SEMShown = true;
    boolean percentilesShown = false;
    
    
    public SNPPlotBox() {        
        femaleChart = new SNPPlot();
        maleChart = new SNPPlot();
        
        //femalePlotBox.addComponents(femaleChart, numberPlotFemale);
        //malePlotBox.addComponents(maleChart, numberPlotMale);
        //plotBox.addComponents(femalePlotBox, malePlotBox);
        plotBox.addComponents(femaleChart, maleChart);
        
        int n10 = (int) ((box.getColumns()-1)*0.1);
        
        int plotStartY = 7;
        box.addComponent(topBox, 1, 0, 70, plotStartY-1);
        box.addComponent(plotBox, 1, plotStartY, 82, 99);
        box.addComponent(rightGrid, 83, plotStartY+1, 99, 99);
        
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
        liteMolButton.addClickListener(event -> openLiteMolWindow());
        
        Button viewPlotDataButton = new Button("View plot data");
        viewPlotDataButton.addClickListener(event -> viewPlotData());

        // sendPlotOptions options
        
        Option <Boolean> medians = new Option("medians", "medians", true);
        Option <Boolean> SEM = new Option("SEM", "SEM", true);
        Option <Boolean> percentiles = new Option("percentiles", "2.5th and 97.5th percentiles", false);
        Option <Boolean> n = new Option("n", "number of individuals", true);
        Option <String> ageSpacing = new Option("age spacing", "age spacing", "to scale");
        Option <Boolean> yaxisToZero = new Option("y to zero", "y-axis to zero", false);
        
        List <Option <Boolean>> showOptionList = new ArrayList();
        showOptionList.add(medians);
        showOptionList.add(SEM);
        showOptionList.add(percentiles);
        showOptionList.add(n);
        
        for (Option <Boolean> showOption : showOptionList) {
            booleanOptions.put(showOption.getName(), showOption);
        }
        booleanOptions.put(yaxisToZero.getName(), yaxisToZero);
        
        stringOptions.put(ageSpacing.getName(), ageSpacing);
        
        showOptionsSelector = new CheckBoxGroup("Show");
        showOptionsSelector.setItems(showOptionList);

        morePlotOptionsButton.addClickListener(event -> openMoreShowOptionsWindow());
        
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
        
        showOptionsSelector.select(medians, SEM, n);
        previousShowOptions = showOptionsSelector.getValue();
        showOptionsSelector.addSelectionListener(event -> changeShowSettings(event));
        
        showOptionsSelectorBox.addComponent(showOptionsSelector);
        showOptionsSelectorBox.addComponent(morePlotOptionsButton);
        
        rightGrid.addComponent(showOptionsSelectorBox, 1, 0, 9, 4);
        
        topBox.addComponent(SNPInput);
        topBox.addComponent(phenotypeSelector);
        topBox.addComponent(locusZoomButton);
        topBox.setComponentAlignment(locusZoomButton, Alignment.BOTTOM_CENTER);
        topBox.addComponent(liteMolButton);
        topBox.setComponentAlignment(liteMolButton, Alignment.BOTTOM_CENTER);
        topBox.addComponent(viewPlotDataButton);
        topBox.setComponentAlignment(viewPlotDataButton, Alignment.BOTTOM_CENTER);
        
        box.setSizeFull();
        topBox.setSizeFull();
        plotBox.setSizeFull();
        femaleChart.setSizeFull();
        maleChart.setSizeFull();
        rightGrid.setSizeFull();
        showOptionsSelectorBox.setSizeFull();        
        SNPInput.setSizeFull();
        phenotypeSelector.setSizeFull();
    }
       
    public void setDatasets(SNP snp, String phenotype) {
        int overallMaxN = 0;
        double overallMaxSEM = 0;
        double overallMinSEM = -1;
        double overallMaxPercentile = 0;
        double overallMinPercentile = -1;
        double overallMaxMedian = 0;
        double overallMinMedian = -1;

        JsonObject snpDataObject = snp.getDataObject();
        
        Map <String, JsonObject> dataObjects = new HashMap();
        
        for (String sex : new String[] {"female", "male"}) {
            dataObject = Json.createObject();
            dataObjects.put(sex, dataObject);
            
            dataObject.put("sex", sex);
            dataObject.put("SNP ID", snp.getID());
            dataObject.put("phenotype", phenotype);
            
            for (String genotype : new String[] {"AA", "AB", "BB"}) { 
                //System.out.println("phenotype: " + phenotype);
                //System.out.println("phenotype: " + phenotype + ", object: " + snpDataObject.getObject(phenotype).getObject(sex));
                JsonObject genotypeObject = snpDataObject.getObject(phenotype).getObject(sex).getObject(genotype);
                //System.out.println("genotypeObject: " + genotypeObject.toJson());
                if (snpDataObject.getObject(phenotype).getBoolean("longitudinal")) {
                    List <String> nData = converter.jsonArrayToList(genotypeObject.get("N"));
                    String nMin = converter.minInteger(nData);
                    String nMax = converter.maxInteger(nData);
                    
                    if (genotypeObject.hasKey("median")) {
                        List <String> medianData = converter.jsonArrayToList(genotypeObject.get("median"));
                        double medianMax = Double.parseDouble(converter.maxDouble(medianData));                        
                        if (medianMax > overallMaxMedian) {
                            overallMaxMedian = medianMax;
                        }
                        double medianMin = Double.parseDouble(converter.minDouble(medianData));
                        if (medianMin < overallMinMedian || overallMinMedian < 0) {
                            overallMinMedian = medianMin;
                        }                        
                    }
                    
                    if (genotypeObject.hasKey("upper SEM")) {
                        List <String> upperSEMData = converter.jsonArrayToList(genotypeObject.get("upper SEM"));
                        double semMax = Double.parseDouble(converter.maxDouble(upperSEMData));
                        if (semMax > overallMaxSEM) {
                            overallMaxSEM = semMax;
                        }   
                    }
                    if (genotypeObject.hasKey("lower SEM")) {
                        List <String> lowerSEMData = converter.jsonArrayToList(genotypeObject.get("lower SEM"));
                        double semMin = Double.parseDouble(converter.minDouble(lowerSEMData));
                        if (semMin < overallMinSEM || overallMinSEM < 0) {
                            overallMinSEM = semMin;
                        }
                    }
                    if (genotypeObject.hasKey("97.5%")) {
                        List <String> upperPercentileData = converter.jsonArrayToList(genotypeObject.get("97.5%"));
                        double percentileMax = Double.parseDouble(converter.maxDouble(upperPercentileData));
                        if (percentileMax > overallMaxPercentile) {
                            overallMaxPercentile = percentileMax;
                        }
                    }
                    if (genotypeObject.hasKey("2.5%")) {
                        List <String> lowerPercentileData = converter.jsonArrayToList(genotypeObject.get("2.5%"));
                        double percentileMin = Double.parseDouble(converter.minDouble(lowerPercentileData));
                        if (percentileMin < overallMinPercentile || overallMinPercentile < 0) {
                            overallMinPercentile = percentileMin;
                        }
                    }


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
            
            JsonObject numberObject = Json.createObject();
            for (String genotype : new String[] {"AA", "AB", "BB"}) {
                numberObject.put(genotype, dataObject.getObject(genotype).getArray("N"));
            }
            //numberPlot.sendData(numberObject);
        }
        
        // when SEM or percentile data is missing, medians might provide the extreme values
        if (overallMinSEM > overallMinMedian) {
            overallMinSEM = overallMinMedian;
        }
        if (overallMinPercentile > overallMinMedian) {
            overallMinPercentile = overallMinMedian;
        }
        if (overallMaxSEM < overallMaxMedian) {
            overallMaxSEM = overallMaxMedian;
        }
        if (overallMaxPercentile < overallMaxMedian) {
            overallMaxPercentile = overallMaxMedian;
        }
        
        // enter extreme values and send
        for (String sex : new String[] {"female", "male"}) {
            dataObjects.get(sex).put("SEM min", overallMinSEM);
            dataObjects.get(sex).put("SEM max", overallMaxSEM);
            dataObjects.get(sex).put("percentile max", overallMaxPercentile);
            dataObjects.get(sex).put("percentile min", overallMinPercentile);
            dataObjects.get(sex).put("n max", overallMaxN);
        }
        femaleChart.sendData(dataObjects.get("female"));
        maleChart.sendData(dataObjects.get("male"));
        plotDataWindow.setTab("1", dataObjects.get("female").toJson(), "female");
        plotDataWindow.setTab("2", dataObjects.get("male").toJson(), "male");        
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
        //Notification.sendPlotOptions("disabled", Notification.Type.TRAY_NOTIFICATION);
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
        //Notification.sendPlotOptions("enabled", Notification.Type.TRAY_NOTIFICATION);
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
            liteMolButton.setEnabled(true);

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
            
            if (liteMolWindow != null) {
                liteMolWindow.close();
            }
            liteMolButton.setEnabled(false);
        }
        
        if (snp == null || !snp.hasData()) {          
            plotBox.removeAllComponents();
            
            showOptionsSelectorBox.removeComponent(showOptionsSelector);        
            showOptionsSelectorBox.removeComponent(morePlotOptionsButton);
            phenotypeSelector.setEnabled(false);
            //currentSNP = null;

            SNPInputActive = false;
            rightGrid.removeComponent(SNPInformation);
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

            rightGrid.addComponent(SNPInformation, 1, 5, 8, 9);
            return false;
        }
        else {
            setDatasets(snp, currentPhenotype);
            rightGrid.removeComponent(SNPInformation);
            SNPInformation = new Label(SNPinformationString, ContentMode.HTML);
            SNPInformation.setSizeFull();
            rightGrid.addComponent(SNPInformation, 1, 5, 8, 9);

            if (plotBox.getComponentCount() < 2) {
                plotBox.removeAllComponents();
                //plotBox.addComponents(femalePlotBox, malePlotBox);
                plotBox.addComponents(femaleChart, maleChart);
            }
            phenotypeSelector.setEnabled(true);
            showOptionsSelectorBox.addComponent(showOptionsSelector);
            showOptionsSelectorBox.addComponent(morePlotOptionsButton);
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
    
    
    
    private void changeShowSettings(SelectionEvent event) {
        //System.out.println("Show settings shanged");
        Set <Option <Boolean>> currentlySelected = event.getAllSelectedItems();
        
        Set <Option <Boolean>> unselected = new HashSet();
        unselected.addAll(previousShowOptions);
        unselected.removeAll(currentlySelected);
        
        Set <Option <Boolean>> newlySelected = new HashSet();
        newlySelected.addAll(currentlySelected);
        newlySelected.removeAll(previousShowOptions);
        
        Set <Option <Boolean>> changed = new HashSet();
        changed.addAll(newlySelected);
        changed.addAll(unselected);
        
        for (Option <Boolean> option : changed) {
            option.setValue(!option.getValue());
        }  
        
        
//        System.out.println("previously selected: " + previousShowOptions);
//        System.out.println("unselected: " + unselected);
//        System.out.println("newly selected: " + newlySelected);
        //System.out.println("changed show options: " + changed);
 
        //changeShowStatus(changed);
        sendPlotOptions();
        previousShowOptions = showOptionsSelector.getValue();
    }
    
    
        
    public void sendPlotOptions() {
        plotOptionsObject = Json.createObject(); // create a new object each time
        for (String optionKey : booleanOptions.keySet()) {
            Option <Boolean> option = booleanOptions.get(optionKey);
            plotOptionsObject.put(option.getName(), option.getValue());
        }
        for (String optionKey : stringOptions.keySet()) {
            Option <String> option = stringOptions.get(optionKey);
            plotOptionsObject.put(option.getName(), option.getValue());
        }
        //System.out.println("plotOptionsObject: " + plotOptionsObject.toJson());
        for (SNPPlot chart : new SNPPlot[] {femaleChart, maleChart}) {
            chart.sendPlotOptions(plotOptionsObject);
        }
    }
    
    public void changeAgeSpacing(SingleSelectionEvent <String> event) {
        if (!event.isUserOriginated()) {
            return;
        }
        //System.out.println("event.getValue(): " + event.getValue());
        stringOptions.get("age spacing").setValue(event.getValue());
        sendPlotOptions();
    }
    
    public void changeYaxisToZero(ValueChangeEvent <Boolean> event) {
        if (!event.isUserOriginated()) {
            return;
        }
        //System.out.println("event.getValue(): " + event.getValue());
        booleanOptions.get("y to zero").setValue(event.getValue());
        sendPlotOptions();
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
            locusZoomWindow.setWidth(85, Sizeable.Unit.PERCENTAGE);
            locusZoomWindow.setHeight(70, Sizeable.Unit.PERCENTAGE);
            locusZoomWindow.center();
        }        
        JsonObject region = Json.createObject();
        region.put("position", currentSNP.getPosition());
        region.put("chromosome", currentSNP.getChromosome());
        locusZoom.setRegion(region);
        //getComponent().getUI().getUI().addWindow(locusZoomWindow);
        toggleWindowVisibility(locusZoomWindow);
    }
    
    private void openLiteMolWindow() {
        if (currentSNP == null) {
            return;
        }
        if (liteMolWindow == null) {
            GridLayout window = new GridLayout(100, 100);
            window.setSizeFull();
            liteMol = new LiteMol();
            
            
            TextField entryIDInputField = new TextField("PDB entry ID");
            //entryIDInputField.addValueChangeListener(event -> setPDBEntryID(event));
            Button submitButton = new Button("Submit");
            submitButton.addClickListener(event -> submitEntryID(entryIDInputField));
            submitButton.addShortcutListener(new ShortcutListener("Enter", ShortcutAction.KeyCode.ENTER, null) {
                    @Override
                    public void handleAction(Object sender, Object target) {
                        submitEntryID(entryIDInputField);
                    }
                    });
            
            String exampleEntryID = "1aoi"; // "1a4y"
            entryIDInputField.setValue(exampleEntryID);
            setEntryID(exampleEntryID);
            
            window.addComponent(entryIDInputField, 47, 0, 49, 2);
            window.addComponent(submitButton, 50, 0, 52, 2);
            window.setComponentAlignment(submitButton, Alignment.BOTTOM_CENTER);
            window.setComponentAlignment(entryIDInputField, Alignment.BOTTOM_CENTER);
            window.addComponent(liteMol, 0, 4, 99, 99);
            liteMolWindow = new Window("LiteMol visualisation", window);
            liteMolWindow.setWidth(60, Sizeable.Unit.PERCENTAGE);
            liteMolWindow.setHeight(90, Sizeable.Unit.PERCENTAGE);
            liteMolWindow.center();
        }        
        //JsonObject region = Json.createObject();
        //region.put("position", currentSNP.getPosition());
        //region.put("chromosome", currentSNP.getChromosome());
        //locusZoom.setRegion(region);
        //getComponent().getUI().getUI().addWindow(locusZoomWindow);
        toggleWindowVisibility(liteMolWindow);
    }
    
    
    private void setEntryID(String entryID) {
        JsonObject entryIDObject = Json.createObject();
        entryIDObject.put("ID", entryID);
        liteMol.setEntryID(entryIDObject);
    }
    
    
    private void submitEntryID(TextField inputField) {
        setEntryID(inputField.getValue());
    }
    
    private void enterEntryID(ValueChangeEvent <String> event) {
        setEntryID(event.getValue());
    }
    
    private void openMoreShowOptionsWindow() {
        if (morePlotOptionsWindow == null) {            
            // age spacing
            List <String> ageListSpacingOptions = Arrays.asList(new String [] {"to scale", "equal"});
            RadioButtonGroup <String> ageSpacingSelector = new RadioButtonGroup("Spacing between ages", ageListSpacingOptions);
            ageSpacingSelector.addSelectionListener(event -> changeAgeSpacing(event));
            ageSpacingSelector.setSelectedItem(ageListSpacingOptions.get(0));
            ageSpacingSelector.addStyleName(ValoTheme.OPTIONGROUP_HORIZONTAL);
            
            // y-axis range
            CheckBox yAxisToZeroBox = new CheckBox("y-axis to zero", false);
            yAxisToZeroBox.addValueChangeListener(event -> changeYaxisToZero(event));
            
            
            VerticalLayout content = new VerticalLayout();
            content.addComponent(ageSpacingSelector);
            content.addComponent(new Label("<b>Miscellaneous</b>", ContentMode.HTML));
            content.addComponent(yAxisToZeroBox);
            morePlotOptionsWindow = new Window("Addtional plot options", content);
            morePlotOptionsWindow.setWidth(30, Sizeable.Unit.PERCENTAGE);
            morePlotOptionsWindow.setHeight(70, Sizeable.Unit.PERCENTAGE);
            morePlotOptionsWindow.center();
        }
        toggleWindowVisibility(morePlotOptionsWindow);
        
        //getComponent().getUI().getUI().addWindow(morePlotOptionsWindow);
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
