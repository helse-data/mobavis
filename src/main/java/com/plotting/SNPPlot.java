package com.plotting;

import com.byteowls.vaadin.chartjs.ChartJs;
import com.byteowls.vaadin.chartjs.config.LineChartConfig;
import com.byteowls.vaadin.chartjs.data.Dataset;
import com.byteowls.vaadin.chartjs.data.LineDataset;
import com.byteowls.vaadin.chartjs.options.InteractionMode;
import com.byteowls.vaadin.chartjs.options.Position;
import com.byteowls.vaadin.chartjs.options.scale.Axis;
import com.byteowls.vaadin.chartjs.options.scale.CategoryScale;
import com.byteowls.vaadin.chartjs.options.scale.LinearScale;
import com.byteowls.vaadin.chartjs.utils.ColorUtils;
import com.main.SNP;
import com.utils.Constants;
import com.utils.Converter;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 *
 * @author ChristofferHjeltnes
 */
public class SNPPlot {
   
    ChartJs chart1;
    ChartJs chart2;
    LineChartConfig config1 = new LineChartConfig();
    LineChartConfig config2 = new LineChartConfig();
    Converter converter = new Converter();
    //String attribute;
    LinearScale attributeScale = new LinearScale();
    LinearScale nScale = new LinearScale();
    Map <String, String> colours = new HashMap();
    Map <String, Integer> indices = new HashMap();
    Map <String, String> transparentColours = new HashMap();
    Map <String, String> barColours = new HashMap();
    Constants constants = new Constants();
    
    public SNPPlot() {         
        String red = ColorUtils.toRgb(new int[] {255, 0, 0});
        String transparentRed = ColorUtils.toRgba(new int[] {200, 0, 0},  0.1);
        String blue = ColorUtils.toRgb(new int[] {0, 0, 255});
        String transparentBlue = ColorUtils.toRgba(new int[] {0, 0, 255},  0.1);
        String black = ColorUtils.toRgb(new int[] {0, 0, 0});
        String grey = ColorUtils.toRgba(new int[] {0, 0, 0},  0.1);
        
        String barRed = ColorUtils.toRgba(new int[] {255, 0, 0},  0.9);
        String barBlack = ColorUtils.toRgba(new int[] {0, 0, 0},  0.9);
        String barBlue = ColorUtils.toRgba(new int[] {0, 0, 255},  0.9);        
        
        colours.put("AA", black);
        colours.put("AB", blue);
        colours.put("BB", red);
        barColours.put("AA", barBlack);
        barColours.put("AB", barBlue);
        barColours.put("BB", barRed);
        
        indices.put("AA", 0);
        indices.put("AB", 5);
        indices.put("BB", 10);
        transparentColours.put("AA", grey);
        transparentColours.put("AB", transparentBlue);
        transparentColours.put("BB", transparentRed);
        
        config1 = createConfig("females");
        config2 = createConfig("males");
        
        chart1 = new ChartJs(config1);
        chart1.setJsLoggingEnabled(true);
        chart2 = new ChartJs(config2);
        chart2.setJsLoggingEnabled(true);
    }
    
    
    private LineChartConfig createConfig(String title) {
        LineChartConfig config = new LineChartConfig(); // seems to have to be a BarChartConfig, not a LineChartConfig
        
        for (String genotype : new String[] {"AA", "AB", "BB"}) {
            LineDataset lineDatasetMedian = new LineDataset();
            LineDataset lineDatasetSEMLower = new LineDataset();
            LineDataset lineDatasetSEMUpper = new LineDataset();
            LineDataset lineDatasetCILower = new LineDataset();
            LineDataset lineDatasetCIUpper = new LineDataset();
            
            //BarDataset nDataSet = new BarDataset().type(); // create data set and set the type
            //nDataSet.yAxisID("Yn").label("").backgroundColor(barColours.get(genotype)).borderColor("white");
            

            for (LineDataset lineDataset : new LineDataset[] {lineDatasetMedian, lineDatasetSEMLower,
                    lineDatasetSEMUpper, lineDatasetCILower, lineDatasetCIUpper}) {
                lineDataset.lineTension(0).spanGaps(true).yAxisID("YAttribute")
                .borderWidth(1).pointRadius(0);
                config.data().addDataset(lineDataset);
            }
            //config.data().addDataset(nDataSet);

            lineDatasetMedian.borderDash(5, 5).fill(false);            
            lineDatasetMedian.borderColor(colours.get(genotype)).backgroundColor(colours.get(genotype));

            for (LineDataset lineDataset : new LineDataset[] {lineDatasetSEMLower, lineDatasetSEMUpper, lineDatasetCILower, lineDatasetCIUpper}) {
                lineDataset.fill(indices.get(genotype)).label("");
                lineDataset.backgroundColor(transparentColours.get(genotype)).borderColor(transparentColours.get(genotype));
            }
            
        }        
        config.options().animation().duration(0);
        
        // scales
        attributeScale.id("YAttribute")
                .display(true)
                .scaleLabel()
                    .display(true)
                    //.labelString("attribut"e")
                    .and()
                .ticks()
                    //.suggestedMin(0)
                    //.suggestedMax(200)
                    .and()
                .position(Position.RIGHT);
        
//        nScale.id("Yn")
//                .display(true)
//                .scaleLabel()
//                    .display(true)
//                    .labelString("number of individuals")
//                    .and()
//                .ticks()
//                    //.suggestedMin(0)
//                    //.suggestedMax(200)
//                    .and()
//                .position(Position.LEFT);
        
        
        config.data()
            .labels(constants.getAgesShort())
            .and()
        .options()
            .responsive(true)
            .title()
            .display(true)
            .text(title)
            .and()
        .tooltips()
            .mode(InteractionMode.NEAREST)
            .intersect(true)
            .and()
        .hover()
            .mode(InteractionMode.NEAREST)
            .intersect(true)
            .and()
        .scales()
        .add(Axis.X, new CategoryScale()
                .display(true)
                .scaleLabel()
                    .display(true)
                    .labelString("age")
                    .and()
                .position(Position.TOP))
        .add(Axis.Y, attributeScale)
        //.add(Axis.Y, nScale)
        .and()
        .done();
        config.options().maintainAspectRatio(false);
        return config;
    }
    
