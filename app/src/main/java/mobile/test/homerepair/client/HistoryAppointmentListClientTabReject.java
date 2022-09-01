package mobile.test.homerepair.client;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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

public class HistoryAppointmentListClientTabReject extends Fragment implements HistoryAppointmentListClientRVAdapter.ItemClickListener {

    private RecyclerView rvListAppointment;
    private ArrayList<Appointment> appointmentArrayList;
    private HistoryAppointmentListClientRVAdapter historyAppointmentListClientRVAdapter;

    ProgressBar loadingPB;

    FirebaseFirestore db = FirebaseFirestore.getInstance();

    Button btn_BackToHome;

    EditText et_searchService;

    String userID = null;
    String providerID, clientID, currentUserID;
    String userType;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_appointment_list_client_tab, container, false);


        currentUserID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        Log.e("testGetCurrentUserID", currentUserID);


        loadingPB = view.findViewById(R.id.idProgressBar);
        rvListAppointment = view.findViewById(R.id.rvListAppointment);
        et_searchService = view.findViewById(R.id.et_searchService);

        appointmentArrayList = new ArrayList<>();
        rvListAppointment.setHasFixedSize(true);
        rvListAppointment.setLayoutManager(new LinearLayoutManager(getContext()));

        historyAppointmentListClientRVAdapter = new HistoryAppointmentListClientRVAdapter(appointmentArrayList, getContext());
        historyAppointmentListClientRVAdapter.setClickListener(this);

        rvListAppointment.setAdapter(historyAppointmentListClientRVAdapter);


        getAppointment();

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
                for (Appointment documentSnapshot : appointmentArrayList) {
                    try {
                        if (documentSnapshot.getAppointmentID().toLowerCase().contains(s.toString().toLowerCase())) {
                            appointments.add(documentSnapshot);
                        } else if (documentSnapshot.getCompanyName().toLowerCase().contains(s.toString().toLowerCase())) {
                            appointments.add(documentSnapshot);
                        } else if (documentSnapshot.getCompanyServiceType().toLowerCase().contains(s.toString().toLowerCase())) {
                            appointments.add(documentSnapshot);
                        } else if (documentSnapshot.getDate().toLowerCase().contains(s.toString().toLowerCase())) {
                            appointments.add(documentSnapshot);
                        } else if (documentSnapshot.getTime().toLowerCase().contains(s.toString().toLowerCase())) {
                            appointments.add(documentSnapshot);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
                historyAppointmentListClientRVAdapter = new HistoryAppointmentListClientRVAdapter(appointments, getContext());
                rvListAppointment.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
                historyAppointmentListClientRVAdapter.setClickListener(HistoryAppointmentListClientTabReject.this);
                rvListAppointment.setAdapter(historyAppointmentListClientRVAdapter);

            }
        });


        return view;
    }


    public void getAppointment() {
        db.collection("appointment")
                .whereEqualTo("clientID", currentUserID)
                .whereEqualTo("appointmentStatus", "reject")
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

                            historyAppointmentListClientRVAdapter.notifyDataSetChanged();
                        } else {
                            // if the snapshot is empty we are displaying a toast message.
                            loadingPB.setVisibility(View.GONE);
//                            tvEmptyDb.setVisibility(View.VISIBLE);
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
    public void onItemClick(View view, int position) {

        String appointmentID = historyAppointmentListClientRVAdapter.getItem(position).getAppointmentID();
        Intent intent = new Intent(getContext(), ClientAppointmentReject.class);
        intent.putExtra("appointmentID", appointmentID);
        startActivity(intent);

//        Toast.makeText(getContext(), "Test " + appointmentID, Toast.LENGTH_SHORT).show();

    }

}