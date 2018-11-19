package com.utils.vaadin;

import com.main.Controller;
import com.visualization.VisualizationBox;
import com.vaadin.ui.AbstractComponent;
import com.vaadin.ui.HorizontalLayout;
import com.visualization.MoBaVisualization;
import com.visualization.MoBaVisualizationInterface;
import java.lang.reflect.InvocationTargetException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * TabWrapper ensures that visualization instances are not loaded before
 * they are displayed to the user.
 * 
 * @author Christoffer Hjeltnes Støle
 */
public class TabWrapper {
    HorizontalLayout component = new HorizontalLayout();
    Class contentClass;
    Controller controller;
    Boolean loaded = false;
    MoBaVisualizationInterface visualization;
    
    /**
     * 
     * @param contentClass - the class of the visualization instance
     * @param controller - the controller object to pass to the visualization instance
     */
    public TabWrapper (Class contentClass, Controller controller) {
        this.contentClass = contentClass;
        this.controller = controller;
        component.setSizeFull();
    }
    
    /**
     * Returns whether the tab has been loaded.
     * @return 
     */
    public boolean isLoaded() {
        return this.loaded;
    }
    
    /**
     * Load the contents of a tab.
     */
    public void loadContents() {
        visualization = getVisualization();
        component.addComponent(visualization.getComponent());
        this.loaded = true;
    }
    
    /**
     * Instantiate the class of the visualization.
     */
    private void instantiateContentClass() {
        try {
            //if (GenoView.class.isAssignableFrom(contentClass)) {
            visualization = (MoBaVisualization) contentClass.getDeclaredConstructor(Controller.class).newInstance(controller);          
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
    }
    
    /**
     * 
     * Returns the visualization instance of the tab.
     * 
     * @return 
     */
    public MoBaVisualizationInterface getVisualization() {
        if (visualization == null) {
            instantiateContentClass();
        }
        return visualization;
        
    }
    
    /**
     * Returns the root component of the tab contents.
     * 
     * @return 
     */
    public AbstractComponent getComponent() {
        return component;
    }
    
}
