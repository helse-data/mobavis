package com.plotting;

import com.files.PercentileReader;
import com.plotly.BarPlot;
import com.plotly.OverlayPlot;
import com.plotly.PlotlyJs;
import com.utils.Age;
import com.utils.Alphanumerical;
import com.utils.Constants;
import com.utils.UtilFunctions;
import com.utils.JsonHelper;
import com.utils.Variable;
import com.vaadin.data.HasValue;
import com.vaadin.data.HasValue.ValueChangeEvent;
import com.vaadin.event.selection.SelectionEvent;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.server.Page;
import com.vaadin.server.Sizeable;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.CheckBoxGroup;
import com.vaadin.ui.Component;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.NativeButton;
import com.vaadin.ui.NativeSelect;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Panel;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.TextField;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.ValoTheme;
import elemental.json.Json;
import elemental.json.JsonBoolean;
import elemental.json.JsonObject;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;


/**
 *
 * @author Christoffer Hjeltnes Støle
 */
public class OverlayPlotBox {
    JsonObject userData;
    Map <String, JsonObject> currentDataObjects = new HashMap();
    JsonBoolean active;
    
    boolean percentilesShown = false;
    
    Map <String, OverlayPlot> longitudinalChartMap;
    Map <String, BarPlot> nonLongitudinalChartMap;
    
    JsonHelper jsonHelper;
    
    PercentileReader percentileReader;
    
    GridLayout box = new GridLayout(100, 100);
    HorizontalLayout topBox = new HorizontalLayout();
    GridLayout optionsBox = new GridLayout(1, 1);
    GridLayout optionsGrid = new GridLayout(100, 100);
    HorizontalLayout formBox = new HorizontalLayout();
    
    Map <String, Label> formLabelsSideBar = new HashMap();
    Map <String, Label> formLabelsTabSheet = new HashMap();
    
    NativeSelect <String> dataPresentationSelector;
    List <String> dataPresentationOptions = new ArrayList();
    
    NativeSelect <String> sexSelector;
    String sex;
    
    Map <String, NativeSelect <Variable>> conditionCategorySelectors = new HashMap();
    Map <String, NativeSelect <Alphanumerical>> conditionSelectors = new HashMap();
    
    Map <String, NativeSelect <Variable>> phenotypeSelectors = new HashMap();
    List <Variable> phenotypeOptions = new ArrayList();
    
    Map <String, Button> enterDataButtons = new HashMap();
    
    boolean selectionRebound = false;
    
    Map <String, TextField> inputFieldsAgeSideBar = new HashMap();
    Map <String, Map <String, TextField>> inputFieldsSideBar = new HashMap();
    Map <String, TextField> inputFieldsAgeTabSheet;
    Map <String, Map <String, TextField>> inputFieldsTabSheet;
    
    TabSheet dataTabSheet;
    Window inputFormWindow;
    PlotDataWindow plotDataWindow = new PlotDataWindow();
    
    //Map <String, Integer> ageToIndex = new HashMap();
    
    CheckBoxGroup <String> showOptionsSelector;
    
    Set <String> showOptions = new HashSet();
    Set <String> previousShowOptions = new HashSet();
    
    HorizontalLayout plotBox = new HorizontalLayout();
    Component hiddenComponent;
    
    Set <Variable> longitudinalPhenotypes = new HashSet();
    Set <Variable> nonLongitudinalPhenotypes = new HashSet();
    
    Map <String, Variable> phenotypeMap = new HashMap();    
    Map <Variable, List <String>> storedUserData = new HashMap();
    List <Age> storedAges = new ArrayList();
    
    UtilFunctions utilFunctions = new UtilFunctions();
    Age ageUtils = new Age();
    Constants constants = new Constants();    
    
    List <Alphanumerical> percentiles;
    
    JsonObject metaData = Json.createObject();
    
    Alphanumerical NONE_ALPHANUMERICAL = new Alphanumerical("[none]");
    Variable NULL_VARIABLE = new Variable(null);
    
    int FULL_PLOT_HEIGHT = 83;
    int FULL_PLOT_WIDTH = 83;
    
