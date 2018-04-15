package com.files;

import com.vaadin.server.VaadinService;
import elemental.json.Json;
import elemental.json.JsonObject;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.GZIPInputStream;

/**
 *
 * @author Christoffer Hjeltnes Støle
 */
public class ReadAnnotation {
    final String basePath = VaadinService.getCurrent().getBaseDirectory().getAbsolutePath() + "\"/../../../../server/data/new_annotation/";
    
    public JsonObject readAnnotationFile(String chromosome, int [] columns) {
        //Map <Integer, List <String>> result = new HashMap();
        JsonObject result = Json.createObject();
        for (Integer column : columns) {
            //result.put(column, new ArrayList());
            result.put(column.toString(), Json.createArray());
        }
        
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
                    result.getArray(column.toString()).set(result.getArray(column.toString()).length(), splitLine[column]);
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
        
        return result;
    }
    
}
