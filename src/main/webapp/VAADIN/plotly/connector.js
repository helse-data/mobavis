/** 
 * 
 * Connectors allow the Java code and the JavaScript code to communicate with each other.
 *
 */

var i = 0; // div IDs for separate plots must be different, this number is passed on to plotlyPlot for this purpose

/** 
 * Connector function for the plot stratifying phenotype by genotype.
 */
window.com_plotly_SNPPlot =
        
	function() {
            i = i + 1;
            // the object is constructed each time it reappears; the boolean versions will then become out of sync
            var firstData = true;
                
            var booleanVersions = {
                    data            : true,
                    'plot options'  : true,
                    'active plot'   : true
                };

            var SNPPlotComponent =
                    //new SNPPlot.Component(this.getElement(), i);
                    new SNPPlot.Component(this.getElement(), i);

            
            var isSetUp = false; // whether or not the plot has been set up
            
            // Pass on information sent from the Java code 
            this.onStateChange = function() {
                var setup = this.getState().setup;
                var data = this.getState().data;
                var plotOptions = this.getState().plotOptions;
                var activePlot = this.getState().activePlot;
                
                //console.log('plot options: ' + JSON.stringify(plot options));
                
                if (!isSetUp) {
                    if (setup != null) {
                        SNPPlotComponent.setUp(setup)
                        isSetUp = true;                                                
                    }
                    
                }
                if (isSetUp) {

                    if (data != null) {
                        //console.log("Data sent to connector: " + JSON.stringify(data));
                        if (firstData || booleanVersions['data'] != data['boolean version']) {
                            console.log('New data provided.')
                            //SNPPlotComponent.updateData(data);
                            SNPPlotComponent.setData(data);
                            booleanVersions['data'] = data['boolean version'];
                        };
                    };
                    if (plotOptions != null) {
                        //console.log("Show status sent to connector: " + JSON.stringify(plot options));
                        if (firstData || booleanVersions['plot options'] != plotOptions['boolean version']) {
                            console.log('New plot options provided.')
                            //SNPPlotComponent.setPlotOptions(plotOptions);
                            SNPPlotComponent.setPlotOptions(plotOptions);
                            booleanVersions['plot options'] = plotOptions['boolean version'];
                        };
                    };
                    if (activePlot != null) {
                        console.log('Active plot sent to connector: ' + JSON.stringify(activePlot));
                        if (firstData || booleanVersions['active plot'] != plotOptions['active plot']) {
                            console.log('New active plot set.')
                            //SNPPlotComponent.setPlotOptions(plotOptions);
                            SNPPlotComponent.setActivePlot(activePlot);
                            booleanVersions['active plot'] = activePlot['boolean version'];
                        };
                    }
                    firstData = false;
                };
            };
	};
        
/** 
 * Connector function for the plot that allows the user to overlay data.
 */
function OverlayCommon(plotObject) {    
    this.plotObject = plotObject;
    
    var firstData = true;
    var currentShowStatus = null;
    
    var booleanVersions = {
        percentiles : true,
        'user data' : true,
        'user ages' : true,
        'meta data' : true,
        size : true
    };
    
    this.manageData = function(metaData, percentileData, userData, userAges, showStatus, size) {        
        console.log('meta data: ' + JSON.stringify(metaData));
        console.log('percentile data: ' + JSON.stringify(percentileData));
        console.log('plot size: ' + JSON.stringify(size));
        //console.log('user data: ' + JSON.stringify(userData));
        console.log('user ages: ' + JSON.stringify(userAges));
        console.log('show status: ' + JSON.stringify(showStatus));
        

        if (metaData != null) {
            if (firstData || booleanVersions['meta data'] != metaData['boolean version']) {
                console.log('New meta data provided.')
                plotObject.setup(metaData);
                booleanVersions['meta data'] = metaData['boolean version'];
            };
        };
        if (percentileData != null) {
            console.log('booleanVersions[\'percentiles\']: ' + booleanVersions['percentiles']);
            if (firstData || booleanVersions['percentiles'] != percentileData['boolean version']) {
                console.log('New percentile data provided: ' + JSON.stringify(percentileData));
                plotObject.setPercentileData(percentileData);
                booleanVersions['percentiles'] = percentileData['boolean version'];
            };
        };
        if (userData != null){
            if (firstData || booleanVersions['user data'] != userData['boolean version']) {
                console.log('New user data provided.');//: ' + JSON.stringify(userData))
                plotObject.updateUserData(userData);
                booleanVersions['user data'] = userData['boolean version'];
            };                    
        };
        if (userAges != null){
            if (firstData || booleanVersions['user ages'] != userAges['boolean version']) {
                console.log('New user ages provided.');//: ' + JSON.stringify(userData))
                plotObject.setAges(userAges);
                booleanVersions['user ages'] = userAges['boolean version'];
            };                    
        };
        if (showStatus != null){
            if (currentShowStatus != showStatus || currentShowStatus == null) {
                console.log('New show status provided.');
                //console.log(JSON.stringify(showStatus));
                plotObject.showPercentiles(showStatus);
                currentShowStatus = showStatus;
            };                    
        };
        if (size != null){
            if (firstData || booleanVersions['size'] != size['boolean version']) {
                console.log('New plot size provided.');
                plotObject.setSize(size);
                booleanVersions['size'] = size['boolean version'];
            };                    
        };
        firstData = false;
    };
};

/** 
 * Connector function for the plot that allows the user to overlay data.
 */
