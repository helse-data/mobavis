package com.plotly;

import com.vaadin.annotations.JavaScript;
import com.vaadin.ui.JavaScriptFunction;
import elemental.json.JsonArray;
import java.io.Serializable;
import java.util.ArrayList;

/**
 *
 * @author Christoffer Hjeltnes Støle
 */

@JavaScript({
    "vaadin://plotly/manhattanPlot.js"
})
public class ManhattanPlot extends PlotlyJs {

    public interface ValueChangeListener extends Serializable {
        void valueChange();
    }
    ArrayList <ValueChangeListener> listeners =
            new ArrayList();
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
    
    public String getClickedSNP() {
        return getState().getClickedSNP();
    }
    
    @Override
    public ManhattanPlotState getState() {
        return (ManhattanPlotState) super.getState();
    }
}