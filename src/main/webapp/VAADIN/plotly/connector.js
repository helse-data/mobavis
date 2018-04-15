var i = 0; // div IDs for separate plots must be different

window.com_plotly_SNPPlot =
        
	function() {
            i = i + 1;
            // the object is constructed each time it reappears; the boolean versions will then become out of sync
            var firstData = true;
                
            var booleanVersions = {
                    data            : true,
                    'plot options'   : true
                };

            var SNPPlotComponent =
                    new SNPPlot.Component(this.getElement(), i);			

            
            // Pass on information sent from the Java code 
            this.onStateChange = function() {
                var data = this.getState().data;
                var plotOptions = this.getState().plotOptions;
                
                //console.log('plot options: ' + JSON.stringify(plot options));

                if (data != null) {
                    //console.log("Data sent to connector: " + JSON.stringify(data));
                    if (firstData || booleanVersions['data'] != data['boolean version']) {
                        console.log('New data provided.')
                        SNPPlotComponent.updateData(data);
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
                firstData = false;
            };
	};
        

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

window.com_plotly_ParameterisedPlot =               
	function() {            
            i = i + 1;
            
            var parameterisedPlotComponent =
                    new parameterisedPlot.Component(this.getElement(), i);
            
            var dataManager = new OverlayCommon(parameterisedPlotComponent);
            
            this.onStateChange = function() {
                var percentileData = this.getState().percentileData;
                var userData = this.getState().userData;
                var showStatus = this.getState().showStatus;
                dataManager.manageData(null, percentileData, userData, showStatus);
            };
        };


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
window.com_plotly_ScatterPlot =               
	function() {            
            i = i + 1;
            
            var scatterPlotComponent = null;

            this.onStateChange = function() {
                var setupData = this.getState().setupData;
                var data = this.getState().data;
                //console.log('Data provided:' + JSON.stringify(data));
                if (setupData != null && scatterPlotComponent == null) {
                    scatterPlotComponent = new scatterPlot.Component(this.getElement(), i, setupData);
                };
                if (scatterPlotComponent != null && data != null) {
                    console.log('New data provided');//:' + JSON.stringify(data));
                    scatterPlotComponent.setData(data);
                };
            };
        };