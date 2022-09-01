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

import mobile.test.homerepair.MailAPI.JavaMailAPI;
import mobile.test.homerepair.R;

public class ClientAppointmentPending extends AppCompatActivity {

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    StorageReference storageReference;

    EditText et_detailCompanyName, et_detailCompanyServiceType, et_detailCompanyEmail,
            et_detailCompanyPhone, et_detailCompanyAddress, et_message;

    TextView tv_detailCompanyDate, tv_detailCompanyTime;

    ImageView img_pictureCompany;
    Button btn_pendingAppointmentCancel;

    String TAG = "TAG";
    String pictureURL;

    String appointmentID;
    String currentUserID;

    String providerAddress1, providerAddress2, providerPostcode, providerState, providerCity;
    String providerFullAddress;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pending_appointment_client);


        try {
            Intent intent = getIntent();
            appointmentID = intent.getStringExtra("appointmentID");
            Log.e("testGetAppointmentID", appointmentID);
        } catch (Exception e) {
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

//                Intent intent = new Intent(getApplicationContext(), AppointmentScheduleClientTabLayout.class);
                Intent intent = new Intent(getApplicationContext(), AppointmentScheduleListClientTabLayout.class);
                intent.putExtra("clientID", currentUserID);
                startActivity(intent);

            }
        });

    }


    // <-- Notification Through Email
    private void getAppointmentDetailFromDB_notifyProviderThroughEmail(String appointmentID) {

        db.collection("appointment")
                .whereEqualTo("appointmentID", appointmentID)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {

                        String providerEmailFromDB = null,
                                clientName = null,
                                clientPhone = null,
                                clientEmail = null,
                                clientFullAddress = null,
                                clientAppointmentDate = null,
                                clientAppointmentTime = null;

                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.e("getDataFormDb->", document.getId() + " => " + document.getData());

                                providerEmailFromDB = document.getData().get("companyEmail").toString();
                                clientName = document.getData().get("clientName").toString();
                                clientPhone = document.getData().get("clientPhone").toString();
                                clientEmail = document.getData().get("clientEmail").toString();
                                clientAppointmentDate = document.getData().get("date").toString();
                                clientAppointmentTime = document.getData().get("time").toString();


                                // Get Full Address
                                String fullAddress;

                                fullAddress = document.getData().get("clientAddress1").toString() + ", ";
                                fullAddress += document.getData().get("clientAddress2").toString() + ", \n";
                                fullAddress += document.getData().get("clientPostcode").toString() + " ";
                                fullAddress += document.getData().get("clientState").toString() + ", \n";
                                fullAddress += document.getData().get("clientCity").toString() ;

                                clientFullAddress = fullAddress;

                            }

                            sendEmailNotificationToProvider(providerEmailFromDB, clientName,
                                    clientPhone, clientEmail, clientFullAddress, clientAppointmentDate, clientAppointmentTime);


                        } else {
                            Log.e("getDataFormDb2->", "Error getting documents: ", task.getException());
                        }
                    }
                });
    }


    private void sendEmailNotificationToProvider(String receiverEmail, String clientName,
                                                 String clientPhone, String clientEmail,
                                                 String clientFullAddress, String date, String time) {

        String emailReceiver = receiverEmail;
        String subjectNotify = "Home Repair Apps: Appointment Cancellation From Client";
        String messageNotify = "Your appointment has been cancel by" +
                "\n\nClient: " + clientName +
                "\nPhone: " + clientPhone +
                "\nEmail: " + clientEmail +
                "\nLocation: " + clientFullAddress +
                "\n\nAppointment Date: " + date + " " + time;


        String mail = emailReceiver.trim();
        String subject = subjectNotify.trim();
        String message = messageNotify;

        //Send Mail
        JavaMailAPI javaMailAPI = new JavaMailAPI(this, mail, subject, message);
        javaMailAPI.execute();
    }

    // --> Notification Through Email


    public void getProviderAddressFromDB() {
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

                                    Log.e("providerAddress1", providerAddress1);
                                    Log.e("providerAddress2", providerAddress2);
                                    Log.e("providerPostcode", providerPostcode);
                                    Log.e("providerCity", providerCity);
                                    Log.e("providerState", providerState);

                                    providerFullAddress = providerAddress1 + providerAddress2 + providerPostcode + providerCity + providerState;

                                    Log.e("providerFullAddress", providerFullAddress);

                                } catch (Exception e) {
                                    e.printStackTrace();
                                    Log.e("LogDisplayUserInformation", "No Data In Database");
                                }

                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }

                    }
                });
    }


    public void displayProviderInfoFromDB() {
        db.collection("appointment").whereEqualTo("appointmentID", appointmentID)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + " => " + document.getData());

                                try {
                                    pictureURL = document.getData().get("providerPictureURL").toString();
                                    Picasso.with(getApplicationContext()).load(pictureURL).into(img_pictureCompany);

                                } catch (Exception e) {
                                    e.printStackTrace();
                                }

                                et_detailCompanyName.setText(document.getData().get("companyName").toString());
                                et_detailCompanyServiceType.setText(document.getData().get("companyServiceType").toString());
                                et_detailCompanyEmail.setText(document.getData().get("companyEmail").toString());
                                et_detailCompanyPhone.setText(document.getData().get("companyPhone").toString());
                                et_detailCompanyAddress.setText(providerFullAddress);

                                tv_detailCompanyDate.setText(document.getData().get("date").toString());
                                tv_detailCompanyTime.setText(document.getData().get("time").toString());


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


    public void cancelAppointment() {
        // use db update requestStatus and apppointmentStatus to "cancel"

        Map<String, Object> appointment = new HashMap<>();

        appointment.put("updateAppointmentStatus", "cancel");

        DocumentReference docRef = db.collection("appointment").document(appointmentID);
        docRef.update("appointmentStatus", appointment.get("updateAppointmentStatus"))
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                        getAppointmentDetailFromDB_notifyProviderThroughEmail(appointmentID);

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


    public void backButton(View view) {
//        Intent intent = new Intent(getApplicationContext(), AppointmentScheduleClientTabLayout.class);
        Intent intent = new Intent(getApplicationContext(), AppointmentScheduleListClientTabLayout.class);
        intent.putExtra("testPassAppointmentID", appointmentID);
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }
}