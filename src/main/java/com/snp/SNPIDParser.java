/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.snp;

/**
 *
 * @author Christoffer Hjeltnes Støle
 */
public class SNPIDParser {
    String rsID;
    String chromosome;
    SNPIDFormat IDFormat;
    String position;
    String ref;
    String alt;
    
    public enum SNPIDFormat {
        RSID("rs ID"), // https://www.ncbi.nlm.nih.gov/books/NBK44417/#Content.what_is_a_reference_snp_or__rs_i
        CHROMOSOME_POSITION("chromosome:position"),
        CHROMOSOME_POSITION_REF_ALT("chromosome__position_ref_alt"),
        UNRECOGNIZED("unrecognized");
        
        private final String displayName;        
     
        SNPIDFormat(String displayName) {
            this.displayName = displayName;
        }
        
        @Override
        public String toString() {
            return displayName;
        }
    }
    
    public SNPIDParser(String ID) {
        if (ID.matches("^rs\\d+")) {
            IDFormat = SNPIDFormat.RSID;
            System.out.println("SNP query format: " + IDFormat);
            
            rsID = ID;
        }
        else if (ID.contains(":")) { // SNP entered in format chromosome:position
            IDFormat = SNPIDFormat.CHROMOSOME_POSITION;
            System.out.println("SNP query format: " + IDFormat);
            
            String [] split = ID.split(":");
            chromosome = split[0];
            position = split[1].replace(" ", "");
        }
        else if (ID.matches("\\d+_.*")){
            IDFormat = SNPIDFormat.CHROMOSOME_POSITION_REF_ALT;
            System.out.println("SNP query format: " + IDFormat);
            String[] split = ID.split("_");

            chromosome = split[0];
            position = split[1];
            ref = split[2];
            alt = split[3];
        }
        else {
            IDFormat = SNPIDFormat.UNRECOGNIZED;
            System.out.println("Unknown ID format.");
        }        
    }
    public SNPIDFormat getIDFormat() {
        return IDFormat;
    }
    public String getRsID() {
        return rsID;
    }
        public String getChromosome() {
        return chromosome;
    }

    public String getPosition() {
        return position;
    }
    public String getRef() {
        return ref;
    }

    public String getAlt() {
        return alt;
    }
}