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
    
    public PValue(String line) {
        this.line = line;
        this.splitLine = line.split("\t");
        
        rawValue = splitLine[splitLine.length-1];        
    }
    
    public double getValue() {
        return Double.parseDouble(rawValue);
    }
    
    public double getMinusLogValue() {
        return -Math.log10(getValue());
    }
    
    public String getChromosome() {
        return splitLine[CHROMOSOME];
    }
    public String getPosition() {
        return splitLine[POSITION];
    }
    public String getSNPname() {
        return splitLine[NAME];
    }
    
    public String getLine() {
        return line;
    }
    
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
