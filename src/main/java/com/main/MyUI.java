package com.main;

import com.demo.DemoCoverPage;
import com.main.Controller.ContentType;
import com.main.Controller.Visualization;
import javax.servlet.annotation.WebServlet;
import com.vaadin.annotations.Theme;
import com.vaadin.annotations.Title;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.ui.UI;

@Theme("mobatheme")
@Title("Mobavis")
public class MyUI extends UI {
    LandingPage landingPage;
       
    @Override    
    protected void init(VaadinRequest vaadinRequest) {
        boolean showLandingPage = false;
        boolean demo = false;
        
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
            controller.setContentType(ContentType.VISUALIZATION);
        }
    }
    
//    public void enter(boolean firstLanding, Controller.Visualization viewOption) {
//        if (firstLanding) {
//            //main.setView(viewOption);
//        }        
//        setContent(main.getComponent());
//    }
   
//    public Component getLandingPage() {
//        return landingPage.getComponent();
//    }
    
    @WebServlet(urlPatterns = "/*", name = "MyUIServlet", asyncSupported = true)
    @VaadinServletConfiguration(ui = MyUI.class, productionMode = false)
    public static class MyUIServlet extends VaadinServlet {
    }
}
