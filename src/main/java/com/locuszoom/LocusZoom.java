package com.locuszoom;

import com.vaadin.annotations.JavaScript;
import com.vaadin.ui.AbstractJavaScriptComponent;
import com.vaadin.ui.JavaScriptFunction;
import elemental.json.JsonArray;
import elemental.json.JsonObject;
import java.io.Serializable;
import java.util.ArrayList;

/**
 * Implements a Vaadin component for LocusZoom.js.
 *
 * @author Christoffer Hjeltnes Støle
 */
@JavaScript({
    "vaadin://locuszoom/locuszoom.vendor.min.js",
    "vaadin://locuszoom/locuszoom.app.min.js",
    "vaadin://locuszoom/connector.js",    
    "vaadin://locuszoom/main.js"})
public class LocusZoom extends AbstractJavaScriptComponent {
    boolean initialBooleanVersion = false;
    boolean booleanRegionVersion = initialBooleanVersion;
    
    /**
     * Enables listening for clicked SNPs.
     */
    public interface ValueChangeListener extends Serializable {
        void valueChange();
    }
    ArrayList <ValueChangeListener> listeners =
            new ArrayList();
    /**
     * Adds a listener for clicked SNPs.
     * 
     * @param listener 
     */
    public void addValueChangeListener(
                   ValueChangeListener listener) {
        listeners.add(listener);
    }

    
    public LocusZoom() {
        addFunction("onSNPclick", new JavaScriptFunction() {
            @Override
            public void call(JsonArray arguments) {
                System.out.println("arguments: " + arguments.toJson());
                getState().setClickedSNP(String.valueOf(Double.valueOf(arguments.getNumber(0)).longValue()));
                for (ValueChangeListener listener: listeners) {
                    listener.valueChange();
                }
            }
        });
    }
    
    /**
     * 
     * Sets the region for LocusZoom.js in the state object.
     * 
     * @param region 
     */
    public void setRegion(JsonObject region) {
        region.put("boolean version", booleanRegionVersion);
        booleanRegionVersion = !booleanRegionVersion;
        getState().setRegion(region);
    }
    
    /**
     * Returns the clicked SNP.
     * 
     * @return 
     */
    public String getClickedSNP() {
        return getState().getClickedSNP();
    }
    
    @Override
    public LocusZoomState getState() {
        return (LocusZoomState) super.getState();
    }
}
