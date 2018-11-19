package com.plotly;

/**
 * State class for the Manhattan plot.
 * 
 * @author Christoffer Hjeltnes Støle
 */
public class ManhattanPlotState extends PlotlyJsState {
    private String clickedSNP;

    /**
     * Returns the SNP that was clicked.
     * 
     * @return 
     */
    public String getClickedSNP() {
        return clickedSNP;
    }

    /**
     * Stores the SNP that was clicked.
     * 
     * @param clickedSNP 
     */
    public void setClickedSNP(String clickedSNP) {
        this.clickedSNP = clickedSNP;
    }
    
}