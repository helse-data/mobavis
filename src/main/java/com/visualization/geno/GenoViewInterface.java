package com.visualization.geno;

import com.visualization.MoBaVisualizationInterface;

/**
 * 
 * Interface for the GenoView abstract class.
 *
 * @author Christoffer Hjeltnes Støle
 */
public interface GenoViewInterface extends MoBaVisualizationInterface {
    
    /**
     * Makes the currently selected SNP take effect.
     * 
     */
    public void updateSNP();
    
}
