package mobile.test.homerepair.client;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import mobile.test.homerepair.R;
import mobile.test.homerepair.model.Services;
import mobile.test.homerepair.model.Users;

public class ServiceProviderDetail extends AppCompatActivity implements ServiceProviderDetailRVAdapter.ItemClickListener {

    private RecyclerView rvServiceDetail;

    private ArrayList<Services> servicesArrayList;
    private ArrayList<Users> usersArrayList;
    private ServiceProviderDetailRVAdapter serviceProviderDetailRVAdapter;

    ProgressBar loadingPB;

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseUser user;

    String userID,pictureURL;
    String TAG = "TAG";

    EditText et_detailCompanyName, et_detailCompanyServiceType, et_detailCompanyEmail,
            et_detailCompanyPhone, et_detailCompanyAddress;

    ImageView img_pictureCompany;
    Button btn_BackToHome,btn_detailRequestAppointment;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_provider_detail);

        Intent intent = getIntent();
        userID = intent.getStringExtra("userID");
        Log.e("testUserID",userID);


        btn_detailRequestAppointment = findViewById(R.id.btn_detailRequestAppointment);
        btn_BackToHome = findViewById(R.id.btn_BackToHome);
        img_pictureCompany = findViewById(R.id.img_pictureCompany);

        et_detailCompanyName =  findViewById(R.id.et_detailCompanyName);
        et_detailCompanyServiceType =  findViewById(R.id.et_detailCompanyServiceType);
        et_detailCompanyEmail =  findViewById(R.id.et_detailCompanyEmail);
        et_detailCompanyPhone =  findViewById(R.id.et_detailCompanyPhone);
        et_detailCompanyAddress =  findViewById(R.id.et_detailCompanyAddress);


        loadingPB = findViewById(R.id.idProgressBar);
        rvServiceDetail = findViewById(R.id.rvServiceDetail);


        servicesArrayList = new ArrayList<>();
        rvServiceDetail.setHasFixedSize(true);
        rvServiceDetail.setLayoutManager(new LinearLayoutManager(this));

        serviceProviderDetailRVAdapter = new ServiceProviderDetailRVAdapter(servicesArrayList,this);
        serviceProviderDetailRVAdapter.setClickListener(this);

        rvServiceDetail.setAdapter(serviceProviderDetailRVAdapter);


        displayProviderInfoFromDB();

        displayServiceOffer();


        btn_BackToHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), SearchServices.class);
                startActivity(intent);
            }
        });

        btn_detailRequestAppointment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), RequestAppointment.class);

                String providerID = userID;
                Log.e("testPassProviderUserID",providerID);
                intent.putExtra("userID",providerID);
                startActivity(intent);
            }
        });


    }

    @Override
    public void onItemClick(View view, int position){
        String test = serviceProviderDetailRVAdapter.getItem(position).getServiceID();
        Toast.makeText(getApplicationContext(), "Test"+test, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

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

                                try{
                                    pictureURL = document.getData().get("pictureURL").toString();
                                    Picasso.with(getApplicationContext()).load(pictureURL).into(img_pictureCompany);

                                }catch(Exception e){
                                    e.printStackTrace();
                                    Log.e("noPicture","noPictureInDatabase");
                                }

                                et_detailCompanyName.setText(document.getData().get("companyName").toString());
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
                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }

                    }
                });
    }


    public void displayServiceOffer(){
        db.collection("serviceOffer")
                .whereEqualTo("userID",userID)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                        if (!queryDocumentSnapshots.isEmpty()) {

                            loadingPB.setVisibility(View.GONE);
                            List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();
                            for (DocumentSnapshot documentSnapshot : list) {

                                Services services = documentSnapshot.toObject(Services.class);

                                servicesArrayList.add(services);
                            }

                            serviceProviderDetailRVAdapter.notifyDataSetChanged();
                        } else {
                            // if the snapshot is empty we are displaying a toast message.
                            loadingPB.setVisibility(View.GONE);
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

                Toast.makeText(getApplicationContext(), "Fail to get the data.", Toast.LENGTH_SHORT).show();
            }
        });
    }



    public void errorDisplayUserInformation(){

        Log.e("testGetUserDisplayInformation",userID);

        String getUserID = userID;

        Log.e("testGetUserDisplayInformation2",getUserID);

        DocumentReference docRef = db.collection("users").document(getUserID);

        Log.e("testGetDofRef", String.valueOf(docRef));

        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                if(task.isSuccessful()){
                    DocumentSnapshot document = task.getResult();

                    if (document.exists()){

                        et_detailCompanyName.setText(document.getData().get("companyName").toString());
                        et_detailCompanyServiceType.setText(document.getData().get("serviceType").toString());
                        et_detailCompanyPhone.setText(document.getData().get("email").toString());
                        et_detailCompanyEmail.setText(document.getData().get("phone").toString());



                        // Get Full Address
                        String fullAddress;

                        fullAddress = document.getData().get("address1").toString() + ", ";
                        fullAddress += document.getData().get("address2").toString() + ", \n";
                        fullAddress += document.getData().get("postcode").toString() + " ";
                        fullAddress += document.getData().get("city").toString() + ", \n";
                        fullAddress += document.getData().get("state").toString() ;

                        et_detailCompanyAddress.setText(fullAddress);

               /*         try {
                            url = document.getData().get("Picture URL").toString();
//                        new EditProfileActivity.FetchImage(url).start();
                            Picasso.with(getApplicationContext()).load(url).into(ivProfile);
                        }catch (Exception e){
                            e.printStackTrace();
                            Log.e("testPicture","No Picture");
                        }*/



                    }else{
                        // No document
                        Log.d(TAG,"no document");
                    }
                }else{
                    Log.d(TAG,"get failed with",task.getException());
                }

            }
        });
    }


    @Override
    public void onBackPressed()
    {
        moveTaskToBack(true);
    }

}