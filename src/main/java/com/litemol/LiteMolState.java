package com.litemol;

import com.vaadin.shared.ui.JavaScriptComponentState;
import elemental.json.JsonObject;

/**
 * State class for LiteMol.
 * 
 * @author Christoffer Hjeltnes Støle
 */

public class LiteMolState extends JavaScriptComponentState {
    private JsonObject entryID;
    
    /**
     * 
     * @return 
     */
    public JsonObject getEntryID() {
        return entryID;
    }    
    
    /**
     * Sets the PDB model to visualize.
     * 
     * @param entryID 
     */
    public void setEntryID(JsonObject entryID) {
        this.entryID = entryID;
        System.out.println("state ID: " + entryID.toJson());
    }
}