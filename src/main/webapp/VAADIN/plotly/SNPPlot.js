var SNPPlot = SNPPlot || {};

SNPPlot.Component = function (element, number) {
    var thisObject = this;
    const style = {
        width : '100%',
        height: '100%'
    };
    plotlyPlot.call(this, element, number, 'SNP', style); // inherit

    const transparentGray = 'rgba(0, 0, 0, 0.1)';
    const transparentBlue = 'rgba(0, 0, 255, 0.1)';
    const transparentRed = 'rgba(255, 0, 0, 0.1)';
    
    
    const transparentColourMap = {
        AA : transparentGray,
        AB : transparentBlue,
        BB : transparentRed};

    const colourMap = {
        AA : 'black',
        AB : 'blue',
        BB : 'red'};
    
    const MEDIANS = 'medians';
    const SEM = 'SEM';
    const PERCENTILES = 'percentiles';
    const N = 'n';
    const AGE_SPACING = 'age spacing';
    const Y_TO_ZERO = 'y to zero';
    
    const plotOptionKeys = [MEDIANS, SEM, PERCENTILES, N, AGE_SPACING, Y_TO_ZERO];
    
    var initialPlotOptions = {};
    initialPlotOptions[MEDIANS] = true;
    initialPlotOptions[SEM] = true;
    initialPlotOptions[PERCENTILES] = false;
    initialPlotOptions[N] = true;
    initialPlotOptions[AGE_SPACING] = 'to scale';
    initialPlotOptions[Y_TO_ZERO] = false;
    
    var plotOptions = {};
    for (var i = 0; i < plotOptionKeys.length; i++) {
        plotOptions[plotOptionKeys[i]] = initialPlotOptions[plotOptionKeys[i]];
    }    
    
    const statisticIndexMap = {
        medians                  : {AA : [], AB : [], BB : []},
        SEM                      : {AA : [], AB : [], BB : []},
        percentiles              : {AA : [], AB : [], BB : []}
    };
    
    const genotypes = ['AA', 'AB', 'BB'];
    const dataTypes = ['median', 'lower SEM', 'upper SEM', '2.5%', '97.5%'];
    var legendStatistic = 'medians';
    
    var yAxisDomains = {
        'main plot': [0.28, 1],
        'number plot': [0, 0.1]
    };
    
    var genotypeIndices = {};
    //var fillingIndices = {SEM : [], percentiles : []};
    
    var genotypeStatuses = {
        AA : true,
        AB : true,
        BB : true 
    };
    
    var oldLegendGroupStatuses = genotypeStatuses;
        
    var traces = {};
    var traceList = [];
    var medianIndices = [];
    var slider;
    var maxN;
    var minSEM, maxSEM;
    var minPercentile, maxPercentile;
    var rangeY;
    var minY;
    var rangeX2;
    var currentYRange = [];
    var currentY2Range = [];
    
    for (var j = 0; j < genotypes.length; j++) {
        for (var i = 0; i < dataTypes.length; i++) {
            var newTrace = {};
            traces['trace ' + dataTypes[i] + ' ' + genotypes[j]] = newTrace;
            traceList.push(newTrace);
        }
    }
    
    var phenotypeIndices = [];
    for (var i = 0; i < traceList.length; i++) {
        phenotypeIndices.push(i);
    }
    
    var index = 0;
    for (var j = 0; j < genotypes.length; j++) {
        var genotype = genotypes[j];
        genotypeIndices[genotype] = [];
        for (var i = 0; i < dataTypes.length; i++) {            
            var dataType = dataTypes[i];    
            var trace = traces['trace ' + dataType + ' ' + genotype]
            trace['x'] = this.ages;
            trace['y'] = [];
            trace['type'] = 'scatter';
            trace['legendgroup'] = genotype;
            //trace['showlegend'] = false;
            genotypeIndices[genotype].push(index);
            if (dataType.substr(-3) == 'SEM' ) {
                trace['visible'] = initialPlotOptions['SEM'];
                statisticIndexMap['SEM'][genotype].push(index);
            }
            else if (dataType == '2.5%' || dataType == '97.5%') {
                trace['visible'] = initialPlotOptions['percentiles'];
                statisticIndexMap['percentiles'][genotype].push(index);
            }
            if (dataType != 'median') {
                trace['showlegend'] = false;
                trace['name'] = dataType + ' (' + genotype + ')';
                
                if (dataType.substring(0, 'lower'.length) === 'lower' || dataType == '2.5%') {
                    trace['mode'] = 'lines';
                    trace['line'] = {
                        color : transparentColourMap[genotype],
                        width : 0};
                }
                else {
                    trace['mode'] = 'none';
                    trace['fill'] = 'tonexty';
                    trace['fillcolor'] = transparentColourMap[genotype];
                }
            }
            else {
                statisticIndexMap['medians'][genotype].push(index);
                medianIndices.push(index);
                trace['visible'] = initialPlotOptions['medians'];
                trace['mode'] = 'lines';
                trace['name'] = genotype;
                trace['line'] = {
                    color : colourMap[genotype],
                    dash: '5px',
                    width: 1};
            }
            index += 1;
        }
    }
    
    var barChart = {
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
//    traceList.push(barChart);
//    var newTraceList = [];
//    for (var i = 0; i < 3; i++) {
//        newTraceList.push(traceList[i]);
//    }
//    traceList = newTraceList;
    traceList.push(barChart);
    var barChartIndex = traceList.length - 1;
    
    var barChartTitle = {
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
    
    //console.log('traceList:' + JSON.stringify(traceList) + ', length:' + traceList.length);

    this.createSteps = function(numbers) {
        var steps = [];

        for (var i = 0; i < this.ages.length; i++) {
            steps.push({
            label: this.ageLabels[i],
            method: 'restyle',
            args: ['x', [[numbers.AA[i], numbers.AB[i], numbers.BB[i]]], [barChartIndex]]
            });
        }
        
        return steps;
    };

    var defaultLayout = {
        //title: '',
        showlegend: true,
        traceorder: 'normal',
        legend: {
            x: '0',
            y: '1.18',
            font: {
		family: 'Arial',
		size: 12
            },
            'orientation'   : 'v'
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
            tickvals: this.ages,
            ticktext: this.ageLabels,
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
        annotations : [barChartTitle],
        xaxis2: {
            domain: [0, 1],
            anchor: 'y2'
        },
        yaxis2: {
            domain: yAxisDomains['number plot'],
            fixedrange: true,
            anchor: 'x2'//,
//            categoryorder : 'category descending'
        }
    };

    for (var i = 0; i < traceList.length; i++) {
        //console.log(JSON.stringify(traceList[i]));
    }
    
    this.showTracesOfGenotype = function(genotype, showGenotype) {
        console.log(genotype + ": " + showGenotype);
        var hideIndices = [];
        var showIndices = [];
        for (var i = 0; i < plotOptionKeys.length; i++) {
            var statistic = plotOptionKeys[i];
            if (showGenotype && initialPlotOptions[statistic]) {
                    showIndices = showIndices.concat(statisticIndexMap[statistic][genotype]); 
                }
            else {
                    hideIndices = hideIndices.concat(statisticIndexMap[statistic][genotype]);
                }
        }
        if (showIndices.length > 0) {
            Plotly.restyle(this.divID, {visible : 'true'}, showIndices);
        }
        if (hideIndices.length > 0) {
            Plotly.restyle(this.divID, {visible : 'legendonly'}, hideIndices);
        }        
    };
    
    this.legendSwap = function() {
        var fillStatisticName = '';
        var newFillTraceStyle;
        var oldLegendStatistic = legendStatistic;
        if (!initialPlotOptions[legendStatistic] || initialPlotOptions['medians']) {        
            if (!initialPlotOptions['medians']) {
                for (var i = 1; i < plotOptionKeys.length; i++) {            
                    var fillStatistic = plotOptionKeys[i];
                    if (initialPlotOptions[fillStatistic]) { // find a filled area that is showing
                        fillStatisticName = dataTypes[2*i];
                        legendStatistic = fillStatistic;
                        for (var g = 0; g < 3; g++) {
                            newFillTraceStyle = {name : genotypes[g], showlegend : true};
                            Plotly.restyle(this.divID, newFillTraceStyle, statisticIndexMap[fillStatistic][genotypes[g]][1]);
                        }
                        break
                    }
                    if (oldLegendStatistic != 'medians') {
                        for (var g = 0; g < 3; g++) {
                            newFillTraceStyle = {name : fillStatisticName + ' (' + genotypes[g] + ')', showlegend : false};
                            Plotly.restyle(this.divID, newFillTraceStyle, statisticIndexMap[oldLegendStatistic][genotypes[g]][1]);
                        }
                    }
                }
            }
            else {
                fillStatistic = legendStatistic;
                legendStatistic = 'medians';
                for (var i = 1; i < plotOptionKeys.length; i++) {
                    for (var g = 0; g < 3; g++) {
                        newFillTraceStyle = {name : 'upper ' + fillStatistic + ' (' + genotypes[g] + ')', showlegend : false};
                        Plotly.restyle(this.divID, newFillTraceStyle, statisticIndexMap[fillStatistic][genotypes[g]][1]);
                    }                     
                }  
            }
            Plotly.restyle(this.divID, {showlegend : initialPlotOptions['medians']}, medianIndices);
        };
    };
    
    this.isGraphEmpty = function () {
        var someStatisticShows = false;
        var i = 0
        while (!someStatisticShows && i < plotOptionKeys.length) {
                someStatisticShows = initialPlotOptions[plotOptionKeys[i]];
                i++;
            }
        return !someStatisticShows;  
    };
    
    this.setPlotOptions = function (newPlotOptions) {
        console.log('newPlotOptions: ' + JSON.stringify(newPlotOptions));
        //console.log('plotOptions: ' + JSON.stringify(plotOptions));
        
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
                            console.log('currentYRange[0]: ' + currentYRange[0] + ', minY: ' + minY);
                            var change = false;
                            //console.log('currentYRange: ' + currentYRange);
                            //console.log('min SEM: ' + minSEM + ', maxSEM: ' + maxSEM);
                            if (newPlotOptions[PERCENTILES]) {
                                if (currentYRange[0] == minY && currentYRange[1] == maxSEM) {
                                    change = true;
                                    if (minY > 0) {
                                        minY = minPercentile;
                                    }
                                    rangeY = [minY, maxPercentile];
                                }                                    
                            }
                            else {
                                if (currentYRange[0] == minY && currentYRange[1] == maxPercentile) {
                                    change = true;
                                    if (minY > 0) {
                                        minY = minSEM;
                                    }
                                    rangeY = [minY, maxSEM];
                                }
                            }
                            if (change) {
                                //console.log('locking axes');
                                Plotly.relayout(thisObject.divID, 'yaxis.autorange', false);
                                Plotly.relayout(thisObject.divID, 'yaxis.range', rangeY);
                            }                            
                        }
                        Plotly.restyle(this.divID, {visible : newPlotOptions[option]}, changedIndices)
                    }
                    else if (option == N) {
                        //console.log('slider: ' + JSON.stringify(slider)); 
                        if (newPlotOptions[N]) {
                            var newLayout = {
                                yaxis: {
                                    domain: yAxisDomains['main plot']
                                },
                                sliders: slider,
                                annotations: [barChartTitle]
                            };

                            Plotly.addTraces(this.divID, barChart);
                            Plotly.relayout(this.divID, newLayout);
                        }
                        else {
                            var newLayout = {
                                yaxis: {
                                    domain: [0, 1]
                                },
                                sliders: [],
                                annotations: []
                            };
                            Plotly.deleteTraces(this.divID, barChartIndex);
                            Plotly.relayout(this.divID, newLayout);                            
                        }
                    }
                    else if (option == AGE_SPACING) {
                        if (newPlotOptions[AGE_SPACING] == 'to scale') {
                            //Plotly.relayout(this.divID, 'xaxis.tickvals', this.ages);
                            Plotly.relayout(this.divID, 'xaxis.type', 'linear');
                        }
                        else {
                            //Plotly.relayout(this.divID, 'xaxis.tickvals', []);
                            Plotly.relayout(this.divID, 'xaxis.type', 'category');
                        }
                    }
                    else if (option == Y_TO_ZERO) {
                        if (newPlotOptions[Y_TO_ZERO]) {
                            minY = 0;
                        }
                        else if (plotOptions[PERCENTILES]) {
                            minY = minPercentile;
                        }
                        else {
                            minY = minSEM;
                        } 
                        Plotly.relayout(this.divID, 'yaxis.range[0]', minY);
                    }
                }
            }
        }
    };
    
    
        
