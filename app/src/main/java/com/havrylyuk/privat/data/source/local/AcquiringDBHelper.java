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

import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.havrylyuk.privat.data.source.local.AcquiringContract.AcquiringEntry;
import com.havrylyuk.privat.data.source.local.AcquiringContract.LocationEntry;
import com.havrylyuk.privat.service.SyncService;

/**
 *
 * Created by Igor Havrylyuk on 25.01.2017.
 */

public class AcquiringDBHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "privat.db";

    public static final int DATABASE_VERSION = 1;

    private Context context;

    private final String SQL_CREATE_ACQUIRING_TABLE = "CREATE TABLE " + AcquiringEntry.TABLE_NAME + " (" +
            AcquiringEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
            AcquiringEntry.ACQ_TYPE + " TEXT NOT NULL DEFAULT '', " +
            AcquiringEntry.ACQ_PLACE + " TEXT NOT NULL DEFAULT '', " +
            AcquiringEntry.ACQ_CITY + " TEXT NOT NULL DEFAULT '', " +
            AcquiringEntry.ACQ_FULL_ADR + " TEXT NOT NULL DEFAULT '', " +
            AcquiringEntry.ACQ_LAT + " REAL NOT NULL DEFAULT 0, " +
            AcquiringEntry.ACQ_LON + " REAL NOT NULL DEFAULT 0 ," +
            AcquiringEntry.ACQ_FAV + " INTEGER NOT NULL DEFAULT 0 ," +
            AcquiringEntry.ACQ_TW + " TEXT NOT NULL DEFAULT '', " +
            " UNIQUE (" + AcquiringEntry.ACQ_LAT + ", " + AcquiringEntry.ACQ_LON + ") ON CONFLICT REPLACE);";

    //Location table
    private final String SQL_CREATE_LOCATION_TABLE = "CREATE TABLE " + LocationEntry.TABLE_NAME + " (" +
            LocationEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
            LocationEntry.LOC_LAT + " REAL NOT NULL DEFAULT 0, " +
            LocationEntry.LOC_DATE + " TEXT NOT NULL DEFAULT '', " +
            LocationEntry.LOC_ROUTE + " TEXT NOT NULL DEFAULT '', " +
            LocationEntry.LOC_LON + " REAL NOT NULL DEFAULT 0 );";


    public AcquiringDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(SQL_CREATE_LOCATION_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_ACQUIRING_TABLE);
        Intent intent = new Intent(context, SyncService.class);
        intent.setData(AcquiringEntry.CONTENT_URI);
        context.startService(intent);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {

    }

}
