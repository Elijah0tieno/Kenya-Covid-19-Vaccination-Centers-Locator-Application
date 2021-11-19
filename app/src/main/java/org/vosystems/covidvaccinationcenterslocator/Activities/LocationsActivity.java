package org.vosystems.covidvaccinationcenterslocator.Activities;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import org.json.JSONArray;
import org.vosystems.covidvaccinationcenterslocator.Models.Facility;
import org.vosystems.covidvaccinationcenterslocator.Network.Requests.GetLocationsRequest;
import org.vosystems.covidvaccinationcenterslocator.Network.VolleySingleton;
import org.vosystems.covidvaccinationcenterslocator.R;
import org.vosystems.covidvaccinationcenterslocator.databinding.ActivityLocationsBinding;

import java.util.ArrayList;

public class LocationsActivity extends FragmentActivity implements OnMapReadyCallback {
    private String keyword = null;
    private ArrayList<Marker> markers;
    private GoogleMap mMap;
    private ActivityLocationsBinding binding;

    private SearchView searchView;
    private ArrayList<Facility> facilities;

    private double latitude = 0.0;
    private double longitude = 0.0;
    private Circle cirle;

    private FusedLocationProviderClient fusedLocationClient;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_locations);

        markers = new ArrayList<>();

        searchView = findViewById(R.id.searchView);


        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                keyword = query;
                getFacilities(true);
                return false;

            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        getLocationPermission();
        fetchGPS();
    }

    static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 2000;
    boolean mLocationPermissionDenied = false;
    private void getLocationPermission() {
        if (ContextCompat.checkSelfPermission(LocationsActivity.this,
                Manifest.permission.ACCESS_FINE_LOCATION)
        == PackageManager.PERMISSION_GRANTED){
            mLocationPermissionDenied = true;
        }else {
            ActivityCompat.requestPermissions(LocationsActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        mLocationPermissionDenied = false;
        switch (requestCode){
            case PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                //if request is cancelled the result arrays are empty
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    mLocationPermissionDenied = true;
                    //getdevicelocation
                    fetchGPS();
                }
            }
        }
    }

    private void fetchGPS() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            return;
        }
        fusedLocationClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                Toast.makeText(LocationsActivity.this, "Fetched", Toast.LENGTH_SHORT).show();

                //get last known location, this can be null in some rare situations
                if (location != null){
                    //Logic to handle location object
                    double lat = location.getLatitude();
                    double lon = location.getLongitude();
                    Toast.makeText(LocationsActivity.this, lat+" "+lon, Toast.LENGTH_SHORT).show();
                    latitude = lat;
                    longitude = lon;

                    LatLng currentLocation = new LatLng(latitude,longitude);
                    mMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)).position(currentLocation).title("My Current Location"));
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(currentLocation));

                    CameraPosition cameraPosition = new CameraPosition.Builder()
                            .target(currentLocation)      // Sets the center of the map to location user
                            .zoom(10)                   // Sets the zoom
                            .bearing(90)                // Sets the orientation of the camera to east
                            .tilt(40)                   // Sets the tilt of the camera to 30 degrees
                            .build();                   // Creates a CameraPosition from the builder
                    mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

                    cirle = mMap.addCircle(new CircleOptions()
                    .center(currentLocation).radius(10000)
                            .strokeWidth(1)
                            .strokeColor(Color.BLUE)
                            .fillColor(Color.argb(50,40,60,250))
                            .clickable(true)
                    );

                }else {
                    Toast.makeText(LocationsActivity.this, "Null", Toast.LENGTH_SHORT).show();
                    fetchGPS();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(LocationsActivity.this, "Failed", Toast.LENGTH_SHORT).show();
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
        getFacilities(false);
        fetchGPS();




        // Add a marker in Sydney and move the camera
//        LatLng sydney = new LatLng(-34, 151);
//        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
//        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
    }


    private void getFacilities(boolean search) {
        VolleySingleton.getInstance(getApplicationContext()).addToRequestQueue(new GetLocationsRequest(this, search? keyword: null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray facilities_response) {
                System.out.println(facilities_response);
                for ( int i = 0; i < markers.size(); i++) {
                    Marker marker = markers.get(i);
                    marker.remove();
                    markers.remove(i);

//                    Marker locationMarker = mMap.addMarker(markerOptions);

                }
                Log.i("Message: ", "Faciilities Received");

                for (int j = 0; j < facilities_response.length(); j++) {
                    mapFacility(Facility.fromJSON(facilities_response.optJSONObject(j)));
                }

            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(LocationsActivity.this, "Error occurred", Toast.LENGTH_SHORT).show();
                System.out.println(error);
            }
        }));

    }

    private void mapFacility(Facility facility) {

        LatLng location = new LatLng(facility.latitudes, facility.longitudes);
        MarkerOptions marker = new MarkerOptions().position(location).title(facility.name);

        markers.add(mMap.addMarker(marker));

//        System.out.println(facility.name + " " + facility.latitudes + " " + facility.longitudes);

    }


}