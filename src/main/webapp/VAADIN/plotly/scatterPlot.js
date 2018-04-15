var scatterPlot = scatterPlot || {};


scatterPlot.Component = function (element, number, setupData) {   
    const style = {
        width : setupData['width'] || '35vw',
        height: setupData['height'] || '83vh'
    };
    var plotCreated = false;
    
    plotlyPlot.call(this, element, number, 'overlay', style);
    
    
    this.setData = function (newData) {
        if (newData != null){
            console.log('new data!');
            var dataObject = newData['data'];
            var y = [];
            console.log('SNPs: ' + dataObject.position.length);
            for (var i = 0; i < dataObject.position.length; i++) {
                y.push(1);
            }
            
            //console.log('dataObject: ' + dataObject);
            if (plotCreated) {
                Plotly.restyle(this.divID, dataObject);
            }
            else {
                var configuration = this.commonConfiguration;
                var trace1 = {
                    x: dataObject['position'],
                    y: y,
                    text: dataObject['ID']//,
                    //mode: 'markers'
                };
                var data = [trace1];
                //console.log('creating plot');
                Plotly.newPlot(this.gd, data, {}, configuration);
            }
        };
    };      
};