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
import com.utils.Constants;
import java.util.List;

/**
 *
 * @author ChristofferHjeltnes
 */
public class LinePlot {
   
    ChartJs chart;
    LineChartConfig config = new LineChartConfig();
    String variable;
    Constants constants = new Constants();
    
    public LinePlot(String variable) {
        this.variable = variable;
        
        // create the data set
        LineDataset lineDataset = new LineDataset().label("Own data").fill(false).borderWidth(1);
        
        int[] red = {255, 0, 0};
        int[] black = {0, 0 ,0};
        
        lineDataset.borderColor(ColorUtils.toRgb(black));
        lineDataset.backgroundColor(ColorUtils.toRgb(black));
        lineDataset.spanGaps(true);
        lineDataset.lineTension(0);
        
        config.options().animation().duration(0);        
        config.data().addDataset(lineDataset);       
        
        
        config.data()
            .labels(constants.getAgesShort())
            .and()
        .options()
            .responsive(true)
            .title()
            .display(true)
            .text("Line plot of " + variable)
            .and()
        .tooltips()
            .mode(InteractionMode.INDEX)
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
        .add(Axis.Y, new LinearScale()
                .display(true)
                .scaleLabel()
                    .display(true)
                    .labelString(variable)
                    .and()
                .ticks()
                    //.suggestedMin(0)
                    //.suggestedMax(200)
                    .and()
                .position(Position.RIGHT))
        .and()
        .done();
        
        config.options().maintainAspectRatio(false);
        
        chart = new ChartJs(config);
        chart.setJsLoggingEnabled(true);
    }
    
    public void updateDataset(List <Double> dataList) {
        Dataset <?, ?> ds = config.data().getDatasetAtIndex(0);
        LineDataset lineDataset = (LineDataset) ds;
        lineDataset.dataAsList(dataList);
       
        chart.refreshData();
    }

    public ChartJs getChart() {
        return chart;
    }    
}
