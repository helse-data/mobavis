package com.visualization.geno;

import com.locuszoom.LocusZoom;
import com.main.Controller;
import com.snp.InputSNP;
import com.snp.SNP;
import com.snp.VerifiedSNP;
import com.utils.Constants;
import com.vaadin.event.ShortcutAction;
import com.vaadin.event.ShortcutListener;
import com.vaadin.server.Page;
import com.vaadin.ui.AbstractComponent;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.NativeSelect;
import com.vaadin.ui.Notification;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.visualization.MoBaVisualization;
import elemental.json.Json;
import elemental.json.JsonObject;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Class for the visualization instance for the regional plot (locus zoom plot).
 *
 * @author Christoffer Hjeltnes Støle
 */
public class LocusZoomBox extends GenoView {    
    LocusZoom locusZoom = new LocusZoom();
    Constants constants = new Constants();
    VerticalLayout box = new VerticalLayout();
    HorizontalLayout navigationBox = new HorizontalLayout();
    NativeSelect <String> chromosomeSelector;
    TextField positionSpecifier;
    Button selectButton = new Button("Go");
    String defaultPosition = "16588359";
    String defaultChromosome = "21";
    Map <String, Integer> chromosomeSizeMap;
    
    public LocusZoomBox (Controller controller) {
        super(controller);
        chromosomeSelector = new NativeSelect("Chromosome", Arrays.asList(constants.getChromosomeList()));
        chromosomeSelector.setEmptySelectionAllowed(false);        
        
        positionSpecifier = new TextField();
        int maxLength = 0;
        for (int element : constants.getChromosomeSizes()) {
            if (element > maxLength) {
                maxLength = element;
            }
        }
        positionSpecifier.setCaption("Position");
        positionSpecifier.setMaxLength(maxLength);
        
        positionSpecifier.addShortcutListener(new ShortcutListener("Enter", ShortcutAction.KeyCode.ENTER, null) {
                    @Override
                    public void handleAction(Object sender, Object target) {
                        useInputFields();
                    }
                    });
        
        selectButton.addClickListener(event -> useInputFields());
        
        navigationBox.addComponent(chromosomeSelector);
        navigationBox.addComponent(positionSpecifier);
        navigationBox.addComponent(selectButton);
        navigationBox.setComponentAlignment(selectButton, Alignment.BOTTOM_CENTER);
        
        box.addComponent(navigationBox);
        box.setComponentAlignment(navigationBox, Alignment.TOP_CENTER);
        box.addComponent(locusZoom);
        
        chromosomeSizeMap = constants.getChromosomeSizeMap();
        
        locusZoom.addValueChangeListener(new LocusZoom.ValueChangeListener() {
                @Override
                public void valueChange() {
                    String clickedPosition = locusZoom.getClickedSNP();
                    System.out.println("Data received in view box: " + clickedPosition);
                    positionSpecifier.setValue(clickedPosition);
                    // new SNP clicked by the user
                    SNP activeSNP = new InputSNP(chromosomeSelector.getValue(), clickedPosition);
                    getController().setActiveSNP(activeSNP);
                }
            });
        
    }
    
    /**
     * Sets the region for LocusZoom.js through a SNP object.
     * 
     * @param snp 
     */
    public void setSNP(SNP snp) {        
        String position;
        String chromosome;
        
        if (snp != null) {
            position = snp.getPosition();
            chromosome = snp.getChromosome();
        }
        else {
            position = defaultPosition;
            chromosome = defaultChromosome;
        }
        setRegion(chromosome, position);       
    }
    
    /**
     * Sets the region for LocusZoom.js through the format "chromosome:position".
     * 
     * @param region 
     */
    public void setRegion(String region) {
        String [] splitRegion = region.split(":");
        setRegion(splitRegion[0], splitRegion[1]);        
    }
    
    /**
     * 
     * Sets the region for LocusZoom.js.
     * 
     * @param chromosome
     * @param position 
     */
    public void setRegion(String chromosome, String position) {
        JsonObject region = Json.createObject();
                
        region.put("position", position);
        region.put("chromosome", chromosome);
        
        if (position != null && chromosome != null) {
            chromosomeSelector.setSelectedItem(chromosome);
            positionSpecifier.setValue(position);
            locusZoom.setRegion(region);
        }                
        
        SNP currentSNP = getController().getActiveSNP();
        if (currentSNP == null || currentSNP.getChromosome() == null || currentSNP.getPosition() == null ||
                !currentSNP.getChromosome().equals(chromosome) || !currentSNP.getPosition().equals(position)) {
            SNP activeSNP = new InputSNP(chromosome, position);
            getController().setActiveSNP(activeSNP);
        }
    }
    
    /**
     * Pulls the input for the drop-down list and the text field, 
     * and makes them take effect.
     */
    private void useInputFields() {
        String chromosome = chromosomeSelector.getValue();
        String position = positionSpecifier.getValue();
        
        long longPosition = Long.parseLong(position);
        
        //int integerPosition = Integer.parseInt(position);
        
        if (longPosition < 1) {
            Notification notification = new Notification("Chromosome position cannot be less than 1.", Notification.Type.WARNING_MESSAGE);
            notification.setDelayMsec(7*1000);
            notification.show(Page.getCurrent());
        }
        else if (longPosition > chromosomeSizeMap.get(chromosome)) {
            Notification notification = new Notification("Position " + position + " exceeds the length of chromosome " + chromosome +
                    " (" + chromosomeSizeMap.get(chromosome) + ")" + " in genome build " + constants.getGenomeBuild() + ".",
                    Notification.Type.WARNING_MESSAGE);
            notification.setDelayMsec(20*1000);
            notification.show(Page.getCurrent());
        }
        else {
            setRegion(chromosome, position);            
        }
        
    }
    
    @Override
    public AbstractComponent getComponent() {
        return box;
    }
    @Override
    public void updateSNP() {
        SNP currentSNP = getController().getActiveSNP();
        setSNP(currentSNP);
    } 

    @Override
    public void resizePlots() {
        
    }

    @Override
    public void handOver() {
        System.out.println("Handed over to locus level.");
        System.out.println("Current active SNP: " + getController().getActiveSNP());
        updateSNP();
    }    
}
