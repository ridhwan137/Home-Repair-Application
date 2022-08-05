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
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import mobile.test.homerepair.R;

public class AppointmentClientDetail extends AppCompatActivity {

    FirebaseFirestore db = FirebaseFirestore.getInstance();

    String clientID,clientPictureURL;
    String TAG = "TAG";

    EditText et_detailClientName, et_detailClientEmail,
            et_detailClientPhone, et_detailClientAddress;

    ImageView img_pictureClient;

    Button btn_BackToHome,btn_editUserInformation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appointment_client_detail);

        Intent intent = getIntent();
        clientID = intent.getStringExtra("clientID");
        Log.e("clientID->", clientID);


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
                Intent intent = new Intent(getApplicationContext(), ApproveRegistrationAdmin.class);
                startActivity(intent);
            }
        });



     ////////////
    }

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