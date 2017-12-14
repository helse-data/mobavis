// book-examples/src/com/vaadin/book/examples/client/js/mycomponent-connector.js

window.com_javascript_PlotlyJs =
	function() {
		// Create the component
		var plotterComponent =
			new plotter.Component(this.getElement());
	
                // Pass on information sent from the Java code 
		this.onStateChange = function() {
                    console.log("Value sent to connector: " + this.getState().data);
                    plotterComponent.setValue(this.getState().data);                        
		};
	};