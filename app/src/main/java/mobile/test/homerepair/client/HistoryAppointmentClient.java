package mobile.test.homerepair.client;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import mobile.test.homerepair.R;
import mobile.test.homerepair.model.Appointment;
import mobile.test.homerepair.provider.AddServices;

public class HistoryAppointmentClient extends AppCompatActivity implements HistoryAppointmentClientRVAdapter.ItemClickListener{

    private RecyclerView rvAppointmentSchedule;
    private ArrayList<Appointment> appointmentArrayList;
    private HistoryAppointmentClientRVAdapter historyAppointmentClientRVAdapter;

    ProgressBar loadingPB;

    private FirebaseAuth mAuth;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseUser user;

    Button btn_BackToHome;

    String clientID,currentUserID;
    String TAG = "TAG";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history_appointment_client);


        try {
            Intent intent = getIntent();
            clientID = intent.getStringExtra("userID");
            Log.e("testGetUserID", clientID);
        }catch (Exception e){
            e.printStackTrace();
        }


        currentUserID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        Log.e("testGetCurrentUserID", currentUserID);


        btn_BackToHome = findViewById(R.id.btn_BackToHome);
        loadingPB = findViewById(R.id.idProgressBar);
        rvAppointmentSchedule = findViewById(R.id.rvAppointmentSchedule);


        //Iniatialize and Assign Variable
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);

        //Set Menu Selected
        bottomNavigationView.setSelectedItemId(R.id.menu_historyAppointmentSchedule);

        //Perform ItemSelectedListener
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()){
                    case R.id.menu_searchService:
                        startActivity(new Intent(getApplicationContext(),SearchServices.class));
                        overridePendingTransition(0,0);
                        return true;

                    case R.id.menu_appointmentSchedule:
                        startActivity(new Intent(getApplicationContext(), AppointmentScheduleClient.class));
                        overridePendingTransition(0,0);
                        return true;

                    case R.id.menu_historyAppointmentSchedule:
//                        startActivity(new Intent(getApplicationContext(), HistoryAppointmentClient.class));
//                        overridePendingTransition(0,0);
                        return true;

                    case R.id.menu_profile:
                        startActivity(new Intent(getApplicationContext(), ProfileClient.class));
                        overridePendingTransition(0,0);
                        return true;

                }
                return false;
            }
        });


        appointmentArrayList = new ArrayList<>();
        rvAppointmentSchedule.setHasFixedSize(true);
        rvAppointmentSchedule.setLayoutManager(new LinearLayoutManager(this));

        historyAppointmentClientRVAdapter = new HistoryAppointmentClientRVAdapter(appointmentArrayList,this);
        historyAppointmentClientRVAdapter.setClickListener(this);

        rvAppointmentSchedule.setAdapter(historyAppointmentClientRVAdapter);


        getAppointment();

/*

        Spinner sp_Status = findViewById(R.id.sp_Status);
        String []appointmentStatus = {"Please Select Status","Complete","Cancel","Reject"};

        final List<String> appointmentStatusList = new ArrayList<>(Arrays.asList(appointmentStatus));
        final ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(this,R.layout.spinner_item,appointmentStatusList){


            @Override
            public boolean isEnabled(int position){
                if(position == 0)
                {
                    return false;
                }
                else
                {
                    return true;
                }
            }
            @Override
            public View getDropDownView(int position, View convertView, ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);

                TextView tv = (TextView) view;
                if(position == 0){
                    // Set the hint text color gray
                    tv.setTextColor(Color.GRAY);
                }
                else {
                    tv.setTextColor(Color.BLACK);
                }
                return view;
            }
        };


        spinnerArrayAdapter.setDropDownViewResource(R.layout.spinner_item);
        sp_Status.setAdapter(spinnerArrayAdapter);
        sp_Status.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedItemText = (String) parent.getItemAtPosition(position);

                if(position > 0){
                    // Notify the selected item text

                    if(selectedItemText.equals("Complete")){
                        Toast.makeText(getApplicationContext(), "complete status", Toast.LENGTH_SHORT).show();
                        getAppointmentAppointmentStatusComplete();

                    }else if(selectedItemText.equals("Cancel")){
                        Toast.makeText(getApplicationContext(), "cancel status", Toast.LENGTH_SHORT).show();
                        getAppointmentRequestStatusCancel();

                    }else if(selectedItemText.equals("Reject")){
                        Toast.makeText(getApplicationContext(), "reject status", Toast.LENGTH_SHORT).show();
                        getAppointmentRequestStatusReject();

                    }else{
                        //
                    }


                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

*/


//        getAppointment();

    }

    @Override
    public void onItemClick(View view, int position){

        String appointmentID = historyAppointmentClientRVAdapter.getItem(position).getAppointmentID();
        Intent intent = new Intent(getApplicationContext(), PendingAppointmentClient.class);
        intent.putExtra("userID",appointmentID);
        startActivity(intent);

        Toast.makeText(getApplicationContext(), "Test "+ appointmentID, Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

    public void getAppointment(){
        db.collection("appointment")
                .whereEqualTo("clientID",currentUserID)
                .whereIn("appointmentStatus", Arrays.asList("cancel","reject","complete"))
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

                            historyAppointmentClientRVAdapter.notifyDataSetChanged();
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


    public void getAppointmentAppointmentStatusComplete(){
        db.collection("appointment")
                .whereEqualTo("clientID",currentUserID)
                .whereEqualTo("appointmentStatus","complete")
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

                            historyAppointmentClientRVAdapter.notifyDataSetChanged();
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

    public void getAppointmentRequestStatusCancel(){
        db.collection("appointment")
                .whereEqualTo("clientID",currentUserID)
                .whereEqualTo("requestStatus","cancel")
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

                            historyAppointmentClientRVAdapter.notifyDataSetChanged();
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

    public void getAppointmentRequestStatusReject(){
        db.collection("appointment")
                .whereEqualTo("clientID",currentUserID)
                .whereEqualTo("requestStatus","reject")
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

                            historyAppointmentClientRVAdapter.notifyDataSetChanged();
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



    /// Ending Bracker
}