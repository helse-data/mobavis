package com.database;

import com.utils.Constants;
import com.utils.Variable;
import elemental.json.Json;
import elemental.json.JsonObject;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author Christoffer Hjeltnes Støle
 */
public class SNPDatabaseEntry {
    Constants constants = new Constants();
    Map <String, String> annotation;
    String chromosome;
    String position;
    Map <String, String> numberToSexMap = constants.getNumberToTextSexMap();
    JsonObject dataObject;
    
    
//     public SNPDatabaseEntry() {
//         
//     }
//    
//    public SNPDatabaseEntry(Map <String, String> annotation) {
//        this.annotation = annotation;
//    }
    
    public SNPDatabaseEntry(String rawDataString, Map <String, String> annotation) {
        System.out.println("annotation: " + annotation);
        this.annotation = annotation;
        dataObject = Json.createObject();
        
        //System.out.println("handleData(): " + data);
        
        if (rawDataString != null) {
            parseRawData(rawDataString);
        }        
    }
    
    private void parseRawData(String rawDataString) {
        String [] lines = rawDataString.split("\n");
        
        //System.out.println("lines: " + Arrays.toString(lines));
        
        for (String line : lines) {
            String [] columns = line.split("\t");
            //System.out.println("columns: " + Arrays.toString(columns));
            String rawPhenotype = columns[0];
            String sex = numberToSexMap.get(columns[1]);
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
    
    public Json queryData(String query) {
        if (dataObject == null) {
            return null;
        }
        String[] args = query.split(" ");
        //System.out.println(query);
        //System.out.println(genotypes.get(args[0]).get(args[1]));
        return dataObject.getObject(args[0]).getObject(args[1]).getObject(args[2]).get(args[3]);
    }
    
    public  Map <String, String> getDatabaseSNPID () {
        return annotation;
    }
    
    public Optional <String> getChromosome() {
        return Optional.ofNullable(chromosome);
    }
    public Optional <String> getPosition() {
        return Optional.ofNullable(position);
    }
    
    public JsonObject getDataObject() {
        return dataObject;
    }
    public boolean hasData() {
        return dataObject != null;
    }
    public boolean hasAnnotation() {
        return annotation != null;
    }
    public Map <String, String> getAnnotation() {
        return annotation;
    }
    
}
