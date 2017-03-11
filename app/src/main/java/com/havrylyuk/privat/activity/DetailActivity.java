package com.havrylyuk.privat.activity;

import android.Manifest;
import android.content.ContentValues;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.Toolbar;
import android.support.v7.app.ActionBar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
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
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.android.clustering.ClusterManager;
import com.havrylyuk.privat.data.source.local.AcquiringContract;
import com.havrylyuk.privat.data.source.remote.MapApiClient;
import com.havrylyuk.privat.data.source.remote.RouteMapService;
import com.havrylyuk.privat.R;
import com.havrylyuk.privat.maps.RouteType;
import com.havrylyuk.privat.maps.cluster.AcquiringClusterRenderer;
import com.havrylyuk.privat.maps.cluster.DetailInfoWindowAdapter;
import com.havrylyuk.privat.maps.cluster.PointItem;
import com.havrylyuk.privat.maps.RouteResponse;
import com.havrylyuk.privat.util.Utility;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.google.maps.android.SphericalUtil.computeDistanceBetween;

/**
 *
 * Created by Igor Havrylyuk on 27.01.2017.
 */

public class DetailActivity extends BaseActivity  implements LoaderManager.LoaderCallbacks<Cursor>,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener,
        OnMapReadyCallback {


    private static final String LOG_TAG = DetailActivity.class.getSimpleName();
    public static final String DETAIL_POINT_URI = "com.havrylyuk.privat.detail_point_uri";
    public static final String TRANSITION_NAME = "TRANSITION_NAME";

    public static final String[] DETAIL_COLUMNS = {
            AcquiringContract.AcquiringEntry.TABLE_NAME + "." + AcquiringContract.AcquiringEntry._ID,
            AcquiringContract.AcquiringEntry.TABLE_NAME + "." + AcquiringContract.AcquiringEntry.ACQ_TYPE,
            AcquiringContract.AcquiringEntry.TABLE_NAME + "." + AcquiringContract.AcquiringEntry.ACQ_PLACE,
            AcquiringContract.AcquiringEntry.TABLE_NAME + "." + AcquiringContract.AcquiringEntry.ACQ_CITY,
            AcquiringContract.AcquiringEntry.TABLE_NAME + "." + AcquiringContract.AcquiringEntry.ACQ_FULL_ADR,
            AcquiringContract.AcquiringEntry.TABLE_NAME + "." + AcquiringContract.AcquiringEntry.ACQ_LAT,
            AcquiringContract.AcquiringEntry.TABLE_NAME + "." + AcquiringContract.AcquiringEntry.ACQ_LON,
            AcquiringContract.AcquiringEntry.TABLE_NAME + "." + AcquiringContract.AcquiringEntry.ACQ_FAV,
            AcquiringContract.AcquiringEntry.TABLE_NAME + "." + AcquiringContract.AcquiringEntry.ACQ_TW
    };

    public static final int COL_ID = 0;
    public static final int COL_TYPE = 1;
    public static final int COL_PLACE = 2;
    public static final int COL_CITY = 3;
    public static final int COL_ADR = 4;
    public static final int COL_LAT = 5;
    public static final int COL_LON = 6;
    public static final int COL_FAV = 7;
    public static final int COL_TW = 8;

    private static final int DETAIL_LOADER = 1002;
    private Uri uri;
    private TextView poitTw;
    private TextView pointPlace;
    private FloatingActionButton fab;
    private boolean pointIsFavorite;
    private int pointId;
    private CollapsingToolbarLayout appBarLayout;
    private TextView pointCity;
    private TextView pointAddress;
    private String poinType;

    private GoogleMap map;
    private LatLng destination;
    ArrayList<LatLng> MarkerPoints;
    private Polyline routeLine;
    private LocationRequest locationRequest;
    private GoogleApiClient googleApiClient;
    private Marker currLocationMarker;
    private ClusterManager<PointItem> clusterManager;
    private String place;
    private PointItem destinationItem;
    private  TextView pointDestination;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        uri =  getIntent().getParcelableExtra(DETAIL_POINT_URI);
        appBarLayout = (CollapsingToolbarLayout) findViewById(R.id.toolbar_layout);
        poitTw = (TextView) findViewById(R.id.point_tw);
        pointPlace = (TextView) findViewById(R.id.point_place);
        pointAddress = (TextView) findViewById(R.id.point_address);
        pointCity = (TextView) findViewById(R.id.point_city);
        pointDestination = (TextView) findViewById(R.id.point_destination);
        initFabFavorite();
        setupMap();
        initToolBar();
        getSupportLoaderManager().initLoader(DETAIL_LOADER, null, this);

    }

    @Override
    protected int getLayout() {
        return R.layout.activity_detail;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void initToolBar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            ActionBar actionBar = getSupportActionBar();
            if (actionBar != null) {
                actionBar.setDisplayHomeAsUpEnabled(true);
                actionBar.setDisplayShowHomeEnabled(true);
            }
        }
    }

    private void setupMap() {
            if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                checkLocationPermission();
            }
            MarkerPoints = new ArrayList<>();
            if (!isGooglePlayServicesAvailable()) {
                Log.d(LOG_TAG, "Google Play Services not available. Ending Test case.");
                finish();
            }
            else {
                Log.d(LOG_TAG, "Google Play Services available. Continuing.");
            }
            SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.map);
            mapFragment.getMapAsync(this);
    }

    protected synchronized void buildGoogleApiClient() {
        googleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        googleApiClient.connect();
    }
    private void initFabFavorite() {
        fab = (FloatingActionButton) findViewById(R.id.fab_like);
        if (fab != null) {
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    updateFavorite(true);
                }
            });
        }
    }

    private void updateFavorite(boolean showSnackbar) {
        pointIsFavorite = !pointIsFavorite;
        ContentValues cv = new ContentValues();
        cv.put(AcquiringContract.AcquiringEntry.ACQ_FAV, pointIsFavorite);
        getContentResolver()
                .update(AcquiringContract.AcquiringEntry.CONTENT_URI, cv, AcquiringContract.AcquiringEntry._ID + " = ?", new String[]{String.valueOf(pointId)});
        String message;
        if (pointIsFavorite) {
            message = getString(R.string.add_favorite);
        } else{
            message = getString(R.string.remove_favorite);
        }
        if (showSnackbar) {
            Snackbar.make(fab, message , Snackbar.LENGTH_LONG)
                    .setAction(R.string.undo, cancelOnClickListener).show();
        }
    }

    View.OnClickListener cancelOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            updateFavorite(false);
            Toast.makeText(DetailActivity.this, R.string.cancel, Toast.LENGTH_LONG).show();
        }
    };

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if ( null != uri ) {
            return new CursorLoader(
                    this,
                    uri,
                    DETAIL_COLUMNS,
                    null,
                    null,
                    null
            );
        }
        return null;
    }

    private void addDestinationMarker(LatLng destination) {

        if (poinType.equalsIgnoreCase(getString(R.string.type_atm))) {
            destinationItem = new PointItem(destination,R.drawable.atm_kv);
        } else  {
            destinationItem = new PointItem(destination,R.drawable.pos_kv);
        }
        destinationItem.setId(pointId);
        destinationItem.setType(poinType);
        destinationItem.setPlaceRu(place);
        destinationItem.setRouteMap(true);
        clusterManager.addItem(destinationItem);
        clusterManager.cluster();
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        if (cursor != null && cursor.moveToFirst()) {
            pointId = cursor.getInt(COL_ID);
            destination = new LatLng(cursor.getDouble(COL_LAT), cursor.getDouble(COL_LON));
            if (appBarLayout != null) {
                appBarLayout.setTitle(Utility.getNameFromType(this,cursor.getString(COL_TYPE)));
                appBarLayout.setExpandedTitleColor(getResources().getColor(R.color.colorPrimaryDark));
                appBarLayout.setCollapsedTitleTextColor(getResources().getColor(android.R.color.white));
            } else if (getSupportActionBar() != null) {
                        getSupportActionBar(). setTitle(Utility.getNameFromType(this,cursor.getString(COL_TYPE)));
            }
            poinType = cursor.getString(COL_TYPE);
            place = cursor.getString(COL_PLACE);
            if (pointPlace != null)
                pointPlace.setText(getString(R.string.format_place, Utility.normalizeStr(place)));
            String address = cursor.getString(COL_ADR);
            if (pointAddress != null) pointAddress.setText(getString(R.string.format_address,address));
            String city = cursor.getString(COL_CITY);
            if (pointCity != null) pointCity.setText(getString(R.string.format_city, city));
            String tw = cursor.getString(COL_TW);
            String[] days = tw.split(",");
            if (null != poitTw && days.length > 7) {
                poitTw.setText(getString(R.string.format_time_work, getString(R.string.mon) + days[0],
                        getString(R.string.tue) + days[1], getString(R.string.wed) + days[2],
                        getString(R.string.thu) + days[3], getString(R.string.fri) + days[4],
                        getString(R.string.sat) + days[5], getString(R.string.sun) + days[6],
                        getString(R.string.hol) + days[7]));
            }
            pointIsFavorite = cursor.getInt(COL_FAV) == 1;
            if (null != fab) {
                fab.setImageResource(pointIsFavorite ? R.drawable.fab_dislike : R.drawable.fab_like);
            }
        } else {
            Toast.makeText(this,getString(R.string.nothing_found),Toast.LENGTH_SHORT).show();
        }

    }
    @Override
    public void onLoaderReset(Loader<Cursor> loader) { }


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

    @Override
    public void onLocationChanged(Location location) {
        if (currLocationMarker != null) {
            currLocationMarker.remove();
        }
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        markerOptions.title(getString(R.string.self_position));
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA));
        currLocationMarker = map.addMarker(markerOptions);
        addDestinationMarker(destination);
        if (currLocationMarker != null && destinationItem != null) {
            drawRoute(currLocationMarker.getPosition(), destinationItem.getPosition(), "driving");
            LatLngBounds.Builder latLngBuilder = new LatLngBounds.Builder();
            latLngBuilder.include(currLocationMarker.getPosition());
            latLngBuilder.include(destinationItem.getPosition());
            LatLngBounds latLngBounds = latLngBuilder.build();
            CameraUpdate track = CameraUpdateFactory.newLatLngBounds(latLngBounds, 120);
            map.moveCamera(track);

        }
        if (googleApiClient != null) {
            LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient, this);
        }

    }
    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        map.getUiSettings().setMapToolbarEnabled(true);
        map.getUiSettings().setZoomControlsEnabled(true);
        clusterManager = new ClusterManager<PointItem>(this, map);
        AcquiringClusterRenderer clusterRenderer = new AcquiringClusterRenderer(this,map,clusterManager);
        clusterRenderer.setDimension((int) getResources().getDimension(R.dimen.custom_point_image));
        clusterManager.setRenderer(clusterRenderer);
        map.setOnCameraIdleListener(clusterManager);
        map.setOnMarkerClickListener(clusterManager);
        map.setInfoWindowAdapter(clusterManager.getMarkerManager());
        map.setOnInfoWindowClickListener(clusterManager);
        clusterManager.getMarkerCollection().setOnInfoWindowAdapter( new DetailInfoWindowAdapter(this));
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
    }

    private boolean isGooglePlayServicesAvailable() {
        GoogleApiAvailability googleAPI = GoogleApiAvailability.getInstance();
        int result = googleAPI.isGooglePlayServicesAvailable(this);
        if(result != ConnectionResult.SUCCESS) {
            if(googleAPI.isUserResolvableError(result)) {
                googleAPI.getErrorDialog(this, result,
                        0).show();
            }
            return false;
        }
        return true;
    }

    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;

    public boolean checkLocationPermission(){
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            }
            return false;
        } else {
            return true;
        }
    }

    private void drawRoute(LatLng origin, LatLng dest, String type) {
        // type walking, bicycling, transit, driving - default
        RouteMapService service = MapApiClient.retrofit().create(RouteMapService.class);
        Call<RouteResponse> responseCall = service.getRoute("metric", origin.latitude + "," + origin.longitude, dest.latitude + "," + dest.longitude, type);
        responseCall.enqueue(new Callback<RouteResponse>() {
            @Override
            public void onResponse(Call<RouteResponse> call, Response<RouteResponse> response) {
                try {
                    if (routeLine != null) {
                        routeLine.remove();
                    }
                    LatLngBounds.Builder latLngBuilder = new LatLngBounds.Builder();
                    for (int i = 0; i < response.body().getRoutes().size(); i++) {
                        String distance = response.body().getRoutes().get(i).getLegs().get(i).getDistance().getText();
                        String time = response.body().getRoutes().get(i).getLegs().get(i).getDuration().getText();
                        if (pointDestination != null) {
                            pointDestination.setText(getString(R.string.format_dest_time, distance, time));
                        }
                        String encodedString = response.body().getRoutes().get(0).getOverviewPolyline().getPoints();
                        List<LatLng> list = Utility.decodePoly(encodedString);
                        routeLine = map.addPolyline(new PolylineOptions()
                                .addAll(list)
                                .width(10)
                                .color(Color.RED)
                                .geodesic(true)
                        );
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<RouteResponse> call, Throwable t) {
                Toast.makeText(DetailActivity.this, R.string.error_build_route,Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}
