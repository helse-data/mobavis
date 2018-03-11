package com.plotly;

import com.vaadin.shared.ui.JavaScriptComponentState;
import java.util.List;
import java.util.Map;

public class OverlayPlotState extends JavaScriptComponentState {    
    private Map <String, List <String>> data;
    
    public void setData(Map <String, List <String>> data) {
        this.data = data;
    }
    
    public Map <String, List <String>> getData() {
        return data;
    }
}