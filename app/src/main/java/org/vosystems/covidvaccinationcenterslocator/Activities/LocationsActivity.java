package org.vosystems.covidvaccinationcenterslocator.Activities;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.FragmentActivity;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.vosystems.covidvaccinationcenterslocator.Models.Facility;
import org.vosystems.covidvaccinationcenterslocator.Network.Requests.GetLocationsRequest;
import org.vosystems.covidvaccinationcenterslocator.Network.VolleySingleton;
import org.vosystems.covidvaccinationcenterslocator.R;
import org.vosystems.covidvaccinationcenterslocator.databinding.ActivityLocationsBinding;

import java.util.ArrayList;

public class LocationsActivity extends FragmentActivity implements OnMapReadyCallback {
    private String keyword = null;
    private ArrayList<MarkerOptions> markers;
    private GoogleMap mMap;
    private ActivityLocationsBinding binding;

    private SearchView searchView;
    private ArrayList<Facility> facilities;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityLocationsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        markers = new ArrayList<>();

        searchView = findViewById(R.id.searchView);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                keyword = query;
                getFacilities();
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
        getFacilities();

        // Add a marker in Sydney and move the camera
//        LatLng sydney = new LatLng(-34, 151);
//        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
//        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
    }

    private void getFacilities(){
        VolleySingleton.getInstance(getApplicationContext()).addToRequestQueue(new GetLocationsRequest(this, keyword,new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray facilities_response) {
                for (MarkerOptions markerOptions: markers){
                    markerOptions.visible(false);
                }
                Log.i("Message: ","Faciilities Received");

                for (int j = 0; j < facilities_response.length(); j++){
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

    private void mapFacility(Facility facility){

        LatLng location = new LatLng(facility.latitudes, facility.longitudes);
        MarkerOptions marker = new MarkerOptions().position(location).title(facility.name);
        mMap.addMarker(marker);
        markers.add(marker);
//        System.out.println(facility.name + " " + facility.latitudes + " " + facility.longitudes);

    }
}