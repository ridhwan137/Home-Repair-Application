package mobile.test.homerepair.admin;

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
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import net.steamcrafted.materialiconlib.MaterialIconView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import mobile.test.homerepair.R;
import mobile.test.homerepair.model.Services;
import mobile.test.homerepair.testDemo.TestMainHomePage;

public class AdminAddService extends AppCompatActivity implements AdminAddServiceRVAdapter.ItemClickListener{

    private RecyclerView rvServiceItem;
    private ArrayList<Services> servicesArrayList;
    private AdminAddServiceRVAdapter adminAddServiceRVAdapter;

    ProgressBar loadingPB;

    FirebaseFirestore db = FirebaseFirestore.getInstance();

    String serviceTypeID;
    String serviceTypeName;

    MaterialIconView btn_addItem;
    EditText et_addServiceOffer;

    Button btn_BackToHome;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_admin_add_service);

        btn_BackToHome = findViewById(R.id.btn_BackToHome);

        loadingPB = findViewById(R.id.idProgressBar);
        rvServiceItem = findViewById(R.id.rvServiceItem);
        et_addServiceOffer = findViewById(R.id.et_addServiceOffer);

        btn_addItem = findViewById(R.id.btn_addItem);


        btn_addItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addServiceType();
            }
        });



        // creating our new array list
        servicesArrayList = new ArrayList<>();
        rvServiceItem.setHasFixedSize(true);
        rvServiceItem.setLayoutManager(new LinearLayoutManager(this));

        // adding our array list to our recycler view adapter class.
        adminAddServiceRVAdapter = new AdminAddServiceRVAdapter(servicesArrayList, this);

        adminAddServiceRVAdapter.setClickListener(this);

        // setting adapter to our recycler view.
        rvServiceItem.setAdapter(adminAddServiceRVAdapter);


        // display to recycleview
        db.collection("service")
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

                            adminAddServiceRVAdapter.notifyDataSetChanged();
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




        btn_BackToHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), TestMainHomePage.class);
//                intent.putExtra("userID",providerID);
                startActivity(intent);
            }
        });

    }

    @Override
    public void onItemClick(View view, int position) {
//        String test = updateServiceOfferRVAdapter.getItem(position).getServiceName();
//        Toast.makeText(getApplicationContext(), "Test: " + test, Toast.LENGTH_SHORT).show();
    }



    public void addServiceType(){


        // <--- Validation Input

        serviceTypeName = et_addServiceOffer.getText().toString();

        if (serviceTypeName.isEmpty()){
            et_addServiceOffer.setError("Field is empty");
            return;
        }

        if (serviceTypeName.isEmpty()){
            et_addServiceOffer.setError("Field is empty");
            return;
        }



        // ----> Validation Input


        //create random id
        Random rand = new Random();
        int randomID = rand.nextInt(99999999)+1;

        serviceTypeID = "serviceType" + randomID;

        Map<String, Object> serviceOffer = new HashMap<>();
//        serviceOffer.put("userID",providerID);
//        serviceOffer.put("servicePrice",servicePrice);
        serviceOffer.put("serviceType", serviceTypeName);
        serviceOffer.put("serviceTypeID", serviceTypeID);

        db.collection("service").document(serviceTypeID)
                .set(serviceOffer)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Intent intent = new Intent(getApplicationContext(), AdminAddService.class);
//                        intent.putExtra("userID",providerID);
                        startActivity(intent);
                        AdminAddService.this.finish();

                        Toast.makeText(getApplicationContext(), "Services added successfully.", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("addServiceOffer->", "Error writing document");
                    }
                });


    }



//    @Override
//    public void onBackPressed()
//    {
//        moveTaskToBack(true);
//    }

}