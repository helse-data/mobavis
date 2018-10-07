package com.files;

import com.utils.Constants;
import com.utils.geno.PValue;
import elemental.json.Json;
import elemental.json.JsonArray;
import elemental.json.JsonObject;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Christoffer Hjeltnes Støle
 */
public class PValueReader {
    
    
    public JsonObject read() {
        JsonObject chromosomes = Json.createObject();
        Constants constants = new Constants();
        
        
        try {    
            String filePath = constants.getServerPath() + "data/p_values/bmi_p-values";

            InputStream inputStream = new FileInputStream(filePath);
            Reader reader = new InputStreamReader(inputStream);
            BufferedReader bufferedReader = new BufferedReader(reader);

            String line = bufferedReader.readLine(); // read the headers
            line = bufferedReader.readLine(); // read the first data line
            
            
            String [] listOfChromosomes = constants.getChromosomeList();
            int [] chromosomeSizes = constants.getChromosomeSizes();
            
            Map <String, Long> chromosomeStartPositions = new HashMap();
            long currentPlotStartPosition = 0;
            
            for (int i = 0; i < listOfChromosomes.length; i++) {
                JsonObject chromosomeObject = Json.createObject();
                chromosomeObject.put("chromosome_positions", Json.createArray());
                chromosomeObject.put("plot_positions", Json.createArray());
                chromosomeObject.put("p_values", Json.createArray());
                chromosomeObject.put("p_value_labels", Json.createArray());
                chromosomeObject.put("names", Json.createArray());
                chromosomes.put(listOfChromosomes[i], chromosomeObject);
                chromosomeStartPositions.put(listOfChromosomes[i], currentPlotStartPosition);
                currentPlotStartPosition += chromosomeSizes[i];
            }
            
            System.out.println("plotStartPositions: " + chromosomeStartPositions);
            
            while(line != null) {    
                
                PValue pValue = new PValue(line);
                //System.out.println("numberOfPValues: " + numberOfPValues);
                if (pValue.exists()) {
                    int pValuesForChromosome = chromosomes.getObject(pValue.getChromosome()).getArray("chromosome_positions").length();
                    chromosomes.getObject(pValue.getChromosome()).getArray("chromosome_positions").set(pValuesForChromosome, pValue.getPosition());
                    chromosomes.getObject(pValue.getChromosome()).getArray("plot_positions").set(pValuesForChromosome,
                            Long.toString(chromosomeStartPositions.get(pValue.getChromosome()) + Long.parseLong(pValue.getPosition())));
                    chromosomes.getObject(pValue.getChromosome()).getArray("p_values").set(pValuesForChromosome, pValue.getMinusLogValue());
                    chromosomes.getObject(pValue.getChromosome()).getArray("p_value_labels").set(pValuesForChromosome, pValue.getValue());
                    chromosomes.getObject(pValue.getChromosome()).getArray("names").set(pValuesForChromosome, pValue.getSNPname());
                }
                line = bufferedReader.readLine();
                
            }
            inputStream.close();
            reader.close();
            bufferedReader.close();
        }
        catch (IOException e) {
            System.out.println(e);
        }

        System.out.println("chromosomes.getObject(pValue.getChromosome()).getArray(\"chromosome_positions\").length(): " + chromosomes.getObject("1").getArray("chromosome_positions").length());
        System.out.println("chromosomes.getObject(pValue.getChromosome()).getArray(\"plot_positions\").length(): " + chromosomes.getObject("1").getArray("plot_positions").length());
        System.out.println("chromosomes.getObject(pValue.getChromosome()).getArray(\"p_values\").length(): " + chromosomes.getObject("1").getArray("p_values").length());
        System.out.println("chromosomes.getObject(pValue.getChromosome()).getArray(\"names\").length(): " + chromosomes.getObject("1").getArray("names").length());
        
        return chromosomes;
        
    }
    
}
