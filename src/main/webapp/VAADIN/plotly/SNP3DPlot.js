var SNP3DPlot = SNP3DPlot || {};


SNP3DPlot.Component = function (element, number, setupData) {   
    var plotCreated = false;
    
    plotlyPlot.call(this, element, number, 'SNP3D', {});



    this.setData = function (newData) {

        var x = [1, 2, 5];
        var xSurface = [[x[0], x[0]], [x[1], x[1]], [x[2], x[2]]];
        var zSurface = [[100, 100], [90, 90], [50, 50]];
        var xLabels = ['1 year', '2 years', '5 years'];
        var y = [10, 15, 17];
        var z = [100, 90, 50];
        //var c = [];

        var y2 = [20, 30, 40];
        var z2 = [30, 25, 22];

        var y3 = [50, 55, 60];
        var z3 = [15, 13, 10];

        var SEM1Data = {
                y: [[9, 11], [14.5, 15.5], [15, 19]]
        };

        var percentiles1Data = {
                y: [[7, 13], [12.5, 17.5], [13, 21]]
        };

        var SEM1Text = [];

        for (var i = 0; i < xSurface.length; i++) {
                var xArray = []
                xArray.push(`
        SEM, lower<br>
        age: ${xLabels[i]}<br>
        BMI: ${SEM1Data.y[i][0]}<br>
        individuals: ${zSurface[i][0]}
        `);
                xArray.push(`
        SEM, upper<br>
        age: ${xLabels[i]}<br>
        BMI: ${SEM1Data.y[i][1]}<br>
        individuals: ${zSurface[i][0]}
        `);
                SEM1Text.push(xArray);
        }

        var Percentiles1Text = [];

        for (var i = 0; i < xSurface.length; i++) {
                var xArray = []
                xArray.push(`
        2.5th percentile<br>
        age: ${xLabels[i]}<br>
        BMI: ${percentiles1Data.y[i][0]}<br>
        individuals: ${zSurface[i][0]}
        `);
                xArray.push(`
        97.5th percentile<br>
        age: ${percentiles1Data[i]}<br>
        BMI: ${SEM1Data.y[i][1]}<br>
        individuals: ${zSurface[i][0]}
        `);
                Percentiles1Text.push(xArray);
        }

        console.log('SEM1Text:' + JSON.stringify(SEM1Text));
        console.log('SEM1Text[0]:' + JSON.stringify(SEM1Text[0]));
        console.log('SEM1Text.length:' + SEM1Text.length);

        var text = x.map((xi, i) => `
        Median<br>
        age: ${xLabels[i]}<br>
        BMI: ${y[i]}<br>
        individuals: ${z[i]}
        `);

        var text2 = x.map((xi, i) => `
        age: ${xLabels[i]}<br>
        BMI: ${y2[i]}<br>
        individuals: ${z2[i]}
        `);

        var text3 = x.map((xi, i) => `
        age: ${xLabels[i]}<br>
        BMI: ${y3[i]}<br>
        individuals: ${z3[i]}
        `);

        console.log('text: ' + JSON.stringify(text));

        var layout = {
                title: 'BMI',
                scene: {
                        xaxis: {
                                title : 'age',
                                tickvals: x,
                                ticktext: xLabels
                                },
                        yaxis: {title : 'BMI'},
                        zaxis: {
                                title : 'number of individuals',
                                range: [0, 100]},
                        camera: {
                  center: {x: 0, y: 0, z: 0}, 
                  eye: 
                          //{x: 0, y: 0, z: 1.25},
                          {x:1.966, y:-0.932, z:1.240}, 
                  up: {x: 0, y: 0, z: 1}
                        }
                }
        };


        var trace1 = {
                type: 'scatter3d',
                hoverinfo: 'text',
                mode: 'lines+markers',
                name: 'AA',
                legendgroup: 'AA',
                x: x,
                y: y,
                z: z,
                text: text,
                line: {
                        width: 6,
                        color: 'black'
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


        var trace2 = {
          type: 'scatter3d',
          mode: 'lines+markers',
          hoverinfo: 'text',
          name: 'AB',
          x: x,
          y: y2,
          z: z2,
          text: text2,
          line: {
                width: 6,
                color: 'blue',
                },
          marker: {
            size: 3.5
          }
        };

        var trace3 = {
          type: 'scatter3d',
          mode: 'lines+markers',
          hoverinfo: 'text',
          name: 'BB',
          x: x,
          y: y3,
          z: z3,
          text: text3,
          line: {
                width: 6,
                color: 'red',
                },
          marker: {
            size: 3.5
          }
        };

        // SEMs

        var surfaceColour = [[9, 10], [9, 10], [9, 10]];
        var colourScale = [[0, "rgb(0, 0, 0)"], [1, "rgb(0, 0, 0)"]];

        var SEM1 = {
                type: 'surface',
                hoverinfo: 'text',
                name: 'AA SEM',
                legendgroup: 'AA',
                x: xSurface,
                y: SEM1Data.y,
                z: zSurface,
                text: SEM1Text,
                surfacecolor: surfaceColour,
                colorscale: colourScale,
                opacity: 0.5,
                showscale: false
        };

        var percentiles1 = {
                type: 'surface',
                hoverinfo: 'text',
                name: 'AA percentiles',
                legendgroup: 'AA',
                x: xSurface,
                y: percentiles1Data.y,
                z: zSurface,
                text: Percentiles1Text,
                surfacecolor: surfaceColour,
                colorscale: colourScale,
                opacity: 0.5,
                showscale: false
        };

        var layout = {
            title: 'BMI',
            scene: {
                    xaxis: {
                            title : 'age',
                            tickvals: x,
                            ticktext: xLabels
                            },
                    yaxis: {title : 'BMI'},
                    zaxis: {
                            title : 'number of individuals',
                            range: [0, 100]},
                    camera: {
              center: {x: 0, y: 0, z: 0}, 
              eye: 
                      //{x: 0, y: 0, z: 1.25},
                      {x:1.966, y:-0.932, z:1.240}, 
              up: {x: 0, y: 0, z: 1}
                    }
            }
        };
        var configuration = this.commonConfiguration;
        var data = [trace1, SEM1, percentiles1, trace2, trace3];


        Plotly.newPlot(this.gd, data, layout, configuration);
        this.resize();
    };


    this.plot.on('plotly_legendclick', function(data){
        var eventData = data.eventData;
            for (var property in eventData) {
                    if (eventData.hasOwnProperty(property)) {
                        console.log(JSON.stringify(eventData[property]));
                    }
            }
            console.log('eventData: ' + eventData);
    });

};