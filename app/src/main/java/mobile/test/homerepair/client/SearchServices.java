package mobile.test.homerepair.client;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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
import mobile.test.homerepair.model.Users;

public class SearchServices extends AppCompatActivity implements SearchServicesRVAdapter.ItemClickListener {

    private RecyclerView rvFindService;
    private ArrayList<Users> usersArrayList;
    private SearchServicesRVAdapter searchServicesRVAdapter;


    ProgressBar loadingPB;

    private FirebaseAuth mAuth;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseUser user;

    String companyID;
    String companyName,companyServiceType;
    Button btn_BackToHome;

    EditText et_searchService;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_services);


        btn_BackToHome = findViewById(R.id.btn_BackToHome);

        loadingPB = findViewById(R.id.idProgressBar);
        rvFindService = findViewById(R.id.rvFindService);
        et_searchService = findViewById(R.id.et_searchService);


        //Iniatialize and Assign Variable
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);

        //Set Menu Selected
        bottomNavigationView.setSelectedItemId(R.id.menu_searchService);

        //Perform ItemSelectedListener
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()){
                    case R.id.menu_searchService:
//                        startActivity(new Intent(getApplicationContext(),AddServices.class));
//                        overridePendingTransition(0,0);
                        return true;

                    case R.id.menu_appointmentSchedule:
                        startActivity(new Intent(getApplicationContext(), AppointmentScheduleClientTabLayout.class));
                        overridePendingTransition(0,0);
                        return true;

                    case R.id.menu_historyAppointmentSchedule:
                        startActivity(new Intent(getApplicationContext(), HistoryAppointmentClientTabLayout.class));
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



        usersArrayList = new ArrayList<>();
        rvFindService.setHasFixedSize(true);
        rvFindService.setLayoutManager(new LinearLayoutManager(this));

        searchServicesRVAdapter = new SearchServicesRVAdapter(usersArrayList,this);
        searchServicesRVAdapter.setClickListener(this);

        rvFindService.setAdapter(searchServicesRVAdapter);


        db.collection("users")
                .whereEqualTo("userType","serviceProvider")
                .whereEqualTo("registrationStatus","accept")
//                .whereEqualTo("hasServiceOffer","yes")
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

                            searchServicesRVAdapter.notifyDataSetChanged();
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
                searchServicesRVAdapter = new SearchServicesRVAdapter(searchItems,SearchServices.this);
                rvFindService.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL,false));
                searchServicesRVAdapter.setClickListener(SearchServices.this);
                rvFindService.setAdapter(searchServicesRVAdapter);

            }
        });



/*        btn_BackToHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SearchServices.this, HomeClient.class);
                startActivity(intent);
            }
        });*/





    }




    @Override
    public void onItemClick(View view, int position){

/*        String userID = searchServicesRVAdapter.getItem(position).getUserID();
        Intent intent = new Intent(SearchServices.this,ServiceProviderDetail.class);
        intent.putExtra("userID",userID);
        startActivity(intent);*/

        String userID = searchServicesRVAdapter.getItem(position).getUserID();
        Intent intent = new Intent(SearchServices.this,RequestAppointment.class);
        intent.putExtra("userID",userID);
        startActivity(intent);

//        Toast.makeText(getApplicationContext(), "Test"+userID, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

    @Override
    public void onBackPressed()
    {
        moveTaskToBack(true);
    }
}