    public OverlayPlotBox() {
        jsonHelper = new JsonHelper();
        
        for (String ageString : constants.getAges()) {
            storedAges.add(new Age(ageString));
        }
     
        longitudinalChartMap = new HashMap();
        nonLongitudinalChartMap = new HashMap();
        
        longitudinalChartMap.put("1", null);
        longitudinalChartMap.put("2", null);
        
        nonLongitudinalChartMap.put("1", null);
        nonLongitudinalChartMap.put("2", null);
        
        int plotStartY = 6;
        box.addComponent(topBox, 1, 0, 90, plotStartY-1);
        box.addComponent(plotBox, 1, plotStartY, 70, 99);
        box.addComponent(optionsBox, 72, plotStartY+1, 99, 99);

        // select phenotype, data presentation etc.
        dataPresentationOptions.addAll(Arrays.asList(new String[]{
            "one plot", "two plots"}));//, "parameterisation"}));
        dataPresentationSelector = new NativeSelect("Data presentation");
        dataPresentationSelector.setIcon(VaadinIcons.DESKTOP);
        dataPresentationSelector.setItems(dataPresentationOptions);
        dataPresentationSelector.setEmptySelectionAllowed(false);
        //dataPresentationSelector.setEnabled(false);
        dataPresentationSelector.addValueChangeListener(event -> selectDataPresentation(String.valueOf(event.getValue())));
        topBox.addComponent(dataPresentationSelector);  
        
        sexSelector = new NativeSelect("Sex");
        //sexSelector = new NativeSelect(VaadinIcons.FEMALE.getHtml() + VaadinIcons.MALE.getHtml() + "Sex");
        //sexSelector.setCaptionAsHtml(true);
        sexSelector.setItems(Arrays.asList(new String[] {"female", "male"}));
        sexSelector.setEmptySelectionAllowed(false);
        sexSelector.addValueChangeListener(event -> selectSex(String.valueOf(event.getValue())));
        topBox.addComponent(sexSelector);
        
        
        // conditions start
        
        conditionCategorySelectors.put("1", new NativeSelect("Condition category"));
        conditionCategorySelectors.put("2", new NativeSelect("Condition category"));
        
        for (String plotNumber : conditionCategorySelectors.keySet()) {
            NativeSelect conditionCategorySelector = conditionCategorySelectors.get(plotNumber);
            conditionCategorySelector.setWidth(100, Sizeable.Unit.PERCENTAGE);;
            conditionCategorySelector.setIcon(VaadinIcons.CHART_LINE);
            //conditionSelector.setItems(Arrays.asList(new String[] {"none"}));
            conditionCategorySelector.setEmptySelectionAllowed(false);
            conditionCategorySelector.addValueChangeListener(event -> selectConditionCategory(plotNumber, event));
            //topBox.addComponent(conditionCategorySelector);
        }
        
        conditionSelectors.put("1", new NativeSelect("Condition"));
        conditionSelectors.put("2", new NativeSelect("Condition"));
        
        for (String plotNumber : conditionSelectors.keySet()) {
            //topBox.addComponent(conditionCategorySelectors.get(plotNumber));
            NativeSelect conditionSelector = conditionSelectors.get(plotNumber);
            conditionSelector.setWidth(100, Sizeable.Unit.PERCENTAGE);;
            //conditionSelector.setIcon(VaadinIcons.CHART_LINE);
            //conditionSelector.setItems(Arrays.asList(new String[] {"none"}));
            conditionSelector.setEmptySelectionAllowed(false);
            conditionSelector.addValueChangeListener(event -> selectCondition(plotNumber, event));
            //topBox.addComponent(conditionSelector);
        }
        
        // conditions end
        
        
        // read the summary statistics from file        
        percentileReader = new PercentileReader();
        percentiles = percentileReader.getPercentiles();
        //percentileReader.readConditionalData();
        percentileReader.getLongitudinalPhenotypes().forEach(
                variableString -> longitudinalPhenotypes.add(new Variable(variableString, true)));
        phenotypeOptions.addAll(longitudinalPhenotypes);

        List <Variable> nonLongitudinalPhenotypeList = new ArrayList();
        percentileReader.getNonLongitudinalPhenotypes().forEach(variableString -> nonLongitudinalPhenotypes.add(new Variable(variableString, false)));
        nonLongitudinalPhenotypeList.addAll(nonLongitudinalPhenotypes);
        Collections.sort(nonLongitudinalPhenotypeList);
        phenotypeOptions.addAll(nonLongitudinalPhenotypeList);
        
        // phenotype selector
        phenotypeSelectors.put("1", new NativeSelect("Phenotype"));
        phenotypeSelectors.put("2", new NativeSelect("Phenotype"));
        
        for (String plotNumber : phenotypeSelectors.keySet()) {
            NativeSelect phenotypeSelector = phenotypeSelectors.get(plotNumber);
            phenotypeSelector.setWidth(100, Sizeable.Unit.PERCENTAGE);
            phenotypeSelector.setItems(phenotypeOptions);            
            phenotypeSelector.setIcon(VaadinIcons.CLIPBOARD_PULSE);
            phenotypeSelector.setEmptySelectionAllowed(false);
            phenotypeSelector.addValueChangeListener(event -> selectPhenotype(plotNumber, event));
            //phenotypeSelector.setSizeFull();
            Button enterDataButton = new Button("Enter own data");
            enterDataButton.addStyleName("highlight-blue");
            enterDataButton.addClickListener(event -> showDataFormWindow(plotNumber));
            enterDataButtons.put(plotNumber, enterDataButton);
            topBox.addComponent(phenotypeSelector);  
            topBox.addComponent(conditionCategorySelectors.get(plotNumber));
            topBox.addComponent(conditionSelectors.get(plotNumber));
            if (plotNumber.equals("2")) {
                topBox.addComponent(enterDataButton);
                topBox.setComponentAlignment(enterDataButton, Alignment.BOTTOM_CENTER);
            }            
        }
        
        Button viewPlotDataButton = new Button("View plot data");
        topBox.addComponent(viewPlotDataButton);
        topBox.setComponentAlignment(viewPlotDataButton, Alignment.BOTTOM_CENTER);
        viewPlotDataButton.addClickListener(event -> viewPlotData());
        
        // show or hide percentiles
        showOptions.add("percentiles");        
        showOptionsSelector = new CheckBoxGroup("Show");
        previousShowOptions = showOptions;
        showOptionsSelector.addSelectionListener(event -> changeShowSettings(event));
        
        // input        
        formBox = new HorizontalLayout();

        Panel formPanel = new Panel();
        formPanel.setContent(formBox);
        
        optionsGrid.addComponent(showOptionsSelector, 0, 0, 99, 5);
        //optionsGrid.addComponent(formPanel, 0, 6, 97, 97);
        //optionsGrid.setComponentAlignment(formPanel, Alignment.TOP_CENTER); 
        
        optionsBox.addComponent(optionsGrid);
        
        
        FormLayout formAge = createAgeForm(inputFieldsAgeSideBar);
        
        //formAgeLabel.setSizeFull();
        formBox.addComponent(formAge);
        
        for (String plotNumber : new String[] {"1", "2"}) {
            Label formLabel = new Label();
            formLabelsSideBar.put(plotNumber, formLabel);
            inputFieldsSideBar.put(plotNumber, new HashMap());
            FormLayout phenotypeForm = createPhenotypeForm(plotNumber, formLabel, inputFieldsSideBar);
            phenotypeForm.setSizeFull();
            formBox.addComponent(phenotypeForm);       
        }

        jsonHelper.putAlphanumerical(metaData, "percentiles", percentiles);
                //, Arrays.asList(
                //new String[] {"1", "5", "10", "25", "50", "75", "90", "95", "99"}));
          
        // chart 1
        // set example data
        List <String> height = Arrays.asList(new String[] {"58.6", null, "79.1", "79.3", "79.4", "84.2",
            "103.7", "110.8", "125.7", "140.2", "160.0", "162.0"});
        storedUserData.put(new Variable("height", true), height);        
        
        // chart 2
        // set example data
        List <String> weight = Arrays.asList(new String[] {"4.4", "7.4", "8.3", "10.6",
        "11.6", "11.9", "15.2", "14.6", "17.3", "23.6", "24.0", "33.0"});  
        storedUserData.put(new Variable("weight", true), weight);
        
        // set default values
        dataPresentationSelector.setValue(dataPresentationOptions.get(1));
        sexSelector.setValue("female");
        phenotypeSelectors.get("1").setValue(new Variable("height", true));
        updatePhenotypeData("1", "phenotype");
        phenotypeSelectors.get("2").setValue(new Variable("weight", true));
        updatePhenotypeData("2", "phenotype");
        conditionCategorySelectors.get("1").setValue(NULL_VARIABLE);
        conditionCategorySelectors.get("2").setValue(NULL_VARIABLE);
               
        
        showOptionsSelector.setItems(showOptions);
        showOptionsSelector.updateSelection(showOptions, new HashSet());
        
        // set sizes to full
        box.setSizeFull();
        topBox.setSizeFull();
        optionsGrid.setSizeFull();
        optionsBox.setSizeFull();
        showOptionsSelector.setSizeFull();
        formBox.setSizeFull();
        formPanel.setSizeFull();
        plotBox.setSizeFull();

        formAge.setSizeFull();
    }
          
