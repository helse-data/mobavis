package com.main;

import com.utils.Constants;
import com.utils.JsonHelper;
import com.utils.UtilFunctions;
import com.utils.Variable;
import elemental.json.Json;
import elemental.json.JsonObject;
import java.util.Arrays;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author Christoffer Hjeltnes Støle
 */
public class SNP {    
    String ID;
    Constants constants = new Constants();
    String[] ageVariables = constants.getAgeVariables();
    String chromosome;
    String position;
    Map <String, String> sexMap = constants.getSexMap();
    UtilFunctions utilFunctions = new UtilFunctions();
    
    JsonObject dataObject;
    
    public SNP(String ID, String chromosome, String position, String data) {
        if (ID.contains(":")) {
            this.ID = "?";
        }
        else {
            this.ID = ID;
        }        
        this.chromosome = chromosome;
        this.position = position;
        
        if (data != null) {
            handleData(data);
        }        
    }
    
    private void handleData(String data) {
        dataObject = Json.createObject();
        
        //System.out.println("handleData(): " + data);
        
        String [] lines = data.split("\n");
        
        //System.out.println("lines: " + Arrays.toString(lines));
        
        for (String line : lines) {
            String [] columns = line.split("\t");
            //System.out.println("columns: " + Arrays.toString(columns));
            String rawPhenotype = columns[0];
            String sex = sexMap.get(columns[1]);
            String genotype = columns[2];
            String statistic = columns[3];
            String value = columns[4];
            String phenotype = "";
            String age = "";
            boolean longitudinal = false;
            
            Pattern pattern = Pattern.compile("(.*?)(Birth|[0-9-]+(w|m|y))");
            Matcher patternMatch = pattern.matcher(rawPhenotype);
            if (patternMatch.find()) {
                longitudinal = true;
                phenotype = patternMatch.group(1);
                age = patternMatch.group(2).toLowerCase();
//                System.out.println("phenoype: " + phenotype + ", age: " + age);
            }
            else {
                phenotype = rawPhenotype;
            }
            
            phenotype = new Variable(phenotype).getDisplayName();
            statistic = new Variable(statistic).getDisplayName();
            if (longitudinal && statistic.equals("50%")) {
                statistic = "median";
            }

            if (!dataObject.hasKey(phenotype)) {
                dataObject.put(phenotype, Json.createObject());
                for (String sexKey : new String [] {"female", "male"}) {
                    dataObject.getObject(phenotype).put(sexKey, Json.createObject());
                    for (String genotypeKey : new String [] {"AA", "AB", "BB"}) {
                        dataObject.getObject(phenotype).getObject(sexKey).put(genotypeKey, Json.createObject());
                    }
                }
                dataObject.getObject(phenotype).put("longitudinal", longitudinal);
            }
            
            if (longitudinal) {
//                System.out.println("Objects here: " + Arrays.toString(new JsonObject[] {
//                    dataObject.getObject(phenotype).getObject(sex),
//                    dataObject.getObject(phenotype).getObject(sex).getObject(genotype)}));
                if (!dataObject.getObject(phenotype).getObject(sex).getObject(genotype).hasKey(statistic)) {
                    dataObject.getObject(phenotype).getObject(sex).getObject(genotype).put(statistic, Json.createArray());
                }
                
                int index = dataObject.getObject(phenotype).getObject(sex).getObject(genotype).getArray(statistic).length();
                dataObject.getObject(phenotype).getObject(sex).getObject(genotype).getArray(statistic).set(index, value);
            }
            else {
                dataObject.getObject(phenotype).getObject(sex).getObject(genotype).put(statistic, value);
            }
        }
        //System.out.println("data object: " + dataObject.toJson()); // TODO: await
        //System.out.println("data object: " + new JsonHelper().stringify(dataObject));
    }

    
    public boolean hasData() {
        return dataObject != null;
    }
    
    public JsonObject getDataObject() {
        return dataObject;
    }
    
    public Json queryData(String query) {
        if (dataObject == null) {
            return null;
        }
        String[] args = query.split(" ");
        //System.out.println(query);
        //System.out.println(genotypes.get(args[0]).get(args[1]));
        return dataObject.getObject(args[0]).getObject(args[1]).getObject(args[2]).get(args[3]);
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
