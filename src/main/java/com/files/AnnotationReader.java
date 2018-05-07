package com.files;

import com.vaadin.server.VaadinService;
import elemental.json.Json;
import elemental.json.JsonArray;
import elemental.json.JsonObject;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.GZIPInputStream;

/**
 *
 * @author Christoffer Hjeltnes Støle
 */
public class AnnotationReader {
    final String basePath = VaadinService.getCurrent().getBaseDirectory().getAbsolutePath() + "\"/../../../../server/data/new_annotation/";
    
    // reads the given columns of the annotation file for the given chromosome
    public Map <String, JsonArray> readAnnotationFile(String chromosome, int [] columns) {
        System.out.println("Reading annotation for chromosome " + chromosome);
        //Map <Integer, List <String>> result = new HashMap();
        JsonObject result = Json.createObject();
        JsonObject result2 = Json.createObject();
        
        Map <String, JsonArray> columnData = new HashMap();
        
        for (Integer column : columns) {
            //result.put(column, new ArrayList());
             //result.put(column.toString(), Json.createArray());        
             columnData.put(column.toString(), Json.createArray());
        }
        //System.out.println("result: " + result.toJson());
        //System.out.println("result2: " + result2.toJson());
        try {
            InputStream inputStream = new FileInputStream(basePath + "snp_annotation_" + chromosome + ".gz");
            InputStream gzipStream = new GZIPInputStream(inputStream);
            Reader reader = new InputStreamReader(gzipStream);
           
            BufferedReader bufferedReader = new BufferedReader(reader);
            
            String line = bufferedReader.readLine(); // read the header
            line = bufferedReader.readLine();
            int snps = 0;
            while (line != null) {
                snps++;
                String [] splitLine = line.split("\t");
                for (Integer column : columns) {
                    columnData.get(column.toString()).set(columnData.get(column.toString()).length(), splitLine[column]);
                    //result.getArray(column.toString()).set(result.getArray(column.toString()).length(), splitLine[column]);                 
                    //result.get(column).add(splitLine[column]);
                }
                line = bufferedReader.readLine();
                //System.out.println("line: " + line);
            }
            System.out.println("SNPs: " + snps);
            result.put("SNPs", snps);
        }
        catch (Exception e) {
            System.out.println(e);
        }
        //System.out.println("testArray: " + Arrays.toString(testArray));
        return columnData;
    }
    
}
