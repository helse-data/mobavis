//var Plotly = require('plotly.js');
var main = main || {};



main.Main = function () {
    const transparentGray = 'rgba(0, 0, 0, 0.1)';
    const transparentBlue = 'rgba(0, 0, 255, 0.1)';
    const transparentRed = 'rgba(255, 0, 0, 0.1)';
    

    const medians = [[14.2, 15.91, 16.79, 17.39, 17.55, 17.24, 17.06, 16.67, 16.24, 15.58, 15.67, 15.98], 
        [14.31, 15.85, 16.75, 17.32, 17.4, 17.12, 16.91, 16.48, 16.12, 15.53, 15.61, 15.93],
    [14.31, 15.92, 16.76, 17.36, 17.43, 17.1, 16.95, 16.55, 16.07, 15.43, 15.45, 15.98]];
    const SEMsLower = [[14.14, 15.82, 16.71, 17.3, 17.46, 17.16, 16.97, 16.58, 16.14, 15.49, 15.56, 15.85],
    [14.26, 15.78, 16.69, 17.26, 17.34, 17.06, 16.85, 16.4, 16.05, 15.46, 15.53, 15.83], 
    [14.24, 15.82, 16.68, 17.27, 17.35, 17.02, 16.87, 16.45, 15.98, 15.34, 15.34, 15.84]];
    const SEMsUpper = [[14.27, 16, 16.88, 17.48, 17.64, 17.34, 17.15, 16.78, 16.35, 15.71, 15.8, 16.16],
    [14.35, 15.92, 16.82, 17.39, 17.48, 17.18, 16.98, 16.56, 16.2, 15.62, 15.71, 16.06], 
    [14.39, 16.01, 16.86, 17.46, 17.53, 17.19, 17.04, 16.65, 16.18, 15.54, 15.6, 16.13]];
    
    const ages = [0, 44, 94, 182, 246, 369, 472, 761, 1103, 1885.83333333333, 2585.41666666667, 2950.41666666667];
    const ageLabels = ["birth", "5 weeks", "3 months", "6 months", "8 months", "1 year",
            "15-18 months", "2 years", "3 years", "5 years", "7 years", "8 years"];
                
        
    // AA

    var traceMedianAA = {
      x: ages,
      y: medians[0],
      type: 'scatter',
      line: {
            color : 'grey',
        dash: 'dash',
        width: 2
      },
      mode : 'lines',
      name : 'AA',
      fill : 'tonexty',
      fillcolor : transparentGray
    };

    var traceUpperAA = {
      x: ages,
      y: SEMsUpper[0],
      type: 'scatter',
      showlegend: false,
      mode: 'none',
      name : 'upper SEM',
      fill : 'tonexty',
      fillcolor : transparentGray
    };

    var traceLowerAA = {
      x: ages,
      y: SEMsLower[0],
      type: 'scatter',
      showlegend: false,
      mode: 'lines',
      name : 'lower SEM',
      line: {
            color : transparentBlue,
        width: 0
      },
      //fill : 'tonexty',
      //fillcolor : transparentGray
    };

    // AB

    var traceMedianAB = {
      x: ages,
      y: medians[1],
      type: 'scatter',
      line: {
            color : 'blue',
        dash: 'dash',
        width: 2
      },
      mode : 'lines',
      name : 'AB',
      fill : 'tonexty',
      fillcolor : transparentBlue
    };

    var traceUpperAB = {
      x: ages,
      y: SEMsUpper[1],
      type: 'scatter',
      showlegend: false,
      mode: 'none',
      name : 'upper SEM',
      fill : 'tonexty',
      fillcolor : transparentBlue
    };

    var traceLowerAB = {
      x: ages,
      y: SEMsLower[1],
      type: 'scatter',
      showlegend: false,
      mode: 'lines',
      name : 'lower SEM',
      line: {
            color : transparentBlue,
        width: 0
      }
    };
    
    var traceMedianBB = {
      x: ages,
      y: medians[2],
      type: 'scatter',
      line: {
            color : 'red',
        dash: 'dash',
        width: 2
      },
      mode : 'lines',
      name : 'BB',
      fill : 'tonexty',
      fillcolor : transparentRed
    };
    
    var traceUpperBB = {
      x: ages,
      y: SEMsUpper[2],
      type: 'scatter',
      showlegend: false,
      mode: 'none',
      name : 'upper SEM',
      fill : 'tonexty',
      fillcolor : transparentRed
    };

    var traceLowerBB = {
      x: ages,
      y: SEMsLower[2],
      type: 'scatter',
      showlegend: false,
      mode: 'lines',
      name : 'lower SEM',
      line: {
            color : transparentRed,
        width: 0
      }
    };

    var layout = {
        title: 'rs9996',
        showlegend: true,
        xaxis: {
            title: 'age',
            tickvals: ages,
            ticktext: ageLabels
          //tickfont: { size: 16 }
        },
        yaxis : {
            title: 'BMI'
        }
    };

    var data = [traceLowerAA, traceMedianAA, traceUpperAA, traceLowerAB, traceMedianAB, traceUpperAB,
    traceLowerBB, traceMedianBB, traceUpperBB];

    //Plotly.newPlot('plot', data, layout, {scrollZoom: true});
    
    // for automatic resizing
    var d3 = Plotly.d3;
    var gd3 = d3.select('#plot').style({
            width: '100vw',
            //'margin-left': '0vw',

            height: '83vh',
            'margin-top': '0.5vh'
            //'margin-bottom': '16vh'
        });
    var gd = gd3.node();
    Plotly.newPlot(gd, data, layout, {scrollZoom: true});
    
    window.onresize = function() {
        Plotly.Plots.resize(gd);
    };

    this.updateData = function (newData) {
        var updatedData  = {y : newData};
        
        console.log("Data sent to update function: " + newData);
        //console.log("Updated data: " + updatedData);
        if (newData != null){
            //Plotly.restyle('plot', updatedData, [4]);
            Plotly.restyle('plot', {y : [20, 20, 20, 20]}, [5]);
            console.log('data updated')
        }
        
        //var updatedData2  = {y : [10, 100, 30, 35]};
        //Plotly.restyle('plot', updatedData2, [1]);
    };
};