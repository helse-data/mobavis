package com.plotting;

import com.utils.Age;
import com.utils.Alphanumerical;
import com.utils.UtilFunctions;
import com.utils.Variable;
import com.vaadin.server.VaadinService;
import elemental.json.Json;
import elemental.json.JsonArray;
import elemental.json.JsonObject;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 *
 * @author Christoffer Hjeltnes Støle
 */
public class PercentileReader {    
    String columnSeparator = "\t";
    
    String sharedPath;
    
    String[] ages;
    UtilFunctions utilFunctions = new UtilFunctions();
    Age ageUtils = new Age();
    
    Set <String> longitudinalPhenotypes = new HashSet();
    Set <String> nonLongitudinalPhenotypes = new HashSet();
    
    Map <Variable, List <String>> conditionDataLists = new HashMap();
    Map <Variable, Map <Variable, List <Alphanumerical>>> conditions = new HashMap();
    
    Map <String, Map <String, List <String[]>>> nonLongitudinalData;
    Map <String, Map <String, Map <String, List <String[]>>>> longitudinalData;
    
    Map <String, String> sexMap = new HashMap();
    
    List <Alphanumerical> percentileList;
    List <Alphanumerical> percentileTextList;
    
    final String CONDITIONAL_DATA_TEXT_FILE = "population_conditioned.txt";
    
    public PercentileReader() {
        ages = new String[] {"birth", "6w", "3m", "6m", "8m", "1y", "15-18m", "2y", "3y", "5y", "7y", "8y"}; // constants.getAgesShort();
        sexMap.put("female", "1");
        sexMap.put("male", "2");
        
        sharedPath = VaadinService.getCurrent().getBaseDirectory().getAbsolutePath();
        sharedPath = sharedPath + "/../../../../server/data";
        
    }

    public void readWholePopulation(){
        System.out.println("Reading whole-population summary statistics.");
        
        try {
            InputStream inputStream = new FileInputStream(sharedPath + "/summary_statistics/population.txt");
            Reader reader = new InputStreamReader(inputStream);
            BufferedReader bufferedReader = new BufferedReader(reader);
            String[] headers = bufferedReader.readLine().split(columnSeparator);
            String line;
            String phenotype = "";
            String age = "";
            boolean isLongitudinal;
            longitudinalData = new HashMap();
            nonLongitudinalData = new HashMap();
            while ((line = bufferedReader.readLine()) != null) {
                //System.out.println(line);
                String[] splitLine = line.split(columnSeparator);
                String phenotypeNameRaw = splitLine[0];
                String sex = splitLine[1];
                String percentile = splitLine[2];
                if (phenotypeNameRaw.endsWith("Birth")) { // longitudinal phenotype
                    age = "birth";
                    isLongitudinal = true;
                    phenotype = phenotypeNameRaw.replace("Birth", "");
                    longitudinalPhenotypes.add(phenotype);
                }
                else {
                    Pattern pattern = Pattern.compile(".*?(\\d{1,2}[wmy0-9-]+)");
                    Matcher match = pattern.matcher(splitLine[0]);
                    if (match.find()) { // time point entry
                        isLongitudinal = true;
                         age = match.group(1);
                    }
                    else { // non-longitudinal phenotype
                        isLongitudinal = false;
                        phenotype = phenotypeNameRaw;
                        nonLongitudinalPhenotypes.add(phenotype);
                    }                    
                }
                
                if (isLongitudinal) {
                    if (!longitudinalData.containsKey(phenotype)) {
                        longitudinalData.put(phenotype, new HashMap());
                        longitudinalData.get(phenotype).put("1", new HashMap());
                        longitudinalData.get(phenotype).put("2", new HashMap());
                    }
                    if (!longitudinalData.get(phenotype).get(sex).containsKey(percentile)) {
                        longitudinalData.get(phenotype).get(sex).put(percentile, new ArrayList());
                    }
                    
                    longitudinalData.get(phenotype).get(sex).get(percentile).add(new String[] {age, splitLine[3]});
                    //longitudinalData.get(phenotype).get(sex).get(age).add(Arrays.copyOfRange(splitLine, 2, splitLine.length));
                    
                }
                else {
                    if (!nonLongitudinalData.containsKey(phenotype)) {
                        nonLongitudinalData.put(phenotype, new HashMap());
                        nonLongitudinalData.get(phenotype).put("1", new ArrayList());
                        nonLongitudinalData.get(phenotype).put("2", new ArrayList());
                    }
                    

                    nonLongitudinalData.get(phenotype).get(sex).add(Arrays.copyOfRange(splitLine, 2, splitLine.length));
                }
                
            }
            
            inputStream.close();
            reader.close();
            bufferedReader.close();
        }
        catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }
    
    
//    public JsonObject getPhenotypeData(Variable phenotype, String sex, Variable conditionCategory, Alphanumerical condition) {
//        return null;
//    }
    
    
    public JsonObject getNonConditionalPhenotypeData(String phenotype, String sex) {
        sex = sexMap.get(sex);
        if (longitudinalData == null) {
            readWholePopulation();
        }
        //System.out.println("sex: " + sex);
        System.out.println("phenotype: " + phenotype);
        //System.out.println("longitudinalData: " + longitudinalData);
        JsonObject object = Json.createObject();
        object.put("phenotype", phenotype);
        JsonObject dataObject = Json.createObject();
        if (longitudinalData.containsKey(phenotype)) {         
            for (String percentile : longitudinalData.get(phenotype).get(sex).keySet()) {
                List <String[]> rawData = longitudinalData.get(phenotype).get(sex).get(percentile);
                dataObject.put(percentile, Json.createObject());
                for (String[] rawDataList : rawData) {
                    String age = rawDataList[0];
                    String value = rawDataList[1];
                    dataObject.getObject(percentile).put(age, value);
                    //object.put(rawDataList[0], rawDataList[1]);
                }
            }
            dataObject = createLongitudinalPlotFriendlyObject(dataObject);
            object.put("data", dataObject);
        }
        else {            
            List <String[]> rawData = nonLongitudinalData.get(phenotype).get(sex);
            
            for (String[] rawDataList : rawData) {
                dataObject.put(rawDataList[0], rawDataList[1]);
            }            
            //dataObject = createNonLongitudinalPlotFriendlyObject(dataObject);
        }        
        object.put("data", dataObject);
        return object;
    }
    
