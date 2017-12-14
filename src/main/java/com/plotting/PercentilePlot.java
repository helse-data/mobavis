package com.plotting;


import com.byteowls.vaadin.chartjs.ChartJs;
import com.byteowls.vaadin.chartjs.config.LineChartConfig;
import com.byteowls.vaadin.chartjs.config.ScatterChartConfig;
import com.byteowls.vaadin.chartjs.data.LineDataset;
import com.byteowls.vaadin.chartjs.data.ScatterDataset;
import com.byteowls.vaadin.chartjs.options.scale.Axis;
import com.byteowls.vaadin.chartjs.options.scale.LinearScale;
import com.byteowls.vaadin.chartjs.utils.ColorUtils;
import com.utils.Constants;
import com.vaadin.server.VaadinService;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;


/**
 *
 * @author ChristofferHjeltnes
 */
public class PercentilePlot {   
    double[] percentiles = {1, 5, 10, 25, 50, 75, 90, 95, 99};
    double[] paraPercentiles = {1, 50, 99};
    //String attribute;
    List <List <Double>> percentileData;
    List <List <Double>> percentileDataX;
    List <List <Double>> percentileDataY;
    boolean isParameterised;
    //Set <Component> hasPercentiles;
    ChartJs chart;
    boolean constructed = false;
    boolean percentilesShown = false;
    //String xAttribute;
    //String yAttribute;
    
    Constants constants = new Constants();
        
    int index;
    
    public PercentilePlot(String attribute, ChartJs chart, int index) {
        this.index = index;
        //this.attribute = attribute;
        //hasPercentiles = components;
        this.chart = chart;
        percentileData = readPercentileData(attribute);
    }
    
    public PercentilePlot(String xAttribute, String yAttribute, ChartJs chart, int index) {
        this.index = index;
        this.chart = chart;
        //hasPercentiles = components;
        //this.xAttribute = xAttribute;
        //this.yAttribute = yAttribute;
        percentileDataX = readPercentileData(xAttribute);
        percentileDataY = readPercentileData(yAttribute);
        isParameterised = true;
    }
    
    private List <List <Double>> readPercentileData(String attribute) {
        List <List <Double>> data = new ArrayList();
        try {
            String basepath = VaadinService.getCurrent().getBaseDirectory().getAbsolutePath();
            String fileName = basepath + "/WEB-INF/percentiles " + attribute + ".csv";

            InputStream inputStream = new FileInputStream(fileName);
            Reader reader = new InputStreamReader(inputStream);
            BufferedReader bufferedReader = new BufferedReader(reader);
            String line;
            int i = 0;
            while ((line = bufferedReader.readLine()) != null) {
                data.add(new ArrayList());
                for (String value : line.split(",")) {
                    data.get(i).add(Double.parseDouble(value));
                }
                i++;
            }
            //System.out.println(masterIndex);
            //System.exit(0);
            inputStream.close();
            reader.close();
            bufferedReader.close();
        }
        catch (IOException e) {
            System.out.println(e.getMessage());
        }
        return data;
    }
   
    public void show(boolean show) {
        LineChartConfig configL = null;
        ScatterChartConfig configS = null;
        //for (Component component : hasPercentiles) {
        //System.out.println(activeComponent == chart);
        //if (activeComponent == chart) {                
            if (!isParameterised) {
                configL = (LineChartConfig) chart.getConfig();
            }
            else {
                configS = (ScatterChartConfig) chart.getConfig();
            }
            if (!constructed && show) {
                if (!isParameterised) {
                    common(configL, index);
                    constructed = true;
                }
                else {
                    parameterise(configS, index);
                    constructed = true;
                }

                }
            else if (constructed) {
                //config.options().animation().duration(0);
                if (!isParameterised) {
                    for (int i = index; i < percentiles.length + index; i++) {
                        LineDataset lds = (LineDataset) configL.data().getDatasetAtIndex(i);
                        lds.hidden(!show);                
                    }
                }
                else {
                    for (int i = index; i < paraPercentiles.length + index; i++) {
                        ScatterDataset lds = (ScatterDataset) configS.data().getDatasetAtIndex(i);
                        lds.hidden(!show);                
                    }
                }
            }                
        //}
    }
        
    
    public ChartJs getChart() {                
        LineChartConfig config = new LineChartConfig();
        config.data();
        common(config);
        
        chart = new ChartJs(config);
        //chart.setJsLoggingEnabled(true);
        
        config
            .data()
                .labels(constants.getAgesShort());
        config.
            options()
                .maintainAspectRatio(true)
                .elements()
                    .line()
                        //.tension(0)
                        .and()
                    .and()
                .scales()
                    .add(Axis.Y, new LinearScale()
                    .display(true)
                    .scaleLabel()
                    .labelString("height")
                    .and())
                .and()
                .title()
                    .display(true)
                    .text("Percentiles")
                    .and()
                //.legend().display(false).and()
               .done();

        return chart;
    }

