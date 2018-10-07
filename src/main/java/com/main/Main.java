package com.main;

import com.snp.SNP;
import com.visualization.geno.SNPStatisticsBox;
import com.visualization.geno.LocusZoomBox;
import com.components.ClickMenu;
import com.visualization.summary.SummaryStatisticsBox;
import com.visualization.geno.SNPPlotBox;
import com.utils.Constants;
import com.utils.HtmlHelper;
import com.utils.UtilFunctions;
import com.vaadin.data.HasValue;
import com.vaadin.event.LayoutEvents.LayoutClickEvent;
import com.vaadin.event.ShortcutAction;
import com.vaadin.event.ShortcutListener;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.server.Sizeable;
import com.vaadin.shared.ui.ContentMode;
import com.vaadin.ui.AbstractComponent;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.ValoTheme;
import com.main.Controller.Visualization;
import com.visualization.geno.ManhattanPlotBox;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import com.visualization.MoBaVisualizationInterface;

public class Main {    
    Map <String, Object> highLevelComponents;
    
    VerticalLayout box = new VerticalLayout();  //100, 1000
    //GridLayout mainGrid = new GridLayout(100, 1000); 
    Component currentContentBox;    
    
    Window mainMenu;
    boolean mainMenuBlurred;
    
    AbstractComponent statsPage;
    HorizontalLayout topBox = new HorizontalLayout();
    //HorizontalLayout leftCornerBox = new HorizontalLayout();
    HorizontalLayout middleBox = new HorizontalLayout();
    GridLayout percentilePlotGrid;    
    SNPPlotBox snpPlotBox;
    LocusZoomBox locusZoomBox;
    ManhattanPlotBox manhattanPlotBox;
    SummaryStatisticsBox overlayPlotBox;
    
    ClickMenu <Visualization> viewSelector;
    Button mainMenuButton = new Button("Menu");
    List <Visualization> viewOptions = new ArrayList();
    Visualization currentView;
    
    List <List <Double>> percentileData;
 
    boolean setupOngoing = true;
    
    Constants constants = new Constants();
    String[] ages = constants.getAges();
    
    boolean userVersion = false;
    
    HtmlHelper htmlHelper = new HtmlHelper();
    UtilFunctions utilityFunctions = new UtilFunctions();

    public Main(Map <String, Object> independentComponents) {
        this.highLevelComponents = independentComponents;
        
        // top row
        // view selection        
        viewOptions.addAll(Arrays.asList(Controller.Visualization.values()));      
        
        viewSelector = new ClickMenu("Select data to visualise");
        //viewSelector.setTextInputAllowed(false);
        viewSelector.addButtonStyleName("main-selector");
        viewSelector.setItems(viewOptions);        
        viewSelector.addValueChangeListener(event -> changeView(event));
        
        createMainMenu();
        mainMenuButton.addStyleName("main-selector");
        mainMenuButton.setIcon(VaadinIcons.MENU);
        mainMenuButton.addClickListener(event -> mainMenuButtonListener());
        
        Button homeLink = new Button("Home");
        homeLink.setIcon(VaadinIcons.ARROW_LEFT);
        homeLink.addStyleName(ValoTheme.BUTTON_LINK);
        homeLink.addStyleNames("own-button-link-style", "own-button-link-style:active");//, "home-link");
        homeLink.addClickListener(event -> goHome());
        Label title = new Label(htmlHelper.bold("MoBa visualisation prototype"), ContentMode.HTML);        
        
        topBox.addComponent(homeLink);
        topBox.setComponentAlignment(homeLink, Alignment.TOP_LEFT);
        topBox.setExpandRatio(homeLink, 2);
        //leftCornerBox.addComponent(viewSelector);
        topBox.addComponent(mainMenuButton);
        
        topBox.setComponentAlignment(mainMenuButton, Alignment.TOP_LEFT);
        topBox.setExpandRatio(mainMenuButton, 2);
        
        //int n1 = (int) ((mainGrid.getColumns()-1)*0.01);
        //int n10 = (int) ((mainGrid.getColumns()-1)*0.1);
        //mainGrid.addComponent(leftCornerBox, 0, 0, 2*n10+6, 8);
        //mainGrid.addComponent(title, 4*n10, 0, 6*n10, 0);
        //mainGrid.addLayoutClickListener(event -> listenToLayoutClick(event));
        
        //mainBox.addComponent(leftCornerBox);
        //mainBox.setExpandRatio(leftCornerBox, n10);
        
        //topBox.addComponent(leftCornerBox);
        //topBox.setExpandRatio(leftCornerBox, 1);
        //topBox.setComponentAlignment(leftCornerBox, Alignment.TOP_LEFT);
        topBox.addComponent(title);
        topBox.setExpandRatio(title, 8);
        topBox.setComponentAlignment(title, Alignment.MIDDLE_LEFT);
        //topBox.addStyleName(ValoTheme.PANEL_WELL);
        box.addComponent(topBox);
        box.setExpandRatio(topBox, 1);
        box.addComponent(middleBox);
        box.setExpandRatio(middleBox, 100);
        box.addLayoutClickListener(event -> listenToLayoutClick(event));
        
        
        
//        System.out.println("2*n10+6: " + 2*n10+6);
//        System.out.println("4*n10: " + 4*n10);
//        System.out.println("6*n10: " + 6*n10);

//        Info:   2*n10+6: 186
//        Info:   4*n10: 36
//        Info:   6*n10: 54

        // end of setup for main grid

        // set sizes to full
        viewSelector.setSizeFull();
        homeLink.setSizeFull();
        topBox.setSizeFull();
        //leftCornerBox.setSizeFull();
        middleBox.setSizeFull();
        //mainGrid.setSizeFull();
        box.setSizeFull();
    }
    
    
     
