package com.main;

import com.components.ClickMenu;
import com.plotting.OverlayPlotBox;
import com.plotting.ParameterisedPlotComponent;
import com.plotting.SNPPlotBox;
import com.utils.Constants;
import com.utils.HtmlHelper;
import com.vaadin.data.HasValue;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.shared.ui.ContentMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.NativeSelect;
import com.vaadin.ui.Panel;
import com.vaadin.ui.VerticalLayout;
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

    ClickMenu <MenuOption> viewSelector;
    List <MenuOption> viewOptions = new ArrayList();
    MenuOption currentView;
    
    List <List <Double>> percentileData;
 
    boolean setupOngoing = true;
    
    Constants constants = new Constants();
    String[] ages = constants.getAges();
    
    boolean userVersion = false;
    
    HtmlHelper htmlHelper = new HtmlHelper();

    public Main(Map <String, Object> independentComponents) {
        this.highLevelComponents = independentComponents;
    }
    
    public enum MenuOption {
        SNP_PLOT("phenotype by SNP genotype"),
        SNP_STATISTICS("SNP statistics"),
        SUMMARY_STATISTICS("summary MoBa statistics"),
        NEW_SNP_PLOT("new SNP genotype page"),
        MANHATTAN("Manhattan plots");
        private final String displayName;
     
        MenuOption(String displayName) {
            this.displayName = displayName;
        }
        
        @Override
        public String toString() {
            return displayName;
        }
    }
     
       
    
    public void execute(MenuOption viewOption) {        
        
        // top row
        // view selection        
        viewOptions.addAll(Arrays.asList(new MenuOption[] {
            MenuOption.SNP_PLOT,
            MenuOption.SNP_STATISTICS,
            MenuOption.SUMMARY_STATISTICS,
            MenuOption.NEW_SNP_PLOT,
            MenuOption.MANHATTAN}));      
        
        viewSelector = new ClickMenu("Select data to visualise");
        //viewSelector.setTextInputAllowed(false);
        viewSelector.addButtonStyleName("main-selector");
        viewSelector.setItems(viewOptions);        
        viewSelector.addValueChangeListener(event -> changeView(event));
        
        Button homeLink = new Button("Home");
        homeLink.setIcon(VaadinIcons.ARROW_LEFT);
        homeLink.addStyleName(ValoTheme.BUTTON_LINK);
        homeLink.addStyleNames("own-button-link-style", "own-button-link-style:active", "home-link");
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
        viewSelector.setValue(viewOption);
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
    
    private void changeView(HasValue.ValueChangeEvent event) {        
        MenuOption option = (MenuOption) event.getValue();
        
        //if (option.equals("null") || option.equals(currentView)){
        if (option == currentView ){
            return;
        }
        
        if (option == MenuOption.SNP_PLOT) {
            if (snpPlotBoxOld == null) {
                snpPlotBoxOld = new SNPPlotBoxOld();
            }
            setMiddleBox(snpPlotBoxOld.getComponent());
        }
        if (option == MenuOption.SNP_STATISTICS) {
            if (statsPage == null) {
                statsPage = new SummaryPage().getComponent();
            }
            setMiddleBox(statsPage);
        }
        else if (option == MenuOption.SUMMARY_STATISTICS) {
            if (overlayPlotBox == null) {
                overlayPlotBox = new OverlayPlotBox();
            }
            setMiddleBox(overlayPlotBox.getComponent());
        }
        else if (option == MenuOption.NEW_SNP_PLOT) {
            if (snpPlotBox == null) {
                snpPlotBox = new SNPPlotBox();
            }
            setMiddleBox(snpPlotBox.getComponent());
        }
        else if (option == MenuOption.MANHATTAN) {
            
            // start of code that can be deleted
            
            Panel panel = new Panel();
            VerticalLayout layout = new VerticalLayout();
            layout.setSizeFull();
            Label label = new Label("Manhattan plot and associated content goes here.");
            layout.addComponent(label);
            layout.setComponentAlignment(label, Alignment.MIDDLE_CENTER);
            panel.setContent(layout);
            panel.setSizeFull();
            
            // end of code that can be deleted
            
            setMiddleBox(panel); // replace the panel with the actual content component
        }
        currentView = option;      
    }

    private void goHome() {
        LandingPage landingPage = (LandingPage) highLevelComponents.get("landing page");
        getComponent().getUI().setContent(landingPage.getComponent());
        //upper.setContent(upper.getLandingPage());
    }    
}
