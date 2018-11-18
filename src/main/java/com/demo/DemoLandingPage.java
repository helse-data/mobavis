package com.demo;

import com.demo.DemoMain.DemoOption;
import com.utils.vaadin.ClosableMessage;
import com.vaadin.ui.AbstractOrderedLayout;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;
import java.util.HashMap;
import java.util.Map;


/**
 *
 * @author Christoffer Hjeltnes Støle
 */
public class DemoLandingPage {
    VerticalLayout page = new VerticalLayout();
    
    Map <Integer, Boolean> genoTabLoaded = new HashMap();
    Map <Integer, Class> genoTabClasses = new HashMap();
    Map <Integer, Boolean> summaryTabLoaded = new HashMap();
    Map <Integer, Class> summaryTabClasses = new HashMap();
    
    Map <Integer, Map <Integer, DemoTabWrapper>> tabWrappers = new HashMap();
    Map <DemoOption, Map <String, Integer>> viewTabIndices = new HashMap();
    //Map <Integer, TabWrapper> genoTabWrappers = new HashMap();
    //Map <Integer, TabWrapper> summaryTabWrappers = new HashMap();
    
    DemoController controller;
    
    TabSheet outerTabSheet;
    TabSheet genoTabSheet;
    TabSheet summaryTabSheet;
    
    String messageText;
    
    public DemoLandingPage() {
        System.out.println("***** DEMO *****");
        page.addStyleName("white");
        
        controller = new DemoController();
        //state.getGenoState().setSNP(new SNP("rs13046557"));
        
        HorizontalLayout genoContainer = new HorizontalLayout();
        genoContainer.setSizeFull();
        
        
        //genoContainer.addComponent(SNPInput);
        //genoContainer.setExpandRatio(SNPInput, 1);
        //genoContainer.setComponentAlignment(SNPInput, Alignment.TOP_RIGHT);
        
        messageText = "The Norwegian Mother and Child Cohort Study (MoBa) recruited more than 90,000 pregnant women and more than 70,000 fathers between 1998 and 2008. " +
                "Phenotype data for the children was collected for 12 ages - from birth to age 8. " +
                "In addition, approximately 16,900 trios, representing over 50,000 donors, underwent genotyping." + 
                "<br><br> If you are a parent, you are most likely interested in the summary statistics.";
        
        ClosableMessage message = new ClosableMessage(messageText, "Background information", page);
        
        page.addComponent(message.getComponent());
        page.setExpandRatio(message.getComponent(), 1);
        page.setComponentAlignment(message.getComponent(), Alignment.TOP_CENTER);
        

        genoTabSheet = new TabSheet();
        genoTabSheet.setSizeFull();
        genoTabSheet.addStyleName(ValoTheme.TABSHEET_CENTERED_TABS);
        genoContainer.addComponent(genoTabSheet);
        //genoContainer.setExpandRatio(genoTabSheet, 10);
        //genoContainer.setComponentAlignment(genoTabSheet, Alignment.MIDDLE_CENTER);
        
        summaryTabSheet = new TabSheet();
        summaryTabSheet.setSizeFull();
        summaryTabSheet.addStyleName(ValoTheme.TABSHEET_CENTERED_TABS);
        
        tabWrappers.put(0, new HashMap());
        tabWrappers.put(1, new HashMap());
        
        int iGeno = 0;
        int iSummary = 0;
        for (DemoOption option : DemoOption.values()) {
            DemoTabWrapper tabWrapper = new DemoTabWrapper(option.getViewClass(), this.controller);
            viewTabIndices.put(option, new HashMap());
            if (option.hasGeneticData()) {
                //genoTabWrappers.put(iGeno, tabWrapper);
                genoTabClasses.put(iGeno, option.getViewClass());
                genoTabLoaded.put(iGeno, false);
                tabWrappers.get(0).put(iGeno, tabWrapper);
                viewTabIndices.get(option).put("outer", 0);
                viewTabIndices.get(option).put("inner", iGeno);
                genoTabSheet.addTab(tabWrapper.getComponent(), iGeno);
                genoTabSheet.getTab(iGeno).setCaption(option.toString());
                iGeno++;
            }
            else {                
                //summaryTabWrappers.put(iSummary, tabWrapper);
                tabWrappers.get(1).put(iSummary, tabWrapper);
                viewTabIndices.get(option).put("outer", 1);
                viewTabIndices.get(option).put("inner", iSummary);
                summaryTabSheet.addTab(tabWrapper.getComponent(), iSummary);
                summaryTabSheet.getTab(iSummary).setCaption(option.toString());
                genoTabClasses.put(iSummary, option.getViewClass());
                summaryTabLoaded.put(iSummary, false);
                iSummary++;
            }
        }
        
        System.out.println("viewTabIndices: " + viewTabIndices);
        
        outerTabSheet = new TabSheet();
        outerTabSheet.setSizeFull();
        outerTabSheet.addSelectedTabChangeListener(event -> changeTab());
        //outerTabSheet.addTab(genoTabSheet, 0);
        outerTabSheet.addTab(genoContainer, 0);

        outerTabSheet.getTab(0).setCaption("Data with genotype information");
        outerTabSheet.addStyleName(ValoTheme.TABSHEET_FRAMED);
        
        outerTabSheet.addTab(summaryTabSheet, 1);
        outerTabSheet.getTab(1).setCaption("Summary cohort statistics");
        
        summaryTabSheet.addSelectedTabChangeListener(event -> changeTab());
        genoTabSheet.addSelectedTabChangeListener(event -> changeTab());

        //tabSheet.addTab(new SNPStatisticsBox().getComponent());
        //tabSheet.getTab(0).setCaption("Statistics");
        
        page.addComponent(outerTabSheet, 0);
        page.setExpandRatio(outerTabSheet, 5);
        
        page.setSizeFull();
    }
    
