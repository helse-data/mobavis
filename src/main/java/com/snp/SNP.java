package com.snp;

import com.database.SNPDatabaseEntry;
import com.utils.Constants;
import com.utils.UtilFunctions;
import com.database.web.DbSNP;
import com.database.web.DbSNPentry;

/**
 * 
 * Class to store verified SNP information.
 * 
 * Other classes can not set the values of a SNP object; information about the SNP
 * is intended to come from reliable sources and not user input.
 * 
 *
 * @author Christoffer Hjeltnes Støle
 */
public class SNP implements SNPInterface{    
    String ID;
    Constants constants = new Constants();
    String[] ageVariables = constants.getAgeVariables();
    String chromosome;
    SNPDatabaseEntry databaseEntry;
    Boolean hasDatabaseEntry;
    DbSNPentry dbSNPentry;
    String position;
    boolean SNPdbQueried = false;
    UtilFunctions utilFunctions = new UtilFunctions();
    
//    public SNP(String ID, String chromosome, String position) {
//        this.ID = ID;
//        this.chromosome = chromosome;
//        this.position = position;
//      
//    }
//    
//    public SNP(String chromosome, String position) {    
//        this.chromosome = chromosome;
//        this.position = position;
//      
//    }
//    
//    public SNP (String ID) {
//        this.ID = ID;
//    }
    
    // a database entry can only be provided at construction time
    public SNP (SNPDatabaseEntry databaseEntry, DbSNPentry dbSNPEntry) {
        this.databaseEntry = databaseEntry;
        this.dbSNPentry = dbSNPEntry;
    }

    public boolean hasData() {
        return databaseEntry.hasData();
    }
    
    public boolean hasAnnotation() {
        return databaseEntry.hasAnnotation();
    }
    
    public SNPDatabaseEntry getDataBaseEntry() {
        return databaseEntry;
    }    
    
    public String getID() {
        if (databaseEntry != null) {
            return getDataBaseEntry().getAnnotation().get("Id");            
        }
        return ID;
    }
    public String getChromosome() {
        if (databaseEntry != null) {
            return getDataBaseEntry().getAnnotation().get("Chromosome");            
        }
        return chromosome;        
    }
    
    public DbSNPentry getDbSNPentry() {
        if (checkDbSNPState()) {
            return dbSNPentry;
        }
        return null;  
    }
    
    private boolean checkDbSNPState() {
        if (dbSNPentry == null) {
            if (ID != null && ID.startsWith("rs")) {
                DbSNP dbSNP = new DbSNP();
                dbSNPentry = dbSNP.getEntry(ID.replace("rs", ""));
                return true;
            }
            else {
                return false;
            }            
        }
        else {
            return true;
        }
    }
    
    public String getLocus() {
        if (checkDbSNPState()) {
            return dbSNPentry.getLocus();
        }
        return null;        
    }
    
    public String getLocusFullName() {
        if (checkDbSNPState()) {
            return dbSNPentry.getLocusFullName();
        }
        return null;  
    }
    
    public String getPosition() {
        return position;
    }
}
