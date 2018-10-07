package com.main;

import com.main.Controller.ContentType;
import com.main.Controller.Visualization;
import com.visualization.VisualizationBox;
import com.utils.Constants;
import com.utils.HtmlHelper;
import com.vaadin.event.ShortcutAction;
import com.vaadin.event.ShortcutListener;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.server.Page;
import com.vaadin.shared.ui.ContentMode;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.PasswordField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Map;

/**
 *
 * @author ChristofferHjeltnes
 */
public class LandingPage {
    //GridLayout page = new GridLayout(100, 100);
    VerticalLayout page = new VerticalLayout();
    Controller controller;
    HorizontalLayout quickMenu;
    Button enterApplicationButton;
    Button enterDemoButton;
    Button returnButton;
    PasswordField keyInputField;
    boolean firstLanding = true;
    private boolean keyFits = false; // for class-internal double check
    
    String NEW_LANDING_PAGE = "new landing page";
    
    Constants constants = new Constants();
    HtmlHelper htmlHelper;
    
    Label sponsorNotice;
    
    private String accessKey;
       
    public LandingPage(Controller controller) {
        this.controller = controller;
        page.addStyleName("white");

        
        htmlHelper = new HtmlHelper();
        Label welcomeMessage = new Label();
        
        welcomeMessage.setContentMode(ContentMode.HTML);

        String welcomeMessageText = "";
        welcomeMessageText += "<p style=\"text-align:center;font-size: 2em\">MoBa visualization prototype</p><br><br>";
        
        welcomeMessageText += "<br>This prototype application allows basic user interactions with and queries for various data from the "
                + htmlHelper.link("https://www.fhi.no/en/studies/moba/", "Norwegian Mother and Child Cohort Study") + " (MoBa). ";
        
        welcomeMessage.setValue(welcomeMessageText);
        
        page.addComponent(welcomeMessage);        
        
        enterApplicationButton = new Button("Enter application");
        enterApplicationButton.addClickListener(event -> checkKey(null));
        
        keyInputField = new PasswordField("Password:");
        keyInputField.setMaxLength(30);
        keyInputField.addShortcutListener(new ShortcutListener("Enter", ShortcutAction.KeyCode.ENTER, null) {
                    @Override
                    public void handleAction(Object sender, Object target) {
                        checkKey(null);
                    }
                    });
 
        VerticalLayout applicationEntranceContainer = new VerticalLayout();
        applicationEntranceContainer.setWidthUndefined();
        applicationEntranceContainer.addStyleName(ValoTheme.LAYOUT_CARD);
        applicationEntranceContainer.addComponent(keyInputField);
        applicationEntranceContainer.addComponent(enterApplicationButton);
        applicationEntranceContainer.setComponentAlignment(keyInputField, Alignment.MIDDLE_CENTER);
        applicationEntranceContainer.setComponentAlignment(enterApplicationButton, Alignment.MIDDLE_CENTER);
        //applicationEntranceContainer.setSizeFull();
        
        page.addComponent(applicationEntranceContainer);
        page.setComponentAlignment(applicationEntranceContainer, Alignment.BOTTOM_CENTER);
        
        
        enterDemoButton = new Button("DEMO version");
        enterDemoButton.addClickListener(event -> enterDemo());
        enterDemoButton.addStyleName("demo");
        page.addComponent(enterDemoButton);
        page.setComponentAlignment(enterDemoButton, Alignment.BOTTOM_CENTER);
        
        page.setSizeFull();
        // top, right, bottom, left
        page.setMargin(new MarginInfo(true, false, false, false));

        
        page.setComponentAlignment(welcomeMessage, Alignment.TOP_CENTER);
        //page.setComponentAlignment(keyInputField, Alignment.TOP_CENTER);
        //page.setComponentAlignment(enterApplicationButton, Alignment.MIDDLE_CENTER);
        
        createSponsorNotice();
        //sponsorNotice.setWidthUndefined();
        sponsorNotice.setSizeUndefined();
        HorizontalLayout sponsorContainer = new HorizontalLayout();
        sponsorContainer.addComponent(sponsorNotice);
        sponsorContainer.setMargin(new MarginInfo(false, false, false, true));
        createQuickMenu();
        //page.addComponent(sponsorNotice, 1, 50, 5, 55);
        page.addComponent(sponsorContainer);
        
        page.setComponentAlignment(sponsorContainer, Alignment.BOTTOM_LEFT);
        
        Label quickMenuTitle = new Label(htmlHelper.bold("Quick menu"), ContentMode.HTML);
        //page.addComponent(quickMenuTitle, 0, 60, 99, 64);
        
        VerticalLayout quickMenuContainer = new VerticalLayout();        
        quickMenuContainer.setSizeFull();
        
        quickMenuContainer.addComponent(quickMenuTitle);
        quickMenuContainer.setComponentAlignment(quickMenuTitle, Alignment.BOTTOM_CENTER);
        quickMenuContainer.addComponent(quickMenu);
        
        // top, right, bottom, left
        quickMenuContainer.setMargin(new MarginInfo(false, false, false, false));
        
        
        //page.addComponent(quickMenu, 0, 66, 99, 99);
        page.addComponent(quickMenuContainer);
        page.setComponentAlignment(quickMenuContainer, Alignment.BOTTOM_CENTER);
        
        // expand ratios
        page.setExpandRatio(welcomeMessage, 6);
        page.setExpandRatio(applicationEntranceContainer, 5);
        page.setExpandRatio(enterDemoButton, 2);
        page.setExpandRatio(sponsorContainer, 5);        
        page.setExpandRatio(quickMenuContainer, 4);
    }
    
