package com.visualization;

import com.vaadin.ui.AbstractComponent;

/**
 *
 * Interface for the MoBaVisualization abstract class.
 * 
 * @author Christoffer Hjeltnes Støle
 */
public interface MoBaVisualizationInterface {
    /**
     * Returns the root component of the visualization instance.
     * 
     * @return 
     */
    public AbstractComponent getComponent();
    /**
     * not in use
     */
    public void resizePlots();
    /**
     * Called when the visualization is displayed to the user.
     */
    public void handOver();
}
