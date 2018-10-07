package com.database;

import com.snp.SNPOld;
import com.utils.Alphanumerical;
import com.utils.Constants;
import com.vaadin.server.VaadinService;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.sqlite.SQLiteConfig;


/**
 * 
 * @author ChristofferHjeltnes
 *
 * 
 */

public class SQLite {
    Constants constants = new Constants();
    //Connection connection;
    String[] chromosomeList = constants.getChromosomeList();
    String[] databaseList = {"1_db", "9_db", "20_db", "21_db", "22_db"};
    //String[] databaseList = constants.getDatabaseList();
    String sharedPath;
    Map <String, String> indices = new HashMap<>();
    Map <String, List <String>> tables = new HashMap<>();
    Map <String, Boolean> indexLoaded = new HashMap<>();
    List <Alphanumerical> masterIndex = new ArrayList();
    List <Boolean> isDataBaseQueried = new ArrayList();
    int[] chromosomeSizes = constants.getChromosomeSizes();
    
    
    Map <Integer, Map <String, String>> annotation = new HashMap();
    
    public SQLite() {
        try {
            // for some reason necessary when using with Vaadin
            Class.forName("org.sqlite.JDBC");
        }
            catch (ClassNotFoundException e) {
                System.out.println(e.getMessage());
        }
        sharedPath = VaadinService.getCurrent().getBaseDirectory().getAbsolutePath();
        sharedPath = sharedPath + "/../../../../server/data";
        
       for (int i = 0; i < 23; i++) {
           isDataBaseQueried.add(false);
       }
       readMasterIndex();
       
        //getTables(connect("1_db"), "1");
        //System.out.println(tables.get("1").get(tables.get("1").size()-1));
    }
    
    public SNPOld getSNP(String snpID) {        
        System.out.println("Fetching SNP \"" + snpID + "\" ...");
        
        String chromosome;
        String position;
        
        String table;
        if (snpID.matches("\\d+_.*")){
            String[] split = snpID.split("_");

            chromosome = split[0];
            position = split[1];
        }
        else {
            String [] searchResult = searchIndices(snpID);
            if (searchResult == null) {
                System.out.println("The SNP \"" + snpID + "\" could not be located in the correct index.");
                return null;
            }
            chromosome = searchResult[0];
            position = searchResult[1];
            
        }
        
        SNPOld snp = new SNPOld(snpID, chromosome, position);
        
        table = getTable(chromosome, position);
        if (table == null) {
                System.out.println("No table was found for the SNP \"" + snpID + "\"");
        }
        queryDatabaseTable(chromosome + "_db", table, snp);

        //System.out.println(chromosome + " " + position);
        //System.out.println(snp);
        return snp;
    }
    
        
    private void queryDatabaseTable(String database, String table, SNPOld snp) {
        ResultSet resultSet;
                
        String chromosome = database.replaceFirst("_db", "");
        int chrInt = Integer.parseInt(chromosome);
        //if (!isDataBaseQueried.get(chrInt)) {
        //    readWholeFile(new File(sharedPath + "/profiles/" + database));
        //}
        
        System.out.println("SNP " + snp.getID() + " is located on chromosome " +
                chromosome + ". It should be found in table " + table + " in the database " + database + ".");
        
        String selectedColumns = "*";
        String query;
        //Connection connection = connect(database);
        Connection connection = null;

        try {
                     
            
            SQLiteConfig config = new SQLiteConfig();
            
            
            // SQLite optimization (suggested by David Bouyssié)
            config.setSynchronous(SQLiteConfig.SynchronousMode.OFF); // disable synchronous access; it is only needed for write operations
            config.setJournalMode(SQLiteConfig.JournalMode.OFF); // disable the DB journal; it is only needed for write operations
            config.setTempStore(SQLiteConfig.TempStore.MEMORY);  // put TEMP data in memory (TEMP tables)
            config.setCacheSize(-100000); // define the amount of RAM dedicated to cached operations, here around 100 Mo
//        connection.exec("PRAGMA mmap_size=2147418112;"); // around 2 GB of mapped-memory, may help for batch processing
            //config.set
            connection = DriverManager.getConnection("jdbc:sqlite:" + sharedPath + "/profiles/" + database, config.toProperties());
          
            Statement statement = connection.createStatement();
          
            System.out.println("Statement created.");
            query = "SELECT " + selectedColumns + " "
                + "FROM " + table + " "
                + "WHERE id = '" + snp.getID() + "'";            
            //System.out.println("Query starts.");
            resultSet = statement.executeQuery(query);
            isDataBaseQueried.set(chrInt, true);
            //connection.commit();
            System.out.println("Query executed.");
            if (resultSet.isBeforeFirst()) {
                System.out.println("Succesful query: " + query);
                snp.setData(resultSet);
            }
            else {                
                System.out.println("No queries were succesful for the SNP \"" + snp.getID() + "\"");
            }
        }
        catch (SQLException e) {
                System.out.println(e.getMessage()); 
        }
        finally {
            disconnect(connection);
        }
    }
    
    // 20 000 bases per table:
    // chr1_164700000_164720000
    // chr9_128020000_128040000
    
    // last table:  chr1_249220000_249250621 || 249 250 621
    // given table: chr1_249220000_249240000 || 249 240 000
    
    // not found: 22_16053352_C_T; given table chr22_16040000_16060000
    // actual table: chr22_460000_480000
    private String getTable(String chromosome, String position) {
        int tableLength = constants.getTableLength();
        int posInt = Integer.parseInt(position);
        int multiple = posInt/tableLength;
        int tableStart = multiple*tableLength;
        int tableEnd = tableStart + tableLength;
        int chromosomeEnd = chromosomeSizes[Integer.parseInt(chromosome) - 1];
        if ((tableEnd + tableLength) > chromosomeEnd) { // special care for the last table of a chromosome
            tableEnd = chromosomeEnd;
        }
        String table = "chr" + chromosome + "_" + tableStart + "_" + tableEnd;
        
        return table;
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

        
    private Connection connect(String database) {
        Connection connection;
        try {
            // db parameters
            String url = "jdbc:sqlite:" + sharedPath + "/profiles/" + database;
            // create a connection to the database
            connection = DriverManager.getConnection(url);
           // connection.setAutoCommit(false);
            System.out.println("Connection to database \"" + database + "\" has been established.");
            return connection;
            
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return null;
    }
    
    public void disconnect(Connection connection) {
        try {
            if (connection != null) {
                connection.close();
                System.out.println("Disconnected from database.");
            }
        } 
        catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }
    }
}