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
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import mobile.test.homerepair.R;
import mobile.test.homerepair.model.Appointment;
import mobile.test.homerepair.model.Users;

public class AppointmentScheduleServiceProvider extends AppCompatActivity implements AppointmentScheduleServiceProviderRVAdapter.ItemClickListener {

    private RecyclerView rvAppointmentSchedule;
    private ArrayList<Appointment> appointmentArrayList;
//    private ArrayList<Users> usersArrayList;
    private AppointmentScheduleServiceProviderRVAdapter appointmentScheduleServiceProviderRVAdapter;

    ProgressBar loadingPB;

    private FirebaseAuth mAuth;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseUser user;

    Button btn_BackToHome;

    String providerID,currentUserID,clientID;
    String TAG = "TAG";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appointment_schedule_service_provider);

        try {
            Intent intent = getIntent();
            providerID = intent.getStringExtra("userID");
            Log.e("testGetUserID", providerID);
        }catch (Exception e){
            e.printStackTrace();
        }

        currentUserID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        Log.e("testGetCurrentUserID", currentUserID);


        //Iniatialize and Assign Variable
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);

        //Set Menu Selected
        bottomNavigationView.setSelectedItemId(R.id.menu_appointmentSchedule);

        //Perform ItemSelectedListener
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()){
                    case R.id.menu_addService:
                        startActivity(new Intent(getApplicationContext(),AddServices.class));
                        overridePendingTransition(0,0);
                        return true;

                    case R.id.menu_appointmentSchedule:
//                        startActivity(new Intent(getApplicationContext(),AppointmentScheduleServiceProvider.class));
//                        overridePendingTransition(0,0);
                        return true;

                    case R.id.menu_historyAppointmentSchedule:
                        startActivity(new Intent(getApplicationContext(),HistoryAppointmentServiceProvider.class));
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


        btn_BackToHome = findViewById(R.id.btn_BackToHome);
        loadingPB = findViewById(R.id.idProgressBar);
        rvAppointmentSchedule = findViewById(R.id.rvAppointmentSchedule);


        appointmentArrayList = new ArrayList<>();
//        usersArrayList = new ArrayList<>();

        rvAppointmentSchedule.setHasFixedSize(true);
        rvAppointmentSchedule.setLayoutManager(new LinearLayoutManager(this));


        appointmentScheduleServiceProviderRVAdapter =
                new AppointmentScheduleServiceProviderRVAdapter(appointmentArrayList,this);

/*
        appointmentScheduleServiceProviderRVAdapter =
                new AppointmentScheduleServiceProviderRVAdapter(appointmentArrayList,usersArrayList,this);
*/

        appointmentScheduleServiceProviderRVAdapter.setClickListener(this);
        rvAppointmentSchedule.setAdapter(appointmentScheduleServiceProviderRVAdapter);


//        getClientIDFromDBAppointment();

        getAppointment();


    }


    @Override
    public void onItemClick(View view, int position){

        String appointmentID = appointmentScheduleServiceProviderRVAdapter.getItem(position).getAppointmentID();
        Intent intent = new Intent(getApplicationContext(), PendingAppointmentServiceProvider.class);
        intent.putExtra("testPassAppointmentID",appointmentID);
        startActivity(intent);

        Toast.makeText(getApplicationContext(), "Test "+ appointmentID, Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }



    public void getAppointment(){

        // <--- 1st DB
        //Display in recycleview for db collection Appointment
        db.collection("appointment")
                .whereEqualTo("providerID",currentUserID)
//                .whereEqualTo("appointmentStatus","complete")
                .whereIn("appointmentStatus", Arrays.asList("pending","in-progress"))
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

                                Log.e("dataFromArray", "Db 1");
                                Log.e("dataFromArray", String.valueOf(appointmentArrayList));


                                clientID = appointment.getClientID();
                                Log.e("getClientIDFromFunction111",clientID);


                            }


                            appointmentScheduleServiceProviderRVAdapter.notifyDataSetChanged();
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

        Log.e("dataFromDB", String.valueOf(db));
        // ---> 1st DB


/*
        // <--- 2nd DB

        // #Note here when change current UserID to clientID it error but when using providerID it can
        //Display in recycleview for db collection Users


        db.collection("users")
                .whereEqualTo("userID",clientID)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                        if (!queryDocumentSnapshots.isEmpty()) {

                            loadingPB.setVisibility(View.GONE);

                            List<DocumentSnapshot> documentSnapshotList = queryDocumentSnapshots.getDocuments();
                            for (DocumentSnapshot documentSnapshot : documentSnapshotList) {

                                Users user = documentSnapshot.toObject(Users.class);
                                usersArrayList.add(user);

                                Log.e("dataFromArray", "Db 2");
                                Log.e("dataFromArray", String.valueOf(usersArrayList));
                            }


                            appointmentScheduleServiceProviderRVAdapter.notifyDataSetChanged();
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

        // ---> 2nd DB
        /// End of 2nd DB bracket
*/

    }


    public void getClientIDFromDBAppointment(){
        db.collection("appointment").whereEqualTo("providerID", currentUserID)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + " => " + document.getData());

                                clientID = document.getData().get("clientID").toString();

                                Log.e("getClientIDFromFunction",clientID);
                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }

                    }
                });

    }




}