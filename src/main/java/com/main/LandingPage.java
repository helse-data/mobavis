package com.main;

import com.vaadin.event.ShortcutAction;
import com.vaadin.event.ShortcutListener;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.server.VaadinService;
import com.vaadin.shared.ui.ContentMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.PasswordField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

/**
 *
 * @author ChristofferHjeltnes
 */
public class LandingPage {
    VerticalLayout page = new VerticalLayout();
    Button enterButton;
    PasswordField keyInputField;
    boolean firstLanding = true;
    MyUI upper;
    
    private String accessKey;
       
    public LandingPage(MyUI upper) {
        this.upper = upper;
        Label message = new Label();
        message.setWidth(null);
        message.setHeight(null);

        message.setContentMode(ContentMode.HTML);

        String messageText = "";
        messageText += "<h1>MoBa visualisation proof-of-concept</h1><br><br>";
        
        messageText += "<br>This proof-of-concept application allows basic user interactions with and queries for various data from the "
                + "<a href=\"https://www.fhi.no/en/studies/moba/\">Norwegian Mother and Child Cohort Study</a> (MoBa). ";
        messageText += "<br><br><b>Current features</b>";
        messageText += "<ul>"
                + "<li>SNP search for chromosomes 1-22 (the autosomes)</li>"
                + "<li>Superimposition of user data on statistics of the MoBa survey</li>"
                + "</ul>";
        messageText += "<br><b>Known issues</b>";
        messageText += "<ul>"
                + "<li>If a chromosome hasn't been queried for a while, the next query might take about 15-30 seconds."
                + " Subsequent queries to the chromosome are near-instant.</li>"
                + "</ul>";
        
        
        enterButton = new Button("Enter", VaadinIcons.ARROW_RIGHT);
        enterButton.addStyleName(ValoTheme.BUTTON_ICON_ALIGN_RIGHT);
        enterButton.addClickListener(event -> checkKey());
        
        
        keyInputField = new PasswordField("Password:");
        keyInputField.setMaxLength(25);
        keyInputField.addShortcutListener(new ShortcutListener("Enter", ShortcutAction.KeyCode.ENTER, null) {
                    @Override
                    public void handleAction(Object sender, Object target) {
                        checkKey();
                    }
                    });
 
        //keyInputField.addValueChangeListener(event -> checkKey());
        
        //page.addComponent(new HorizontalLayout());
        message.setValue(messageText);
        page.setSizeFull();
        page.addComponent(message);
        page.addComponent(keyInputField);
        page.addComponent(enterButton);
        //page.addComponent(new HorizontalLayout());
        page.setComponentAlignment(message, Alignment.MIDDLE_CENTER);
        page.setComponentAlignment(keyInputField, Alignment.BOTTOM_CENTER);
        page.setComponentAlignment(enterButton, Alignment.MIDDLE_CENTER);
        
    }
    
    private void enter() {        
        upper.enter(firstLanding);
        if (firstLanding) {
            firstLanding = false;
            enterButton.setCaption("Return");
            page.removeComponent(keyInputField);
        }
        
    }
    
    public Component getComponent() {
        return page;
    }
    
    //private void accessControl() {
    //}
    
    
    private void checkKey() {
        if (accessKey == null) {
            try {
                String basepath = VaadinService.getCurrent().getBaseDirectory().getAbsolutePath();
                String fileName = basepath + "/WEB-INF/key.dat";

                InputStream inputStream = new FileInputStream(fileName);
                Reader reader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(reader);
                accessKey = bufferedReader.readLine();
                inputStream.close();
                reader.close();
                bufferedReader.close();
            }
            catch (IOException e) {
                System.out.println(e.getMessage());
                Notification.show("Password error.", Type.ERROR_MESSAGE);
                return;
            }
        }
        String key = keyInputField.getValue();
        //if (key.equals("")) {
        //    
        //}
        if (key.equals(accessKey)) {
            enter();
        }
        else {
            //JavaScript.getCurrent().execute("alert('Access denied.')");       
            Notification.show("Access denied.", Type.WARNING_MESSAGE);
        }
    }
}
