package mobile.test.homerepair.provider;

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
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import mobile.test.homerepair.R;
import mobile.test.homerepair.model.Appointment;


public class HistoryAppointmentServiceProviderTabReject extends Fragment implements HistoryAppointmentServiceProviderRVAdapter.ItemClickListener {

    private RecyclerView rvHistoryAppointment;
    private ArrayList<Appointment> appointmentArrayList;
    private HistoryAppointmentServiceProviderRVAdapter historyAppointmentServiceProviderRVAdapter;

    FirebaseFirestore db = FirebaseFirestore.getInstance();

    String currentUserID;
    String TAG = "TAG";

    TextView tvEmptyDb;

    ProgressBar loadingPB;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_history_appointment_service_provider_tab_reject, container, false);

        currentUserID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        Log.e("testGetCurrentUserID", currentUserID);

        tvEmptyDb = view.findViewById(R.id.tvEmptyDb);

        rvHistoryAppointment = view.findViewById(R.id.rvHistoryAppointment);
        loadingPB = view.findViewById(R.id.idProgressBar);

        appointmentArrayList = new ArrayList<>();
        rvHistoryAppointment.setHasFixedSize(true);
        rvHistoryAppointment.setLayoutManager(new LinearLayoutManager(getContext()));

        historyAppointmentServiceProviderRVAdapter = new HistoryAppointmentServiceProviderRVAdapter(appointmentArrayList,getContext());
        historyAppointmentServiceProviderRVAdapter.setClickListener(this);

        rvHistoryAppointment.setAdapter(historyAppointmentServiceProviderRVAdapter);

        getAppointment();

        Log.e("Test","Test");
        Log.e("currentUserID->",currentUserID);

        return view;
    }

    public void getAppointment(){
        db.collection("appointment")
                .whereEqualTo("providerID",currentUserID)
                .whereEqualTo("appointmentStatus","reject")
//                .orderBy("date", Query.Direction.ASCENDING)
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
//                            Collections.reverse(appointmentArrayList);

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

                            historyAppointmentServiceProviderRVAdapter.notifyDataSetChanged();
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

/*        String appointmentID = historyAppointmentServiceProviderRVAdapter.getItem(position).getAppointmentID();
        Intent intent = new Intent(getContext(), PendingAppointmentClient.class);
        intent.putExtra("userID",appointmentID);
        startActivity(intent);

        Toast.makeText(getContext(), "Test "+ appointmentID, Toast.LENGTH_SHORT).show();*/

    }


}