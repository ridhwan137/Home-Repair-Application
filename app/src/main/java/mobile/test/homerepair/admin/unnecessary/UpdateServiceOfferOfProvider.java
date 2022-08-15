package mobile.test.homerepair.admin.unnecessary;

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
import com.google.firebase.firestore.SetOptions;

import net.steamcrafted.materialiconlib.MaterialIconView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import mobile.test.homerepair.R;
import mobile.test.homerepair.admin.ListRegisteredUserDetailServiceProviderAdmin;
import mobile.test.homerepair.model.Services;

public class UpdateServiceOfferOfProvider extends AppCompatActivity implements UpdateServiceOfferRVAdapter.ItemClickListener{

    private RecyclerView rvServiceItem;
    private ArrayList<Services> servicesArrayList;
    private UpdateServiceOfferRVAdapter updateServiceOfferRVAdapter;

    ProgressBar loadingPB;

    FirebaseFirestore db = FirebaseFirestore.getInstance();

    String serviceID;
    String serviceName,servicePrice;

    String providerID;

    MaterialIconView btn_addItem;
    EditText et_addServiceOffer,et_addServicePrice;

    Button btn_BackToHome;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_service_offer_of_provider);


        try {
            Intent intent = getIntent();
            providerID = intent.getStringExtra("userID");
            Log.e("testGetUserID", providerID);
        }catch (Exception e){
            e.printStackTrace();
        }



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
        updateServiceOfferRVAdapter = new UpdateServiceOfferRVAdapter(servicesArrayList, this);

        updateServiceOfferRVAdapter.setClickListener(this);

        // setting adapter to our recycler view.
        rvServiceItem.setAdapter(updateServiceOfferRVAdapter);


        // display to recycleview
        db.collection("serviceOffer")
                .whereEqualTo("userID",providerID)
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

                            updateServiceOfferRVAdapter.notifyDataSetChanged();
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
                Intent intent = new Intent(getApplicationContext(), ListRegisteredUserDetailServiceProviderAdmin.class);
                intent.putExtra("userID",providerID);
                startActivity(intent);
            }
        });

    }

    @Override
    public void onItemClick(View view, int position) {
//        String test = updateServiceOfferRVAdapter.getItem(position).getServiceName();
//        Toast.makeText(getApplicationContext(), "Test: " + test, Toast.LENGTH_SHORT).show();
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
        serviceOffer.put("userID",providerID);
        serviceOffer.put("serviceName",serviceName);
        serviceOffer.put("servicePrice",servicePrice);
        serviceOffer.put("serviceID",serviceID);

        db.collection("serviceOffer").document(serviceID)
                .set(serviceOffer)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
/*                        Intent intent = new Intent(getApplicationContext(), UpdateServiceOfferOfProvider.class);
                        intent.putExtra("userID",providerID);
                        startActivity(intent);
                        UpdateServiceOfferOfProvider.this.finish();
                        */

                        Toast.makeText(UpdateServiceOfferOfProvider.this, "Services added successfully.", Toast.LENGTH_SHORT).show();
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

        db.collection("users").document(providerID)
                .set(data, SetOptions.merge())
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Intent intent = new Intent(getApplicationContext(), UpdateServiceOfferOfProvider.class);
                        intent.putExtra("userID",providerID);
                        startActivity(intent);
                        UpdateServiceOfferOfProvider.this.finish();

                        Toast.makeText(UpdateServiceOfferOfProvider.this, "Services added successfully.", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("addServiceOffer->", "Error writing document");
                    }
                });
    }



    @Override
    public void onBackPressed()
    {
        moveTaskToBack(true);
    }

}