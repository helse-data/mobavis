/*
 * Copyright (c) 2016 - now David Sehnal, licensed under Apache 2.0. Code adapted and modfied from original.
 */
var simpleController = simpleController || {};

simpleController.Component = function (element) {
    const divID = 'litemol';
    element.innerHTML = '<div id="' + divID + '">';
    
    var currentEntryID;
    
    var plugin = LiteMol.Plugin.create({
        target: '#' + divID,
        viewportBackground: '#fff',
        layoutState: {
            hideControls: true,
            isExpanded: false
        },
        allowAnalytics: true
    });
    
    
    this.setEntryID = function (entryIDObject) {
        var entryID = entryIDObject['ID'];
        
        if (entryID == currentEntryID) {
            return;
        }
        
        var currentMoleculeList = plugin.context.select(currentEntryID + '-model');
        //console.log('currentMoleculeList: ' + currentMoleculeList);
        //console.log('currentMoleculeList.length: ' + currentMoleculeList.length);
        
        console.log('currentEntryID: ' + currentEntryID);
        if (currentMoleculeList.length > 0) {
            var toRemove = currentMoleculeList[0].parent;
            //LiteMol.Bootstrap.Command.Entity.SetVisibility.dispatch(plugin.context, {entity: currentMolecule, visible: false});
            //LiteMol.Visualization.ModelStore.removeAndDispose(currentMolecule);
            LiteMol.Bootstrap.Command.Tree.RemoveNode.dispatch(toRemove.tree.context, toRemove);
            
            //? <Controls.Button title='Remove' onClick={() => Bootstrap.Command.Tree.RemoveNode.dispatch(entity.tree!.context, entity) } icon='remove' style='link' customClass='lm-entity-tree-entry-remove' />
        }
        
        plugin.loadMolecule({
            id: entryID,
            format: 'cif',
            url: "https://www.ebi.ac.uk/pdbe/static/entry/" + entryID.toLowerCase() + "_updated.cif",
            // instead of url, it is possible to use
            // data: "string" or ArrayBuffer (for BinaryCIF)
            // loaded molecule and model can be accessed after load
            // using plugin.context.select(modelRef/moleculeRef)[0],
            // for example plugin.context.select('1tqn-molecule')[0]
            moleculeRef: entryID + '-molecule',
            modelRef: entryID + '-model'
        }).then(function () {
            // Use this (or a modification of this) for custom visualization:
            // const style = LiteMol.Bootstrap.Visualization.Molecule.Default.ForType.get('BallsAndSticks');  
            // const t = plugin.createTransform();
            // t.add(id + '-model', LiteMol.Bootstrap.Entity.Transformer.Molecule.CreateVisual, { style: style })
            // plugin.applyTransform(t);
            console.log('Molecule loaded');
            currentEntryID = entryID;
        }).catch(function (e) {
            console.error(e);
        }); 
    };

};