package com.main;

import javax.servlet.annotation.WebServlet;

import com.vaadin.annotations.Theme;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.ui.Component;
import com.vaadin.ui.UI;

@Theme("mytheme")
public class MyUI extends UI {
    
     Main main;
     LandingPage landingPage;
       
    @Override
    protected void init(VaadinRequest vaadinRequest) {
        landingPage = new LandingPage(this);
        setContent(landingPage.getComponent());
        main = new Main(this);
    }
    
    public void enter(boolean firstLanding) {
        if (firstLanding) {
            main.execute();
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
