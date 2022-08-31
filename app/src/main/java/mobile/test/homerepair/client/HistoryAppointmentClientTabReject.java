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
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import mobile.test.homerepair.R;
import mobile.test.homerepair.model.Appointment;

public class HistoryAppointmentClientTabReject extends Fragment implements HistoryAppointmentClientRVAdapter.ItemClickListener {

    private RecyclerView rvHistoryAppointment;
    private ArrayList<Appointment> appointmentArrayList;
    private HistoryAppointmentClientRVAdapter historyAppointmentClientRVAdapter;

    ProgressBar loadingPB;

    FirebaseFirestore db = FirebaseFirestore.getInstance();

    String currentUserID;
    String TAG = "TAG";

    TextView tvEmptyDb;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
/*
        try {
            Intent intent = getIntent();
            clientID = intent.getStringExtra("userID");
            Log.e("testGetUserID", clientID);
        }catch (Exception e){
            e.printStackTrace();
        }
        */


        currentUserID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        Log.e("testGetCurrentUserID", currentUserID);

        View view = inflater.inflate(R.layout.fragment_history_appointment_client_complete, container, false);

        tvEmptyDb = view.findViewById(R.id.tvEmptyDb);

        rvHistoryAppointment = view.findViewById(R.id.rvHistoryAppointment);
        loadingPB = view.findViewById(R.id.idProgressBar);

        appointmentArrayList = new ArrayList<>();
        rvHistoryAppointment.setHasFixedSize(true);
        rvHistoryAppointment.setLayoutManager(new LinearLayoutManager(getContext()));

        historyAppointmentClientRVAdapter = new HistoryAppointmentClientRVAdapter(appointmentArrayList,getContext());
        historyAppointmentClientRVAdapter.setClickListener(this);

        rvHistoryAppointment.setAdapter(historyAppointmentClientRVAdapter);

        getAppointment();

        Log.e("Test","Test");
        Log.e("currentUserID->","currentUserID");

        return view;
    }


    public void getAppointment(){
        db.collection("appointment")
                .whereEqualTo("clientID",currentUserID)
                .whereEqualTo("appointmentStatus","reject")
//                .orderBy("date", Query.Direction.ASCENDING)

//                .orderBy("date").orderBy("time", Query.Direction.DESCENDING)
//                .whereIn("appointmentStatus", Arrays.asList("cancel","reject","complete"))
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

                            historyAppointmentClientRVAdapter.notifyDataSetChanged();
                        } else {
                            // if the snapshot is empty we are displaying a toast message.
                            loadingPB.setVisibility(View.GONE);
                            tvEmptyDb.setVisibility(View.VISIBLE);
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

                Toast.makeText(getContext(), "Fail to get the data.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onItemClick(View view, int position){

        String appointmentID = historyAppointmentClientRVAdapter.getItem(position).getAppointmentID();
        Intent intent = new Intent(getContext(), PendingAppointmentClient.class);
        intent.putExtra("userID",appointmentID);
        startActivity(intent);

        Toast.makeText(getContext(), "Test "+ appointmentID, Toast.LENGTH_SHORT).show();

    }

}