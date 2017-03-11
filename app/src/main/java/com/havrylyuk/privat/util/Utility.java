package com.havrylyuk.privat.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.text.TextUtils;

import com.google.android.gms.maps.model.LatLng;
import com.havrylyuk.privat.R;
import com.havrylyuk.privat.data.source.local.AcquiringContract.AcquiringEntry;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static com.google.maps.android.SphericalUtil.computeDistanceBetween;

/**
 *
 * Created by Igor Havrylyuk on 25.01.2017.
 */

public class Utility {

    public static String[] buildRequestedArgs(Context context, String query) {
        PreferencesHelper pref = PreferencesHelper.getInstance();
        if ("ALL".equalsIgnoreCase(pref.getSearchMode(context.getString(R.string.pref_query_list_key), "ALL"))) {
            return new String[]{"%" + query + "%"};
        } else if ("ATM".equalsIgnoreCase(pref.getSearchMode(context.getString(R.string.pref_query_list_key), "ALL")) ){
            return new String[]{"%" + query + "%", "ATM"};
        } else {
            return new String[]{"%" + query + "%", "TSO"};
        }
    }

    public static String buildRequestedSelection(Context context) {
        StringBuilder builder = new StringBuilder();
         PreferencesHelper pref = PreferencesHelper.getInstance();
         if ("ALL".equalsIgnoreCase(pref.getSearchMode(context.getString(R.string.pref_query_list_key), "ALL"))) {
             return builder.append(AcquiringEntry.ACQ_FULL_ADR).append(" LIKE ? ").toString();
         } else {
             return  builder.append(AcquiringEntry.ACQ_FULL_ADR).append(" LIKE ? ")
                     .append(" AND ").append(AcquiringEntry.ACQ_TYPE).append(" = ?").toString();
         }
    }

    public static String[] buildSuggestArgs(String suggest) {
        List<String> result = new ArrayList<>();
        String[] data = suggest.trim().split(",");
        if (data.length > 1) {
            for (int i = 0; i < data.length - 1; i++) {
                if (data[i].length() > 0) {
                    result.add("%"+data[i].trim()+"%");
                }
            }
            result.add("%"+data[data.length-1].trim()+"%");
            return  result.toArray(new String[result.size()]);
        } else return new String[]{"%"+suggest+"%"};
    }

    public static String buildSuggestSelection(String suggest) {
        StringBuilder builder = new StringBuilder();
        String[] result = suggest.trim().split(",");
        if (result.length > 1) {
            for (int i = 0; i < result.length - 1; i++) {
                if (result[i].length() > 0) {
                    builder.append(AcquiringEntry.ACQ_FULL_ADR).append(" LIKE ? ");
                    builder.append(" AND ");
                }
            }
            builder.append(AcquiringEntry.ACQ_FULL_ADR).append(" LIKE ? ");
        } else return builder.append(AcquiringEntry.ACQ_FULL_ADR).append(" LIKE ? ").toString();

        return builder.toString();
    }

    public static boolean isNetworkAvailable(final Context context) {
        ConnectivityManager cm = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        @SuppressWarnings("deprecation")
        boolean isNetworkAvailable = cm.getBackgroundDataSetting() &&
                cm.getActiveNetworkInfo() != null;
        return isNetworkAvailable;
    }

    public static String getNameFromType(Context context, String type) {
        if (context.getString(R.string.type_atm).equalsIgnoreCase(type)) {
            return context.getString(R.string.atm);
        } else if (context.getString(R.string.type_tso).equalsIgnoreCase(type)) {
            return context.getString(R.string.tso);
        }
        return "";
    }

    public static int getLanguageIndex() {
        final  String lang = Locale.getDefault().getLanguage();
        int result;
        if (lang.equalsIgnoreCase("en")) result = 0;else
        if (lang.equalsIgnoreCase("ru")) result = 1;else
        if (lang.equalsIgnoreCase("uk")) result = 2;else
            result = 0;
        return result;
    }

    public static String normalizeStr(String place) {
        return TextUtils.isEmpty(place) ? "" : place.replace('\\', ' ');
    }

    public static  String toCapsWord(String s) {
        String result="";
        if (s != null && s.length()>0) {
            String[] words = s.split("\\s");
            for (String w : words) {
                if (w.length() > 0) {
                    w = w.trim().replaceFirst(String.valueOf(w.trim().charAt(0)),
                            String.valueOf(Character.toUpperCase(w.trim().charAt(0))))+" ";
                    result+=w;
                }
            }
        }
        return  result.trim();
    }


    public static List<LatLng> decodePoly(String encoded) {
        List<LatLng> poly = new ArrayList<LatLng>();
        int index = 0, len = encoded.length();
        int lat = 0, lng = 0;

        while (index < len) {
            int b, shift = 0, result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;

            shift = 0;
            result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;

            LatLng p = new LatLng( (((double) lat / 1E5)),
                    (((double) lng / 1E5) ));
            poly.add(p);
        }
        return poly;
    }

    private static double distance(LatLng point1, LatLng point2) {
        return computeDistanceBetween(point1, point2) / 1000;//km
    }
}
