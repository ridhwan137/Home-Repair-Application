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
import android.widget.Toast;

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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import mobile.test.homerepair.MailAPI.JavaMailAPI;
import mobile.test.homerepair.R;

public class ApproveRegisteredUserServiceProviderDetailAdmin extends AppCompatActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener{

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseUser user;

    String userID,pictureURL;
    String TAG = "TAG";

//    TextView tv_detailCompanyName,tv_detailCompanyServiceType,tv_detailCompanyEmail,
//            tv_detailCompanyPhone,tv_detailCompanyAddress,tv_detailCompanyNo,tv_detailCompanyDateApply;

    EditText et_detailCompanyName,et_detailCompanyServiceType,et_detailCompanyEmail,et_detailCompanyPhone,
            et_detailCompanyAddress,et_detailCompanyNo,et_detailCompanyDateApply;

    Button btn_BackToHome,btn_acceptRegistration,btn_rejectRegistration;

    GoogleMap mGoogleMap;
    String getFullAddressForMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registered_user_detail_admin);

        Intent intent = getIntent();
        userID = intent.getStringExtra("userID");
        Log.e("testUserID",userID);


        btn_BackToHome = findViewById(R.id.btn_BackToHome);

        et_detailCompanyName =  findViewById(R.id.et_detailCompanyName);
        et_detailCompanyServiceType =  findViewById(R.id.et_detailCompanyServiceType);
        et_detailCompanyEmail =  findViewById(R.id.et_detailCompanyEmail);
        et_detailCompanyPhone =  findViewById(R.id.et_detailCompanyPhone);
        et_detailCompanyAddress =  findViewById(R.id.et_detailCompanyAddress);

        et_detailCompanyNo =  findViewById(R.id.et_detailCompanyNo);
        et_detailCompanyDateApply =  findViewById(R.id.et_detailCompanyDateApply);

        btn_acceptRegistration =  findViewById(R.id.btn_acceptRegistration);
        btn_rejectRegistration =  findViewById(R.id.btn_rejectRegistration);


        displayProviderInfoFromDB();

        initializeMap();


        btn_BackToHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ApproveRegistrationAdmin.class);
                startActivity(intent);
            }
        });

        btn_rejectRegistration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rejectRegistration();


                Intent intent = new Intent(getApplicationContext(), HomeAdmin.class);
                intent.putExtra("userID",userID);
                startActivity(intent);
            }
        });

        btn_acceptRegistration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                acceptRegistration();


                Intent intent = new Intent(getApplicationContext(), HomeAdmin.class);
                intent.putExtra("userID",userID);
                startActivity(intent);

            }
        });

        // End Bracket
    }

    // <-- Notification Through Email Status Reject
    private void getProviderEmailFromDB_notifyProviderThroughEmail_statusReject(){

        db.collection("users")
                .whereEqualTo("userID",userID)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {

                        String providerEmailFromDB = null;

                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.e("getDataFormDb->", document.getId() + " => " + document.getData());

                                providerEmailFromDB = document.getData().get("email").toString();

                            }

                            sendEmailNotificationToProvider_statusReject(providerEmailFromDB);


                        } else {
                            Log.e("getDataFormDb2->", "Error getting documents: ", task.getException());
                        }
                    }
                });
    }



    private void sendEmailNotificationToProvider_statusReject(String receiverEmail) {

        String emailReceiver = receiverEmail;
        String subjectNotify = "Home Repair Apps: Service Provider Approval Status";
        String messageNotify = "Your registration status has been rejected.\n" +
                "Kindly contact our admin if there is any question.\n" + "home.repair.management@gmail.com";


        String mail = emailReceiver.trim();
        String subject = subjectNotify.trim();
        String message = messageNotify;

        //Send Mail
        JavaMailAPI javaMailAPI = new JavaMailAPI(this,mail,subject,message);
        javaMailAPI.execute();
    }

    // --> Notification Through Email




    // <-- Notification Through Email Status Accept
    private void getProviderEmailFromDB_notifyProviderThroughEmail_statusAccept(){

        db.collection("users")
                .whereEqualTo("userID",userID)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {

                        String providerEmailFromDB = null;

                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.e("getDataFormDb->", document.getId() + " => " + document.getData());

                                providerEmailFromDB = document.getData().get("email").toString();

                            }

                            sendEmailNotificationToProvider_statusAccept(providerEmailFromDB);


                        } else {
                            Log.e("getDataFormDb2->", "Error getting documents: ", task.getException());
                        }
                    }
                });
    }



    private void sendEmailNotificationToProvider_statusAccept(String receiverEmail) {

        String emailReceiver = receiverEmail;
        String subjectNotify = "Home Repair Apps: Service Provider Approval Status";
        String messageNotify = "Congratulation your registration has been approve by our administration.\n"+
                "You may login to use the application";


        String mail = emailReceiver.trim();
        String subject = subjectNotify.trim();
        String message = messageNotify;

        //Send Mail
        JavaMailAPI javaMailAPI = new JavaMailAPI(this,mail,subject,message);
        javaMailAPI.execute();
    }

    // --> Notification Through Email






    ///// Map Function
    private void geoLocate(String getFullAddressForMap) {
        String locationName = getFullAddressForMap;

        Log.e("geoLocate->",getFullAddressForMap);

        Geocoder geocoder = new Geocoder(this, Locale.getDefault());

        try {
            List<Address> addressList = geocoder.getFromLocationName(locationName,5);

            if(addressList.size()>0){
                Address address = addressList.get(0);
                Log.e("geoLocate->addressList->", String.valueOf(address));

                mGoogleMap.addMarker(new MarkerOptions().position(new LatLng(address.getLatitude(),address.getLongitude())));
                gotoLocation(address.getLatitude(),address.getLongitude());


            }
        } catch (Exception e) {
            e.printStackTrace();
            Intent intent = new Intent(getApplicationContext(), ApproveRegisteredUserServiceProviderDetailAdmin.class);
            intent.putExtra("userID",userID);

            startActivity(intent);
        }
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
        db.collection("users")
                .whereEqualTo("userID",userID)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + " => " + document.getData());


                                et_detailCompanyName.setText(document.getData().get("companyName").toString());
                                et_detailCompanyNo.setText(document.getData().get("companyNo").toString());
                                et_detailCompanyServiceType.setText(document.getData().get("serviceType").toString());
                                et_detailCompanyPhone.setText(document.getData().get("email").toString());
                                et_detailCompanyEmail.setText(document.getData().get("phone").toString());

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



                                try {
                                    et_detailCompanyDateApply.setText(document.getData().get("dateRegistration").toString());
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




    public void rejectRegistration() {
        Map<String, Object> appointment = new HashMap<>();

        appointment.put("updateRegistrationStatus", "reject");

        DocumentReference docRef = db.collection("users").document(userID);
        docRef.update("registrationStatus", appointment.get("updateRegistrationStatus"))
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(getApplicationContext(), "Successfully Update", Toast.LENGTH_SHORT).show();
                        Log.d(TAG, "DocumentSnapshot successfully updated!");

                        getProviderEmailFromDB_notifyProviderThroughEmail_statusReject();

//                        Intent intent = new Intent(getApplicationContext(), AppointmentScheduleServiceProvider.class);
//                        startActivity(intent);
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.w(TAG, "Error updating document", e);
            }
        });

    }


    public void acceptRegistration() {

        Map<String, Object> appointment = new HashMap<>();

        appointment.put("updateRegistrationStatus", "accept");

        DocumentReference docRef = db.collection("users").document(userID);
        docRef.update("registrationStatus", appointment.get("updateRegistrationStatus"))
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(getApplicationContext(), "Successfully Update", Toast.LENGTH_SHORT).show();
                        Log.d(TAG, "DocumentSnapshot successfully updated!");

                        getProviderEmailFromDB_notifyProviderThroughEmail_statusAccept();
//                        Intent intent = new Intent(getApplicationContext(), AppointmentScheduleServiceProvider.class);
//                        startActivity(intent);
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.w(TAG, "Error updating document", e);
            }
        });

    }



    // End Bracket
}