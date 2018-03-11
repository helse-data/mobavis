package com.plotly;

import com.vaadin.annotations.JavaScript;

@JavaScript({"vaadin://plotly/overlayPlot.js"})
public class OverlayPlot extends OverlayCommon {
   public OverlayPlot(String phenotypeID) {
       super(phenotypeID);
   }
}