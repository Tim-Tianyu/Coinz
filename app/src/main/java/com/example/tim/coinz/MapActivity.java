package com.example.tim.coinz;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.mapbox.android.core.location.LocationEngine;
import com.mapbox.android.core.location.LocationEngineListener;
import com.mapbox.android.core.location.LocationEnginePriority;
import com.mapbox.android.core.location.LocationEngineProvider;
import com.mapbox.android.core.permissions.PermissionsListener;
import com.mapbox.android.core.permissions.PermissionsManager;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.annotations.Icon;
import com.mapbox.mapboxsdk.annotations.IconFactory;
import com.mapbox.mapboxsdk.annotations.Marker;
import com.mapbox.mapboxsdk.annotations.MarkerOptions;
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.location.modes.CameraMode;
import com.mapbox.mapboxsdk.location.modes.RenderMode;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.plugins.locationlayer.LocationLayerPlugin;

import java.util.List;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback, LocationEngineListener{
    private MapView mapView;
    private MapboxMap map;
    private LocationEngine locationEngine;
    private Location originLocation;
    private String tag = "MAP";
    private Icon iconDolr, iconPenny, iconQuid, iconShil, iconBlack;
    private PermissionsManager permissionsManager;
    private FirebaseAuth mAuth;
    static final Boolean NORMAL = true;
    static final Boolean TREASURE_HUNT = false;
    static Boolean selectedMode = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Mapbox.getInstance(this,  getString(R.string.mapbox_access_token));
        setContentView(R.layout.activity_map);
        mAuth = FirebaseAuth.getInstance();

        Button btnWallet = findViewById(R.id.activity_map_btn_wallet);
        btnWallet.setOnClickListener(v -> startActivity(new Intent(MapActivity.this, WalletActivity.class)));
        Button btnBank = findViewById(R.id.activity_map_btn_bank);
        btnBank.setOnClickListener(v -> startActivity(new Intent(MapActivity.this, BankActivity.class)));
        Button btnFriend = findViewById(R.id.activity_map_btn_friends);
        btnFriend.setOnClickListener(v -> startActivity(new Intent(MapActivity.this, FriendActivity.class)));
        Button btnLogOut = findViewById(R.id.activity_map_btn_log_out);
        btnLogOut.setOnClickListener(v -> {
            Request.detachAllListener();
            User.detachAllListener();
            Gift.detachAllListener();
            mAuth.signOut();
            startActivity(new Intent(MapActivity.this, MainActivity.class));
            FirebaseListener.clearCurrentFirestoreData();
            finish();
        });
        Button btnReward = findViewById(R.id.activity_map_btn_reward);
        btnReward.setOnClickListener(v -> startActivity(new Intent(MapActivity.this, rewardActivity.class)));

        permissionsManager = new PermissionsManager(new PermissionsListener() {
            @Override
            public void onExplanationNeeded(List<String> permissionsToExplain) {
                Log.d(tag, "Permissions: " + permissionsToExplain.toString());
                // Present toast or dialog.
            }

            @Override
            public void onPermissionResult(boolean granted) {
                Log.d(tag, "[onPermissionResult] granted == " + granted);
                if (granted) {
                    enableLocation();
                } else {
                    enableLocation();
                }
            }
        });

        mapView = findViewById(R.id.activity_map_mv_map);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);
        IconFactory iconFactory = IconFactory.getInstance(MapActivity.this);
        iconDolr = iconFactory.fromBitmap(SVGtoBitmap.getBitmap(MapActivity.this,R.drawable.ic_baseline_room_dolr));
        iconQuid = iconFactory.fromBitmap(SVGtoBitmap.getBitmap(MapActivity.this,R.drawable.ic_baseline_room_quid));
        iconPenny = iconFactory.fromBitmap(SVGtoBitmap.getBitmap(MapActivity.this,R.drawable.ic_baseline_room_penny));
        iconShil = iconFactory.fromBitmap(SVGtoBitmap.getBitmap(MapActivity.this,R.drawable.ic_baseline_room_shil));
        iconBlack = iconFactory.fromBitmap(SVGtoBitmap.getBitmap(MapActivity.this,R.drawable.ic_baseline_room_24px));
    }

    @Override
    public void onMapReady(MapboxMap mapboxMap) {
        if (mapboxMap == null) {
            Log.d(tag, "[onMapReady] mapBox is null");
        } else {
            map = mapboxMap;
            // Set user interface options
            map.getUiSettings().setCompassEnabled(true);
            map.getUiSettings().setZoomControlsEnabled(false);
            map.setMaxZoomPreference(200.0);
            map.setOnMarkerClickListener(marker -> {
                Coin coin = Coin.getCoinByMarker(marker);
                if (coin != null && Coin.inRanged(originLocation, coin)) {
                    Toast.makeText(MapActivity.this, "Collected " + Double.toString(coin.getValue()) + " " + coin.getCurrency(), Toast.LENGTH_LONG).show();
                    Coin.collectCoin(coin);
                    map.removeMarker(marker);
                } else if (coin == null) {
                    Log.d(tag, "Unknown coins");
                } else {
                    Toast.makeText(MapActivity.this, "Out of Range", Toast.LENGTH_LONG).show();
                }
                return true;
            });
            // Make location information available
            enableLocation();
        }
        for (Coin coin : Coin.coinsList){
            Marker marker = map.addMarker(new MarkerOptions().icon(getIconByName(coin.getCurrency().name())).title(coin.getCurrency().name()).snippet(coin.getSymbol()).position(coin.getPosition()));
            coin.setMarker(marker);
        }
    }

    private Icon getIconByName(String name){
        switch (name) {
            case "DOLR":
                return iconDolr;
            case "QUID":
                return iconQuid;
            case "PENY":
                return iconPenny;
            case "SHIL":
                return iconShil;
            default:
                Log.d(tag, "Unknown coins");
                return iconBlack;
        }
    }

    private void enableLocation() {
        if (PermissionsManager.areLocationPermissionsGranted(this)) {
            Log.d(tag, "Permissions are granted");
            initializeLocationEngine();
            initializeLocationLayer();
        } else {
            Log.d(tag, "Permissions are not granted");
            permissionsManager.requestLocationPermissions(this);
        }
    }

    @SuppressWarnings("MissingPermission")
    private void initializeLocationEngine() {
        LatLng latLng = new LatLng(-3.188758, 55.943680);
        map.setCameraPosition(new CameraPosition.Builder().target(latLng).build());
        locationEngine = new LocationEngineProvider(this)
                .obtainBestLocationEngineAvailable();
        locationEngine.setInterval(5000); // preferably every 5 seconds
        locationEngine.setFastestInterval(1000); // at most every second
        locationEngine.setPriority(LocationEnginePriority.HIGH_ACCURACY);
        locationEngine.activate();

        Location lastLocation = locationEngine.getLastLocation();
        if (lastLocation != null) {
            originLocation = lastLocation;
            setCameraPosition(lastLocation);
        } else {
            locationEngine.addLocationEngineListener(this);
        }
    }

    @SuppressWarnings("MissingPermission")
    private void initializeLocationLayer() {
        if (mapView == null) {
            Log.d(tag, "mapView is null");
        } else {
            if (map == null) {
                Log.d(tag, "map is null");
            } else {
                LocationLayerPlugin locationLayerPlugin = new LocationLayerPlugin(mapView, map, locationEngine);
                locationLayerPlugin.setLocationLayerEnabled(true);
                locationLayerPlugin.setCameraMode(CameraMode.TRACKING);
                locationLayerPlugin.setRenderMode(RenderMode.NORMAL);
            }
        }
    }

    private void setCameraPosition(Location location) {
        LatLng latLng = new LatLng(location.getLatitude(),
                location.getLongitude());
        map.animateCamera(CameraUpdateFactory.newLatLng(latLng));
    }

    @Override
    public void onLocationChanged(Location location) {
        if (location == null) {
            Log.d(tag, "[onLocationChanged] location is null");
        } else {
            Log.d(tag, "[onLocationChanged] location is not null");
            if (originLocation != null && ! recordWalkingDistance(originLocation, location)){
                Toast.makeText(MapActivity.this, "Going too fast", Toast.LENGTH_SHORT);
            }
            originLocation = location;
            setCameraPosition(location);
            reRenderMarkers();
        }
    }

    private boolean recordWalkingDistance(Location prev, Location now){
        float max = 50;
        float dist =  prev.distanceTo(now);
        if (dist > max) return false;
        User.walkingDistance += dist;
        return true;
    }

    private void reRenderMarkers(){
        for (Coin coin : Coin.coinsList){
            Marker marker = coin.getMarker();
            if (Coin.inViewRange(originLocation, coin)){
                if (marker == null){
                    marker = map.addMarker(new MarkerOptions().icon(getIconByName(coin.getCurrency().name())).title(coin.getCurrency().name()).snippet(coin.getSymbol()).position(coin.getPosition()));
                    coin.setMarker(marker);
                }
            } else {
                if (marker != null){
                    map.removeMarker(marker);
                    coin.setMarker(null);
                }
            }
        }
    }


    @Override
    @SuppressWarnings("MissingPermission")
    public void onConnected() {
        Log.d(tag, "[onConnected] requesting location updates");
        locationEngine.requestLocationUpdates();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        permissionsManager.onRequestPermissionsResult(requestCode, permissions, grantResults);
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onStart() {
        super.onStart();
        mapView.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
        mapView.onStop();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }
}
