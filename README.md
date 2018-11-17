This is a prototype web application for the visualization of summary statistics from the Norwegian Mother and Child Cohort Study (MoBa).


# Normal order of code execution

`MyUI.java` is executed first, specfically the `init` method. 

Via `Controller.java`, `MyUI.java` calls on either `LandingPage.java` or directly on `VisualizationBox.java`.

The visualizations available through the web application are defined in `Controller.java`.

# Demo

A publicly available demonstration of the web application can be found at https://helse-data.no/demo.
