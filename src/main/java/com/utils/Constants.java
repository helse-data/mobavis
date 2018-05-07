package com.utils;

import com.vaadin.server.VaadinService;
import java.util.HashMap;
import java.util.Map;


/**
 *
 * @author Christoffer Hjeltnes Støle
 */
public class Constants {
    public String[] getAges() {
        return new String[] {"birth", "6 weeks", "3 months", "6 months", "8 months", "1 year",
            "15-18 months", "2 years", "3 years", "5 years", "7 years", "8 years"};
    }
    
    public String[] getAgesShort() {
        return new String[] {"birth", "6w", "3m", "6m", "8m", "1y", "15-18m", "2y", "3y", "5y", "7y", "8y"};
    }

    public String[] getAgeVariables() {
        return new String[] {"age0", "age1", "age2", "age3", "age4", "age5", "age6", "age8", "age9", "age10", "age11", "age12"};
    }
    public Map <String, String> getNumberToTextSexMap() {
        Map <String, String> map = new HashMap();
        map.put("1", "male");
        map.put("2", "female");
        return map;
    }
    
    public Map <String, String> getTextToNumberSexMap() {
        Map <String, String> map = new HashMap();
        map.put("male", "1");
        map.put("female", "2");
        return map;
    }
    
    public String[] getTimeUnits() {
        return new String[] {"day", "week", "month", "year"};
    }
    public String[] getTimeUnitsShort() {
        return new String[] {"d", "w", "m", "y"};
    }
       
    public String[] getChromosomeList() {
        return new String[] {"1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11",
                "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "X", "Y"};
    }
    
    public int[] getChromosomeSizes() {
        return new int[] {249250621, 243199373, 198022430, 191154276, 180915260, 171115067, 159138663, 146364022,
        141213431, 135534747, 135006516, 133851895, 115169878, 107349540, 102531392, 90354753, 81195210, 78077248,
        59128983, 63025520, 48129895, 51304566};
    }
    
    public String getGenomeBuild() {
        return "GRCh37.p13";
    }
    public String getServerPath() {
        return getVaadinPath() + "/../../../../server/";
    }
    public String getVaadinPath() {
        return VaadinService.getCurrent().getBaseDirectory().getAbsolutePath();
    }    
    public String[] getDatabaseList() {
        return new String[] {"1_db", "2_db", "3_db", "4_db", "5_db", "6_db", "7_db", "8_db",
            "9_db", "10_db", "11_db", "12_db","13_db", "14_db", "15_db", "16_db", "17_db",
            "18_db", "19_db", "20_db", "21_db", "22_db"};
    }
    public int getTableLength() {
        return 20000;
    }
    
}