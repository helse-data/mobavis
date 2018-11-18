package com.visualization;

import com.main.Controller;

/**
 * 
 * Abstract class for containers of the visualizations.
 * 
 * A container class is the highest-level class specific for a visualization, and is where
 * the visualization should be initiated.
 *
 * @author Christoffer Hjeltnes Støle
 */
public abstract class MoBaVisualization implements MoBaVisualizationInterface {
    Controller controller;
    
    public MoBaVisualization(Controller controller) {
        this.controller = controller;        
    };
    public Controller getController() {
        return controller;
    }
    
}