    private void createMainMenu() {
        mainMenu = new Window();
        mainMenu.setClosable(false);
        mainMenu.setResizable(false);
        mainMenu.setHeight(100, Sizeable.Unit.PERCENTAGE);
        mainMenu.addBlurListener(event -> mainMenuBlurListener());
        VerticalLayout mainMenuContent = new VerticalLayout();
        
        mainMenuContent.addComponent(new Label(htmlHelper.bold("Menu"), ContentMode.HTML));
        
        for (Controller.Visualization option : Controller.Visualization.values()) {
            Button optionButton = new Button("• " + option.toString());
            optionButton.addClickListener(event -> listenToMainMenuClick(option));
            optionButton.addStyleNames(ValoTheme.BUTTON_LINK, "own-button-link-style", "own-button-link-style:active");
            mainMenuContent.addComponent(optionButton);
        }
        
        mainMenu.setContent(mainMenuContent);
        
    }
    
    private void navigateWithArrow(int key) {
        System.out.println("key nav, key: " + key);
        if (currentView == Visualization.VARIANT_PLOT) {
            if (key == ShortcutAction.KeyCode.ARROW_RIGHT) {
                setView(Visualization.LOCUS_ZOOM);
            }
        }
        else if (currentView == Visualization.LOCUS_ZOOM) {
            if (key == ShortcutAction.KeyCode.ARROW_LEFT) {
                setView(Visualization.VARIANT_PLOT);
            }
            else if (key == ShortcutAction.KeyCode.ARROW_RIGHT) {
                setView(Visualization.MANHATTAN);
            }
        }
        else if (currentView == Visualization.MANHATTAN) {
            if (key == ShortcutAction.KeyCode.ARROW_LEFT) {
                setView(Visualization.LOCUS_ZOOM);
            }
        }
    }

    private void setMiddleBoxLayout(AbstractComponent middleBoxLayout) {
        if (currentContentBox != null) {
            middleBox.removeComponent(currentContentBox);
        }        
        //mainGrid.addComponent(middleBox, 0, 15, mainGrid.getColumns()-1, mainGrid.getRows()-1); 
        middleBox.addComponent(middleBoxLayout);

        
        currentContentBox = middleBoxLayout;
        // TODO: confirm listener management
        middleBoxLayout.addShortcutListener(new ShortcutListener("right arrow,", ShortcutAction.KeyCode.ARROW_RIGHT, null) {
            @Override
            public void handleAction(Object sender, Object target) {
                navigateWithArrow(ShortcutAction.KeyCode.ARROW_RIGHT);
            }
        });
        middleBoxLayout.addShortcutListener(new ShortcutListener("left arrow,", ShortcutAction.KeyCode.ARROW_LEFT, null) {
            @Override
            public void handleAction(Object sender, Object target) {
                navigateWithArrow(ShortcutAction.KeyCode.ARROW_LEFT);
            }
        });
    }
    
