package com.locuszoom;

import com.vaadin.annotations.JavaScript;
import com.vaadin.ui.AbstractJavaScriptComponent;
import elemental.json.JsonObject;

/**
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
    
    
    public void setRegion(JsonObject region) {
        region.put("boolean version", booleanRegionVersion);
        booleanRegionVersion = !booleanRegionVersion;
        getState().setRegion(region);
    }
    
    @Override
    public LocusZoomState getState() {
        return (LocusZoomState) super.getState();
    }
}
