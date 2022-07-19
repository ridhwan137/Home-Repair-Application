package mobile.test.homerepair.admin;

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
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

import mobile.test.homerepair.R;
import mobile.test.homerepair.client.SearchServices;
import mobile.test.homerepair.provider.AppointmentScheduleServiceProviderTabLayout;

public class ApproveRegisteredUserServiceProviderDetailAdmin extends AppCompatActivity {

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseUser user;

    String userID,pictureURL;
    String TAG = "TAG";

//    TextView tv_detailCompanyName,tv_detailCompanyServiceType,tv_detailCompanyEmail,
//            tv_detailCompanyPhone,tv_detailCompanyAddress,tv_detailCompanyNo,tv_detailCompanyDateApply;

    EditText et_detailCompanyName,et_detailCompanyServiceType,et_detailCompanyEmail,et_detailCompanyPhone,
            et_detailCompanyAddress,et_detailCompanyNo,et_detailCompanyDateApply;

    Button btn_BackToHome,btn_acceptRegistration,btn_rejectRegistration;

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


    public void displayProviderInfoFromDB(){
        db.collection("users").whereEqualTo("userID",userID)
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


    public void acceptRegistration() {

        Map<String, Object> appointment = new HashMap<>();

        appointment.put("updateRegistrationStatus", "accept");

        DocumentReference docRef = db.collection("users").document(userID);
        docRef.update("registrationStatus", appointment.get("updateRegistrationStatus"))
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
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



    // End Bracket
}