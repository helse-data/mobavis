package com.main;

import com.utils.HtmlHelper;
import com.vaadin.event.ShortcutAction;
import com.vaadin.event.ShortcutListener;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.server.VaadinService;
import com.vaadin.shared.ui.ContentMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.PasswordField;
import com.vaadin.ui.themes.ValoTheme;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Arrays;
import java.util.Map;

/**
 *
 * @author ChristofferHjeltnes
 */
public class LandingPage {
    GridLayout page = new GridLayout(100, 100);
    HorizontalLayout buttonBox = new HorizontalLayout();
    Button snpButton;
    Button summaryButton;
    Button returnButton;
    Label entryDescription;
    PasswordField keyInputField;
    boolean firstLanding = true;
    Map <String, Object> highLevelComponents;
    Main main;
    
    HtmlHelper html;
    
    private String accessKey;
       
    public LandingPage(Map <String, Object> independentComponents) {
        this.highLevelComponents = independentComponents;
        
        html = new HtmlHelper();
        Label message = new Label();
        message.setWidth(null);
        message.setHeight(null);
        
        message.setContentMode(ContentMode.HTML);

        String messageText = "";
        messageText += "<p style=\"text-align:center;font-size: 2em\">MoBa visualisation prototype</p><br><br>";
        
        messageText += "<br>This prototype application allows basic user interactions with and queries for various data from the "
                + html.link("https://www.fhi.no/en/studies/moba/", "Norwegian Mother and Child Cohort Study") + " (MoBa). ";
        
        messageText += "<br><br>" + html.bold("Current features");
        messageText += html.createList(Arrays.asList(new String[]{"SNP search for chromosomes 1-22 (the autosomes)", 
            "Superimposition of user data on statistics of the MoBa survey"}));
        messageText += "<br>" + html.bold("Known issues");
        messageText += html.createList(Arrays.asList(new String[]{
            "If a chromosome hasn't been queried for a while, the next query might take about 15-30 seconds. Subsequent queries to the chromosome are near-instant.",
        }));
        
        
        snpButton = new Button("SNP data");//, VaadinIcons.ARROW_RIGHT);
        //snpButton.addStyleName(ValoTheme.BUTTON_ICON_ALIGN_RIGHT);
        snpButton.addClickListener(event -> checkKey(0));
        //snpButton.setSizeFull();
        
        summaryButton = new Button("summary statistics");//, VaadinIcons.ARROW_RIGHT);
        //summaryButton.addStyleName(ValoTheme.BUTTON_ICON_ALIGN_RIGHT);
        summaryButton.addClickListener(event -> checkKey(2));
        //summaryButton.setSizeFull();
        
        entryDescription = new Label("Type of data to visualise:");
        
        keyInputField = new PasswordField("Password:");
        keyInputField.setMaxLength(25);
        keyInputField.addShortcutListener(new ShortcutListener("Enter", ShortcutAction.KeyCode.ENTER, null) {
                    @Override
                    public void handleAction(Object sender, Object target) {
                        checkKey(0);
                    }
                    });
 
        //keyInputField.addValueChangeListener(event -> checkKey());
        
        //page.addComponent(new HorizontalLayout());
        message.setValue(messageText);
        message.setSizeFull();
        //message.setHeight(100, Sizeable.Unit.PERCENTAGE);
        snpButton.setSizeFull();
        summaryButton.setSizeFull();
        keyInputField.setSizeFull();
        
        page.setSizeFull();
        buttonBox.setSizeFull();
//        buttonBox.setRowExpandRatio(0, (float) 0.7);
//        buttonBox.setRowExpandRatio(1, (float) 0.1);
//        buttonBox.setRowExpandRatio(2, (float) 0.1);
        page.addComponent(message, 20, 0, 80, 35);
        page.addComponent(keyInputField, 45, 60, 50, 62);
        page.addComponent(entryDescription, 40, 70, 50, 72);
        page.addComponent(buttonBox, 42, 75, 54, 77);
        //buttonBox.addComponent(snpButton, 37, 0, 47, 12);
        //buttonBox.addComponent(summaryButton, 49, 0, 59, 12);
        buttonBox.addComponent(snpButton);
        buttonBox.addComponent(summaryButton);
        page.setComponentAlignment(message, Alignment.MIDDLE_CENTER);
        page.setComponentAlignment(entryDescription, Alignment.MIDDLE_CENTER);
        page.setComponentAlignment(keyInputField, Alignment.BOTTOM_CENTER);
        page.setComponentAlignment(buttonBox, Alignment.MIDDLE_CENTER);        
    }
    
    private void enter(int viewOption) {        
        //upper.enter(firstLanding, viewOption);
        
        if (firstLanding) {
            main = (Main) highLevelComponents.get("main");
            main.execute(viewOption);
        }        
        getComponent().getUI().setContent(main.getComponent());
        
        if (firstLanding) {
            firstLanding = false;
            returnButton = new Button("return", VaadinIcons.ARROW_RIGHT);
            returnButton.addStyleName(ValoTheme.BUTTON_ICON_ALIGN_RIGHT);
            returnButton.addClickListener(event -> checkKey(-1));
            page.removeComponent(keyInputField);
            page.removeComponent(entryDescription);
            buttonBox.removeAllComponents();
            buttonBox.addComponent(returnButton);
        }
        
    }
    
    public Component getComponent() {
        return page;
    }    
    
    private void checkKey(int viewOption) {
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
            enter(viewOption);
        }
        else {    
            Notification.show("Access denied.", Type.WARNING_MESSAGE);
        }
    }
}
