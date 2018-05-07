var nonLongitudinalPercentiles = nonLongitudinalPercentiles || {};

nonLongitudinalPercentiles.Component = function (element, number, setupData) {   

    var plotCreated = false;
    
    plotlyPlot.call(this, element, number, 'overlay', {});
    
    
    this.setData = function (newData) {
        if (newData != null){
            console.log('new data: ' + JSON.stringify(newData));
            var dataObject = newData['data'];
            
            if (!plotCreated) {
                var layout = {
                    title : newData.layout['title'],
                    xaxis : {
                            type : 'category',
                            title : 'percentile'
                    },
                    yaxis : {
                            title : newData.layout['y-axis']
                    }
                };
                var configuration = this.commonConfiguration;
                var trace = {
                    x: dataObject['x'],
                    y: dataObject['y']
                };
                var data = [trace];
                //console.log('creating plot');
                Plotly.newPlot(this.gd, data, layout, configuration);
                this.resize();
            }
            else {
                Plotly.restyle(this.divID, dataObject);
            }
        };
    };      
};