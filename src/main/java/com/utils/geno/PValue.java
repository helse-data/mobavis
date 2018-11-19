package com.utils.geno;

/**
 * 
 * Stores information about a p-value entry
 *
 * @author Christoffer Hjeltnes Støle
 */
public class PValue  implements Comparable <PValue> {    
    String line;
    String [] splitLine;
    String rawValue;
    
    int CHROMOSOME = 0;
    int POSITION = 1;
    int NAME = 4;
    int P_VALUE = 9;

    public PValue() {
    }
    
    /**
     * 
     * @param line - the line the text file for the p-value
     */
    public PValue(String line) {
        this.line = line;
        this.splitLine = line.split("\t");
        
        rawValue = splitLine[splitLine.length-1];        
    }
    
    /**
     * Returns the p-value.
     * 
     * @return 
     */
    public double getValue() {
        return Double.parseDouble(rawValue);
    }
    /**
     * Returns the negative of the base-10 logarithm of the p-value.
     * 
     * @return 
     */
    public double getMinusLogValue() {
        return -Math.log10(getValue());
    }
    /**
     * Returns the chromosome of the SNP of the p-value.
     * @return 
     */
    public String getChromosome() {
        return splitLine[CHROMOSOME];
    }
    /**
     * Returns the position of the SNP of the p-value.
     * @return 
     */
    public String getPosition() {
        return splitLine[POSITION];
    }
    /**
     * Returns the ID of the SNP of the p-value.
     * @return 
     */
    public String getSNPname() {
        return splitLine[NAME];
    }
    /**
     * Returns the line for the p-value in the text file.
     * @return 
     */
    public String getLine() {
        return line;
    }
    
    /**
     * Whether a p-value was actually stored in the file for the SNP.
     * 
     * @return 
     */
    public boolean exists() {
        return !rawValue.equals("NA");        
    }

    @Override
    public int compareTo(PValue other) {
        return Double.compare(getValue(), other.getValue());
    }
    
    @Override
    public String toString() {
        return rawValue;
    }    
}
