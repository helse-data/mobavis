package com.main;

import com.byteowls.vaadin.chartjs.ChartJs;
import com.byteowls.vaadin.chartjs.config.BarChartConfig;
import com.byteowls.vaadin.chartjs.data.BarDataset;
import com.byteowls.vaadin.chartjs.utils.ColorUtils;
import com.utils.Constants;
import com.utils.MoBaChromosome;
import com.vaadin.server.VaadinService;
import com.vaadin.ui.Component;
import com.vaadin.ui.Grid;
import com.vaadin.ui.HorizontalLayout;
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
    Map <String, Integer> SNPs = new HashMap();
    Constants constants = new Constants();
    String [] chromosomeList = constants.getChromosomeList();
    ChartJs chart;
    Grid <MoBaChromosome> table = new Grid();
    
    
    public SummaryPage() {
        loadStatistics();
        //System.out.println(SNPs);
        createChart();
        page.setSizeFull();
        page.addComponent(getChart());
        createTable();
        table.setSizeFull();
        page.addComponent(table);
    }
    
    
    private void createChart() {
        String colour = ColorUtils.toRgb(new int[] {30, 0, 200});
        
        BarChartConfig barConfig = new BarChartConfig();
        barConfig.options().maintainAspectRatio(false);
        barConfig.options().legend().display(false);
        barConfig.options().title()
                .text("Registered SNPs per chromosome")
                .display(true);
        barConfig.data().labelsAsList(Arrays.asList(chromosomeList));
        
        BarDataset data = new BarDataset();
        data.backgroundColor(colour);
        for (String chromosome : chromosomeList) {
            data.addData(SNPs.get(chromosome));
        }
        barConfig.data().addDataset(data);
                
        
        chart = new ChartJs(barConfig);
        chart.setSizeFull();        
    }
    
    public void createTable() {
        List <MoBaChromosome> mobaChromosomes = new ArrayList();
        for (String chromosome : chromosomeList) {
            mobaChromosomes.add(new MoBaChromosome(chromosome, SNPs.get(chromosome)));
        }
        mobaChromosomes.add(new MoBaChromosome("[last]total", SNPs.get("total")));
        table.setItems(mobaChromosomes);
        table.addColumn(MoBaChromosome::getName).setCaption("Chromosome");
        table.addColumn(MoBaChromosome::getNumberOfSNPs).setCaption("Registered SNPs");
    }    
    
    public Component getComponent() {
        return page;
    }
    
    public ChartJs getChart() {
        return chart;
    }
    
    private void loadStatistics() {
        String basepath = VaadinService.getCurrent().getBaseDirectory().getAbsolutePath();
        File file = new File(basepath + "/WEB-INF/SNPs per chromosome.csv");
        
        int total = 0;
        
        try {
            Scanner inputStream = new Scanner(file, "UTF-8").useDelimiter("\r\n");
            
            while (inputStream.hasNext()) {               
                String line = inputStream.next();
                String[] split = line.split(" ");
                //System.out.println("string: " + split[1] + ", length: " + split[1].length() + 
                //        ", last character: " + split[1].charAt(split[1].length()-1));
                int n = Integer.parseInt(split[1]);
                SNPs.put(split[0], n);
                total += n;
            }
            
        SNPs.put("total", total);            
        inputStream.close();      
        }
        catch(IOException e) {
            System.out.println("Error: " + e);
            System.exit(1);
            }
    }
}