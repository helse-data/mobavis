/*
 * 
 * Sets up a LocusZoom.js plot.
 * 
 */

var main = main || {};

main.Component = function (element, connector) {
    
    console.log('Constructing LocusZoom.');
    
    const divID = 'locuszoom';
    
    var self = this;
        
    this.firstSNP = true; // whether or not multiple SNPs has been selected for the current window
    
    element.innerHTML = '<div id="' + divID + '" data-region="10:114550452-115067678" class="lz-container-responsive" style="width: 83vw; height: 30h; min-width: 400px; min-height: 400px;">';
    
    
    // Beginning of code by University of Michigan Center for Statistical Genetics copyright (c) 2017
    // Code used to help set up functional example of LocusZoom.js.
    
    // Determine if we're online, based on browser state or presence of an optional query parameter
    var online = !(typeof navigator != "undefined" && !navigator.onLine);
    if (window.location.search.indexOf("offline") != -1){ online = false; }

    // Define LocusZoom Data Sources object differently depending on online status
    var apiBase;
    if (online){
      apiBase = "https://portaldev.sph.umich.edu/api/v1/";
      this.data_sources = new LocusZoom.DataSources()
            .add("assoc", ["AssociationLZ", {url: apiBase + "statistic/single/", params: {analysis: 45, id_field: "variant"}}])
            .add("ld", ["LDLZ", { url:apiBase + "pair/LD/" }])
            .add("gene", ["GeneLZ", { url: apiBase + "annotation/genes/", params: {source: 2} }])
            .add("recomb", ["RecombLZ", { url: apiBase + "annotation/recomb/results/", params: {source: 15} }])
            .add("constraint", ["GeneConstraintLZ", { url: "http://exac.broadinstitute.org/api/constraint" }]);
    } else {
      apiBase = window.location.origin + window.location.pathname.substr(0, window.location.pathname.lastIndexOf("/") + 1) + "staticdata/";
      this.data_sources = new LocusZoom.DataSources()
            .add("assoc", ["AssociationLZ", {url: apiBase + "assoc_10_114550452-115067678.json?", params: {analysis: 45, id_field: "variant"}}])
            .add("ld", ["LDLZ", { url: apiBase + "ld_10_114550452-115067678.json?" }])
            .add("gene", ["GeneLZ", { url: apiBase + "genes_10_114550452-115067678.json?" }])
            .add("recomb", ["RecombLZ", { url: apiBase + "recomb_10_114550452-115067678.json?" }])
            .add("constraint", ["GeneConstraintLZ", {  url: apiBase + "constraint_10_114550452-115067678.json?" }]);
    }

    // Get the standard association plot layout from LocusZoom's built-in layouts
    this.layout = LocusZoom.Layouts.get("plot", "standard_association");
    this.layout.dashboard = LocusZoom.Layouts.get("dashboard", "region_nav_plot");

    // Add a button to show the study abstract the layout
    var abstract = "<h4 style=\"margin-top: 0px;\"><a href=\"https://www.ncbi.nlm.nih.gov/pubmed/?term=22885922\" target=\"_new\">Type 2 diabetes meta-analysis</a></h4>1. Nat Genet. 2012 Sep;44(9):981-90. doi: 10.1038/ng.2383. Epub 2012 Aug 12.<br><br>Large-scale association analysis provides insights into the genetic architecture <br>and pathophysiology of type 2 diabetes.<br><br>Morris AP, Voight BF, Teslovich TM, Ferreira T, Segrè AV, Steinthorsdottir V,<br>Strawbridge RJ, Khan H, Grallert H, Mahajan A, Prokopenko I, Kang HM, Dina C,<br>Esko T, Fraser RM, Kanoni S, Kumar A, Lagou V, Langenberg C, Luan J, Lindgren CM,<br>Müller-Nurasyid M, Pechlivanis S, Rayner NW, Scott LJ, Wiltshire S, Yengo L,<br>Kinnunen L, Rossin EJ, Raychaudhuri S, Johnson AD, Dimas AS, Loos RJ, Vedantam S,<br>Chen H, Florez JC, Fox C, Liu CT, Rybin D, Couper DJ, Kao WH, Li M, Cornelis MC, <br>Kraft P, Sun Q, van Dam RM, Stringham HM, Chines PS, Fischer K, Fontanillas P,<br>Holmen OL, Hunt SE, Jackson AU, Kong A, Lawrence R, Meyer J, Perry JR, Platou CG,<br>Potter S, Rehnberg E, Robertson N, Sivapalaratnam S, Stančáková A, Stirrups K,<br>Thorleifsson G, Tikkanen E, Wood AR, Almgren P, Atalay M, Benediktsson R,<br>Bonnycastle LL, Burtt N, Carey J, Charpentier G, Crenshaw AT, Doney AS, Dorkhan<br>M, Edkins S, Emilsson V, Eury E, Forsen T, Gertow K, Gigante B, Grant GB, Groves <br>CJ, Guiducci C, Herder C, Hreidarsson AB, Hui J, James A, Jonsson A, Rathmann W, <br>Klopp N, Kravic J, Krjutškov K, Langford C, Leander K, Lindholm E, Lobbens S,<br>Männistö S, Mirza G, Mühleisen TW, Musk B, Parkin M, Rallidis L, Saramies J,<br>Sennblad B, Shah S, Sigurðsson G, Silveira A, Steinbach G, Thorand B, Trakalo J, <br>Veglia F, Wennauer R, Winckler W, Zabaneh D, Campbell H, van Duijn C,<br>Uitterlinden AG, Hofman A, Sijbrands E, Abecasis GR, Owen KR, Zeggini E, Trip MD,<br>Forouhi NG, Syvänen AC, Eriksson JG, Peltonen L, Nöthen MM, Balkau B, Palmer CN, <br>Lyssenko V, Tuomi T, Isomaa B, Hunter DJ, Qi L; Wellcome Trust Case Control<br>Consortium; Meta-Analyses of Glucose and Insulin-related traits Consortium<br>(MAGIC) Investigators; Genetic Investigation of ANthropometric Traits (GIANT)<br>Consortium; Asian Genetic Epidemiology Network–Type 2 Diabetes (AGEN-T2D)<br>Consortium; South Asian Type 2 Diabetes (SAT2D) Consortium, Shuldiner AR, Roden<br>M, Barroso I, Wilsgaard T, Beilby J, Hovingh K, Price JF, Wilson JF, Rauramaa R, <br>Lakka TA, Lind L, Dedoussis G, Njølstad I, Pedersen NL, Khaw KT, Wareham NJ,<br>Keinanen-Kiukaanniemi SM, Saaristo TE, Korpi-Hyövälti E, Saltevo J, Laakso M,<br>Kuusisto J, Metspalu A, Collins FS, Mohlke KL, Bergman RN, Tuomilehto J, Boehm<br>BO, Gieger C, Hveem K, Cauchi S, Froguel P, Baldassarre D, Tremoli E, Humphries<br>SE, Saleheen D, Danesh J, Ingelsson E, Ripatti S, Salomaa V, Erbel R, Jöckel KH, <br>Moebus S, Peters A, Illig T, de Faire U, Hamsten A, Morris AD, Donnelly PJ,<br>Frayling TM, Hattersley AT, Boerwinkle E, Melander O, Kathiresan S, Nilsson PM,<br>Deloukas P, Thorsteinsdottir U, Groop LC, Stefansson K, Hu F, Pankow JS, Dupuis<br>J, Meigs JB, Altshuler D, Boehnke M, McCarthy MI; DIAbetes Genetics Replication<br>And Meta-analysis (DIAGRAM) Consortium.<br><br>To extend understanding of the genetic architecture and molecular basis of type 2<br>diabetes (T2D), we conducted a meta-analysis of genetic variants on the<br>Metabochip, including 34,840 cases and 114,981 controls, overwhelmingly of<br>European descent. We identified ten previously unreported T2D susceptibility<br>loci, including two showing sex-differentiated association. Genome-wide analyses <br>of these data are consistent with a long tail of additional common variant loci<br>explaining much of the variation in susceptibility to T2D. Exploration of the<br>enlarged set of susceptibility loci implicates several processes, including<br>CREBBP-related transcription, adipocytokine signaling and cell cycle regulation, <br>in diabetes pathogenesis.<br><br>DOI: 10.1038/ng.2383 <br>PMCID: PMC3442244<br>PMID: 22885922  [PubMed - indexed for MEDLINE]</div>";
    this.layout.panels[0].dashboard.components.push({
            type: "menu",
            color: "yellow",
            position: "right",
            button_html: "Study Abstract",
            menu_html: abstract
    });
    
    // prepare the layout for SNP highlighting (own code)
    console.log('this.layout.panels[0].data_layers[2].point_shape: ' + JSON.stringify(this.layout.panels[0].data_layers[2].point_shape));
    console.log('this.layout.panels[0].data_layers[2].color: ' + JSON.stringify(this.layout.panels[0].data_layers[2].point_shape));
    console.log('this.layout.panels[0].data_layers[2]..point_size: ' + JSON.stringify(this.layout.panels[0].data_layers[2].point_shape));
    this.layout.panels[0].data_layers[2].point_shape = [{}, this.layout.panels[0].data_layers[2].point_shape];
    this.layout.panels[0].data_layers[2].color = [{}, this.layout.panels[0].data_layers[2].color];
    this.layout.panels[0].data_layers[2].point_size = [{}, this.layout.panels[0].data_layers[2].point_size];
    

    // Populate a list of top hits links for the plot
    var top_hits = [
      ["16:53819169", "FTO"],
      ["9:22051670", "CDKN2A/B"],
      ["7:28196413", "JAZF1"],
      ["12:71433293", "TSPAN8"],
      ["10:114758349", "TCF7L2"],
      ["8:95937502", "TP53INP1"],
      ["6:20679709", "CDKAL1"],
      ["2:161346447", "RBMS1"],
      ["16:75247245", "BCAR1"],
      ["15:77832762", "HMG20A"],
      ["7:15052860", "DGKB"]
    ];
    top_hits.forEach(function(hit){
      d3.select("ul.top_hits").append("li")
            .html("<a href=\"javascript:void(0);\" onclick=\"javascript:jumpTo('" + hit[0] + "');\">" + hit[1] + "</a>");
    });

    // end of code by University of Michigan Center for Statistical Genetics



    this.createRegionString = function (regionObject) {
        
        var chromosome = regionObject['chromosome'];
        var position =  parseInt(regionObject['position']);

        console.log('chromosome: ' + chromosome + ', position: ' + position);
        var rangeWidth;
        if (regionObject['width'] != null) {
            rangeWidth =  parseInt(regionObject['width']);
        }
        else {
            //rangeWidth = 517230;
            rangeWidth = 20000;
        }

        var rangeBegin = position - rangeWidth;
        var rangeEnd = position + rangeWidth;

        var newDataregion = chromosome + ':' + rangeBegin + '-' + rangeEnd;

        return newDataregion;        
    };

    this.setRegion = function (newRegion) {         
        if (newRegion != null){
            //element.innerHTML = '<div id="' + divID + '" data-region="10:114550452-115067678"
            var chromosome = newRegion['chromosome'];
            var position =  parseInt(newRegion['position']);
            
            console.log('chromosome: ' + chromosome + ', position: ' + position);
            var rangeWidth;
            if (newRegion['width'] != null) {
                rangeWidth =  parseInt(newRegion['width']);
            }
            else {
                //rangeWidth = 517230;
                rangeWidth = 20000;
            }
                    
            var rangeBegin = position - rangeWidth;
            var rangeEnd = position + rangeWidth;
            
            var newDataregion = chromosome + ':' + rangeBegin + '-' + rangeEnd;
            
            console.log('new data region: ' + newDataregion);
            
            //var regex = new RegExp('data-region="[^ ]*"');
            
            //console.log('replaced: ' + element.innerHTML.replace(regex, 'data-region="' + newDataregion + '"'));
            
            if (this.firstSNP) {
                this.populatPlot();
            }

            
            this.highlightSNP(position);

            
            //console.log('this.layout.panels[0].data_layers[2]: ' + JSON.stringify(this.layout.panels[0].data_layers[2]));
            
            //element.innerHTML = element.innerHTML.replace(regex, 'data-region="' + newDataregion + '"');'
            
            
            
            //console.log('this.layout.panels[0].data_layers[2].color: ' + JSON.stringify(this.layout.panels[0].data_layers[2].color));
            //console.log('this.layout.panels[0].data_layers[2].point_shape[0]: ' + JSON.stringify(this.layout.panels[0].data_layers[2].point_shape));
            
//            for (var property in plot.layout) {
//                if (plot.layout.hasOwnProperty(property)) {
//                    console.log(`plot.layout[${property}]: ` + plot.layout[property]);
//                }
//            } 

//            plot.on("data_requested", function(eventData){
//                console.log("data requested for LocusZoom plot" + this.id);
//                console.log('eventData: ' + JSON.stringify(eventData));
//            });            


            // Add a basic loader to each panel (one that shows when data is requested and hides when one rendering)
            this.plot.layout.panels.forEach(function(panel){
              self.plot.panels[panel.id].addBasicLoader();
            });
            
            this.plot.applyState({ chr : chromosome, start: rangeBegin, end: rangeEnd, ldrefvar: "" });
            //console.log('element.innerHTML: ' + element.innerHTML);
        }
    };
    
    this.populatPlot = function() {
        console.log('creating plot');
        // creating the LocusZoom plot
        this.plot = LocusZoom.populate('#' + divID, this.data_sources, this.layout);
        this.firstSNP = false;
        console.log('plot: ' + this.plot);
        
        
        this.plot.on("element_clicked", function(eventData){
            console.log("element clicked: " + this.id + ", source ID: " + eventData.sourceID);
            //console.log('eventData: ' + JSON.stringify(eventData));
            if (eventData.sourceID == 'locuszoom.association') {
                console.log('SNP clicked.');
                console.log('eventData: ' + JSON.stringify(eventData));
            }
            else if (eventData.sourceID == 'locuszoom.genes') {
                console.log('gene clicked.');
                console.log('start of gene data ...\n');
                for (var property in eventData.data) {
                    if (eventData.data.hasOwnProperty(property)) {
                        console.log(`object.data[${property}]: ` + eventData.data[property]);
                    }
                } 
                console.log('\n... end of gene data');
            }

            self.highlightSNP(eventData.data['assoc:position']);
            connector.registerSNPclick(eventData.data);

        });
        
    };
    
    
    this.highlightSNP = function (position) {                
        // point shape for current SNP
        
        console.log('highlighting position: ' + position);
        
        var newPointShapeFunction = {
            "scale_function" : "if",
            "field":"assoc:position",
            "parameters": {
                "field_value": position,
                "then": "cross"
            }
        };
        this.layout.panels[0].data_layers[2].point_shape[0] = newPointShapeFunction;

        // colour the current SNP
        var newColourFunction = {
            scale_function  : "if",
            field : "assoc:position",
            parameters: {
                "field_value" : position,
                "then" : "#ff0000"
            }
        };

        this.layout.panels[0].data_layers[2].color[0] = newColourFunction;


        // make the current SNP larger
        var newPointPointSizeFunction = {
            scale_function : 'if',
            field : 'assoc:position',
            parameters: {
                field_value: position,
                then: 90
            }
        };

        this.layout.panels[0].data_layers[2].point_size[0] = newPointPointSizeFunction;

        //this.plot.plot.refresh();
        
        //console.log('this.layout.state: ' + JSON.stringify(this.layout.state));
        this.plot.applyState(this.layout.state); // force a redrawing of the plot to ensure immediate highlighting
    };
    
};