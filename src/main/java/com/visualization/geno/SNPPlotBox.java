package com.visualization.geno;

import com.utils.vaadin.PlotDataWindow;
import com.components.LoadingIndicator;
import com.litemol.LiteMol;
import com.locuszoom.LocusZoom;
import com.database.Database;
import com.database.SNPDatabaseEntry;
import com.snp.SNP;
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
import com.visualization.State;
import com.visualization.MoBaVisualizationInterface;

/**
 *
 * @author Christoffer Hjeltnes Støle
 */
public class SNPPlotBox extends GenoView {
    HtmlHelper html = new HtmlHelper();
    
    JsonObject dataObject;
    JsonHelper jsonHelper = new JsonHelper();    
    //JsonObject plotOptionsObject;
        
    //GridLayout box = new GridLayout(100, 100);
    VerticalLayout box = new VerticalLayout();
    HorizontalLayout middleBox = new HorizontalLayout();
    HorizontalLayout plotBox = new HorizontalLayout();
    
    SNPPlot femaleChart;
    SNPPlot maleChart;
    PlotDataWindow plotDataWindow = new PlotDataWindow();
    //GridLayout rightGrid = new GridLayout(10, 10);
    VerticalLayout rightBox = new VerticalLayout();
    //VerticalLayout showOptionsSelectorBox = new VerticalLayout();
    Button morePlotOptionsButton = new Button("More options");
    Window morePlotOptionsWindow;
    HorizontalLayout topBox = new HorizontalLayout();
    CheckBoxGroup <ShowOption> showOptionsSelector;
    Map <String, Option <Boolean>> booleanOptions = new HashMap();
    Map <ShowOption, Boolean> showOptions = new HashMap();
    Map <String, Option <String>> stringOptions = new HashMap();
    Set <ShowOption> previousShowOptions = new HashSet();
    Label message;
    Label SNPInformation = new Label();
    HorizontalLayout SNPinformationContainer = new HorizontalLayout();
    
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
    
    
    public SNPPlotBox(Controller controller) {
        super(controller);
        // phenotype selector
        box.addComponent(getController().getSNPInputField());
        phenotypeOptions.addAll(Arrays.asList(new String[]{
            "height", "weight", "BMI"}));
        //phenotypeSelector = new NativeSelect("Phenotype");
        phenotypeSelector = new NativeSelect();
        phenotypeSelector.setItems(phenotypeOptions);        
        phenotypeSelector.addValueChangeListener(event -> selectPhenotype(String.valueOf(
                event.getValue())));  
        //phenotypeSelector.setIcon(VaadinIcons.CLIPBOARD_PULSE);
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
        
        //for (Option <Boolean> showOption : showOptionList) {
        ///    booleanOptions.put(showOption.getName(), showOption);
        //}
        booleanOptions.put(yaxisToZero.getName(), yaxisToZero);
        
        stringOptions.put(ageSpacing.getName(), ageSpacing);
        
        showOptionsSelector = new CheckBoxGroup("Show");
        showOptionsSelector.setItems(showOptionList);

        morePlotOptionsButton.addClickListener(event -> openMoreShowOptionsWindow());
        
        // create the plots
        JsonObject setup = getPlotOptions();
        femaleChart = new SNPPlot(setup);
        maleChart = new SNPPlot(setup);
        
        //femalePlotBox.addComponents(femaleChart, numberPlotFemale);
        //malePlotBox.addComponents(maleChart, numberPlotMale);
        //plotBox.addComponents(femalePlotBox, malePlotBox);
        plotBox.addComponents(femaleChart, maleChart);
        
//        int n10 = (int) ((box.getColumns()-1)*0.1);        
//        int plotStartY = 7;
//        box.addComponent(topBox, 1, 0, 70, plotStartY-1);
//        box.addComponent(plotBox, 1, plotStartY, 82, 99);
//        box.addComponent(rightGrid, 83, plotStartY+1, 99, 99); 

        box.addComponent(topBox);
        box.setExpandRatio(topBox, 1);
        
        middleBox.addComponent(plotBox);
        middleBox.setExpandRatio(plotBox, 5);
        middleBox.addComponent(rightBox);
        middleBox.setExpandRatio(rightBox, 1);
        
        box.addComponent(middleBox);
        box.setExpandRatio(middleBox, 10);
        
        // default SNP input
        
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
        SNPInput.addValueChangeListener(event -> controller.SNPIDinputChanged());
        SNPInput.setNewItemHandler(inputString -> addSNP(inputString));
        SNPInput.setIcon(VaadinIcons.CUBES);
        SNPInput.setEmptySelectionAllowed(false);
        
        currentPhenotype = "BMI";
        phenotypeSelector.setValue(currentPhenotype);
        
        SNPInput.setValue(SNPOptions.get(0));  
        
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
    
    // enumerator of the show options; includes the default value for each option
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
       
    public void setDatasets(SNP snp, String phenotype) {
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
    
    public boolean searchSNP(String searchString) {
        if (SNPInputActive) {
            return false;
        }
        //viewSelector.setEnabled(false); // TODO: check effects
        SNPInputActive = true;
        //SNPInformation.setCaption("");
        System.out.println("searchSNP(): " + searchString);
        //System.out.println(currentSNP == null);
        SNPInput.setValue(searchString.replaceFirst(" \\[your input\\]$", ""));
        searchString = searchString.replaceFirst(" \\[.*?\\]$", "");
        
        if (searchString.equals("null") || searchString.equals("") || (currentSNP != null && searchString.equals(currentSNP.getID()))
                || searchString.contains("(not found)")) {
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
        currentSNPInputValue = searchString;
        
        SNP snp = null;
        
        SNPIDParser snpIDParser = new SNPIDParser(searchString);
        SNPIDFormat IDFormat = snpIDParser.getIDFormat();
        
        //System.out.println("format: " + snpIDParser.getIDFormat());
        
        if (IDFormat == SNPIDFormat.UNRECOGNIZED) {
            snp = null;
        }        
        else if (IDFormat.equals(SNPIDFormat.CHROMOSOME_POSITION)) { // SNP entered in format chromosome:position
            
            String chromosome = snpIDParser.getChromosome();
            String position = snpIDParser.getPosition();
            
            snp = database.getSNP(chromosome, position);
            
            if (!snp.hasData() && !snp.hasAnnotation()) { // SNP is not found in the database system; try searching the neighbourhood
                Map <String, String> result = database.getNearestSNPs(chromosome, Integer.parseInt(position));
                System.out.println("result: " + result);
                
                if (result.get("result").equals("exact")) {
                    snp = database.getSNP(new SNPIDParser(result.get("0")));
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
            snp = database.getSNP(snpIDParser);
        }

        //viewSelector.setEnabled(true);
        phenotypeSelector.setEnabled(true);
        //Notification.sendPlotOptions("enabled", Notification.Type.TRAY_NOTIFICATION);
        //System.out.println("enabled");
        
        String SNPinformationString = "";
        if (snp != null) {
            
            String locusString = "N/A";
            String locus = snp.getLocus();
            if (locus != null) {
                locusString = html.hoverText(locus, snp.getLocusFullName());
            }
            DbSNPentry dbSNPentry = snp.getDbSNPentry();
            String dbSNPreference = "";
            if (dbSNPentry != null) {
                dbSNPreference = html.floatRight(html.link(dbSNPentry.getEntryURL(), "<br>(dbSNP)"));
            }
            
            //System.out.println("snp: " + snp);
            //System.out.println("snp.getDataBaseEntry(): " + snp.getDataBaseEntry());
            
            
            SNPinformationString = 
                "SNP: " + html.floatRight(html.bold(snp.getDataBaseEntry().getAnnotation().get("Id"))) + "<br>" +
                "Chromosome: " +  html.floatRight(html.bold(snp.getDataBaseEntry().getAnnotation().get("Chromosome"))) + "<br>" +
                "Position: " +  html.floatRight(html.bold(new Alphanumerical(snp.getDataBaseEntry().getAnnotation().get("Position")).toNonBreakingString())) + "<br>" +
                html.floatRight(" (" + constants.getGenomeBuild() + ")") + "<br>" +
                html.hoverText("Locus: ", "As defined by dbSNP") + html.floatRight(html.bold(locusString)) + 
                dbSNPreference;
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
        
        if (snp == null || !snp.hasData() || snp.getDataBaseEntry().getDataObject().keys().length == 0) {          
            plotBox.removeAllComponents();
            
            rightBox.removeComponent(showOptionsSelector);        
            rightBox.removeComponent(morePlotOptionsButton);
            phenotypeSelector.setEnabled(false);
            //currentSNP = null;

            SNPInputActive = false;
            SNPinformationContainer.removeComponent(SNPInformation);
            if (snp != null) {
                SNPinformationString += "<br><br>" + html.bold("No phenotype data could be found.");
                message = new Label(html.bold("No phenotype data could be found for the SNP " + html.italics(searchString) + "."), ContentMode.HTML);
            }
            else {
                
                String text = "The SNP " + html.italics(searchString) + " could not be found.";
                if (IDFormat == SNPIDFormat.UNRECOGNIZED) {
                    text += " The input format was not recognized.";
                }                
                
                message = new Label(html.bold(text), ContentMode.HTML);
                
            }
            
            message.setSizeFull();
            plotBox.addComponent(message);
            
            plotBox.setComponentAlignment(message, Alignment.MIDDLE_CENTER);
            SNPInformation = new Label(SNPinformationString, ContentMode.HTML);

            SNPinformationContainer.addComponent(SNPInformation);
            return false;
        }
        else {
            setDatasets(snp, currentPhenotype);
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
            
            String exampleEntryID = "1aoi"; // "1a4y", 3J6R
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
    public SNP getCurrentSNP() {
        return currentSNP;
    }
    public void setSNP() {
        //TODO: implement
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
        femaleChart.resize();
        maleChart.resize();
    }
}
