package com.mobaextraction;

import java.util.ArrayList;
import java.util.List;

/**
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
        
    public boolean isStratifiedBySex() {
        return stratifiedBySex;
    }
    public List<String> getLabels() {
        return labels;
    }
    public int[] getData() {
        return data;
    }
    
    public List <Integer> getFemaleValues () {
        return femaleValues;
    }
    public List <Integer> getMaleValues () {
        return maleValues;
    }
    
    public List <String> getFemaleLabels() {
        return femaleLabels;
    }

    public List <String> getMaleLabels() {
        return maleLabels;
    }
    
    public List <Integer> getData(String sex) {
        if (sex.equals("female")) {
            return getFemaleValues();
        }
        else if (sex.equals("male")) {
            return getMaleValues();
        }
        return null;
    } 
    
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
