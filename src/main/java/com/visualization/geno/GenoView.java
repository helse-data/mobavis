package com.visualization.geno;

import com.main.Controller;
import com.visualization.MoBaVisualization;

/**
 *
 * Abstract class for classes of the visualization instances that visualize genetic data.
 * 
 * @author Christoffer Hjeltnes Støle
 */
public abstract class GenoView extends MoBaVisualization implements GenoViewInterface {
    public GenoView(Controller controller) {
        super(controller);
    };
}
    

