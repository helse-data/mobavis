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
 * Implements a message in Vaadin that can be closed, using a tab sheet.
 * 
 * @author Christoffer Hjeltnes Støle
 */
public class ClosableMessage {
    TabSheet container = new TabSheet();
    Label message;
    String messageText;
    
    /**
     * Makes it possible to listen for the user closing the message.
     */
    public interface CloseListener extends Serializable {
        void windowClose();
    }
    ArrayList <CloseListener> listeners =
            new ArrayList();
    /**
     * Add a listener for the closing of the message.
     * 
     * @param listener 
     */
    public void addCloseListener(CloseListener listener) {
        System.out.println("closeListener added");
        listeners.add(listener);
    }
    
    /**
     * 
     * @param messageText - the message
     * @param title - the title of the message
     * @param parentLayout - the parent layout of the message
     */
    public ClosableMessage(String messageText, String title, Layout parentLayout) {
        this.messageText = messageText;
        message = new Label(messageText, ContentMode.HTML);
        
        message.setSizeFull();
        
        
        container.setSizeFull();
        container.addTab(message, title);
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
    /**
     * Returns the root component of the closable message.
     * 
     * @return 
     */
    public AbstractComponent getComponent() {
        return container;        
    }
    
}
