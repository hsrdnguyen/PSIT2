package ch.avocado.share.common;

import java.util.Comparator;

/**
 * Created by bergm on 11/05/2016.
 */
public class SearchResultComparater implements Comparator {
    @Override
    public int compare(Object o1, Object o2) {
        return o1.toString().toLowerCase().compareTo(o2.toString().toLowerCase());
    }
}
