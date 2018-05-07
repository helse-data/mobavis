package com.plotly;

import com.vaadin.annotations.JavaScript;
import elemental.json.Json;

/**
 *
 * @author Christoffer Hjeltnes Støle
 */
@JavaScript({"vaadin://plotly/nonLongitudinalPercentiles.js"})
public class NonLongitudinalPercentiles extends PlotlyJs {
    
    public NonLongitudinalPercentiles() {
        setUp(Json.createObject());
    }
    
}
