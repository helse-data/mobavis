package com.database.web;

import elemental.json.JsonObject;
import elemental.json.impl.JreJsonFactory;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;

/**
 * 
 * DbSNP queries dbSNP for data.
 *
 * @author Christoffer Hjeltnes Støle
 */
public class DbSNP {    
    String QUERY_URL_START = "https://api.ncbi.nlm.nih.gov/variation/v0/beta/refsnp/";
    
    /**
     * 
     * Gets a dbSNP entry by rsID.
     * 
     * @param rsID
     * @return 
     */
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
            System.out.println(e);
            return null; // better handling?
        }
        
        String response = stringBuilder.toString();
        //System.out.println("Response from dbSNP:" + response);
        
//        if (response.equals("")) {
//            
//        }
        DbSNPentry dbSNPentry = new DbSNPentry(response);
        
        return dbSNPentry;        
    }
    
}
