package mobile.test.homerepair.testDemo;

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
import mobile.test.homerepair.admin.ListRegisteredUserDetailServiceProviderAdmin;
import mobile.test.homerepair.model.Services;

public class TestProviderDisplayServiceOffer extends AppCompatActivity implements TestProviderDisplayServiceOfferRVAdapter.ItemClickListener{

    private RecyclerView rvServiceItem;
    private ArrayList<Services> servicesArrayList;
    private TestProviderDisplayServiceOfferRVAdapter testProviderDisplayServiceOfferRVAdapter;

    ProgressBar loadingPB;

    FirebaseFirestore db = FirebaseFirestore.getInstance();

    String serviceTypeID,serviceType;

    EditText et_addServiceOffer,et_addServicePrice;

    Button btn_BackToHome,btn_serviceTypeName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_provider_display_service_offer);


/*        try {
            Intent intent = getIntent();
            serviceTypeID = intent.getStringExtra("serviceTypeID");
            serviceType = intent.getStringExtra("serviceType");
            Log.e("serviceTypeID->", serviceTypeID);
            Log.e("serviceType->", serviceType);
        }catch (Exception e){
            e.printStackTrace();
        }*/

        serviceType = "Plumber";
        Log.e("serviceType->", serviceType);


        btn_BackToHome = findViewById(R.id.btn_BackToHome);

        loadingPB = findViewById(R.id.idProgressBar);
        rvServiceItem = findViewById(R.id.rvServiceItem);
        et_addServiceOffer = findViewById(R.id.et_addServiceOffer);
        et_addServicePrice = findViewById(R.id.et_addServicePrice);

        btn_serviceTypeName = findViewById(R.id.btn_serviceTypeName);

        btn_serviceTypeName.setText(serviceType);

        // creating our new array list
        servicesArrayList = new ArrayList<>();
        rvServiceItem.setHasFixedSize(true);
        rvServiceItem.setLayoutManager(new LinearLayoutManager(this));

        // adding our array list to our recycler view adapter class.
        testProviderDisplayServiceOfferRVAdapter = new TestProviderDisplayServiceOfferRVAdapter(servicesArrayList, this);

        testProviderDisplayServiceOfferRVAdapter.setClickListener(this);

        // setting adapter to our recycler view.
        rvServiceItem.setAdapter(testProviderDisplayServiceOfferRVAdapter);


        // display to recycleview
        db.collection("serviceOffer")
//                .whereEqualTo("serviceTypeID", serviceTypeID)
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

                            testProviderDisplayServiceOfferRVAdapter.notifyDataSetChanged();
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
                intent.putExtra("serviceTypeID", serviceTypeID);
                startActivity(intent);
            }
        });

    }

    @Override
    public void onItemClick(View view, int position) {
//        String test = updateServiceOfferRVAdapter.getItem(position).getServiceName();
//        Toast.makeText(getApplicationContext(), "Test: " + test, Toast.LENGTH_SHORT).show();
    }



/*
    @Override
    public void onBackPressed()
    {
        moveTaskToBack(true);
    }*/

}