    private void setDatasets(SNP snp, String attribute) {      
        for (String sex : new String[] {"female", "male"}) {
            int overallMaxN = 0;
            LineChartConfig config = config1;
            if (sex.equals("female")) {
                config = config1;                
            }
            else if (sex.equals("male")) {
                config = config2;
            }
            for (String genotype : new String[] {"AA", "AB", "BB"}) {
                int index = indices.get(genotype);
                //System.out.println(index);
                
                Dataset <?, ?> ds = config.data().getDatasetAtIndex(index);
                LineDataset lineDatasetMedian = (LineDataset) ds;
                List <Double> medianData = converter.doubleList(snp.getData(genotype + " " + attribute + " " + sex +  " median"));
                lineDatasetMedian.dataAsList(medianData);
                                
                Dataset <?, ?> dsSEMLower = config.data().getDatasetAtIndex(index + 1);
                LineDataset lineDatasetSEMLower = (LineDataset) dsSEMLower;
                List <Double> lowerSEMData = converter.doubleList(snp.getData(genotype + " " + attribute + " " + sex +  " 95%_SEM_up"));
                lineDatasetSEMLower.dataAsList(lowerSEMData);
                
                Dataset <?, ?> dsSEMUpper = config.data().getDatasetAtIndex(index + 2);
                LineDataset lineDatasetSEMUpper = (LineDataset) dsSEMUpper;
                List <Double> upperSEMData = converter.doubleList(snp.getData(genotype + " " + attribute + " " + sex +  " 95%_SEM_down"));
                lineDatasetSEMUpper.dataAsList(upperSEMData);
                
                Dataset <?, ?> dsCILower = config.data().getDatasetAtIndex(index + 3);
                LineDataset lineDatasetCILower = (LineDataset) dsCILower;
                List <Double> lowerCIData = converter.doubleList(snp.getData(genotype + " " + attribute + " " + sex +  " 95%_CI_up"));
                lineDatasetCILower.dataAsList(lowerCIData);
                
                Dataset <?, ?> dsCIUpper = config.data().getDatasetAtIndex(index + 4);
                LineDataset lineDatasetCIUpper = (LineDataset) dsCIUpper;
                List <Double> upperCIData = converter.doubleList(snp.getData(genotype + " " + attribute + " " + sex +  " 95%_CI_down"));
                lineDatasetCIUpper.dataAsList(upperCIData);
                
                List <String> nData = snp.getData(genotype + " " + attribute + " " + sex +  " n");
                String nMin = converter.minInteger(nData);
                String nMax = converter.maxInteger(nData);
                
//                Dataset <?, ?> dsN = config.data().getDatasetAtIndex(index + 5);
//                BarDataset barDataSetN = (BarDataset) dsN;
//                barDataSetN.dataAsList(converter.doubleList(nData));
                
                String info  = "";
                
                if (!nMax.equals("<5")) {
                    int currMax = Integer.parseInt(nMax);
                    if (currMax > overallMaxN) {
                        overallMaxN = currMax;    
                    }                    
                }                
                
                if (nMin.equals("0") && nMax.equals("0")) {
                    info = " (no individuals)";
                }
                else if (nMin.equals("<5") && nMax.equals("<5")) {
                    info = " (less than 5 individuals)";
                }
                else {
                    info = ", n ∈ [" + nMin + ", " + nMax + "]";
                }
               
                //System.out.println();
                lineDatasetMedian.label(genotype + info);                       
            }
            nScale.ticks().max(10*overallMaxN); 
        }        
    }
        
    
    