    private FormLayout createAgeForm(Map inputFieldsMap) {
        FormLayout ageForm = new FormLayout();
        
        Label formAgeLabel = new Label("age");
        ageForm.addComponent(formAgeLabel);
        ageForm.setComponentAlignment(formAgeLabel, Alignment.MIDDLE_CENTER);
        
        for (int i = 0; i < storedAges.size(); i++) {
            final String index = Integer.toString(i);
            TextField currentAgeInputField = new TextField();
            currentAgeInputField.setValue(storedAges.get(i).getDescription());
            currentAgeInputField.addValueChangeListener(event -> updateAge(index, event));
            currentAgeInputField.setSizeFull();
            ageForm.addComponent(currentAgeInputField);
            inputFieldsMap.put(index, currentAgeInputField);
        }
        
        Button clearingButtonAge = new Button("Clear");
        clearingButtonAge.addClickListener(event -> clearDataPoints("age"));
        ageForm.addComponent(clearingButtonAge);
        ageForm.setComponentAlignment(clearingButtonAge, Alignment.MIDDLE_CENTER);

        return ageForm;
    }
    
    private FormLayout createPhenotypeForm(String plotNumber, Label label, Map <String, Map <String, TextField>> inputFieldsMap) {
        FormLayout phenotypeForm = new FormLayout();
        inputFieldsMap.put(plotNumber, new HashMap());
        phenotypeForm.addComponent(label);
        phenotypeForm.setComponentAlignment(label, Alignment.MIDDLE_RIGHT);
        
        for (int i = 0; i < storedAges.size(); i++) {
            final String index = Integer.toString(i);

            TextField currentInputField = new TextField();
            currentInputField.setMaxLength(7);
            currentInputField.addValueChangeListener(event -> updateDataPoint(event, index, plotNumber));
            currentInputField.setSizeFull();

            phenotypeForm.addComponent(currentInputField);
            phenotypeForm.setComponentAlignment(currentInputField, Alignment.MIDDLE_RIGHT);

            inputFieldsMap.get(plotNumber).put(index, currentInputField);
        }
        
        // buttons for clearing data
        Button clearingButton = new Button("Clear");
        clearingButton.addClickListener(event -> clearDataPoints(plotNumber));
        phenotypeForm.addComponent(clearingButton);
        phenotypeForm.setComponentAlignment(clearingButton, Alignment.MIDDLE_RIGHT);

        return phenotypeForm;
    }
    