    public void setViewWithSNP (Controller.Visualization option, String query) {
        if (option.equals(Visualization.LOCUS_ZOOM)) {
            if (locusZoomBox == null) {
                //locusZoomBox = new LocusZoomBox();
            }
            locusZoomBox.setRegion(query);
            setMiddleBoxLayout(locusZoomBox.getComponent());
            currentView = option;
        }
        else if (option == Visualization.VARIANT_PLOT) {
            if (snpPlotBox == null) {
                //snpPlotBox = new SNPPlotBox();
            }
            snpPlotBox.searchSNP(query);
            setMiddleBoxLayout(snpPlotBox.getComponent());
            currentView = option;
        }
        //snpPlotBox.searchSNP(query);
    }
    
    public void setView(Controller.Visualization option) {
        if (option == currentView ){
            return;
        }
        
        if (option == Visualization.VARIANT_PLOT) {
            if (snpPlotBox == null) {
                //snpPlotBox = new SNPPlotBox();
            }
            setMiddleBoxLayout(snpPlotBox.getComponent());
        }
        else if (option == Visualization.SNP_STATISTICS) {
            if (statsPage == null) {
                //statsPage = new SNPStatisticsBox().getComponent();
            }
            setMiddleBoxLayout(statsPage);
        }
        else if (option == Visualization.SUMMARY_STATISTICS) {
            if (overlayPlotBox == null) {
                //overlayPlotBox = new SummaryStatisticsBox();
            }
            setMiddleBoxLayout(overlayPlotBox.getComponent());
        }
//        else if (option == Visualization.OLD_SNP_PLOT) {
//            if (snpPlotBoxOld == null) {
//                snpPlotBoxOld = new SNPPlotBoxOld();
//            }
//            setMiddleBoxLayout(snpPlotBoxOld.getComponent());
//        }
        else if (option == Visualization.LOCUS_ZOOM) {
            if (locusZoomBox == null) {
                //locusZoomBox = new LocusZoomBox();
            }
            locusZoomBox.setSNP(getCurrentSNP());
            setMiddleBoxLayout(locusZoomBox.getComponent());
        }
        else if (option == Visualization.MANHATTAN) {
            if (manhattanPlotBox == null) {
                //manhattanPlotBox = new ManhattanPlotBox(this); // TODO
            }
            setMiddleBoxLayout(manhattanPlotBox.getComponent());
        }
        currentView = option;
        if (viewSelector.getValue() == null) {
            viewSelector.setValue(option);
        }
    }
    
    private void changeView(HasValue.ValueChangeEvent <Visualization> event) {        
        Visualization option = event.getValue();
        
        if (option == currentView ){
            return;
        }
        setView(option);
    }
    
    private void listenToMainMenuClick(Visualization option) {
        System.out.println("Option selected from main menu: " + option);
        setView(option);
        showMainMenu(false);
        //Timer timer = new Timer();
        
//        timer.schedule(new TimerTask() {            
//            @Override
//            public void run() {
//                getComponent().getUI().getUI().access(new Runnable() {
//                    @Override
//                    public void run() {
//                        mainMenu.close();
//                    }
//                });
//
//                //showMainMenu(false);
//                System.out.println("task run");
//            }
//        }, 2*1000);
        
    }
    
    private void listenToLayoutClick(LayoutClickEvent event) {
        //System.out.println("event: " + event);
        Component clickedComponent = event.getChildComponent();
        System.out.println("Clicked component: " + clickedComponent);
        if (mainMenu.isAttached()) { // is the window already open?
            showMainMenu(false);
        }
    }

    private void mainMenuButtonListener () {
        if (!mainMenu.isAttached()) { // is the window already open?
            showMainMenu(true);
        }
        else{
            showMainMenu(false);
        }
        
        //mainMenuButton.setEnabled(false);
    }
    
    private void mainMenuBlurListener () {
        //System.out.println("main menu blurred");
        //mainMenuBlurred = true;
        //showMainMenu(false);
        //mainMenuButton.setEnabled(true);
    }
    
    private void showMainMenu(boolean show) {
        if (show) {
             getComponent().getUI().getUI().addWindow(mainMenu);
             mainMenu.focus(); // set focous for the blur listener
        }
        else{
            mainMenu.close();
        }
    }

    
    private SNP getCurrentSNP () {
        if (currentView == Visualization.VARIANT_PLOT) {
            return snpPlotBox.getCurrentSNP();
        }
        else {
            return null;
        }
    }
    
    public Component getComponent() {
        return box;
    }
    
    private void goHome() {
        LandingPage landingPage = (LandingPage) highLevelComponents.get("landing page");
        getComponent().getUI().setContent(landingPage.getComponent());
        //upper.setContent(upper.getLandingPage());
    }
}
