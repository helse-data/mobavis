package com.snp;

/**
 *
 * Interface for the SNP and InputSNP classes. Guarantees interoperability for the 
 * key functions relating to ID, chromosome and position.
 * 
 * @author Christoffer Hjeltnes Støle
 */
public interface SNP {
    /**
     * Returns the input ID.
     * 
     * @return 
     */
    public String getID();
    /**
     * Returns the provided chromosome.
     * 
     * @return 
     */
    public String getChromosome();
    /**
     * Returns the input position.
     * 
     * @return 
     */
    public String getPosition();    
}
