package scu.edu.assignment4;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private LocationManager myLocationManager;
    private LocationManager myLocationManager_2;
    private String provider;
    private String provider_2;
    private static final String TAG = "MapsActivity";
    private Marker current;
    private Marker home;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        Button button = findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateCurrent();
            }
        });


        myLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        myLocationManager_2 = (LocationManager) getSystemService(Context.LOCATION_SERVICE);


        List<String> list = myLocationManager.getProviders(true);
        List<String> list_2 = myLocationManager_2.getProviders(true);


        if (list.contains(LocationManager.GPS_PROVIDER)) {
            provider = LocationManager.GPS_PROVIDER;
        } else if (list.contains(LocationManager.NETWORK_PROVIDER)) {
            provider = LocationManager.NETWORK_PROVIDER;

        } else {
            Toast.makeText(this, "please open the network or gps",
                    Toast.LENGTH_LONG).show();
            return;
        }

        if (list_2.contains(LocationManager.GPS_PROVIDER)) {
            provider_2 = LocationManager.GPS_PROVIDER;
        } else if (list.contains(LocationManager.NETWORK_PROVIDER)) {
            provider_2 = LocationManager.NETWORK_PROVIDER;

        } else {
            Toast.makeText(this, "please open the network or gps",
                    Toast.LENGTH_LONG).show();
            return;
        }

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        myLocationManager_2.requestLocationUpdates(provider, 30000, 0, new LocationListener() {
            @Override
            public void onLocationChanged(@NonNull Location location) {
                Log.d(TAG, "auto get current 2 :" + location.getLatitude() + ":" + location.getLongitude());
                current.setPosition(new LatLng(location.getLatitude(), location.getLongitude()));
            }
        });
        myLocationManager.requestLocationUpdates(provider, 3000, 0, new LocationListener() {
            @Override
            public void onLocationChanged(@NonNull Location location) {
                Log.d(TAG, "auto get current" + location.getLatitude() + ":" + location.getLongitude());
                //current.setPosition(new LatLng(location.getLatitude(), location.getLongitude()));
            }
        });


    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;


        LatLng currentLoc = getCurrentLocation();

        current =  mMap.addMarker(new MarkerOptions().position(currentLoc).title("Current"));
        current.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));

        LatLng myHome = new LatLng(37.41154444,-121.90666763);
        home = mMap.addMarker(new MarkerOptions().position(myHome).title("Home"));;


       // mMap.animateCamera(CameraUpdateFactory.zoomIn());
       // mMap.animateCamera(CameraUpdateFactory.zoomTo(10), 2000, null);
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLoc, 14.0f));
        mMap.setIndoorEnabled(true);
        mMap.setBuildingsEnabled(true);
        //mMap.setTrafficEnabled(true);

        mMap.getUiSettings().setZoomControlsEnabled(true);

        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                Log.d(TAG, "you tap" + latLng.latitude + ":" + latLng.longitude);
                if (home == null) {
                    home = mMap.addMarker(new MarkerOptions().position(latLng).title("Home"));
                } else home.setPosition(latLng);
            }
        });


//        new Thread(new Runnable() {
//            public void run(){
//
//                runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        try {
//                            Thread.sleep(5000);
//
//                            LatLng cln = getCurrentLocation();
//                            Log.d(TAG, "auto set current" + cln.latitude + ":" + cln.longitude);
//                            current.setPosition(cln);
//                        } catch (InterruptedException e) {
//                            e.printStackTrace();
//                        }
//
//                    }
//                });
//
//            }
//        }).start();


    }
    private LatLng getCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {


            Log.e(TAG, "error no permission");
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_BACKGROUND_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
            return null;
        }
        LatLng currentLoc = null;


        Location location = myLocationManager.getLastKnownLocation(provider);

        if (location != null) {
            // get Current Location
            currentLoc = new LatLng(location.getLatitude(), location.getLongitude());
        }
        Log.d(TAG, "current location"+currentLoc.latitude + ":" + currentLoc.longitude);

        return currentLoc;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }

    private void updateCurrent() {
        LatLng cl = getCurrentLocation();

        //LatLng temp = new LatLng(home.getPosition().latitude, home.getPosition().longitude);
        Log.d(TAG, "update current" + cl.latitude + ":" + cl.longitude);

        current.setPosition(cl);
    }
}