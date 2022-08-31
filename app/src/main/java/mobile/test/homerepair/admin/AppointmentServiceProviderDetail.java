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

public class AppointmentServiceProviderDetail extends AppCompatActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener{

    FirebaseFirestore db = FirebaseFirestore.getInstance();

    String providerID,providerPictureURL;
    String TAG = "TAG";

    EditText et_detailCompanyName, et_detailCompanyServiceType, et_detailCompanyEmail,
            et_detailCompanyPhone, et_detailCompanyAddress, et_detailCompanyNo;

    ImageView img_pictureCompany;

    Button btn_BackToHome,btn_editUserInformation;

    GoogleMap mGoogleMap;
    String getFullAddressForMap;

    String appointmentLayout,appointmentID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appointment_service_provider_detail);

        initializeMap();


        try {
            Intent intent = getIntent();
            providerID = intent.getStringExtra("providerID");
            appointmentLayout = intent.getStringExtra("appointmentLayout");
            appointmentID = intent.getStringExtra("appointmentID");

            Log.e("providerID->",providerID);
            Log.e("appointmentLayout->",appointmentLayout);
            Log.e("appointmentID->",appointmentID);

        }catch (NullPointerException e){
            e.printStackTrace();
        }



        btn_BackToHome = findViewById(R.id.btn_BackToHome);

        img_pictureCompany =  findViewById(R.id.img_pictureCompany);
        et_detailCompanyName =  findViewById(R.id.et_detailCompanyName);
        et_detailCompanyServiceType =  findViewById(R.id.et_detailCompanyServiceType);
        et_detailCompanyEmail =  findViewById(R.id.et_detailCompanyEmail);
        et_detailCompanyPhone =  findViewById(R.id.et_detailCompanyPhone);
        et_detailCompanyAddress =  findViewById(R.id.et_detailCompanyAddress);

        et_detailCompanyNo =  findViewById(R.id.et_detailCompanyNo);
        btn_editUserInformation =  findViewById(R.id.btn_editUserInformation);


        displayProviderInfoFromDB();


        btn_BackToHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

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



        /////////
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();

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
//            Intent intent = new Intent(getApplicationContext(), AppointmentServiceProviderDetail.class);
//            intent.putExtra("providerID",providerID);
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

                    Intent intent = new Intent(getApplicationContext(), AppointmentServiceProviderDetail.class);
                    intent.putExtra("providerID",providerID);
                    intent.putExtra("appointmentLayout",appointmentLayout);
                    intent.putExtra("appointmentID",appointmentID);
                    startActivity(intent);

//                    finish();
//                    overridePendingTransition(0, 0);
//                    startActivity(intent);
//                    overridePendingTransition(0, 0);

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
        db.collection("users").whereEqualTo("userID",providerID)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + " => " + document.getData());

                                try {
                                    providerPictureURL = document.getData().get("pictureURL").toString();
                                    Picasso.with(getApplicationContext()).load(providerPictureURL).into(img_pictureCompany);

                                    et_detailCompanyName.setText(document.getData().get("companyName").toString());
                                    et_detailCompanyNo.setText(document.getData().get("companyNo").toString());
                                    et_detailCompanyServiceType.setText(document.getData().get("serviceType").toString());
                                    et_detailCompanyEmail.setText(document.getData().get("email").toString());
                                    et_detailCompanyPhone.setText(document.getData().get("phone").toString());


                                    // Get Full Address
                                    String fullAddress;

                                    fullAddress = document.getData().get("address1").toString() + ", ";
                                    fullAddress += document.getData().get("address2").toString() + ",\n";
                                    fullAddress += document.getData().get("postcode").toString() + " ";
                                    fullAddress += document.getData().get("city").toString() + "\n";
                                    fullAddress += document.getData().get("state").toString() ;

                                    et_detailCompanyAddress.setText(fullAddress);

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


    ////////
}