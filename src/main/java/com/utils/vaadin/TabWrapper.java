package com.utils.vaadin;

import com.main.Controller;
import com.visualization.VisualizationBox;
import com.vaadin.ui.AbstractComponent;
import com.vaadin.ui.HorizontalLayout;
import com.visualization.MoBaVisualization;
import java.lang.reflect.InvocationTargetException;
import java.util.logging.Level;
import java.util.logging.Logger;
import com.visualization.State;

/**
 *
 * @author Christoffer Hjeltnes Støle
 */
public class TabWrapper {
    HorizontalLayout component = new HorizontalLayout();
    Class contentClass;
    Controller controller;
    Boolean loaded = false;
    
    public TabWrapper (Class contentClass, Controller controller) {
        this.contentClass = contentClass;
        this.controller = controller;
        component.setSizeFull();
    }
    
    public boolean isLoaded() {
        return this.loaded;
    }
    
    public void loadContents() {
        MoBaVisualization visualization = instantiateContentClass();
        component.addComponent(visualization.getComponent());
        this.loaded = true;
    }
    
    private MoBaVisualization instantiateContentClass() {
        MoBaVisualization view = null;
        try {
            //if (GenoView.class.isAssignableFrom(contentClass)) {
            view = (MoBaVisualization) contentClass.getDeclaredConstructor(Controller.class).newInstance(controller);          
        } catch (InstantiationException ex) {
            Logger.getLogger(VisualizationBox.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            Logger.getLogger(VisualizationBox.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NoSuchMethodException ex) {
            Logger.getLogger(TabWrapper.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SecurityException ex) {
            Logger.getLogger(TabWrapper.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalArgumentException ex) {
            Logger.getLogger(TabWrapper.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InvocationTargetException ex) {
            Logger.getLogger(TabWrapper.class.getName()).log(Level.SEVERE, null, ex);
        }
        return view;
    }
    
    public AbstractComponent getComponent() {
        return component;
    }
    
}
