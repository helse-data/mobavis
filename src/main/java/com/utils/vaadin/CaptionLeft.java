package com.utils.vaadin;

import com.vaadin.icons.VaadinIcons;
import com.vaadin.ui.AbstractComponent;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;

/**
 *
 * @author Christoffer Hjeltnes Støle
 */
public class CaptionLeft {
    HorizontalLayout box = new HorizontalLayout();
    
    public CaptionLeft(String captionString, AbstractComponent component, VaadinIcons icon) {
        Label caption = new Label(captionString);
        //caption.setIcon(icon);
        
        box.addComponent(caption);
        box.addComponent(component);
        
        box.setComponentAlignment(caption, Alignment.MIDDLE_RIGHT);
        box.setComponentAlignment(component, Alignment.MIDDLE_LEFT);
        
        box.setSizeFull();
    }
    
    public AbstractComponent getComponent() {
        return box;
    }
    
}
