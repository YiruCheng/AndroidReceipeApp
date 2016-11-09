package com.semanticweb.receipe.receipeapp.Utilities;

/**
 * Created by Omer on 11/8/2016.
 */

import java.util.Comparator;

public class IgnoreCaseComparator implements Comparator<String> {
    public int compare(String strA, String strB) {
        return strA.compareToIgnoreCase(strB);
    }
}