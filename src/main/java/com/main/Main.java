package com.main;

import com.components.ClickMenu;
import com.plotting.OverlayPlotBox;
import com.plotting.ParameterisedPlotComponent;
import com.plotting.SNPPlotBox;
import com.utils.Constants;
import com.utils.HtmlHelper;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.shared.ui.ContentMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.NativeSelect;
import com.vaadin.ui.themes.ValoTheme;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class Main {    
    Map <String, Object> highLevelComponents;
    
    GridLayout mainGrid = new GridLayout(100, 1000); 
    Component currentContentBox;    
    
    Component statsPage;
    HorizontalLayout leftCornerBox = new HorizontalLayout();
    GridLayout percentilePlotGrid;    
    SNPPlotBoxOld snpPlotBoxOld;
    SNPPlotBox snpPlotBox;
    OverlayPlotBox overlayPlotBox;
    
    ParameterisedPlotComponent parameterisedPlotComponent;

    ClickMenu viewSelector;
    List <String> viewOptions = new ArrayList();
    String currentView;
    
    List <List <Double>> percentileData;
 
    boolean setupOngoing = true;
    
    Constants constants = new Constants();
    String[] ages = constants.getAges();
    
    boolean userVersion = false;
    
    HtmlHelper htmlHelper = new HtmlHelper();

    public Main(Map <String, Object> independentComponents) {
        this.highLevelComponents = independentComponents;
    }
        
    public void execute(int viewOption) {        
        
        // top row
        // view selection        
        viewOptions.addAll(Arrays.asList(new String[]{
            "phenotype by SNP genotype",
            "SNP statistics",
            "summary MoBa statistics",
            "new SNP genotype page"}));      
        
        viewSelector = new ClickMenu("Select data to visualise");
        //viewSelector.setTextInputAllowed(false);
        viewSelector.setItems(viewOptions);        
        viewSelector.addValueChangeListener(event -> changeView(String.valueOf(
                event.getValue())));
        
        Button homeLink = new Button("Home");
        homeLink.setIcon(VaadinIcons.ARROW_LEFT);
        homeLink.addStyleName(ValoTheme.BUTTON_LINK);
        homeLink.addStyleName("own-button-link-style");
        homeLink.addStyleName("own-button-link-style:active");
        homeLink.addClickListener(event -> goHome());
        Label title = new Label(htmlHelper.bold("MoBa visualisation prototype"), ContentMode.HTML);        
        
        leftCornerBox.addComponent(homeLink);
        leftCornerBox.addComponent(viewSelector);
        
        
        int n1 = (int) ((mainGrid.getColumns()-1)*0.01);
        int n10 = (int) ((mainGrid.getColumns()-1)*0.1);
        mainGrid.addComponent(leftCornerBox, 0, 0, 2*n10+6, 8);
        mainGrid.addComponent(title, 4*n10, 0, 6*n10, 0);
        //mainGrid.setComponentAlignment(title, Alignment.MIDDLE_CENTER);
//        mainGrid.addComponent(homeLink, 0, 0, 5, 5);
//        //mainGrid.addComponent(viewSelector, 1, 0, 2*n10, 30);
//        mainGrid.addComponent(viewSelectorPopup, 7, 0, 2*n10, 8);
//        mainGrid.setComponentAlignment(viewSelectorPopup, Alignment.MIDDLE_CENTER);
        
                       
        // end of setup for main grid

        // set sizes to full
        viewSelector.setSizeFull();
        homeLink.setSizeFull();
        leftCornerBox.setSizeFull();
        mainGrid.setSizeFull(); 
        
        // done
        
        // set default views and go
//        if (userVersion) {
//            viewSelector.setValue(viewOptions.get(0));
//        }
//        else {
//            viewSelector.setValue(viewOptions.get(2));
//        }
        viewSelector.setValue(viewOptions.get(viewOption));
        setupOngoing = false;
    }
    
    public Component getComponent() {
        return mainGrid;
    }
    
    private void setMiddleBox(Component middleBox) {
        mainGrid.removeComponent(currentContentBox);
        mainGrid.addComponent(middleBox, 0, 15, mainGrid.getColumns()-1, mainGrid.getRows()-1);        
        currentContentBox = middleBox;
    }
    
    private void changeView(String option) {
        if (option.equals("null") || option.equals(currentView)){
            return;
        }
        
        if (option.equals(viewOptions.get(0))) {
            if (snpPlotBoxOld == null) {
                snpPlotBoxOld = new SNPPlotBoxOld();
            }
            setMiddleBox(snpPlotBoxOld.getComponent());
        }
        if (option.equals(viewOptions.get(1))) {
            if (statsPage == null) {
                statsPage = new SummaryPage().getComponent();
            }
            setMiddleBox(statsPage);
        }
        else if (option.equals(viewOptions.get(2))) {
            if (overlayPlotBox == null) {
                overlayPlotBox = new OverlayPlotBox();
            }
            setMiddleBox(overlayPlotBox.getComponent());
        }
        if (option.equals(viewOptions.get(3))) {
            if (snpPlotBox == null) {
                snpPlotBox = new SNPPlotBox();
            }
            setMiddleBox(snpPlotBox.getComponent());
        }
        //else if (option.equals(viewOptions.get(3))) {
            
            //middleBox.addComponent(parameterisedPlotComponent.getComponent(), 0, 0, 70, 99);
            //middleBox.addComponent(optionsBox, 71, 0, 99, 99);
        //}
        currentView = option;      
    }

    private void goHome() {
        LandingPage landingPage = (LandingPage) highLevelComponents.get("landing page");
        getComponent().getUI().setContent(landingPage.getComponent());
        //upper.setContent(upper.getLandingPage());
    }    
}
