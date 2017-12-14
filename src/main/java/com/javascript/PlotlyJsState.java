package com.javascript;

import com.vaadin.shared.ui.JavaScriptComponentState;
import java.util.List;

public class PlotlyJsState extends JavaScriptComponentState {
    
    private List <String> data;       
    
    public void setData(List <String> value) {
        this.data = value;
    }
    
    public List <String> getData() {
        return data;
    }
}