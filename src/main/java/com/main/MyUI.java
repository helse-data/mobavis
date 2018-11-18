package com.main;

import com.demo.temp.DemoCoverPage;
import com.main.Controller.ContentType;
import com.main.Controller.Visualization;
import javax.servlet.annotation.WebServlet;
import com.vaadin.annotations.Theme;
import com.vaadin.annotations.Title;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.server.VaadinServletRequest;
import com.vaadin.ui.UI;
import javax.servlet.http.HttpServletRequest;

@Theme("mobatheme")
@Title("Mobavis")
public class MyUI extends UI {
    LandingPage landingPage;
    
    /**
     * This is the entry point for the web application; code initation starts here.
     * 
     * @param vaadinRequest 
     */
    @Override    
    protected void init(VaadinRequest vaadinRequest) {
        boolean showLandingPage = true;
        boolean demo = false;
        
        HttpServletRequest httpServletRequest = ((VaadinServletRequest) vaadinRequest).getHttpServletRequest();
        String requestUrl = httpServletRequest.getRequestURL().toString();
        
        if (requestUrl == null || !requestUrl.startsWith("https://localhost")) {
            showLandingPage = true; // simple safeguard to ensure that the landing page is shown if the application is available online
        }
        
        // launching the demo
        if (demo) {
            DemoCoverPage demoCoverPage = new DemoCoverPage();
            setContent(demoCoverPage.getComponent());
            return;
        }        
        
        Controller controller = new Controller(this);
        landingPage = new LandingPage(controller);
        
        if (showLandingPage) {
            controller.setContentType(ContentType.LANDING_PAGE);
        }
        else {
            //controller.setVisualization(Visualization.MOTHER);
            controller.setVisualization(Visualization.MANHATTAN);
            controller.setContentType(ContentType.VISUALIZATION);
        }
    }
    
    @WebServlet(urlPatterns = "/*", name = "MyUIServlet", asyncSupported = true)
    @VaadinServletConfiguration(ui = MyUI.class, productionMode = false)
    public static class MyUIServlet extends VaadinServlet {
    }
}
