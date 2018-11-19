package com.mobaextraction;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * Table objects represent tables stored in memory-mapped files.
 * 
 *
 * @author Christoffer Hjeltnes Støle
 */
public class Table {
    int [] data;
    List <Integer> femaleValues;
    List <Integer> maleValues;
    List <String> femaleLabels;
    List <String> maleLabels;    

    List <String> labels;
    public Table() {
    }
    
    boolean stratifiedBySex;
    
    /**
     * 
     * @param data
     * @param labels
     * @param stratifiedBySex - whether or not the data is stratified by sex
     */
    public Table(int [] data, List <String> labels, boolean stratifiedBySex) {
        this.data = data;
        this.labels = labels;
        this.stratifiedBySex = stratifiedBySex;
        
        if (stratifiedBySex) {
            femaleValues = new ArrayList();
            maleValues = new ArrayList();
            femaleLabels = new ArrayList();
            maleLabels = new ArrayList();
            for (int i = 0; i < data.length; i++) {
                String label = labels.get(i);
                if (label.contains("female")) {
                    femaleValues.add(data[i]);
                    femaleLabels.add(label.replace("female: ", ""));
                }
                else if (label.contains("male")) {
                    maleValues.add(data[i]);
                    maleLabels.add(label.replace("male: ", ""));
                }
            }
        }        
    }
    
    /**
     * Returns whether or not the data is stratified by sex.
     * 
     * @return 
     */
    public boolean isStratifiedBySex() {
        return stratifiedBySex;
    }
    /**
     * Returns labels for the values of a phenotype.
     * 
     * @return 
     */
    public List<String> getLabels() {
        return labels;
    }
    /**
     * Returns the data stored in the table.
     * 
     * @return 
     */
    public int[] getData() {
        return data;
    }
    
    /**
     * Returns the data stored on females.
     * 
     * @return 
     */
    public List <Integer> getFemaleValues () {
        return femaleValues;
    }
    /**
     * Returns the data stored on males.
     * @return 
     */
    public List <Integer> getMaleValues () {
        return maleValues;
    }
    
    /**
     * 
     * Returns the labels for the values stored on females.
     * 
     * @return 
     */
    public List <String> getFemaleLabels() {
        return femaleLabels;
    }
    /**
     * Returns the labels for the values stored on males.
     * 
     * @return 
     */
    public List <String> getMaleLabels() {
        return maleLabels;
    }
    
    /**
     * 
     * Returns the data for the given sex.
     * 
     * @param sex
     * @return 
     */
    public List <Integer> getData(String sex) {
        if (sex.equals("female")) {
            return getFemaleValues();
        }
        else if (sex.equals("male")) {
            return getMaleValues();
        }
        return null;
    } 
    
    /**
     * 
     * Returns the labels for the for the values stored on the given sex.
     * 
     * @param sex
     * @return 
     */
    public List <String> getLabels(String sex) {
        if (sex.equals("female")) {
            return getFemaleLabels();
        }
        else if (sex.equals("male")) {
            return getMaleLabels();
        }
        return null;
    }    
}
