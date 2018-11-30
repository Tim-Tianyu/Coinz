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

import com.google.gson.JsonObject;
import com.mapbox.android.core.location.LocationEngine;
import com.mapbox.android.core.location.LocationEngineListener;
import com.mapbox.android.core.location.LocationEnginePriority;
import com.mapbox.android.core.location.LocationEngineProvider;
import com.mapbox.android.core.permissions.PermissionsListener;
import com.mapbox.android.core.permissions.PermissionsManager;
import com.mapbox.geojson.Feature;
import com.mapbox.geojson.FeatureCollection;
import com.mapbox.geojson.Geometry;
import com.mapbox.geojson.Point;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.annotations.Icon;
import com.mapbox.mapboxsdk.annotations.IconFactory;
import com.mapbox.mapboxsdk.annotations.Marker;
import com.mapbox.mapboxsdk.annotations.MarkerOptions;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.location.modes.CameraMode;
import com.mapbox.mapboxsdk.location.modes.RenderMode;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.plugins.locationlayer.LocationLayerPlugin;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;
import java.util.Objects;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback, LocationEngineListener, PermissionsListener {
    private MapView mapView;
    private MapboxMap map;
    private PermissionsManager permissionsManager;
    private LocationEngine locationEngine;
    private LocationLayerPlugin locationLayerPlugin;
    private Location originLocation;
    private String tag = "MAP";
    private String jsonString;
    private Icon iconDolr, iconPenny, iconQuid, iconShil, iconBlack;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Mapbox.getInstance(this,  getString(R.string.mapbox_access_token));
        setContentView(R.layout.activity_map);
        Intent intent = getIntent();
        jsonString = intent.getStringExtra("GEO_JSON");
        double rateShil, rateDolr, ratePenny, rateQuid;;

        try {
            JSONObject jsonObject = new JSONObject(jsonString);
            JSONObject rates = jsonObject.getJSONObject("rates");
            rateShil = rates.getDouble("SHIL");
            rateDolr = rates.getDouble("DOLR");
            ratePenny = rates.getDouble("PENNY");
            rateQuid = rates.getDouble("QUID");
        } catch (JSONException e) {
            e.printStackTrace();
            rateShil = 1.0;
            rateDolr = 1.0;
            ratePenny = 1.0;
            rateQuid = 1.0;
        }

        FeatureCollection featureCollection = FeatureCollection.fromJson(jsonString);
        for (Feature feature : Objects.requireNonNull(featureCollection.features())){
            Geometry geometry = feature.geometry();
            Point point = (Point) geometry;
            JsonObject properties =  feature.properties();
            String currency = Objects.requireNonNull(properties).get("currency").getAsString();
            String symbol = Objects.requireNonNull(properties).get("marker-symbol").getAsString();
            String id = Objects.requireNonNull(properties).get("id").getAsString();
            String value = Objects.requireNonNull(properties).get("value").getAsString();

            if (point == null) throw new AssertionError();
            List<Double> coordinates = point.coordinates();
            LatLng position = new LatLng(coordinates.get(1), coordinates.get(0));
            Coin.coinsList.add(new Coin(id, Double.parseDouble(value), currency, symbol, position));
        }

        Bank.theBank = new Bank(20, 0,rateDolr, rateQuid, rateShil, ratePenny, 5,10,10,10,10);

        Button btnWallet = (Button) findViewById(R.id.activity_map_btn_wallet);
        btnWallet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MapActivity.this, WalletActivity.class));
            }
        });
        Button btnBank = (Button) findViewById(R.id.activity_map_btn_bank);
        btnBank.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MapActivity.this, BankActivity.class));
            }
        });
        Button btnFriend = (Button) findViewById(R.id.activity_map_btn_friends);
        btnFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MapActivity.this, FriendActivity.class));
            }
        });

        mapView = (MapView) findViewById(R.id.activity_map_mv_map);
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
            map.getUiSettings().setZoomControlsEnabled(true);
            map.setOnMarkerClickListener(new MapboxMap.OnMarkerClickListener() {
                @Override
                public boolean onMarkerClick(@NonNull Marker marker) {
                    Coin coin = Coin.getCoinByMarker(marker);
                    if (coin != null && Coin.inRanged(originLocation, coin)) {
                        Toast.makeText(MapActivity.this, "Collected " + Double.toString(coin.getValue()) + " " + coin.getCurrency(), Toast.LENGTH_LONG).show();
                        Coin.collectedCoinsList.add(coin);
                        map.removeMarker(marker);
                    } else if (coin == null) {
                        Log.d(tag, "Unknown coins");
                    } else {
                        Toast.makeText(MapActivity.this, "Out of Range", Toast.LENGTH_LONG).show();
                    }
                    return true;
                }
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
            case "PENNY":
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
            permissionsManager = new PermissionsManager(this);
            permissionsManager.requestLocationPermissions(this);
        }
    }

    @SuppressWarnings("MissingPermission")
    private void initializeLocationEngine() {
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
                locationLayerPlugin = new LocationLayerPlugin(mapView, map, locationEngine);
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
            originLocation = location;
            setCameraPosition(location);
        }
    }


    @Override
    @SuppressWarnings("MissingPermission")
    public void onConnected() {
        Log.d(tag, "[onConnected] requesting location updates");
        locationEngine.requestLocationUpdates();
    }

    @Override
    public void onExplanationNeeded(List<String> permissionsToExplain){
        Log.d(tag, "Permissions: " + permissionsToExplain.toString());
        // Present toast or dialog.
    }

    @Override
    public void onPermissionResult(boolean granted) {
        Log.d(tag, "[onPermissionResult] granted == " + granted);
        if (granted) {
            enableLocation();
        } else {
            // Open a dialogue with the user
        }
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
}