    // create a JSON object from a list of strings
    private JsonObject createJson(String phenotype, List <String> data) {
        JsonObject jsonObject = Json.createObject(); // create a new object each time
        jsonObject.put("phenotype", phenotype);         
        jsonHelper.put(jsonObject, "data", data);
        return jsonObject;
    }
    
    public void setUserData(String plotNumber, Variable phenotype, List <String> enteredData) {
        System.out.println("setting user data");
        storedUserData.put(phenotype, enteredData);
        userData = createJson(phenotype.getDisplayName(), enteredData);
        if (longitudinalChartMap.get(plotNumber) != null) {
            longitudinalChartMap.get(plotNumber).sendUserData(userData);
        }
    }
    
    public void setPlotData(String plotNumber, Variable phenotype, JsonObject dataObject) {
        if (phenotype == null) {
            return;
        }
        
        plotDataWindow.setTab(plotNumber, dataObject.toJson(), phenotype.getDisplayName());
        int thisComponentNumber = Integer.parseInt(plotNumber) - 1;
        Component chartRef = null;
        if (phenotype.isLongitudinal()) {
            OverlayPlot chart;
            if (longitudinalChartMap.get(plotNumber) == null) {
                chart = new OverlayPlot(phenotype.getDisplayName());
                longitudinalChartMap.put(plotNumber, chart);
                chart.setSizeFull();
                chart.sendMetaData(metaData);
            }
            chart = longitudinalChartMap.get(plotNumber);
            chartRef = chart;
            chart.sendPercentileData(dataObject);
            System.out.println("percentile data: " + dataObject);
        }
        else {
            BarPlot chart;
            String yAxisLabel = phenotype.getDisplayName().substring(phenotype.getDisplayName().indexOf(": ") + 1);
            if (nonLongitudinalChartMap.get(plotNumber) == null) { // TODO: awaiting bug fix
                chart = new BarPlot();
                nonLongitudinalChartMap.put(plotNumber, chart);
                JsonObject setupData = Json.createObject();
                jsonHelper.put(setupData, "x", percentileReader.getPercentileTextList());
                setupData.put("colour", "rgba(35, 100, 35, 0.95)");
                setupData.put("x-axis", "percentile");
                setupData.put("y-axis", yAxisLabel);
                setupData.put("title", phenotype.getDisplayName());
                chart.setUp(setupData);
                JsonObject sizeObject = Json.createObject();
                sizeObject.put("height", "83vh");
                sizeObject.put("width", "35vw");                
                chart.setSize(sizeObject);
            }
            chart = nonLongitudinalChartMap.get(plotNumber);
            chart.setSizeFull();
            chartRef = chart;
            JsonObject layout = Json.createObject();
            layout.put("y-axis", yAxisLabel);
            layout.put("title", phenotype + " [n = " + dataObject.getObject("data").getString("N") + "]");
            dataObject.put("layout", layout);
            chart.sendData(dataObject);
        }
        
        
        //map.put(thisComponentNumber, chartRef);
        //System.out.println("number: " + plotNumber);
        //System.out.println("count: " + plotBox.getComponentCount());
        if (plotBox.getComponentCount() >= Integer.parseInt(plotNumber)) {
            //map.put(otherComponentNumber, chartRef);
            plotBox.removeComponent(plotBox.getComponent(thisComponentNumber));            
        }        
        plotBox.addComponent(chartRef, thisComponentNumber);
        
    }
    
