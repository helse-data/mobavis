package com.plotly;

import com.vaadin.annotations.JavaScript;
import elemental.json.Json;

/**
 *
 * @author Christoffer Hjeltnes Støle
 */
@JavaScript({"vaadin://plotly/scatterPlot.js"})
public class ScatterPlot extends PlotlyJs {
    
    public ScatterPlot() {
        setUp(Json.createObject());
    }
    
}
