package mobile.test.homerepair.client;

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

public class AppointmentScheduleClient extends AppCompatActivity implements AppointmentScheduleClientRVAdapter.ItemClickListener {

    private RecyclerView rvAppointmentSchedule;
    private ArrayList<Appointment> appointmentArrayList;
    private AppointmentScheduleClientRVAdapter appointmentScheduleClientRVAdapter;

    ProgressBar loadingPB;

    FirebaseFirestore db = FirebaseFirestore.getInstance();

    Button btn_BackToHome;

    String clientID,currentUserID;
    String TAG = "TAG";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appointment_schedule_client);


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
        bottomNavigationView.setSelectedItemId(R.id.menu_appointmentSchedule);

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
//                        startActivity(new Intent(getApplicationContext(), AppointmentScheduleClient.class));
//                        overridePendingTransition(0,0);
                        return true;

                    case R.id.menu_historyAppointmentSchedule:
                        startActivity(new Intent(getApplicationContext(), HistoryAppointmentClient.class));
                        overridePendingTransition(0,0);
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

        appointmentScheduleClientRVAdapter = new AppointmentScheduleClientRVAdapter(appointmentArrayList,this);
        appointmentScheduleClientRVAdapter.setClickListener(this);

        rvAppointmentSchedule.setAdapter(appointmentScheduleClientRVAdapter);


        getAppointmentScheduleFromDB();


/*


        db.collection("users").whereEqualTo("userID",currentUserID)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + " => " + document.getData());

                                Appointment c = document.toObject(Appointment.class);

                                appointmentArrayList.add(c);
                            }
                            appointmentScheduleClientRVAdapter.notifyDataSetChanged();
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }

                    }
                });

*/

    }



    @Override
    public void onItemClick(View view, int position){

        String appointmentID = appointmentScheduleClientRVAdapter.getItem(position).getAppointmentID();
        Intent intent = new Intent(getApplicationContext(), PendingAppointmentClient.class);
        intent.putExtra("userID",appointmentID);
        startActivity(intent);

        Toast.makeText(getApplicationContext(), "Test "+ appointmentID, Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }


    public void getAppointmentScheduleFromDB(){
        db.collection("appointment")
                .whereEqualTo("clientID",currentUserID)
//                .whereEqualTo("requestStatus","pending")
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
                            }

                            appointmentScheduleClientRVAdapter.notifyDataSetChanged();
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


}