    public void setPhenotype(String setPlotNumber, Variable phenotype) {
        if (phenotype == null) {// || (dataPresentationSelector.getValue().equals(dataPresentationOptions.get(0)) && plotNumber.equals("2"))) {
            return;
        }
        
        if (storedUserData.containsKey(phenotype)) {
            //setFormData(plotNumber, storedUserData.get(phenotype), inputFieldsSideBar);
            if (inputFieldsTabSheet != null) {
                setFormData(setPlotNumber, storedUserData.get(phenotype), inputFieldsTabSheet);
            }
            setUserData(setPlotNumber, phenotype, storedUserData.get(phenotype));
        }
        else {
            List <String> nullList = createNullList(storedAges.size());
            setFormData(setPlotNumber, nullList, inputFieldsSideBar);
            if (inputFieldsTabSheet != null) {
                setFormData(setPlotNumber, nullList, inputFieldsTabSheet);
            }
            setUserData(setPlotNumber, phenotype, nullList);
        }
        
        formLabelsSideBar.get(setPlotNumber).setValue(phenotype.getDisplayName());
        if (dataTabSheet != null) {// update tab captions
            dataTabSheet.getTab(Integer.parseInt(setPlotNumber)-1).setCaption(phenotype.getDisplayName()); 
            formLabelsTabSheet.get(setPlotNumber).setValue(phenotype.getDisplayName());
        }        
        phenotypeMap.put(setPlotNumber, phenotype);
        
        
        // conditions start        
        Variable currentConditionCategory = conditionCategorySelectors.get(setPlotNumber).getValue();        
        Set <Variable> conditionCategories = percentileReader.getConditionCategories(phenotype);
        if (conditionCategories != null) {
            List <Variable> conditionCategoryList = new ArrayList();
            conditionCategoryList.addAll(conditionCategories);
            Collections.sort(conditionCategoryList);
            conditionCategoryList.add(0, NULL_VARIABLE);
            conditionCategorySelectors.get(setPlotNumber).setItems(conditionCategoryList);
            System.out.println("current condition category: " + currentConditionCategory + ", retained: " + conditionCategoryList.contains(currentConditionCategory));
            if (conditionCategoryList.contains(currentConditionCategory)) { // retain the condition category if possible
                conditionCategorySelectors.get(setPlotNumber).setValue(currentConditionCategory);
            }
            else {
                conditionCategorySelectors.get(setPlotNumber).setValue(NULL_VARIABLE);
            }
        }
        //System.out.println("condition categories: " + conditionCategories);

        // conditions end      
        
    }
    
    
    private void updatePhenotypeData(String plotNumber, String updatedOption) { // TODO: evaluate
        Variable currentPhenotype = phenotypeSelectors.get(plotNumber).getValue();
        if (currentPhenotype == null) {
            return;
        }
        Variable currentConditionCategory = conditionCategorySelectors.get(plotNumber).getValue();
        Alphanumerical currentCondition = conditionSelectors.get(plotNumber).getValue();
        
        System.out.println("updated option: " + updatedOption);
        System.out.println("Current selection:\n\tphenotype: " + currentPhenotype + "\n\tcondition category: " + currentConditionCategory
        + "\n\tcondition: " + currentCondition);
       
        JsonObject dataObject = null;
        
        if (currentCondition != null && !currentCondition.equals(NONE_ALPHANUMERICAL)) {
            dataObject = percentileReader.getConditionalPhenotypeData(currentPhenotype, sex, currentConditionCategory, currentCondition);
        }
        else {
            dataObject = percentileReader.getNonConditionalPhenotypeData(currentPhenotype, sex);
        }
//        if (updatedOption.equals("condition")) {
//            if (!(currentCondition == null || currentCondition.equals(NONE_ALPHANUMERICAL))) {
//                dataObject = percentileReader.getConditionalPhenotypeData(currentPhenotype, sex, currentConditionCategory, currentCondition);
//            }
//            else {
//                dataObject =  percentileReader.getNonConditionalPhenotypeData(currentPhenotype, sex);
//            }
//            setPlotData(plotNumber, currentPhenotype, dataObject);
//        }
//        else if (updatedOption.equals("category")) {
//            //setConditionCategory(plotNumber, currentConditionCategory);
//            if (currentConditionCategory == null || currentConditionCategory.equals(NULL_VARIABLE)) {
//                dataObject = percentileReader.getNonConditionalPhenotypeData(currentPhenotype, sex);
//                setPlotData(plotNumber, currentPhenotype, dataObject);
//            }
////            if (!(currentCondition == null || currentCondition.equals(NONE_ALPHANUMERICAL))) {
////                dataObject = percentileReader.getConditionalPhenotypeData(currentPhenotype, sex, currentConditionCategory, currentCondition);
////            }
//            
//        }
//        else if (updatedOption.equals("phenotype")){
//            if (currentConditionCategory == null || currentConditionCategory.equals(NULL_VARIABLE) || currentCondition == null || currentCondition.equals(NONE_ALPHANUMERICAL)) {
//                dataObject = percentileReader.getNonConditionalPhenotypeData(currentPhenotype, sex);
//                setPlotData(plotNumber, currentPhenotype, dataObject);
//            }
//            setPhenotype(plotNumber, currentPhenotype);
//        }
        
        if (dataObject != null) {
            System.out.println("data object: " + dataObject.toJson());
            setPlotData(plotNumber, currentPhenotype, dataObject);
        }
        if (updatedOption.equals("category")) {
            setConditionCategory(plotNumber, currentConditionCategory);
        }
        else if (updatedOption.equals("phenotype")){
            setPhenotype(plotNumber, currentPhenotype);
        }
    }
    
