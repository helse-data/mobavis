var plotter = plotter || {};

plotter.Component = function (element) {
	element.innerHTML =
                "<div id=\"plot\"\></div>";
                //"<div id=\"plot\" style=\"width:100%;height:100%;\"></div>";
                //"<script type=\"text/javascript\" src=\"https://cdn.plot.ly/plotly-latest.min.js\"></script>" +
                //"<script src=\"main.js\"></script>" +
        
        var mainPlot = new main.Main();      

	this.setValue = function (data) {
            //const data = JSON.parse("[" + dataString + "]");
            //console.log(data);
            console.log("Value sent to plotter: " + data);
            mainPlot.updateData(data);
	};  
};