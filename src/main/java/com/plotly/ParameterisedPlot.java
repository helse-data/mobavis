package com.plotly;

import com.vaadin.annotations.JavaScript;

@JavaScript({"vaadin://plotly/parameterisedPlot.js"})
public class ParameterisedPlot extends OverlayCommon {    
   public ParameterisedPlot() {
       super(null);
   }
}