    private void selectPhenotype(String plotNumber, ValueChangeEvent event) {
        Variable phenotype = (Variable) event.getValue();
        
        if (phenotype.getDisplayName().equals("null") || phenotype.equals(phenotypeMap.get(plotNumber)) || selectionRebound){
            selectionRebound = false;
            return;
        }
        if (phenotypeMap.containsValue(phenotype)) {
            Notification notification = new Notification("This phenotype has already been selected for another plot.", Notification.Type.HUMANIZED_MESSAGE);
            notification.setDelayMsec(5000);
            notification.show(Page.getCurrent());
            selectionRebound = true;
            phenotypeSelectors.get(plotNumber).setValue(phenotypeMap.get(plotNumber));
            return;            
        }
        
        if (event.isUserOriginated()) {
            updatePhenotypeData(plotNumber, "phenotype");
        }
        System.out.println("phenotype selected: " + phenotype + ", is longitudinal: " + phenotype.isLongitudinal());
        //setPhenotype(plotNumber, option);
    }
    
    
    private void setConditionCategory(String plotNumber, Variable conditionCategory) {
        if (conditionCategory == null) {
            return;
        }
        System.out.println("Condition category set: " + conditionCategory + " (" + plotNumber + ")");
        // conditions start
        if (!conditionCategory.equals(NULL_VARIABLE)) {
            Alphanumerical currentCondition = conditionSelectors.get(plotNumber).getValue();
            List <Alphanumerical> conditions = percentileReader.getConditions(phenotypeSelectors.get(plotNumber).getValue(), conditionCategory);
            
            if (!conditions.contains(NONE_ALPHANUMERICAL)) { // TODO: better way?
                Collections.sort(conditions);
                conditions.add(0, NONE_ALPHANUMERICAL);
            }            
            conditionSelectors.get(plotNumber).setItems(conditions);
            System.out.println("current condition: " + currentCondition + ", retained: " + conditions.contains(currentCondition));
            if (conditions.contains(currentCondition)) { // retain the condition if possible
                conditionSelectors.get(plotNumber).setValue(currentCondition);
            }
            else {
                conditionSelectors.get(plotNumber).setValue(NONE_ALPHANUMERICAL);
            }
        }
        else {
            conditionSelectors.get(plotNumber).setItems(new HashSet());
        }
        
        
        // conditions end
    }
    
    private void selectConditionCategory(String plotNumber, ValueChangeEvent event) {
        Variable conditionCategory = (Variable) event.getValue();
        setConditionCategory(plotNumber, conditionCategory);
        if (!event.isUserOriginated()) {
            return;
        }
        
        System.out.println("Condition category selected: " + conditionCategory.getDisplayName() + " (" + plotNumber + ")");
        
        updatePhenotypeData(plotNumber, "category");
    
        
    }
    
    private void setCondition(String plotNumber, Alphanumerical condition) {
        if (condition == null) {
            return;
        }
        System.out.println("Condition set: " + condition.getValue() + " (" + plotNumber + ")");
        Variable phenotype = phenotypeMap.get(plotNumber);
        Variable conditionCategory = conditionCategorySelectors.get(plotNumber).getValue();
        JsonObject object = null;
        updatePhenotypeData(plotNumber, "condition");
    }
    
    private void selectCondition(String plotNumber, ValueChangeEvent event) {
        if (!event.isUserOriginated()) {
            return;
        }
        
        System.out.println("event.getOldValue(): " + event.getOldValue());
        
        //Alphanumerical conditionObject = conditionSelectors.get(plotNumber).getValue();
        //System.out.println("Condition selected: " + condition.getValue() + " (" + plotNumber + ")");
        Alphanumerical oldValue = (Alphanumerical) event.getOldValue();
        Alphanumerical newValue = (Alphanumerical) event.getValue();
        if (!newValue.equals(NONE_ALPHANUMERICAL) || oldValue != null) { // check if the new value is "none" and the previous one null
            setCondition(plotNumber, newValue);
        }
    }
    
