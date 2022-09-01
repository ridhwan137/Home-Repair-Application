package mobile.test.homerepair.provider;

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
import android.widget.TextView;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import mobile.test.homerepair.MailAPI.JavaMailAPI;
import mobile.test.homerepair.R;

public class ServiceProviderAppointmentPending extends AppCompatActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {



    FirebaseFirestore db = FirebaseFirestore.getInstance();

    EditText et_detailClientName, et_detailClientEmail,
            et_detailClientPhone, et_detailClientAddress;

    TextView tv_detailClientDate, tv_detailClientTime, et_message;

    ImageView img_pictureClient;
    Button btn_pendingAppointmentReject,btn_pendingAppointmentAccept;

    String TAG = "TAG";
    String clientPictureURL;

    String appointmentID;
    String currentUserID;

    String clientAddress1,clientAddress2,clientPostcode,clientCity,clientState;
    String clientFullAddress;

    GoogleMap mGoogleMap;
    String getFullAddressForMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pending_appointment_service_provider);

        try{
            Intent intent = getIntent();
            appointmentID = intent.getStringExtra("appointmentID");
            Log.e("testGetAppointmentID", appointmentID);
        }catch (Exception e){
            e.printStackTrace();
            Log.e("testGetAppointmentID", "No-AppointmentID");
        }



        currentUserID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        Log.e("testGetCurrentUserID", currentUserID);


        img_pictureClient = findViewById(R.id.img_pictureClient);

        et_detailClientName =  findViewById(R.id.et_detailClientName);
        et_detailClientEmail =  findViewById(R.id.et_detailClientEmail);
        et_detailClientPhone =  findViewById(R.id.et_detailClientPhone);
        et_detailClientAddress =  findViewById(R.id.et_detailClientAddress);
        et_message = findViewById(R.id.et_message);

        tv_detailClientDate =  findViewById(R.id.tv_detailClientDate);
        tv_detailClientTime =  findViewById(R.id.tv_detailClientTime);



        btn_pendingAppointmentReject = findViewById(R.id.btn_pendingAppointmentReject);
        btn_pendingAppointmentAccept = findViewById(R.id.btn_pendingAppointmentAccept);

        getClientAddressFromDB();
        displayClientInfoFromDB();


        initMap();


        btn_pendingAppointmentReject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                rejectAppointment();
                rejectAppointmentUsingAppointmentStatusFromDB();

                Intent intent = new Intent(getApplicationContext(), AppointmentScheduleListServiceProviderTabLayout.class);
                intent.putExtra("testPassAppointmentID",appointmentID);
                startActivity(intent);
            }
        });

        btn_pendingAppointmentAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                acceptAppointment();
                acceptAppointmentUsingAppointmentStatusFromDB();

                Intent intent = new Intent(getApplicationContext(), AppointmentScheduleListServiceProviderTabLayout.class);
                intent.putExtra("testPassAppointmentID",appointmentID);
                startActivity(intent);

            }
        });


    }



    ///// Map Function
    private void geoLocate() {
        String locationName = getFullAddressForMap;

        Log.e("geoLocate->",locationName);

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
//            Intent intent = new Intent(getApplicationContext(),PendingAppointmentServiceProvider.class);
//            intent.putExtra("appointmentID",appointmentID);
//            overridePendingTransition(0,0);
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

                    Intent intent = new Intent(getApplicationContext(), ServiceProviderAppointmentPending.class);
                    intent.putExtra("appointmentID",appointmentID);
                    overridePendingTransition(0,0);
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

    private void initMap() {
        SupportMapFragment supportMapFragment =  (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapFragment);
        supportMapFragment.getMapAsync(this);
    }
    ///// Map Function




    public void displayClientInfoFromDB(){
        db.collection("appointment").whereEqualTo("appointmentID",appointmentID)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + " => " + document.getData());

                                clientPictureURL = document.getData().get("clientPictureURL").toString();
                                Picasso.with(getApplicationContext()).load(clientPictureURL).into(img_pictureClient);

                                et_detailClientName.setText(document.getData().get("clientName").toString());
                                et_detailClientEmail.setText(document.getData().get("clientEmail").toString());
                                et_detailClientPhone.setText(document.getData().get("clientPhone").toString());
                                et_detailClientAddress.setText(clientFullAddress);
                                tv_detailClientDate.setText(document.getData().get("date").toString());
                                tv_detailClientTime.setText(document.getData().get("time").toString());

                                getFullAddressForMap = clientFullAddress;
                                Log.e("displayClientInfoFromDB->",getFullAddressForMap);

                                et_message.setText(document.getData().get("message").toString());

                                String message = et_message.getText().toString();

                                if(message.equals("") || message == null){
                                    et_message.setText("No Message");
                                }


                                geoLocate();
                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }

                    }
                });
    }


    public void getClientAddressFromDB(){
        db.collection("appointment").whereEqualTo("appointmentID", appointmentID)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + " => " + document.getData());

                                try {
                                    clientAddress1 = document.getData().get("clientAddress1").toString() + ", ";
                                    clientAddress2 = document.getData().get("clientAddress2").toString() + ",\n";
                                    clientPostcode = document.getData().get("clientPostcode").toString() + " ";
                                    clientCity = document.getData().get("clientCity").toString() + ",\n";
                                    clientState = document.getData().get("clientState").toString();

                                    Log.e("clientAddress1",clientAddress1);
                                    Log.e("clientAddress2",clientAddress2);
                                    Log.e("clientPostcode",clientPostcode);
                                    Log.e("clientCity",clientCity);
                                    Log.e("clientState",clientState);

                                    clientFullAddress = clientAddress1 + clientAddress2 + clientPostcode + clientCity + clientState;

                                    Log.e("clientFullAddress",clientFullAddress);

                                }catch (Exception e){
                                    e.printStackTrace();
                                    Log.e("LogDisplayUserInformation","No Data In Database");
                                }

                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }

                    }
                });
    }



    public void rejectAppointment() {
        Map<String, Object> appointment = new HashMap<>();

        appointment.put("updateRequestStatus", "reject");

        DocumentReference docRef = db.collection("appointment").document(appointmentID);
        docRef.update("requestStatus", appointment.get("updateRequestStatus"))
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(getApplicationContext(), "Successfully Updated", Toast.LENGTH_SHORT).show();
                        Log.d(TAG, "DocumentSnapshot successfully updated!");
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.w(TAG, "Error updating document", e);
            }
        });

    }


    public void acceptAppointment() {
        // use db update requestStatus and apppointmentStatus to "cancel"

        Map<String, Object> appointment = new HashMap<>();

        appointment.put("updateRequestStatus", "accept");
        appointment.put("updateAppointmentStatus", "in-progress");

        DocumentReference docRef = db.collection("appointment").document(appointmentID);
        docRef.update("requestStatus", appointment.get("updateRequestStatus"),
                "appointmentStatus", appointment.get("updateAppointmentStatus"))
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(getApplicationContext(), "Successfully Updated", Toast.LENGTH_SHORT).show();
                        Log.d(TAG, "DocumentSnapshot successfully updated!");
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.w(TAG, "Error updating document", e);
            }
        });

    }



    public void rejectAppointmentUsingAppointmentStatusFromDB() {
        Map<String, Object> appointment = new HashMap<>();

        appointment.put("updateRequestStatus", "reject");

        DocumentReference docRef = db.collection("appointment").document(appointmentID);
        docRef.update("appointmentStatus", appointment.get("updateRequestStatus"))
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                        getAppointmentDetailFromDB_notifyClientThroughEmail_statusReject(appointmentID);

                        Toast.makeText(getApplicationContext(), "Successfully Updated", Toast.LENGTH_SHORT).show();
                        Log.d(TAG, "DocumentSnapshot successfully updated!");

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


    public void acceptAppointmentUsingAppointmentStatusFromDB() {
        // use db update requestStatus and apppointmentStatus to "cancel"

        Map<String, Object> appointment = new HashMap<>();

        appointment.put("updateAppointmentStatus", "in-progress");

        DocumentReference docRef = db.collection("appointment").document(appointmentID);
        docRef.update("appointmentStatus", appointment.get("updateAppointmentStatus"))
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                        getAppointmentDetailFromDB_notifyClientThroughEmail_statusAccept(appointmentID);

                        Toast.makeText(getApplicationContext(), "Successfully Updated", Toast.LENGTH_SHORT).show();
                        Log.d(TAG, "DocumentSnapshot successfully updated!");

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



    // <-- Notification Through Email Appointment Accept
    private void getAppointmentDetailFromDB_notifyClientThroughEmail_statusAccept(String appointmentID) {

        db.collection("appointment")
                .whereEqualTo("appointmentID", appointmentID)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {

                        String clientEmailFromDB = null,
                                providerName = null,
                                providerPhone = null,
                                providerEmail = null,
                                providerFullAddress = null,
                                providerAppointmentDate = null,
                                providerAppointmentTime = null;

                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.e("getDataFormDb->", document.getId() + " => " + document.getData());

                                clientEmailFromDB = document.getData().get("clientEmail").toString();
                                providerName = document.getData().get("companyName").toString();
                                providerPhone = document.getData().get("companyPhone").toString();
                                providerEmail = document.getData().get("companyEmail").toString();
                                providerAppointmentDate = document.getData().get("date").toString();
                                providerAppointmentTime = document.getData().get("time").toString();


                                // Get Full Address
                                String fullAddress;

                                fullAddress = document.getData().get("companyAddress1").toString() + ", ";
                                fullAddress += document.getData().get("companyAddress2").toString() + ", \n";
                                fullAddress += document.getData().get("companyPostcode").toString() + " ";
                                fullAddress += document.getData().get("companyState").toString() + ", \n";
                                fullAddress += document.getData().get("companyCity").toString() ;

                                providerFullAddress = fullAddress;

                            }

                            sendEmailNotificationToProvider_statusAccept(clientEmailFromDB, providerName,
                                    providerPhone, providerEmail, providerFullAddress, providerAppointmentDate, providerAppointmentTime);


                        } else {
                            Log.e("getDataFormDb2->", "Error getting documents: ", task.getException());
                        }
                    }
                });
    }


    private void sendEmailNotificationToProvider_statusAccept(String receiverEmail, String name,
                                                 String phone, String email,
                                                 String fullAddress, String date, String time) {

        String emailReceiver = receiverEmail;
        String subjectNotify = "Home Repair Apps: Appointment Accept By Service Provider";
        String messageNotify = "Your request appointment has been accepted by" +
                "\n\nService Provider: " + name +
                "\nPhone: " + phone +
                "\nEmail: " + email +
                "\nLocation: " + fullAddress +
                "\n\nAppointment Date: " + date + " " + time;


        String mail = emailReceiver.trim();
        String subject = subjectNotify.trim();
        String message = messageNotify;

        //Send Mail
        JavaMailAPI javaMailAPI = new JavaMailAPI(this, mail, subject, message);
        javaMailAPI.execute();
    }

    // --> Notification Through Email



    // <-- Notification Through Email Appointment Reject
    private void getAppointmentDetailFromDB_notifyClientThroughEmail_statusReject(String appointmentID) {

        db.collection("appointment")
                .whereEqualTo("appointmentID", appointmentID)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {

                        String clientEmailFromDB = null,
                                providerName = null,
                                providerPhone = null,
                                providerEmail = null,
                                providerFullAddress = null,
                                providerAppointmentDate = null,
                                providerAppointmentTime = null;

                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.e("getDataFormDb->", document.getId() + " => " + document.getData());

                                clientEmailFromDB = document.getData().get("clientEmail").toString();
                                providerName = document.getData().get("companyName").toString();
                                providerPhone = document.getData().get("companyPhone").toString();
                                providerEmail = document.getData().get("companyEmail").toString();
                                providerAppointmentDate = document.getData().get("date").toString();
                                providerAppointmentTime = document.getData().get("time").toString();


                                // Get Full Address
                                String fullAddress;

                                fullAddress = document.getData().get("companyAddress1").toString() + ", ";
                                fullAddress += document.getData().get("companyAddress2").toString() + ", \n";
                                fullAddress += document.getData().get("companyPostcode").toString() + " ";
                                fullAddress += document.getData().get("companyState").toString() + ", \n";
                                fullAddress += document.getData().get("companyCity").toString() ;

                                providerFullAddress = fullAddress;

                            }

                            sendEmailNotificationToProvider_statusReject(clientEmailFromDB, providerName,
                                    providerPhone, providerEmail, providerFullAddress, providerAppointmentDate, providerAppointmentTime);


                        } else {
                            Log.e("getDataFormDb2->", "Error getting documents: ", task.getException());
                        }
                    }
                });
    }


    private void sendEmailNotificationToProvider_statusReject(String receiverEmail, String name,
                                                 String phone, String email,
                                                 String fullAddress, String date, String time) {

        String emailReceiver = receiverEmail;
        String subjectNotify = "Home Repair Apps: Appointment Reject By Service Provider";
        String messageNotify = "Your request appointment has been rejected by" +
                "\n\nService Provider: " + name +
                "\nPhone: " + phone +
                "\nEmail: " + email +
                "\nLocation: " + fullAddress +
                "\n\nAppointment Date: " + date + " " + time;


        String mail = emailReceiver.trim();
        String subject = subjectNotify.trim();
        String message = messageNotify;

        //Send Mail
        JavaMailAPI javaMailAPI = new JavaMailAPI(this, mail, subject, message);
        javaMailAPI.execute();
    }

    // --> Notification Through Email


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


    public void backButton(View view) {
        Intent intent = new Intent(getApplicationContext(), AppointmentScheduleListServiceProviderTabLayout.class);
        intent.putExtra("appointmentID",appointmentID);
        startActivity(intent);
    }

    @Override
    public void onBackPressed()
    {
        moveTaskToBack(true);
    }

}