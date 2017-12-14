package com.javascript;

import com.vaadin.annotations.JavaScript;
import com.vaadin.ui.AbstractJavaScriptComponent;
import java.util.List;

@JavaScript({"vaadin://javascript/plotter.js",
    "vaadin://javascript/plotly-connector.js",
    //"vaadin://javascript/plotly-latest.min.js",
    "https://cdn.plot.ly/plotly-latest.min.js",
"vaadin://javascript/main.js"})
public class PlotlyJs extends AbstractJavaScriptComponent {
    
   public PlotlyJs() {
    }
    
    public void setData(List <String> list) {
        getState().setData(list);
        //markAsDirty();
    }
    
   @Override
    public List <String> getData() {
        return getState().getData();
    }

    @Override
    public PlotlyJsState getState() {
        return (PlotlyJsState) super.getState();
    } 
    
}