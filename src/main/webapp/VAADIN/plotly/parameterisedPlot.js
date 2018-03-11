var parameterisedPlot = parameterisedPlot || {};

parameterisedPlot.Component = function (element, number) {
    console.log('parameterisedPlot constructed');
    const style = {
        width : '70vw',
        height: '83vh'
    };
    
    plotlyPlot.call(this, element, number, 'parameterised', style);

    const percentileIndices = [0, 1, 2];
    
    const colours = {
            1  : 'rgb(107, 215, 288)',
            50 : 'rgb(22, 61, 157)',
            99 : 'rgb(107, 215, 288)'
    };

    var trace1 = {
            x: [],
            y: [],
            type: 'scatter',
            showlegend: true,
            line: {
                    color : colours[1],
                    dash: 'none',
                    width: 1
            },
            mode : 'markers+lines',
            name : '1st percentile',
    };

    var trace50 = {
            x: [],
            y: [],
            type: 'scatter',
            showlegend: true,
            line: {
                    color : colours[50], 
                    dash: '5px',
                    width: 2
            },	
            mode : 'markers+lines',
            name : '50th percentile',
    };

    var trace99 = {
            x: [],
            y: [],
            type: 'scatter',
            showlegend: true,
            line: {
                    color : colours[99],
                    dash: 'none',
                    width: 1
            },	
            mode : 'markers+lines',
            name : '99th percentile',
    };

    var trace1Annotation = {
            xref: 'x',
            yref: 'y',
            //x: 0.95,
            x : [],
            y: [],
            //xanchor: 'left',
            //yanchor: 'middle',
            text: '1st percentile',
            font: {
                    //family : 'Times New Roman',
                    family: 'Arial',
                    //family: 'Verdana',
                    size: 14,
                    color: colours[1]
            },
            showarrow: false
    };

    var trace99Annotation = {
            xref: 'x',
            yref: 'y',
            //x: 0.95,
            x : [],
            y: [],
            //xanchor: 'left',
            //yanchor: 'middle',
            text: '99th percentile',
            font: {
                    //family : 'Times New Roman',
                    family: 'Arial',
                    //family: 'Verdana',
                    size: 14,
                    color: colours[1]
            },
            showarrow: false
    };	

    var traceUser = {
          x: [],
          y: [],
          type: 'scatter',
          showlegend: true,
              connectgaps: true,
          mode: 'markers+lines',
          name : 'user data',
          line: {
                color : 'black',
            width: 1
          }
    };

    var percentileData = [trace1, trace50, trace99];
    var data = percentileData.concat(traceUser);

    var userDataAnnotation = {
            xref: 'x',
            yref: 'y',
            //x: ages[highestIndexWithUserData-1],
            //y: userData[highestIndexWithUserData-1],
            //xanchor: 'right',
            yanchor: 'top',
            text: 'user data',
            font: {
                    //family : 'Times New Roman',
                    family: 'Arial',
                    //family: 'Verdana',
                    size: 14,
                    color: 'black'
            },
            showarrow: false
    };

    //var annotations = [trace1Annotation, trace99Annotation];

    var layout = {
        title: '',
        showlegend: true,
        traceorder: 'normal',
		//annotations : annotations,
        legend: {
            x: '0.5',
            y: '1.15',
            font: {
		//family: 'Arial',
		size: 16
            }
            //'orientation'   : 'h',
            //'xanchor'       : 'center',
            //'yanchor'       : 'middle'
        },
        hovermode: 'closest',
        xaxis: {
            title: '',
            //tickvals: ages,
            //ticktext: ageLabels
          //tickfont: { size: 16 }
        },
        yaxis : {
            title: ''
        }
    };
    
    Plotly.newPlot(this.gd, data, layout, {scrollZoom: true});

    
    this.setPercentileData = function (data) {
        if (data != null){
            var newAxes = {
                xaxis : {title : data['phenotypes'][0]},
                yaxis : {title : data['phenotypes'][1]}
            };
            Plotly.relayout(this.divID, newAxes);
            Plotly.restyle(this.divID, 'x', [data['data 1'][0], data['data 1'][1], data['data 1'][2]], percentileIndices);
            Plotly.restyle(this.divID, 'y', [data['data 2'][0], data['data 2'][1], data['data 2'][2]], percentileIndices);
        };
    };
    
    this.showPercentiles = function(showPercentiles) {
        Plotly.restyle(this.divID, {'visible' : showPercentiles}, percentileIndices);
    };
    
    this.updateUserData = function (data) {        
        if (data != null){
            console.log('data: ' + JSON.stringify(data));         
            Plotly.restyle(this.divID, 'x', [data['data 1']], [3]);
            Plotly.restyle(this.divID, 'y', [data['data 2']], [3]);
        };
    };
};