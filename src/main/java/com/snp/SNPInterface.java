package com.snp;

/**
 *
 * Interface for the SNP and InputSNP classes. Guarantees interoperability for the 
 * key functions relating to ID, chromosome and position.
 * 
 * @author Christoffer Hjeltnes Støle
 */
public interface SNPInterface {    
    public String getID();
    public String getChromosome();
    public String getPosition();    
}
