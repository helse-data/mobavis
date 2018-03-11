package com.main;

import com.plotly.BarPlot;
import com.utils.Constants;
import com.utils.JsonHelper;
import com.utils.MoBaChromosome;
import com.vaadin.server.VaadinService;
import com.vaadin.ui.Component;
import com.vaadin.ui.Grid;
import com.vaadin.ui.HorizontalLayout;
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
    
    HorizontalLayout page = new HorizontalLayout();
    JsonObject snpsPerChromosome = Json.createObject();
    Constants constants = new Constants();
    String [] chromosomeList = constants.getChromosomeList();
    BarPlot chart;
    Grid <MoBaChromosome> table = new Grid();
    
    
    public SummaryPage() {
        loadStatistics();
        //System.out.println(SNPs);
        JsonHelper jsonHelper = new JsonHelper();
        chart = new BarPlot();
        chart.setSizeFull();
        JsonObject setupData = Json.createObject();
        jsonHelper.put(setupData, "x", Arrays.asList(chromosomeList));
        setupData.put("y", snpsPerChromosome.getArray("chromosomes"));
        setupData.put("x-axis", "chromosome");
        setupData.put("y-axis", "number of SNPs");
        setupData.put("title", "Registered SNPs per chromosome");
        setupData.put("colour", "rgb(30, 0, 200)");
        chart.setUp(setupData);
        //chart.sendData(snpsPerChromosome);
        page.setSizeFull();
        page.addComponent(getChart());
        createTable();
        table.setSizeFull();
        page.addComponent(table);
    }
    
    public void createTable() {
        List <MoBaChromosome> mobaChromosomes = new ArrayList();
        int i = 0;
        for (String chromosome : chromosomeList) {
            mobaChromosomes.add(new MoBaChromosome(chromosome, Integer.parseInt(snpsPerChromosome.getArray("chromosomes").getString(i))));
            i++;
        }
        mobaChromosomes.add(new MoBaChromosome("[last]total", Integer.parseInt(snpsPerChromosome.getString("total"))));
        table.setItems(mobaChromosomes);
        table.addColumn(MoBaChromosome::getName).setCaption("Chromosome");
        table.addColumn(MoBaChromosome::getNumberOfSNPs).setCaption("Registered SNPs");
    }    
    
    public Component getComponent() {
        return page;
    }
    
    public BarPlot getChart() {
        return chart;
    }
    
    private void loadStatistics() {
        String basepath = VaadinService.getCurrent().getBaseDirectory().getAbsolutePath();
        File file = new File(basepath + "/WEB-INF/SNPs per chromosome.csv");
        
        int total = 0;
        
        try {
            Scanner inputStream = new Scanner(file, "UTF-8").useDelimiter("\r\n");
            JsonArray array = Json.createArray();
            int i = 0;
            while (inputStream.hasNext()) {               
                String line = inputStream.next();
                String[] split = line.split(" ");
                array.set(i, split[1]);
                total += Integer.parseInt(split[1]);
                i++;
            }
        snpsPerChromosome.put("chromosomes", array);
        snpsPerChromosome.put("total", Integer.toString(total));            
        inputStream.close();      
        }
        catch(IOException e) {
            System.out.println("Error: " + e);
            System.exit(1);
            }
    }
}