package com.visualization;

import com.vaadin.ui.AbstractComponent;

/**
 *
 * Interface for the MoBaVisualization abstract class.
 * 
 * @author Christoffer Hjeltnes Støle
 */
public interface MoBaVisualizationInterface {    
    public AbstractComponent getComponent();
    public void resizePlots();
    /**
     * Called when the visualization is displayed to the user.
     */
    public void handOver();
}
