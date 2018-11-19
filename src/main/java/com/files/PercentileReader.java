package com.files;

import com.utils.Age;
import com.utils.Alphanumerical;
import com.utils.Constants;
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
 * PercentileReader reads the percentile data (without genotyping information)
 * stored for the continuous variables.
 * 
 *
 * @author Christoffer Hjeltnes Støle
 */
public class PercentileReader {    
    String columnSeparator = "\t";
    
    String sharedPath;
    
    String[] ages;
    UtilFunctions utilFunctions = new UtilFunctions();
    Constants constants = new Constants();
    Age ageUtils = new Age();
    
    Set <String> longitudinalPhenotypes = new HashSet();
    Set <String> nonLongitudinalPhenotypes = new HashSet();
    
    Map <Variable, List <String>> conditionDataLists = new HashMap();
    Map <Variable, Map <Variable, List <Alphanumerical>>> conditions = new HashMap();
    
    Map <String, Map <String, List <String[]>>> nonLongitudinalData;
    Map <String, Map <String, Map <String, List <String[]>>>> longitudinalData;
    
    Map <String, String> textToNumberSexMap = constants.getTextToNumberSexMap();
    Map <String, String> numberToTextSexMap = constants.getNumberToTextSexMap();
    
    List <Alphanumerical> percentileList; // sorted list of percentiles
    List <Alphanumerical> percentileTextList;
    
    final String CONDITIONAL_DATA_TEXT_FILE = "population_conditioned.txt";
    
    public PercentileReader() {
        ages = constants.getAgesShort(); 
       
        sharedPath = VaadinService.getCurrent().getBaseDirectory().getAbsolutePath();
        sharedPath = sharedPath + "/../../../../server/data";
        
    }
    
    /**
     * 
     * Reads and parses non-conditional data.
     * 
     */
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
    
    /**
     * 
     * Returns a JSON object with non-conditional phenotype data.
     * 
     * @param phenotype
     * @param sexAsText
     * @return 
     */
    public JsonObject getNonConditionalPhenotypeData(Variable phenotype, String sexAsText) {
        String sexAsNumber = textToNumberSexMap.get(sexAsText);
        if (longitudinalData == null) {
            readWholePopulation();
        }
        if (longitudinalData == null) {
            return null;
        }
        //System.out.println("sex: " + sex);
        System.out.println("phenotype: " + phenotype);
        //System.out.println("longitudinalData: " + longitudinalData);
        JsonObject object = Json.createObject();
        object.put("phenotype", phenotype.getDisplayName());
        JsonObject dataObject = Json.createObject();
        if (phenotype.isLongitudinal()) {         
            for (String percentile : longitudinalData.get(phenotype.getName()).get(sexAsNumber).keySet()) {
                List <String[]> rawData = longitudinalData.get(phenotype.getName()).get(sexAsNumber).get(percentile);
                dataObject.put(percentile, Json.createObject());
                for (String[] rawDataList : rawData) {
                    String age = rawDataList[0];
                    String value = rawDataList[1];
                    dataObject.getObject(percentile).put(age, value);
                    //object.put(rawDataList[0], rawDataList[1]);
                }
            }
            dataObject = convertLongitudinalDataToXYArrays(dataObject);
            object.put("data", dataObject);
        }
        else {
            //System.out.println("nonLongitudinalData: " + nonLongitudinalData);
            List <String[]> rawData = nonLongitudinalData.get(phenotype.getName()).get(sexAsNumber);
            
            for (String[] rawDataList : rawData) {
                dataObject.put(rawDataList[0], rawDataList[1]);
            }            
            dataObject = convertNonLongitudinalDataToXYArrays(dataObject);
        }
        object.put("data", dataObject);
        object.put("sex", sexAsText);
        return object;
    }
    
    /**
     * 
     * Returns a JSON object with conditional phenotype data.
     * 
     * @param phenotype
     * @param sexAsText
     * @param conditionCategory
     * @param condition
     * @return 
     */
    public JsonObject getConditionalPhenotypeData(Variable phenotype, String sexAsText, Variable conditionCategory, Alphanumerical condition) {
        String sexAsNumber = textToNumberSexMap.get(sexAsText);
        if (!conditionDataLists.containsKey(phenotype)) {
            ScanFile(CONDITIONAL_DATA_TEXT_FILE, phenotype);
        }
        
        
        System.out.println("conditional data");
        System.out.println("sex: " + sexAsNumber);
        System.out.println("phenotype: " + phenotype);
        JsonObject object = Json.createObject();
        object.put("phenotype", phenotype.getDisplayName());
        object.put("sex", sexAsText);
        JsonObject dataObject;
        
        if (phenotype.isLongitudinal()) {
            dataObject = extractLongitudinalConditionalData(phenotype, sexAsNumber, conditionCategory, condition);
        }
        else {
            dataObject = extractNonLongitudinalConditionalData(phenotype, sexAsNumber, conditionCategory, condition);
            System.out.println("dataObject: " + dataObject);
            dataObject = convertNonLongitudinalDataToXYArrays(dataObject);
        }       
        object.put("data", dataObject);
        return object;
    }
    
