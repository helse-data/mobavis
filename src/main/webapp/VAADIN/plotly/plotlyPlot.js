/**
 * Super class for plotly.js. Allows inheritance of methods, constants and provides standardisation.
 * 
 * @constructor
 * @param {?} element - HTML element
 * @param {number} number - which number in a row of this plot type this is
 * @param {string} type - the type of plot. Combined with the number argument it should provide a uniqe div ID for the plot
 * @param {?} style - styling of the div
 */
function plotlyPlot (element, number, type, style) {    
    var thisObject = this; // for use inside the plot.on() functions
    this.divID = type + 'Plot' + number;
    
    //this.element = element;
    
    element.innerHTML = '<div id="' + this.divID + '"></div>';
    
    this.ages =  [0, 42, 94, 182, 246, 369, 472, 761, 1103, 1885.83333333333, 2585.41666666667, 2950.41666666667];
    this.ageLabels = ["birth", "6 weeks", "3 months", "6 months", "8 months", "1 year",
            "15-18 months", "2 years", "3 years", "5 years", "7 years", "8 years"];
                
    this.ageIndices = {};
    
    for (var i = 0; i < this.ageLabels.length; i++) {
	this.ageIndices[this.ageLabels[i]] = i;
    }	
        
    this.ageLabelsShort = ["birth", "5w", "3m", "6m", "8m", "1y", "15-18m", "2y", "3y",
       "5y", "7y", "8y"];
    this.chromosomes = ['1', '2', '3', '4', '5', '6', '7', '8', '9', '10', '11',
                    '12', '13', '14', '15', '16', '17', '18', '19', '20', '21', '22', 'X', 'Y'];
    this.chromosomeLengths = [249250621, 243199373, 198022430, 191154276, 180915260, 171115067, 159138663, 146364022,
        141213431, 135534747, 135006516, 133851895, 115169878, 107349540, 102531392, 90354753, 81195210, 78077248,
        59128983, 63025520, 48129895, 51304566, 155270560, 59373566];

    this.commonConfiguration = {
        scrollZoom: true,
        modeBarButtonsToAdd: [{ // add option to download SVG
        name: 'Download plot as SVG',
        icon: Plotly.Icons.camera,
        click: function(gd) {
          Plotly.downloadImage(gd, {format: 'svg'})
        }
    }]};

    // use d3 for automatic resizing  
    d3 = Plotly.d3;
//    this.gd3 = d3.select('#' + this.divID);

    //console.log('style: ' + (style['width']   || '98vw') + ' |-| ' +  (style['height'] || '96vh'));

    this.gd3 = d3.select('#' + this.divID).style({
            width: style['width']   || '100%',
            height: style['height'] || '100%'//,
            //'margin-top' : style['margin-top'] || '20px' 
     });

//    this.gd3 = d3.select('#' + this.divID).style({
//             width: style['width']   || '400px',
//             height: style['height'] || '400px',
//             'margin-top' : style['margin-top'] || '20px' 
//     });
    
    this.gd = this.gd3.node();
    
    this.plot = document.getElementById(this.divID);
    
    // cf. jQuery
    this.elementIsVisible = function() {
        return element.offsetWidth > 0 || element.offsetHeight > 0 || element.getClientRects().length > 0;
    }
    
    this.setSize = function(newSize) {
        d3.select('#' + this.divID).style(newSize);
        console.log('new size: ' + JSON.stringify(newSize));
        thisObject.resize();
        console.log('changed size');
    };    
    
    // use addEventListener to support resizing of multiple plots on the same page
    this.resize = function() {
        console.log('resize()');
        if (thisObject.elementIsVisible()) { // only resize visible elements
            Plotly.Plots.resize(thisObject.gd);
        }        
    };   
    
    window.addEventListener('resize', this.resize);
};

