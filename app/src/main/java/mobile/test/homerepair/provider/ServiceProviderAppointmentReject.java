package mobile.test.homerepair.provider;

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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

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

public class ServiceProviderAppointmentReject extends AppCompatActivity {

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
        setContentView(R.layout.activity_reject_appointment_service_provider);

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



        getClientAddressFromDB();
        displayClientInfoFromDB();


    }


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




    public void backButton(View view) {
        Intent intent = new Intent(getApplicationContext(), HistoryAppointmentListServiceProviderTabLayout.class);
        intent.putExtra("appointmentID",appointmentID);
        startActivity(intent);
    }

    @Override
    public void onBackPressed()
    {
        moveTaskToBack(true);
    }

}