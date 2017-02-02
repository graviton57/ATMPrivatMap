package com.havrylyuk.privat.activity;

import android.Manifest;
import android.app.SearchManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.CursorAdapter;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.widget.SearchView;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.util.Log;

import android.location.Address;
import android.support.v4.content.ContextCompat;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterItem;
import com.google.maps.android.clustering.ClusterManager;
import com.havrylyuk.privat.R;
import com.havrylyuk.privat.maps.cluster.MarkerInfoWindowAdapter;
import com.havrylyuk.privat.data.source.local.AcquiringContract.AcquiringEntry;
import com.havrylyuk.privat.maps.cluster.AcquiringClusterRenderer;
import com.havrylyuk.privat.maps.cluster.PointItem;
import com.havrylyuk.privat.service.SyncService;
import com.havrylyuk.privat.util.PreferencesHelper;
import com.havrylyuk.privat.util.Utility;


import java.io.IOException;
import java.util.List;
import java.util.Locale;


/**
 * Maps Activity
 * Created by Igor Havrylyuk on 26.01.2017.
 */
public class MapsActivity extends FragmentActivity implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener,
        GoogleMap.OnMapClickListener,
        ClusterManager.OnClusterClickListener<PointItem>,
        ClusterManager.OnClusterInfoWindowClickListener<PointItem>,
        ClusterManager.OnClusterItemInfoWindowClickListener<PointItem>,
        LoaderManager.LoaderCallbacks<Cursor> {

    private static final String LOG_TAG = MapsActivity.class.getSimpleName();

    private static final int POINTS_LOADER = 0;
    private static final int SUGGEST_LOADER = 1;


    private GoogleMap map;
    GoogleApiClient googleApiClient;
    Location lastLocation;

    LocationRequest locationRequest;
    String requestedCity;

    private ProgressBar progressBar;
    private ClusterManager<PointItem> clusterManager;
    private PointItem destinationPoint; //destination position
    Marker currLocationMarker;//original user position
    private SearchView searchView;
    private SimpleCursorAdapter suggestionsAdapter;
    private PreferencesHelper pref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        pref = PreferencesHelper.getInstance();
        setContentView(R.layout.activity_maps);
        setupSearchView();
        IntentFilter filter = new IntentFilter(SyncContentReceiver.SYNC_RESPONSE_STATUS);
        filter.addCategory(Intent.CATEGORY_DEFAULT);
        syncContentReceiver = new SyncContentReceiver();
        registerReceiver(syncContentReceiver, filter);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.action_favorites:
                                startActivity(new Intent(MapsActivity.this, FavoritesActivity.class));
                                 break;
                            case R.id.action_share:
                                if (currLocationMarker != null) {
                                    Intent shareIntent = new Intent(Intent.ACTION_SEND);
                                    shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
                                    shareIntent.setType("text/plain");
                                    String share = "geo:"+currLocationMarker.getPosition().latitude+
                                            ","+currLocationMarker.getPosition().longitude;
                                    shareIntent.putExtra(Intent.EXTRA_TEXT, share);
                                    startActivity(shareIntent);
                                }
                                break;
                            case R.id.action_settings:
                                startActivity(new Intent(MapsActivity.this, SettingsActivity.class));
                                break;
                        }
                        return true;
                    }
                });

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkLocationPermission();
        }
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(syncContentReceiver);
        super.onDestroy();
    }

    private  void  setupSearchView() {
         searchView = (SearchView) findViewById(R.id.searchView);
         SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
         searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
         searchView.setImeOptions(EditorInfo.IME_ACTION_SEARCH);
         suggestionsAdapter = new SimpleCursorAdapter(
                 this, android.R.layout.simple_list_item_1, null,
                 new String[] { SearchManager.SUGGEST_COLUMN_TEXT_1 },
                 new int[] { android.R.id.text1 });
         searchView.setSuggestionsAdapter(suggestionsAdapter);
         searchView.setOnTouchListener(new View.OnTouchListener() {
             @Override
             public boolean onTouch(View v, MotionEvent event) {
                 if (event.getAction() == MotionEvent.ACTION_DOWN) {
                     hideKeyboard();
                 }
                 return false;
             }
         });
         searchView.setOnSuggestionListener(new SearchView.OnSuggestionListener() {
             @Override
             public boolean onSuggestionSelect(int position) {
                 return true;
             }

             @Override
             public boolean onSuggestionClick(int position) {
                 CursorAdapter cursorAdapter = searchView.getSuggestionsAdapter();
                 Cursor cursor = cursorAdapter.getCursor();
                 cursor.moveToPosition(position);
                 String query = cursor.getString(cursor.getColumnIndex(SearchManager.SUGGEST_COLUMN_TEXT_1));
                 searchView.setQuery(query,true);
                 requestedCity = query;
                 getSupportLoaderManager().restartLoader(POINTS_LOADER, null, MapsActivity.this);
                 hideKeyboard();
                 return true;
             }
         });
         searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
             @Override
             public boolean onQueryTextChange(String query) {
                 if (query.length() >= pref.getSuggestionsCount(getString(R.string.pref_suggestions_start_count_key))) {
                     requestedCity = query;
                     getSupportLoaderManager().restartLoader(SUGGEST_LOADER, null, MapsActivity.this);
                 }
                 return true;
             }
             @Override
             public boolean onQueryTextSubmit(String query) {
                     requestedCity = query;
                     searchView.clearFocus();
                     searchView.getSuggestionsAdapter().changeCursor(null);
                     getSupportLoaderManager().restartLoader(POINTS_LOADER, null, MapsActivity.this);
                     return true;
             }
         });

     }

    private void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(searchView.getWindowToken(), 0);
    }
    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        map.getUiSettings().setMapToolbarEnabled(true);
        map.setOnMapClickListener(this);
        clusterManager = new ClusterManager<PointItem>(this, map);
        clusterManager.setRenderer(new AcquiringClusterRenderer(this,map,clusterManager));
        map.setOnCameraIdleListener(clusterManager);
        map.setOnMarkerClickListener(clusterManager);
        map.setInfoWindowAdapter(clusterManager.getMarkerManager());
        map.setOnInfoWindowClickListener(clusterManager);
        clusterManager.setOnClusterItemInfoWindowClickListener(this);
        clusterManager.setOnClusterItemClickListener(new ClusterManager.OnClusterItemClickListener<PointItem>() {
                    @Override
                    public boolean onClusterItemClick(PointItem item) {
                        destinationPoint = item;
                        return false;
                    }
                });
        clusterManager.setOnClusterClickListener(this);
        clusterManager.setOnClusterInfoWindowClickListener(this);
        clusterManager.getMarkerCollection().setOnInfoWindowAdapter( new MarkerInfoWindowAdapter(this));
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                buildGoogleApiClient();
                map.setMyLocationEnabled(true);
            }
        }
        else {
            buildGoogleApiClient();
            map.setMyLocationEnabled(true);
        }
        getSupportLoaderManager().initLoader(POINTS_LOADER, null, this);//load data
    }

    protected synchronized void buildGoogleApiClient() {
        googleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        googleApiClient.connect();
    }

    @Override
    public void onConnected(Bundle bundle) {
        locationRequest = new LocationRequest();
        locationRequest.setInterval(1000);
        locationRequest.setFastestInterval(1000);
        locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        int permission =  ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
        if (permission == PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, this);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        //
    }
    private void addLocationMarker(Location location) {
        lastLocation = location;
        if (currLocationMarker != null) {
            currLocationMarker.remove();
        }
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        markerOptions.title(getString(R.string.self_position));
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA));
         if (pref.isShowMarker(getString(R.string.pref_marker_switch_key)) && map!=null) {
            currLocationMarker = map.addMarker(markerOptions);
        }
    }
    @Override
    public void onLocationChanged(Location location) {

        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        addLocationMarker(location);
        map.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        map.animateCamera(CameraUpdateFactory.zoomTo(11));
        parseLocation(location);
        if (googleApiClient != null) {
            LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient, this);
        }
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    public static final int AQC_PERMISSIONS_REQUEST_LOCATION = 99;

    public boolean checkLocationPermission(){
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, AQC_PERMISSIONS_REQUEST_LOCATION);
            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        AQC_PERMISSIONS_REQUEST_LOCATION);
            }
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case AQC_PERMISSIONS_REQUEST_LOCATION: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ContextCompat.checkSelfPermission(this,
                            Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                        if (googleApiClient == null) {
                            buildGoogleApiClient();
                        }
                        map.setMyLocationEnabled(true);
                    }

                } else {
                    Toast.makeText(this, R.string.permission_denied, Toast.LENGTH_LONG).show();
                }
                return;
            }
        }
    }

    private void parseLocation(Location location) {
       Geocoder geocoder = new Geocoder(getBaseContext(), Locale.getDefault());
       List<Address> addresses;
        try {
            addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
            if (addresses != null && addresses.size() > 0) {
                //requestedCity = addresses.get(0).getLocality();
                requestedCity = "Рус"; //todo remove this line
                String requestedAddress = addresses.get(0).getAddressLine(0);
                Log.d(LOG_TAG, "You current cityname=" + requestedCity);
                Log.d(LOG_TAG, "You current address=" + requestedAddress);
                getSupportLoaderManager().restartLoader(POINTS_LOADER, null, this);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        switch (id) {
            case POINTS_LOADER:
                if (progressBar != null) progressBar.setVisibility(View.VISIBLE);
                String selection = Utility.buildRequestedSelection(this);
                String[] selAgrs = Utility.buildRequestedArgs(this, requestedCity);
                Log.d(LOG_TAG, "start points loader selection= "+selection);
                Log.d(LOG_TAG, "args="+"%"+requestedCity+"%");
                return new CursorLoader(this,
                        AcquiringEntry.CONTENT_URI,
                        DetailActivity.DETAIL_COLUMNS,
                        selection,
                        selAgrs,
                        null);

            case SUGGEST_LOADER:
                return new CursorLoader(this,
                        AcquiringEntry.CONTENT_URI,
                        new String[]{AcquiringEntry._ID, AcquiringEntry.ACQ_FULL_ADR + " AS " + SearchManager.SUGGEST_COLUMN_TEXT_1},
                        Utility.buildSuggestSelection(requestedCity),
                        Utility.buildSuggestArgs(requestedCity),
                        null);
        }

        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        switch (loader.getId()) {
            case SUGGEST_LOADER:
                searchView.getSuggestionsAdapter().changeCursor(cursor);
                break;
            case POINTS_LOADER:
                if (cursor != null && cursor.moveToFirst()) {
                    clusterManager.clearItems();
                    map.clear();
                    if (lastLocation != null) {
                        addLocationMarker(lastLocation);
                    }
                    LatLngBounds.Builder latLngBuilder = new LatLngBounds.Builder();
                    do {
                        float lat = cursor.getFloat(DetailActivity.COL_LAT);
                        float lon = cursor.getFloat(DetailActivity.COL_LON);
                        LatLng point = new LatLng(lat, lon);
                        PointItem pointItem = new PointItem(point);
                        String type = cursor.getString(DetailActivity.COL_TYPE);
                        if (type.equalsIgnoreCase(getString(R.string.type_atm))) {
                            pointItem.setIcon(R.drawable.atm_kv);
                        } else {
                            pointItem.setIcon(R.drawable.pos_kv);
                        }
                        pointItem.setId(cursor.getLong(DetailActivity.COL_ID));
                        pointItem.setType(type);
                        pointItem.setPlaceRu(cursor.getString(DetailActivity.COL_PLACE));
                        latLngBuilder.include(point);
                        clusterManager.addItem(pointItem);
                    } while (cursor.moveToNext());
                    greenToast(getString(R.string.format_found_points,String.valueOf(cursor.getCount())));
                    clusterManager.cluster();
                    LatLngBounds latLngBounds = latLngBuilder.build();
                    CameraUpdate track = CameraUpdateFactory.newLatLngBounds(latLngBounds, 25);
                    map.moveCamera(track);
                } else {
                    if (!TextUtils.isEmpty(requestedCity))  greenToast(getString(R.string.nothing_found));
                }
                if (progressBar != null) progressBar.setVisibility(View.GONE);
                break;
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
    private void greenToast(String message) {
        final SpannableString spannableString = new SpannableString(message);
        spannableString.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.colorPrimary)), 0, spannableString.length(), 0);
        Toast.makeText(this,spannableString,Toast.LENGTH_SHORT).show();
    }

    // Clusters listeners
    @Override
    public boolean onClusterClick(Cluster<PointItem> cluster) {
        LatLngBounds.Builder builder = LatLngBounds.builder();
        for (ClusterItem item : cluster.getItems()) {
            builder.include(item.getPosition());
        }
        final LatLngBounds bounds = builder.build();
        try {
            map.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 50));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }

    @Override
    public void onClusterInfoWindowClick(Cluster<PointItem> cluster) {
        //
    }

    @Override
    public void onClusterItemInfoWindowClick(PointItem pointItem) {
        Bundle args = new Bundle();
        Intent intent = new Intent(this, DetailActivity.class);
        Uri uri = AcquiringEntry.buildAcquiringUri(destinationPoint.getId());
        args.putParcelable(DetailActivity.DETAIL_POINT_URI, uri);
        intent.putExtras(args);
        startActivity(intent);
    }


    @Override
    public void onMapClick(LatLng latLng) {
     //
    }

    private SyncContentReceiver syncContentReceiver;



    public class SyncContentReceiver extends BroadcastReceiver {
        public static final String SYNC_RESPONSE_STATUS = "com.havrylyuk.privat.intent.action.SYNC_RESPONSE_STATUS";
        @Override
        public void onReceive(Context context, Intent intent) {
            boolean sync = intent.getIntExtra(SyncService.EXTRA_KEY_SYNC, 0) == 1;
            if (!sync) {
                greenToast(getString(R.string.sync_complete));
            }
        }
    }




}
