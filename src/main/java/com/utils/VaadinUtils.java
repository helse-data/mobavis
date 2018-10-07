package com.utils;

import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author Christoffer Hjeltnes Støle
 */
public class VaadinUtils {
    
    public Set getChangedOptions (Set currentlySelected, Set previouslySelected) {
        Set unselected = new HashSet();
        unselected.addAll(previouslySelected);
        unselected.removeAll(currentlySelected);
        
        Set newlySelected = new HashSet();
        newlySelected.addAll(currentlySelected);
        newlySelected.removeAll(previouslySelected);
        
        Set changed = new HashSet();
        changed.addAll(newlySelected);
        changed.addAll(unselected);
        
        return changed;
    }    
}