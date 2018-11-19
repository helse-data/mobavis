package com.snp;

/**
 * 
 * Class to store SNP input. The data stored here (ID, position, chromosome or a combination of them) 
 * are not verified and not guaranteed to correspond to any real SNP.
 * This is in contrast to the SNP class, which should only contain information from verified sources.
 *
 * @author Christoffer Hjeltnes Støle
 */
public class InputSNP implements SNP {
    String ID;
    String chromosome;
    String position;
    Boolean verificationFailed;
    
    /**
     * 
     * @param ID - input ID
     * @param chromosome
     * @param position 
     */
    public InputSNP(String ID, String chromosome, String position) {
        this.ID = ID;
        this.chromosome = chromosome;
        this.position= position;
    }
    /**
     * 
     * @param chromosome
     * @param position 
     */
    public InputSNP(String chromosome, String position) {
        this.chromosome = chromosome;
        this.position= position;
    }
    /**
     * 
     * @param ID 
     */
    public InputSNP(String ID) {
        this.ID = ID;
    }
    
    /**
     * Whether the SNP was found in the database system.
     * 
     * @return 
     */
    public Boolean verificationFailed() {
        return verificationFailed;
    }

    @Override
    public String getID() {
        return ID;
    }
    @Override
    public String getChromosome() {
        return chromosome;
    }
    @Override
    public String getPosition() {
        return position;
    }
    
    @Override
    public String toString() {
      return "Input SNP with ID \"" + getID() + "\"";  
    }
    
    
}
