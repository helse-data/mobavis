package com.main;

import javax.servlet.annotation.WebServlet;

import com.vaadin.annotations.Theme;
import com.vaadin.annotations.Title;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.ui.Component;
import com.vaadin.ui.UI;
import java.util.HashMap;
import java.util.Map;

@Theme("mobatheme")
@Title("Mobavis")
public class MyUI extends UI {
    
    Main main;
    Map <String, Object> highLevelComponents = new HashMap();
    LandingPage landingPage;
       
    @Override    
    protected void init(VaadinRequest vaadinRequest) {
        main = new Main(highLevelComponents);
        highLevelComponents.put("main", main);
        landingPage = new LandingPage(highLevelComponents);
        highLevelComponents.put("landing page", landingPage);
        
        
        boolean showLandingPage = true;
        
        if (showLandingPage) {
            //setContent(new NewLandingPage(highLevelComponents).getComponent());
            setContent(landingPage.getComponent());
        }
        else {
            main.setView(Main.MenuOption.SNP_STATISTICS);
            setContent(main.getComponent());
        }
    }
    
    public void enter(boolean firstLanding, Main.MenuOption viewOption) {
        if (firstLanding) {
            main.setView(viewOption);
        }        
        setContent(main.getComponent());
    }
   
    public Component getLandingPage() {
        return landingPage.getComponent();
    }
    
    @WebServlet(urlPatterns = "/*", name = "MyUIServlet", asyncSupported = true)
    @VaadinServletConfiguration(ui = MyUI.class, productionMode = false)
    public static class MyUIServlet extends VaadinServlet {
    }
}
