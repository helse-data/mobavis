package com.visualization;

import com.main.Controller;

/**
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
