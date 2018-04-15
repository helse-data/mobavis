package com.main;

import com.files.ReadAnnotation;
import com.plotly.BarPlot;
import com.plotly.ScatterPlot;
import com.utils.Constants;
import com.utils.JsonHelper;
import com.utils.MoBaChromosome;
import com.vaadin.data.HasValue.ValueChangeEvent;
import com.vaadin.server.VaadinService;
import com.vaadin.ui.Component;
import com.vaadin.ui.Grid;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.NativeSelect;
import elemental.json.Json;
import elemental.json.JsonArray;
import elemental.json.JsonObject;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

/**
 *
 * @author ChristofferHjeltnes
 */
public class SummaryPage {
    GridLayout box = new GridLayout(100, 100);
    NativeSelect <MenuOptionSummary> optionsSelector = new NativeSelect("Show");
    NativeSelect<String> chromosomeSelector = new NativeSelect("Chromosome");
    ReadAnnotation annotationReader = new ReadAnnotation();
    HorizontalLayout contentBox = new HorizontalLayout();
    ScatterPlot positionPlot;
    HorizontalLayout topBox = new HorizontalLayout();
    JsonObject snpsPerChromosome;
    Constants constants = new Constants();
    String [] chromosomeList = constants.getChromosomeList();
    BarPlot chart;
    Grid <MoBaChromosome> table = new Grid();
    Map <Integer, String> columnMap = new HashMap();
    
    
    public SummaryPage() {
        topBox.addComponent(optionsSelector);
        //topBox.addComponent(chromosomeSelector);
        
        int plotStartY = 6;
        box.addComponent(topBox, 1, 0, 99, plotStartY-1);
        box.addComponent(contentBox, 1, plotStartY+2, 99, 99-2);
        
        List <MenuOptionSummary> options = new ArrayList();
                
        options.addAll(Arrays.asList(new MenuOptionSummary[] {
            MenuOptionSummary.SNPS_PER_CHROMOSOME}));//,
            //MenuOptionSummary.SNP_POSITION}));
        
        optionsSelector.setItems(options);
        optionsSelector.setEmptySelectionAllowed(false);
        //optionsSelector.setValue(MenuOptionSummary.SNP_POSITION);        
        optionsSelector.addValueChangeListener(event -> selectOption(event));
        
        chromosomeSelector.setItems(constants.getChromosomeList());
        chromosomeSelector.setEmptySelectionAllowed(false);
        chromosomeSelector.addValueChangeListener(event -> selectChromosome(event));
        chromosomeSelector.setEnabled(false);
        
        columnMap.put(1, "position");
        columnMap.put(2, "ID");
        
        contentBox.setSizeFull();
        box.setSizeFull();
        topBox.setSizeFull();
        optionsSelector.setValue(MenuOptionSummary.SNPS_PER_CHROMOSOME);
    }
    
    public enum MenuOptionSummary {
        SNPS_PER_CHROMOSOME("SNPs per chromosome"),
        SNP_POSITION("SNP position plot");
        private final String displayName;
     
        MenuOptionSummary(String displayName) {
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
            mobaChromosomes.add(new MoBaChromosome(chromosome, Integer.parseInt(snpsPerChromosome.getObject(chromosome).getString("total"))));
            i++;
        }
        mobaChromosomes.add(new MoBaChromosome("[last]total", Integer.parseInt(snpsPerChromosome.getString("total"))));
        table.setItems(mobaChromosomes);
        table.addColumn(MoBaChromosome::getName).setCaption("Chromosome");
        table.addColumn(MoBaChromosome::getNumberOfSNPs).setCaption("Registered SNPs");
    }    
    
    public Component getComponent() {
        return box;
    }
    
    public BarPlot getChart() {
        return chart;
    }
    
