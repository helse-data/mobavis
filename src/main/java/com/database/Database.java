package com.database;

import com.database.web.DbSNP;
import com.database.web.DbSNPentry;
import com.snp.VerifiedSNP;
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
 * 
 * The main class the database system, requests for MoBa data on SNPS go through here.
 * 
 * @author Christoffer Hjeltnes Støle 
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
    
    Map <String, VerifiedSNP> requestedSNPs = new HashMap();
    
    public Database() {
        System.out.println("Database constructing ...");
        sharedPath = VaadinService.getCurrent().getBaseDirectory().getAbsolutePath();
        //System.out.println("sharedPath: " + sharedPath);
        sharedPath = sharedPath + "/../../../../server/data";
        
        readMasterIndex();
    }
    
    /**
     * 
     * Returs the data for the SNP specified by the input.
     * 
     * @param snpIDParser - the input in form of a snpIDParser object
     * @return 
     */
    public VerifiedSNP getSNP(SNPIDParser snpIDParser) {
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
    
    /**
     * Returns a SNP object with MoBa data based on the SNPs rsID.
     * 
     * @param rsID - the rsID of the SNP
     * @return 
     */
    public VerifiedSNP getSNP(String rsID) {
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
        
        VerifiedSNP snp = new VerifiedSNP(databaseEntry, dbSNPentry);
        requestedSNPs.put(rsID, snp);
        
        return snp;
        
    }
    
    /**
     * 
     * Returns a SNP object with MoBa data based on the SNPs chromosome and position.
     * 
     * @param chromosome
     * @param position
     * @return 
     */    
    public VerifiedSNP getSNP(String chromosome, String position) {
        System.out.println("Fetching SNP on chromosome " + chromosome + " at position " + position + " ...");
        
        Map <String, String> result = getNearestSNPs(chromosome, Integer.parseInt(position));
        if (result.containsKey("0")) {
            return getSNP(result.get("0"));
        }
        else {
            return null;
        }

        //String snpData = queryEntry(chromosome, snpID);
        //SNPDatabaseEntry databaseEntry = new SNPDatabaseEntry(snpData);
        //SNP snp = new VerifiedSNP(snpID, chromosome, position, snpData);
        //requestedSNPs.put(chromosome + ":" + position, databaseEntry);

        //System.out.println(chromosome + " " + position);
        //System.out.println(snp);
        //return databaseEntry;
        //return null;
    }
    
    
    /**
     * 
     * Returns a SNP object with MoBa data based on the SNPs chromosome, position,
     * reference allele and alternative allele.
     * 
     * @param chromosome
     * @param position
     * @param ref - reference allele
     * @param alt - alternative alele
     * @return 
     */
    public VerifiedSNP getSNP(String chromosome, String position, String ref, String alt) {   
        String databaseSNPID = chromosome + "_" + position + "_" + ref + "_" + alt;
        if (requestedSNPs.containsKey(databaseSNPID)) { // don't query the database system more than necessary
            System.out.println("Using data stored in memory for SNP with database ID " + databaseSNPID);
            return requestedSNPs.get(databaseSNPID);
        }
        
        SNPDatabaseEntry databaseEntry = getEntry(chromosome, databaseSNPID);
        DbSNPentry dbSNPentry = null;
        VerifiedSNP snp = new VerifiedSNP(databaseEntry, dbSNPentry);
        requestedSNPs.put(databaseSNPID, snp);
        return snp;
    }
    
    /**
     * 
     * If a SNP is located at the provided position, the ID of the SNP is returned.
     * Otherwise, the ID of the neareast SNP before or after the proved position is returned.
     * 
     * @param chromosome - chromosome to search on
     * @param position - position to match
     * @return HashMap with mandatory key "result" and optional keys
 "-1" - closest VerifiedSNP before before the position
 "0" - exact match
 "1" - closest VerifiedSNP after the position
 * 
 * The returned SNP ID is paired with the relevant key above.
     */
    public Map <String, String> getNearestSNPs(String chromosome, int position) {
        //int maxNumber = 5;
        
        Map <String, String> result = new HashMap();
        
        System.out.println("Scanning neighbourhood of position " + position + " on chromosome " + chromosome + ".");
        String fileName = sharedPath + "/new_annotation/snp_annotation_" + chromosome + ".gz";
        
        File file = new File(fileName);
        Long fileSize = file.length(); // get size of annotation file in bytes
        int bytesPerLine = 9621220/531277; // calculated for chr 21
        //Long offset = fileSize/bytesPerLine;
                
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
                //if (currentPosition == 9411377) {
                //    System.out.println("match: " + currentPosition);
                //}
                
                if (currentPosition > 16588000 && currentPosition < 16588999) {
                    System.out.println("Current position: " + currentPosition);
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
    
    /**
     * 
     * Returns a SNPDatabaseEntry entry  based on provided ID of the SNP. Chromosome is required.
     * 
     * @param chromosome
     * @param databaseSNPID - rsID or unique internal identifier
     * @return 
     */
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
    /**
     * 
     * Searches the index files for the given SNP ID and returns the columns of the index entry
     * as array elements, if found.
     * 
     * @param SNPID - rsID or unique internal identifier
     * @return 
     */
    
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
    
    /**
     * Retrieves the right index file from the given SNP from the master index.
     * 
     * @param SNPID - rsID or unique internal identifier
     * @return 
     */
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
    
    /**
     * Parses the contents of the master index and loads the result
     * into an ArrayList object.
     */
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