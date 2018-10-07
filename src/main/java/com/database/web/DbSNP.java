package com.database.web;

import elemental.json.JsonObject;
import elemental.json.impl.JreJsonFactory;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;

/**
 *
 * @author Christoffer Hjeltnes Støle
 */
public class DbSNP {
    
    String QUERY_URL_START = "https://api.ncbi.nlm.nih.gov/variation/v0/beta/refsnp/";
    
    
    public DbSNPentry getEntry (String rsID) {
        String query = QUERY_URL_START + rsID;
        StringBuilder stringBuilder = new StringBuilder("");
        
        System.out.println("dbSNP query: " + query);
        
        try {
            URL oracle = new URL(query);
            BufferedReader in = new BufferedReader(new InputStreamReader(oracle.openStream()));

            String inputLine;
            while ((inputLine = in.readLine()) != null)
                //System.out.println(inputLine);
                stringBuilder.append(inputLine);
            in.close();
            
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
        }
        
        DbSNPentry dbSNPentry = new DbSNPentry(stringBuilder.toString());

        
        return dbSNPentry;        
    }
    
}
