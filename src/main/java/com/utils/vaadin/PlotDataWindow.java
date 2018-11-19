package com.utils.vaadin;

import com.vaadin.server.Sizeable;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.Window;

/**
 * PlotDataWindow provides a means to display the data underlying a plot.
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
    
    /**
     * Adds or adjusts a tab in the tabshet of the window.
     * 
     * @param indexString
     * @param data
     * @param tabCaption 
     */
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
    /**
     * Returns the window.
     * 
     * @return 
     */
    public Component getComponent() {
        return window;
    }
    
}
