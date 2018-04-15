package com.main;

import com.utils.Constants;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author ChristofferHjeltnes
 */
public class SNPOld {
    
    String ID;
    Constants constants = new Constants();
    String[] ageVariables = constants.getAgeVariables();
    //ResultSet resultSet;
    String chromosome;
    String position;
    
    Map <String, Map <String, Map <String, Map <String,
            List <String>>>>> genotypes;
    
    public SNPOld(String ID, String chromosome, String position) {
        this.ID = ID;
        this.chromosome = chromosome;
        this.position = position;
        //this.resultSet = resultSet;
        //build();
    }
    
    
    public void setData(ResultSet resultSet) {
        genotypes = new HashMap();
        genotypes.put("AA", new HashMap());
        genotypes.put("AB", new HashMap());
        genotypes.put("BB", new HashMap());
        
        for (String genotype : new String[] {"AA", "AB", "BB"}) {        
            for (String attribute : new String[] {"height", "weight", "BMI"}) {
                genotypes.get(genotype).put(attribute, new HashMap());
                for (String sex : new String [] {"female", "male"}) {
                    genotypes.get(genotype).get(attribute).put(sex, new HashMap());                    
                }
            }   
        }
        
        try {
            ID = resultSet.getString("id");
                        
            while (resultSet.next()) {
                String genotype = resultSet.getString("snp");
                String attribute = resultSet.getString("pheno");
                String sex = resultSet.getString("sex").toLowerCase();
                if (!attribute.equals("BMI")) {
                    attribute = attribute.toLowerCase();
                }
                String statistic = resultSet.getString("stat");
                if (statistic.equals("Median")) {
                    statistic = statistic.toLowerCase();
                }
                //System.out.println(genotype + " " + attribute + " " + statistic);
                if (!genotypes.get(genotype).get(attribute).get(sex).containsKey(statistic)) {
                    genotypes.get(genotype).get(attribute).get(sex).put(statistic, new ArrayList());
                }
                for (String age : ageVariables) {
                    genotypes.get(genotype).get(attribute).get(sex).get(statistic).add(resultSet.getString(age));
                    //totalData.get(dataName).add(resultSet.getString(age));
                    //System.out.println(resultSet.getString(age));
                }
                //index++;
            }
        }
        catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        //System.out.println("genotypes: " + genotypes);
    }
    
    public boolean hasData() {
        return genotypes != null;
    }
    
    public List <String> getData(String query) {
        if (genotypes == null) {
            return null;
        }
        String[] args = query.split(" ");
        //System.out.println(query);
        //System.out.println(genotypes.get(args[0]).get(args[1]));
        return genotypes.get(args[0]).get(args[1]).get(args[2]).get(args[3]);
    }
    
    public String getID() {
        return ID;
    }
    public String getChromosome() {
        return chromosome;
    }
    public String getPosition() {
        return position;
    }
    
}
