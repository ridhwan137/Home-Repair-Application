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
import mobile.test.homerepair.model.Users;

public class ApproveRegistrationAdmin extends AppCompatActivity implements ApproveRegistrationAdminRVAdapter.ItemClickListener {


    private RecyclerView rvCompany;
    private ArrayList<Users> usersArrayList;
    private ApproveRegistrationAdminRVAdapter approveRegistrationAdminRVAdapter;


    ProgressBar loadingPB;

    FirebaseFirestore db = FirebaseFirestore.getInstance();

    Button btn_BackToHome;

    EditText et_searchService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_approve_registration_admin);

        // Do listing of service provider filter by date it apply
        // Make it searchable like search services


        btn_BackToHome = findViewById(R.id.btn_BackToHome);

        loadingPB = findViewById(R.id.idProgressBar);
        rvCompany = findViewById(R.id.rvCompany);
        et_searchService = findViewById(R.id.et_searchService);

        usersArrayList = new ArrayList<>();
        rvCompany.setHasFixedSize(true);
        rvCompany.setLayoutManager(new LinearLayoutManager(this));

        approveRegistrationAdminRVAdapter = new ApproveRegistrationAdminRVAdapter(usersArrayList,this);
        approveRegistrationAdminRVAdapter.setClickListener(this);

        rvCompany.setAdapter(approveRegistrationAdminRVAdapter);


        db.collection("users")
//                .orderBy("dateRegistration", Query.Direction.DESCENDING)
                .whereEqualTo("userType","serviceProvider")
                .whereEqualTo("registrationStatus","pending")
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                        if (!queryDocumentSnapshots.isEmpty()) {

                            loadingPB.setVisibility(View.GONE);
                            List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();
                            for (DocumentSnapshot d : list) {

                                Users c = d.toObject(Users.class);

                                usersArrayList.add(c);
                            }

                            approveRegistrationAdminRVAdapter.notifyDataSetChanged();
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

                ArrayList<Users> searchItems = new ArrayList<>();
                for(Users documentSnapshot : usersArrayList){
                    if(documentSnapshot.getServiceType().toLowerCase().contains(s.toString().toLowerCase())){
                        searchItems.add(documentSnapshot);
                    }
                    else if(documentSnapshot.getCompanyName().toLowerCase().contains(s.toString().toLowerCase())){
                        searchItems.add(documentSnapshot);
                    }
                }
                approveRegistrationAdminRVAdapter = new ApproveRegistrationAdminRVAdapter(searchItems, getApplicationContext());
                rvCompany.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL,false));
                approveRegistrationAdminRVAdapter.setClickListener(ApproveRegistrationAdmin.this);
                rvCompany.setAdapter(approveRegistrationAdminRVAdapter);

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

        String userID = approveRegistrationAdminRVAdapter.getItem(position).getUserID();
        Intent intent = new Intent(getApplicationContext(), ApproveRegisteredUserServiceProviderDetailAdmin.class);
        intent.putExtra("userID",userID);
        startActivity(intent);

//        Toast.makeText(getApplicationContext(), "Test"+userID, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

    // End Bracket
}