    private List <String> createNullList(int length) {
        List <String> nullList = new ArrayList();
        for (int i = 0; i < length; i++) {
            nullList.add(null);
        }
        return nullList;
    }
    
    private void setFormData(String plotNumber, List <String> data, Map <String, Map <String, TextField>> inputFieldMap) {
        for (int i = 0; i < data.size(); i++) {
            String dataPoint = data.get(i);
            
            if (dataPoint != null) {
                //System.out.println("dataPoint: " + dataPoint);
                //System.out.println("inputFieldMap.get(plotNumber): " + inputFieldMap.get(plotNumber));
                inputFieldMap.get(plotNumber).get(Integer.toString(i)).setValue(dataPoint);
            }
            else {
                inputFieldMap.get(plotNumber).get(Integer.toString(i)).setValue("");
            }          
        }        
    }
    
    private void selectDataPresentation(String option) {
        if (option.equals(dataPresentationOptions.get(0))) {
            hiddenComponent = plotBox.getComponent(1);
            plotBox.removeComponent(hiddenComponent);
            PlotlyJs chart = (PlotlyJs) plotBox.getComponent(0);
            //JsonObject sizeObject = Json.createObject();
            //sizeObject.put("width", Integer.toString(FULL_PLOT_WIDTH));
            //chart.setSize(sizeObject);
        }
        else if (option.equals(dataPresentationOptions.get(1))) {
            if (hiddenComponent != null) {
                plotBox.addComponent(hiddenComponent);
            }            
        }
        else if (option.equals(dataPresentationOptions.get(2))) {
            
        }
    }
    
    private void setSex(String sex) {
        this.sex = sex;
        updatePhenotypeData("1", "sex");
        updatePhenotypeData("2", "sex");
        //if (longitudinalPhenotypes.contains(phenotypeMap.get("1"))) {
            //setPhenotype("1", phenotypeMap.get("1"));
        //}
        //if (longitudinalPhenotypes.contains(phenotypeMap.get("2"))) {
            //setPhenotype("2", phenotypeMap.get("2"));
        //}
    }
    
    private void selectSex(String option) {
        setSex(option);
        sexSelector.setIcon(VaadinIcons.valueOf(option.toUpperCase()));
    }
        
