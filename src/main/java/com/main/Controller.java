package com.main;

import com.main.Controller.Visualization;
import com.snp.InputSNP;
import com.snp.SNP;
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

/**
 * 
 * Class to ensure smooth navigation between the different contents of the application.
 * An instance of it is meant to be passed on to the constructor of all higher-level content classes, such as the different visualizations.
 *
 * @author Christoffer Hjeltnes Støle
 */
public class Controller {
    SNP SNP;
    ComboBox SNPInputField;
    
    UI ui;
    InputSNP inputSNP;
    LandingPage landingPage;
    VisualizationBox visualizationBox;
       
    public Controller(UI ui) {
        this.ui = ui;
        SNPInputField = new ComboBox("SNP");
    }
    
    public void setVisualization(Visualization visualization) {
        if (visualizationBox == null) {
            visualizationBox = new VisualizationBox(this);
        }
        visualizationBox.setVisualization(visualization);
    }
    
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
    
    public void SNPIDinputChanged() {
        SNP = null;
        System.out.println("SNP input change registered in controller.");
        //componentMap.get("SNP input").getData()
    }
    
    public void setSNPObject(SNP snp) {
        SNP = snp;    
    }
    public SNP getSNPObject() {
        return SNP;
    }
    public void setInputSNP(InputSNP inputSNP) {
        this.inputSNP = inputSNP;
    }
    public InputSNP getInputSNP() {
        return inputSNP;
    }
    public ComboBox getSNPInputField() {
        return SNPInputField;
    }
    
    
    /**
     * 
     * The list of visualizations available in the application is defined here.
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
        CHILD(MotherVisBox.class, "Child", false),
        MOTHER(MotherVisBox.class, "Mother", false),
        FATHER(MotherVisBox.class, "Father", false);
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
        
        public Class getViewClass() {
            return visualizationClass;
        }
        
        public boolean hasGeneticData() {
            return hasGeneticData;
        }
    }
    
    public void SNPInputChanged (ValueChangeEvent event) {
        
    }
    
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
