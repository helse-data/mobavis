package com.main;

import com.utils.HtmlHelper;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.server.Sizeable;
import com.vaadin.shared.ui.ContentMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;
import java.util.Map;

/**
 *
 * @author Christoffer Hjeltnes Støle
 */
public class NewLandingPage {    
    Component page;
    
    Main main;
    
    GridLayout firstPage;
    GridLayout researcherPage;
    GridLayout parentOrMedicalPage;
    
    HorizontalLayout quickMenu;
    
    HtmlHelper htmlHelper = new HtmlHelper();
    Map <String, Object> independentComponents;
    
    String FIRST_PAGE = "first page";
    String RESEARCHER_PAGE = "researcher page";
    String PARENT_OR_MEDICAL = "parent or medical professional";
    
    Label sponsorNotice;
    
    public NewLandingPage(Map <String, Object> independentComponents) {
        this.independentComponents = independentComponents;
        main = (Main) independentComponents.get("main");
        
        firstPage = new GridLayout(100, 100);
        
        Label label = new Label(htmlHelper.bold("Which describes you best?"), ContentMode.HTML);
        Button researcherButton = new Button("I am a researcher");
        researcherButton.addClickListener(event -> enterPage(RESEARCHER_PAGE));
        Button parentOrMedicalButton = new Button("I am a medical professional or parent");
        parentOrMedicalButton.addClickListener(event -> enterPage(PARENT_OR_MEDICAL));
        
        
        GridLayout centreBox = new GridLayout(100, 100);
        VerticalLayout buttonBox = new VerticalLayout();
        buttonBox.setSizeFull();
        //buttonBox.setWidth(100, Sizeable.Unit.PERCENTAGE);
        //buttonBox.setHeight(100, Sizeable.Unit.PERCENTAGE);
        //centreBox.setHeight(100, Sizeable.Unit.PERCENTAGE);
        centreBox.setSizeFull();
        
        centreBox.addComponent(label, 50, 0, 99, 30);
        centreBox.addComponent(researcherButton, 50, 40, 99, 45);
        centreBox.addComponent(parentOrMedicalButton, 50, 60, 99, 65);
        //buttonBox.addComponent(researcherButton);
        //buttonBox.addComponent(parentOrMedicalButton);
        //buttonBox.setComponentAlignment(researcherButton, Alignment.TOP_CENTER);
        //buttonBox.setComponentAlignment(parentOrMedicalButton, Alignment.TOP_CENTER);
        
        
        //buttonBox.addStyleName(ValoTheme.LAYOUT_CARD);
        //centreBox.addComponent(buttonBox, 50, 31, 99, 99);
        
        //centreBox.addStyleName(ValoTheme.LAYOUT_CARD);
        
        centreBox.setComponentAlignment(label, Alignment.MIDDLE_LEFT);
        centreBox.setComponentAlignment(researcherButton, Alignment.MIDDLE_LEFT);
        centreBox.setComponentAlignment(parentOrMedicalButton, Alignment.MIDDLE_LEFT);
        //centreBox.setComponentAlignment(buttonBox, Alignment.MIDDLE_LEFT);
        
//        centreBox.addComponent(label, 50, 30, 50, 35);
//        centreBox.addComponent(researcherButton, 50, 40, 50, 45);
//        centreBox.addComponent(parentOrMedicalButton, 50, 50, 50, 55);
        
        firstPage.addComponent(centreBox, 0, 10, 99, 70);
        firstPage.setComponentAlignment(centreBox, Alignment.MIDDLE_CENTER);
        
        //firstPage.setComponentAlignment(label, Alignment.MIDDLE_CENTER);
        //firstPage.setComponentAlignment(researcherButton, Alignment.MIDDLE_CENTER);
        //firstPage.setComponentAlignment(parentOrMedicalButton, Alignment.MIDDLE_CENTER);
        
        
        
        // bottom
        createSponsorNotice();
        createQuickMenu();
        Label quickMenuTitle = new Label(htmlHelper.bold("Quick menu"), ContentMode.HTML);
        firstPage.addComponent(sponsorNotice, 1, 79, 5, 84);
        firstPage.setComponentAlignment(sponsorNotice, Alignment.BOTTOM_LEFT);
        firstPage.addComponent(quickMenuTitle, 0, 85, 99, 89);
        firstPage.setComponentAlignment(quickMenuTitle, Alignment.BOTTOM_CENTER);
        
        firstPage.addComponent(quickMenu, 0, 90, 99, 99);
        
        //firstPage.setComponentAlignment(sponsorNotice, Alignment.TOP_LEFT);
        
        firstPage.setSizeFull();
        page = firstPage;
        
        
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
            quickMenuButton.addClickListener(event -> enterApplication(option));
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
    
    private void enterPage(String targetName) {
        Component target = null;
        if (targetName.equals(RESEARCHER_PAGE)) {
            if (researcherPage == null) {
                createResearcherPage();
            }
            target = researcherPage;
        }
        else if (targetName.equals(PARENT_OR_MEDICAL)) {
            if (parentOrMedicalPage == null) {
                createParentOrMedicalPage();
            }
            target = parentOrMedicalPage;
        }
        else if (targetName.equals(FIRST_PAGE)) {
            target = firstPage;            
        }
        getComponent().getUI().setContent(target);
        page = target;
    }
    
    private void createResearcherPage() {
        researcherPage = new GridLayout(100, 100);
        researcherPage.setSizeFull();
        
        Label label = new Label(htmlHelper.bold("Which data do you want to see?"), ContentMode.HTML);        
        researcherPage.addComponent(label, 50, 30, 50, 35);
        
        Button genotypeDataButton = new Button("Phenotype variation by genotype");
        genotypeDataButton.addClickListener(event -> enterApplication(Main.MenuOption.SNP_PLOT));
        
        Button genotypeStatisticsButton = new Button("Summary statistics for the genotype data in MoBa");
        genotypeStatisticsButton.addClickListener(event -> enterApplication(Main.MenuOption.SNP_STATISTICS));
        
        Button summaryStatisticsButton = new Button("Whole-population phenotype summary statistics");
        summaryStatisticsButton.addClickListener(event -> enterApplication(Main.MenuOption.SUMMARY_STATISTICS));
        
        researcherPage.addComponent(genotypeDataButton, 50, 40, 50, 45);
        researcherPage.addComponent(genotypeStatisticsButton, 50, 50, 50, 55);
        researcherPage.addComponent(summaryStatisticsButton, 50, 60, 50, 65);
        
        addReturnButton(researcherPage, FIRST_PAGE);
        
    }
    
    private void createParentOrMedicalPage() {
        parentOrMedicalPage = new GridLayout(100, 100);
        parentOrMedicalPage.setSizeFull();
        
        Label label = new Label(htmlHelper.bold("I want to ..."), ContentMode.HTML);        
        parentOrMedicalPage.addComponent(label, 50, 30, 50, 35);
        
        Button summaryStatisticsButton = new Button("compare the development of a child to the general population");
        summaryStatisticsButton.addClickListener(event -> enterApplication(Main.MenuOption.SUMMARY_STATISTICS));
        
        Button snpPlotButton = new Button("given a genetic profile of a child, compare its development to the general population");
        snpPlotButton.addClickListener(event -> enterApplication(Main.MenuOption.NEW_SNP_PLOT));
        
        parentOrMedicalPage.addComponent(summaryStatisticsButton, 50, 40, 50, 45);
        parentOrMedicalPage.addComponent(snpPlotButton, 50, 50, 50, 55);
        
        addReturnButton(parentOrMedicalPage, FIRST_PAGE);
    }
    
    private void addReturnButton(GridLayout layout, String targetName) {
        Button returnButton = new Button("return", VaadinIcons.ARROW_LEFT);
        //returnButton.addStyleName(ValoTheme.BUTTON_ICON_ALIGN_RIGHT);
        returnButton.addClickListener(event -> enterPage(targetName));
        layout.addComponent(returnButton, 0, 90, 99, 99);
        layout.setComponentAlignment(returnButton, Alignment.MIDDLE_CENTER);
    }
    
    public Component getComponent() {
        return page;
    }
    
    public void enterApplication(Main.MenuOption menuOption) {
            main.setView(menuOption);
            getComponent().getUI().setContent(main.getComponent());
    }
    
}
