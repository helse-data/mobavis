var barPlot = barPlot || {};

barPlot.Component = function (element, number) {
    
    const style = {
        width : '49vw',
        height: '83vh'
    };
//    const style = {
//            width : '35vw',
//            height: '83vh'
//        };

    
    var x = [];
    var y = [];
    
    plotlyPlot.call(this, element, number, 'bar', style);
    
    this.setUp = function(setupData) {
        if (setupData == null) {
            setupData = {};
        }

        console.log('Setup data: ' + JSON.stringify(setupData));

        const defaultValues = {
            x : [],
            y : [],
            title : '',
            colour : 'rgb(30, 0, 200)',
            'x-axis' : '',
            'y-axis' : ''
        };
        
        var initialValues = {};
        const initialValueNames = ['x', 'y', 'title', 'colour', 'x-axis', 'y-axis'];
        
        for (var i = 0; i < initialValueNames.length; i++) {
            var name = initialValueNames[i];
            initialValues[name] = setupData[name] || defaultValues[name];
        }
        
        x = initialValues.x;
        y = initialValues.y;
        //console.log('x: ' + x);
        var data = [
          {
            x: x, //this.chromosomes,
            y: y,//SNPsPerChromosome,
            type: 'bar',
                marker : {
                        color : initialValues.colour
                }
          }
        ];

        var layout = {
                title : initialValues.title,
                xaxis : {
                        type : 'category',
                        title : initialValues['x-axis']
                },
                yaxis : {
                        title : initialValues['y-axis']
                },
                //yaxis : {
                //        type: 'linear'
                //},
                margin: {
                        l: 60,
                        r: 20,
                        b: 35,
                        t: 60,
                        //pad: 4
                    }
        };
        var configuration = this.commonConfiguration;
        configuration['modeBarButtonsToRemove'] = ['sendDataToCloud'];
        Plotly.newPlot(this.gd, data, layout, configuration);
}
    
    this.setData = function (data) {
        if (data != null){
            console.log('Data through setData(): ' + JSON.stringify(data));
            var dataList = [];
            for (var i = 0; i < x.length; i++) {
                dataList.push(data['data'][x[i]]);
            }
            
            //console.log('dataList: ' + dataList);
            //console.log(layout);
            //data.y = dataList;
            
            //Plotly.update(this.divID, data_update, layout_update);
            
            //Plotly.update(this.divID, [dataList], layout);
            
            
            Plotly.restyle(this.divID, 'y', [dataList]);
            
            if (data['layout'] != null) {
                //console.log('layout: ' + JSON.stringify(data['layout']));
                var newLayout = {yaxis : {}};
                newLayout.title = data.layout.title;
                newLayout.yaxis.title = data.layout['y-axis'];
                Plotly.relayout(this.divID, newLayout);
            }
            
            //Plotly.relayout(this.divID, layout);
            //console.log('this.gd: ' + this.gd);
            //Plotly.newPlot(this.gd, [dataList], layout, {scrollZoom: true});
        };
    };
};



