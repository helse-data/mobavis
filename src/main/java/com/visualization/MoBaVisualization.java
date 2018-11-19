package com.visualization;

import com.main.Controller;

/**
 * 
 * Abstract class for classes of the visualization instances.
 * 
 * A visualization class is the highest-level class specific
 * for a visualization (such as a Manhattan plot), and is where
 * the visualization should be initiated.
 *
 * @author Christoffer Hjeltnes Støle
 */
public abstract class MoBaVisualization implements MoBaVisualizationInterface {
    Controller controller;
    
    /**
     * 
     * @param controller - the shared controller object for all visualization instances
     */
    public MoBaVisualization(Controller controller) {
        this.controller = controller;        
    };
    
    /**
     * Returns the controller object.
     * @return 
     */
    public Controller getController() {
        return controller;
    }
    
}
