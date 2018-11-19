package com.plotly;

import com.vaadin.annotations.JavaScript;
import com.vaadin.ui.JavaScriptFunction;
import elemental.json.JsonArray;
import java.io.Serializable;
import java.util.ArrayList;

/**
 *
 * Implements a Vaadin component for a Manhattan plot.
 * 
 * @author Christoffer Hjeltnes Støle
 */

@JavaScript({
    "vaadin://plotly/manhattanPlot.js"
})
public class ManhattanPlot extends PlotlyJs {

    /**
     * Enables listening for clicked SNPs.
     */
    public interface ValueChangeListener extends Serializable {
        void valueChange();
    }
    ArrayList <ValueChangeListener> listeners =
            new ArrayList();
    /**
     * Adds a listener for clicked SNPs.
     * 
     * @param listener 
     */
    public void addValueChangeListener(
                   ValueChangeListener listener) {
        listeners.add(listener);
    }

    
    public ManhattanPlot() {
        addFunction("onSNPclick", new JavaScriptFunction() {
            @Override
            public void call(JsonArray arguments) {
                System.out.println("arguments: " + arguments.toJson());
                getState().setClickedSNP(arguments.getString(0));
                for (ValueChangeListener listener: listeners) {
                    listener.valueChange();
                }
            }
        });
    }
    
    /**
     * Returns the clicked SNP.
     * 
     * @return 
     */
    public String getClickedSNP() {
        return getState().getClickedSNP();
    }
    
    @Override
    public ManhattanPlotState getState() {
        return (ManhattanPlotState) super.getState();
    }
}