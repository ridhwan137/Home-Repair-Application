package mobile.test.homerepair.admin;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import java.util.List;
import java.util.Locale;

import mobile.test.homerepair.R;

public class AppointmentClientDetail extends AppCompatActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    FirebaseFirestore db = FirebaseFirestore.getInstance();

    String clientID,clientPictureURL;
    String TAG = "TAG";

    EditText et_detailClientName, et_detailClientEmail,
            et_detailClientPhone, et_detailClientAddress;

    ImageView img_pictureClient;

    Button btn_BackToHome,btn_editUserInformation;

    GoogleMap mGoogleMap;
    String getFullAddressForMap;

    String appointmentLayout,appointmentID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appointment_client_detail);

        initializeMap();

        Intent intent = getIntent();
        clientID = intent.getStringExtra("clientID");
        appointmentLayout = intent.getStringExtra("appointmentLayout");
        appointmentID = intent.getStringExtra("appointmentID");

        Log.e("clientID->", clientID);
        Log.e("appointmentLayout->",appointmentLayout);
        Log.e("appointmentID->",appointmentID);



        btn_BackToHome = findViewById(R.id.btn_BackToHome);

        img_pictureClient =  findViewById(R.id.img_pictureClient);
        et_detailClientName =  findViewById(R.id.et_detailClientName);
        et_detailClientEmail =  findViewById(R.id.et_detailClientEmail);
        et_detailClientPhone =  findViewById(R.id.et_detailClientPhone);
        et_detailClientAddress =  findViewById(R.id.et_detailClientAddress);

        btn_editUserInformation =  findViewById(R.id.btn_editUserInformation);


        displayProviderInfoFromDB();


        btn_BackToHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                onBackPressed();

                if (appointmentLayout.equals("pendingAppointment")){

                    Intent intent = new Intent(getApplicationContext(), PendingAppointmentDetail.class);
                    intent.putExtra("appointmentID",appointmentID);
                    startActivity(intent);

                }else if (appointmentLayout.equals("inProgressAppointment")){

                    Intent intent = new Intent(getApplicationContext(), InProgressAppointmentDetail.class);
                    intent.putExtra("appointmentID",appointmentID);
                    startActivity(intent);

                }else if (appointmentLayout.equals("completeAppointment")){

                    Intent intent = new Intent(getApplicationContext(), CompleteAppointmentDetail.class);
                    intent.putExtra("appointmentID",appointmentID);
                    startActivity(intent);

                }else if (appointmentLayout.equals("rejectAppointment")){

                    Intent intent = new Intent(getApplicationContext(), RejectAppointmentDetail.class);
                    intent.putExtra("appointmentID",appointmentID);
                    startActivity(intent);

                }else if (appointmentLayout.equals("cancelAppointment")){

                    Intent intent = new Intent(getApplicationContext(), CancelAppointmentDetail.class);
                    intent.putExtra("appointmentID",appointmentID);
                    startActivity(intent);

                }else{
//                    onBackPressed();
                }


            }
        });



     ////////////
    }


    ///// Map Function
    private void geoLocate(String getFullAddressForMap) {
        String locationName = getFullAddressForMap;

        Log.e("geoLocate->",getFullAddressForMap);



//        try {
//            Geocoder geocoder = new Geocoder(this, Locale.getDefault());
//            List<Address> addressList = geocoder.getFromLocationName(locationName,5);
//
//            if(addressList.size()>0){
//                Address address = addressList.get(0);
//                Log.e("geoLocate->addressList->", String.valueOf(address));
//
//                mGoogleMap.addMarker(new MarkerOptions().position(new LatLng(address.getLatitude(),address.getLongitude())));
//                gotoLocation(address.getLatitude(),address.getLongitude());
//
//
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//            Intent intent = new Intent(getApplicationContext(), ApproveRegisteredUserServiceProviderDetailAdmin.class);
//            intent.putExtra("clientID",clientID);
//
//            startActivity(intent);
//        }


        // Run on Thread
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                //
                try {
                    Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
                    List<Address> addressList = geocoder.getFromLocationName(locationName, 5);

                    if (addressList.size() > 0) {
                        Address address = addressList.get(0);

                        mGoogleMap.addMarker(new MarkerOptions().position(new LatLng(address.getLatitude(), address.getLongitude())));
                        gotoLocation(address.getLatitude(), address.getLongitude());
                    }
                } catch (Exception e) {
                    e.printStackTrace();

                    Intent intent = new Intent(getApplicationContext(), AppointmentClientDetail.class);
                    intent.putExtra("clientID",clientID);
                    intent.putExtra("appointmentLayout",appointmentLayout);
                    intent.putExtra("appointmentID",appointmentID);
                    startActivity(intent);
                }
            }
        });



    }

    private void gotoLocation(double latitude, double longitude) {

        LatLng LatLng = new LatLng(latitude, longitude);

        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(LatLng, 18);
        mGoogleMap.moveCamera(cameraUpdate);
        mGoogleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
    }

    private void initializeMap() {
        SupportMapFragment supportMapFragment =  (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapFragment);
        supportMapFragment.getMapAsync(this);
    }
    ///// Map Function

    ///// Map Function/Method
    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mGoogleMap = googleMap;
    }
    ///// Map Function/Method


    public void displayProviderInfoFromDB(){
        db.collection("users").whereEqualTo("userID", clientID)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + " => " + document.getData());

                                try {
                                    clientPictureURL = document.getData().get("pictureURL").toString();
                                    Picasso.with(getApplicationContext()).load(clientPictureURL).into(img_pictureClient);

                                    et_detailClientName.setText(document.getData().get("name").toString());
                                    et_detailClientPhone.setText(document.getData().get("email").toString());
                                    et_detailClientEmail.setText(document.getData().get("phone").toString());

                                    // Get Full Address
                                    String fullAddress;

                                    fullAddress = document.getData().get("address1").toString() + ", ";
                                    fullAddress += document.getData().get("address2").toString() + ",\n";
                                    fullAddress += document.getData().get("postcode").toString() + " ";
                                    fullAddress += document.getData().get("city").toString() + "\n";
                                    fullAddress += document.getData().get("state").toString() ;

                                    et_detailClientAddress.setText(fullAddress);

                                    getFullAddressForMap = fullAddress;
                                    geoLocate(getFullAddressForMap);

                                }catch (Exception e){
                                    e.printStackTrace();
                                }



                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }

                    }
                });
    }

    //////////
}