package mobile.test.homerepair.admin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
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

import java.util.ArrayList;
import java.util.List;

import mobile.test.homerepair.R;
import mobile.test.homerepair.client.unnecessary.HomeClient;
import mobile.test.homerepair.model.Appointment;
import mobile.test.homerepair.model.Users;

public class PendingAppointmentList extends AppCompatActivity implements PendingAppointmentListRVAdapter.ItemClickListener {

    private RecyclerView rvPendingAppointment;
    private ArrayList<Appointment> appointmentArrayList;
    private PendingAppointmentListRVAdapter pendingAppointmentListRVAdapter;


    ProgressBar loadingPB;

    FirebaseFirestore db = FirebaseFirestore.getInstance();

    Button btn_BackToHome;

    EditText et_searchService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pending_appointment_list);

        btn_BackToHome = findViewById(R.id.btn_BackToHome);

        loadingPB = findViewById(R.id.idProgressBar);
        rvPendingAppointment = findViewById(R.id.rvPendingAppointment);
        et_searchService = findViewById(R.id.et_searchService);

        appointmentArrayList = new ArrayList<>();
        rvPendingAppointment.setHasFixedSize(true);
        rvPendingAppointment.setLayoutManager(new LinearLayoutManager(this));

        pendingAppointmentListRVAdapter = new PendingAppointmentListRVAdapter(appointmentArrayList,this);
        pendingAppointmentListRVAdapter.setClickListener(this);

        rvPendingAppointment.setAdapter(pendingAppointmentListRVAdapter);


        db.collection("appointment")
                .whereEqualTo("appointmentStatus","pending")
//                .whereEqualTo("registrationStatus","accept")
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

                            pendingAppointmentListRVAdapter.notifyDataSetChanged();
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
                pendingAppointmentListRVAdapter = new PendingAppointmentListRVAdapter(appointments, getApplicationContext());
                rvPendingAppointment.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL,false));
                pendingAppointmentListRVAdapter.setClickListener(PendingAppointmentList.this);
                rvPendingAppointment.setAdapter(pendingAppointmentListRVAdapter);

            }
        });



        btn_BackToHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), HomeClient.class);
                startActivity(intent);
            }
        });


        // End Bracket
    }

    @Override
    public void onItemClick(View view, int position){

        String appointmentID = pendingAppointmentListRVAdapter.getItem(position).getAppointmentID();
        Intent intent = new Intent(getApplicationContext(), ListRegisteredUserDetailClientAdmin.class);
        intent.putExtra("appointmentID->",appointmentID);
        startActivity(intent);

//        Toast.makeText(getApplicationContext(), "Test"+userID, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

// End Bracket
}