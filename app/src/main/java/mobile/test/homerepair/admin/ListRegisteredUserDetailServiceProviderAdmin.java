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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import mobile.test.homerepair.R;

public class ListRegisteredUserDetailServiceProviderAdmin extends AppCompatActivity {

    FirebaseFirestore db = FirebaseFirestore.getInstance();

    String userID,providerPictureURL;
    String TAG = "TAG";

    EditText et_detailCompanyName, et_detailCompanyServiceType, et_detailCompanyEmail,
            et_detailCompanyPhone, et_detailCompanyAddress, et_detailCompanyNo;

    ImageView img_pictureCompany;

    Button btn_BackToHome,btn_editUserInformation,btn_editServiceOffer,btn_pendingAppointment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_registered_user_detail_service_provider_admin);

        Intent intent = getIntent();
        userID = intent.getStringExtra("userID");
        Log.e("testUserID",userID);


        btn_BackToHome = findViewById(R.id.btn_BackToHome);

        img_pictureCompany =  findViewById(R.id.img_pictureCompany);
        et_detailCompanyName =  findViewById(R.id.et_detailCompanyName);
        et_detailCompanyServiceType =  findViewById(R.id.et_detailCompanyServiceType);
        et_detailCompanyEmail =  findViewById(R.id.et_detailCompanyEmail);
        et_detailCompanyPhone =  findViewById(R.id.et_detailCompanyPhone);
        et_detailCompanyAddress =  findViewById(R.id.et_detailCompanyAddress);

        et_detailCompanyNo =  findViewById(R.id.et_detailCompanyNo);
        btn_editUserInformation =  findViewById(R.id.btn_editUserInformation);
        btn_editServiceOffer = findViewById(R.id.btn_editServiceOffer);

        btn_pendingAppointment = findViewById(R.id.btn_pendingAppointment);


        displayProviderInfoFromDB();


        btn_BackToHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ApproveRegistrationAdmin.class);
                startActivity(intent);
            }
        });


        btn_editUserInformation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), UpdateUserServiceProviderInfoAdmin.class);
                intent.putExtra("userID",userID);
                startActivity(intent);
            }
        });


        btn_editServiceOffer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), UpdateServiceOfferOfProvider.class);
                intent.putExtra("userID",userID);
                startActivity(intent);
            }
        });

        btn_pendingAppointment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), PendingAppointmentList.class);
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

                                try {
                                    providerPictureURL = document.getData().get("pictureURL").toString();
                                    Picasso.with(getApplicationContext()).load(providerPictureURL).into(img_pictureCompany);

                                    et_detailCompanyName.setText(document.getData().get("companyName").toString());
                                    et_detailCompanyNo.setText(document.getData().get("companyNo").toString());
                                    et_detailCompanyServiceType.setText(document.getData().get("serviceType").toString());
                                    et_detailCompanyPhone.setText(document.getData().get("phone").toString());
                                    et_detailCompanyEmail.setText(document.getData().get("email").toString());

                                    // Get Full Address
                                    String fullAddress;

                                    fullAddress = document.getData().get("address1").toString() + ", ";
                                    fullAddress += document.getData().get("address2").toString() + ",\n";
                                    fullAddress += document.getData().get("postcode").toString() + " ";
                                    fullAddress += document.getData().get("city").toString() + "\n";
                                    fullAddress += document.getData().get("state").toString() ;

                                    et_detailCompanyAddress.setText(fullAddress);

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

    // End Bracket


}