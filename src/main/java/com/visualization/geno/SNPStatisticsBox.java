package com.visualization.geno;

import com.database.AnnotationReader;
import com.main.Controller;
import com.plotly.BarPlot;
import com.plotly.ScatterPlot;
import com.utils.Constants;
import com.utils.JsonHelper;
import com.utils.MoBaChromosome;
import com.vaadin.data.HasValue.ValueChangeEvent;
import com.vaadin.ui.AbstractComponent;
import com.vaadin.ui.Component;
import com.vaadin.ui.Grid;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.NativeSelect;
import com.vaadin.ui.RadioButtonGroup;
import com.vaadin.ui.VerticalLayout;
import com.visualization.MoBaVisualization;
import elemental.json.Json;
import elemental.json.JsonArray;
import elemental.json.JsonObject;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import com.visualization.MoBaVisualizationInterface;

/**
 *
 * @author ChristofferHjeltnes
 */
public class SNPStatisticsBox extends GenoView {
    //GridLayout box = new GridLayout(100, 100);
    VerticalLayout box = new VerticalLayout();
    NativeSelect <VisualizationSummary> optionsSelector = new NativeSelect("Show");
    NativeSelect <String> chromosomeSelector = new NativeSelect("Chromosome");
    AnnotationReader annotationReader = new AnnotationReader();
    //GridLayout contentBox = new GridLayout(100, 100);
    HorizontalLayout contentBox = new HorizontalLayout();
    HorizontalLayout plotContainer = new HorizontalLayout();
    VerticalLayout rightContentSubBox = new VerticalLayout();
    //ScatterPlot positionPlot;
    HorizontalLayout topBox = new HorizontalLayout();
    JsonObject snpsPerChromosomeObject;
    Constants constants = new Constants();
    String [] chromosomeList = constants.getChromosomeList();
    BarPlot chart;
    Grid <MoBaChromosome> table = new Grid();
    Map <Integer, String> columnMap = new HashMap();
    RadioButtonGroup <String> snpsPerChromosomeSelector = new RadioButtonGroup("Differentiate based on");
    RadioButtonGroup <String> numberTypeSelector = new RadioButtonGroup("Show");
    JsonHelper jsonHelper = new JsonHelper();
    
    // options
    String NOTHING = "nothing";
    String GENOTYPED = "genotyped or imputed";
    String GENOTYPE = "genotype";
    String QUALITY = "quality";
    String ABSOLUTE_NUMBERS = "absolute numbers";
    String FRACTIONS = "fractions";
    
    public SNPStatisticsBox(Controller controller) {
        super(controller);
        topBox.addComponent(optionsSelector);
        //topBox.addComponent(chromosomeSelector);
        
        //int plotStartY = 6;
        //box.addComponent(topBox, 1, 0, 99, plotStartY-1);
        //box.addComponent(contentBox, 1, plotStartY+2, 99, 99-2);
        
        box.addComponent(topBox);
        box.setExpandRatio(topBox, 1);
        box.addComponent(contentBox);
        box.setExpandRatio(contentBox, 10);
        
        List <VisualizationSummary> options = new ArrayList();

        for (VisualizationSummary option : VisualizationSummary.values()) {
            if (!option.equals(VisualizationSummary.SNP_POSITION)) {
                options.add(option);
            }            
        }
        
        optionsSelector.setItems(options);
        optionsSelector.setEmptySelectionAllowed(false);
        //optionsSelector.setValue(VisualizationSummary.SNP_POSITION);        
        optionsSelector.addValueChangeListener(event -> selectOption(event));
        
        chromosomeSelector.setItems(constants.getChromosomeList());
        chromosomeSelector.setEmptySelectionAllowed(false);
        chromosomeSelector.addValueChangeListener(event -> selectChromosome(event));
        chromosomeSelector.setEnabled(false);
        
        // snpsPerChromosomeSelector
        List <String> snpsPerChromosomeSelectorOptions = Arrays.asList(new String [] {NOTHING, GENOTYPED, GENOTYPE, QUALITY});
        snpsPerChromosomeSelector.setItems(snpsPerChromosomeSelectorOptions);
        snpsPerChromosomeSelector.addValueChangeListener(event -> changeNumberType(event));
        
        // numberTypeSelector
        List <String> numberTypeSelectorOptions = Arrays.asList(new String [] {ABSOLUTE_NUMBERS, FRACTIONS});
        numberTypeSelector.setItems(numberTypeSelectorOptions);
        numberTypeSelector.addValueChangeListener(event -> changeNumberType(event));
        
        columnMap.put(1, "position");
        columnMap.put(2, "ID");
        columnMap.put(6, "RefMAF");        
        
        box.setSizeFull();
        topBox.setSizeFull();
        contentBox.setSizeFull();
        plotContainer.setSizeFull();
        // default option
        optionsSelector.setValue(VisualizationSummary.SNPS_PER_CHROMOSOME);
    }
    