    public JsonObject getConditionalPhenotypeData(Variable phenotype, String sex, Variable conditionCategory, Alphanumerical condition) {
        sex = sexMap.get(sex);
        if (!conditionDataLists.containsKey(phenotype)) {
            ScanFile(CONDITIONAL_DATA_TEXT_FILE, phenotype);
        }
        
        
        System.out.println("conditional data");
        System.out.println("sex: " + sex);
        System.out.println("phenotype: " + phenotype);
        JsonObject object = Json.createObject();
        object.put("phenotype", phenotype.getDisplayName());
        JsonObject dataObject;
        
        if (phenotype.isLongitudinal()) {
            dataObject = extractLongitudinalConditionalData(phenotype, sex, conditionCategory, condition);
        }
        else {
            dataObject = extractNonLongitudinalConditionalData(phenotype, sex, conditionCategory, condition);
        }       
        object.put("data", dataObject);
        return object;
    }
    
    private JsonObject extractLongitudinalConditionalData(Variable phenotype, String sex, Variable conditioncategory, Alphanumerical condition) {
        JsonObject object = Json.createObject();
        for (String line : conditionDataLists.get(phenotype)) {
            String [] splitLine = line.split(columnSeparator);
            if (splitLine[1].equals(sex) && splitLine[2].equals(conditioncategory.getName()) && splitLine[3].equals(condition.getValue())) {
                String percentile = splitLine[5];

                if (!object.hasKey(percentile)) {
                    object.put(percentile, Json.createArray());
                }
                object.getArray(percentile).set(object.getArray(percentile).length(), splitLine[6]);
            }
        }
        //System.out.println("object: " + object.toJson());
        return object;
    }
    private JsonObject extractNonLongitudinalConditionalData(Variable phenotype, String sex, Variable conditioncategory, Alphanumerical condition) {
        JsonObject object = Json.createObject();
        
        for (String line : conditionDataLists.get(phenotype)) {
            String [] splitLine = line.split(columnSeparator);
            if (splitLine[1].equals(sex) && splitLine[2].equals(conditioncategory.getName()) && splitLine[3].equals(condition.getValue())) {
                object.put(splitLine[5], splitLine[6]);
            }
        }
        //System.out.println("object: " + object.toJson());
        return object;
    }
    
    
    private JsonObject createLongitudinalPlotFriendlyObject (JsonObject object) {
        JsonObject newObject = Json.createObject();

        for (String percentile : object.keys()) {
            JsonArray array = Json.createArray();

            int i = 0;            
            for (String age : ages) {
                //System.out.println(age + " " + percentile);
                array.set(i, object.getObject(percentile).getString(age));
                i++;
            }
            newObject.put(percentile, array);
        }
        return newObject;
    }
    
