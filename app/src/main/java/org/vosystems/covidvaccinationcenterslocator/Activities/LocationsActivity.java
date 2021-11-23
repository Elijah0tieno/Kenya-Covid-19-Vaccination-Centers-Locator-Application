package org.vosystems.covidvaccinationcenterslocator.Activities;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
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
import com.google.android.gms.location.LocationListener;
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
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import org.json.JSONArray;
import org.json.JSONObject;
import org.vosystems.covidvaccinationcenterslocator.Helpers.DirectionsJSONParser;
import org.vosystems.covidvaccinationcenterslocator.Models.Facility;
import org.vosystems.covidvaccinationcenterslocator.Network.Requests.GetLocationsRequest;
import org.vosystems.covidvaccinationcenterslocator.Network.VolleySingleton;
import org.vosystems.covidvaccinationcenterslocator.R;
import org.vosystems.covidvaccinationcenterslocator.databinding.ActivityLocationsBinding;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class LocationsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener, LocationListener {
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

    private LatLng currentLocation;
    private ProgressDialog progressDialog;

    private Polyline routePolyline = null;
    private LatLng lastPositionClicked = null;
    private Marker currentLocationMarker = null;
    private Circle currentLocationCircle = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_locations);

        progressDialog = new ProgressDialog(LocationsActivity.this);

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

                    currentLocation = new LatLng(latitude,longitude);

                    showCurrentUserPosition();

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

        mMap.setOnMarkerClickListener(this);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                double lat = currentLocation.latitude;
                double lng = currentLocation.longitude;

//                loc.setLatitude(lat);
//                loc.setLongitude(lng);
//
//                onLocationChanged(new Location());
            }
        }, 2000);
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
    }

    @Override
    public boolean onMarkerClick(@NonNull Marker marker) {
        // Update last position clicked
        lastPositionClicked = marker.getPosition();

        // draw route to clicked marker from current location
        getRoute(marker);
        return false;
    }

    public void getRoute(Marker marker){
//        Toast.makeText(this, marker.getPosition().latitude + "", Toast.LENGTH_SHORT).show();
        drawPolylines(marker.getPosition(), true);
    }

    // Will be called when the user's location changes
    private void updateGPS(Location newLocation){
        Toast.makeText(LocationsActivity.this, "Location changed", Toast.LENGTH_SHORT).show();

        // Update current location to the new location
        currentLocation = new LatLng(newLocation.getLatitude(), newLocation.getLongitude());

        // Update current location marker
        showCurrentUserPosition();

        // Update route
        if(lastPositionClicked != null){
            drawPolylines(lastPositionClicked, false);
        }
    }

    private void showCurrentUserPosition(){
        // Remove existing current location circle and marker
        if(currentLocationCircle != null){
            currentLocationCircle.remove();
        }

        if(currentLocationMarker != null){
            currentLocationMarker.remove();
        }

        // New marker
        currentLocationMarker = mMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)).position(currentLocation).title("Your Location"));

        // point camera to new marker
        mMap.moveCamera(CameraUpdateFactory.newLatLng(currentLocation));

        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(currentLocation)      // Sets the center of the map to location user
                .zoom(10)                   // Sets the zoom
                .tilt(40)                   // Sets the tilt of the camera to 30 degrees
                .build();                   // Creates a CameraPosition from the builder

        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

        // New circle
        currentLocationCircle = mMap.addCircle(new CircleOptions()
                .center(currentLocation).radius(10000)
                .strokeWidth(0)
                .strokeColor(Color.BLUE)
                .fillColor(Color.argb(50,40,60,250))
                .clickable(true)
        );

    }

    private void drawPolylines(LatLng dest, boolean showProgress) {
        if(dest.equals(currentLocation)){
            return;
        }

        if(showProgress) {
            progressDialog.setMessage("Please Wait, Polyline between two locations is building.");
            progressDialog.setCancelable(true);
            progressDialog.show();
        }

        // Checks, whether start and end locations are captured
        // Getting URL to the Google Directions API
        String url = getDirectionsUrl(currentLocation, dest);
        Log.d("url", url + "");
        DownloadTask downloadTask = new DownloadTask();
        // Start downloading json data from Google Directions API
        downloadTask.execute(url);
    }

    @Override
    public void onLocationChanged(Location location) {
        updateGPS(location);
    }

    private class DownloadTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... url) {

            String data = "";

            try {
                data = downloadUrl(url[0]);
            } catch (Exception e) {
                Log.d("Background Task", e.toString());
            }
            return data;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            ParserTask parserTask = new ParserTask();
            parserTask.execute(result);
        }
    }


    /**
     * A class to parse the Google Places in JSON format
     */
    private class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String, String>>>> {

        // Parsing the data in non-ui thread
        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {

            JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;

            try {
                jObject = new JSONObject(jsonData[0]);
                DirectionsJSONParser parser = new DirectionsJSONParser();

                routes = parser.parse(jObject);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return routes;
        }

        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> result) {
            progressDialog.dismiss();
            Log.d("result", result.toString());

            if(routePolyline != null){
                // Remove the last polyline
                routePolyline.remove();
            }

            for (int i = 0; i < result.size(); i++) {
                PolylineOptions lineOptions = new PolylineOptions();
                lineOptions.width(20);
                lineOptions.color(Color.parseColor("#85007bff"));
                lineOptions.geodesic(true);

                List<HashMap<String, String>> path = result.get(i);

                for (int j = 0; j < path.size(); j++) {
                    HashMap<String, String> point = path.get(j);

                    double lat = Double.parseDouble(point.get("lat"));
                    double lng = Double.parseDouble(point.get("lng"));
                    LatLng position = new LatLng(lat, lng);

                    lineOptions.add(position);
                }

                // Drawing polyline in the Google Map for the i-th route
                // Assign the generated polyline to route polyline
                routePolyline = mMap.addPolyline(lineOptions);

                // Increase zoom after drawing route
                CameraPosition cameraPosition = new CameraPosition.Builder()
                        .target(currentLocation)      // Sets the center of the map to location user
                        .zoom(13)                   // Sets the zoom
                        .tilt(40)                   // Sets the tilt of the camera to 30 degrees
                        .build();
                // Creates a CameraPosition from the builder
                mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
            }


        }
    }

    private String getDirectionsUrl(LatLng origin, LatLng dest) {

        // Origin of route
        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;

        // Destination of route
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;

        // Sensor enabled
        String sensor = "sensor=false";
        String mode = "mode=driving";
        String key = "key="+getResources().getString(R.string.google_maps_key);

        // Building the parameters to the web service
        String parameters = str_origin + "&" + str_dest + "&" + sensor + "&" + mode+ "&" + key;

        // Output format
        String output = "json";

        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters;

        return url;
    }

    /**
     * A method to download json data from url
     */
    private String downloadUrl(String strUrl) throws IOException {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(strUrl);

            urlConnection = (HttpURLConnection) url.openConnection();

            urlConnection.connect();

            iStream = urlConnection.getInputStream();

            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

            StringBuffer sb = new StringBuffer();

            String line = "";
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }

            data = sb.toString();

            br.close();
            Log.d("data", data);

        } catch (Exception e) {
            Log.d("Exception", e.toString());
        } finally {
            iStream.close();
            urlConnection.disconnect();
        }
        return data;
    }

}