package com.plotly;

import com.vaadin.annotations.JavaScript;

/** Implements a Vaadin component for a plot that allows the user to overlay data.
 * 
 * @author Christoffer Hjeltnes Støle
 */
@JavaScript({"vaadin://plotly/overlayPlot.js"})
public class OverlayPlot extends OverlayCommon {
   public OverlayPlot(String phenotypeID) {
       super(phenotypeID);
   }
}