    private void changeTab() {
        int outerTabIndex = outerTabSheet.getTabPosition(outerTabSheet.getTab(outerTabSheet.getSelectedTab()));
        System.out.println("index of selected outer tab: " + outerTabIndex);
        
        TabSheet innerTabSheet;
        if (outerTabIndex == 0) {
            AbstractOrderedLayout genoContainer = (AbstractOrderedLayout) outerTabSheet.getTab(0).getComponent();
            //innerTabSheet = (TabSheet) genoContainer.getComponent(1);
            innerTabSheet = (TabSheet) genoContainer.getComponent(0);
            // SCRS
        }
        else {
            innerTabSheet = (TabSheet) outerTabSheet.getTab(outerTabIndex).getComponent();
        }
        
        if (innerTabSheet.getTab(innerTabSheet.getSelectedTab()) != null) {
            int innerTabIndex = innerTabSheet.getTabPosition(innerTabSheet.getTab(innerTabSheet.getSelectedTab()));
        
            System.out.println("index of selected inner tab: " + innerTabIndex);

            DemoTabWrapper tabWrapper = tabWrappers.get(outerTabIndex).get(innerTabIndex);

            //TabWrapper tabWrapper = (TabWrapper) innerTabSheet.getTab(innerTabIndex).getComponent();

            if (!tabWrapper.isLoaded()) {
               tabWrapper.loadContents();
            }            
        }
        
        
        
        //tabSheet.setSelectedTab(outerTabIndex);
        
    }
    
    private void changeGenoTab () {
        int tabIndex = genoTabSheet.getTabPosition(genoTabSheet.getTab(genoTabSheet.getSelectedTab()));
        System.out.println("index of selected genotab: " + tabIndex);
        
//        TabWrapper tabWrapper = tabWrappers.get(tabIndex);
//        
//        //if (!genoTabLoaded.get(tabIndex)) {
//        if (!tabWrapper.isLoaded()) {
//            System.out.println("Loading genotab " + tabIndex + " for the first time.");
//            //MoBaView view = instantiateMobaView(genoTabClasses.get(tabIndex));
//            //genoTabSheet.addTab(view.getComponent(), tabIndex);
//            //genoTabLoaded.put(tabIndex, true);
//            tabWrapper.loadContents();
//        }        
        
        //genoTabSheet.getTab(genoTabSheet.getSelectedTab()).setCaption("CAUGHT TAB");        
    }
    
     private void changeSummaryTab () {
        
        int tabIndex = summaryTabSheet.getTabPosition(summaryTabSheet.getTab(summaryTabSheet.getSelectedTab()));
        System.out.println("index of selected summary tab: " + tabIndex);
        
//        TabWrapper tabWrapper = summaryTabWrappers.get(tabIndex);
//        
//         System.out.println("summary tab is attached: " + summaryTabSheet.getSelectedTab().isAttached());
//        
//        //if (!genoTabLoaded.get(tabIndex)) {
//        if (!tabWrapper.isLoaded() && summaryTabSheet.getSelectedTab().isAttached()) {
//            System.out.println("Loading summary tab " + tabIndex + " for the first time.");
//            tabWrapper.loadContents();
//        }
         
     }
    
    
    public Component getComponent() {
        return page;
    }
    
}
