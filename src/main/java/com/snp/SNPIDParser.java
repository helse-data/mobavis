package com.snp;

/**
 * 
 * SNPIDParser parses the input string to identify a SNP,
 * such as rsID or chromosome and position.
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
    
    /**
     * The formats recognized.
     * 
     */
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
    
    /**
     * 
     * @param ID - the input to parse
     */
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
    /**
     * Returns the ID format.
     * 
     * @return 
     */
    public SNPIDFormat getIDFormat() {
        return IDFormat;
    }
    /**
     * Returns the rsID.
     * 
     * @return 
     */
    public String getRsID() {
        return rsID;
    }
    /** 
     * Returns the chromosome.
     * 
     * @return 
     */
    public String getChromosome() {
        return chromosome;
    }

    /**
     * Returns the position.
     * 
     * @return 
     */
    public String getPosition() {
        return position;
    }
    /**
     * Returns the reference allele.
     * 
     * @return 
     */
    public String getRef() {
        return ref;
    }

    /**
     * Returns the alternative allele.
     * 
     * @return 
     */
    public String getAlt() {
        return alt;
    }
}