    public void plotSNP(SNP snp, String attribute) {
        
        setDatasets(snp, attribute);
        attributeScale.scaleLabel().labelString(attribute);
        
        //int maxY = getMaxY(data1, data2);
        //config1.options().scales().getClass()
        //config1.options().scales().xAxis;
        //config1.options().
        
        //scale.ticks().suggestedMax(maxY);
        
        //scale.ticks().
        //chart1.refreshData();
        //chart2.refreshData();
        chart1 = new ChartJs(config1);
        chart2 = new ChartJs(config2);
        
    }
    
    public void show(String statistic, boolean show) {
        for (LineChartConfig config : new LineChartConfig[] {config1, config2}) {
            for (String genotype : new String[] {"AA", "AB", "BB"}) {
                int index = indices.get(genotype);

                if (statistic.equals("medians")) {
                    LineDataset lineDataset = (LineDataset) config.data().getDatasetAtIndex(index);
                    lineDataset.hidden(!show);
                    anchorFill(config, genotype, show);
                }
//                else if (statistic.equals("number of individuals")) {
//                        BarDataset barDataset = (BarDataset) config.data().getDatasetAtIndex(index + 5);
//                        barDataset.hidden(!show);
//                }
                else {
                    int startDiff = -1;
                    if (statistic.equals("SEM")) {
                        startDiff = 1;
                    }
                    else if (statistic.equals("confidence intervals")) {
                        startDiff = 3;
                    }
                    else {
                        return;
                    }
                    //System.out.println("statistic: " + statistic + ", " + (index + startDiff));
                    LineDataset lineDataset1 = (LineDataset) config.data().getDatasetAtIndex(index + startDiff);
                    lineDataset1.hidden(!show);
                    LineDataset lineDataset2 = (LineDataset) config.data().getDatasetAtIndex(index + startDiff + 1);
                    lineDataset2.hidden(!show);
                }
            }
        }
        chart1.refreshData();
        chart2.refreshData();
        
    }
    
    private void anchorFill(LineChartConfig config, String genotype, boolean showMedian) {
        int index = indices.get(genotype);
        int fillTo1 = -1;
        int fillTo2 = -1;
        
        if (showMedian == true) {
            fillTo1 = index;
            fillTo2 = index;
        }
        else {
            fillTo1 = index + 2;
            fillTo2 = index + 4;
        }
        
        LineDataset lineDataset1 = (LineDataset) config.data().getDatasetAtIndex(index + 1);
        lineDataset1.fill(fillTo1);
        
        LineDataset lineDataset2 = (LineDataset) config.data().getDatasetAtIndex(index + 3);
        lineDataset2.fill(fillTo2);

    }
    
    private int getMaxY(List <Double> data1, List <Double> data2) {
        Double maxY1 = Collections.max(data1);
        Double maxY2 = Collections.max(data1);
        
        int maxY = (int) Math.round(Math.max(maxY1, maxY2) + 0.05*Math.max(maxY1, maxY2));
        
        //int factor = String.valueOf(maxY).length()/10;
        
        if (maxY > 100) {
            maxY = ((maxY + 5)/10)*10;
        }
        else if (maxY > 1000) {
            maxY = ((maxY + 50)/100)*100;
        }        
        return maxY;
    }
    
    public ChartJs getChart1() {
        return chart1;
    }
    public ChartJs getChart2() {
        return chart2;
    }
}
