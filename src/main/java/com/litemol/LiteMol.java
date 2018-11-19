package com.litemol;

import com.vaadin.annotations.JavaScript;
import com.vaadin.ui.AbstractJavaScriptComponent;
import elemental.json.JsonObject;

/**
 * 
 * Implements a Vaadin component for LiteMol.
 *
 * @author Christoffer Hjeltnes Støle
 */
@JavaScript({
    "vaadin://litemol/LiteMol-core.min.js",
    "vaadin://litemol/LiteMol-plugin.min.js",
    "vaadin://litemol/LiteMol-viewer.min.js",
    "vaadin://litemol/connector.js",
    "vaadin://litemol/simpleController.js"})
public class LiteMol extends AbstractJavaScriptComponent {
    boolean initialBooleanVersion = false;
    boolean booleanEntryIDVersion = initialBooleanVersion;
    
    @Override
    public LiteMolState getState() {
        return (LiteMolState) super.getState();
    }
    
    /**
     * Sets the PDB model to visualize in the state object.
     * 
     * @param entryID 
     */
    public void setEntryID(JsonObject entryID) {
        entryID.put("boolean version", booleanEntryIDVersion);
        booleanEntryIDVersion = !booleanEntryIDVersion;
        getState().setEntryID(entryID);
    }
}
