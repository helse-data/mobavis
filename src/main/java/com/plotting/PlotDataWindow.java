package com.plotting;

import com.vaadin.server.Sizeable;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.Window;

/**
 *
 * @author Christoffer Hjeltnes Støle
 */
public class PlotDataWindow {    
    Window window = new Window();
    TabSheet tabSheet = new TabSheet();

    
    public PlotDataWindow() {
        window.center();
        window.setCaption("Plot data");
        window.setContent(tabSheet);
        window.setWidth(80, Sizeable.Unit.PERCENTAGE);
        window.setHeight(80, Sizeable.Unit.PERCENTAGE);
        
        tabSheet.setSizeFull();
    }
    
    public void setTab(String indexString, String data, String tabCaption ){
        int index = Integer.parseInt(indexString) - 1;
        if (tabSheet.getTab(index) == null) {
            HorizontalLayout layout = new HorizontalLayout(); // use a layout as the root to produce scrolling
            layout.setSizeFull();
            
            TextArea textArea = new TextArea();
            textArea.setReadOnly(true);
            textArea.setSizeFull();
            
            //tabSheet.addTab(textArea, index);
            layout.addComponent(textArea);
            tabSheet.addTab(layout, index);
            
            tabSheet.getTab(index).setCaption(tabCaption);
        }
        HorizontalLayout layout = (HorizontalLayout) tabSheet.getTab(index).getComponent();
        
        //TextArea tab = (TextArea) tabSheet.getTab(index).getComponent();
        TextArea tab = (TextArea) layout.getComponent(0);
        if (data == null) {
            data = "No data to display.";
        }
        tab.setValue(data);
    }
    
    public Component getComponent() {
        return window;
    }
    
}
