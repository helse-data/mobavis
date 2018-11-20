/**
 *
 * Connector function for LiteMol.
 *
 */
window.com_litemol_LiteMol =
	function() {
		// Create the component
		var component =
			new simpleController.Component(this.getElement());
	
                var booleanVersions = {
                    'entry ID'  : true
                };
                
                var firstData = true;

		this.onStateChange = function() {                    
                    var entryID = this.getState().entryID;
                    
                    if (entryID != null) {
                        console.log('ID: ' + JSON.stringify(entryID));                        
                        if (booleanVersions['entry ID'] != entryID['boolean version'] || firstData) {
                            console.log('New entry ID provided.');
                            component.setEntryID(entryID);                            
                            booleanVersions['entry ID'] = entryID['boolean version'];
                        }
                    };
                    firstData = false;
                };
	};