package com.plotting;

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
import com.vaadin.event.selection.SelectionEvent;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.server.Page;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.CheckBoxGroup;
import com.vaadin.ui.Component;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.NativeSelect;
import com.vaadin.ui.Notification;
import com.vaadin.ui.TextField;
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
    JsonObject percentileData;
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
    HorizontalLayout formGrid = new HorizontalLayout();
    
    Map <String, Label> formLabels = new HashMap();
    
    NativeSelect <String> dataPresentationSelector;
    List <String> dataPresentationOptions = new ArrayList();
    
    NativeSelect <String> sexSelector;
    String sex;
    
    Map <String, NativeSelect <Variable>> conditionCategorySelectors = new HashMap();
    Map <String, NativeSelect <Alphanumerical>> conditionSelectors = new HashMap();
    
    Map <String, NativeSelect <Variable>> phenotypeSelectors = new HashMap();
    List <Variable> phenotypeOptions = new ArrayList();
    
    boolean selectionRebound = false;
    
    Map <String, TextField> inputFieldsAge = new HashMap();
    Map <String, TextField> inputFields1 = new HashMap();
    Map <String, TextField> inputFields2 = new HashMap();
    
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
        box.addComponent(topBox, 1, 0, 70, plotStartY-1);
        box.addComponent(plotBox, 1, plotStartY, 70, 99);
        box.addComponent(optionsBox, 72, plotStartY+1, 99, 99);

        // select phenotype, data presentation etc.
        dataPresentationOptions.addAll(Arrays.asList(new String[]{
            "one plot", "two plots", "parameterisation"}));
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
            conditionCategorySelector.setSizeFull();
            conditionCategorySelector.setIcon(VaadinIcons.CHART_LINE);
            //conditionSelector.setItems(Arrays.asList(new String[] {"none"}));
            conditionCategorySelector.setEmptySelectionAllowed(false);
            conditionCategorySelector.addValueChangeListener(event -> selectConditionCategory(plotNumber, (Variable) event.getValue()));
            //topBox.addComponent(conditionCategorySelector);
        }
        
        conditionSelectors.put("1", new NativeSelect("Condition"));
        conditionSelectors.put("2", new NativeSelect("Condition"));
        
        for (String plotNumber : conditionSelectors.keySet()) {
            //topBox.addComponent(conditionCategorySelectors.get(plotNumber));
            NativeSelect conditionSelector = conditionSelectors.get(plotNumber);
            conditionSelector.setSizeFull();
            //conditionSelector.setIcon(VaadinIcons.CHART_LINE);
            //conditionSelector.setItems(Arrays.asList(new String[] {"none"}));
            conditionSelector.setEmptySelectionAllowed(false);
            conditionSelector.addValueChangeListener(event -> selectCondition(plotNumber, (Alphanumerical) event.getValue()));
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
            phenotypeSelector.setSizeFull();
            phenotypeSelector.setItems(phenotypeOptions);            
            phenotypeSelector.setIcon(VaadinIcons.CLIPBOARD_PULSE);
            phenotypeSelector.setEmptySelectionAllowed(false);
            phenotypeSelector.addValueChangeListener(event -> selectPhenotype(plotNumber, (Variable) event.getValue()));
            //phenotypeSelector.setSizeFull();
            topBox.addComponent(phenotypeSelector);  
            topBox.addComponent(conditionCategorySelectors.get(plotNumber));
            topBox.addComponent(conditionSelectors.get(plotNumber));
        }
        
        // show or hide percentiles
        showOptions.add("percentiles");        
        showOptionsSelector = new CheckBoxGroup("Show");
        previousShowOptions = showOptions;
        showOptionsSelector.addSelectionListener(event -> changeShowSettings(event));
        
        // input        
        formGrid = new HorizontalLayout();
                     
        optionsGrid.addComponent(showOptionsSelector, 0, 0, 99, 5);
        optionsGrid.addComponent(formGrid, 0, 6, 97, 97);
        optionsGrid.setComponentAlignment(formGrid, Alignment.TOP_CENTER); 
        
        optionsBox.addComponent(optionsGrid);
        
        formLabels.put("1", new Label());
        formLabels.put("2", new Label());
        
        //formLabels.get("1").setSizeFull();
        //formLabels.get("2").setSizeFull();
        
        FormLayout formAge = new FormLayout();
        Label formAgeLabel = new Label("age");
        formAge.addComponent(formAgeLabel);
        formAge.setComponentAlignment(formAgeLabel, Alignment.MIDDLE_CENTER);
        //formAgeLabel.setSizeFull();
        
        FormLayout form1 = new FormLayout();
        form1.addComponent(formLabels.get("1"));
        form1.setComponentAlignment(formLabels.get("1"), Alignment.MIDDLE_RIGHT);
        //formLabels.get("1").setSizeFull();
        
        FormLayout form2 = new FormLayout();
        form2.addComponent(formLabels.get("2"));
        form2.setComponentAlignment(formLabels.get("2"), Alignment.MIDDLE_RIGHT);
        //formLabels.get("2").setSizeFull();

        formGrid.addComponent(formAge);
        formGrid.addComponent(form1);
        formGrid.addComponent(form2);
        
        for (int i = 0; i < storedAges.size(); i++) {
            //ageToIndex.put(age, index);
            final String index = Integer.toString(i);
            //String index = Integer.toString(i);
            //Label currLabel = new Label(age, ContentMode.HTML);
            TextField currAge = new TextField();
            currAge.setValue(storedAges.get(i).getDescription());
            currAge.addValueChangeListener(event -> updateAge(index, event));
            
            TextField curr1 = new TextField();
            //curr1.setCaption(age);
            curr1.setMaxLength(7);
            curr1.addValueChangeListener(event -> updateDataPoint(event, index, "1"));
            
            TextField curr2 = new TextField();
            curr2.setMaxLength(7);
            curr2.addValueChangeListener(event -> updateDataPoint(event, index, "2"));
            
            currAge.setSizeFull();
            curr1.setSizeFull();
            //curr2.setWidth(80, Unit.PERCENTAGE);
            curr2.setSizeFull();
            //formAge.addComponent(currLabel);
            formAge.addComponent(currAge);
            form1.addComponent(curr1);
            form1.setComponentAlignment(curr1, Alignment.MIDDLE_RIGHT);
            form2.addComponent(curr2);
            form2.setComponentAlignment(curr2, Alignment.MIDDLE_RIGHT);
            inputFieldsAge.put(index, currAge);
            inputFields1.put(index, curr1);
            inputFields2.put(index, curr2);
        }
        
        // buttons for clearing data
        Button clearingButtonAge = new Button("Clear");
        clearingButtonAge.addClickListener(event -> clearDataPoints("age"));
        formAge.addComponent(clearingButtonAge);
        formAge.setComponentAlignment(clearingButtonAge, Alignment.MIDDLE_CENTER);
        Button clearingButton1 = new Button("Clear");
        clearingButton1.addClickListener(event -> clearDataPoints("1"));
        form1.addComponent(clearingButton1);
        form1.setComponentAlignment(clearingButton1, Alignment.MIDDLE_RIGHT);
        Button clearingButton2 = new Button("Clear");
        clearingButton2.addClickListener(event -> clearDataPoints("2"));
        form2.addComponent(clearingButton2);
        form2.setComponentAlignment(clearingButton2, Alignment.MIDDLE_RIGHT);
        
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
        conditionCategorySelectors.get("1").setValue(new Variable(null));
        conditionCategorySelectors.get("2").setValue(new Variable(null));
        phenotypeSelectors.get("1").setValue(new Variable("height", true));
        phenotypeSelectors.get("2").setValue(new Variable("weight", true));        
        
        showOptionsSelector.setItems(showOptions);
        showOptionsSelector.updateSelection(showOptions, new HashSet());
        
        // set sizes to full   
        
        box.setSizeFull();
        topBox.setSizeFull();
        optionsGrid.setSizeFull();
        optionsBox.setSizeFull();
        showOptionsSelector.setSizeFull();
        formGrid.setSizeFull();
        plotBox.setSizeFull();

        formAge.setSizeFull();
        form1.setSizeFull();
        form2.setSizeFull();
    }
          
    // create a JSON object from a list of strings
    private JsonObject createJson(String phenotype, List <String> data) {
        JsonObject jsonObject = Json.createObject(); // create a new object each time
        jsonObject.put("phenotype", phenotype);         
        jsonHelper.put(jsonObject, "data", data);
        return jsonObject;
    }
    
    public void setUserData(String plotNumber, Variable phenotype, List <String> enteredData) {
        storedUserData.put(phenotype, enteredData);
        userData = createJson(phenotype.getDisplayName(), enteredData);
        if (longitudinalChartMap.get(plotNumber) != null) {
            longitudinalChartMap.get(plotNumber).sendUserData(userData);
        }
    }
    
    public void setPlotData(String plotNumber, Variable phenotype) {
        if (phenotype == null) {
            return;
        }
        
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
            chart.sendPercentileData(percentileData);
            System.out.println("percentileData: " + percentileData);
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
            layout.put("title", phenotype + " [n = " + percentileData.getObject("data").getString("N") + "]");
            percentileData.put("layout", layout);
            chart.sendData(percentileData);
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
    
    public void setPhenotype(String plotNumber, Variable phenotype) {
        if (phenotype == null) {// || (dataPresentationSelector.getValue().equals(dataPresentationOptions.get(0)) && plotNumber.equals("2"))) {
            return;
        }         
        formLabels.get(plotNumber).setCaption(phenotype.getDisplayName());
        phenotypeMap.put(plotNumber, phenotype);
        
        
        // conditions start        
        Variable currentConditionCategory = conditionCategorySelectors.get(plotNumber).getValue();        
        Set <Variable> conditionCategories = percentileReader.getConditionCategories(phenotype);
        
        List <Variable> conditionCategoryList = new ArrayList();
        conditionCategoryList.addAll(conditionCategories);
        Collections.sort(conditionCategoryList);
        conditionCategoryList.add(0, new Variable(null));
        
        if (conditionCategoryList.contains(currentConditionCategory)) {
            conditionCategorySelectors.get(plotNumber).setValue(currentConditionCategory);
        }
        else {
            conditionCategorySelectors.get(plotNumber).setValue(new Variable(null));
        }
        
        //System.out.println("condition categories: " + conditionCategories);
        
        conditionCategorySelectors.get("1").setItems(conditionCategoryList);
        conditionCategorySelectors.get("2").setItems(conditionCategoryList);
        
        
        
        // conditions end
        
        percentileData = percentileReader.getNonConditionalPhenotypeData(phenotype.getName(), sex);        
        setPlotData(plotNumber, phenotype);
        
        
    }
    
    private void selectPhenotype(String plotNumber, Variable option) {
        if (option.getDisplayName().equals("null") || option.equals(phenotypeMap.get(plotNumber)) || selectionRebound){
            selectionRebound = false;
            return;
        }
        if (phenotypeMap.containsValue(option)) {
            Notification notification = new Notification("This phenotype has already been selected for another plot.", Notification.Type.HUMANIZED_MESSAGE);
            notification.setDelayMsec(5000);
            notification.show(Page.getCurrent());
            selectionRebound = true;
            phenotypeSelectors.get(plotNumber).setValue(phenotypeMap.get(plotNumber));
            return;            
        }
        System.out.println("option: " + option);
        setPhenotype(plotNumber, option);
        if (storedUserData.containsKey(option)) {
            setFormData(plotNumber, storedUserData.get(option));
            setUserData(plotNumber, option, storedUserData.get(option));
        }
        else {
            List <String> nullList = createNullList(storedAges.size());
            setFormData(plotNumber, nullList);
            setUserData(plotNumber, option, nullList);
        }      
        phenotypeMap.put(plotNumber, option);
    }
    
    
    private void setConditionCategory(String plotNumber, Variable conditionCategory) {
        if (conditionCategory == null) {
            return;
        }     
    }
    
    private void selectConditionCategory(String plotNumber, Variable conditionCategory) {
        //Variable conditionCategoryVariable = conditionCategorySelectors.get(plotNumber).getValue();
        System.out.println("Condition category selected: " + conditionCategory.getDisplayName() + " (" + plotNumber + ")");
        setConditionCategory(plotNumber, conditionCategory);
        if (conditionCategory.equals(new Variable(null))) {
            return;
        }
        
        Alphanumerical currentCondition = conditionSelectors.get(plotNumber).getValue();
        // conditions
        List <Alphanumerical> conditions = percentileReader.getConditions(phenotypeSelectors.get(plotNumber).getValue(), conditionCategory);
        
        Collections.sort(conditions);
        conditions.add(0, new Alphanumerical("[none]"));
        conditionSelectors.get("1").setItems(conditions);
        conditionSelectors.get("2").setItems(conditions);
        System.out.println("condition phenotype: " + conditionCategory + ", conditions: " + conditions);
        
        if (conditions.contains(currentCondition)) {
            conditionSelectors.get(plotNumber).setValue(currentCondition);
        }
        else {
            conditionSelectors.get(plotNumber).setValue(conditions.get(0));
        }
        
    }
    
    private void setCondition(String plotNumber, Alphanumerical condition) {
        if (condition == null) {
            return;
        }
        System.out.println("Condition set: " + condition.getValue() + " (" + plotNumber + ")");
        Variable phenotype = phenotypeMap.get(plotNumber);
        Variable conditionCategory = conditionCategorySelectors.get(plotNumber).getValue();
        if (condition.equals(new Alphanumerical("[none]"))) {
            percentileData = percentileReader.getNonConditionalPhenotypeData(phenotype.getName(), sexSelector.getValue());
        }
        else {
            percentileData = percentileReader.getConditionalPhenotypeData(phenotype, sex, conditionCategory, condition);
        }
        
        setPlotData(plotNumber, phenotype);
    }
    
    private void selectCondition(String plotNumber, Alphanumerical condition) {
        //Alphanumerical conditionObject = conditionSelectors.get(plotNumber).getValue();
        //System.out.println("Condition selected: " + condition.getValue() + " (" + plotNumber + ")");
        setCondition(plotNumber, condition);
    }
    
    private List <String> createNullList(int length) {
        List <String> nullList = new ArrayList();
        for (int i = 0; i < length; i++) {
            nullList.add(null);
        }
        return nullList;
    }
    
    private void setFormData(String plotNumber, List <String> data) {
        Map <String, TextField> inputFieldMap = null;
        
        if (plotNumber.equals("1")) {
            inputFieldMap = inputFields1;
        }
        else if (plotNumber.equals("2")) {
            inputFieldMap = inputFields2;
        }

        for (int i = 0; i < data.size(); i++) {
            String dataPoint = data.get(i);            
            if (dataPoint != null) {            
                inputFieldMap.get(Integer.toString(i)).setValue(dataPoint);
            }
            else {
                inputFieldMap.get(Integer.toString(i)).setValue("");
            }          
        }        
    }
    
    private void selectDataPresentation(String option) {
        if (option.equals(dataPresentationOptions.get(0))) {
            hiddenComponent = plotBox.getComponent(1);
            plotBox.removeComponent(hiddenComponent);
            PlotlyJs chart = (PlotlyJs) plotBox.getComponent(0);
            JsonObject sizeObject = Json.createObject();
            sizeObject.put("width", Integer.toString(FULL_PLOT_WIDTH));
            chart.setSize(sizeObject);
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
        //if (longitudinalPhenotypes.contains(phenotypeMap.get("1"))) {
            setPhenotype("1", phenotypeMap.get("1"));
        //}
        //if (longitudinalPhenotypes.contains(phenotypeMap.get("2"))) {
            setPhenotype("2", phenotypeMap.get("2"));
        //}
    }
    
    private void selectSex(String option) {
        setSex(option);
        sexSelector.setIcon(VaadinIcons.valueOf(option.toUpperCase()));
    }
        
    public void show(String statistic, boolean show) {
        //for (OverlayPlot chart : new OverlayPlot[] {chart1, chart2}) {
        for (String number : new String[] {"1", "2"}) {
            System.out.println(show);
            longitudinalChartMap.get(number).sendShowStatus(show);
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
                effectuatePercentiles();
            }
        }
        previousShowOptions = showOptionsSelector.getValue();
    }
    
    private void effectuatePercentiles() {
//        if (currentView.equals(viewOptions.get(2))) {
        show("percentiles", percentilesShown);
//        }
//        if (currentView.equals(viewOptions.get(3))) {                    
//            parameterisedPlotComponent.show("percentiles", percentilesShown);
//        }
    }
    
    private void clearDataPoints(String attribute) {        
        if (attribute.equals("age")) {
            inputFieldsAge.values().forEach(inputField -> {
                inputField.clear();
                inputField.removeStyleName("problem");
            });
            setElementsToNull(storedAges);
            setAges(storedAges);
        }
        else if (attribute.equals("1") || attribute.equals("2")) {
            Variable phenotype = phenotypeMap.get(attribute);
            inputFields1.values().forEach(inputField -> {
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
        inputFieldsAge.get(fieldIndex).addStyleName("problem");
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
            inputFieldsAge.get(index).removeStyleName("problem");
            inputFieldsAge.get(index).setValue(age.getDescription());   
        }
        else {
            Notification notification = new Notification("Valid age formats: birth|x-y day(s)|week(s)|month(s)|year(s)", Notification.Type.HUMANIZED_MESSAGE);
            notification.setDelayMsec(5000);
            notification.show(Page.getCurrent());
            setAgeFieldToTroubled(index);
        }
    }
    
    public Component getComponent() {
        return box;
    }
    
}
