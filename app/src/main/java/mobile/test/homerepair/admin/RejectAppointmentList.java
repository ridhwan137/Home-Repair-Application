package mobile.test.homerepair.admin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
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
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import mobile.test.homerepair.R;
import mobile.test.homerepair.client.unnecessary.HomeClient;
import mobile.test.homerepair.model.Appointment;

public class RejectAppointmentList extends AppCompatActivity implements AppointmentListRVAdapter.ItemClickListener {

    protected RecyclerView rvAppointment;
    protected ArrayList<Appointment> appointmentArrayList;
    protected AppointmentListRVAdapter appointmentListRVAdapter;


    ProgressBar loadingPB;

    FirebaseFirestore db = FirebaseFirestore.getInstance();

    Button btn_BackToHome;

    EditText et_searchService;

    String userID = null;
    String providerID, clientID;
    String userType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reject_appointment_list);

        btn_BackToHome = findViewById(R.id.btn_BackToHome);

        loadingPB = findViewById(R.id.idProgressBar);
        rvAppointment = findViewById(R.id.rvAppointment);
        et_searchService = findViewById(R.id.et_searchService);

        appointmentArrayList = new ArrayList<>();
        rvAppointment.setHasFixedSize(true);
        rvAppointment.setLayoutManager(new LinearLayoutManager(this));

        appointmentListRVAdapter = new AppointmentListRVAdapter(appointmentArrayList,this);
        appointmentListRVAdapter.setClickListener(this);

        rvAppointment.setAdapter(appointmentListRVAdapter);


        try {
            Intent intent = getIntent();
            userID = intent.getStringExtra("userID");
            Log.e("testGetUserID", userID);
        } catch (Exception e) {
            e.printStackTrace();
        }


        /* Note for future revision.

        Flow for code below.

        First it will define either the user ID is null or not.

        If userID is not null, it will retrieve data from Firebase to get User Type,
        and then display the  appointment based on  condition.

        If userID is null, it will retrieve data from Firebase and display all appointment based on condition.
         */

        if (userID != null) {

            db.collection("users")
                    .whereEqualTo("userID", userID)
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {

                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    Log.e("getUserInfoUsersDB->", document.getId() + " => " + document.getData());

                                    userType = document.getData().get("userType").toString();
                                }


                                if (userType != null) {

                                    if (userType.equals("serviceProvider")) {

                                        Log.e("serviceProvider->userType->", userType);
                                        getProviderIDFromAppointmentDB(userID);

                                    } else if (userType.equals("client")) {

                                        Log.e("client->userType->", userType);
                                        getClientIDFromAppointmentDB(userID);

                                    } else {
                                        ///
                                    }
                                } else {
                                    ///
                                }


                            } else {
                                Log.e("Error getting documents->", "Error getting documents: ", task.getException());
                            }
                        }
                    });

        } else {

            db.collection("appointment")
                    .whereEqualTo("appointmentStatus", "reject")
//                    .orderBy("date", Query.Direction.DESCENDING)
                    .get()
                    .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                            if (!queryDocumentSnapshots.isEmpty()) {

                                loadingPB.setVisibility(View.GONE);
                                List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();
                                for (DocumentSnapshot documentSnapshot : list) {
                                    Appointment appointment = documentSnapshot.toObject(Appointment.class);
                                    appointmentArrayList.add(appointment);
                                }

                                /*
                                 * This will Sort By Date first before it pass to RecyclerView Adapter
                                 */
                                Collections.sort(appointmentArrayList, new Comparator<Appointment>() {
                                    @Override
                                    public int compare(Appointment o1, Appointment o2) {

//                                    return o1.getDate().compareToIgnoreCase(o2.getDate()); // Sort in ascending
                                        return o2.getDate().compareToIgnoreCase(o1.getDate()); // Sort in descending

                                    }
                                });

                                appointmentListRVAdapter.notifyDataSetChanged();
                            } else {
                                // if the snapshot is empty we are displaying a toast message.
                                loadingPB.setVisibility(View.GONE);
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.e("testDate",e.getMessage());
                    Toast.makeText(getApplicationContext(), "Fail to get the data.", Toast.LENGTH_SHORT).show();
                }
            });
        }





        et_searchService.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                ArrayList<Appointment> appointments = new ArrayList<>();
                for(Appointment documentSnapshot : appointmentArrayList){
                    try {
                        if(documentSnapshot.getAppointmentID().toLowerCase().contains(s.toString().toLowerCase())){
                            appointments.add(documentSnapshot);
                        }
                        else if(documentSnapshot.getClientName().toLowerCase().contains(s.toString().toLowerCase())){
                            appointments.add(documentSnapshot);
                        }
                        else if(documentSnapshot.getCompanyName().toLowerCase().contains(s.toString().toLowerCase())){
                            appointments.add(documentSnapshot);
                        }
                        else if(documentSnapshot.getDate().toLowerCase().contains(s.toString().toLowerCase())){
                            appointments.add(documentSnapshot);
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                    }

                }
                appointmentListRVAdapter = new AppointmentListRVAdapter(appointments, getApplicationContext());
                rvAppointment.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL,false));
                appointmentListRVAdapter.setClickListener(RejectAppointmentList.this);
                rvAppointment.setAdapter(appointmentListRVAdapter);

            }
        });



        btn_BackToHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), AdminManageAppointment.class);
                startActivity(intent);
            }
        });


        // End Bracket
    }

    @Override
    public void onItemClick(View view, int position){

        String appointmentID = appointmentListRVAdapter.getItem(position).getAppointmentID();
        Intent intent = new Intent(getApplicationContext(), RejectAppointmentDetail.class);
        intent.putExtra("appointmentID",appointmentID);
        startActivity(intent);

//        Toast.makeText(getApplicationContext(), "Test"+userID, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }


    public void getProviderIDFromAppointmentDB(String userID) {
        db.collection("appointment")
                .whereEqualTo("providerID", userID)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {

                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.e("getUserInfoFromAppointmentDB->", document.getId() + " => " + document.getData());
                                providerID = document.getData().get("providerID").toString();
                            }

                            displayProviderPendingAppointmentFromDB(providerID);

                        } else {
                            Log.e("updateFieldOnOtherCollection->", "Error getting documents: ", task.getException());
                        }
                    }
                });

    }

    private void displayProviderPendingAppointmentFromDB(String providerID) {

        db.collection("appointment")
                .whereEqualTo("providerID", providerID)
                .whereEqualTo("appointmentStatus", "reject")
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                        if (!queryDocumentSnapshots.isEmpty()) {

                            loadingPB.setVisibility(View.GONE);
                            List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();
                            for (DocumentSnapshot documentSnapshot : list) {
                                Appointment appointment = documentSnapshot.toObject(Appointment.class);
                                appointmentArrayList.add(appointment);
                            }
                            appointmentListRVAdapter.notifyDataSetChanged();
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

    public void getClientIDFromAppointmentDB(String userID) {

        db.collection("appointment")
                .whereEqualTo("clientID", userID)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.e("getUserInfoFromAppointmentDB->", document.getId() + " => " + document.getData());

                                clientID = document.getData().get("clientID").toString();
                            }

                            displayClientPendingAppointmentFromDB(clientID);

                        } else {
                            Log.e("updateFieldOnOtherCollection->", "Error getting documents: ", task.getException());
                        }
                    }
                });
    }


    private void displayClientPendingAppointmentFromDB(String clientID) {

        db.collection("appointment")
                .whereEqualTo("clientID", clientID)
                .whereEqualTo("appointmentStatus", "reject")
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                        if (!queryDocumentSnapshots.isEmpty()) {

                            loadingPB.setVisibility(View.GONE);
                            List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();
                            for (DocumentSnapshot documentSnapshot : list) {
                                Appointment appointment = documentSnapshot.toObject(Appointment.class);
                                appointmentArrayList.add(appointment);
                            }
                            appointmentListRVAdapter.notifyDataSetChanged();
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

// End Bracket


}