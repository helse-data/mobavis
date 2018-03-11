window.com_locuszoom_LocusZoom =
	function() {
		// Create the component
		var component =
			new main.Component(this.getElement());
	
                var booleanVersions = {
                    region            : true
                };
                
                var firstData = true;

		this.onStateChange = function() {                    
                    var region = this.getState().region;
                    if (region != null) {
                        console.log('region: ' + JSON.stringify(region));                        
                        if (booleanVersions['region'] != region['boolean version'] || firstData) {
                            component.setRegion(region);
                            console.log('New region provided.');
                            booleanVersions['region'] = region['boolean version'];

                        }
                    };
                    firstData = false;
                };
	};