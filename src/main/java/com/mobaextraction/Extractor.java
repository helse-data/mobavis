package com.mobaextraction;

import com.utils.Constants;
import java.io.File;
import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import no.uib.mobaextraction.api.PhenotypeSummaryProvider;

/**
 *
 * @author Christoffer Hjeltnes Støle
 */
public class Extractor {
    PhenotypeSummaryProvider phenotypeProvider;
    Constants constants = new Constants();
    int NO_VALUE = 1;
    int MALE = 2;
    int FEMALE = 3;
    
    public Extractor() {
        try {
            String path = constants.getServerPath() + "/data/V10_28.05.18/";
            // The pheno and index files
            File phenoFile = new File(path + "phenoSummary");
            File indexFile = new File(path + "phenoSummary.index.gz");

            System.out.println(Instant.now() + " - Parsing index");

            // Initialize the PhenotypeSummaryProvider
            phenotypeProvider = PhenotypeSummaryProvider.wrap(phenoFile, indexFile);

        } catch (Exception e) {
            System.out.println(e);
        }
    }
    
    public String [] getTables() {        
        String[] phenoTables = phenotypeProvider.phenoNames;

        System.out.println(Instant.now() + " - " + phenoTables.length + " phenotype tables loaded.");

        return phenoTables;
    }
    

    
    public int [] getTableData(String table) {        
        try {
            return phenotypeProvider.getPhenoTable(table);
        } catch (IOException e) {
            System.out.println(e);
        }        
        return null;
    }
    
    public List <String> getTableLevels(String tableName) {
        return phenotypeProvider.getLevels(tableName);
    }
    
    public List <String> getTableLabels(String tableName) {
        List <String> labels = new ArrayList();
        List <String> levels = getTableLevels(tableName);
        
        int [] table = getTableData(tableName);
        
        if (table.length == levels.size() + 1) {
            labels.add("no value");
            
            for (int i = 1; i < table.length; i++) {                
                labels.add(levels.get(i - 1).toLowerCase());
            }
        } 
        else if (table.length == 2 * (levels.size() + 1)) {
            labels.add("male: no value");

            for (int i = 1; i < table.length / 2; i++) {
                labels.add("male: " + levels.get(i - 1).toLowerCase());

            }
            labels.add("female: no value");

            for (int i = 1; i < table.length / 2; i++) {
                labels.add("female: " + levels.get(i - 1).toLowerCase());
            }
        }
        
        return labels;
    }
    
    
    
    public Table getTable(String name) {        
        List <String> labels = new ArrayList();
        List <String> levels = getTableLevels(name);
        boolean isStratifiedBySex = false;
        
        int [] tableData = getTableData(name);
        
        if (tableData.length == levels.size() + 1) {
            labels.add("no value");
            
            for (int i = 1; i < tableData.length; i++) {                
                labels.add(levels.get(i - 1).toLowerCase());
            }
        } 
        else if (tableData.length == 2 * (levels.size() + 1)) {
            isStratifiedBySex = true;
            labels.add("male: no value");

            for (int i = 1; i < tableData.length / 2; i++) {
                labels.add("male: " + levels.get(i - 1).toLowerCase());

            }
            labels.add("female: no value");

            for (int i = 1; i < tableData.length / 2; i++) {
                labels.add("female: " + levels.get(i - 1).toLowerCase());
            }
        }
        
        return new Table(tableData, labels, isStratifiedBySex);
        
    }
    
}
