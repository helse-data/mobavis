var SNP3DPlot = SNP3DPlot || {};

/**
 * 
 * Constructor of the 3D SNP plot.
 * 
 * @constructor
 * 
 * @param {object} plotObject - the underlying object of the plot
 */
SNP3DPlot.Object = function (plotObject) { 
    var createNewPlot = true;
    
    const dataTypes = plotObject.dataTypes;
    const genotypes = plotObject.genotypes;
    
    var medianColours = {'AA' : 'black', 'AB' : 'blue', 'BB' : 'red'};
    
    var colourScales = {
        'AA' : [[0, "rgb(0, 0, 0)"], [1, "rgb(0, 0, 0)"]],
        'AB' : [[0, "rgb(0, 0, 255)"], [1, "rgb(0, 0, 255)"]],
        'BB' : [[0, "rgb(255, 0, 0)"], [1, "rgb(255, 0, 0)"]]
    };
    
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
    this.setData = function (newData) {
        //console.log('newData: ' + JSON.stringify(newData));
        console.log('Setting data for 3D plot');
        
        if (createNewPlot) {
            console.log('Creating new 3D plot.');
            var x = plotObject.ages;
            var xLabels = plotObject.ageLabels;

            var traces = {};
            for (var i = 0; i < 3; i++) {
                var genotype = genotypes[i];
                traces[genotype] = {};
                for (var j = 0; j < dataTypes.length; j++) {
                    traces[genotype][dataTypes[j]] = {};
                }
                
            }
            console.log(1);
            // create the surfaces corresponding to SEM and percentiles            
            var surfaceColour = [];
            for (var i = 0; i < x.length; i++) {
                surfaceColour.push([9, 10]);
            }
            console.log(2);
            var surfaces = this.createSurfaces(newData);
            console.log(2.1);
            var SEMyData = this.convertSEMyData(newData);
            console.log(2.2);
            var percentileData = this.convertPercentileYdata(newData);
            console.log(2.3);
            var medianTexts = this.createMedianTexts(newData);
            console.log(2.4);
            var SEMtexts = this.createSEMtexts(newData);
            console.log(2.5);
            var percentileTexts = this.createPercentileTexts(newData);
            
            //console.log('percentileTexts: ' + JSON.stringify(percentileTexts));
            var medianTraces = {};
            console.log(3);
            for (var i = 0; i < genotypes.length; i++) {
                var genotype = genotypes[i];
                var medianTrace = {
                    type: 'scatter3d',
                    hoverinfo: 'text',
                    mode: 'lines+markers',
                    name: genotype,
                    visible: plotObject.currentPlotOptions.medians,
                    legendgroup: genotype,
                    x: x,
                    y: newData[genotype]['median'],
                    z: newData[genotype]['N'],
                    text: medianTexts[genotype],
                    line: {
                            width: 6,
                            color: medianColours[genotype]
                    },
                    //color: c,
                    //colorscale: "Viridis"},
                        marker: {
                                size: 3.5//,
                    //color: c,
                    //colorscale: "Greens",
                    //cmin: -20,
                    //cmax: 50
                  }
                };
                medianTraces[genotype] = medianTrace;
            }
            
            // SEMs

            var surfaceColour = [];
            for (var i = 0; i < x.length; i++) {
                surfaceColour.push([9, 10]);
            }

            var SEMtraces = {};
            for (var i = 0; i < genotypes.length; i++) {
                var genotype = genotypes[i];
                var SEMtrace = {
                    type: 'surface',
                    hoverinfo: 'text',
                    name: genotype + ' SEM',
                    legendgroup: genotype,
                    visible: plotObject.currentPlotOptions.SEM,
                    x: surfaces[genotype]['x'],
                    y: SEMyData[genotype],
                    z: surfaces[genotype]['z'],
                    text: SEMtexts[genotype],
                    surfacecolor: surfaceColour,
                    colorscale: colourScales[genotype],
                    opacity: 0.5,
                    showscale: false
                };
                SEMtraces[genotype] = SEMtrace;
            }
            //console.log('SEMtexts: ' + JSON.stringify(SEMtexts));
            //console.log('SEMtraces: ' + JSON.stringify(SEMtraces));
            
            //console.log('percentileData: ' + JSON.stringify(percentileData));
            //console.log('percentileTexts: ' + JSON.stringify(percentileTexts));
            
            var percentileTraces = {};
            
            for (var i = 0; i < genotypes.length; i++) {
                var genotype = genotypes[i];                
                var percentileTrace = {
                    type: 'surface',
                    hoverinfo: 'text',
                    name: genotype + ' percentiles',
                    legendgroup: genotype,
                    visible: plotObject.currentPlotOptions.percentiles, 
                    x: surfaces[genotype]['x'],
                    y: percentileData[genotype],
                    z: surfaces[genotype]['z'],
                    text: percentileTexts[genotype],
                    surfacecolor: surfaceColour,
                    colorscale: colourScales[genotype],
                    opacity: 0.5,
                    showscale: false
                };
                percentileTraces[genotype] = percentileTrace;
            }
           
            console.log('plotObject.currentOptions: ' + JSON.stringify(plotObject.currentPlotOptions));

            var layout = {
                title: newData['sex'] + 's',
                scene: {
                        xaxis: {
                                title : 'age',
                                tickvals: x,
                                ticktext: xLabels,
                                type : plotObject.ageSpacingMap[plotObject.currentPlotOptions['age spacing']]
                                },
                        yaxis: {title : newData['phenotype']},
                        zaxis: {
                                title : 'number of individuals'},
                                //range: [0, 100]}, TODO
                        camera: {
                  center: {x: 0, y: 0, z: 0}, 
                  eye: 
                          //{x: 0, y: 0, z: 1.25},
                          {x:1.966, y:-0.932, z:1.240}, 
                  up: {x: 0, y: 0, z: 1}
                        }
                }
            };
            
            //console.log('trace1: ' +  JSON.stringify(trace1));
            //console.log('SEM1: ' + JSON.stringify(SEM1));
            
            var configuration = plotObject.commonConfiguration;
            var traceList = [medianTraces.AA, SEMtraces.AA, percentileTraces.AA, medianTraces.AB, percentileTraces.AB, SEMtraces.AB, medianTraces.BB, SEMtraces.BB, percentileTraces.BB];
            
            this.medianIndices = [], this.SEMindices = [], this.percentileIndices = [];
            
            for (var i = 0; i < traceList.length; i++) {
                var traceName = traceList[i].name;
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
            
            //console.log('this.medianIndices: ' + JSON.stringify(this.medianIndices));
            //console.log('this.SEMindices: ' + JSON.stringify(this.SEMindices));
            //console.log('this.percentileIndices: ' + JSON.stringify(this.percentileIndices));
            
            var phenotypeIndices = [];
            for (var i = 0; i < traceList.length; i++) {
                phenotypeIndices.push(i);
            }

            Plotly.newPlot(plotObject.gd, traceList, layout, configuration);
            plotObject.resize();

            var plot = document.getElementById(plotObject.divID);

            plot.on('plotly_legendclick', function(data){
                var eventData = data.eventData;
                    for (var property in eventData) {
                            if (eventData.hasOwnProperty(property)) {
                                console.log(JSON.stringify(eventData[property]));
                            }
                    }
                    console.log('eventData: ' + eventData);
            });
        createNewPlot = false;
        }
        else {
            var medianDataList = [], SEMdataList = [], percentileDataList = [];
            var newSEMdata = this.convertSEMyData(newData);
            var newPercentileData = this.convertPercentileYdata(newData);
            for (var j = 0; j < genotypes.length; j++) {
                var medianArray = newData[genotypes[j]]['median'];
                if (medianArray == null) {
                    medianArray = [];
                }
                var SEMarray = newSEMdata[genotypes[j]];
                if (SEMarray == null) {
                    SEMarray = [];
                }
                var percentileArray = newPercentileData[genotypes[j]];
                if (percentileArray == null) {
                    percentileArray = [];
                }
                //console.log('line (' + genotypes[j] + ', ' + dataTypes[i] + '): ' + line);
                medianDataList.push(medianArray);
                SEMdataList.push(SEMarray);
                percentileDataList.push(percentileArray);
            }
            //console.log('SEMyDataList: ' + JSON.stringify(SEMyDataList));
            //console.log('this.SEMindices: ' + JSON.stringify(this.SEMindices));

            // update the y data
            Plotly.restyle(plotObject.divID, 'y', medianDataList, this.medianIndices);
            Plotly.restyle(plotObject.divID, 'y', SEMdataList, this.SEMindices);
            Plotly.restyle(plotObject.divID, 'y', percentileDataList, this.percentileIndices);
            
            // update the texts
            var newMedianTextsObject =  this.createMedianTexts(newData);
            var newMedianTexts = [];
            var newSEMtextsObject =  this.createSEMtexts(newData);
            var newSEMtexts = [];
            var newPercentileTextsObject =  this.createPercentileTexts(newData);
            var newPercentileTexts = [];
            for (var i = 0; i < genotypes.length; i++) {
                var genotype = genotypes[i];
                newMedianTexts.push(newMedianTextsObject[genotype]);
                newSEMtexts.push(newSEMtextsObject[genotype]);
                newPercentileTexts.push(newPercentileTextsObject[genotype]);                
            }
            
            //console.log('this.createPercentileTexts(newData): ' + JSON.stringify(this.createPercentileTexts(newData)));
            Plotly.restyle(plotObject.divID, 'text', newMedianTexts, this.medianIndices);
            Plotly.restyle(plotObject.divID, 'text', newSEMtexts, this.SEMindices);
            Plotly.restyle(plotObject.divID, 'text', newPercentileTexts, this.percentileIndices);
            
            // update the title
            Plotly.relayout(plotObject.divID, 'scene.yaxis.title', newData['phenotype']);
            
            
        }
    };
    
    /*
     * Function for converting the SEM y-axis data into a 3D compatible format.
     * 
     * @param {object} data - the data to convert
     * @return {object}
     */
    this.convertSEMyData = function(data) {
        var SEMYData = {};
        for (var i = 0; i < genotypes.length; i++) {
            var genotype = genotypes[i];
            SEMYData[genotype] = [];
            if (data[genotype]['lower SEM'] != null) {
                for (var j = 0; j < data[genotype]['lower SEM'].length; j++) {
                    SEMYData[genotype].push([data[genotype]['lower SEM'][j], data[genotype]['upper SEM'][j]]);
                }
            }
        }
        return SEMYData;
    };
    
    this.createMedianTexts = function (data) {
        var medianTexts = {};
        //var axes = ['x', 'y', 'z'];
        for (var i = 0; i < 3; i++) {
            var genotype = genotypes[i];
            if (data[genotype]['median'] != null) {                
                medianTexts[genotype] = plotObject.ages.map((xj, j) =>
            `
Median (genotype ${genotype})<br>
age: ${plotObject.ageLabels[j]}<br>
${data['phenotype']}: ${data[genotype]['median'][j]}<br>
individuals: ${data[genotype]['N'][j]}
`);
            }
            else {
                medianTexts[genotype] = [];
            }
        }
        return medianTexts;
    };
    
    this.createSEMtexts = function (data) {
        var SEMyData = this.convertSEMyData(data);
        var SEMtexts = {};
            
        for (var i = 0; i < genotypes.length; i++) {
            var genotype = genotypes[i];
            var SEMtext = [];
            if (SEMyData[genotype]['2.5%'] != null && SEMyData[genotype]['97.5%'] != null) {
                for (var j = 0; j < plotObject.ages.length; j++) {
                    var xArray = [];
                    xArray.push(`
SEM (genotype ${genotype}), lower<br>
age: ${plotObject.ageLabels[j]}<br>
${data['phenotype']}: ${SEMyData[genotype][j][0]}<br>
individuals: ${data[genotype]['N'][j]}
`);
                    xArray.push(`
SEM (genotype ${genotype}), upper<br>
age: ${plotObject.ageLabels[j]}<br>
${data['phenotype']}: ${SEMyData[genotype][j][1]}<br>
individuals: ${data[genotype]['N'][j]}
`);
                    SEMtext.push(xArray);
                }
            }
            SEMtexts[genotype] = SEMtext;
        }
        return SEMtexts;
    };
    
    this.createPercentileTexts = function (data) {
        console.log('Creating percentile texts ...');
        var percentileData = this.convertPercentileYdata(data);
        var percentileTexts = {};
            
        for (var i = 0; i < genotypes.length; i++) {
            var percentileText = [];
            var genotype = genotypes[i];
            console.log('percentileData[genotype]: ' + JSON.stringify(percentileData[genotype]));
            //if (data[genotype]['2.5%'].length == plotObject.ages.length && 
            //        data[genotype]['97.5%'].length == plotObject.ages.length) {
            if (data[genotype]['2.5%'] != null && data[genotype]['97.5%']) {
                for (var j = 0; j < plotObject.ages.length; j++) {                    
                    var xArray = [];
                    if (percentileData[genotype].length > j) {
                        xArray.push(`
2.5th percentile (genotype ${genotype})<br>
age: ${plotObject.ageLabels[j]}<br>
${data['phenotype']}: ${percentileData[genotype][j][0]}<br>
individuals: ${data[genotype]['N'][j]}
`);
                        xArray.push(`
97.5th percentile (genotype ${genotype})<br>
age: ${plotObject.ageLabels[j]}<br>
${data['phenotype']}: ${percentileData[genotype][j][1]}<br>
individuals: ${data[genotype]['N'][j]}
`);
                        percentileText.push(xArray);
                    }
                }
            }
            percentileTexts[genotype] = percentileText;                
        }
        return percentileTexts;
    };
    
    this.createSurfaces = function(data) {
        var surfaces = {};
            
        for (var i = 0; i < 3; i++) {
            var genotype = genotypes[i];
            surfaces[genotype] = {x : [], z : []};
            var z = data[genotype]['N'];

            for (var j = 0; j < plotObject.ages.length; j++) {
                surfaces[genotype]['x'].push([plotObject.ages[j], plotObject.ages[j]]);
                surfaces[genotype]['z'].push([z[j], z[j]]);
            }
        }
        
        return surfaces;
    };
    
    
    
    /*
     * Function for converting the percentile y-axis data into a 3D compatible format.
     * 
     * @param {object} data - the data to convert
     * @return {object}
     */
    this.convertPercentileYdata = function (data) {
        var percentileData = {};
        for (var i = 0; i < genotypes.length; i++) {
            var genotype = genotypes[i];
            percentileData[genotype] = [];
            if (data[genotype]['2.5%'] && data[genotype]['97.5%']) {
                for (var j = 0; j < data[genotype]['2.5%'].length; j++) {
                    percentileData[genotype].push([data[genotype]['2.5%'][j], data[genotype]['97.5%'][j]]);
                } 
            }            
        }
        return percentileData;        
    };
     
    
    /**
     * Set the options for the plot.
     * 
     * @param {object} changedPlotOptions - the plot options to set
     */
    this.setPlotOptions = function (changedPlotOptions) {
        console.log('newPlotOptions for 3D plot: ' + JSON.stringify(changedPlotOptions));
        for (var i = 0; i < changedPlotOptions.length; i++) {
            var option = changedPlotOptions[i];
            if (option == 'medians') {
                Plotly.restyle(plotObject.divID, {visible : plotObject.currentPlotOptions[option]}, this.medianIndices);
            }
            else if (option == 'SEM') {
                Plotly.restyle(plotObject.divID, {visible : plotObject.currentPlotOptions[option]}, this.SEMindices);
            }
            else if (option == 'percentiles') {
                Plotly.restyle(plotObject.divID, {visible : plotObject.currentPlotOptions[option]}, this.percentileIndices);
            }
            else if (option == 'age spacing') {
                Plotly.relayout(plotObject.divID, 'scene.xaxis.type', plotObject.ageSpacingMap[plotObject.currentPlotOptions['age spacing']]);
            }
            else if (option == 'y to zero') {
                console.log('plotObject.getMinY(): ' + JSON.stringify(plotObject.getMinY()));
                //Plotly.relayout(plotObject.divID, 'scene.yaxis.range[0]', plotObject.getMinY());
                Plotly.relayout(plotObject.divID, 'scene.yaxis.range', [plotObject.getMinY(), plotObject.currentData['percentile max']]);
            }
        }
    };
};