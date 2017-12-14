package com.utils;

/**
 *
 * @author ChristofferHjeltnes
 */
public class MoBaChromosome {
    Alphanumerical name;
    Alphanumerical numberOfSNPs;
    
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