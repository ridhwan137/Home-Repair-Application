package mobile.test.homerepair.provider.unnecessary;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;

import net.steamcrafted.materialiconlib.MaterialIconView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import mobile.test.homerepair.R;
import mobile.test.homerepair.model.Services;
import mobile.test.homerepair.provider.AppointmentScheduleServiceProviderTabLayout;
import mobile.test.homerepair.provider.HistoryAppointmentServiceProviderTabLayout;
import mobile.test.homerepair.provider.ProfileServiceProvider;

public class AddServices extends AppCompatActivity implements AddServicesRVAdapter.ItemClickListener{

    private RecyclerView rvServiceItem;
    private ArrayList<Services> servicesArrayList;
    private AddServicesRVAdapter addServicesRVAdapter;

    ProgressBar loadingPB;

    private FirebaseAuth mAuth;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseUser user;

    String serviceID,currentUserID;
    String serviceName,servicePrice;

    MaterialIconView btn_addItem;
    EditText et_addServiceOffer,et_addServicePrice;

    Button btn_BackToHome;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_service_offer);

/*        try {
            Intent intent = getIntent();
            clientID = intent.getStringExtra("userID");
            Log.e("testGetUserID", clientID);
        }catch (Exception e){
            e.printStackTrace();
        }*/



        currentUserID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        Log.e("testGetCurrentUserID", currentUserID);



        //Iniatialize and Assign Variable
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);

        //Set Menu Selected
        bottomNavigationView.setSelectedItemId(R.id.menu_serviceOffer);

        //Perform ItemSelectedListener
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()){
                    case R.id.menu_serviceOffer:
//                        startActivity(new Intent(getApplicationContext(),AddServices.class));
//                        overridePendingTransition(0,0);
                        return true;

                    case R.id.menu_appointmentSchedule:
                        startActivity(new Intent(getApplicationContext(), AppointmentScheduleServiceProviderTabLayout.class));
                        overridePendingTransition(0,0);
                        return true;

                    case R.id.menu_historyAppointmentSchedule:
                        startActivity(new Intent(getApplicationContext(), HistoryAppointmentServiceProviderTabLayout.class));
                        overridePendingTransition(0,0);
                        return true;

                    case R.id.menu_profile:
                        startActivity(new Intent(getApplicationContext(), ProfileServiceProvider.class));
                        overridePendingTransition(0,0);
                        return true;

                }
                return false;
            }
        });


        btn_BackToHome = findViewById(R.id.btn_BackToHome);

        loadingPB = findViewById(R.id.idProgressBar);
        rvServiceItem = findViewById(R.id.rvServiceItem);
        et_addServiceOffer = findViewById(R.id.et_addServiceOffer);
        et_addServicePrice = findViewById(R.id.et_addServicePrice);
        btn_addItem = findViewById(R.id.btn_addItem);




        btn_addItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                addServiceOffer();

            }
        });



        // creating our new array list
        servicesArrayList = new ArrayList<>();
        rvServiceItem.setHasFixedSize(true);
        rvServiceItem.setLayoutManager(new LinearLayoutManager(this));

        // adding our array list to our recycler view adapter class.
        addServicesRVAdapter = new AddServicesRVAdapter(servicesArrayList, this);

        addServicesRVAdapter.setClickListener(this);

        // setting adapter to our recycler view.
        rvServiceItem.setAdapter(addServicesRVAdapter);


        // display to recycleview
        db.collection("serviceOffer")
                .whereEqualTo("userID",currentUserID)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                        if (!queryDocumentSnapshots.isEmpty()) {

                            loadingPB.setVisibility(View.GONE);
                            List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();
                            for (DocumentSnapshot d : list) {

                                Services c = d.toObject(Services.class);

                                servicesArrayList.add(c);
                            }

                            addServicesRVAdapter.notifyDataSetChanged();
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




/*        btn_BackToHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), HomeServiceProvider.class);
                startActivity(intent);
            }
        });*/

    }

    @Override
    public void onItemClick(View view, int position) {
        String test = addServicesRVAdapter.getItem(position).getServiceName();
        Toast.makeText(getApplicationContext(), "Test: " + test, Toast.LENGTH_SHORT).show();
    }



    public void addServiceOffer(){


        // <--- Validation Input

        serviceName = et_addServiceOffer.getText().toString();
        servicePrice = et_addServicePrice.getText().toString();


        if (serviceName.isEmpty() && servicePrice.isEmpty()){
            et_addServiceOffer.setError("Field is empty");
            et_addServicePrice.setError("Field is empty");
            return;
        }

        if (serviceName.isEmpty()){
            et_addServiceOffer.setError("Field is empty");
            return;
        }

        if (servicePrice.isEmpty()){
            et_addServicePrice.setError("Field is empty");
            return;
        }

//        if (!serviceName.matches("^[a-zA-Z\\s]+$")){
//            et_addServiceOffer.setError("Invalid character, input A~Z only");
//            return;
//        }


        if (!servicePrice.matches("^\\d+\\.\\d{2,2}$")){
            et_addServicePrice.setError("Invalid character, input 0.00~9.00 only");
            return;
        }

        // ----> Validation Input


        //create random id
        Random rand = new Random();
        int randomID = rand.nextInt(99999999)+1;

        serviceID = "service" + randomID;

        Map<String, Object> serviceOffer = new HashMap<>();
        serviceOffer.put("userID",currentUserID);
        serviceOffer.put("serviceName",serviceName);
        serviceOffer.put("servicePrice",servicePrice);
        serviceOffer.put("serviceID",serviceID);

        String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        Log.e("testIdentifyUserID",userID);


        db.collection("serviceOffer").document(serviceID)
                .set(serviceOffer)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Intent intent = new Intent(getApplicationContext(), AddServices.class);
                        startActivity(intent);

                        AddServices.this.finish();

                        Toast.makeText(AddServices.this, "Services added successfully.", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("addServiceOffer->", "Error writing document");
                    }
                });


        // input one attribute has service offer on table user
        Map<String, Object> data = new HashMap<>();
        data.put("hasServiceOffer","yes");

        db.collection("users").document(currentUserID)
                .set(data, SetOptions.merge())
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Intent intent = new Intent(getApplicationContext(), AddServices.class);
                        startActivity(intent);

                        AddServices.this.finish();

                        Toast.makeText(AddServices.this, "Services added successfully.", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("addServiceOffer->", "Error writing document");
                    }
                });


    }


    // Add to FireStore DB
    public void addServiceOfferBugError(){

        serviceName = et_addServiceOffer.getText().toString();
        servicePrice = et_addServicePrice.getText().toString();

        //create random id
        Random rand = new Random();
        int randomID = rand.nextInt(99999999)+1;

        serviceID = "service" + randomID;

        Map<String, Object> serviceOffer = new HashMap<>();
        serviceOffer.put("userID",currentUserID);
        serviceOffer.put("serviceName",serviceName);
        serviceOffer.put("servicePrice",servicePrice);
        serviceOffer.put("serviceID",serviceID);

        String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        Log.e("testIdentifyUserID",userID);

        FirebaseFirestore.getInstance().collection("users")
                .whereEqualTo("userID", currentUserID)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(@NonNull QuerySnapshot queryDocumentSnapshots) {
                        Log.e("testIdentifyDocumentMaxSize", String.valueOf(queryDocumentSnapshots.size()));
                        if (queryDocumentSnapshots.size() == 0 ) {
                            db.collection("serviceOffer").document(serviceID)
                                    .set(serviceOffer)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Intent intent = new Intent(getApplicationContext(), AddServices.class);
                                            startActivity(intent);

                                            AddServices.this.finish();

                                            Toast.makeText(AddServices.this, "Services added successfully.", Toast.LENGTH_SHORT).show();
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
//                                            Log.w(TAG, "Error writing document", e);
                                        }
                                    });
                        } else {
                            Toast.makeText(AddServices.this, "Unsuccessfully Add Services", Toast.LENGTH_SHORT).show();
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