package mobile.test.homerepair.client;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import mobile.test.homerepair.R;
import mobile.test.homerepair.model.Appointment;


public class AppointmentScheduleClientTabPending extends Fragment implements AppointmentScheduleClientRVAdapter.ItemClickListener  {

    private RecyclerView rvAppointment;
    private ArrayList<Appointment> appointmentArrayList;
    private AppointmentScheduleClientRVAdapter appointmentScheduleClientRVAdapter;

    ProgressBar loadingPB;

    FirebaseFirestore db = FirebaseFirestore.getInstance();

    Button btn_BackToHome;

    String currentUserID;
    String TAG = "TAG";

    TextView tvEmptyDb;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_appointment_schedule_client_tab_pending, container, false);


        currentUserID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        Log.e("testGetCurrentUserID", currentUserID);



        btn_BackToHome = view.findViewById(R.id.btn_BackToHome);
        loadingPB = view.findViewById(R.id.idProgressBar);
        rvAppointment = view.findViewById(R.id.rvAppointment);

        tvEmptyDb = view.findViewById(R.id.tvEmptyDb);


        appointmentArrayList = new ArrayList<>();
        rvAppointment.setHasFixedSize(true);
        rvAppointment.setLayoutManager(new LinearLayoutManager(getContext()));

        appointmentScheduleClientRVAdapter = new AppointmentScheduleClientRVAdapter(appointmentArrayList,getContext());
        appointmentScheduleClientRVAdapter.setClickListener(this);

        rvAppointment.setAdapter(appointmentScheduleClientRVAdapter);


        getAppointmentScheduleFromDB();


        return view;
    }


    @Override
    public void onItemClick(View view, int position) {
        String appointmentID = appointmentScheduleClientRVAdapter.getItem(position).getAppointmentID();
        Intent intent = new Intent(getContext(), ClientAppointmentPending.class);
        intent.putExtra("appointmentID",appointmentID);
        startActivity(intent);

        Toast.makeText(getContext(), "Test "+ appointmentID, Toast.LENGTH_SHORT).show();
    }


    public void getAppointmentScheduleFromDB(){
        db.collection("appointment")
                .whereEqualTo("clientID",currentUserID)
                .whereEqualTo("appointmentStatus","pending")
//                .orderBy("date", Query.Direction.ASCENDING)

//                .whereIn("appointmentStatus", Arrays.asList("pending","in-progress"))
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

                                    return o1.getDate().compareToIgnoreCase(o2.getDate()); // Sort in ascending
//                                        return o2.getDate().compareToIgnoreCase(o1.getDate()); // Sort in descending

                                }
                            });

                            appointmentScheduleClientRVAdapter.notifyDataSetChanged();
                        } else {
                            // if the snapshot is empty we are displaying a toast message.
                            loadingPB.setVisibility(View.GONE);
                            tvEmptyDb.setVisibility(View.VISIBLE);
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e("testDate",e.getMessage());
                Toast.makeText(getContext(), "Fail to get the data.", Toast.LENGTH_SHORT).show();
            }
        });
    }


}