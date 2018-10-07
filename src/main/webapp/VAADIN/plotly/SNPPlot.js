var SNPPlot = SNPPlot || {};

/**
 * 
 * Component object constructor.
 * 
 * @constructor
 */
SNPPlot.Component = function (element, number) {
    plotlyPlot.call(this, element, number, 'SNP', {}); // inherit via the call() method
    
    var activePlotType;
    var activePlot;
    
    this.currentData;
    this.currentPlotOptions;
    
    this.genotypes = ['AA', 'AB', 'BB'];
    this.dataTypes = ['median', 'lower SEM', 'upper SEM', '2.5%', '97.5%'];

    var twoDPlot = new SNP2DPlot.Object(this);
    var threeDPlot = new SNP3DPlot.Object(this);
    
    var optionsActivePlotMap = {
        true : '3D',
        false: '2D'
    };
    this.ageSpacingMap = {
        'to scale' : 'linear',
        'equal' : 'category'
    };
    
    /**
     * Sets up the plot.
     * 
     * @param {object} setup
     */
    this.setUp = function (setup) {
        console.log('setup: ' + JSON.stringify(setup));
        activePlotType = optionsActivePlotMap[setup['3D']];
        this.setActivePlot(activePlotType);
        this.currentPlotOptions = setup;
    };
    
    
    /**
     * Sets the data to be visualised.
     * 
     * @param {object} newData
     */
    this.setData = function (newData) {
        // this variable being updated before the setPlot() command allows it to be used while the command is executed
        this.currentData = newData; 
        activePlot.setData(newData);
    };
    
    /**
     * Adjust the settings of the plot
     * 
     * @param {object} newPlotOptions
     */
    this.setPlotOptions = function (newPlotOptions) {
        //console.log('newPlotOptions: ' + JSON.stringify(newPlotOptions));
        if (newPlotOptions != null) {
            var changedPlotOptions = [];
            
            for (var option in newPlotOptions) {
                if (newPlotOptions.hasOwnProperty(option)) {
                    if (newPlotOptions[option] != this.currentPlotOptions[option])
                    changedPlotOptions.push(option);
                }
            }
            this.currentPlotOptions = newPlotOptions;
            
            
            if (activePlotType != optionsActivePlotMap[newPlotOptions['3D']]) {
                activePlotType = optionsActivePlotMap[newPlotOptions['3D']];
                this.setActivePlot(activePlotType);
            }
            
            activePlot.setPlotOptions(changedPlotOptions);
            
        }
    };
    
    /**
     * Choose between 2D and 3D
     * 
     * @param {string} option
     */
    this.setActivePlot = function (option) {
        if (activePlot != null) {
            activePlot.inactivate();
        }
        if (option == '2D') {
            activePlot = twoDPlot;
        }
        else if (option == '3D') {
            activePlot = threeDPlot;
        }
        if (this.currentData != null) {
            activePlot.setData(this.currentData);
        }
        console.log('Setting active plot: ' + option);
    };
    
    this.getMinY = function () {
        var minY;
        if (this.currentPlotOptions['y to zero']) {
            minY = 0;
        }
        else if (this.currentPlotOptions['percentiles']) {
            minY = this.currentData['percentile min'];
        }
        else {
            minY = this.currentData['SEM min'];
        }
        return minY;
    };
    
    this.getMaxY = function () {
        var maxY;
        if (this.currentPlotOptions['percentiles']) {
            maxY = this.currentData['percentile max'];
        }
        else {
            maxY = this.currentData['SEM max'];
        }
        return maxY;
    };
    
};