    private void createQuickMenu() {
        quickMenu = new HorizontalLayout();
        quickMenu.setSizeFull();
        quickMenu.addStyleName(ValoTheme.LAYOUT_WELL);
        
        //quickMenu.addComponent(sponsorNotice);
        for (Visualization visualization : Visualization.values()) {
            Button quickMenuButton;
            // TODO: should probably be removed
            if (visualization.toString().startsWith("Summary statistics")) {
                quickMenuButton = new Button("Summary statistics");
            }
            else {
                quickMenuButton = new Button(visualization.toString());
            }
            quickMenu.addComponent(quickMenuButton);
            quickMenu.setComponentAlignment(quickMenuButton, Alignment.MIDDLE_CENTER);
            quickMenuButton.addClickListener(event -> checkKey(visualization));
            //System.out.println("option: " + option);
        }
    }
    private void createSponsorNotice() {
        String sourceText = htmlHelper.bold("Sponsors:");
        sourceText += htmlHelper.listStart();
        
        sourceText += htmlHelper.listElement("Dr. Nils Henrichsen og hustru Anna Henrichsens legat");
        sourceText += htmlHelper.listElement("L. Meltzers Høyskolefond and Bergen University Fund");
        
        sourceText += htmlHelper.listEnd();
        sponsorNotice = new Label(sourceText, ContentMode.HTML);
    }
    
    
    public Component getComponent() {
        return page;
    }
    
    private void loadAccessKey() {
        try {
            String basepath = constants.getVaadinPath();
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
            System.out.println(e);
            Notification.show("Password error.", Type.ERROR_MESSAGE);
        }
    }
    
    private void enterDemo() {
        Page.getCurrent().open("https://helse-data.no/demo", null);
    }
    
    private void checkKey(Visualization visualization) {
        if (accessKey == null) {
            loadAccessKey();
        }
        String key = keyInputField.getValue();
        
        if (key.equals(accessKey)) {
            keyFits = true;
            if (visualization != null) {
                controller.setVisualization(visualization);
            }
            controller.setContentType(ContentType.VISUALIZATION);
        }
        else {    
            Notification.show("Access denied.", Type.WARNING_MESSAGE);
        }
    }
}
