package com.components;

import com.vaadin.data.HasValue.ValueChangeListener;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.ui.Button;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.NativeSelect;
import com.vaadin.ui.themes.ValoTheme;
import java.awt.AWTException;
import java.awt.Robot;
import java.awt.event.KeyEvent;
import java.util.Collection;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Christoffer Hjeltnes Støle
 */
public class ClickMenu <T> extends CustomComponent {    
    HorizontalLayout root = new HorizontalLayout();
    Button button;
    NativeSelect <T> selector = new NativeSelect();
    
    public ClickMenu(String caption) {
        button = new Button(caption);
        button.addStyleName(ValoTheme.BUTTON_LINK);
        button.addStyleName("own-button-link-style");
        button.setIcon(VaadinIcons.MENU);
        button.addClickListener(event -> buttonClicked());
        root.addComponent(button);
        
        selector.setDescription(caption);
        selector.setEmptySelectionAllowed(false);
        selector.addValueChangeListener(event -> showButton());
        selector.addBlurListener(event -> showButton());

        // set the sizes of the subcomponents to undefined
        button.setSizeUndefined();
        selector.setSizeUndefined();
        root.setSizeUndefined();
        setCompositionRoot(root);
    }
    
    public void setItems(Collection items) {
        selector.setItems(items);
    }
    public void setValue(T value) {
        selector.setValue(value);
    }
    
    public T getValue() {
        return selector.getValue();
    }
    
    public void addValueChangeListener(ValueChangeListener listener) {
        selector.addValueChangeListener(listener);
    }
    
    public void addButtonStyleName(String styleName) {
        button.addStyleName(styleName);
    }
    
    private void buttonClicked() {
        root.removeComponent(button);
        root.addComponent(selector);
        selector.focus(); // force the focus on this component so that clicking anywhere else brings back the button with its caption
        
//        try {
//            Robot r = new Robot();
//            int keyCode = KeyEvent.VK_ENTER;
//            r.keyPress(keyCode);
//            r.keyRelease(keyCode);
//            System.out.println("r");
//        } catch (AWTException e) {
//            System.out.println(e);
//        }        
    }
    
    private void showButton() {
        root.removeAllComponents();
        root.addComponent(button);
    }
}
