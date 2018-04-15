var numberPlot = numberPlot || {};

numberPlot.Component = function (element, number) {
    
    var thisObject = this;
    const style = {
        width : '41vw',
        height: '20vh'
    };
        
    plotlyPlot.call(this, element, number, 'number', style); // inherit

    var numbers = {
        'AA'    : [2065,1393,1784,1797,1595,1547,1470,1155,1143,1009,966,767],
        'AB'    : [1916,1302,1638,1660,1450,1399,1330,1049,1065,927,903,727],
        'BB'    : [468,334,412,415,378,360,336,269,274,244,229,184]
    };

    var genotypes = ['AA', 'AB', 'BB'];	

    const maxN = Math.max(...numbers.AA.concat(numbers.AB).concat(numbers.BB)); // find max n

    console.log('max n: ' + maxN);

    var ages = ["birth", "6 weeks", "3 months", "6 months", "8 months", "1 year",
                "15-18 months", "2 years", "3 years", "5 years", "7 years", "8 years"];

    var steps = [];

    for (var i = 0; i < ages.length; i++) {
            steps.push({
          label: ages[i],
          method: 'restyle',
          args: ['x', [[numbers.AA[i], numbers.AB[i], numbers.BB[i]]]]
        });
    }

    console.log('steps: ' + JSON.stringify(steps));
    
    var layout = {
        margin: {
            r: 20,
            t: 25,
            b: 25,
            l: 25
  },
  title : 'number of individuals',
  xaxis: {range: [0, maxN*1.05]},
  sliders: [{
    pad: {t: 10},
    currentvalue: {
      xanchor: 'right',
      prefix: 'age: ',
      font: {
        color: '#888',
        size: 20
      }
    },
    steps: steps
  }]
}	

Plotly.plot(this.gd, [{
        x: [numbers.AA[0], numbers.AB[0], numbers.BB[0]],
        y: genotypes,
        type : 'bar',
        orientation : 'h'
        }], layout);
};