package com.main;

import com.utils.Constants;
import com.utils.HtmlHelper;
import com.vaadin.event.ShortcutAction;
import com.vaadin.event.ShortcutListener;
import com.vaadin.icons.VaadinIcons;
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
import com.vaadin.ui.UI;
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
    HorizontalLayout quickMenu;
    Button snpButton;
    Button summaryButton;
    Button newLandingPageButton;
    Button returnButton;
    PasswordField keyInputField;
    boolean firstLanding = true;
    private boolean keyFits = false; // for class-internal double check
    Map <String, Object> highLevelComponents;
    Main main;
    
    String NEW_LANDING_PAGE = "new landing page";
    
    Constants constants = new Constants();
    HtmlHelper htmlHelper;
    
    Label sponsorNotice;
    
    private String accessKey;
       
    public LandingPage(Map <String, Object> independentComponents) {
        this.highLevelComponents = independentComponents;
        main = (Main) highLevelComponents.get("main");
        
        htmlHelper = new HtmlHelper();
        Label welcomeMessage = new Label();
        //welcomeMessage.setWidth(null);
        //welcomeMessage.setHeight(null);
        
        welcomeMessage.setContentMode(ContentMode.HTML);

        String welcomeMessageText = "";
        welcomeMessageText += "<p style=\"text-align:center;font-size: 2em\">MoBa visualisation prototype</p><br><br>";
        
        welcomeMessageText += "<br>This prototype application allows basic user interactions with and queries for various data from the "
                + htmlHelper.link("https://www.fhi.no/en/studies/moba/", "Norwegian Mother and Child Cohort Study") + " (MoBa). ";
        
        welcomeMessageText += "<br><br>" + htmlHelper.bold("Current features");
        welcomeMessageText += htmlHelper.createList(Arrays.asList(new String[]{"SNP search for chromosomes 1-22 (the autosomes)", 
            "Superimposition of user data on statistics of the MoBa survey"}));
        welcomeMessageText += "<br>" + htmlHelper.bold("Known issues");
        welcomeMessageText += htmlHelper.createList(Arrays.asList(new String[]{
            "If a chromosome hasn't been queried for a while, the next query might take about 15-30 seconds. Subsequent queries to the chromosome are near-instant.",
        }));
        
        newLandingPageButton = new Button("new landing page");
        newLandingPageButton.addClickListener(event -> checkKey(NEW_LANDING_PAGE));
        
        
        keyInputField = new PasswordField("Password:");
        keyInputField.setMaxLength(25);
        keyInputField.addShortcutListener(new ShortcutListener("Enter", ShortcutAction.KeyCode.ENTER, null) {
                    @Override
                    public void handleAction(Object sender, Object target) {
                        checkKey(NEW_LANDING_PAGE);
                    }
                    });
 
        //keyInputField.addValueChangeListener(event -> checkKey());
        
        //page.addComponent(new HorizontalLayout());
        welcomeMessage.setValue(welcomeMessageText);
        //welcomeMessage.setSizeFull();
        //message.setHeight(100, Sizeable.Unit.PERCENTAGE);
        //snpButton.setSizeFull();
        //summaryButton.setSizeFull();
        //keyInputField.setSizeFull();
        
        page.setSizeFull();
//        buttonBox.setRowExpandRatio(0, (float) 0.7);
//        buttonBox.setRowExpandRatio(1, (float) 0.1);
//        buttonBox.setRowExpandRatio(2, (float) 0.1);
        page.addComponent(welcomeMessage, 0, 0, 99, 30);
        page.addComponent(keyInputField, 0, 31, 99, 35);
        page.addComponent(newLandingPageButton, 0, 40, 99, 45);
        
        page.setComponentAlignment(welcomeMessage, Alignment.TOP_CENTER);
        page.setComponentAlignment(keyInputField, Alignment.TOP_CENTER);
        page.setComponentAlignment(newLandingPageButton, Alignment.MIDDLE_CENTER);
        
        createSponsorNotice();
        createQuickMenu();
        page.addComponent(sponsorNotice, 1, 50, 5, 55);
        page.setComponentAlignment(sponsorNotice, Alignment.BOTTOM_LEFT);
        Label quickMenuTitle = new Label(htmlHelper.bold("Quick menu"), ContentMode.HTML);
        page.addComponent(quickMenuTitle, 0, 60, 99, 64);
        page.setComponentAlignment(quickMenuTitle, Alignment.BOTTOM_CENTER);
        page.addComponent(quickMenu, 0, 66, 99, 99);
    }
    
    private void enter(Main.MenuOption viewOption) {
        if (!keyFits) { // double check
            return;
        }

        //upper.enter(firstLanding, viewOption);
        
        UI ui = getComponent().getUI();
        System.out.println("main: " + main);
        System.out.println("ui: " + ui);
        main.setView(viewOption);

        ui.setContent(main.getComponent());
        
        if (firstLanding) {
            firstLanding();
        }
        
    }
    
    private void firstLanding() {
        firstLanding = false;
        returnButton = new Button("return", VaadinIcons.ARROW_RIGHT);
        returnButton.addStyleName(ValoTheme.BUTTON_ICON_ALIGN_RIGHT);
        returnButton.addClickListener(event -> getComponent().getUI().setContent(main.getComponent()));
        page.removeComponent(keyInputField);
        page.removeComponent(newLandingPageButton);
        page.addComponent(returnButton, 40, 40, 60, 45);
        page.setComponentAlignment(returnButton, Alignment.TOP_LEFT);
    }
    
    private void createQuickMenu() {
        quickMenu = new HorizontalLayout();
        quickMenu.setSizeFull();
        quickMenu.addStyleName(ValoTheme.LAYOUT_WELL);
        //quickMenu.addComponent(sponsorNotice);
        for (Main.MenuOption option : Main.MenuOption.values()) {
            Button quickMenuButton = new Button(option.toString());
            quickMenu.addComponent(quickMenuButton);
            quickMenu.setComponentAlignment(quickMenuButton, Alignment.MIDDLE_CENTER);
            quickMenuButton.addClickListener(event -> checkKey(option));
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
    
    //private void checkKey(Main.MenuOption viewOption) {
    private void checkKey(Object viewOption) {
        if (accessKey == null) {
            loadAccessKey();
        }
        String key = keyInputField.getValue();
        
        
        if (key.equals(accessKey)) {
            
            keyFits = true;
            
            if (viewOption.toString().equals(NEW_LANDING_PAGE)) {
                NewLandingPage newLandingPage = new NewLandingPage(highLevelComponents);
                getComponent().getUI().setContent(newLandingPage.getComponent());
                firstLanding();
                firstLanding = false;
            }
            else {
                enter((Main.MenuOption) viewOption);
            }
            
            
            
        }
        else {    
            Notification.show("Access denied.", Type.WARNING_MESSAGE);
        }
    }
}
