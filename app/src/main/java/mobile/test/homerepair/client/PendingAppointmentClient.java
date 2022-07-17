package mobile.test.homerepair.client;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

import mobile.test.homerepair.R;

public class PendingAppointmentClient extends AppCompatActivity {

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    StorageReference storageReference;

    EditText et_detailCompanyName, et_detailCompanyServiceType, et_detailCompanyEmail,
            et_detailCompanyPhone, et_detailCompanyAddress, et_message;

    TextView tv_detailCompanyDate,tv_detailCompanyTime;

    ImageView img_pictureCompany;
    Button btn_pendingAppointmentCancel;

    String TAG = "TAG";
    String pictureURL;

    String appointmentID;
    String currentUserID;

    String providerAddress1,providerAddress2,providerPostcode,providerState,providerCity;
    String providerFullAddress;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pending_appointment_client);


        try {
            Intent intent = getIntent();
            appointmentID = intent.getStringExtra("appointmentID");
            Log.e("testGetAppointmentID", appointmentID);
        }catch (Exception e){
            e.printStackTrace();
            Log.e("appointmentID->", "No-appointmentID");
        }



        currentUserID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        Log.e("testGetCurrentUserID", currentUserID);


        img_pictureCompany = findViewById(R.id.img_pictureCompany);

        et_detailCompanyName = findViewById(R.id.et_detailCompanyName);
        et_detailCompanyServiceType = findViewById(R.id.et_detailCompanyServiceType);
        et_detailCompanyEmail = findViewById(R.id.et_detailCompanyEmail);
        et_detailCompanyPhone = findViewById(R.id.et_detailCompanyPhone);
        et_detailCompanyAddress = findViewById(R.id.et_detailCompanyAddress);
        et_message = findViewById(R.id.et_message);

        tv_detailCompanyDate = findViewById(R.id.tv_detailCompanyDate);
        tv_detailCompanyTime = findViewById(R.id.tv_detailCompanyTime);


        btn_pendingAppointmentCancel = findViewById(R.id.btn_pendingAppointmentCancel);

        getProviderAddressFromDB();

        displayProviderInfoFromDB();

        btn_pendingAppointmentCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancelAppointment();

                Intent intent = new Intent(getApplicationContext(), AppointmentScheduleClientTabLayout.class);
                intent.putExtra("clientID",currentUserID);
                startActivity(intent);

            }
        });

    }


    public void getProviderAddressFromDB(){
        db.collection("appointment").whereEqualTo("appointmentID", appointmentID)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + " => " + document.getData());

                                try {
                                    providerAddress1 = document.getData().get("companyAddress1").toString() + ", ";
                                    providerAddress2 = document.getData().get("companyAddress2").toString() + ",\n";
                                    providerPostcode = document.getData().get("companyPostcode").toString() + " ";
                                    providerCity = document.getData().get("companyCity").toString() + ",\n";
                                    providerState = document.getData().get("companyState").toString();

                                    Log.e("providerAddress1",providerAddress1);
                                    Log.e("providerAddress2",providerAddress2);
                                    Log.e("providerPostcode",providerPostcode);
                                    Log.e("providerCity",providerCity);
                                    Log.e("providerState",providerState);

                                    providerFullAddress = providerAddress1 + providerAddress2 + providerPostcode + providerCity + providerState;

                                    Log.e("providerFullAddress",providerFullAddress);

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


    public void displayProviderInfoFromDB(){
        db.collection("appointment").whereEqualTo("appointmentID",appointmentID)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + " => " + document.getData());

                                pictureURL = document.getData().get("providerPictureURL").toString();
                                Picasso.with(getApplicationContext()).load(pictureURL).into(img_pictureCompany);

                                et_detailCompanyName.setText(document.getData().get("companyName").toString());
                                et_detailCompanyServiceType.setText(document.getData().get("companyServiceType").toString());
                                et_detailCompanyEmail.setText(document.getData().get("companyEmail").toString());
                                et_detailCompanyPhone.setText(document.getData().get("companyPhone").toString());
                                et_detailCompanyAddress.setText(providerFullAddress);
                                et_message.setText(document.getData().get("message").toString());

                                tv_detailCompanyDate.setText(document.getData().get("date").toString());
                                tv_detailCompanyTime.setText(document.getData().get("time").toString());
                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }

                    }
                });
    }




    public void cancelAppointmentDeleted(){
        db.collection("appointment").document(appointmentID)
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("TAG", "DocumentSnapshot successfully deleted!");
                        Intent intent = new Intent(getApplicationContext(), AppointmentScheduleClient.class);
                        intent.putExtra("clientID",currentUserID);
                        startActivity(intent);

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("TAG", "Error deleting document", e);
                    }
                });
    }

    public void cancelAppointment(){
        // use db update requestStatus and apppointmentStatus to "cancel"

        Map<String, Object> appointment = new HashMap<>();

        appointment.put("updateAppointmentStatus","cancel");

        DocumentReference docRef = db.collection("appointment").document(appointmentID);
        docRef.update("appointmentStatus",appointment.get("updateAppointmentStatus"))
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




/*
        db.collection("appointment").document(appointmentID)
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("TAG", "DocumentSnapshot successfully deleted!");
                        Intent intent = new Intent(getApplicationContext(), AppointmentScheduleClient.class);
                        intent.putExtra("clientID",currentUserID);
                        startActivity(intent);

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("TAG", "Error deleting document", e);
                    }
                });

                */
    }


    public void backButton(View view) {
        Intent intent = new Intent(getApplicationContext(), AppointmentScheduleClientTabLayout.class);
        intent.putExtra("testPassAppointmentID",appointmentID);
        startActivity(intent);
    }

    @Override
    public void onBackPressed()
    {
        moveTaskToBack(true);
    }
}