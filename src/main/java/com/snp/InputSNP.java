package com.snp;

/**
 * 
 * Class to store SNP input. The data stored here (ID, position and chromosome) 
 *  are not verified and not guaranteed to correspond to any real SNP.
 *
 * @author Christoffer Hjeltnes Støle
 */
public class InputSNP implements SNPInterface {
    String ID;
    String chromosome;
    String position;
    
    public InputSNP(String ID, String chromosome, String position) {
        this.ID = ID;
        this.chromosome = chromosome;
        this.position= position;
    }
    public InputSNP(String chromosome, String position) {
        this.chromosome = chromosome;
        this.position= position;
    }
    public InputSNP(String ID) {
        this.ID = ID;
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
    
    
}
