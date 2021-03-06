package com.visualization.geno;

import com.utils.vaadin.PlotDataWindow;
import com.components.archive.LoadingIndicator;
import com.litemol.LiteMol;
import com.locuszoom.LocusZoom;
import com.database.Database;
import com.database.SNPDatabaseEntry;
import com.snp.VerifiedSNP;
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
import com.vaadin.ui.AbstractComponent;
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
import com.visualization.MoBaVisualization;
import com.database.web.DbSNPentry;
import com.main.Controller;
import com.snp.InputSNP;
import com.snp.SNP;
import com.snp.SNPIDParser;
import com.snp.SNPIDParser.SNPIDFormat;
import com.utils.vaadin.CaptionLeft;
import elemental.json.Json;
import elemental.json.JsonObject;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import com.visualization.MoBaVisualizationInterface;

/**
 * Class for the visualization instance for the phenotype plots stratified by genotype.
 *
 * @author Christoffer Hjeltnes Støle
 */
public class SNPPlotBox extends GenoView {
    HtmlHelper html = new HtmlHelper();
    
    JsonObject dataObject;
    JsonHelper jsonHelper = new JsonHelper();    

    VerticalLayout box = new VerticalLayout();
    HorizontalLayout middleBox = new HorizontalLayout();
    HorizontalLayout plotBox = new HorizontalLayout();
    
    SNPPlot femaleChart;
    SNPPlot maleChart;
    PlotDataWindow plotDataWindow = new PlotDataWindow();
    VerticalLayout rightBox = new VerticalLayout();
    Button morePlotOptionsButton = new Button("More options");
    Window morePlotOptionsWindow;
    HorizontalLayout topBox = new HorizontalLayout();
    CheckBoxGroup <ShowOption> showOptionsSelector;
    Map <String, Option <Boolean>> booleanOptions = new HashMap();
    Map <ShowOption, Boolean> showOptions = new HashMap();
    Map <String, Option <String>> stringOptions = new HashMap();
    Set <ShowOption> previousShowOptions = new HashSet();
    Label plotReplacingMessage;
    Label SNPInformation = new Label();
    HorizontalLayout SNPinformationContainer = new HorizontalLayout();
    
    List <String> phenotypeOptions = new ArrayList();
    
    String currentPhenotype;
    
    List <String> SNPOptions;
    
