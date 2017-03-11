package com.havrylyuk.privat.service;

import android.app.IntentService;
import android.content.ContentValues;
import android.content.Intent;
import android.util.Log;

import com.havrylyuk.privat.BuildConfig;
import com.havrylyuk.privat.activity.MapsActivity;
import com.havrylyuk.privat.data.model.AcquiringPoint;
import com.havrylyuk.privat.data.model.AcquiringResponse;
import com.havrylyuk.privat.data.source.local.AcquiringContract.AcquiringEntry;
import com.havrylyuk.privat.data.source.remote.AcquiringService;
import com.havrylyuk.privat.data.source.remote.PrivatBankApiClient;
import com.havrylyuk.privat.util.Utility;

import java.io.IOException;

import retrofit2.Call;


/**
 *
 * Created by Igor Havrylyuk on 25.01.2017.
 */
public class SyncService extends IntentService {

    public static final String EXTRA_CITY = "search_city";
    public static final String EXTRA_ADDRESS = "search_address";
    public static final String EXTRA_KEY_SYNC ="com.havrylyuk.privat.intent.action.EXTRA_KEY_SYNC" ;

    private static final String LOG_TAG = SyncService.class.getSimpleName();
    private static final int START_SYNC = 1;
    private static final int END_SYNC = 0;


    public SyncService() {
        super("SyncService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            if (BuildConfig.DEBUG) Log.d(LOG_TAG, " Beginning network data synchronization ");
            long lStartTime = System.nanoTime();
            sendSyncStatus(START_SYNC);
            String reqCity = intent.getStringExtra(EXTRA_CITY);
            String reqAddress = intent.getStringExtra(EXTRA_ADDRESS);
            if(Utility.isNetworkAvailable(getBaseContext())){ //no network
                    if (BuildConfig.DEBUG) Log.d(LOG_TAG, "load atm");
                    AcquiringService service = PrivatBankApiClient.retrofit().create(AcquiringService.class);
                    Call<AcquiringResponse> responseCall = service.getAtms(reqAddress, reqCity);
                    try {
                        AcquiringResponse response =  responseCall.execute().body();
                        assert response != null;
                        ContentValues[] cv = new ContentValues[response.getAcquiringPoints().size()];
                        for (int i = 0; i < response.getAcquiringPoints().size(); i++) {
                            cv[i] = pointsToContentValues(response.getAcquiringPoints().get(i));
                        }
                        getContentResolver().bulkInsert(AcquiringEntry.CONTENT_URI, cv);
                        if (BuildConfig.DEBUG) Log.d(LOG_TAG, "load terminal");
                        responseCall = service.getTerminals(reqAddress, reqCity);
                        response =  responseCall.execute().body();
                        assert response != null;
                        cv = new ContentValues[response.getAcquiringPoints().size()];
                        for (int i = 0; i < response.getAcquiringPoints().size(); i++) {
                            cv[i] = pointsToContentValues(response.getAcquiringPoints().get(i));
                        }
                        getContentResolver().bulkInsert(AcquiringEntry.CONTENT_URI, cv);
                        sendSyncStatus(END_SYNC);
                    } catch (IOException  | OutOfMemoryError e) {
                        e.printStackTrace();
                    }
            } else {
                Log.w(LOG_TAG, "No internet connections");
                sendSyncStatus(END_SYNC);
            }
            long lEndTime = System.nanoTime();
            long timeElapsed = lEndTime - lStartTime;
            if (BuildConfig.DEBUG) Log.d(LOG_TAG,
                    " ---- End network synchronization duration=" + (timeElapsed / 1000000000) );
        }
    }

    private ContentValues pointsToContentValues(AcquiringPoint aPoint) {
        ContentValues cv = new ContentValues();
        cv.put(AcquiringEntry.ACQ_TYPE, aPoint.getType());
        cv.put(AcquiringEntry.ACQ_LAT, aPoint.getLatitude());
        cv.put(AcquiringEntry.ACQ_LON, aPoint.getLongitude());
        cv.put(AcquiringEntry.ACQ_TW, aPoint.getTimeWork().toString());
        switch (Utility.getLanguageIndex()) {
            case 0:
                cv.put(AcquiringEntry.ACQ_PLACE, aPoint.getPlaceRu());// no placeEn() :-(
                cv.put(AcquiringEntry.ACQ_CITY, aPoint.getCityEN());
                cv.put(AcquiringEntry.ACQ_FULL_ADR, aPoint.getFullAddressEn());
                break;
            case 1:
                cv.put(AcquiringEntry.ACQ_PLACE, aPoint.getPlaceRu());
                cv.put(AcquiringEntry.ACQ_CITY, aPoint.getCityRU());
                cv.put(AcquiringEntry.ACQ_FULL_ADR, aPoint.getFullAddressRu());
                break;
            case 2:
                cv.put(AcquiringEntry.ACQ_PLACE, aPoint.getPlaceUa());
                cv.put(AcquiringEntry.ACQ_CITY, aPoint.getCityUA());
                cv.put(AcquiringEntry.ACQ_FULL_ADR, aPoint.getFullAddressUa());
                break;
        }
        return cv;
    }

    // send sync status to activity 1-start sync 0 - stop
    private void sendSyncStatus(int status) {
        Intent intentUpdate = new Intent();
        intentUpdate.setAction(MapsActivity.SyncContentReceiver.SYNC_RESPONSE_STATUS);
        intentUpdate.addCategory(Intent.CATEGORY_DEFAULT);
        intentUpdate.putExtra(EXTRA_KEY_SYNC, status);
        sendBroadcast(intentUpdate);
    }

}
