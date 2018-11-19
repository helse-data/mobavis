package com.snp;

import com.database.SNPDatabaseEntry;
import com.utils.Constants;
import com.utils.UtilFunctions;
import com.database.web.DbSNP;
import com.database.web.DbSNPentry;

/**
 * 
 * Class to store verified VerifiedSNP information.
 * 
 * Other classes can not set the values of a VerifiedSNP object; information about the VerifiedSNP
 * is intended to come from reliable sources and not user input.
 * 
 * This is in contrast to the InputSNP class, which is based on user input.
 * 
 *
 * @author Christoffer Hjeltnes Støle
 */
public class VerifiedSNP implements SNP{    
    Constants constants = new Constants();
    String[] ageVariables = constants.getAgeVariables();
    SNPDatabaseEntry databaseEntry;
    Boolean hasDatabaseEntry;
    DbSNPentry dbSNPentry;
    boolean SNPdbQueried = false;
    UtilFunctions utilFunctions = new UtilFunctions();
    
   
    // a database entry can only be provided at construction time
    public VerifiedSNP (SNPDatabaseEntry databaseEntry, DbSNPentry dbSNPEntry) {
        this.databaseEntry = databaseEntry;
        this.dbSNPentry = dbSNPEntry;
    }

    /**
     * Whether the database query returned data.
     * 
     * @return 
     */
    public boolean hasData() {
        return databaseEntry.hasData();
    }
    /**
     * Whether the SNP has annotation.
     * 
     * @return 
     */
    public boolean hasAnnotation() {
        return databaseEntry.hasAnnotation();
    }
    /**
     * Returns the database entry for the SNP.
     * 
     * @return 
     */
    public SNPDatabaseEntry getDataBaseEntry() {
        return databaseEntry;
    }  
    

    @Override
    public String getID() {
        if (databaseEntry != null) {
            return getDataBaseEntry().getAnnotation().get("Id");            
        }
        return null;
    }

    @Override
    public String getChromosome() {
        if (databaseEntry != null) {
            return getDataBaseEntry().getAnnotation().get("Chromosome");            
        }
        return null;        
    }
    
    /**
     * 
     * Returns the dbSNP entry of the SNP.
     * 
     * @return 
     */
    public DbSNPentry getDbSNPentry() {
        if (checkDbSNPState()) {
            return dbSNPentry;
        }
        return null;  
    }
    
    /**
     * Checks whether dbSNP has been queried, and queries it if not.
     * 
     * @return 
     */
    private boolean checkDbSNPState() {
        if (dbSNPentry == null) {
            String ID = getID();
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
    
    /**
     * Returns the short name of the locus of the SNP.
     * 
     * @return 
     */
    public String getLocus() {
        if (checkDbSNPState() && dbSNPentry != null) {
            return dbSNPentry.getLocus();
        }
        return null;        
    }
    /**
     * 
     * Returns the full name of the locus of the SNP.
     * 
     * @return 
     */
    public String getLocusFullName() {
        if (checkDbSNPState()) {
            return dbSNPentry.getLocusFullName();
        }
        return null;  
    }
    
    @Override
    public String getPosition() {
        if (databaseEntry != null) {
            return getDataBaseEntry().getAnnotation().get("Position");            
        }
        return null;
    }
    @Override
    public String toString() {
      return "Verified SNP with ID \"" + getID() + "\", on chromosome " + getChromosome() + " at position " + getPosition() + ".";  
    }
}
