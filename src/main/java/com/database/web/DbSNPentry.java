package com.database.web;

import elemental.json.JsonArray;
import elemental.json.JsonObject;
import elemental.json.impl.JreJsonFactory;

/**
 *
 * DbSNPentry parses and stores data returned from dbSNP.
 * 
 * @author Christoffer Hjeltnes Støle
 */
public class DbSNPentry {
    private JsonObject queryObject;
    private String SNPID;
    private String locusID;
    private String locus;
    private String locusFullName;
    
    /**
     * 
     * 
     * @param response - the response from dbSNP
     */
    public DbSNPentry (String response) {        
        JreJsonFactory jreJsonFactory = new JreJsonFactory();
        queryObject = jreJsonFactory.parse(response);
        SNPID = queryObject.getString("refsnp_id");
    }

    /**
     * Parses the response from dbSNP and stores it.
     * 
     */
    private void parseResponse() {
        JsonObject primary_snapshot_data = queryObject.getObject("primary_snapshot_data");
            
            JsonArray allele_annotations = primary_snapshot_data.getArray("allele_annotations");
            
            //System.out.println("allele_annotations: " + allele_annotations.toJson());
            
            
            JsonArray assembly_annotation = allele_annotations.getObject(0).getArray("assembly_annotation");
            
            System.out.println("assembly_annotation: " + assembly_annotation.toJson());
            
            JsonArray genes = assembly_annotation.getObject(0).getArray("genes");
            
            for (int i = 0; i < genes.length(); i++) {
                JsonObject geneObject = genes.get(i);
                System.out.println("\n\ngeneObject " + i + ": \n");
                System.out.println(geneObject.toJson());
                
                JsonArray sequenceOntology = geneObject.getArray("sequence_ontology");
                
                if (sequenceOntology.length() >= 1) {
                    System.out.println("\nsequence ontology: " + sequenceOntology.getObject(0).getString("name"));
                }
                else {
                    System.out.println("\nsequence ontology (RNA): " + geneObject.getArray("rnas").getObject(0).getArray("sequence_ontology").getObject(0).getString("name"));
                }
                
                locusID = Integer.toString((int) geneObject.getNumber("id"));
                locusFullName = geneObject.getString("name");
                locus = geneObject.getString("locus");
            } 
        
    }
    
    /**
     * Returns the ID of the SNP.
     * 
     * @return 
     */
    public String getSNPID() {
        return SNPID;
    }
    
    /**
     * 
     * Returns the ID of the locus provided by dbSNP.
     * 
     * @return 
     */
    public String getLocusID() {
        return locusID;
    }
    
    /**
     * Returns the full name of the locus of the SNP.
     * 
     * @return 
     */
    public String getLocusFullName () {
        if (locusFullName == null) {
            parseResponse();
        }
        return locusFullName;        
    }
    
    /**
     * Returns the short name of the locus of the SNP.
     * 
     * @return 
     */
    public String getLocus() {
        if (locus == null) {
            parseResponse();
        }
        return locus;
    }
    
    /**
     * Returns the URL of the entry.
     * 
     * @return 
     */
    public String getEntryURL() {
        return "https://www.ncbi.nlm.nih.gov/snp/rs" + getSNPID();
    }
    
}
