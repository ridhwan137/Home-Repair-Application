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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import net.steamcrafted.materialiconlib.MaterialIconView;

import java.util.ArrayList;
import java.util.List;

import mobile.test.homerepair.R;
import mobile.test.homerepair.model.Services;

public class RejectAppointmentDetail extends AppCompatActivity implements ServiceOfferRVAdapter.ItemClickListener{


    private RecyclerView rvServiceDetail;
    private ArrayList<Services> servicesArrayList;
    private ServiceOfferRVAdapter serviceOfferRVAdapter;

    ProgressBar loadingPB;

    FirebaseFirestore db = FirebaseFirestore.getInstance();

    EditText et_appointmentID,et_clientName,et_companyName,et_serviceType,et_appointmentDate,et_appointmentTime;

    MaterialIconView btn_clientDetail,btn_companyDetail;

    String TAG = "TAG";
    String appointmentID;
    String providerID,clientID;

    Button btn_back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reject_appointment_detail);


        Intent intent = getIntent();
        appointmentID = intent.getStringExtra("appointmentID");
        Log.e("appointmentID->",appointmentID);


        btn_back = findViewById(R.id.btn_back);

        et_appointmentID = findViewById(R.id.et_appointmentID);
        et_clientName = findViewById(R.id.et_clientName);
        et_companyName = findViewById(R.id.et_companyName);
        et_serviceType = findViewById(R.id.et_serviceType);
        et_appointmentDate = findViewById(R.id.et_appointmentDate);
        et_appointmentTime = findViewById(R.id.et_appointmentTime);

        btn_clientDetail = findViewById(R.id.btn_clientDetail);
        btn_companyDetail = findViewById(R.id.btn_companyDetail);

        getAppointmentInfoFromDB();


        loadingPB = findViewById(R.id.idProgressBar);
        rvServiceDetail = findViewById(R.id.rvServiceDetail);

        servicesArrayList = new ArrayList<>();
        rvServiceDetail.setHasFixedSize(true);
        rvServiceDetail.setLayoutManager(new LinearLayoutManager(this));

        serviceOfferRVAdapter = new ServiceOfferRVAdapter(servicesArrayList,this);
        serviceOfferRVAdapter.setClickListener(this);

        rvServiceDetail.setAdapter(serviceOfferRVAdapter);


        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), RejectAppointmentList.class);
                startActivity(intent);
            }
        });


        btn_clientDetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("2->clientID->",clientID);

                Intent intent = new Intent(getApplicationContext(), AppointmentClientDetail.class);
                intent.putExtra("clientID",clientID);
                startActivity(intent);
            }
        });

        btn_companyDetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("2->providerID->",providerID);

                Intent intent = new Intent(getApplicationContext(), AppointmentServiceProviderDetail.class);
                intent.putExtra("providerID",providerID);
                startActivity(intent);
            }
        });

        /////////
    }


    @Override
    public void onItemClick(View view, int position){

    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

    public void getAppointmentInfoFromDB(){
        db.collection("appointment").whereEqualTo("appointmentID",appointmentID)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d("QueryDocumentSnapshot->", document.getId() + " => " + document.getData());

                                try {

                                    et_appointmentID.setText(document.getData().get("appointmentID").toString());
                                    et_clientName.setText(document.getData().get("clientName").toString());
                                    et_companyName.setText(document.getData().get("companyName").toString());
                                    et_serviceType.setText(document.getData().get("companyServiceType").toString());
                                    et_appointmentDate.setText(document.getData().get("date").toString());
                                    et_appointmentTime.setText(document.getData().get("time").toString());

                                    providerID = document.getData().get("providerID").toString();
                                    Log.e("1->providerID->",providerID);
                                    displayServiceOffer(providerID);

                                    clientID = document.getData().get("clientID").toString();
                                    Log.e("1->clientID->",clientID);
//                                    clientDetail(clientID);


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

    public void displayServiceOffer(String providerID){
        db.collection("serviceOffer")
                .whereEqualTo("userID",providerID)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                        if (!queryDocumentSnapshots.isEmpty()) {

                            loadingPB.setVisibility(View.GONE);
                            List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();
                            for (DocumentSnapshot documentSnapshot : list) {
                                Log.d("DocumentSnapshot->", documentSnapshot.getId() + " => " + documentSnapshot.getData());

                                Services services = documentSnapshot.toObject(Services.class);
                                servicesArrayList.add(services);
                            }

                            serviceOfferRVAdapter.notifyDataSetChanged();
                        } else {
                            // if the snapshot is empty we are displaying a toast message.
                            loadingPB.setVisibility(View.GONE);
                            Log.e("displayServiceOffer->","empty");
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

                Toast.makeText(getApplicationContext(), "Fail to get the data.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void clientDetail(String clientID){
        Intent intent = new Intent(getApplicationContext(), AppointmentClientDetail.class);
        intent.putExtra("clientID",clientID);
        startActivity(intent);
    }

    //////////
}