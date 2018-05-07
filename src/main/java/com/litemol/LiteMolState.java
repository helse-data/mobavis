package com.litemol;

import com.vaadin.shared.ui.JavaScriptComponentState;
import elemental.json.JsonObject;

public class LiteMolState extends JavaScriptComponentState {
    private JsonObject entryID;
    
    public JsonObject getEntryID() {
        return entryID;
    }
    
    public void setEntryID(JsonObject entryID) {
        this.entryID = entryID;
        System.out.println("state ID: " + entryID.toJson());
    }
}