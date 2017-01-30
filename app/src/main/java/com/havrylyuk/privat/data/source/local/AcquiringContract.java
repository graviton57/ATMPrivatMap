/*
 * Copyright (c)  2017. Igor Gavriluyk
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.havrylyuk.privat.data.source.local;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 *
 * Created by Igor Havrylyuk on 25.01.2017.
 */

public class AcquiringContract {

    public static final String CONTENT_AUTHORITY = "com.havrylyuk.privat";


    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String PATH_ACQUIRING = "acquiring";
    public static final String PATH_LOCATION = "location";


    public static final class AcquiringEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_ACQUIRING).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_ACQUIRING;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_ACQUIRING;

        // Table name
        public static final String TABLE_NAME = "acquiring";

        public static final String ACQ_TYPE = "type";
        public static final String ACQ_PLACE = "place";
        public static final String ACQ_CITY = "city";
        public static final String ACQ_FULL_ADR = "full_address";
        public static final String ACQ_LAT = "latitude";
        public static final String ACQ_LON = "longitude";
        public static final String ACQ_FAV = "favorite";
        public static final String ACQ_TW = "tw";

        public static Uri buildAcquiringUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        public static long getIdFromUri(Uri uri) {
            return Long.parseLong(uri.getPathSegments().get(1));
        }
    }


    public static final class LocationEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_LOCATION).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_LOCATION;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_LOCATION;


        public static final String TABLE_NAME = "location";

        public static final String LOC_LAT = "latitude";
        public static final String LOC_LON = "longitude";
        public static final String LOC_DATE = "timestamp";
        public static final String LOC_ROUTE = "route";

        public static Uri buildLocationUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        public static long getIdFromUri(Uri uri) {
            return Long.parseLong(uri.getPathSegments().get(1));
        }
    }

}
