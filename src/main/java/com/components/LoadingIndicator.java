package com.components;

import com.vaadin.server.Sizeable;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.Label;
import com.vaadin.ui.NativeSelect;
import com.vaadin.ui.Window;

/**
 *
 * @author Christoffer Hjeltnes Støle
 */
public class LoadingIndicator <T> extends Window {    
    //Window root;
    //Label label;
    
    public LoadingIndicator(T captionObject) {
        super(captionObject.toString());
        center();
        setDraggable(true);
        setResizable(false);
        setClosable(false);
        setWidth(190, Sizeable.Unit.PIXELS);
        setHeight(30, Sizeable.Unit.PIXELS);
//        root = new Window(captionObject.toString());
//        root.center();
//        root.setDraggable(true);
//        root.setResizable(false);
//        root.setClosable(false);

        // set the sizes of the subcomponents to undefined
        //label.setSizeUndefined();
        //root.setSizeUndefined();
        //setCompositionRoot(root);
    }
    
//    public Window getWindow() {
//        return root;
//    }
    
}
