var SNP2DPlot = SNP2DPlot || {};

/**
 * 
 * Constructor of the 2D SNP plot.
 * 
 * @constructor
 * 
 * @param {object} plotObject - the underlying object of the plot
 */
SNP2DPlot.Object = function (plotObject) {
    
    var createNewPlot = true;
    
    /**
     * Tells the object that it should create a new plot the next time it receives new data.
     * 
     */
    this.inactivate = function () {
        createNewPlot = true;
    };
    
    /**
     * Sets the data to be visualised.
     * 
     * @param {object} newData
     */
    
    var thisObject = this; // for use inside the plot.on() functions

    const transparentGray = 'rgba(0, 0, 0, 0.1)';
    const transparentBlue = 'rgba(0, 0, 255, 0.1)';
    const transparentRed = 'rgba(255, 0, 0, 0.1)';    
    
    const transparentColourMap = {
        AA: transparentGray,
        AB: transparentBlue,
        BB: transparentRed};

    const colourMap = {
        AA: 'black',
        AB: 'blue',
        BB: 'red'
    };
    
    const MEDIANS = 'medians';
    const SEM = 'SEM';
    const PERCENTILES = 'percentiles';
    const N = 'n';
    const AGE_SPACING = 'age spacing';
    const Y_TO_ZERO = 'y to zero';
    
    const plotOptionKeys = [MEDIANS, SEM, PERCENTILES, N, AGE_SPACING, Y_TO_ZERO]; 
    
    const statisticIndexMap = { // TODO: redundant
        medians:                {AA : [], AB : [], BB : []},
        SEM:                    {AA : [], AB : [], BB : []},
        percentiles:            {AA : [], AB : [], BB : []}
    };
    
    this.medianIndices = [], this.SEMindices = [], this.percentileIndices = [];
    
    const genotypes = plotObject.genotypes;
    const dataTypes = plotObject.dataTypes;
    
    var yAxisDomains = {
        'main plot': [0.28, 1],
        'number plot': [0, 0.1]
    };
    
    this.barChart;
    var slider;
    this.maxN;
    var minSEM, maxSEM;
    var minPercentile, maxPercentile;
    var rangeY;
    this.rangeX2;
    var currentYRange = [];
    var currentY2Range = [];    
 
    
    //console.log('traceList:' + JSON.stringify(traceList) + ', length:' + traceList.length);

    this.createSteps = function(numbers) {
        //console.log('Creating slider steps ...');
        
        var steps = [];
        for (var i = 0; i < plotObject.ages.length; i++) {
            steps.push({
            label: plotObject.ageLabels[i],
            method: 'restyle',
            args: ['x', [[numbers.AA[i], numbers.AB[i], numbers.BB[i]]], [this.barChartIndex]]
            });
        }
        //console.log('steps: ' + JSON.stringify(steps));
        return steps;
    };

    
    
    /**
     * Sets the options for the plot.
     * 
     * @param {object} newPlotOptions
     */
    this.setPlotOptions2 = function (newPlotOptions) {        
        if (newPlotOptions != null){            
            for (var i = 0; i < plotOptionKeys.length; i++) {
                var option = plotOptionKeys[i];
                //console.log('option: ' + option);
                
                if (newPlotOptions[option] != plotOptions[option]) {
                    plotOptions[option] = newPlotOptions[option];
                    console.log('changed option: ' + option);
                    
                    if (option == MEDIANS || option == SEM || option == PERCENTILES) {
                        var changedIndices = [];
                        for (var i = 0; i < 3; i++) {
                            changedIndices = changedIndices.concat(statisticIndexMap[option][genotypes[i]]); 
                        }
                        //console.log('changeIndices: ' + changeIndices);
                        
                        if (option == SEM || option == PERCENTILES) {
                            
                            var change = false;
                            //console.log('currentYRange: ' + currentYRange);
                            //console.log('min SEM: ' + minSEM + ', maxSEM: ' + maxSEM);
                            if (newPlotOptions[PERCENTILES]) {
                                if (currentYRange[0] == this.minY && currentYRange[1] == maxSEM) {
                                    change = true;
                                    if (this.minY > 0) {
                                        this.minY = minPercentile;
                                    }
                                    rangeY = [this.minY, maxPercentile];
                                }                                    
                            }
                            else {
                                if (currentYRange[0] == this.minY && currentYRange[1] == maxPercentile) {
                                    change = true;
                                    if (this.minY > 0) {
                                        this.minY = minSEM;
                                    }
                                    rangeY = [this.minY, maxSEM];
                                }
                            }
                            if (change) {
                                //console.log('locking axes');
                                Plotly.relayout(plotObject.divID, 'yaxis.autorange', false);
                                Plotly.relayout(plotObject.divID, 'yaxis.range', rangeY);
                            }                            
                        }
                        Plotly.restyle(plotObject.divID, {visible : newPlotOptions[option]}, changedIndices)
                    }
                }
            }
        }
    };
    
    /**
     * Set the options for the plot.
     * 
     * @param {object} changedPlotOptions - the plot options to set
     */
    this.setPlotOptions = function (changedPlotOptions) {
        console.log('newPlotOptions for 2D plot: ' + JSON.stringify(changedPlotOptions));
        for (var i = 0; i < changedPlotOptions.length; i++) {
            var option = changedPlotOptions[i];
            if (option == 'medians') {
                Plotly.restyle(plotObject.divID, {visible : plotObject.currentPlotOptions[option]}, this.medianIndices);
            }
            else if (option == 'SEM') {
                Plotly.restyle(plotObject.divID, {visible : plotObject.currentPlotOptions[option]}, this.SEMindices);
                Plotly.relayout(plotObject.divID, 'yaxis.autorange', true); // trigger axis control; TODO
            }
            else if (option == 'percentiles') {
                console.log('plotObject.currentPlotOptions[option]: ' + JSON.stringify(plotObject.currentPlotOptions[option]));
                console.log('this.percentileIndices: ' + JSON.stringify(this.percentileIndices));
                console.log('before restyle');
                Plotly.restyle(plotObject.divID, {visible : plotObject.currentPlotOptions[option]}, this.percentileIndices);
                Plotly.relayout(plotObject.divID, 'yaxis.autorange', true); // trigger axis control; TODO
            }
            else if (option == 'age spacing') {
                Plotly.relayout(plotObject.divID, 'xaxis.type', plotObject.ageSpacingMap[plotObject.currentPlotOptions['age spacing']]);
            }
            else if (option == 'y to zero') {
                //console.log('plotObject.getMinY(): ' + JSON.stringify(plotObject.getMinY()));
                //Plotly.relayout(plotObject.divID, 'scene.yaxis.range[0]', plotObject.getMinY());
                Plotly.relayout(plotObject.divID, 'yaxis.range[0]', plotObject.getMinY());
            }
            else if (option == 'n') {
                var sliderUpdate, yAxisDomain, annotations;
                if (plotObject.currentPlotOptions['n'] == true) { // show the number of individuals
                    sliderUpdate = slider;
                    yAxisDomain = yAxisDomains['main plot'];
                    annotations = [this.barChartTitle];
                    Plotly.addTraces(plotObject.divID, thisObject.barChart);
                }
                else { // hide the number of individuals
                    sliderUpdate = [];
                    yAxisDomain = [0, 1];
                    annotations = [];
                    Plotly.deleteTraces(plotObject.divID, thisObject.barChartIndex);
                }
                
                var newYaxis = {
                    domain: yAxisDomain,
                    range: rangeY
                    };
                var newXaxis2 = {
                    anchor: 'y2',
                    range: this.rangeX2
                    };
                //console.log('steps: ' + JSON.stringify(this.createSteps(numbers)));
                Plotly.relayout(plotObject.divID, 'yaxis', newYaxis);    
                Plotly.relayout(plotObject.divID, 'xaxis2', newXaxis2);
                Plotly.relayout(plotObject.divID, 'sliders', sliderUpdate);
                Plotly.relayout(plotObject.divID, 'annotations', annotations);
            }
        }
        this.updateAxes();
    };
    
    
    /**
     * 
     * Function for getting the maximum numerical value of an array.
     * 
     * @param {array} array - the array to find the maximal value of
     * @return {number} - the maximal value
     */
    this.getMaxN = function(array) {
        var curMax = -1;
        for (var i = 0; i < array.length; i++) {
            var number = Number(array[i]);
            if (!isNaN(number) && number > curMax) {
                curMax = number;
            }
        }
        return curMax;
    }
    
    /**
     * Sets the data to be visualised.
     * 
     * @param {object} newData
     */
    this.setData = function (newData) {
        //console.log('Setting data for 2D plot: ' + JSON.stringify(newData));
        if (newData != null) {
            
            var numbers = {AA: newData.AA.N, AB: newData.AB.N, BB: newData.BB.N};
            
            if (createNewPlot) {
                console.log('Creating new 2D plot.');
                
                var medianTraces = {};
                var SEMtraces = {lower : {}, upper : {}}, percentileTraces = {lower : {}, upper : {}};

                for (var i = 0; i < genotypes.length; i++) {
                    var genotype = genotypes[i];

                        // median traces

                    var medianTrace = {
                        x : plotObject.ages,
                        y : newData[genotype]['median'],
                        type : 'scatter',
                        legendgroup : genotype,
                        connectgaps : true,
                        visible : plotObject.currentPlotOptions.medians,
                        showlegend : true, // TODO
                        name : genotype,
                        mode : 'lines',
                        line : {
                            color : colourMap[genotype],
                            dash : '5px',
                            width: 1
                        }        
                    };

                    medianTraces[genotype] = medianTrace;

                    // SEM traces

                    var lowerSEMtrace = {
                            x : plotObject.ages,
                            y : newData[genotype]['lower SEM'],
                            type : 'scatter',
                            legendgroup : genotype,
                            connectgaps : true,
                            visible : plotObject.currentPlotOptions.SEM,
                            showlegend : false,
                            name : `lower SEM (${genotype})`,
                            mode : 'lines',
                            line : {
                                color : transparentColourMap[genotype],
                                width : 0
                            }                
                    };

                    SEMtraces.lower[genotype] = lowerSEMtrace;

                    var upperSEMtrace = {
                            x : plotObject.ages,
                            y : newData[genotype]['upper SEM'],
                            type : 'scatter',
                            legendgroup : genotype,
                            connectgaps : true,
                            visible : plotObject.currentPlotOptions.SEM,
                            showlegend : false,
                            name : `upper SEM (${genotype})`,
                            mode : 'none',
                            fill : 'tonexty',
                            fillcolor : transparentColourMap[genotype]
                    };

                    SEMtraces.upper[genotype] = upperSEMtrace;

                    // percentile traces

                    var lowerPercentileTrace = {                        
                        x : plotObject.ages,
                        y : newData[genotype]['2.5%'],
                        type : 'scatter',
                        legendgroup : genotype,
                        connectgaps : true,
                        visible : plotObject.currentPlotOptions.percentiles,
                        showlegend : false,
                        name : `2.5th percentile (${genotype})`,
                        mode : 'lines',
                        line : {
                            color : transparentColourMap[genotype],
                            width : 0
                        }                
                    };

                    percentileTraces.lower[genotype] = lowerPercentileTrace;

                    var upperPercentileTrace = {
                        x : plotObject.ages,
                        y : newData[genotype]['97.5%'],
                        type : 'scatter',
                        legendgroup : genotype,
                        connectgaps : true,
                        visible : plotObject.currentPlotOptions.percentiles,
                        showlegend : false,
                        name : `97.5th percentile (${genotype})`,
                        mode : 'none',
                        fill : 'tonexty',
                        fillcolor : transparentColourMap[genotype]
                    }; 

                    percentileTraces.upper[genotype] = upperPercentileTrace;
                }

                var traceList = [
                    medianTraces.AA, SEMtraces.lower.AA, SEMtraces.upper.AA, percentileTraces.lower.AA, percentileTraces.upper.AA,
                    medianTraces.AB, SEMtraces.lower.AB, SEMtraces.upper.AB, percentileTraces.lower.AB, percentileTraces.upper.AB,
                    medianTraces.BB, SEMtraces.lower.BB, SEMtraces.upper.BB, percentileTraces.lower.BB, percentileTraces.upper.BB
                ];



                this.barChart = {
                    //x: [0, 0, 0],
                    y: genotypes,
                    marker : {color : ['rgba(0, 0, 0, 0.7)', 'rgb(40, 40, 180)', 'rgb(180, 40, 40)']},
                    type: 'bar',
                    orientation : 'h',
                    showlegend : false,
                    hoverinfo : ['x', 'x', 'x'],
                    xaxis: 'x2',
                    yaxis: 'y2'
                };
            //    console.log('traceList[3]]: ' + JSON.stringify(traceList[3]));
            //    traceList.push(this.barChart);
            //    var newTraceList = [];
            //    for (var i = 0; i < 3; i++) {
            //        newTraceList.push(traceList[i]);
            //    }
            //    traceList = newTraceList;                

                this.barChartTitle = {
                    xref: 'paper',
                    yref: 'paper',
                    //xref : 'x2',
                    //yref : 'y2',
                    x : 0.5,
                    y : 0.12,
                    //xanchor: 'middle',
                    //yanchor: 'middle',
                    text: '<b>number of individuals per age</b>',
                    font: {
                            family: 'Arial',
                            size: 14,
                            //color: 'rgb(20, 20, 137)'
                    },
                    showarrow: false
                };
                
                var layout = {
                    title: newData.sex + 's',
                    showlegend: true,
                    traceorder: 'normal',
                    legend: {
                        x: '0',
                        y: '1.18',
                        font: {
                            family: 'Arial',
                            size: 12
                        },
                        'orientation'   : 'h'
                        //'xanchor'       : 'center',
                        //'yanchor'       : 'top'
                    },
                    hovermode: 'closest',
                    margin: {
                        //r: 20,
                        t: 15//,
                        //b: 25,
                        //l: 25
                    },
                    xaxis: {
                        title: 'age',
                        tickvals: plotObject.ages,
                        ticktext: plotObject.ageLabels,
                        //linecolor: 'black',
                        domain: [0, 1],
                        anchor: 'y1'
                      //tickfont: { size: 16 }
                    },
                    yaxis : {
                        title: '',
                        //linecolor: 'black',
                        domain: yAxisDomains['main plot'],
                        anchor: 'x1'
                    },
                    xaxis2: {
                        domain: [0, 1],
                        anchor: 'y2'
                    },
                    yaxis2: {
                        domain: yAxisDomains['number plot'],
                        fixedrange: true,
                        anchor: 'x2',
                        categoryorder : 'category descending'
                    }
                };
                
                if (plotObject.currentPlotOptions.n) { // the bart chart should show
                    traceList.push(this.barChart);
                    this.barChartIndex = traceList.length - 1;
                    layout.annotations = [this.barChartTitle];
                }

                //console.log('traceList: ' + JSON.stringify(traceList));
                for (var i = 0; i < traceList.length; i++) {
                    var traceName = traceList[i].name;
                    //console.log('traceName: ' + JSON.stringify(traceName));
                    if (traceName != null) {
                        if (traceName.includes('SEM')) {
                            this.SEMindices.push(i);
                        }
                        else if (traceName.includes('percentile')) {
                            this.percentileIndices.push(i);
                        }
                        else {
                            this.medianIndices.push(i);
                        }
                    }
                }
                
                // creating the plot
                var configuration = plotObject.commonConfiguration;
                configuration['modeBarButtonsToRemove'] = ['sendDataToCloud'];

                //console.log('traceList before plot: ' + JSON.stringify(traceList));

                Plotly.newPlot(plotObject.gd, traceList, layout, configuration);
                createNewPlot = false;           

                //console.log('numbers: ' + JSON.stringify(numbers));

                // layout            
                slider = [{
                        pad: {t: 10},
                        currentvalue: {
                          xanchor: 'right',
                          prefix: 'age: ',
                          font: {
                            color: '#888',
                            size: 15
                          }
                        },
                        steps: this.createSteps(numbers, this.barChartIndex)
                }];

                //this.maxN = Math.max(...numbers.AA.concat(numbers.AB).concat(numbers.BB)); // find maximum number of individuals
                this.barChart.x = [numbers.AA[0], numbers.AB[0], numbers.BB[0]];
                this.maxN = newData['n max'];//this.getMaxN(numbers.AA.concat(numbers.AB).concat(numbers.BB));
                this.rangeX2 = [0, this.maxN];
                minSEM = newData['SEM min'];
                maxSEM = newData['SEM max'];
                //if (this.minY == null) { //TODO: delete
                //    this.minY = minSEM;
                //}
                if (currentYRange.length == 0) {
                    currentYRange = [plotObject.getMinY(), maxSEM];
                }
                if (currentY2Range.length == 0) {
                    currentY2Range = [0, this.maxN];
                }
                minPercentile = newData['percentile min'];
                maxPercentile = newData['percentile max'];
                if (plotObject.currentPlotOptions.percentiles) {
//                    if (minPercentile < this.minY) { // TODO: delete
//                        this.minY = minPercentile;
//                    }
                    rangeY = [plotObject.getMinY(), maxPercentile];
                }
                else {
//                    if (minSEM < this.minY) { // TOOD: delete
//                        this.minY = minSEM;
//                    }
                    rangeY = [plotObject.getMinY(), maxSEM];
                }
                console.log('this.maxN: ' + this.maxN);
                var sliderUpdate;
                var yAxisDomain;
                if (plotObject.currentPlotOptions.n) {
                    sliderUpdate = slider;
                    yAxisDomain = yAxisDomains['main plot'];
                    // update the bar chart
                    Plotly.restyle(plotObject.divID, 'x', [thisObject.barChart.x], [thisObject.barChartIndex]);
                }
                else {
                    sliderUpdate = [];
                    yAxisDomain = [0, 1];
                }

                var newYaxis = {
                    title: newData['phenotype'],
                    domain: yAxisDomain,
                    range: rangeY
                    };
                var newXaxis2 = {
                    anchor: 'y2',
                    range: this.rangeX2
                    };
                //console.log('steps: ' + JSON.stringify(this.createSteps(numbers)));
                Plotly.relayout(plotObject.divID, 'yaxis', newYaxis);    
                Plotly.relayout(plotObject.divID, 'xaxis2', newXaxis2);
                Plotly.relayout(plotObject.divID, 'sliders', sliderUpdate);
            }
            else {  // update existing plot              
                console.log('updating existing plot');
                
                // update sliders
                Plotly.relayout(plotObject.divID, 'sliders[0].steps', this.createSteps(numbers));
                
                
                
                var medianDataList = [], SEMdataList = [], percentileDataList = [];

                for (var j = 0; j < genotypes.length; j++) {
                    //console.log('newData[genotypes[j]]: ' + JSON.stringify(newData[genotypes[j]]));
                    var medianArray = newData[genotypes[j]]['median'];
                    if (medianArray == null) {
                        medianArray = [];
                    }
                    
                    var lowerSEMarray = newData[genotypes[j]]['lower SEM'];
                    var upperSEMarray = newData[genotypes[j]]['upper SEM'];

                    if (lowerSEMarray == null) {
                        lowerSEMarray = [];
                    }
                    if (upperSEMarray == null) {
                        upperSEMarray = [];
                    }
                    
                    var lowerPercentileArray = newData[genotypes[j]]['2.5%'];

                    if (lowerPercentileArray == null) {
                        lowerPercentileArray = [];
                    }
                    var upperPercentileArray = newData[genotypes[j]]['97.5%'];

                    if (upperPercentileArray == null) {
                        upperPercentileArray = [];
                    }
                    
                    //console.log('line (' + genotypes[j] + ', ' + dataTypes[i] + '): ' + line);
                    medianDataList.push(medianArray);
                    SEMdataList.push(lowerSEMarray, upperSEMarray);
                    percentileDataList.push(lowerPercentileArray, upperPercentileArray);
                }
                
                console.log('medianDataList: ' + JSON.stringify(medianDataList));
                console.log('this.medianIndices: ' + JSON.stringify(this.medianIndices));
                // TODO: confirm that restyle() resets axis type
                Plotly.restyle(plotObject.divID, 'y', medianDataList, this.medianIndices);
                Plotly.restyle(plotObject.divID, 'y', SEMdataList, this.SEMindices);
                Plotly.restyle(plotObject.divID, 'y', percentileDataList, this.percentileIndices);
                //Plotly.relayout(plotObject.divID, 'xaxis.type', 'category'); // does not work
                this.updateAxes();
                
                
                // update bar chart parameters
                var numbers = {AA: plotObject.currentData.AA.N, AB: plotObject.currentData.AB.N, BB: plotObject.currentData.BB.N};
                this.barChart.x = [numbers.AA[0], numbers.AB[0], numbers.BB[0]];
                this.maxN = newData['n max'];
                this.rangeX2 = [0, this.maxN];
                console.log('restyling bar chart');
                console.log('plotObject.currentData.BB.N: ' + JSON.stringify(plotObject.currentData.BB.N));
                console.log('this.barChart.x: ' + JSON.stringify(this.barChart.x));
                if (plotObject.currentPlotOptions['n']) { // update the bar chart if it is showing
                    Plotly.restyle(plotObject.divID, 'x', [this.barChart.x], [this.barChartIndex]);                    
                }
                Plotly.relayout(plotObject.divID, 'yaxis.autorange', true); // trigger axis control; TODO
            }
        }
        
        var plot = document.getElementById(plotObject.divID);
        
        
        /**
         * Catch when the slider is dragged.
         */        
        plot.on('plotly_sliderchange', function(eventData){
            console.log('slider event');	
            console.log('Set age: ' + eventData.step.value);
            var highlightIndex = plotObject.ageIndices[eventData.step.value];
            
            Plotly.Fx.hover(plotObject.divID, // trigger hover event on selected age           
            [
                {curveNumber: thisObject.medianIndices[0], pointNumber: highlightIndex},
                {curveNumber: thisObject.medianIndices[1], pointNumber: highlightIndex},
                {curveNumber: thisObject.medianIndices[2], pointNumber: highlightIndex}
            ]);
        });
    
        /** 
         * Catch the plotly_restyle event.
         * 
          */
        plot.on('plotly_restyle', function(eventData){
            console.log('restyle caught');
            //console.log('plotly_restyle eventData: ' + JSON.stringify(eventData));
            if (eventData[1].length == 1 && eventData[1] == 15) { // slider event
                //console.log('eventData: ' + JSON.stringify(eventData));
                thisObject.barChart.x = eventData[0].x[0];
                //console.log('active: ' + slider[0].active);
            }
        });

        /** 
         * Catch the plotly_relayout event.
         *  
         */
        plot.on('plotly_relayout', function(eventData) {
            //console.log('plotly_relayout eventData: ' + JSON.stringify(eventData));
            //console.log('this.maxN: ' + JSON.stringify(thisObject.maxN));
            // keep track of range changes for the y-axis
            thisObject.trackAxes(eventData);
            
            // don't enter negative x territory
            if (eventData['xaxis.range[0]'] && eventData['xaxis.range[0]'] < 0) {
                Plotly.relayout(plotObject.divID, 'xaxis.range[0]', 0);
            }
            if (eventData['xaxis2.range[0]'] && eventData['xaxis2.range[0]'] < 0) {
                Plotly.relayout(plotObject.divID, 'xaxis2.range[0]', 0);
            }

            // lock the x-axis range for the bar chart        
            if (eventData['xaxis2.autorange']) {
                //console.log('x-axis 2 range locked');
                Plotly.relayout(plotObject.divID, 'xaxis2.autorange', false);
                Plotly.relayout(plotObject.divID, 'xaxis2.range', [0, thisObject.maxN]);
            }
            // don't zoom the bar chart when the modebar is used
            if (eventData['xaxis.range[0]'] && eventData['xaxis2.range[0]']) {
                console.log('x-axis2 range locked');
                //Plotly.relayout(plotObject.divID, 'xaxis2.autorange', false);
                Plotly.relayout(plotObject.divID, 'xaxis2.range', [0, thisObject.maxN]);
            }

            // keep the ranges for the y-axes the same
            if (eventData['yaxis.autorange']) {
                //console.log('y-axis range locked');            
                if (plotObject.currentPlotOptions.percentiles) {
                    Plotly.relayout(plotObject.divID, 'yaxis.autorange', false);
                    Plotly.relayout(plotObject.divID, 'yaxis.range', [plotObject.getMinY(), plotObject.currentData['percentile max']]);
                }
                else {
                    Plotly.relayout(plotObject.divID, 'yaxis.autorange', false);
                    Plotly.relayout(plotObject.divID, 'yaxis.range', [plotObject.getMinY(), plotObject.currentData['SEM max']]);
                }
            }
            
            //console.log('currentYRange: ' + currentYRange);
        });
    };
    
    /** Function that keeps track of the current axis ranges. */
    this.trackAxes = function(eventData) {
        if (eventData['yaxis.range[0]'] != null) {
            //console.log('"yaxis.range[0]" changed');
            currentYRange[0] = eventData['yaxis.range[0]'];
        }
        if (eventData['yaxis.range[1]'] != null) {
            currentYRange[1] = eventData['yaxis.range[1]'];
        }
        if (eventData['yaxis.range'] != null) {
            currentYRange = eventData['yaxis.range'];
        }
        if (eventData['yaxis2.range[0]'] != null) {
            currentY2Range[0] = eventData['yaxis2.range[0]'];
        }
        if (eventData['yaxis2.range[1]'] != null) {
            currentY2Range[1] = eventData['yaxis2.range[1]'];
        }
        if (eventData['yaxis2.range'] != null) {
            currentY2Range = eventData['yaxis2.range'];
        } 
    };
    
    
    /*
     * Function to keep the axes useful as the data changes.
     * 
     */
    this.updateAxes = function() {
        Plotly.relayout(plotObject.divID, 'yaxis.title', plotObject.currentData['phenotype']);  
        if (plotObject.currentPlotOptions.percentiles) {
            console.log('percentiles active');
            Plotly.relayout(plotObject.divID, 'yaxis.autorange', false);
            Plotly.relayout(plotObject.divID, 'yaxis.range', [plotObject.getMinY(), plotObject.currentData['percentile max']]);
            console.log('y-axis range: ' + [plotObject.getMinY(), plotObject.currentData['percentile max']]);
        }
        else {
            Plotly.relayout(plotObject.divID, 'yaxis.autorange', false);
            Plotly.relayout(plotObject.divID, 'yaxis.range', [plotObject.getMinY(), plotObject.currentData['SEM max']]);
        }
    };
    
};
    