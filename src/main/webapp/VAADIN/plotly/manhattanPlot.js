var manhattanPlot = manhattanPlot || {};

/**
 * 
 * Component object constructor.
 * 
 * @constructor
 */
manhattanPlot.Component = function (element, number, connector) {
    plotlyPlot.call(this, element, number, 'SNP', {}); // inherit via the call() method
    
    var thisObject = this;
    
    var THRESHOLD = 0;
    var SUGGESTIVE_THRESHOLD = 1;
    var thresholdVisibility = [true, true];
    
    
     /**
     * Sets the data to be visualised.
     * 
     * @param {object} newData
     */
    this.setData = function (newData) {
        console.log('setting data');
        
        const colours = ['gray', 'black'];
        
        var traceList = [];

        var annotationList = [];

        for (var i = 0; i < this.chromosomes.length; i++) {
            var x = [];
            var y = [];
            var hoverText = [];
            var colour = colours[i % 2];
            
            var chromosome = this.chromosomes[i];
            var chromosomeData = newData[chromosome]
            
            for (var j = 0; j < chromosomeData.chromosome_positions.length; j++) {
                var p_value = chromosomeData.p_values[j];
                var SNPname = chromosomeData.names[j];
                var hoverTextString = 'SNP: ' + SNPname +  '<br>p-value: ' + chromosomeData.p_value_labels[j] + '<br>chromosome: ' + chromosome + '<br>position: ' + chromosomeData.chromosome_positions[j];
                
                x.push(chromosomeData.plot_positions[j]); // plot coordinate
                y.push(p_value); // p-value
                hoverText.push(hoverTextString);
                //console.log('chromosomeData.plot_positions[j]: ' + JSON.stringify(chromosomeData.plot_positions[j]));
            }
            
            //console.log('x[24000]: ' + JSON.stringify(x[24000]));
            //console.log('min: ' + Math.min(...x) + ', max: ' + Math.max(...x));

            var trace = {
                x: x,
                y: y,
                showlegend : false,
                hoverinfo: 'text',
                text: hoverText,
                type: 'scattergl',
                mode: 'markers',
                marker : {
                        color : colour,
                	size: 4
                } 
            };
            traceList.push(trace);		
        }

        //console.log('traceList: ' + JSON.stringify(traceList));
        var chromosomeStartPosition = 0;
        var tickValues = [];
        for (var i = 0; i < this.chromosomes.length; i++) {
            
            tickValues.push(chromosomeStartPosition + this.chromosomeLengths[i]/2);
            
            var annotation = {
                xref: 'x',
                yref: 'y',
                x: chromosomeStartPosition + this.chromosomeLengths[i]/2,
                //xanchor: 'right',
                y: -10,
                //yanchor: 'bottom',
                text: this.chromosomes[i],
                //textangle : -90, 
                showarrow : false
            };

            //console.log('annotation x: ' + (CHROMOSOME_LENGTH/2 + j*CHROMOSOME_LENGTH));

            annotationList.push(annotation);
            chromosomeStartPosition += this.chromosomeLengths[i];
        }
        
        this.minX = -10;
        this.maxX = chromosomeStartPosition + this.chromosomeLengths[this.chromosomeLengths.length-1];
        
        console.log('this.maxX: ' + this.maxX);
        
        const threshold1 = -Math.log10(5E-8) ;
        const threshold2 = -Math.log10(1E-5) ;
        
        var layout = {
            title: 'Manhattan plot ',
            xaxis : {
                    title : 'Chromosome',
                    tickvals: tickValues,
                    ticktext: this.chromosomes,
                    //ticks: 'inside',
                    range: [this.minX, this.maxX]
            },
            yaxis: {
                    title : '-log<sub>10</sub> (p)'
                    //anchor: 'free',
                    //position: 0
            },
            hovermode: 'closest',
            shapes: [
                // horizontal threshold lines
                {
                  type: 'line',
                  x0: 0,
                  y0: threshold1,
                  x1: this.maxX,
                  y1: threshold1,
                  line: {
                        color: 'rgb(0, 245, 0)',
                        width: 0.8
                  }
                },
                {
                  type: 'line',
                  x0: 0,
                  y0: threshold2,
                  x1: this.maxX,
                  y1: threshold2,
                  line: {
                        color: 'green',
                        width: 0.8
                  }
                }
                ]
            //annotations: annotationList
        };
        
        
         // creating the plot
        var configuration = this.commonConfiguration;
        configuration['modeBarButtonsToRemove'] = ['sendDataToCloud'];

        Plotly.newPlot(this.gd, traceList, layout, configuration);
        
        var plot = document.getElementById(thisObject.divID);
        
        plot.on('plotly_relayout', function(eventData) {
            console.log('eventData: ' + JSON.stringify(eventData));
            if (eventData['xaxis.autorange'] || eventData.autosize == true) {
                Plotly.relayout(thisObject.divID, 'xaxis.autorange', false);
                Plotly.relayout(thisObject.divID, 'xaxis.range', [thisObject.minX, thisObject.maxX]);
            }
        });
        
        plot.on('plotly_click', function(eventData){
            //console.log(JSON.stringify(eventData));
            //console.log(eventData);

            //console.log('Clicked SNP: ' +  eventData.points[0].text);
            connector.registerSNPclick(eventData.points[0].text);
        });
        
        this.resize(); // TODO: make redundant
        
        /** Catch the plotly_relayout event. */
        
        
    };
    
    /**
     * 
     * @param {type} options
     * @returns {undefined}
     */
    this.setOptions = function (options) {
        console.log('setting options');       
        
        
        if (thresholdVisibility[THRESHOLD] != options['genome-wide significance threshold']) {
            Plotly.relayout(thisObject.divID, 'shapes[' + THRESHOLD + '].visible', options['genome-wide significance threshold']);
            thresholdVisibility[THRESHOLD] = options['genome-wide significance threshold'];
            console.log('changed visbility of ' + THRESHOLD + ' to ' + options['genome-wide significance threshold']);
        }
        if (thresholdVisibility[SUGGESTIVE_THRESHOLD] != options['suggestive genome-wide significance threshold']) {
            Plotly.relayout(thisObject.divID, 'shapes[' + SUGGESTIVE_THRESHOLD + '].visible', options['suggestive genome-wide significance threshold']);
            thresholdVisibility[SUGGESTIVE_THRESHOLD] = options['suggestive genome-wide significance threshold'];
            console.log('changed visbility of ' + SUGGESTIVE_THRESHOLD + ' to ' + options['suggestive genome-wide significance threshold']);
        }        
    };
    
    
   
};