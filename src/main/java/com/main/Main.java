package com.main;

import com.plotting.LinePlot;
import com.plotting.ParameterisedCurve;
import com.plotting.PercentilePlot;
import com.byteowls.vaadin.chartjs.ChartJs;
import com.javascript.PlotlyJs;
import com.plotting.SNPPlot;
import com.utils.Alphanumerical;
import com.utils.Constants;
import com.utils.HTML;

import com.vaadin.data.HasValue.ValueChangeEvent;
import com.vaadin.event.selection.SelectionEvent;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.server.Sizeable;
import com.vaadin.shared.ui.ContentMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.CheckBoxGroup;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.NativeSelect;
import com.vaadin.ui.TextField;
import com.vaadin.ui.themes.ValoTheme;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Main {     
    
    MyUI upper;
    
    HTML html = new HTML();
    
    GridLayout mainGrid = new GridLayout(100, 100);
    HorizontalLayout plotGrid = new HorizontalLayout();
    HorizontalLayout SNPGrid = new HorizontalLayout();
    GridLayout topLayout = new GridLayout(3, 1);
    GridLayout middleBox = new GridLayout(100, 100);
    GridLayout plotBox = new GridLayout(1, 1);
    GridLayout optionsBox = new GridLayout(1, 1);
    HorizontalLayout formGrid = new HorizontalLayout();
    GridLayout optionsGrid = new GridLayout(100, 100);
    GridLayout SNPRightGrid = new GridLayout(10, 4);
    
    Component statsPage;
    
    GridLayout percentilePlotGrid;
    
    Label disclaimer;
    Label message;
    
    List <List <Double>> userData = new ArrayList();
    
    Map<String, TextField> inputFieldsAge = new HashMap();
    Map<String, TextField> inputFields1 = new HashMap();
    Map<String, TextField> inputFields2 = new HashMap();
    
    SQLite sqlite = new SQLite();

    SNPPlot snpPlot = new SNPPlot();
    ChartJs chartFemales;
    ChartJs chartMales;
    LinePlot linePlot1;
    LinePlot linePlot2;
    ParameterisedCurve parameterisedCurve;
    ChartJs chartPercentiles;
    PlotlyJs plotlyDemo;
    
    CheckBoxGroup <String> showOptionsSelector;
    CheckBoxGroup <String> SNPShowOptionsSelector;
    HorizontalLayout SNPShowOptionsSelectorBox = new HorizontalLayout();
    
    NativeSelect <String> viewSelector;
    ComboBox <String> SNPInput;
    String currentSNPInputValue;
    boolean SNPInputActive = false;
    NativeSelect <String> attributeSelector;
    
    List <List <Double>> percentileData;
    PercentilePlot percentilePlotterWeight;
    PercentilePlot percentilePlotterHeight;
    PercentilePlot percentilePlotterParameterised;
        
    List <String> viewOptions = new ArrayList();
    List <String> SNPOptions;
    Set <String> showOptions = new HashSet();
    List <String> SNPShowOptions = new ArrayList();
    List <String> attributeOptions = new ArrayList();
    Set <String> previousShowOptions = new HashSet();
    Set <String> previousSNPShowOptions = new HashSet();
    Label SNPInformation = new Label();
    
    String currentView;
    String currentAttribute;
    SNP currentSNP;

    boolean percentilesShown = true;

    boolean mediansShown = true;
    boolean SEMShown = true;
    boolean CIShown = true;
    //boolean nShown = true;
    
    Constants constants = new Constants();
    String[] ages = constants.getAges();
    Map <String, Integer> ageToIndex = new HashMap();
    
    boolean userVersion = false;

    public Main(MyUI upper) {
        this.upper = upper;
        // attribute selector
        attributeOptions.addAll(Arrays.asList(new String[]{
            "height", "weight", "BMI"}));
        attributeSelector = new NativeSelect("Attribute");
        attributeSelector.setItems(attributeOptions);        
        attributeSelector.addValueChangeListener(event -> changeAttribute(String.valueOf(
                event.getValue())));  
        attributeSelector.setIcon(VaadinIcons.CLIPBOARD_PULSE);
        attributeSelector.setEmptySelectionAllowed(false);
                
        // show options for SNP plots
        SNPShowOptions.addAll(Arrays.asList(new String[] {"medians", "SEM", "confidence intervals"}));//, "number of individuals"}));
        SNPShowOptionsSelector = new CheckBoxGroup("Show");
        SNPShowOptionsSelector.setItems(SNPShowOptions);
        
        
        // SNP input
        SNPInput = new ComboBox("SNP");

        SNPInput.addFocusListener(event -> clearSNPInput(true));
        SNPInput.addBlurListener(event -> clearSNPInput(false));
        viewOptions.addAll(Arrays.asList(new String[]{"SNP plots", "SNP statistics",
            "overlay MoBa statistics", "overlay MoBa statistics (parameterisation)"}));
         if (userVersion){
             SNPOptions = new ArrayList(Arrays.asList(new String[]{
            "rs9996", "21_10915988_A_C"}));
        }
        else {
            SNPOptions = new ArrayList(Arrays.asList(new String[]{
            "rs9996", "rs2767486 [big difference]", "rs117845375 [female plunge]", "rs41301756 [female plunge x2]", "21_10915988_A_C", "rs12627379", "rs28720096 [AA-only]", "rs62033413", "rs375583050 [BB-only]",
        "rs147446959", "1_154729900_T_G [large chromosome]"}));
            
            plotlyDemo = new PlotlyJs();
            plotlyDemo.setSizeFull();
            viewOptions.add("plotly.js demonstration");
            //plotly.setData(new String[]{"1", "2", "3", "4"});
            
        }
        
        // rs72970193 (good spread between genotypes)
        // rs17649232 (female below, male above)
        // rs16861872 (male below, female above)
        SNPInput.setItems(SNPOptions);        
        SNPInput.addValueChangeListener(event -> searchSNP(String.valueOf(
                event.getValue())));
        SNPInput.setNewItemHandler(inputString -> addSNP(inputString));
        SNPInput.setIcon(VaadinIcons.CUBES);
        SNPInput.setEmptySelectionAllowed(false);
        
        currentAttribute = "BMI";
        attributeSelector.setValue(currentAttribute);
        viewSelector = new NativeSelect("Functionality");
        //viewSelector.setTextInputAllowed(false);
        SNPInput.setValue("rs9996");        
    }
        
    public void execute() {
        // views
        
        
        // view selector
        
        viewSelector.setItems(viewOptions);        
        viewSelector.addValueChangeListener(event -> changeView(String.valueOf(
                event.getValue())));  
        viewSelector.setIcon(VaadinIcons.CHART_3D);
        viewSelector.setEmptySelectionAllowed(false);

        // top row
        Button homeLink = new Button("Home");
        homeLink.setIcon(VaadinIcons.ARROW_LEFT);
        homeLink.addStyleName(ValoTheme.BUTTON_LINK);
        homeLink.addClickListener(event -> goHome());
        Label title = new Label("MoBa visualisation proof-of-concept");        
        
        // disclaimer
        disclaimer = new Label("NOTE: these are not actual data, and are for illustrative purposes only.");
        
        // show chart options
        showOptions.add("percentiles");        
        showOptionsSelector = new CheckBoxGroup("Show");
        showOptionsSelector.setItems(showOptions);
        showOptionsSelector.updateSelection(showOptions, new HashSet());
        previousShowOptions = showOptions;
        showOptionsSelector.addSelectionListener(event -> changeShowSettings(event));
        
        
        
        
        Set initialSNPShowSettings = new HashSet();
        initialSNPShowSettings.addAll(Arrays.asList(new String[] {"medians", "SEM", "confidence intervals"}));//, "number of individuals"}));
        //initialSNPShowSettings.addAll(Arrays.asList(new String[] {"medians", "number of individuals"}));
        SNPShowOptionsSelector.setValue(initialSNPShowSettings);
        previousSNPShowOptions = SNPShowOptionsSelector.getValue();
        SNPShowOptionsSelector.addSelectionListener(event -> changeSNPShowSettings(event));
        
        // input        
        formGrid = new HorizontalLayout();
        //formGrid.setHeight(100, Unit.PERCENTAGE);
        //formGrid.setWidth(100, Unit.PERCENTAGE);
        
        
        FormLayout formAge = new FormLayout();
        formAge.addComponent(new Label("age"));
        
        FormLayout form1 = new FormLayout();
        //form1.addComponent(showOptionsSelector);
        form1.addComponent(new Label("height"));
        
        FormLayout form2 = new FormLayout();
        form2.addComponent(new Label("weight"));

        formGrid.addComponent(formAge);
        formGrid.addComponent(form1);
        formGrid.addComponent(form2);
        
        //form.setSizeFull();
        

        int index = 0;
        for (String age : ages) {
            ageToIndex.put(age, index);
            
            //Label currLabel = new Label(age, ContentMode.HTML);
            TextField currAge = new TextField();
            currAge.setValue(age);
            
            TextField curr1 = new TextField();
            //curr1.setCaption(age);
            curr1.setMaxLength(7);
            curr1.addValueChangeListener(event -> updateDataPoint(event, age, 1));
            
            TextField curr2 = new TextField();
            curr2.setMaxLength(7);
            curr2.addValueChangeListener(event -> updateDataPoint(event, age, 2));
            
            currAge.setSizeFull();
            curr1.setSizeFull();
            //curr2.setWidth(80, Unit.PERCENTAGE);
            curr2.setSizeFull();
            //formAge.addComponent(currLabel);
            formAge.addComponent(currAge);
            form1.addComponent(curr1);
            form2.addComponent(curr2);
            inputFieldsAge.put(age, currAge);
            inputFields1.put(age, curr1);
            inputFields2.put(age, curr2);
            index++;
        }
        
        // buttons for clearing data
        Button clearingButtonAge = new Button("Clear");
        clearingButtonAge.addClickListener(event -> clearDataPoints("age"));
        formAge.addComponent(clearingButtonAge);        
        Button clearingButton1 = new Button("Clear");
        clearingButton1.addClickListener(event -> clearDataPoints("1"));
        form1.addComponent(clearingButton1);
        Button clearingButton2 = new Button("Clear");
        clearingButton2.addClickListener(event -> clearDataPoints("2"));
        form2.addComponent(clearingButton2);   
        
        // calculate percentiles
        //double[] percentiles = {1, 5, 10, 25, 50, 75, 90, 95, 99};
        //heightPercentiles = new PercentileData(data, percentiles, "height");
        //weightPercentiles = new PercentileData(data, percentiles, "weight");       
        
        // SNP plot
        SNPGrid.addComponent(snpPlot.getChart1());
        SNPGrid.addComponent(snpPlot.getChart2());
        
        // chart 1
        // get data
        List <Double> height = Arrays.asList(new Double[] {58.6, null, 79.1, 79.3, 79.4, 84.2,
            103.7, 110.8, 125.7, 140.2, 160.0, 162.0});                   
       // plot
       linePlot1 = new LinePlot("height");       
       linePlot1.updateDataset(height);
       userData.add(height);
        
        // chart 2
        // get data
        List <Double> weight = Arrays.asList(new Double[] {4.4, 7.4, 8.3, 10.6,
        11.6, 11.9, 15.2, 14.6, 17.3, 23.6, 24.0, 33.0});  
        // plot
        linePlot2 = new LinePlot("weight");
        linePlot2.updateDataset(weight);
        userData.add(weight);
        
        
        // percentile plots
        percentilePlotterHeight = new PercentilePlot("height", linePlot1.getChart(), 1);
        percentilePlotterWeight = new PercentilePlot("weight", linePlot2.getChart(), 1);
        
        
        //chartPercentiles = percentilePlotterHeight.getChart();
        //chartPercentiles.setSizeFull();
        
        // parameterised view       
        parameterisedCurve = new ParameterisedCurve("height", "weight");
        parameterisedCurve.updateDataset(height, weight);
        parameterisedCurve.getChart().setWidth(95, Sizeable.Unit.PERCENTAGE);
        parameterisedCurve.getChart().setHeight(52, Sizeable.Unit.PERCENTAGE);
        //
        
        // set example values
         for (int i = 0; i < ages.length; i++) {
            Double dataPoint1 = height.get(i);            
            Double dataPoint2 = weight.get(i);  
            if (dataPoint1 != null) {
                inputFields1.get(ages[i]).setValue(dataPoint1.toString());
            }
            else {
                inputFields1.get(ages[i]).setValue("");
            }
            if (dataPoint2 != null) {
                inputFields2.get(ages[i]).setValue(dataPoint2.toString());
            }
            else {
                inputFields2.get(ages[i]).setValue("");
            }            
        }

        percentilePlotterParameterised = new PercentilePlot("height", "weight", parameterisedCurve.getChart(), 1);       
        
        // set up main grid      
        plotGrid.addComponent(linePlot1.getChart());
        plotGrid.addComponent(linePlot2.getChart());
        
        optionsGrid.addComponent(showOptionsSelector, 0, 0, 99, 5);
        optionsGrid.addComponent(formGrid, 0, 6, 97, 97);
        optionsGrid.setComponentAlignment(formGrid, Alignment.TOP_CENTER);     
        
        
        SNPShowOptionsSelectorBox.addComponent(SNPShowOptionsSelector);
        SNPRightGrid.addComponent(SNPShowOptionsSelectorBox, 1, 0, 9, 0);
        //SNPRightGrid.addComponent(SNPInformation, 0, 1, 0, 2);
        //SNPOptionsGrid.setComponentAlignment(formGrid, Alignment.TOP_CENTER);
        
        topLayout.addComponent(homeLink);
        topLayout.addComponent(title, 1, 0, 2, 0);
        topLayout.setComponentAlignment(title, Alignment.TOP_LEFT);
        mainGrid.addComponent(topLayout, 0, 0, mainGrid.getColumns()-1, 3);
        
        mainGrid.addComponent(viewSelector, 1, 5, 18, 11);
        mainGrid.addComponent(SNPInput, 19, 5, 37, 11);
        mainGrid.addComponent(attributeSelector, 38, 5, 51, 11);
        //combinedViewGrid.setComponentAlignment(viewSelector, Alignment.TOP_LEFT);
        mainGrid.addComponent(middleBox, 0, 12, mainGrid.getColumns()-1, 95);
        

        mainGrid.addComponent(disclaimer, 0, mainGrid.getRows()-1, 
                mainGrid.getColumns()-1, mainGrid.getRows()-1);
        mainGrid.setComponentAlignment(disclaimer, Alignment.MIDDLE_CENTER);
                       
        // end of setup for main grid

        // set sizes to full
        optionsGrid.setSizeFull();
        SNPRightGrid.setSizeFull();
        SNPShowOptionsSelectorBox.setSizeFull();
        optionsBox.setSizeFull();
        formGrid.setSizeFull();
        showOptionsSelector.setSizeFull();

        formAge.setSizeFull();
        form1.setSizeFull();
        form2.setSizeFull();
        topLayout.setSizeFull();
        middleBox.setSizeFull();
        plotBox.setSizeFull();
        plotGrid.setSizeFull();
        SNPInput.setSizeFull();
        attributeSelector.setSizeFull();
        viewSelector.setSizeFull();
        linePlot1.getChart().setSizeFull();
        linePlot2.getChart().setSizeFull();
        parameterisedCurve.getChart().setSizeFull();
        SNPGrid.setSizeFull();
        snpPlot.getChart1().setSizeFull();
        snpPlot.getChart2().setSizeFull();
        mainGrid.setSizeFull(); 
        
        // done
        //System.out.println(currentView);
        if (userVersion) {
            viewSelector.setValue(viewOptions.get(0));
        }
        else {
            viewSelector.setValue(viewOptions.get(4));
        }
    }
    
    public Component getComponent() {
        return mainGrid;
    }
    
    
    private boolean searchSNP(String option) {
        if (SNPInputActive) {
            return false;
        }
         viewSelector.setEnabled(false);
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
             viewSelector.setEnabled(true);
            return false;
        }
        
       
        attributeSelector.setEnabled(false);
        //System.out.println("disabled");
        //Notification.show("disabled", Notification.Type.TRAY_NOTIFICATION);
        currentSNPInputValue = option;
        SNP snp = sqlite.getSNP(option);
        viewSelector.setEnabled(true);
        attributeSelector.setEnabled(true);
        //Notification.show("enabled", Notification.Type.TRAY_NOTIFICATION);
        //System.out.println("enabled");
        
        String SNPinformationString = "";
        if (snp != null) {
            SNPinformationString = 
                "SNP: " + html.floatRight(html.bold(snp.getID())) + "<br>" +
                "Chromosome: " +  html.floatRight(html.bold(snp.getChromosome()))+ "<br>" +
                "Position: " +  html.floatRight(html.bold(new Alphanumerical(snp.getPosition()).toNonBreakingString())) + "<br>" +
                html.floatRight(" (" + constants.getGenomeBuild() + ")");
        }
        
        
        
        if (snp == null || !snp.hasData()) {
            
            
            SNPGrid.removeAllComponents();
            
            SNPShowOptionsSelectorBox.removeComponent(SNPShowOptionsSelector);            
            attributeSelector.setEnabled(false);
            currentSNP = null;
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
            SNPGrid.addComponent(message);
            
            SNPGrid.setComponentAlignment(message, Alignment.MIDDLE_CENTER);
            SNPInformation = new Label(SNPinformationString, ContentMode.HTML);

            SNPRightGrid.addComponent(SNPInformation, 1, 1, 8, 3);
            return false;
        }
        snpPlot.plotSNP(snp, currentAttribute);
        SNPRightGrid.removeComponent(SNPInformation);
        SNPInformation = new Label(SNPinformationString, ContentMode.HTML);
        SNPInformation.setSizeFull();
        SNPRightGrid.addComponent(SNPInformation, 1, 1, 8, 3);
   
        //
        SNPGrid.removeAllComponents();
        chartFemales = snpPlot.getChart1();
        chartFemales.setSizeFull();
        SNPGrid.addComponent(chartFemales);
        chartMales = snpPlot.getChart2();
        chartMales.setSizeFull();
        SNPGrid.addComponent(chartMales);
        //
        attributeSelector.setEnabled(true);
        SNPShowOptionsSelectorBox.addComponent(SNPShowOptionsSelector);
        currentSNP = snp;
        SNPInputActive = false;
        return true;
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
    
    private void changeAttribute(String option) {
        if (option.equals("null") || option.equals(currentAttribute)){
            return;
        }        
        
        snpPlot.plotSNP(currentSNP, option);
        //
        SNPGrid.removeAllComponents();
        chartFemales = snpPlot.getChart1();
        chartFemales.setSizeFull();
        SNPGrid.addComponent(chartFemales);
        chartMales = snpPlot.getChart2();
        chartMales.setSizeFull();
        SNPGrid.addComponent(chartMales);
        //
        currentAttribute = option;
        
    }
    
    private void changeView(String option) {
        if (option.equals("null") || option.equals(currentView)){
            return;
        }
        
        if (option.equals(viewOptions.get(0))) {            
            middleBox.removeAllComponents();
            middleBox.addComponent(SNPGrid, 0, 0, 79, 99);
            middleBox.addComponent(SNPRightGrid, 80, 0, 99, 99);
            
            disclaimer.setVisible(false);
            SNPInput.setVisible(true);
            showOptionsSelector.setVisible(false);
            attributeSelector.setVisible(true);
            optionsGrid.setVisible(false);
        }
        if (option.equals(viewOptions.get(1))) {
            if (statsPage == null) {
                statsPage = new SummaryPage().getComponent();
            }
            middleBox.removeAllComponents();
            middleBox.addComponent(statsPage, 0, 0, 99, 99);
            
            disclaimer.setVisible(false);
            SNPInput.setVisible(false);
            showOptionsSelector.setVisible(false);
            attributeSelector.setVisible(false);
            optionsGrid.setVisible(false);
        }
        else if (option.equals(viewOptions.get(2))) {
            plotBox.removeAllComponents();
            plotBox.addComponent(plotGrid);
            optionsBox.removeAllComponents();
            optionsBox.addComponent(optionsGrid);
            
            middleBox.removeAllComponents();
            middleBox.addComponent(plotBox, 0, 0, 70, 99);
            middleBox.addComponent(optionsBox, 71, 0, 99, 99);
            
            disclaimer.setVisible(true);
            showOptionsSelector.setVisible(true);
            SNPInput.setVisible(false);
            optionsGrid.setVisible(true);
            attributeSelector.setVisible(false);
        } 
        else if (option.equals(viewOptions.get(3))) {
            plotBox.removeAllComponents();
            plotBox.addComponent(parameterisedCurve.getChart());
            plotBox.setComponentAlignment(parameterisedCurve.getChart(), Alignment.MIDDLE_CENTER);
            optionsBox.removeAllComponents();
            optionsBox.addComponent(optionsGrid);
            
            middleBox.removeAllComponents();
            middleBox.addComponent(plotBox, 0, 0, 70, 99);
            middleBox.addComponent(optionsBox, 71, 0, 99, 99);
            
            disclaimer.setVisible(true);
            showOptionsSelector.setVisible(true);
            SNPInput.setVisible(false);
            optionsGrid.setVisible(true);
            attributeSelector.setVisible(false);
        }
        else if (!userVersion) {
            if (option.equals(viewOptions.get(4))) {
            //plotBox.removeAllComponents();
            //plotBox.addComponent(plotlyDemo);
            //plotBox.setComponentAlignment(plotlyDemo, Alignment.MIDDLE_CENTER);
            //optionsBox.removeAllComponents();
            //optionsBox.addComponent(optionsGrid);
            
            disclaimer.setVisible(false);
            showOptionsSelector.setVisible(true);
            SNPInput.setVisible(false);
            optionsGrid.setVisible(true);
            attributeSelector.setVisible(false); 

            middleBox.removeAllComponents();
            middleBox.addComponent(plotlyDemo, 0, 0, 99, 99);
            //mainGrid.removeAllComponents();
            //mainGrid.addComponent(plotlyDemo);
            //middleBox.addComponent(snpPlot.getChart1(), 0, 0, 99, 99);

            
            //middleBox.addComponent(optionsBox, 71, 0, 99, 99);
            }
        }

        currentView = option;
        
        effectuatePercentiles();
        
    }
    
    
    private void changeSNPShowSettings(SelectionEvent event) {
        //System.out.println("Changed");
        Set<String> currentlySelected = event.getAllSelectedItems();
        
        Set <String> unselected = new HashSet();
        unselected.addAll(previousSNPShowOptions);
        unselected.removeAll(currentlySelected);
        
        Set <String> newlySelected = new HashSet();
        newlySelected.addAll(currentlySelected);
        newlySelected.removeAll(previousSNPShowOptions);
        
        Set <String> changed = new HashSet();
        changed.addAll(newlySelected);
        changed.addAll(unselected);
        
        
//        System.out.println("previously selected: " + previousSNPShowOptions);
//        System.out.println("unselected: " + unselected);
//        System.out.println("newly selected: " + newlySelected);
//        System.out.println("changed: " + changed);
 
        for (String s : changed) {
            if (s.equals("medians")) {
                mediansShown = !mediansShown;
                snpPlot.show(s, mediansShown);
            }
            else if (s.equals("SEM")) {
                SEMShown = !SEMShown;
                snpPlot.show(s, SEMShown);
            }
            else if (s.equals("confidence intervals")) {
                CIShown = !CIShown;
                snpPlot.show(s, CIShown);
            }
//            else if (s.equals("number of individuals")) {
//                nShown = !nShown;
//                snpPlot.show(s, nShown);
//            }
        }
        previousSNPShowOptions = SNPShowOptionsSelector.getValue();
    }
    
    private void changeShowSettings(SelectionEvent event) {
        Set<String> selected = event.getAllSelectedItems();

        Set <String> unselected = new HashSet();
        unselected.addAll(previousShowOptions);
        unselected.removeAll(selected);
        
        Set <String> changed = new HashSet();
        changed.addAll(unselected);
        changed.addAll(showOptionsSelector.getValue());
        
        for (String s : changed) {
            if (s.equals("percentiles")) {
                percentilesShown = !percentilesShown;
                //System.out.println("percentiles shown: " + percentilesShown);
                effectuatePercentiles();
                //LineChartConfig config1 = (LineChartConfig) linePlot1.getChart().getConfig();
                //config1.options().animation().duration(0);
                //linePlot1.getChart().setVisible(false);
                //linePlot1.getChart().setVisible(true);
            }
        }
        previousShowOptions = showOptionsSelector.getValue();
    }
    
    private void effectuatePercentiles() {
        if (currentView.equals(viewOptions.get(2))) {
            percentilePlotterHeight.show(percentilesShown);
            percentilePlotterWeight.show(percentilesShown);
            linePlot1.getChart().refreshData();
            linePlot2.getChart().refreshData();
        }
        if (currentView.equals(viewOptions.get(3))) {                    
            percentilePlotterParameterised.show(percentilesShown);
            parameterisedCurve.getChart().refreshData();
        }
    }
    
    private void clearDataPoints(String attribute) {
        
        if (attribute.equals("age")) {
            inputFieldsAge.values().forEach(inputField -> {
                inputField.clear();
            });
            //linePlot1.updateDataset(new ArrayList());
            //parameterisedCurve.updateDataset(new ArrayList(), userData.get(1));            
        }
        else if (attribute.equals("1")) {
            inputFields1.values().forEach(inputField -> {
                inputField.clear();
            });
            linePlot1.updateDataset(new ArrayList());
            parameterisedCurve.updateDataset(new ArrayList(), userData.get(1));            
        }
        else if (attribute.equals("2")) {
            inputFields2.values().forEach(inputField -> { 
                inputField.clear();
            });            
            linePlot2.updateDataset(new ArrayList());
            parameterisedCurve.updateDataset(new ArrayList(), userData.get(1));
        }
        
    }
    
    private void updateDataPoint(ValueChangeEvent<String> event, String age, int attribute) {
        String newString = event.getValue();
        Double newValue;
        if (!newString.equals("")) {
            newValue = Double.parseDouble(newString);
        }
        else {
            newValue = null;
        }
        
        userData.get(attribute-1).set(ageToIndex.get(age), newValue);
        if (attribute == 1) {
                linePlot1.updateDataset(userData.get(0));
                parameterisedCurve.updateDataset(userData.get(0), userData.get(1));
        }
        else if (attribute == 2) {
                linePlot2.updateDataset(userData.get(1));
                parameterisedCurve.updateDataset(userData.get(0), userData.get(1));
        }
    }
    
    private void clearSNPInput(boolean clear) {
        //Notification.show(Boolean.toString(clear));

        if (clear) {
            //SNPInput.setValue("");
        }
        else {
            SNPInput.setValue(currentSNPInputValue);
        } 
    }
    
    private void goHome() {
        upper.setContent(upper.getLandingPage());
    }
    
}
