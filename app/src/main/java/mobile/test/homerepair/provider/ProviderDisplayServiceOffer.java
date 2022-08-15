package mobile.test.homerepair.provider;

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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import mobile.test.homerepair.R;
import mobile.test.homerepair.model.Services;

public class ProviderDisplayServiceOffer extends AppCompatActivity implements ProviderDisplayServiceOfferRVAdapter.ItemClickListener{

    private RecyclerView rvServiceItem;
    private ArrayList<Services> servicesArrayList;
    private ProviderDisplayServiceOfferRVAdapter providerDisplayServiceOfferRVAdapter;

    ProgressBar loadingPB;

    FirebaseFirestore db = FirebaseFirestore.getInstance();

    String serviceType;

    EditText et_addServiceOffer,et_addServicePrice;

    Button btn_serviceTypeName;

    String currentUserID;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_provider_display_service_offer);


        currentUserID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        Log.e("testGetCurrentUserID", currentUserID);


        // Get User Service Type from DB
        getUserServiceTypeFromDB();


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
                        startActivity(new Intent(getApplicationContext(),AppointmentScheduleServiceProviderTabLayout.class));
                        overridePendingTransition(0,0);
                        return true;

                    case R.id.menu_historyAppointmentSchedule:
                        startActivity(new Intent(getApplicationContext(),HistoryAppointmentServiceProviderTabLayout.class));
                        overridePendingTransition(0,0);
                        return true;

                    case R.id.menu_profile:
                        startActivity(new Intent(getApplicationContext(),ProfileServiceProvider.class));
                        overridePendingTransition(0,0);
                        return true;

                }
                return false;
            }
        });



        loadingPB = findViewById(R.id.idProgressBar);
        rvServiceItem = findViewById(R.id.rvServiceItem);
        et_addServiceOffer = findViewById(R.id.et_addServiceOffer);
        et_addServicePrice = findViewById(R.id.et_addServicePrice);

        btn_serviceTypeName = findViewById(R.id.btn_serviceTypeName);


        // creating our new array list
        servicesArrayList = new ArrayList<>();
        rvServiceItem.setHasFixedSize(true);
        rvServiceItem.setLayoutManager(new LinearLayoutManager(this));

        // adding our array list to our recycler view adapter class.
        providerDisplayServiceOfferRVAdapter = new ProviderDisplayServiceOfferRVAdapter(servicesArrayList, this);

        providerDisplayServiceOfferRVAdapter.setClickListener(this);

        // setting adapter to our recycler view.
        rvServiceItem.setAdapter(providerDisplayServiceOfferRVAdapter);

    }


    @Override
    public void onItemClick(View view, int position) {
//        String test = updateServiceOfferRVAdapter.getItem(position).getServiceName();
//        Toast.makeText(getApplicationContext(), "Test: " + test, Toast.LENGTH_SHORT).show();
    }


    public void getUserServiceTypeFromDB(){
        DocumentReference docRef = FirebaseFirestore.getInstance().collection("users").document(currentUserID);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                if(task.isSuccessful()){
                    DocumentSnapshot document = task.getResult();

                    if(document.exists()){

                        try {
                            serviceType = document.getData().get("serviceType").toString();
                            Log.e("serviceTypeDB->", serviceType);

                            // Display to recycleview
                            getDataFromDbSendToRecyclerView(serviceType);

                            // Set UI title based on user service type
                            btn_serviceTypeName.setText(serviceType);

                        }catch (Exception e){
                            e.printStackTrace();
                        }

                    }else{
                        Toast.makeText(getApplicationContext(), "Such data not exist.",
                                Toast.LENGTH_SHORT).show();
                    }
                }

            }
        });
    }

    public void getDataFromDbSendToRecyclerView(String serviceType){

        // display to recycleview
        db.collection("serviceOffer")
                .whereEqualTo("serviceType", serviceType)
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

                            providerDisplayServiceOfferRVAdapter.notifyDataSetChanged();
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


/*
    @Override
    public void onBackPressed()
    {
        moveTaskToBack(true);
    }*/


}