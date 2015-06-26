package org.mrcpp.constant;

/**
 * Created by evanhutomo on 6/24/15.
 */
public class EHConstant {
    public static final String[] LEAF_TYPE = new String[] {
            "Soybean",
            "Rice",
            "Corn",
            "Cassava",
            "Ground nut",
            "Yam"
    };

    public static final String SIMPLE_DATE_FORMAT = "yyyy-MM-dd";
    public static final String DETAIL_DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";
    public static final String DIR_CSV_MATADAUN = "/MATADAUN/CSV/";
    public static final String DIR_IMG_MATADAUN = "/MATADAUN/IMG/";

    public String EHCapitalize(final String line) {
        String sRep = line.replace("_", " ");
        return Character.toUpperCase(sRep.charAt(0)) + sRep.substring(1);
    }
}