window.com_plotly_OverlayPlot =            
	function() {           
            i = i + 1;
            
            var overlayPlotComponent =
                    new overlayPlot.Component(this.getElement(), i);
            
            var dataManager = new OverlayCommon(overlayPlotComponent);
            
            this.onStateChange = function() {
                console.log('OverlayPlot state change');
                
                var metaData = this.getState().metaData;
                var percentileData = this.getState().percentileData;
                var userData = this.getState().userData;
                var userAges = this.getState().userAges;
                var showStatus = this.getState().showStatus;
                var size = this.getState().size;
                
                dataManager.manageData(metaData, percentileData, userData, userAges, showStatus, size);
            };
        };

/** 
 * Connector function for the bar chart.
 */
window.com_plotly_BarPlot =               
	function() {            
            i = i + 1;
            
            var booleanVersions = {
                size : true
            };

            //var dataSet = false;
            var setUp = false;
            
            var barPlotComponent =
                    new barPlot.Component(this.getElement(), i);

            this.onStateChange = function() {
                var setupData = this.getState().setupData;
                var data = this.getState().data;
                var size = this.getState().size;

                //if (data != null && dataSet == false) {
                if (setupData != null && !setUp) {
                    //console.log('Data provided:' + JSON.stringify(data));
                    barPlotComponent.setUp(setupData);
                    setUp = true;
                };
                if (setUp && data != null) {
                    //console.log('Data provided:' + JSON.stringify(data));
                    barPlotComponent.setData(data);
                    //dataSet = true;
                };
                 if (size != null) {
                    console.log('plot size: ' + JSON.stringify(size));
                    if (booleanVersions['size'] != size['boolean version']) {
                        console.log('New plot size provided.')
                        barPlotComponent.setSize(size);
                        booleanVersions['size'] = !booleanVersions['size'];
                    };
                };
            };
        };
        
/**
 * 
 * Connector function for the plot visualizing non-longitudinal percentile data.
 * 
 */        
window.com_plotly_NonLongitudinalPercentiles =               
	function() {            
            i = i + 1;
            
            var nonLongitudinalPercentilesComponent = null;

            this.onStateChange = function() {
                var setupData = this.getState().setupData;
                var data = this.getState().data;
                //console.log('Data provided:' + JSON.stringify(data));
                if (setupData != null && nonLongitudinalPercentilesComponent == null) {
                    nonLongitudinalPercentilesComponent = new nonLongitudinalPercentiles.Component(this.getElement(), i, setupData);
                };
                if (nonLongitudinalPercentilesComponent != null && data != null) {
                    console.log('New data provided');//:' + JSON.stringify(data));
                    nonLongitudinalPercentilesComponent.setData(data);
                };
            };
        };
/**
 * 
 * Connector function for for the 3D version of the plots
 * stratifying phenotype by genotype.
 * 
 */        
window.com_plotly_SNP3DPlot =               
	function() {            
            i = i + 1;
            
            var SNP3DPlotComponent = null;

            this.onStateChange = function() {
                var setupData = this.getState().setupData;
                var data = this.getState().data;
                //console.log('Data provided:' + JSON.stringify(data));
                if (SNP3DPlotComponent == null) {
                    SNP3DPlotComponent = new SNP3DPlot.Component(this.getElement(), i, {});
                };
                //if (SNP3DPlotComponent != null && data != null) {
                //    console.log('New data provided');//:' + JSON.stringify(data));
                //    SNP3DPlotComponent.setData(data);
                //};
            };
        };
/**
 * Connector function for the Manhattan plot.
 * 
 */
window.com_plotly_ManhattanPlot =               
	function() {            
            i = i + 1;
            
            var manhattanPlotComponent = null;
            
            const variables = ['data', 'options', 'resize'];
            
            var dataManager = new DataManager(this, variables);            

            this.onStateChange = function() {
                var data = this.getState().data;
                var options = this.getState().options;
                var variableChanged = dataManager.manageData();
                
                //console.log('Data provided:' + JSON.stringify(data));
                if (manhattanPlotComponent == null) {
                    manhattanPlotComponent = new manhattanPlot.Component(this.getElement(), i, this);
                };
//                if (manhattanPlotComponent != null && data != null) {
//                    
//                };
                
                if (variableChanged["data"]) {
                    console.log('new data provided, including ' + data["1"].names.length + ' p-values for chromosome 1.');
                    manhattanPlotComponent.setData(data);                    
                }
                if (variableChanged["options"]) {
                    manhattanPlotComponent.setOptions(options);
                }
                if (variableChanged["resize"]) {
                    console.log('resize requested');
                }
            };
            
            var self = this;
            this.registerSNPclick = function (data) {
                console.log('SNP click registered in connector: ' + data);
                self.onSNPclick(data);
            };
        };
        
/**
 * Constructor for an object that manages the versions of incoming data.
 * 
 * @param {type} connectorObject
 * @param {type} variables
 * @returns {DataManager}
 */   
function DataManager(connectorObject, variables) {
    var firstData = true;
    const initialBooleanVersion = true;
    
     var booleanVersions = {};
    
    for (var i = 0; i < variables.length; i++) {
        booleanVersions[variables[i]] = initialBooleanVersion;
    }
    
    this.manageData = function() {
        
        var variableChanged = {};
        
        for (var i = 0; i < variables.length; i++) {
            var variable = variables[i];
            var variableData = connectorObject.getState()[variable];
            if (variableData != null){
                if (firstData || booleanVersions[variable] != variableData['boolean version']) {
                    console.log('New data provided for variable "' + variable + '"' + '.');//: ' + JSON.stringify(userData))
                    booleanVersions[variable] = variableData['boolean version'];
                    variableChanged[variable] = true;
                };                
            }
            else {
                variableChanged[variable] = false;
            }
        }
        
        firstData = false;
        
        return variableChanged;
        
    };
    
}