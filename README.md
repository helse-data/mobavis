# Normal order of code execution

`MyUI.java` is executed first, specfically the `init` method. 

Via `Controller.java`, `MyUI.java` calls on either `LandingPage.java` or directly on `VisualizationBox.java`.

The visualizations available through the web application are defined in `Controller.java`.