    private void common(LineChartConfig config) {
        common(config, 0);
    }
    
    
    private void common(LineChartConfig config, int index) {
        for (int i=0; i < percentiles.length - 1; i++) {
            config.data().addDataset(new LineDataset().fill(true, 1).borderWidth(1).pointRadius(0));
        }
        config.data().addDataset(new LineDataset().fill(false).borderWidth(1).pointRadius(0));

        int[][] colours = getColours(percentiles.length, percentiles);
        
        for (int i = index; i < percentiles.length + index; i++) {
            LineDataset lds = (LineDataset) config.data().getDatasetAtIndex(i);
            lds.lineTension(0);
            lds.label(Double.toString(percentiles[i-index]));

            int[] rgb;
            if (percentiles[i-index] == 50) {
                lds.borderDash(5, 5);
            }

             rgb = colours[i-index];
            
            lds.borderColor(ColorUtils.toRgb(rgb));
            lds.backgroundColor(ColorUtils.toRgba(rgb, 0.5));
            lds.spanGaps(true);
            

            List<Double> rowData = percentileData.get(i-index);
            lds.dataAsList(rowData);
        }        
    }
    
    private void parameterise(ScatterChartConfig config, int index) {
        for (int i=0; i < paraPercentiles.length - 1; i++) {
            config.data().addDataset(new ScatterDataset().fill(false).borderWidth(1).pointRadius(1));
        }
        config.data().addDataset(new ScatterDataset().fill(false).borderWidth(1).pointRadius(1));

        int[][] colours = getColours(paraPercentiles.length, paraPercentiles);
        
        for (int i = index; i < paraPercentiles.length + index; i++) {
        //for (int i = 4 + index; i < 4 + index + 1; i++) {    
            ScatterDataset sds = (ScatterDataset) config.data().getDatasetAtIndex(i);
            sds.lineTension(0);
            //ScatterDataset sds = (ScatterDataset) config.data().getDatasetAtIndex(2);
            sds.label(Double.toString(paraPercentiles[i-index]));

            int[] rgb;
            if (paraPercentiles[i-index] == 50) {
                sds.borderDash(5, 5);
            }

             rgb = colours[i-index];
            
            sds.borderColor(ColorUtils.toRgb(rgb));
            sds.backgroundColor(ColorUtils.toRgba(rgb, 0.5));
            sds.spanGaps(true);
            
            //List<Double> rowData = percentileData.get(i-index);
            //lds.dataAsList(rowData);
            
            // add x,y points
            // TODO: choose individuals at each data point?
            for (int j = 0; j < percentileDataX.get(0).size(); j++) {
                List<Double> point = new ArrayList();
                int percentileIndex = getPercentileIndex(paraPercentiles[i-index]);
                point.add(percentileDataX.get(percentileIndex).get(j));
                point.add(percentileDataY.get(percentileIndex).get(j));
                sds.addData(point.get(0), point.get(1));
            }
        }
    }
    
    private int getPercentileIndex(double percentile) {
        for (int i = 0; i < percentiles.length; i++) {
            if (percentiles[i] == percentile) {
                return i;
            }
        }
        return -1;
    }
    
    
    int[][] getColours(int n, double[] percentileList) {
        int[] base = new int[]{54, 162, 235};
        float diff = (float) 160/n;
        int[][] colours = new int[n][3];
        //System.out.println("diff: " + diff + ", n: " + n);
        
        // the percentile closest to 50 should have the darkest colour
        double closestDistance = 100;
        int closest = 0;
        for (int i=0; i < percentileList.length; i++) {
            if (Math.abs(50 - percentileList[i]) < closestDistance) {
                closest = i;
                closestDistance = Math.abs(50 - percentileList[i]);
            }
        }
        
        // create the colour gradients        
        
        for (int i=closest; i < percentileList.length; i++) {
            int j = i - closest;
            //System.out.println(j);
            colours[i] = new int[]{Math.round(base[0] + j*diff),
                Math.round(base[1] + j*diff),
                Math.round(base[2] + j*diff)};
        }        
        for (int i = closest - 1; i > -1; i--) {
            int j = closest - i - 1;
            //System.out.println(j);
            colours[i] = new int[]{Math.round(base[0] + j*diff),
                Math.round(base[1] + j*diff),
                Math.round(base[2] + j*diff)};
        }
        
        // deal with exceptions
        colours[closest-1] = colours[closest];
        colours[colours.length - 1] = colours[colours.length - 2];
        
        //for (int i =0; i < colours.length; i++) {
            //System.out.println("Colour for percentile " + percentileList[i] + ", blue: " + colours[i][2]);
            //for (int rgb : colour) {
           //     System.out.print(rgb + ", ");
            //}
            //System.out.println();
        //}
        
        return colours;
    }
    
}