    public void show(String statistic, boolean show) {
        //System.out.println("longitudinalChartMap: " + longitudinalChartMap);
        for (String number : longitudinalChartMap.keySet()) {
            //System.out.println("show: " + show);
            if (longitudinalChartMap.get(number) != null) {
                longitudinalChartMap.get(number).sendShowStatus(show);
            }            
        }         
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
                show("percentiles", percentilesShown);
            }
        }
        previousShowOptions = showOptionsSelector.getValue();
    }
    
    private void clearDataPoints(String attribute) {        
        if (attribute.equals("age")) {
            inputFieldsAgeSideBar.values().forEach(inputField -> {
                inputField.clear();
                inputField.removeStyleName("problem");
            });
            setElementsToNull(storedAges);
            setAges(storedAges);
        }
        else if (attribute.equals("1") || attribute.equals("2")) {
            Variable phenotype = phenotypeMap.get(attribute);
            inputFieldsSideBar.values().forEach(inputField -> {
                inputField.clear();
            });
            setElementsToNull(storedUserData.get(phenotype));
            setUserData(attribute, phenotype, storedUserData.get(phenotype));      
            //parameterisedPlotComponent.setUserData(userDataList.get(0), userDataList.get(1));        
        }      
    }
    
    private void setElementsToNull(List list) {
        for (int i=0; i < list.size(); i++) {
            list.set(i, null);
        }        
    }
    
    private void updateDataPoint(HasValue.ValueChangeEvent<String> event, String index, String plotNumber) {
        //System.out.println("event.isUserOriginated()"); 
        if (!event.isUserOriginated()) {
            return;
        }
        
        String newValue = event.getValue();
        Variable phenotype = phenotypeMap.get(plotNumber);
        
        storedUserData.get(phenotype).set(Integer.parseInt(index), newValue);
        
        setUserData(plotNumber, phenotype, storedUserData.get(phenotype));
        //parameterisedPlotComponent.setUserData(userData.get(0), userData.get(1));   

    }
    
    private void setAges(List <Age>  ages) {
        JsonObject ageObject = Json.createObject();
        JsonObject dataObject = Json.createObject();
        
        List <String> ageDescriptions = new ArrayList();
        List <String> ageValues = new ArrayList();
        
        ages.forEach((age) -> ageDescriptions.add(age.getDescription()));
        ages.forEach((age) -> ageValues.add(age.getAgeInDays()));
        
        jsonHelper.put(dataObject, "descriptions", ageDescriptions);
        jsonHelper.put(dataObject, "values", ageValues);
        ageObject.put("data", dataObject);
        for (String chartNumber : longitudinalChartMap.keySet()) {
            OverlayPlot chart = longitudinalChartMap.get(chartNumber);
            if (chart != null) {
                chart.sendUserAges(ageObject);
            }
        }        
    }
    
    private void setAgeFieldToTroubled(String fieldIndex) {
        storedAges.set(Integer.parseInt(fieldIndex), null);
        inputFieldsAgeSideBar.get(fieldIndex).addStyleName("problem");
    }
    
    private void updateAge(String index, HasValue.ValueChangeEvent<String> event) {
        String ageString = event.getValue();
        String oldValue = event.getOldValue();

        if (!event.isUserOriginated() || ageString.equals(oldValue)) {}
        else if (ageString.startsWith("-")) { // negative ages not allowed
            Notification notification = new Notification("Negative age provided.", Notification.Type.HUMANIZED_MESSAGE);
            notification.setDelayMsec(3000);
            notification.show(Page.getCurrent());
            setAgeFieldToTroubled(index);
        }
        else if (ageUtils.isValidAge(ageString)) {
            Age age = new Age(ageString);
            if (storedAges.contains(age)) {
                Notification notification = new Notification("Data for this age is already set in another input field.", Notification.Type.HUMANIZED_MESSAGE);
                notification.setDelayMsec(3000);
                notification.show(Page.getCurrent());
                setAgeFieldToTroubled(index);
            }        
            storedAges.set(Integer.parseInt(index), age);
            setAges(storedAges);
            inputFieldsAgeSideBar.get(index).removeStyleName("problem");
            inputFieldsAgeSideBar.get(index).setValue(age.getDescription());   
        }
        else {
            Notification notification = new Notification("Valid age formats: birth|x-y day(s)|week(s)|month(s)|year(s)", Notification.Type.HUMANIZED_MESSAGE);
            notification.setDelayMsec(5000);
            notification.show(Page.getCurrent());
            setAgeFieldToTroubled(index);
        }
    }
    
    private void toggleWindowVisibility(Window window) {
        if (!window.isAttached()) { // is the window already open?
            getComponent().getUI().getUI().addWindow(window);
        }
        else{
            window.close();
        }
    }
    
    private void showDataFormWindow(String plotNumber) {
        System.out.println("phenotypeMap: " + phenotypeMap);
        if (dataTabSheet == null) {
            dataTabSheet = new TabSheet();
            inputFieldsTabSheet = new HashMap();
            for (String plotKey : phenotypeMap.keySet()) {            
                Variable phenotype = phenotypeMap.get(plotKey);
                //dataTabSheet.addTab(dataForms.get(thisPlotNumber), phenotypeMap.get(thisPlotNumber).getDisplayName());
                HorizontalLayout tab = new HorizontalLayout();
                inputFieldsAgeTabSheet = new HashMap();
                FormLayout ageForm = createAgeForm(inputFieldsAgeTabSheet); 
                inputFieldsTabSheet.put(plotKey, new HashMap());
                Label phenotypeLabel = new Label(phenotype.getDisplayName());
                formLabelsTabSheet.put(plotKey, phenotypeLabel);
                FormLayout phenotypeForm = createPhenotypeForm(plotKey, phenotypeLabel, inputFieldsTabSheet);
                tab.addComponent(ageForm);
                tab.addComponent(phenotypeForm);
                tab.setCaption(phenotype.getDisplayName());
                dataTabSheet.addTab(tab);
            }
            
            for (String plotKey : phenotypeMap.keySet()) {
                Variable phenotype = phenotypeMap.get(plotKey);
                //System.out.println("storedUserData.get(phenotype): " + storedUserData.get(phenotype) + ", plot number: " + thisPlotNumber);
                setFormData(plotKey, storedUserData.get(phenotype), inputFieldsTabSheet);                
            }
            
            inputFormWindow = new Window("Enter own data");
            inputFormWindow.setContent(dataTabSheet);
            inputFormWindow.setWidth(Math.round(phenotypeMap.keySet().size()*250), Sizeable.Unit.PIXELS);
            inputFormWindow.setHeight(90, Sizeable.Unit.PERCENTAGE);
            inputFormWindow.center();
        }
        toggleWindowVisibility(inputFormWindow);        

        System.out.println("Show data form for plot number " + plotNumber);
    }
    
    private void viewPlotData() {
        Window window = (Window) plotDataWindow.getComponent();
        toggleWindowVisibility(window);
    }
    
    public Component getComponent() {
        return box;
    }
    
}
