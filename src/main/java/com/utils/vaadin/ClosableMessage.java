package com.utils.vaadin;

import com.vaadin.shared.ui.ContentMode;
import com.vaadin.ui.AbstractComponent;
import com.vaadin.ui.Component;
import com.vaadin.ui.Label;
import com.vaadin.ui.Layout;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.themes.ValoTheme;
import java.io.Serializable;
import java.util.ArrayList;

/**
 *
 * @author Christoffer Hjeltnes Støle
 */
public class ClosableMessage {
    TabSheet container = new TabSheet();
    Label message;
    String messageText;
    
    
    public interface CloseListener extends Serializable {
        void windowClose();
    }
    ArrayList <CloseListener> listeners =
            new ArrayList();
    public void addCloseListener(CloseListener listener) {
        System.out.println("closeListener added");
        listeners.add(listener);
    }
    
    public ClosableMessage(String messageText, String tabCaption, Layout parentLayout) {
        this.messageText = messageText;
        message = new Label(messageText, ContentMode.HTML);
        
        message.setSizeFull();
        
        
        container.setSizeFull();
        container.addTab(message, tabCaption);
        //container.addStyleName("rightaligned-tabs");
        container.addStyleName(ValoTheme.TABSHEET_CENTERED_TABS);
        container.getTab(message).setClosable(true);
        
        
        container.getTab(message).setClosable(true);
        
        container.setCloseHandler(new TabSheet.CloseHandler() {
            @Override
            public void onTabClose(TabSheet tabsheet, Component tabContent) {
                for (CloseListener listener: listeners) {
                    listener.windowClose();
                }
                //container.removeComponent(message);
                parentLayout.removeComponent(container);
                //System.out.println("Tab closed");
                //parentLayout.markAsDirty();
                //parentLayout.getUI().push();
            }
        });
        
        
    }
    
    public AbstractComponent getComponent() {
        return container;        
    }
    
}