    ComboBox <String> SNPInput;
    String currentSNPInputValue;
    boolean SNPInputActive = false;
    boolean snpUpdateInProgress = false;
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
    
    
    public SNPPlotBox(Controller controller) {
        super(controller);
        phenotypeOptions.addAll(Arrays.asList(new String[]{
            "height", "weight", "BMI"}));
        phenotypeSelector = new NativeSelect();
        phenotypeSelector.setItems(phenotypeOptions);        
        phenotypeSelector.addValueChangeListener(event -> selectPhenotype(String.valueOf(
                event.getValue())));  
        phenotypeSelector.setEmptySelectionAllowed(false);
        
        CaptionLeft phenotypeSelectorComponent = new CaptionLeft("Phenotype", phenotypeSelector, VaadinIcons.CLIPBOARD_PULSE);
        
        locusZoomButton.addClickListener(event -> openLocusZoomWindow());
        liteMolButton.addClickListener(event -> openLiteMolWindow());
        
        Button viewPlotDataButton = new Button("View plot data");
        viewPlotDataButton.addClickListener(event -> viewPlotData());

        // plot options        
        Option <String> ageSpacing = new Option("age spacing", "age spacing", "to scale");
        Option <Boolean> yaxisToZero = new Option("y to zero", "y-axis to zero", false);
        
        List <ShowOption> showOptionList = new ArrayList();
        
        for (ShowOption showOption : ShowOption.values()) {
            showOptionList.add(showOption);
            showOptions.put(showOption, showOption.getDefaultValue());
        }
        
        booleanOptions.put(yaxisToZero.getName(), yaxisToZero);
        
        stringOptions.put(ageSpacing.getName(), ageSpacing);
        
        showOptionsSelector = new CheckBoxGroup("Show");
        showOptionsSelector.setItems(showOptionList);

        morePlotOptionsButton.addClickListener(event -> openMoreShowOptionsWindow());
        
        // create the plots
        JsonObject setup = getPlotOptions();
        femaleChart = new SNPPlot(setup);
        maleChart = new SNPPlot(setup);
        
        plotBox.addComponents(femaleChart, maleChart);
        

        box.addComponent(topBox);
        box.setExpandRatio(topBox, 1);
        
        middleBox.addComponent(plotBox);
        middleBox.setExpandRatio(plotBox, 5);
        middleBox.addComponent(rightBox);
        middleBox.setExpandRatio(rightBox, 1);
        
        box.addComponent(middleBox);
        box.setExpandRatio(middleBox, 10);
               
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
        

        SNPInput.setItems(SNPOptions);        
        SNPInput.addValueChangeListener(event -> snpInputEntered(event));
        //SNPInput.addValueChangeListener(event -> controller.SNPIDinputChanged()); 03.11
        SNPInput.setNewItemHandler(inputString -> addSNP(inputString));
        SNPInput.setIcon(VaadinIcons.CUBES);
        SNPInput.setEmptySelectionAllowed(false);
        
        currentPhenotype = "BMI";
        phenotypeSelector.setValue(currentPhenotype);
        
        //SNPInput.setValue(SNPOptions.get(0));  
        SNPInput.setValue(getController().getActiveSNP().getID());
        //updateSNP();
        
        for (ShowOption showOption : showOptionList) {
            if (showOption.getDefaultValue()) {
                showOptionsSelector.select(showOption);
            }
        }
        
        previousShowOptions = showOptionsSelector.getValue();
        showOptionsSelector.addSelectionListener(event -> changeShowSettings(event));
        
        rightBox.addComponent(showOptionsSelector);
        rightBox.setExpandRatio(showOptionsSelector, 6);
        //rightBox.setComponentAlignment(showOptionsSelector, Alignment.TOP_CENTER);
        rightBox.addComponent(morePlotOptionsButton);
        rightBox.setComponentAlignment(morePlotOptionsButton, Alignment.TOP_CENTER);
        rightBox.setExpandRatio(morePlotOptionsButton, 1);
        //showOptionsSelectorBox.addStyleName(ValoTheme.PANEL_WELL);
        
        //
        
        //rightGrid.addComponent(showOptionsSelectorBox, 1, 0, 9, 4);      
        //rightBox.addComponent(showOptionsSelectorBox, 0);
        
        
        rightBox.addComponent(SNPinformationContainer);
        rightBox.setComponentAlignment(SNPinformationContainer, Alignment.TOP_CENTER);
        rightBox.setExpandRatio(SNPinformationContainer, 8);
        rightBox.setComponentAlignment(SNPinformationContainer, Alignment.BOTTOM_CENTER);
        //SNPinformationContainer.addStyleName(ValoTheme.PANEL_WELL);
        
        topBox.addComponent(SNPInput);
        //topBox.addComponent(phenotypeSelector);
        topBox.addComponent(phenotypeSelectorComponent.getComponent());
        topBox.addComponent(locusZoomButton);
        topBox.setComponentAlignment(locusZoomButton, Alignment.BOTTOM_CENTER);
        topBox.addComponent(liteMolButton);
        topBox.setComponentAlignment(liteMolButton, Alignment.BOTTOM_CENTER);
        topBox.addComponent(viewPlotDataButton);
        topBox.setComponentAlignment(viewPlotDataButton, Alignment.BOTTOM_CENTER);
        
        box.setSizeFull();
        topBox.setSizeFull();
        plotBox.setSizeFull();
        middleBox.setSizeFull();
        femaleChart.setSizeFull();
        maleChart.setSizeFull();
        rightBox.setSizeFull();
        //showOptionsSelectorBox.setSizeFull();
        SNPinformationContainer.setSizeFull();
        SNPInput.setSizeFull();
        //phenotypeSelector.setSizeFull();
    }
    
    /**
     * Enumerator of the show options; includes the default value for each option.
     * 
     */
    public enum ShowOption {
        MEDIANS("medians", "medians", true),
        SEM("SEM", "SEM", true),
        PERCENTILES("percentiles", "2.5th and 97.5th percentiles", false),
        N("n", "number of individuals", true),
        THREE_D("3D", "3D", false);

        private final String shortName;
        private final String displayName;
        private final boolean defaultValue;
     
        ShowOption(String shortName, String displayName, boolean defaultValue) {
            this.shortName = shortName;
            this.displayName = displayName;
            this.defaultValue = defaultValue;
        }
        
        @Override
        public String toString() {
            return displayName;
        }
        
        public String getShortName() {
            return shortName;
        }
        