    public enum VisualizationSummary {
        SNPS_PER_CHROMOSOME("SNPs per chromosome"),
        SNP_POSITION("SNP position plot");
        private final String displayName;
     
        VisualizationSummary(String displayName) {
            this.displayName = displayName;
        }
        
        @Override
        public String toString() {
            return displayName;
        }
    }
    
    public void createTable() {
        List <MoBaChromosome> mobaChromosomes = new ArrayList();
        int i = 0;
        for (String chromosome : chromosomeList) {
            mobaChromosomes.add(new MoBaChromosome(chromosome, Integer.parseInt(snpsPerChromosomeObject.getObject(chromosome).getString("total"))));
            i++;
        }
        mobaChromosomes.add(new MoBaChromosome("[last]total", Integer.parseInt(snpsPerChromosomeObject.getString("total"))));
        table.setItems(mobaChromosomes);
        table.addColumn(MoBaChromosome::getName).setCaption("Chromosome");
        table.addColumn(MoBaChromosome::getNumberOfSNPs).setCaption("Registered SNPs");
    }    
    
    public AbstractComponent getComponent() {
        return box;
    }
    
    public BarPlot getChart() {
        return chart;
    }
    
    private void loadStatistics() {
        snpsPerChromosomeObject = Json.createObject();
        String serverPath = constants.getServerPath();
        File file = new File(serverPath + "data/snp_summary/data.dat");        
        int allChromosomesTotal = 0;
        
        try {
            Scanner inputStream = new Scanner(file, "UTF-8").useDelimiter("\r\n");
            int i = 0;
            String [] columnHeaders = inputStream.next().split("\t"); //read the header
            while (inputStream.hasNext()) {
                String line = inputStream.next();
                String[] splitLine = line.split("\t");
                String chromosome = splitLine[0];
                JsonObject chromosomeObject = Json.createObject();
                for (int headerIndex = 1; headerIndex < columnHeaders.length; headerIndex++) {
                    chromosomeObject.put(columnHeaders[headerIndex], splitLine[headerIndex]);
                }
                //System.out.println("chromosomeObject: " + chromosomeObject.toJson());
                snpsPerChromosomeObject.put(chromosome, chromosomeObject);
                allChromosomesTotal += Integer.parseInt(splitLine[1]);
                i++;
            }
        snpsPerChromosomeObject.put("total", Integer.toString(allChromosomesTotal));
        inputStream.close();      
        }
        catch(Exception e) {
            System.out.println(e);
        }
        System.out.println("snpsPerChromosomeObject: " + snpsPerChromosomeObject);
    }
    
