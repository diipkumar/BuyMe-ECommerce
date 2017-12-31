package com.online.shopping.buyme.placecomplete;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.online.shopping.buyme.Config.ConfigData;
import com.online.shopping.buyme.Customer.CustomerBaseAcctivity;
import com.online.shopping.buyme.R;
import com.online.shopping.buyme.Utility.UtilityFunction;
import com.online.shopping.buyme.common.activities.SampleActivityBase;


public class GoogleMapServices extends SampleActivityBase
        implements GoogleApiClient.OnConnectionFailedListener {
    UtilityFunction utility;
    protected GoogleApiClient mGoogleApiClient;
    private PlaceAutocompleteAdapter mAdapter;
    private static final LatLngBounds BOUNDS_GREATER_SYDNEY = new LatLngBounds(
            new LatLng(-34.041458, 150.790100), new LatLng(-33.682247, 151.383362));

    private GoogleMap googleMap;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.googlemap);
        DisplayMap();
        Placesetup();
        Addmarker(new LatLng(getIntent().getExtras().getDouble("latitude"),getIntent().getExtras().getDouble("longitude")));
    }

    private void Placesetup() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, 0 /* clientId */, this)
                .addApi(Places.GEO_DATA_API)
                .build();
        AutoCompleteTextView mAutocompleteView = (AutoCompleteTextView)
                findViewById(R.id.autocomplete_places);
        mAutocompleteView.setOnItemClickListener(mAutocompleteClickListener);
        mAdapter = new PlaceAutocompleteAdapter(this, android.R.layout.simple_list_item_1,
                mGoogleApiClient, BOUNDS_GREATER_SYDNEY, null);
        mAutocompleteView.setAdapter(mAdapter);
    }

    private AdapterView.OnItemClickListener mAutocompleteClickListener
            = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            final PlaceAutocompleteAdapter.PlaceAutocomplete item = mAdapter.getItem(position);
            final String placeId = String.valueOf(item.placeId);
            Log.i(TAG, "Autocomplete item selected: " + item.description);
            PendingResult<PlaceBuffer> placeResult = Places.GeoDataApi
                    .getPlaceById(mGoogleApiClient, placeId);
            placeResult.setResultCallback(new ResultCallback<PlaceBuffer>() {
                        @Override
                        public void onResult(PlaceBuffer places) {
                            if (places.getStatus().isSuccess()) {
                                final Place myPlace = places.get(0);
                                Addmarker(myPlace.getLatLng());
                            }
                            places.release();
                        }
                    });

            Log.i(TAG, "Called getPlaceById to get Place details for " + item.placeId);
        }
    };

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

        Log.e(TAG, "onConnectionFailed: ConnectionResult.getErrorCode() = "
                + connectionResult.getErrorCode());

        // TODO(Developer): Check error code and notify the user of error state and resolution.
        Toast.makeText(this,
                "Could not connect to Google ",
                Toast.LENGTH_SHORT).show();
    }

    private void DisplayMap() {
            initilizeMap();
            utility = new UtilityFunction(this);
            ConfigData.signuplatlng = null;
            if(googleMap == null)
            return;
            googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
            googleMap.getUiSettings().setCompassEnabled(false);
            googleMap.setMyLocationEnabled(true);
            googleMap.getUiSettings().setMyLocationButtonEnabled(false);
            googleMap.getUiSettings().setRotateGesturesEnabled(true);
            googleMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
                @Override
                public void onMapLongClick(LatLng latLng) {
                    Addmarker(latLng);
                }
            });
             (findViewById(R.id.mapsave)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(ConfigData.signuplatlng == null)
                        utility.SanckMessage(getResources().getString(R.string.locationerror));
                    else {
                        Intent intent=new Intent();
                        setResult(CustomerBaseAcctivity.MAP_CODE,intent);
                        finish();
                    }
                }
            });
    }

    private void Addmarker(LatLng latLng ) {
        ConfigData.signuplatlng = latLng;
        googleMap.clear();
        MarkerOptions marker = new MarkerOptions().position(latLng);
        marker.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));
        googleMap.addMarker(marker);
        CameraUpdate yourLocation = CameraUpdateFactory.newLatLngZoom(latLng, 15);
        googleMap.animateCamera(yourLocation);
        }

    @Override
    protected void onResume() {
        super.onResume();
        initilizeMap();
    }

    private void initilizeMap() {
        if (googleMap == null) {
            googleMap = ((MapFragment) getFragmentManager().findFragmentById(
                    R.id.map)).getMap();
            // check if map is created successfully or not
            if (googleMap == null) {
                Toast.makeText(getApplicationContext(),
                        "Sorry! unable to create maps", Toast.LENGTH_SHORT)
                        .show();
            }
        }
    }

}