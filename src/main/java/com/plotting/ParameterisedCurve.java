package com.plotting;

import com.byteowls.vaadin.chartjs.ChartJs;
import com.byteowls.vaadin.chartjs.config.ScatterChartConfig;
import com.byteowls.vaadin.chartjs.data.Dataset;
import com.byteowls.vaadin.chartjs.data.ScatterData;
import com.byteowls.vaadin.chartjs.data.ScatterDataset;
import com.byteowls.vaadin.chartjs.options.InteractionMode;
import com.byteowls.vaadin.chartjs.options.Position;
import com.byteowls.vaadin.chartjs.options.scale.Axis;
import com.byteowls.vaadin.chartjs.options.scale.LinearScale;
import com.byteowls.vaadin.chartjs.utils.ColorUtils;
import java.util.List;

/**
 *
 * @author ChristofferHjeltnes
 */
public class ParameterisedCurve {
    
    ChartJs chart;
    ScatterChartConfig config = new ScatterChartConfig();
    String variable1;
    String variable2;
    
    public ParameterisedCurve(String variable1, String variable2) {
        this.variable1 = variable1;
        this.variable2 = variable2;
        
        int[] red = {255, 0, 0};
        int[] black = {0, 0 ,0};
        
        ScatterDataset scatterDataset = new ScatterDataset().label("Own data").fill(false).borderWidth(1);
        scatterDataset.borderColor(ColorUtils.toRgb(black));
        scatterDataset.backgroundColor(ColorUtils.toRgb(black));
        scatterDataset.spanGaps(true);
        scatterDataset.lineTension(0);
        
        config.data().addDataset(scatterDataset);
        config.options().animation().duration(0); 
        
        
        config.
            options()
                .tooltips()
                    .intersect(true)
                    .mode(InteractionMode.NEAREST)
                    //.callbacks()
                    .and()
                .responsive(true)
                .hover()
                    .mode(InteractionMode.NEAREST)
                    .intersect(false)
                    .and()
                .title()
                    .display(true)
                    .text("Parameterised curve - " + variable1 + " versus " + variable2)
                    .and()
                .scales()
                    .add(Axis.X, new LinearScale()
                            .scaleLabel()
                                .display(true)
                                .labelString(variable1)
                                .and()
                            .position(Position.BOTTOM).gridLines().zeroLineColor("rgba(0,0,0,1)").and())
                    .add(Axis.Y, new LinearScale()
                            .display(true)
                            .scaleLabel()
                                .display(true)
                                .labelString(variable2)
                                .and()
                            .position(Position.LEFT))
                    .and()
               .done();
        
        //curve1
        
        //Point point = new Point(2, 3);
        
        config.options().maintainAspectRatio(false);
        
        chart = new ChartJs(config);
        chart.setJsLoggingEnabled(true);
        
    }
       
    public ChartJs getChart() {
        return chart;
    }
        
    public void updateDataset(List <Double> dataList1, List <Double> dataList2) {
        Dataset<?, ?> ds = config.data().getDatasetAtIndex(0);    
        ScatterDataset scatterDataset = (ScatterDataset) ds; 
       
        ScatterData[] scatterData = new ScatterData[dataList1.size()];
        
        // add x,y points
        for (int i = 0; i < dataList1.size(); i++) {
            ScatterData sd = new ScatterData();
            sd.x(dataList1.get(i));
            sd.y(dataList2.get(i));
            scatterData[i] = sd;
        }
        
        scatterDataset.data(scatterData);
        
        chart.refreshData();        
    }    
}