    private void selectOption(ValueChangeEvent event) {
        VisualizationSummary eventValue = (VisualizationSummary) event.getValue();
        System.out.println("choice: " + eventValue);
        
        if (snpsPerChromosomeObject == null) {
            loadStatistics();
            //createTable();
            //table.setSizeFull();
        }
        
        if (eventValue == VisualizationSummary.SNP_POSITION) {
//            if (positionPlot == null) {
//                positionPlot = new ScatterPlot();
//                positionPlot.setSizeFull();
//            }
            if (chromosomeSelector.getValue() == null) {
                chromosomeSelector.setValue("21");
                //chromosomeSelector.setValue("2");
            }
            contentBox.removeAllComponents();
            //contentBox.addComponent(positionPlot, 0, 0, 99, 99);
            //contentBox.addComponent(positionPlot);
        }
        else if (eventValue == VisualizationSummary.SNPS_PER_CHROMOSOME) {
            if (!snpsPerChromosomeSelector.getSelectedItem().isPresent()) {
                // default values
                snpsPerChromosomeSelector.setSelectedItem(GENOTYPE);
                numberTypeSelector.setSelectedItem(ABSOLUTE_NUMBERS);
            }

            contentBox.removeAllComponents();
            //contentBox.addComponent(getChart(), 0, 0, 80, 99);
            //contentBox.addComponent(snpsPerChromosomeSelector, 93, 0, 95, 99);            
            //contentBox.addComponent(numberTypeSelector, 96, 0, 98, 99);
           
            rightContentSubBox.addComponent(snpsPerChromosomeSelector);
            rightContentSubBox.addComponent(numberTypeSelector);
            plotContainer.addComponent(getChart());
            contentBox.addComponent(plotContainer);
            contentBox.setExpandRatio(plotContainer, 7);
            contentBox.addComponent(rightContentSubBox);
            contentBox.setExpandRatio(rightContentSubBox, 1);
            
        }
    }
    
    private void selectChromosome(ValueChangeEvent event) {
        String chromosome = (String) event.getValue();

        //JsonObject object = Json.createObject();
        //object.put("data", Json.createObject());
        int [] columnsToRead = new int [] {6};
        //Map <String, JsonArray> columnData = annotationReader.readAnnotationFile(chromosome, new int [] {1, 2, 6});
        Map <String, JsonArray> columnData = annotationReader.readAnnotationFile(chromosome, columnsToRead);
        System.out.println("position data obtained");
        for (Integer column : columnsToRead) {
            JsonObject object = Json.createObject();
            object.put("data", Json.createObject());
            object.getObject("data").put(columnMap.get(column), columnData.get(column.toString()));
            System.out.println("Sending data for " + columnMap.get(column));
            //positionPlot.sendData(object);
        }
        //object.put("SNPs", columnData.getNumber("SNPs"));
        
        //positionPlot.sendData(object);
        //System.out.println("annotation: " + annotation);
    }
    
    public void changeDifferentiation(ValueChangeEvent <String> event) {
        //if (!event.isUserOriginated()) {
        //    return;
        //}
        String choice = event.getValue();
        //System.out.println("choice: " + choice);
        BarPlot currentChart = getChart();
        if (choice.equals("typed or non-typed")) { 
            
        }
        else if (choice.equals("genotype")) {
            
        }
        // TODO: await #2144
//        contentBox.removeComponent(currentChart);
//        contentBox.addComponent(getChart(), 0, 0, 80, 99);
    }
    