    /**
     * Parses longitudinal conditional data.
     * 
     * @param phenotype
     * @param sex
     * @param conditioncategory
     * @param condition
     * @return 
     */
    private JsonObject extractLongitudinalConditionalData(Variable phenotype, String sex, Variable conditioncategory, Alphanumerical condition) {
        Map <String, Map <String, String>> dataMap = new HashMap();
        Set <String> percentileSet = new HashSet();
        List <String> ageList = new ArrayList();
        JsonArray N = Json.createArray();
        for (String line : conditionDataLists.get(phenotype)) { // initial parsing of the data
            String [] splitLine = line.split(columnSeparator);
            if (splitLine[1].equals(sex) && splitLine[2].equals(conditioncategory.getName()) && splitLine[3].equals(condition.getValue())) {                
                String age = splitLine[0].replace(phenotype.getName(), "");
                if (!ageList.contains(age)) { // keep track of all ages with values
                        ageList.add(age);
                }
                if (!dataMap.containsKey(age)) {
                    dataMap.put(age, new HashMap());
                }
                if (splitLine[5].equals("N")) {
                    //dataMap.get(age).put(splitLine[5], splitLine[6]);
                    N.set(N.length(), splitLine[6]);
                }
                else {
                    String percentile = splitLine[5];
                    percentileSet.add(percentile); // keep track of all percentiles with values
                    dataMap.get(age).put(percentile, splitLine[6]);
                }
            }
        }
        //System.out.println("ageList: " + ageList);
        //System.out.println("dataMap: " + dataMap);
        // go through the result of the initial parsing of the data and implicitly check for missing values
        JsonObject object = Json.createObject();
        JsonObject nullValues = null;
        int i = 0;
        
        Map <String, Integer> nullIndexStart = new HashMap(); // the last index of a continuous line of null values from the beginning
        Map <String, Integer> nullIndexEnd = new HashMap(); // the first index of a continuous line of null values to the end
        for (String percentile : percentileSet) {
            object.put(percentile, Json.createArray());
            nullIndexStart.put(percentile, -2);
            nullIndexEnd.put(percentile, -2);
        }
        
        for (String age : ageList) {
            //System.out.println("age: " + age);
            
            for (String percentile : percentileSet) {
                //System.out.println("percentile: " + percentile);
                if (dataMap.get(age).containsKey(percentile)) {
                    object.getArray(percentile).set(object.getArray(percentile).length(), dataMap.get(age).get(percentile));
                    nullIndexEnd.put(percentile, -2); // can be no continous line of null values to the end with containing index
                }
                else { // have no value for this combination
                    if (i - nullIndexStart.get(percentile) == 1) {
                        nullIndexStart.put(percentile, i);                        
                    }
                    else if (nullIndexEnd.get(percentile) < 0) { // first null value found that does not form a continuous line from the start
                        nullIndexEnd.put(percentile, i);
                    }
                    object.getArray(percentile).set(object.getArray(percentile).length(), Json.createNull());
                    if (nullValues == null) {
                        nullValues = Json.createObject();
                    }
                }
            }
            i++;
        }
        object.put("N", N);
        
        
        for (String percentile : percentileSet) {
            if (nullIndexStart.get(percentile) > 0 || nullIndexEnd.get(percentile) > 0) {
                if (nullValues == null) {
                    nullValues = Json.createObject();
                }
                JsonArray array = Json.createArray();
                array.set(0, nullIndexStart.get(percentile));
                array.set(1, nullIndexEnd.get(percentile));
                nullValues.put(percentile, array);
            }
        }
        
        if (nullValues == null) {
            object.put("null values", Json.createNull());
        }
        else {
            object.put("null values", nullValues);
        }
        System.out.println("null values: " + nullValues);
        //System.out.println("N: " + N.toJson());
        object.put("sex", numberToTextSexMap.get(sex));
        return object;
    }
    
    /**
     * Parses non-longitudinal conditional data.
     * 
     * @param phenotype
     * @param sex
     * @param conditioncategory
     * @param condition
     * @return 
     */
    private JsonObject extractNonLongitudinalConditionalData(Variable phenotype, String sex, Variable conditioncategory, Alphanumerical condition) {
        JsonObject object = Json.createObject();
        
        for (String line : conditionDataLists.get(phenotype)) {
            String [] splitLine = line.split(columnSeparator);
            if (splitLine[1].equals(sex) && splitLine[2].equals(conditioncategory.getName()) && splitLine[3].equals(condition.getValue())) {
                object.put(splitLine[5], splitLine[6]);
            }
        }
        object.put("sex", numberToTextSexMap.get(sex));
        //System.out.println("object: " + object.toJson());
        return object;
    }
    