//        var changes = false;
//        var medianChanged = showStatus['medians'] != initialShowStatus['medians'];
//        if (showStatus != null){
//            console.log('showStatus: ' + JSON.stringify(showStatus));
//            for (var i = 0; i < showStatusKeys.length; i++) {
//                var statistic = showStatusKeys[i];
//                if (initialShowStatus[statistic] != showStatus[statistic]) {
//                    changes = true;
//                    console.log('Changed show status for ' + showStatusKeys[i] + ': ' + showStatus[statistic]);
//                    initialShowStatus[statistic] = showStatus[statistic];
//                };
//            };
//            if (changes) {
//                for (var g = 0; g < 3; g++) {
//                    var genotype = genotypes[g];
//                    thisObject.showTracesOfGenotype(genotype, genotypeStatuses[genotype]);
//                }
//            }
//        };
//        if (medianChanged || currentShowStatus['medians'] == false) {
//            if (!this.isGraphEmpty() && !(legendStatistic == 'medians' && currentShowStatus['medians'])) {
//                console.log('swapping');
//                this.legendSwap();
//            } 
//        }
        //console.log('current1: ' + JSON.stringify(currentShowStatus));
    
    // get the maximum numerical value of an array
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

    this.updateData = function (newData) {
        if (newData != null){
            //console.log('newData: ' + JSON.stringify(newData));
            var AAData = newData['AA'];
            var ABData = newData['AB'];
            var BBData = newData['BB'];            
            
            var dataList = [];            
            //console.log('statisticIndexMap: ' + JSON.stringify(statisticIndexMap));
            for (var j = 0; j < genotypes.length; j++) {
                for (var i = 0; i < dataTypes.length; i++) {
                    var line = newData[genotypes[j]][dataTypes[i]];
                    if (line == null) {
                        line = [];
                    }
                    //console.log('line (' + genotypes[j] + ', ' + dataTypes[i] + '): ' + line);
                    dataList.push(line);
                }
            }
            //console.log(dataList);
            Plotly.restyle(this.divID, 'y', dataList, phenotypeIndices);
            var numbers = {AA: newData.AA.N, AB: newData.AB.N, BB: newData.BB.N};
            
            // legend 
            Plotly.restyle(this.divID, 'name', AAData['labels'][0], statisticIndexMap['medians']['AA'][0]);
            Plotly.restyle(this.divID, 'name', ABData['labels'][0], statisticIndexMap['medians']['AB'][0]);
            Plotly.restyle(this.divID, 'name', BBData['labels'][0], statisticIndexMap['medians']['BB'][0]);
            
            //console.log('index map: ' + JSON.stringify(statisticIndexMap));
            
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
                    steps: this.createSteps(numbers)
            }];
        
            //var maxN = Math.max(...numbers.AA.concat(numbers.AB).concat(numbers.BB)); // find maximum number of individuals
            barChart.x = [numbers.AA[0], numbers.AB[0], numbers.BB[0]];
            maxN = newData['n max'];//this.getMaxN(numbers.AA.concat(numbers.AB).concat(numbers.BB));
            rangeX2 = [0, maxN];
            minSEM = newData['SEM min'];
            maxSEM = newData['SEM max'];
            if (minY == null) {
                minY = minSEM;
            }
            if (currentYRange.length == 0) {
                currentYRange = [minY, maxSEM];
            }
            if (currentY2Range.length == 0) {
                currentY2Range = [0, maxN];
            }
            minPercentile = newData['percentile min'];
            maxPercentile = newData['percentile max'];
            if (plotOptions[PERCENTILES]) {
                if (minPercentile < minY) {
                    minY = minPercentile;
                }
                rangeY = [minY, maxPercentile];
            }
            else {
                if (minSEM < minY) {
                    minY = minSEM;
                }
                rangeY = [minY, maxSEM];
            }
            console.log('maxN: ' + maxN);
            var sliderUpdate;
            var yAxisDomain;
            if (plotOptions['n']) {
                sliderUpdate = slider;
                yAxisDomain = yAxisDomains['main plot'];
                // update the bar chart
                Plotly.restyle(this.divID, 'x', [barChart.x], [barChartIndex]);
            }
            else {
                sliderUpdate = [];
                yAxisDomain = [0, 1];
            }
            
            var newLayout = {
                title : newData['sex'] + 's',
                yaxis: {
                    title: newData['phenotype'],
                    domain: yAxisDomain,
                    range: rangeY
                },
                //xaxis : {type: 'category'},
                xaxis2: {
                    anchor: 'y2',
                    range: rangeX2
                },
                sliders: sliderUpdate
            };
            //console.log('steps: ' + JSON.stringify(this.createSteps(numbers)));
            Plotly.relayout(this.divID, newLayout);
        }
    };
    // creating the plot
    var configuration = this.commonConfiguration;
    configuration['modeBarButtonsToRemove'] = ['sendDataToCloud'];
    
    Plotly.newPlot(this.gd, traceList, defaultLayout, configuration);
    
    this.traceIsVisible = function(status) {
        return !(status == false || status == 'legendonly');
    }
    
    this.getLegendGroupsThatChangedVisibility = function() {
        var changed = []
        for (var g = 0; g < 3; g++) {
            var genotype = genotypes[g];
            if (oldLegendGroupStatuses[genotype] != genotypeStatuses[genotype]) {
                changed.push(genotype);
            }
        }
        return changed;
    }
    
    
    this.updateLegendGroupStatuses = function() {
        oldLegendGroupStatuses = {}
        for (var g = 0; g < 3; g++) { // copy over the old statues
            genotype = genotypes[g];
            oldLegendGroupStatuses[genotype] = genotypeStatuses[genotype];
        }
        for (var g = 0; g < 3; g++) {
            genotype = genotypes[g];
                
            var allHaveSameStatus = true;
            var i = 0;
            while (allHaveSameStatus && i < genotypeIndices['AA'].length - 1) {
               if (thisObject.traceIsVisible(traceList[genotypeIndices[genotype][i]]['visible']) !=
                       thisObject.traceIsVisible(traceList[genotypeIndices[genotype][i+1]]['visible'])) {
                   allHaveSameStatus = false;
               }
               i++;
            }
            if (allHaveSameStatus) {
                genotypeStatuses[genotype] = thisObject.traceIsVisible(traceList[genotypeIndices[genotype][0]]['visible']);
            }
            else { // at least some are showing
                genotypeStatuses[genotype] = true;
            }            
        }
        //console.log("genotype statuses: " + JSON.stringify(genotypeStatuses));
    };
    
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
    
    var plot = document.getElementById(this.divID);
    
    plot.on('plotly_restyle', function(eventData){
        //console.log('plotly_restyle eventData: ' + JSON.stringify(eventData));
        if (eventData[1].length == 1 && eventData[1] == 15) { // slider event
            //console.log('eventData: ' + JSON.stringify(eventData));
            barChart.x = eventData[0].x[0];
            //console.log('active: ' + slider[0].active);
        }
    });
    
    plot.on('plotly_relayout', function(eventData){
        //console.log('plotly_relayout eventData: ' + JSON.stringify(eventData));
        // keep track of range changes for the y-axis
        thisObject.trackAxes(eventData);
        
        // lock the x-axis range for the bar chart        
        if (eventData['xaxis2.autorange']) {
            //console.log('x-axis 2 range locked');
            Plotly.relayout(thisObject.divID, 'xaxis2.autorange', false);
            Plotly.relayout(thisObject.divID, 'xaxis2.range', [0, maxN]);
        }
        // don't zoom the bar chart when the modebar is used
        if (eventData['xaxis.range[0]'] && eventData['xaxis2.range[0]']) {
            //console.log('x-axis 2 range locked');
            //Plotly.relayout(thisObject.divID, 'xaxis2.autorange', false);
            Plotly.relayout(thisObject.divID, 'xaxis2.range', [0, maxN]);
        }
        
        
        // keep the ranges for the y-axes the same
        if (eventData['yaxis.autorange']) {
            //console.log('y-axis range locked');            
            if (plotOptions[PERCENTILES]) {
                Plotly.relayout(thisObject.divID, 'yaxis.autorange', false);
                Plotly.relayout(thisObject.divID, 'yaxis.range', [minY, maxPercentile]);
            }
            else {
                Plotly.relayout(thisObject.divID, 'yaxis.autorange', false);
                Plotly.relayout(thisObject.divID, 'yaxis.range', [minY, maxSEM]);
            }
        }
        // don't enter negative x territory
        if (eventData['xaxis.range[0]'] && eventData['xaxis.range[0]'] < 0) {
            Plotly.relayout(thisObject.divID, 'xaxis.range[0]', 0);
        }
        if (eventData['xaxis2.range[0]'] && eventData['xaxis2.range[0]'] < 0) {
            Plotly.relayout(thisObject.divID, 'xaxis2.range[0]', 0);
        }
        //console.log('currentYRange: ' + currentYRange);
    });

//    plot.on('plotly_restyle', function(eventData){ // register legend clicks
//        if (eventData[0]['visible'] != null && !thisObject.isGraphEmpty()) {
//            console.log("Restyle: " + JSON.stringify(eventData));
//            thisObject.updateLegendGroupStatuses();
//            var changedGenotypes = thisObject.getLegendGroupsThatChangedVisibility();
//            if (changedGenotypes.length > 0 || eventData[0]['visible'].length == traceList.length) {
//                var changeGenotypes;
//                if (changedGenotypes.length == 2 || eventData[0]['visible'].length == traceList.length) { // double click on one legend
//                    changeGenotypes = ['AA', 'AB', 'BB']
//                }
//                else {
//                    changeGenotypes = changedGenotypes;
//                }
//                console.log('changed: ' + changedGenotypes);
//                for (var i = 0; i < changeGenotypes.length; i++) {
//                    var genotype = changeGenotypes[i];
//                    thisObject.showTracesOfGenotype(genotype, genotypeStatuses[genotype]);
//                }
//                
//            }
//        }        
//    }); 
};