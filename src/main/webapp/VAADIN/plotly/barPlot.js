var barPlot = barPlot || {};

barPlot.Component = function (element, number) {
    
    const style = {
        width : '100%',
        height: '100%'
    };

    
    var x = [];
    var y = [];
    
    plotlyPlot.call(this, element, number, 'bar', style);
    
    this.setUp = function(setupData) {
        if (setupData == null) {
            setupData = {};
        }

        console.log('Setup data: ' + JSON.stringify(setupData));

        const defaultValues = {
            x: [],
            y: [],
            title: '',
            colour: 'rgb(30, 0, 200)',
            colour1: 'rgb(31, 119, 180)',
            colour2: 'rgb(30, 200, 0)',
            'x-axis': '',
            'y-axis': ''
        };
        
        var initialValues = {};
        const initialValueNames = ['x', 'y', 'title', 'colour', 'colour2', 'x-axis', 'y-axis'];
        
        for (var i = 0; i < initialValueNames.length; i++) {
            var name = initialValueNames[i];
            initialValues[name] = setupData[name] || defaultValues[name];
        }
        
       
        //console.log('x: ' + x);
        
        var data;
        
        if (setupData.barmode == 'stack') {
            data = [];
            var i = 1;
            var colourKey;
            
            while (setupData['y' + i] != null) {
                var nameData = setupData['name' + i];
                var name = '';
                var showlegend = false;
                if (nameData != null) {
                    name = nameData;
                    showlegend = true;
                }
                var dataObject = {
                    x: initialValues.x,
                    y: setupData['y' + i],
                    name: name,
                    type: 'bar',
                    marker: {
                        color : initialValues['colour' + i]
                    },
                    showlegend: showlegend
                };
                data.push(dataObject);
                i++;
            }
            console.log('data: ' + JSON.stringify(data));
            
        }
        else {
            x = initialValues.x;
            y = initialValues.y;
            data = [{
                x: x,
                y: y,
                type: 'bar',
                marker: {
                    color : initialValues.colour
                }
              }];
        }
        

        var layout = {
                title : initialValues.title,
                barmode: setupData.barmode,
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
//                margin: {
//                        l: 60,
//                        r: 20,
//                        b: 35,
//                        t: 60,
//                        //pad: 4
//                    }
        };
        var configuration = this.commonConfiguration;
        configuration['modeBarButtonsToRemove'] = ['sendDataToCloud'];
        console.log('data: ' + JSON.stringify(data));
        console.log('layout: ' + JSON.stringify(layout));
        Plotly.newPlot(this.gd, data, layout, configuration);
        //this.resize(); // TODO: debug!
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



