package com.main;

import com.plotly.SNPPlot;
import com.main.SQLite;
import com.utils.Alphanumerical;
import com.utils.Constants;
import com.utils.UtilFunctions;
import com.utils.HtmlHelper;
import com.utils.JsonHelper;
import com.vaadin.event.selection.SelectionEvent;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.shared.ui.ContentMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.CheckBoxGroup;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.NativeSelect;
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
public class SNPPlotBoxOld {
    HtmlHelper html = new HtmlHelper();
    
    JsonObject data;
    JsonHelper jsonHelper = new JsonHelper();
    JsonObject showStatus;
    
    Map <String, Boolean> currentShowStatus;
    
    GridLayout box = new GridLayout(100, 100);
    HorizontalLayout plotBox = new HorizontalLayout();
    
    SNPPlot femaleChart;
    SNPPlot maleChart;
    HorizontalLayout SNPGrid = new HorizontalLayout();
    GridLayout SNPRightGrid = new GridLayout(10, 4);
    HorizontalLayout SNPShowOptionsSelectorBox = new HorizontalLayout();
    HorizontalLayout topBox = new HorizontalLayout();
    CheckBoxGroup <String> SNPShowOptionsSelector;
    Set <String> previousSNPShowOptions = new HashSet();
    Label message;
    Label SNPInformation = new Label();
    
    List <String> phenotypeOptions = new ArrayList();
    
    String currentPhenotype;
    SNPOld currentSNP;
    
    List <String> SNPOptions;
    
    ComboBox <String> SNPInput;
    String currentSNPInputValue;
    boolean SNPInputActive = false;
    NativeSelect <String> phenotypeSelector;
    
    SQLite sqlite = new SQLite();
    
    UtilFunctions converter = new UtilFunctions();
    Constants constants = new Constants();
    
    boolean mediansShown = true;
    boolean SEMShown = true;
    boolean CIShown = false;
    boolean nShown = true;
    
    
    public SNPPlotBoxOld() {        
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

        // show options
        List <String> SNPShowOptions = new ArrayList();
        SNPShowOptions.addAll(Arrays.asList(new String[] {"medians", "SEM", "confidence intervals", "n"}));
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
            SNPOptions = new ArrayList(Arrays.asList(new String[]{
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
        
        SNPInput.setValue("rs9996");  
        
        currentShowStatus = new HashMap();
        currentShowStatus.put("medians", true);
        currentShowStatus.put("SEM", true);
        currentShowStatus.put("confidence intervals", false);
        currentShowStatus.put("n", true);
        
        
        Set initialSNPShowSettings = new HashSet();
        initialSNPShowSettings.addAll(Arrays.asList(new String[] {"medians", "SEM", "n"}));//, "confidence intervals"}));
        SNPShowOptionsSelector.setValue(initialSNPShowSettings);
        previousSNPShowOptions = SNPShowOptionsSelector.getValue();
        SNPShowOptionsSelector.addSelectionListener(event -> changeSNPShowSettings(event));
        
        SNPShowOptionsSelectorBox.addComponent(SNPShowOptionsSelector);
        SNPRightGrid.addComponent(SNPShowOptionsSelectorBox, 1, 0, 9, 0);
        
        topBox.addComponent(SNPInput);
        topBox.addComponent(phenotypeSelector);

        
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
       
    public void setDatasets(SNPOld snp, String phenotype) {
        for (String sex : new String[] {"female", "male"}) {
            data = Json.createObject();
            
            data.put("sex", sex);
            data.put("SNP ID", snp.getID());
            data.put("phenotype", phenotype);            
                        
            int overallMaxN = 0;
            SNPPlot chart = null;
            if (sex.equals("female")) {
                chart = femaleChart;                
            }
            else if (sex.equals("male")) {
                chart = maleChart;
            }
            for (String genotype : new String[] {"AA", "AB", "BB"}) {
                JsonObject genotypeObject = Json.createObject();
                
                jsonHelper.put(genotypeObject, "median", snp.getData(genotype + " " + phenotype + " " + sex +  " median"));                
                jsonHelper.put(genotypeObject, "lower SEM", snp.getData(genotype + " " + phenotype + " " + sex +  " 95%_SEM_up")); // sic
                jsonHelper.put(genotypeObject, "upper SEM", snp.getData(genotype + " " + phenotype + " " + sex +  " 95%_SEM_down")); // sic
                jsonHelper.put(genotypeObject, "lower CI", snp.getData(genotype + " " + phenotype + " " + sex +  " 95%_CI_up"));
                jsonHelper.put(genotypeObject, "upper CI", snp.getData(genotype + " " + phenotype + " " + sex +  " 95%_CI_down"));    
                jsonHelper.put(genotypeObject, "N", snp.getData(genotype + " " + phenotype + " " + sex +  " n"));    
                
                
                List <String> nData = snp.getData(genotype + " " + phenotype + " " + sex +  " n");
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
                data.put(genotype, genotypeObject);    
                //System.out.println("genotypeObject: " + genotypeObject);
            }
            chart.sendData(data);
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
        //Notification.show("disabled", Notification.Type.TRAY_NOTIFICATION);
        currentSNPInputValue = option;
        SNPOld snp = sqlite.getSNP(option);
        //viewSelector.setEnabled(true);
        phenotypeSelector.setEnabled(true);
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
            phenotypeSelector.setEnabled(false);
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
        setDatasets(snp, currentPhenotype);
        SNPRightGrid.removeComponent(SNPInformation);
        SNPInformation = new Label(SNPinformationString, ContentMode.HTML);
        SNPInformation.setSizeFull();
        SNPRightGrid.addComponent(SNPInformation, 1, 1, 8, 3);
   
        phenotypeSelector.setEnabled(true);
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
    
    private void selectPhenotype(String option) {
        if (option.equals("null") || option.equals(currentPhenotype)){
            return;
        }        
        
        setDatasets(currentSNP, option);
        currentPhenotype = option;        
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
                show(s, mediansShown);
            }
            else if (s.equals("SEM")) {
                SEMShown = !SEMShown;
                show(s, SEMShown);
            }
            else if (s.equals("confidence intervals")) {
                CIShown = !CIShown;
                show(s, CIShown);
            }
            else if (s.equals("n")) {
                nShown = !nShown;
                show(s, nShown);
            }
        }
        previousSNPShowOptions = SNPShowOptionsSelector.getValue();
    }
        
    public void show(String statistic, boolean show) {
        currentShowStatus.put(statistic, show);
        showStatus = Json.createObject(); // create a new object each time
        for (String stat : currentShowStatus.keySet()) {
            showStatus.put(stat, currentShowStatus.get(stat));
        }
        showStatus.put("percentiles", false); // forward compatibility
        for (SNPPlot chart : new SNPPlot[] {femaleChart, maleChart}) {
            chart.sendPlotOptions(showStatus);
        }
        System.out.println("showStatus: " + showStatus);
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
