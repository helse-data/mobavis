var SNPPlot = SNPPlot || {};

SNPPlot.Component = function (element, number) {
    var thisObject = this;
    const style = {
        width : '41vw',
        height: '63vh'
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
    
    const showStatusKeys = ['medians', 'SEM', 'percentiles'];
    
    var initialShowStatus = {
        medians                 : true,
        SEM                     : true,
        percentiles             : false
    };
    
    const statisticIndexMap = {
       medians                  : {AA : [], AB : [], BB : []},
       SEM                      : {AA : [], AB : [], BB : []},
       percentiles              : {AA : [], AB : [], BB : []}
   };

    
    const genotypes = ['AA', 'AB', 'BB'];
    const dataTypes = ['median', 'lower SEM', 'upper SEM', '2.5%', '97.5%'];
    var legendStatistic = 'medians';
    
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
    
    for (var j = 0; j < genotypes.length; j++) {
        for (var i = 0; i < dataTypes.length; i++) {
            var newTrace = {};
            traces['trace ' + dataTypes[i] + ' ' + genotypes[j]] = newTrace;
            traceList.push(newTrace);
        }
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
                trace['visible'] = initialShowStatus['SEM'];
                statisticIndexMap['SEM'][genotype].push(index);
            }
            else if (dataType == '2.5%' || dataType == '97.5%') {
                trace['visible'] = initialShowStatus['percentiles'];
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
                trace['visible'] = initialShowStatus['medians'];
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

    var layout = {
        //title: '',
        showlegend: true,
        traceorder: 'normal',
        legend: {
            x: '0',
            y: '1.26',
            font: {
		family: 'Arial',
		size: 12
            },
            'orientation'   : 'v',
            //'xanchor'       : 'center',
            //'yanchor'       : 'top'
        },
        hovermode:'closest',
        xaxis: {
            title: 'age',
            tickvals: this.ages,
            ticktext: this.ageLabels,
            linecolor: 'black'
          //tickfont: { size: 16 }
        },
        yaxis : {
            title: '',
            linecolor: 'black'
        }
    };
    
    //for (var i = 0; i < traceList.length; i++) {
    //    console.log(JSON.stringify(traceList[i]));
    //}
    
    this.showTracesOfGenotype = function(genotype, showGenotype) {
        console.log(genotype + ": " + showGenotype);
        var hideIndices = [];
        var showIndices = [];
        for (var i = 0; i < showStatusKeys.length; i++) {
            var statistic = showStatusKeys[i];
            if (showGenotype && initialShowStatus[statistic]) {
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
        if (!initialShowStatus[legendStatistic] || initialShowStatus['medians']) {        
            if (!initialShowStatus['medians']) {
                for (var i = 1; i < showStatusKeys.length; i++) {            
                    var fillStatistic = showStatusKeys[i];
                    if (initialShowStatus[fillStatistic]) { // find a filled area that is showing
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
                for (var i = 1; i < showStatusKeys.length; i++) {
                    for (var g = 0; g < 3; g++) {
                        newFillTraceStyle = {name : 'upper ' + fillStatistic + ' (' + genotypes[g] + ')', showlegend : false};
                        Plotly.restyle(this.divID, newFillTraceStyle, statisticIndexMap[fillStatistic][genotypes[g]][1]);
                    }                     
                }  
            }
            Plotly.restyle(this.divID, {showlegend : initialShowStatus['medians']}, medianIndices);
        };
    };
    
    this.isGraphEmpty = function () {
        var someStatisticShows = false;
        var i = 0
        while (!someStatisticShows && i < showStatusKeys.length) {
                someStatisticShows = initialShowStatus[showStatusKeys[i]];
                i++;
            }
        return !someStatisticShows;  
    };
    
    this.setShowStatus = function (newShowStatus) {
        //console.log(JSON.stringify(showStatus));
        //console.log('current0: ' + JSON.stringify(currentShowStatus));
        
        var changeIndices = [];
        
        if (newShowStatus != null){
            for (var i = 0; i < showStatusKeys.length; i++) {
                var statistic = showStatusKeys[i];
                if (newShowStatus[statistic] != null) {
                    console.log('change statistic: ' + statistic);
                    for (var i = 0; i < 3; i++) {
                        changeIndices = changeIndices.concat(statisticIndexMap[statistic][genotypes[i]]); 
                    }
                    console.log('changeIndices: ' + changeIndices);
                    Plotly.restyle(this.divID, {visible : newShowStatus[statistic]}, changeIndices)
                }
            }            
        }
        
        
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
    };

    this.updateData = function (newData) {         
        if (newData != null){
            //console.log('newData: ' + JSON.stringify(newData));
            var AAData = newData['AA'];
            var ABData = newData['AB'];
            var BBData = newData['BB'];            
            
            var dataList = [];            
            console.log('statisticIndexMap: ' + JSON.stringify(statisticIndexMap));
            for (var j = 0; j < genotypes.length; j++) {
                for (var i = 0; i < dataTypes.length; i++) {
                    var line = newData[genotypes[j]][dataTypes[i]];
                    if (line == null) {
                        line = [];
                    }
                    console.log('line (' + genotypes[j] + ', ' + dataTypes[i] + '): ' + line);
                    dataList.push(line);
                }
            }
            //console.log(dataList);
            Plotly.restyle(this.divID, 'y', dataList);
            
            // legend 
            Plotly.restyle(this.divID, 'name', AAData['labels'][0], statisticIndexMap['medians']['AA'][0]);
            Plotly.restyle(this.divID, 'name', ABData['labels'][0], statisticIndexMap['medians']['AB'][0]);
            Plotly.restyle(this.divID, 'name', BBData['labels'][0], statisticIndexMap['medians']['BB'][0]);
            
            // layout
            var newLayout = {
                title: newData['sex'] + 's',
                yaxis : {
                    title: newData['phenotype']
                }
            };
            Plotly.relayout(this.divID, newLayout);
            
        }
    };
    // creating the plot
    var configuration = this.commonConfiguration;
    configuration['modeBarButtonsToRemove'] = ['sendDataToCloud'];
    Plotly.newPlot(this.gd, traceList, layout, configuration);
 
    
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
                
            var allHasSameStatus = true;
            var i = 0;
            while (allHasSameStatus && i < genotypeIndices['AA'].length - 1) {
               if (thisObject.traceIsVisible(traceList[genotypeIndices[genotype][i]]['visible']) !=
                       thisObject.traceIsVisible(traceList[genotypeIndices[genotype][i+1]]['visible'])) {
                   allHasSameStatus = false;
               }
               i++;
            }
            if (allHasSameStatus) {
                genotypeStatuses[genotype] = thisObject.traceIsVisible(traceList[genotypeIndices[genotype][0]]['visible']);
            }
            else { // at least some are showing
                genotypeStatuses[genotype] = true;
            }            
        }
        //console.log("genotype statuses: " + JSON.stringify(genotypeStatuses));
    };
        
    var plot = document.getElementById(this.divID);
    
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