    public List <String> getPercentileTextList() {
        List <String> list = new ArrayList();
        for (Alphanumerical percentile : getPercentiles()) {
            list.add(percentile.toString() +"%");
        }
        return list;
    }
    
    public List <Alphanumerical> getPercentiles() {
        if (percentileList == null) {
            percentileList = new ArrayList();
            if (longitudinalData == null) {
                readWholePopulation();
            }
            for (String percentile : longitudinalData.get(longitudinalData.keySet().iterator().next()).get("1").keySet()) {
                if (!percentile.equals("N")) {
                    percentileList.add(new Alphanumerical(percentile.replace("%", "")));
                }            
            }
            Collections.sort(percentileList);
        }
        return percentileList;
    }
    
    
    public Set <String> getLongitudinalPhenotypes() {
        if (longitudinalData == null) {
            readWholePopulation();
        }
        return longitudinalPhenotypes;
    }
    
    public Set <String> getNonLongitudinalPhenotypes() {
        if (nonLongitudinalData == null) {
            readWholePopulation();
        }
        return nonLongitudinalPhenotypes;
    }
    
    public Set <String> getPhenotypes() {
        Set <String> allPhenoypes = new HashSet();
        allPhenoypes.addAll(getLongitudinalPhenotypes());
        allPhenoypes.addAll(getNonLongitudinalPhenotypes());
        return allPhenoypes;
    }
    
    public Set <Variable> getConditionCategories(Variable phenotype) {
        if (!conditions.containsKey(phenotype)) {
            if (!conditionDataLists.containsKey(phenotype)) {
                ScanFile(CONDITIONAL_DATA_TEXT_FILE, phenotype);
            }
            List <String> dataList = conditionDataLists.get(phenotype);
            Set <String> conditionStrings = new HashSet();

            for (String line : dataList) {
                 String[] splitLine = line.split(columnSeparator);
                 String conditionPhenotype = splitLine[2];
                 conditionStrings.add(conditionPhenotype);
            }
            Map <Variable, List <Alphanumerical>> conditionMap = new HashMap();
            conditionStrings.forEach(conditionString -> conditionMap.put(new Variable(conditionString), null));
            conditions.put(phenotype, conditionMap);
        }
        return conditions.get(phenotype).keySet();    
    }
    
    public List <Alphanumerical> getConditions(Variable phenotype, Variable condition) {
        if (!conditions.containsKey(phenotype)) {
            PercentileReader.this.getConditionCategories(phenotype);
        }
        if (conditions.get(phenotype).get(condition) == null) {
            List <String> dataList = conditionDataLists.get(phenotype);
            Set <String> conditionSet = new HashSet();
            for (String line : dataList) {
                String[] splitLine = line.split(columnSeparator);
                conditionSet.add(splitLine[3]);
            }
            List <Alphanumerical> conditionList = new ArrayList();
            conditionSet.forEach(conditionString -> conditionList.add(new Alphanumerical(conditionString)));
            conditions.get(phenotype).put(condition, conditionList);
        }
        
        return conditions.get(phenotype).get(condition);
        
     
    }
    
    private void ScanFile(String fileName, Variable phenotype) {
        try {
            InputStream inputStream = new FileInputStream(sharedPath + "/summary_statistics/" + fileName);
            Reader reader = new InputStreamReader(inputStream);
            BufferedReader bufferedReader = new BufferedReader(reader);
            String[] headers = bufferedReader.readLine().split(columnSeparator);
            String line;
            List <String> dataList = new ArrayList();

            while ((line = bufferedReader.readLine()) != null) {
                //System.out.println(line);
                String[] splitLine = line.split(columnSeparator);

                if (splitLine[0].startsWith(phenotype.getType())) {
                    dataList.add(line);
                }
            }
            System.out.println("File scanned.");
            
            inputStream.close();
            reader.close();
            bufferedReader.close();
            conditionDataLists.put(phenotype, dataList);
            
        }
        catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }
    
    
}
