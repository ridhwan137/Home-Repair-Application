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
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import net.steamcrafted.materialiconlib.MaterialIconView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import mobile.test.homerepair.R;
import mobile.test.homerepair.admin.unnecessary.AppointmentServiceProviderDetail;
import mobile.test.homerepair.model.Services;


public class PendingAppointmentDetail extends AppCompatActivity implements ServiceOfferRVAdapter.ItemClickListener {

    private RecyclerView rvServiceDetail;
    private ArrayList<Services> servicesArrayList;
    private ServiceOfferRVAdapter serviceOfferRVAdapter;

    ProgressBar loadingPB;

    FirebaseFirestore db = FirebaseFirestore.getInstance();

    EditText et_appointmentID,et_clientName,et_companyName,et_serviceType,et_appointmentDate,et_appointmentTime;

    MaterialIconView btn_clientDetail,btn_companyDetail;

    Button btn_acceptAppointment,btn_rejectAppointment,btn_cancelAppointment;

    Button btn_back;

    String TAG = "TAG";
    String appointmentID;
    String providerID,clientID;
    String serviceType;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pending_appointment_detail);

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

        btn_acceptAppointment = findViewById(R.id.btn_acceptAppointment);
        btn_rejectAppointment = findViewById(R.id.btn_rejectAppointment);
        btn_cancelAppointment = findViewById(R.id.btn_cancelAppointment);

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
                Intent intent = new Intent(getApplicationContext(), PendingAppointmentList.class);
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


        btn_acceptAppointment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("2->providerID->",providerID);

                acceptAppointmentUsingAppointmentStatusFromDB();

                Intent intent = new Intent(getApplicationContext(), HomeAdmin.class);
                intent.putExtra("providerID",providerID);
                startActivity(intent);
            }
        });


        btn_rejectAppointment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("2->providerID->",providerID);

                rejectAppointmentUsingAppointmentStatusFromDB();

                Intent intent = new Intent(getApplicationContext(), HomeAdmin.class);
                intent.putExtra("providerID",providerID);
                startActivity(intent);
            }
        });


        btn_cancelAppointment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("2->providerID->",providerID);

                cancelAppointmentUsingAppointmentStatusFromDB();

                Intent intent = new Intent(getApplicationContext(), HomeAdmin.class);
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
//                                    displayServiceOffer(providerID);


                                    serviceType = document.getData().get("companyServiceType").toString();
                                    Log.e("1->serviceType->",serviceType);
                                    displayServiceOffer(serviceType);

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

    public void displayServiceOffer(String serviceType){
        db.collection("serviceOffer")
                .whereEqualTo("serviceType",serviceType)
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


    public void rejectAppointmentUsingAppointmentStatusFromDB() {
        Map<String, Object> appointment = new HashMap<>();

        appointment.put("updateAppointmentStatus", "reject");

        DocumentReference docRef = db.collection("appointment").document(appointmentID);
        docRef.update("appointmentStatus", appointment.get("updateAppointmentStatus"))
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


    public void acceptAppointmentUsingAppointmentStatusFromDB() {
        // use db update requestStatus and apppointmentStatus to "cancel"

        Map<String, Object> appointment = new HashMap<>();

        appointment.put("updateAppointmentStatus", "in-progress");

        DocumentReference docRef = db.collection("appointment").document(appointmentID);
        docRef.update("appointmentStatus", appointment.get("updateAppointmentStatus"))
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


    public void cancelAppointmentUsingAppointmentStatusFromDB() {
        // use db update requestStatus and apppointmentStatus to "cancel"

        Map<String, Object> appointment = new HashMap<>();

        appointment.put("updateAppointmentStatus", "cancel");

        DocumentReference docRef = db.collection("appointment").document(appointmentID);
        docRef.update("appointmentStatus", appointment.get("updateAppointmentStatus"))
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


    public void clientDetail(String clientID){
        Intent intent = new Intent(getApplicationContext(), AppointmentClientDetail.class);
        intent.putExtra("clientID",clientID);
        startActivity(intent);
    }

    //////////
}