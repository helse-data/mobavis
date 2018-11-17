package com.main;

import com.main.Controller.Visualization;
import com.snp.InputSNP;
import com.snp.VerifiedSNP;
import com.vaadin.data.HasValue;
import com.vaadin.data.HasValue.ValueChangeEvent;
import com.vaadin.ui.AbstractComponent;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.UI;
import com.visualization.VisualizationBox;
import com.visualization.geno.LocusZoomBox;
import com.visualization.geno.ManhattanPlotBox;
import com.visualization.geno.SNPPlotBox;
import com.visualization.geno.SNPStatisticsBox;
import com.visualization.summary.MotherVisBox;
import com.visualization.summary.SummaryStatisticsBox;
import java.util.Map;
import com.snp.SNP;

/**
 * 
 * Controller ensures smooth navigation between the different contents of the application.
 * An instance of this class is meant to be passed on to the constructor of all higher-level content classes, such as the different visualizations.
 *
 * @author Christoffer Hjeltnes Støle
 */
public class Controller {
    VerifiedSNP SNP;
    SNP activeSNP;
    ComboBox SNPInputField;
    
    UI ui;
    InputSNP inputSNP;
    LandingPage landingPage;
    VisualizationBox visualizationBox;
       
    public Controller(UI ui) {
        this.ui = ui;
        SNPInputField = new ComboBox("SNP");
        activeSNP = new InputSNP("rs13046557", "21", "16588359"); // default SNP
    }
    
    /**
     * Set which visualizatin tab to display to the user.
     * 
     * @param visualization 
     */
    public void setVisualization(Visualization visualization) {
        if (visualizationBox == null) {
            visualizationBox = new VisualizationBox(this);
        }
        visualizationBox.setVisualization(visualization);
    }
    
    /**
     * Sets the content type.
     * 
     * @param contentType - landing page or visualization 
     */
    public void setContentType(ContentType contentType) {
        if (contentType == ContentType.LANDING_PAGE) {
            if (landingPage == null) {
                landingPage = new LandingPage(this);
            }
            ui.setContent(landingPage.getComponent());
        }
        else if (contentType == ContentType.VISUALIZATION) {
            if (visualizationBox == null) {
                visualizationBox = new VisualizationBox(this);
            }
            ui.setContent(visualizationBox.getComponent());
        }
    }
    
    /**
     * Sets which SNP object is active.
     * 
     * @param snp 
     */
    public void setActiveSNP(SNP snp) {
        activeSNP = snp;    
    }
    
    /**
     * Returns the active SNP object.
     * @return 
     */
    public SNP getActiveSNP() {
        return activeSNP;
    }    
    
    /**
     * 
     * The visualizations available in the web application are listed here.
     * 
     * Each visualization must have its own class and implement the MobaVisualizationInterface or extentions of it.
     * The classes must be extensions of the MoBaVisualization class or extensions of it.
     * 
     */
    public enum Visualization {
        MANHATTAN(ManhattanPlotBox.class, "Manhattan plot", true),
        LOCUS_ZOOM(LocusZoomBox.class, "Locus zoom plot", true),
        VARIANT_PLOT(SNPPlotBox.class, "Phenotype by SNP genotype", true),        
        SNP_STATISTICS(SNPStatisticsBox.class, "SNP statistics", true),
        
        SUMMARY_STATISTICS(SummaryStatisticsBox.class, "Summary statistics for the MoBa cohort", false),
        //CHILD(MotherVisBox.class, "Child", false),
        MOTHER(MotherVisBox.class, "Mother", false);
        //FATHER(MotherVisBox.class, "Father", false);
        private final Class visualizationClass;
        private final String displayName;
        private final boolean hasGeneticData;
        
        /**
         * 
         * @param visualizationClass - the class of the visualization
         * @param displayName - the name that the visualization will have in the menu
         * @param hasGeneticData - whether or not the visualization visualizes data with genetic information
         */
        Visualization(Class visualizationClass, String displayName, boolean hasGeneticData) {
            this.visualizationClass = visualizationClass;
            this.displayName = displayName;
            this.hasGeneticData = hasGeneticData;
        }
        
        @Override
        public String toString() {
            return displayName;
        }
        
        public Class getVisualizationClass() {
            return visualizationClass;
        }
        
        public boolean hasGeneticData() {
            return hasGeneticData;
        }
    }
    
    /**
     * 
     * The content types available in the web application are listed here.
     * 
     */    
    public enum ContentType {
        LANDING_PAGE("Landing page"),
        VISUALIZATION("Visualization");

        private final String displayName;        
     
        ContentType(String displayName) {
            this.displayName = displayName;
        }
        
        @Override
        public String toString() {
            return displayName;
        }
    }
}