    public void changeNumberType(ValueChangeEvent <String> event) {
        BarPlot currentChart = getChart();
        System.out.println("event.getValue(): " + event.getValue());
        String numberType = numberTypeSelector.getValue();
        String differentiation = snpsPerChromosomeSelector.getValue();
        
        if (numberType == null || differentiation == null){
            return;
        }
        
        System.out.println("numberType: " + numberType + ", differentiation: " + differentiation);
        
        List <JsonArray> yData = new ArrayList();
        
        JsonObject setupData = Json.createObject();
        jsonHelper.put(setupData, "x", Arrays.asList(chromosomeList));
        setupData.put("x-axis", "chromosome");
        
        if (differentiation.equals(NOTHING)) {
            numberTypeSelector.setEnabled(false);
            
            setupData.put("title", "Registered SNPs per chromosome");
            setupData.put("y-axis", "number of SNPs");
            
            JsonArray totalArray = Json.createArray();
            for (int i = 0; i < chromosomeList.length; i++) {
                int total = Integer.parseInt(snpsPerChromosomeObject.getObject(chromosomeList[i]).getString("total"));
                totalArray.set(i, total);
            }
            setupData.put("y", totalArray);
            setupData.put("colour", "rgb(30, 0, 200)");            
            //setupData.put("barmode", Json.createNull());
        }
        else {
            numberTypeSelector.setEnabled(true);
            if (numberType.equals(ABSOLUTE_NUMBERS)) {
                // shared
                setupData.put("title", "Registered SNPs per chromosome");
                setupData.put("y-axis", "number of SNPs");

                if (differentiation.equals(GENOTYPED)) {
                    JsonArray nonTypedArray = Json.createArray();
                    JsonArray typedArray = Json.createArray();
                    for (int i = 0; i < chromosomeList.length; i++) {
                        //totalArray.set(i, snpsPerChromosomeObject.getObject(chromosomeList[i]).getString("total"));
                        int typed = Integer.parseInt(snpsPerChromosomeObject.getObject(chromosomeList[i]).getString("typed"));
                        int total = Integer.parseInt(snpsPerChromosomeObject.getObject(chromosomeList[i]).getString("total"));
                        typedArray.set(i, typed);
                        nonTypedArray.set(i, total - typed);
                    }
                    yData.add(nonTypedArray);
                    yData.add(typedArray);
                }
                else if (differentiation.equals(GENOTYPE)) {
                    setupData.put("title", "Registered instances of individuals with given genotype");
                    setupData.put("y-axis", "number of instances");
                    JsonArray nAA = Json.createArray();
                    JsonArray nAB = Json.createArray();
                    JsonArray nBB = Json.createArray();
                    for (int i = 0; i < chromosomeList.length; i++) {
                        long nAAi = Double.valueOf(snpsPerChromosomeObject.getObject(chromosomeList[i]).getString("nAA")).longValue();
                        long nABi = Double.valueOf(snpsPerChromosomeObject.getObject(chromosomeList[i]).getString("nAB")).longValue();
                        long nBBi = Double.valueOf(snpsPerChromosomeObject.getObject(chromosomeList[i]).getString("nBB")).longValue();
                        nAA.set(i, nAAi);
                        nAB.set(i, nABi);
                        nBB.set(i, nBBi);
                    }
                    yData.add(nAA);
                    yData.add(nAB);
                    yData.add(nBB);
                }
                else if (differentiation.equals(QUALITY)) {
                    JsonArray lowQualityArray = Json.createArray();
                    JsonArray highQualityArray = Json.createArray();
                    for (int i = 0; i < chromosomeList.length; i++) {
                        //totalArray.set(i, snpsPerChromosomeObject.getObject(chromosomeList[i]).getString("total"));
                        int highQuality = Integer.parseInt(snpsPerChromosomeObject.getObject(chromosomeList[i]).getString("highQuality"));
                        int total = Integer.parseInt(snpsPerChromosomeObject.getObject(chromosomeList[i]).getString("total"));
                        highQualityArray.set(i, highQuality);
                        lowQualityArray.set(i, total - highQuality);
                    }
                    yData.add(lowQualityArray);
                    yData.add(highQualityArray);
                }
            }
            else if (numberType.equals(FRACTIONS)) {
                // shared
                setupData.put("title", "Distribution of SNPs per chromosome");
                setupData.put("y-axis", "fraction of SNPs");

                if (differentiation.equals(GENOTYPED)) {
                    JsonArray nonTypedArray = Json.createArray();
                    JsonArray typedArray = Json.createArray();
                    for (int i = 0; i < chromosomeList.length; i++) {
                        //totalArray.set(i, snpsPerChromosomeObject.getObject(chromosomeList[i]).getString("total"));
                        int total = Integer.parseInt(snpsPerChromosomeObject.getObject(chromosomeList[i]).getString("total"));
                        double fTyped = Integer.parseInt(snpsPerChromosomeObject.getObject(chromosomeList[i]).getString("typed"))/(double) total;
                        double fNontyped = 1 - fTyped;

                        typedArray.set(i, fTyped);
                        nonTypedArray.set(i, fNontyped);
                    }
                    yData.add(nonTypedArray);
                    yData.add(typedArray);
                }
                else if (differentiation.equals(GENOTYPE)) {
                    setupData.put("title", "Distribution of instances of individuals with given genotype");
                    setupData.put("y-axis", "fraction of instances");
                    JsonArray fAA = Json.createArray();
                    JsonArray fAB = Json.createArray();
                    JsonArray fBB = Json.createArray();
                    for (int i = 0; i < chromosomeList.length; i++) {
                        long nAAi = Double.valueOf(snpsPerChromosomeObject.getObject(chromosomeList[i]).getString("nAA")).longValue();
                        long nABi = Double.valueOf(snpsPerChromosomeObject.getObject(chromosomeList[i]).getString("nAB")).longValue();
                        long nBBi = Double.valueOf(snpsPerChromosomeObject.getObject(chromosomeList[i]).getString("nBB")).longValue();
                        long total = nAAi + nABi + nBBi;

                        double fAAi = 0, fABi = 0, fBBi = 0;

                        if (total != 0) {
                            fAAi = nAAi/ (double) total;
                            fABi = nABi/(double) total;
                            fBBi = nBBi/(double) total;                        
                        }
                        fAA.set(i, fAAi);
                        fAB.set(i, fABi);
                        fBB.set(i, fBBi);
                    }
                    yData.add(fAA);
                    yData.add(fAB);
                    yData.add(fBB);                
                }
                else if (differentiation.equals(QUALITY)) {
                    JsonArray lowQualityArray = Json.createArray();
                    JsonArray highQualityArray = Json.createArray();
                    for (int i = 0; i < chromosomeList.length; i++) {
                        //totalArray.set(i, snpsPerChromosomeObject.getObject(chromosomeList[i]).getString("total"));
                        int total = Integer.parseInt(snpsPerChromosomeObject.getObject(chromosomeList[i]).getString("total"));
                        double fHighQuality = Integer.parseInt(snpsPerChromosomeObject.getObject(chromosomeList[i]).getString("highQuality"))/(double) total;
                        double fLowQuality = 1 - fHighQuality;

                        highQualityArray.set(i, fHighQuality);
                        lowQualityArray.set(i, fLowQuality);
                    }
                    yData.add(lowQualityArray);
                    yData.add(highQualityArray);
                }
            }

            if (differentiation.equals(GENOTYPED)) {      
                JsonObject data1 = Json.createObject();
                JsonObject data2 = Json.createObject();
                setupData.put("data 1", data1);
                setupData.put("data 2", data2);
                data1.put("y", yData.get(0));
                data2.put("y", yData.get(1));
                data1.put("name", "imputed");
                data2.put("name", "genotyped");
                data1.put("colour", "rgb(31, 119, 180)");
                data2.put("colour", "rgb(30, 200, 0)");
                setupData.put("barmode", "stack");
            }
            else if (differentiation.equals(GENOTYPE)) {
                JsonObject data1 = Json.createObject();
                JsonObject data2 = Json.createObject();
                JsonObject data3 = Json.createObject();
                setupData.put("data 1", data1);
                setupData.put("data 2", data2);
                setupData.put("data 3", data3);
                data1.put("y", yData.get(0));
                data2.put("y", yData.get(1));
                data3.put("y", yData.get(2));

                // names
                data1.put("name", "AA");
                data2.put("name", "AB");
                data3.put("name", "BB");

                // colours
                data1.put("colour", "rgba(0, 0, 0, 0.7)");
                data2.put("colour", "rgb(40, 40, 180)");
                data3.put("colour", "rgb(180, 40, 40)");

                setupData.put("barmode", "stack");
            }
            else if (differentiation.equals(QUALITY)) {
                JsonObject data1 = Json.createObject();
                JsonObject data2 = Json.createObject();
                setupData.put("data 1", data1);
                setupData.put("data 2", data2);
                data1.put("y", yData.get(0));
                data2.put("y", yData.get(1));
                data1.put("name", "low-quality");
                data2.put("name", "high-quality");
                data1.put("colour", "rgb(31, 119, 180)");
                data2.put("colour", "rgb(30, 200, 0)");
                setupData.put("barmode", "stack");
            }
        }
        chart = new BarPlot();
        chart.setUp(setupData);
        chart.setSizeFull();
        // TODO: await #2144
        if (currentChart != null) {
            plotContainer.removeComponent(currentChart);
        }
        plotContainer.addComponent(getChart());
    }
    
    @Override
    public void updateSNP() {} // no functionality yet
    
    @Override
    public void resizePlots() {
        chart.resize();
    }
    
    @Override
    public void handOver() {
    }
    
}