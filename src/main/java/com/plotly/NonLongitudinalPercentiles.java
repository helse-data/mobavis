package com.plotly;

import com.vaadin.annotations.JavaScript;
import elemental.json.Json;

/**
 * 
 * Implements a Vaadin component for a plot visualizing non-longitudinal percentile data.
 *
 * @author Christoffer Hjeltnes Støle
 */
@JavaScript({"vaadin://plotly/nonLongitudinalPercentiles.js"})
public class NonLongitudinalPercentiles extends PlotlyJs {
    
    public NonLongitudinalPercentiles() {
        setUp(Json.createObject());
    }
    
}