        public boolean getDefaultValue () {
            return defaultValue;
        }
    }
       
    
    /**
     * Makes the data retrieved from the database system take effect.
     * 
     * @param snp - VerifiedSNP object with data from the database system
     * @param phenotype 
     */
    public void setDatasets(VerifiedSNP snp, String phenotype) {
        int overallMaxN = 0;
        double overallMaxSEM = 0;
        double overallMinSEM = -1;
        double overallMaxPercentile = 0;
        double overallMinPercentile = -1;
        double overallMaxMedian = 0;
        double overallMinMedian = -1;

        JsonObject snpDataObject = snp.getDataBaseEntry().getDataObject();
        
        //System.out.println("snpDataObject: " + snpDataObject.toJson());
        
        Map <String, JsonObject> dataObjects = new HashMap();
        
        for (String sex : new String[] {"female", "male"}) {
            dataObject = Json.createObject();
            dataObjects.put(sex, dataObject);
            
            dataObject.put("sex", sex);
            dataObject.put("SNP ID", snp.getDataBaseEntry().getAnnotation().get("Id"));
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
        
        //System.out.println("dataObjects: " + dataObjects);
        
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
    
    private void snpInputEntered(ValueChangeEvent event) {
        String input = event.getValue().toString();
        if (event.isUserOriginated()) {
            searchSNP(input);
        }
    }
    
    /**
     * Invokes the database system to search for the input string.
     * 
     * @param searchString 
     */
    private void searchSNP(String searchString) {
        SNP currentSNP = getController().getActiveSNP();
        
        if (SNPInputActive) {
            System.out.println("SNP input is active.");
            return;
        }
        //viewSelector.setEnabled(false); // TODO: check effects
        SNPInputActive = true;
        //SNPInformation.setCaption("");
        System.out.println("searchSNP(): " + searchString);
        //System.out.println(currentSNP == null);
        SNPInput.setValue(searchString.replaceFirst(" \\[your input\\]$", ""));
        searchString = searchString.replaceFirst(" \\[.*?\\]$", "");
        
        if (searchString.equals("null") || searchString.equals("") || (currentSNP != null && currentSNP instanceof VerifiedSNP && searchString.equals(currentSNP.getID()))
                || searchString.contains("(not found)")) {
            SNPInputActive = false;
            System.out.println("SNP already chosen");
            //SNPRightGrid.removeComponent(SNPInformation);
            //SNPInformation = new Label("");
            //SNPRightGrid.addComponent(SNPInformation);
             //viewSelector.setEnabled(true);// TODO: check effects
            
            //return false;
        }
        
       
        phenotypeSelector.setEnabled(false);
        //System.out.println("disabled");
        //Notification.sendPlotOptions("disabled", Notification.Type.TRAY_NOTIFICATION);
        currentSNPInputValue = searchString;
        
        VerifiedSNP verifiedSNP = null;
        
        SNPIDParser snpIDParser = new SNPIDParser(searchString);
        SNPIDFormat IDFormat = snpIDParser.getIDFormat();
        
        //System.out.println("format: " + snpIDParser.getIDFormat());
        
        if (IDFormat == SNPIDFormat.UNRECOGNIZED) {
            SNPInputActive = false;
            verifiedSNP = null;
        }        
        else if (IDFormat.equals(SNPIDFormat.CHROMOSOME_POSITION)) { // SNP entered in format chromosome:position
            String chromosome = snpIDParser.getChromosome();
            String position = snpIDParser.getPosition();
            
            verifiedSNP = database.getSNP(chromosome, position);
            
            if (verifiedSNP != null && !verifiedSNP.hasData() && !verifiedSNP.hasAnnotation()) { // SNP is not found in the database system; try searching the neighbourhood
                Map <String, String> result = database.getNearestSNPs(chromosome, Integer.parseInt(position));
                System.out.println("result: " + result);
                
                if (result.get("result").equals("exact")) {
                    verifiedSNP = database.getSNP(new SNPIDParser(result.get("0")));
                }
                else {
                    List <String> snpOptions = new ArrayList();

                    if (result.containsKey("-1")) {
                        snpOptions.add(result.get("-1"));
                    }
                    if (result.containsKey("1")) {
                        snpOptions.add(result.get("1"));
                    }

                    RadioButtonGroup <String> selection = new RadioButtonGroup("Select SNP", snpOptions);
                    selection.addValueChangeListener(event -> searchSNP(event.getValue()));
                    String windowCaption = "SNPs nearest to position " + position + " on chromosome " + chromosome + ":";
                    Window window = new Window(windowCaption);
                    window.center();
                    window.setContent(selection);
                    window.setWidth(Math.round(windowCaption.length()*9.7), Sizeable.Unit.PIXELS);
                    window.setHeight(10, Sizeable.Unit.PERCENTAGE);
                    getComponent().getUI().addWindow(window);
                }

            }
        }
        else {
            verifiedSNP = database.getSNP(snpIDParser);
        }
        
        
        
        //viewSelector.setEnabled(true);
        phenotypeSelector.setEnabled(true);
        //Notification.sendPlotOptions("enabled", Notification.Type.TRAY_NOTIFICATION);
        //System.out.println("enabled");
        if (verifiedSNP == null) {
            if (!snpUpdateInProgress) {
                if (IDFormat.equals(SNPIDFormat.CHROMOSOME_POSITION)) {
                    getController().setActiveSNP(new InputSNP(snpIDParser.getChromosome(), snpIDParser.getPosition()));
                }
                else {
                    getController().setActiveSNP(new InputSNP(searchString));
                }
            }
        }
        else {
            getController().setActiveSNP(verifiedSNP);
        }
        if (!snpUpdateInProgress) {
            updateSNP();
        }
        SNPInputActive = false;
    }
    
    @Override
    public void updateSNP() {        
        snpUpdateInProgress = true;
        String SNPinformationString = "";
        
        SNP currentSNP = getController().getActiveSNP();
        
        if (currentSNP instanceof InputSNP && ((InputSNP) currentSNP).verificationFailed() == null) {
            String currentSNPID = currentSNP.getID();
            
            if (currentSNPID == null) { // search by input chromosome and position
                String searchString = currentSNP.getChromosome() + ":" + currentSNP.getPosition();
                searchSNP(searchString);
            }
            else { // search by input SNP ID
                searchSNP(currentSNPID);
            }            
            currentSNP = getController().getActiveSNP();
        }
  
        
        System.out.println("Current SNP: " + currentSNP);
        String plotReplacingMessageText = "";
        
        
        if (currentSNP instanceof VerifiedSNP) {
            VerifiedSNP verifiedCurrentSNP = (VerifiedSNP) currentSNP;
            if (currentSNP != null) {

                String locusString = "N/A";
                String locus = verifiedCurrentSNP.getLocus();
                if (locus != null) {
                    locusString = html.hoverText(locus, verifiedCurrentSNP.getLocusFullName());
                }
                DbSNPentry dbSNPentry = verifiedCurrentSNP.getDbSNPentry();
                String dbSNPreference = "";
                if (dbSNPentry != null) {
                    dbSNPreference = html.floatRight(html.link(dbSNPentry.getEntryURL(), "<br>(dbSNP)"));
                }

                //System.out.println("snp: " + snp);
                //System.out.println("snp.getDataBaseEntry(): " + snp.getDataBaseEntry());


                SNPinformationString = 
                    "SNP: " + html.floatRight(html.bold(verifiedCurrentSNP.getDataBaseEntry().getAnnotation().get("Id"))) + "<br>" +
                    "Chromosome: " +  html.floatRight(html.bold(verifiedCurrentSNP.getDataBaseEntry().getAnnotation().get("Chromosome"))) + "<br>" +
                    "Position: " +  html.floatRight(html.bold(new Alphanumerical(verifiedCurrentSNP.getDataBaseEntry().getAnnotation().get("Position")).toNonBreakingString())) + "<br>" +
                    html.floatRight(" (" + constants.getGenomeBuild() + ")") + "<br>" +
                    html.hoverText("Locus: ", "As defined by dbSNP") + html.floatRight(html.bold(locusString)) + 
                    dbSNPreference;
            }

            if (verifiedCurrentSNP != null) {
                //currentSNP = verifiedSNP; 
                locusZoomButton.setEnabled(true);
                liteMolButton.setEnabled(true);

                if (locusZoom != null) {
                    JsonObject region = Json.createObject();
                    region.put("position", verifiedCurrentSNP.getPosition());
                    region.put("chromosome", verifiedCurrentSNP.getChromosome());
                    locusZoom.setRegion(region);
                }  
            }
            else {
                verifiedCurrentSNP = null;
                if (locusZoomWindow != null) {
                    locusZoomWindow.close();
                }
                locusZoomButton.setEnabled(false);

                if (liteMolWindow != null) {
                    liteMolWindow.close();
                }
                liteMolButton.setEnabled(false);
            }

            if (!verifiedCurrentSNP.hasData() || verifiedCurrentSNP.getDataBaseEntry().getDataObject().keys().length == 0) {          

                //verifiedSNP = null;

                SNPInputActive = false;
               
                SNPinformationString += "<br><br>" + html.bold("No phenotype data could be found.");
                plotReplacingMessageText = "No phenotype data could be found for the SNP " + html.italics(verifiedCurrentSNP.getID()) + ".";
                plotReplacingMessage = new Label(html.bold(plotReplacingMessageText), ContentMode.HTML);
                replacePlotsWithMessage(plotReplacingMessage);
  
                SNPInformation = new Label(SNPinformationString, ContentMode.HTML);

                SNPinformationContainer.addComponent(SNPInformation);
                //return false;
            }
            else {
                setDatasets(verifiedCurrentSNP, currentPhenotype);
                SNPinformationContainer.removeComponent(SNPInformation);
                SNPInformation = new Label(SNPinformationString, ContentMode.HTML);
                SNPInformation.setSizeFull();
                SNPinformationContainer.addComponent(SNPInformation);

                if (plotBox.getComponentCount() < 2) {
                    plotBox.removeAllComponents();
                    //plotBox.addComponents(femalePlotBox, malePlotBox);
                    plotBox.addComponents(femaleChart, maleChart);
                }
                phenotypeSelector.setEnabled(true);
                rightBox.addComponent(showOptionsSelector, 0);
                rightBox.addComponent(morePlotOptionsButton, 0);
                SNPInputActive = false;
                //return true;   
            }
        }
        else {
            if (currentSNP.getID() == null) {
                plotReplacingMessageText = "No SNP was found on chromosome " + currentSNP.getChromosome() + " at position " + currentSNP.getPosition() + ".";
            }
            else {
                SNPIDParser snpIDParser = new SNPIDParser(currentSNP.getID());
                SNPIDFormat IDFormat = snpIDParser.getIDFormat();
                plotReplacingMessageText = "The SNP " + html.italics(currentSNP.getID()) + " could not be found.";
                if (IDFormat == SNPIDFormat.UNRECOGNIZED) {
                    plotReplacingMessageText += " The input format was not recognized.";
                }
            }
            plotReplacingMessage = new Label(html.bold(plotReplacingMessageText), ContentMode.HTML);
            replacePlotsWithMessage(plotReplacingMessage);    
        }
        snpUpdateInProgress = false;
    }
    
    /**
     * 
     * Replaces the plots with a message, e.g. when a SNP was not found.
     * 
     * @param message 
     */
    private void replacePlotsWithMessage(Label message) {
        message.setSizeFull();
        plotBox.removeAllComponents();
        rightBox.removeComponent(showOptionsSelector);        
        rightBox.removeComponent(morePlotOptionsButton);
        phenotypeSelector.setEnabled(false);
        SNPinformationContainer.removeComponent(SNPInformation);
        plotBox.addComponent(message);
        plotBox.setComponentAlignment(message, Alignment.MIDDLE_CENTER);
        
    }
    
    /**
     * Adds a SNP to the drop-down list.
     * 
     * @param option 
     */
    private void addSNP(String option) {
        //System.out.println(option);
        //System.out.println(currentSNP == null);
        
        option = option.trim();

        //System.out.println("addSNP(): " + option);
        
        SNP currentSNP = getController().getActiveSNP();
        if (option.equals("null") || option.equals("") || (currentSNP != null && option.equals(currentSNP.getID()))
                || option.contains("(not found)") || option.matches(".*?\\[.*?\\]$")) {
            return;
        }
        //System.out.println(option);
        searchSNP(option);
        if (getController().getActiveSNP() instanceof VerifiedSNP) {
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
    
    
    /**
     * Responds to a phenotype selection by the user.
     * 
     * @param option 
     */
    private void selectPhenotype(String option) {
        if (option.equals("null") || option.equals(currentPhenotype)){
            return;
        }        
        
        setDatasets((VerifiedSNP) getController().getActiveSNP(), option);
        currentPhenotype = option;        
    }
    
    
    /**
     * 
     * Responds to the user changing the plot settings.
     * 
     * @param event 
     */
    private void changeShowSettings(SelectionEvent event) {
        //System.out.println("Show settings shanged");
        
        Set <ShowOption> currentlySelected = event.getAllSelectedItems();
        
        Set <ShowOption> unselected = new HashSet();
        unselected.addAll(previousShowOptions);
        unselected.removeAll(currentlySelected);
        
        Set <ShowOption> newlySelected = new HashSet();
        newlySelected.addAll(currentlySelected);
        newlySelected.removeAll(previousShowOptions);
        
        Set <ShowOption> changed = new HashSet();
        changed.addAll(newlySelected);
        changed.addAll(unselected);
        
        for (ShowOption showOption : changed) {
            showOptions.put(showOption, !showOptions.get(showOption));
        }  
        
        
//        System.out.println("previously selected: " + previousShowOptions);
//        System.out.println("unselected: " + unselected);
//        System.out.println("newly selected: " + newlySelected);
        //System.out.println("changed show options: " + changed);
 
        //changeShowStatus(changed);
        
        // TODO: try with JS feedback
        //LoadingIndicator <String> loadingIndicator = new LoadingIndicator("Loading 3D views ...");
        //getComponent().getUI().addWindow(loadingIndicator);
        sendPlotOptions();
        //loadingIndicator.close();
 
        // the option to show the bar chart is only enabled for the 2D view
        if (showOptions.get(ShowOption.THREE_D)) {
            showOptionsSelector.setItemEnabledProvider(item -> !item.equals(ShowOption.N));
        }
        else {
            showOptionsSelector.setItemEnabledProvider(item -> true);
        }
        
        previousShowOptions = showOptionsSelector.getValue();
    }

    /**
     * Returns the current plot options.
     * 
     * @return 
     */
    public JsonObject getPlotOptions() {
        JsonObject plotOptionsObject = Json.createObject(); // create a new object each time
        for (ShowOption showOption : ShowOption.values()) {
            plotOptionsObject.put(showOption.getShortName(), showOptions.get(showOption));
        }
        for (String optionKey : booleanOptions.keySet()) {
            Option <Boolean> option = booleanOptions.get(optionKey);
            plotOptionsObject.put(option.getName(), option.getValue());
        }
        for (String optionKey : stringOptions.keySet()) {
            Option <String> option = stringOptions.get(optionKey);
            plotOptionsObject.put(option.getName(), option.getValue());
        }
        return plotOptionsObject;
    }
    
    /**
     * 
     * Sends the current plot options towards the JavaScript code.
     * 
     */
    public void sendPlotOptions() {
        JsonObject plotOptions = getPlotOptions();
        //System.out.println("plotOptionsObject: " + plotOptionsObject.toJson());
        for (SNPPlot chart : new SNPPlot[] {femaleChart, maleChart}) {
            chart.sendPlotOptions(plotOptions);
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
        SNP currentSNP = getController().getActiveSNP();
        if (currentSNP == null) {
            return;
        }
        if (locusZoomWindow == null) {
            locusZoom = new LocusZoom();
            locusZoom.addValueChangeListener(new LocusZoom.ValueChangeListener() {
                @Override
                public void valueChange() {
                    String clickedPosition = locusZoom.getClickedSNP();
                    System.out.println("Data received in SNP plot box: " + clickedPosition);
                    searchSNP(currentSNP.getChromosome() + ":" + clickedPosition);
                }
            });
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
    
    /**
     * Opens the window for the LiteMol visualization.
     * 
     */
    private void openLiteMolWindow() {
        SNP currentSNP = getController().getActiveSNP();
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
            
            String exampleEntryID = "1aoi"; // "1a4y", 3J6R
            entryIDInputField.setValue(exampleEntryID);
            setEntryID(exampleEntryID);
            
            window.addComponent(entryIDInputField, 47, 0, 49, 2);
            window.addComponent(submitButton, 50, 0, 52, 2);
            window.setComponentAlignment(submitButton, Alignment.BOTTOM_CENTER);
            window.setComponentAlignment(entryIDInputField, Alignment.BOTTOM_CENTER);
            window.addComponent(liteMol, 0, 4, 99, 99);
            liteMolWindow = new Window("LiteMol visualization", window);
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
            getComponent().getUI().addWindow(window);
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
    @Override
    public AbstractComponent getComponent() {
        return box;
    }
    
    @Override
    public void resizePlots() {
        femaleChart.resize();
        maleChart.resize();
    }
    
    @Override
    public void handOver() {
        System.out.println("Handed over to SNP level.");
        System.out.println("Current active SNP: " + getController().getActiveSNP());
        updateSNP();
    }
    
}