    private void loadStatistics() {
        snpsPerChromosome = Json.createObject();
        String basepath = VaadinService.getCurrent().getBaseDirectory().getAbsolutePath();
        File file = new File(basepath + "/../../../../server/data/snp_summary/data.dat");        
        int allChromosomesTotal = 0;
        
        try {
            
            Scanner inputStream = new Scanner(file, "UTF-8").useDelimiter("\r\n");
            int i = 0;
            inputStream.next(); //read the header
            while (inputStream.hasNext()) {               
                String line = inputStream.next();
                String[] splitLine = line.split("\t");
                String chromosome = splitLine[0];
                JsonObject chromosomeObject = Json.createObject();
                chromosomeObject.put("total", splitLine[1]);
                chromosomeObject.put("typed", splitLine[2]);
                snpsPerChromosome.put(chromosome, chromosomeObject);
                allChromosomesTotal += Integer.parseInt(splitLine[1]);
                i++;
            }
        snpsPerChromosome.put("total", Integer.toString(allChromosomesTotal));            
        inputStream.close();      
        }
        catch(Exception e) {
            System.out.println(e.getMessage());
        }
    }
    
    private void selectOption(ValueChangeEvent event) {
        MenuOptionSummary eventValue = (MenuOptionSummary) event.getValue();
        System.out.println("choice: " + eventValue);
        if (eventValue == MenuOptionSummary.SNP_POSITION) {
            if (positionPlot == null) {
                positionPlot = new ScatterPlot();
            }
            if (chromosomeSelector.getValue() == null) {
                chromosomeSelector.setValue("21");
            }
            contentBox.removeAllComponents();
            contentBox.addComponent(positionPlot);
        }
        else if (eventValue == MenuOptionSummary.SNPS_PER_CHROMOSOME) {
            if (snpsPerChromosome == null) {
                loadStatistics();
                JsonHelper jsonHelper = new JsonHelper();
                JsonObject setupData = Json.createObject();
                jsonHelper.put(setupData, "x", Arrays.asList(chromosomeList));
                
                JsonArray nonTypedArray = Json.createArray();
                JsonArray typedArray = Json.createArray();
                for (int i = 0; i < chromosomeList.length; i++) {
                    //totalArray.set(i, snpsPerChromosome.getObject(chromosomeList[i]).getString("total"));
                    int typed = Integer.parseInt(snpsPerChromosome.getObject(chromosomeList[i]).getString("typed"));
                    int total = Integer.parseInt(snpsPerChromosome.getObject(chromosomeList[i]).getString("total"));
                    typedArray.set(i, typed);
                    nonTypedArray.set(i, total - typed);
                }
                setupData.put("y1", nonTypedArray);
                setupData.put("y2", typedArray);
                setupData.put("name1", "not typed");
                setupData.put("name2", "typed");
                setupData.put("x-axis", "chromosome");
                setupData.put("y-axis", "number of SNPs");
                setupData.put("title", "Registered SNPs per chromosome");
                setupData.put("colour", "rgb(30, 0, 200)");
                setupData.put("barmode", "stack");
                chart = new BarPlot();
                chart.setUp(setupData);
                chart.setSizeFull();
                createTable();
                table.setSizeFull();
            }
            contentBox.removeAllComponents();
            contentBox.addComponent(getChart());
            //contentBox.addComponent(table);            
        }
    }
    
    private void selectChromosome(ValueChangeEvent event) {
        String chromosome = (String) event.getValue();
        //Map <Integer, List <String>>
        JsonObject object = Json.createObject();
        object.put("data", Json.createObject());
        JsonObject annotation = annotationReader.readAnnotationFile(chromosome, new int [] {1, 2});
        
        for (Integer column : columnMap.keySet()) {
            object.getObject("data").put(columnMap.get(column), annotation.getArray(column.toString()));
        }
        object.put("SNPs", annotation.getNumber("SNPs"));
        positionPlot.sendData(object);
        //System.out.println("annotation: " + annotation);
    }
    
}