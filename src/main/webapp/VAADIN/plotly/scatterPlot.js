var scatterPlot = scatterPlot || {};


scatterPlot.Component = function (element, number, setupData) {   
    var plotCreated = false;
    
    plotlyPlot.call(this, element, number, 'overlay', {});
    
    var x;
    var y;
    var text;
    
    this.setData = function (newData) {
        if (newData != null){
            console.log('newData[\'data\']: ' + newData['data']);
            var dataObject = newData['data'];
            
            if (dataObject['position'] != null) {
                console.log('SNPs: ' + dataObject.position.length);
                x = dataObject['position'];
            }
            if (dataObject['RefMAF'] != null) {
                y = dataObject['RefMAF'];
            }
            if (dataObject['ID'] != null) {
                text = dataObject['ID'];
            }
            
            
            //for (var i = 0; i < dataObject.position.length; i++) {
            //    y.push(1);
            //}
            
            //console.log('dataObject: ' + dataObject);
            
        };
        if (x != null && y != null && text != null) {
            plot();
        }
    };
    
    this.plot = function () {
        if (plotCreated) {
                //Plotly.restyle(this.divID, dataObject);
            }
            else {
                var configuration = this.commonConfiguration;
                var trace1 = {
                    x: x,
                    y: y,
                    text: text,
                    type: 'scattergl',
                    mode: 'markers'
                };
                var data = [trace1];
                //console.log('creating plot');
                Plotly.newPlot(this.gd, data, {}, configuration);
                plotCreated = true;
            }
    };
};