    /**
     * Prepares non-longitudinal data for plotting.
     * 
     * @param oldObject
     * @return 
     */
    private JsonObject convertNonLongitudinalDataToXYArrays (JsonObject oldObject) {
        //System.out.println("object: " + oldObject.toJson());
        JsonObject newObject = Json.createObject();
        
        JsonArray xArray = Json.createArray();
        JsonArray yArray = Json.createArray();
        
        for (int i = 0; i < percentileList.size(); i++) {
            String percentile = percentileList.get(i).toString() + "%";
            xArray.set(i, percentile);
            yArray.set(i, oldObject.getString(percentile));
        }
        newObject.put("N", oldObject.getString("N"));
        newObject.put("x", xArray);
        newObject.put("y", yArray);
        //System.out.println("newObject: " + newObject.toJson());
        return newObject;
    }
    
    /**
     * Prepares longitudinal data for plotting.
     * 
     * @param oldObject
     * @return 
     */
    private JsonObject convertLongitudinalDataToXYArrays (JsonObject oldObject) {
        JsonObject newObject = Json.createObject();

        for (String percentile : oldObject.keys()) {
            JsonArray array = Json.createArray();

            int i = 0;            
            for (String age : ages) {
                //System.out.println(age + " " + percentile);
                array.set(i, oldObject.getObject(percentile).getString(age));
                i++;
            }
            newObject.put(percentile, array);
        }
        return newObject;
    }
    
    /**
     * Returns a list of the percentiles as strings.
     * 
     * @return 
     */
    public List <String> getPercentileTextList() {
        List <String> list = new ArrayList();
        for (Alphanumerical percentile : getPercentiles()) {
            list.add(percentile.toString() +"%");
        }
        return list;
    }
    
    /**
     * 
     * Returns a list of percentiles for which statistics were generated.
     * 
     * @return 
     */
    public List <Alphanumerical> getPercentiles() {
        if (percentileList == null) {
            percentileList = new ArrayList();
            if (longitudinalData == null) {
                readWholePopulation();
            }
            if (longitudinalData == null) { // something went wrong
                return null;
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
    
    /**
     * Returns a set with all longitudinal phenotypes.
     * 
     * @return 
     */
    public Set <String> getLongitudinalPhenotypes() {
        if (longitudinalData == null) {
            readWholePopulation();
        }
        return longitudinalPhenotypes;
    }
    
    /**
     * Returns a set with all non-longitudinal phenotypes.
     * 
     * @return 
     */
    public Set <String> getNonLongitudinalPhenotypes() {
        if (nonLongitudinalData == null) {
            readWholePopulation();
        }
        return nonLongitudinalPhenotypes;
    }
    
    /**
     * Returns a set with all phenotypes, longitudinal and non-longitudinal.
     * 
     * @return 
     */
    public Set <String> getPhenotypes() {
        Set <String> allPhenoypes = new HashSet();
        allPhenoypes.addAll(getLongitudinalPhenotypes());
        allPhenoypes.addAll(getNonLongitudinalPhenotypes());
        return allPhenoypes;
    }
    
    /**
     * 
     * Returns a set with all condition categories.
     * 
     * @param phenotype
     * @return 
     */
    public Set <Variable> getConditionCategories(Variable phenotype) {
        if (!conditions.containsKey(phenotype)) {
            if (!conditionDataLists.containsKey(phenotype)) {
                ScanFile(CONDITIONAL_DATA_TEXT_FILE, phenotype);
            }
            List <String> dataList = conditionDataLists.get(phenotype);
            Set <String> conditionStrings = new HashSet();
            if (dataList == null) {
                return null;
            }
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
    /**
     * Returns a list with all condition values.
     * 
     * @param phenotype
     * @param conditionCategory
     * @return 
     */
    public List <Alphanumerical> getConditions(Variable phenotype, Variable conditionCategory) {
        if (!conditions.containsKey(phenotype)) {
            PercentileReader.this.getConditionCategories(phenotype);
        }
        if (conditions.get(phenotype).get(conditionCategory) == null) {
            List <String> dataList = conditionDataLists.get(phenotype);
            Set <String> conditionSet = new HashSet();
            for (String line : dataList) {
                String[] splitLine = line.split(columnSeparator);
                conditionSet.add(splitLine[3]);
            }
            List <Alphanumerical> conditionList = new ArrayList();
            conditionSet.forEach(conditionString -> conditionList.add(new Alphanumerical(conditionString)));
            conditions.get(phenotype).put(conditionCategory, conditionList);
        }
        
        return conditions.get(phenotype).get(conditionCategory);
    }
    
    /**
     * Adds all lines of a file containing data on the given phenotype into an ArrayList object.
     * 
     * @param fileName
     * @param phenotype 
     */
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
