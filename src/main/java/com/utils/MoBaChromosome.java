package com.utils;

/**
 * MoBaChromosome stores information relating to MoBa for a give chromosome.
 *
 * @author ChristofferHjeltnes
 */
public class MoBaChromosome {
    Alphanumerical name;
    Alphanumerical numberOfSNPs;
    
    /**
     * 
     * @param name - name of the chromosome
     * @param numberOfSNPs - number of SNP present in MoBa data
     */
    public MoBaChromosome(String name, int numberOfSNPs) {
        this.name = new Alphanumerical(name);
        this.numberOfSNPs = new Alphanumerical(Integer.toString(numberOfSNPs));
    }
    
    public Alphanumerical getNumberOfSNPs() {
        return numberOfSNPs;
    }
       
    public Alphanumerical getName() {
        return name;
    }
}