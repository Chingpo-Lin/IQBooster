package com.example.iqbooster.login;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.example.iqbooster.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback {

    private TextInputEditText mInputLocation;
    private GoogleMap mMap;
    private SupportMapFragment mapFragment;
    private Button mConfirmButton;
    private String mConfirmAddress;
    private FusedLocationProviderClient client;
    private ImageView mGetMyLocation;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        mInputLocation = findViewById(R.id.inputLocation);
        mConfirmAddress = "";
        mConfirmButton = findViewById(R.id.location_button);
        client = LocationServices.getFusedLocationProviderClient(this);
        mapFragment = SupportMapFragment.newInstance();
        getSupportFragmentManager().beginTransaction().add(R.id.container, mapFragment).commit();
        mapFragment.getMapAsync(this);
        mGetMyLocation = findViewById(R.id.get_my_location);

        mGetMyLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Dexter.withContext(getApplicationContext())
                        .withPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                        .withListener(new PermissionListener() {
                            @Override
                            public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {
                                getLocation();
                            }

                            @Override
                            public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {
                                Snackbar sn = Snackbar.make(findViewById(android.R.id.content),  "Permission Denied", Snackbar.LENGTH_LONG);
                                View view = sn.getView();
                                TextView tv = (TextView) view.findViewById(com.google.android.material.R.id.snackbar_text);
                                tv.setTextColor(Color.parseColor("#FFD700"));
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                                    tv.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                                } else {
                                    tv.setGravity(Gravity.CENTER_HORIZONTAL);
                                }
                                sn.show();
                            }

                            @Override
                            public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {
                                permissionToken.continuePermissionRequest();
                            }
                        }).check();
            }
        });

        mConfirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent goBackToSetUp = new Intent();
                goBackToSetUp.putExtra("location", mConfirmAddress);
                setResult(88, goBackToSetUp);
                MapsActivity.super.onBackPressed();
            }
        });

        mInputLocation.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if ((actionId == EditorInfo.IME_NULL && event.getAction() == KeyEvent.ACTION_DOWN)|| (actionId == EditorInfo.IME_ACTION_DONE)){
                    View view = getCurrentFocus();
                    if (view != null) {
                        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                    }

                    String location = mInputLocation.getText().toString();
                    if (location.isEmpty()) {
                        Snackbar sn = Snackbar.make(findViewById(android.R.id.content),  "Please Input a Location", Snackbar.LENGTH_LONG);
                        View viewSn = sn.getView();
                        TextView tv = (TextView) viewSn.findViewById(com.google.android.material.R.id.snackbar_text);
                        tv.setTextColor(Color.parseColor("#FFD700"));
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                            tv.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                        } else {
                            tv.setGravity(Gravity.CENTER_HORIZONTAL);
                        }
                        sn.show();
                    } else {
                        mConfirmAddress = location;
                        mConfirmButton.setEnabled(true);
                        Geocoder geocoder = new Geocoder(MapsActivity.this, Locale.getDefault());
                        try {
                            List<Address> listAddress = geocoder.getFromLocationName(location, 1);
                            if (listAddress.size() > 0) {
                                LatLng latLng = new LatLng(listAddress.get(0).getLatitude(), listAddress.get(0).getLongitude());
                                mMap.clear();
                                List<Address> addressName = geocoder.getFromLocation(listAddress.get(0).getLatitude(),
                                        listAddress.get(0).getLongitude(), 1);
                                Address obj = addressName.get(0);
                                mConfirmAddress = obj.getLocality() + ", " + obj.getAdminArea();

                                mMap.addMarker(new MarkerOptions()
                                        .position(latLng)
                                        .title("My search location"));
                                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16.0f));
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
                return true;
            }
        });
    }

    private void getLocation() {
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
        Task<Location> task = client.getLastLocation();
        task.addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                mapFragment.getMapAsync(new OnMapReadyCallback() {
                    @Override
                    public void onMapReady(@NonNull GoogleMap googleMap) {
                        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                        MarkerOptions markerOptions = new MarkerOptions().position(latLng).title("You are here");
                        Geocoder geocoder = new Geocoder(MapsActivity.this, Locale.getDefault());
                        List<Address> addressName = null;
                        try {
                            addressName = geocoder.getFromLocation(location.getLatitude(),
                                    location.getLongitude(), 1);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        Address obj = addressName.get(0);
                        mInputLocation.setText(obj.getLocality() + ", " + obj.getAdminArea());
                        mConfirmAddress = obj.getLocality() + ", " + obj.getAdminArea();
                        mConfirmButton.setEnabled(true);
                        mMap.clear();
                        mMap.addMarker(markerOptions);
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16.0f));
                    }
                });
            }
        });
    }
    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     *
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        // Add a marker in Sydney and move the camera
    }
}