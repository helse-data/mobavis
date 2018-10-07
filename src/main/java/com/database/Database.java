package com.database;

import com.database.web.DbSNP;
import com.database.web.DbSNPentry;
import com.snp.SNP;
import com.utils.Alphanumerical;
import com.utils.Constants;
import com.snp.SNPIDParser;
import com.snp.SNPIDParser.SNPIDFormat;
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
 * Class for the management of the entirety of the database system, including auxiliary files.
 * 
 */
public class Database {
    Constants constants = new Constants();
    AnnotationReader annotationReader = new AnnotationReader();
    DbSNP dbSNP = new DbSNP();
    String[] chromosomeList = constants.getChromosomeList();
    String sharedPath;
    Map <String, String> indices = new HashMap();
    Map <String, List <String>> tables = new HashMap();
    Map <String, Boolean> indexLoaded = new HashMap();
    List <Alphanumerical> masterIndex = new ArrayList();
    
    Map <String, RocksDB> databases = new HashMap();
    
    Map <Integer, Map <String, String>> annotation = new HashMap();
    
    Map <String, SNP> requestedSNPs = new HashMap();
    
    public Database() {
        System.out.println("Database constructing ...");
        sharedPath = VaadinService.getCurrent().getBaseDirectory().getAbsolutePath();
        //System.out.println("sharedPath: " + sharedPath);
        sharedPath = sharedPath + "/../../../../server/data";
        
        readMasterIndex();
    }
    
    public SNP getSNP(SNPIDParser snpIDParser) {
        SNPIDFormat snpIDFormat = snpIDParser.getIDFormat();
        if (snpIDFormat == SNPIDFormat.RSID) {
            return getSNP(snpIDParser.getRsID());
        }
        else if (snpIDFormat == SNPIDFormat.CHROMOSOME_POSITION) {
            return getSNP(snpIDParser.getChromosome(), snpIDParser.getPosition());
        }
        else if (snpIDFormat == SNPIDFormat.CHROMOSOME_POSITION_REF_ALT) 
            return getSNP(snpIDParser.getChromosome(), snpIDParser.getPosition(), snpIDParser.getRef(), snpIDParser.getAlt());
        return null;
    }
    
    // https://www.ncbi.nlm.nih.gov/books/NBK44417/#Content.what_is_a_reference_snp_or__rs_i
    public SNP getSNP(String rsID) {
        System.out.println("Fetching SNP \"" + rsID + "\" ...");
        if (requestedSNPs.containsKey(rsID)) { // don't query the database system more than necessary
            System.out.println("Using data stored in memory for SNP with database ID " + rsID);
            return requestedSNPs.get(rsID);
        }
        
        String [] searchResult = searchIndices(rsID);
        //System.out.println("Search result: " + Arrays.toString(searchResult));
        if (searchResult == null) {
            System.out.println("The SNP \"" + rsID + "\" could not be located in the correct index.");
            return null;
        }
        String chromosome = searchResult[0];
        String position = searchResult[1];
        SNPDatabaseEntry databaseEntry = getEntry(chromosome, rsID);
        DbSNPentry dbSNPentry = dbSNP.getEntry(rsID.replace("rs", ""));
        
        SNP snp = new SNP(databaseEntry, dbSNPentry);
        requestedSNPs.put(rsID, snp);
        
        return snp;
        
    }
    
    public SNP getSNP(String chromosome, String position) {   // TODO: implement
        System.out.println("Fetching SNP on chromosome " + chromosome + " at position " + position + " ...");
        

        //String snpData = queryEntry(chromosome, snpID);
        //SNPDatabaseEntry databaseEntry = new SNPDatabaseEntry(snpData);
        //SNP snp = new SNP(snpID, chromosome, position, snpData);
        //requestedSNPs.put(chromosome + ":" + position, databaseEntry);

        //System.out.println(chromosome + " " + position);
        //System.out.println(snp);
        //return databaseEntry;
        return null;
    }
    
    // of the form 21_9411298_G_A
    public SNP getSNP(String chromosome, String position, String ref, String alt) {   
        String databaseSNPID = chromosome + "_" + position + "_" + ref + "_" + alt;
        if (requestedSNPs.containsKey(databaseSNPID)) { // don't query the database system more than necessary
            System.out.println("Using data stored in memory for SNP with database ID " + databaseSNPID);
            return requestedSNPs.get(databaseSNPID);
        }
        
        SNPDatabaseEntry databaseEntry = getEntry(chromosome, databaseSNPID);
        DbSNPentry dbSNPentry = null;
        SNP snp = new SNP(databaseEntry, dbSNPentry);
        requestedSNPs.put(databaseSNPID, snp);
        return snp;
    }
    
    /**
     * 
     * @param chromosome - chromosome to search on
     * @param position - position
     * @return Map with mandatory key "result" and optional keys
     * "-1" - closest SNP before before the position
     * "0" - exact match
     * "1" - closest SNP after the position
     */
    public Map <String, String> getNearestSNPs(String chromosome, int position) {
        int maxNumber = 5;
        
        Map <String, String> result = new HashMap();
        
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
            String currentSNP = null;
            String prevSNP = null;
            
            int currentPosition = -1;
            int previousPosition = -1;
            
            System.out.println("searching for position: " + position);
            
            while ((line = bufferedReader.readLine()) != null) {
                previousPosition = currentPosition;
                prevSNP = currentSNP;
                String[] columns = line.split("\t");
                currentSNP = columns[2];

                currentPosition = Integer.parseInt(columns[1]);
                //System.out.println("current position: " + currentPosition);
                if (currentPosition == 9411377) {
                    System.out.println("match: " + currentPosition);
                }
                if (currentPosition == position) {
                    System.out.println("exact match: " + currentPosition);
                    result.put("result", "exact");
                    result.put("0", currentSNP);
                    break;
                }
                else if (currentPosition > position) {
                    System.out.println("\nPassed position, closest next match is " + currentSNP + " at position " + currentPosition +
                            ". \nPrevious SNP was " + prevSNP + " at position " + previousPosition + ".");
                    result.put("result", "nearest");
                    
                    if (prevSNP != null) {
                        result.put("-1", prevSNP);
                    }
                    
                    result.put("1", currentSNP);
                    break;
                }
            }
            long elapsedTime = System.nanoTime() - start;
            System.out.println("Time taken: " + String.format("%.2f", elapsedTime/Math.pow(10, 9)) + " seconds.");
        }
        catch (IOException e) {
            System.out.println(e.getMessage());
        }
        return result;
    }
    
    private SNPDatabaseEntry getEntry(String chromosome, String databaseSNPID) {
        String snpData = null;
        Options options = new Options().setCreateIfMissing(false);
        System.out.println("database SNP ID: " + databaseSNPID);
        try (RocksDB db = RocksDB.openReadOnly(options, sharedPath + "/databases/" + chromosome);) {
            byte[] rawData = db.get(databaseSNPID.getBytes());
            if (rawData != null) {
                 snpData = new String(rawData);
                 //System.out.println("snpData: " + snpData);
            }
        }
        catch (RocksDBException e) {
            System.out.println(e);
        }
        
        Map <String, String> annotation = annotationReader.getAnnotation(chromosome, databaseSNPID);
        
        SNPDatabaseEntry databaseEntry = new SNPDatabaseEntry(snpData, annotation);
        return databaseEntry;
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