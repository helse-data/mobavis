package com.main;

import com.utils.Alphanumerical;
import com.utils.Constants;
import com.vaadin.server.VaadinService;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.GZIPInputStream;
import org.rocksdb.Options;
import org.rocksdb.RocksDB;
import org.rocksdb.RocksDBException;


/**
 * 
 * @author Christoffer Hjeltnes Støle
 *
 * 
 * Class for the entirety of the database system, including auxiliary files.
 * 
 */
public class Database {
    Constants constants = new Constants();
    String[] chromosomeList = constants.getChromosomeList();
    String[] databaseList = {"2", "21"};
    //String[] databaseList = constants.getDatabaseList();
    String sharedPath;
    Map <String, String> indices = new HashMap();
    Map <String, List <String>> tables = new HashMap();
    Map <String, Boolean> indexLoaded = new HashMap();
    List <Alphanumerical> masterIndex = new ArrayList();
    
    Map <String, RocksDB> databases = new HashMap();
    
    Map <Integer, Map <String, String>> annotation = new HashMap();
    
    Map <String, SNP> requestedSNPs = new HashMap();
    
    public Database() {
        sharedPath = VaadinService.getCurrent().getBaseDirectory().getAbsolutePath();
        sharedPath = sharedPath + "/../../../../server/data";
        
        readMasterIndex();
    }
    
    public SNP getSNP(String snpID) {   
        System.out.println("Fetching SNP \"" + snpID + "\" ...");
        
        if (requestedSNPs.containsKey(snpID)) { // don't query the database system more than necessary
            return requestedSNPs.get(snpID);
        }
        
        String chromosome;
        String position;
        
        
        if (snpID.matches("\\d+_.*")){
            String[] split = snpID.split("_");

            chromosome = split[0];
            position = split[1];
        }
        else {
            String [] searchResult = searchIndices(snpID);
            //System.out.println("Search result: " + Arrays.toString(searchResult));
            if (searchResult == null) {
                System.out.println("The SNP \"" + snpID + "\" could not be located in the correct index.");
                return null;
            }
            chromosome = searchResult[0];
            position = searchResult[1]; 
        }
        
        String snpData = queryDatabase(chromosome, snpID);
        SNP snp = new SNP(snpID, chromosome, position, snpData);
        requestedSNPs.put(snpID, snp);

        //System.out.println(chromosome + " " + position);
        //System.out.println(snp);
        return snp;
    }
    
    public List <String> getNearestSNPs(String chromosome, int position) {
        int maxNumber = 5;
        
        List <String> results = new ArrayList();
        
        System.out.println("Scanning neighbourhood of position " + position + " on chromosome " + chromosome + ".");
        String fileName = sharedPath + "/new_annotation/snp_annotation_" + chromosome + ".gz";
        
        File file = new File(fileName);
        Long fileSize = file.length(); // get size of annotation file in bytes
        int bytesPerLine = 9621220/531277; // calculated for chr 21
        Long offset = fileSize/bytesPerLine;
                
        try (InputStream inputStream = new FileInputStream(fileName);
                InputStream gzipStream = new GZIPInputStream(inputStream);) {
            
            Reader reader = new InputStreamReader(gzipStream);
            BufferedReader bufferedReader = new BufferedReader(reader);
            String line = bufferedReader.readLine(); // read the header
            
            long start = System.nanoTime();    
            String current = null;
            String prev = null;
            while ((line = bufferedReader.readLine()) != null) {
                prev = current;
                String[] columns = line.split("\t");
                current = columns[2];
                int currentPosition = Integer.parseInt(columns[1]);
                if (currentPosition == position) {
                    System.out.println("exact match: " + currentPosition);
                    results.add(current);
                    break;
                }
                else if (currentPosition > position) {
                    System.out.println("passed position; next match: " + currentPosition);
                    if (prev != null) {
                        results.add(prev);
                    }
                    
                    results.add(current);
                    break;
                }
            }
            long elapsedTime = System.nanoTime() - start;
            System.out.println("Time taken: " + String.format("%.2f", elapsedTime/Math.pow(10, 9)) + " seconds.");
        }
        catch (IOException e) {
            System.out.println(e.getMessage());
        }
        return results;
    }
    
    private String queryDatabase(String database, String snpID) {
        String snpData = null;
        Options options = new Options().setCreateIfMissing(false);
        System.out.println("SNP ID: " + snpID);
        try (RocksDB db = RocksDB.openReadOnly(options, sharedPath + "/databases/" + database);) {
            byte[] rawData = db.get(snpID.getBytes());
            if (rawData != null) {
                 snpData = new String(rawData);
                 //System.out.println("snpData: " + snpData);
            }
        }
        catch (RocksDBException e) {
            System.out.println(e);
        }
        return snpData;
    }
    
    private String[] searchIndices(String SNPID) {
        Alphanumerical index = getIndex(SNPID);
        System.out.println("SNP " + SNPID + " should be found in index " + index + ".csv");
       
        try {
            InputStream inputStream = new FileInputStream(sharedPath + "/indices/" + index + ".csv");
            Reader reader = new InputStreamReader(inputStream);
            BufferedReader bufferedReader = new BufferedReader(reader);
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                String[] split = line.split(" ");
                if (split[0].equals(SNPID)) {
                    return new String[] {split[1], split[2]};
                }
            }
            inputStream.close();
            reader.close();
            bufferedReader.close();
        }
        catch (IOException e) {
            System.out.println(e.getMessage());
        }
        return null;
    }
    
    private Alphanumerical getIndex(String SNPID) {
        Alphanumerical ID = new Alphanumerical(SNPID, "<letters><integers>");
        for (int i = 1; i < masterIndex.size(); i++) {
            int comparison = ID.compareTo(masterIndex.get(i));
            if (comparison < 0) {
                return masterIndex.get(i-1);
            }
            else if (comparison == 0) {
                return masterIndex.get(i);
            }
        }
        return masterIndex.get(masterIndex.size()-1);
    }
    
    private void readMasterIndex() {
        try {
            System.out.println("Reading master index.");

            InputStream inputStream = new FileInputStream(sharedPath + "/indices/master.dat");
            Reader reader = new InputStreamReader(inputStream);
            BufferedReader bufferedReader = new BufferedReader(reader);
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                masterIndex.add(new Alphanumerical(line));
            }
            inputStream.close();
            reader.close();
            bufferedReader.close();
        }
        catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }
}