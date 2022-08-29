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

public class ListRegisteredUserServiceProviderAdmin extends AppCompatActivity implements ListRegisteredUserServiceProviderAdminRVAdapter.ItemClickListener{


    private RecyclerView rvUser;
    private ArrayList<Users> usersArrayList;
    private ListRegisteredUserServiceProviderAdminRVAdapter listRegisteredUserServiceProviderAdminRVAdapter;


    ProgressBar loadingPB;

    FirebaseFirestore db = FirebaseFirestore.getInstance();

    Button btn_BackToHome;

    EditText et_searchService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_registered_user_admin);

        // It will display registered user for  and service provider
        // Make it searchable like search services


        btn_BackToHome = findViewById(R.id.btn_BackToHome);

        loadingPB = findViewById(R.id.idProgressBar);
        rvUser = findViewById(R.id.rvUser);
        et_searchService = findViewById(R.id.et_searchService);

        usersArrayList = new ArrayList<>();
        rvUser.setHasFixedSize(true);
        rvUser.setLayoutManager(new LinearLayoutManager(this));

        listRegisteredUserServiceProviderAdminRVAdapter = new ListRegisteredUserServiceProviderAdminRVAdapter(usersArrayList,this);
        listRegisteredUserServiceProviderAdminRVAdapter.setClickListener(this);

        rvUser.setAdapter(listRegisteredUserServiceProviderAdminRVAdapter);


        db.collection("users")
                .whereEqualTo("userType","serviceProvider")
                .whereEqualTo("registrationStatus","accept")
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                        if (!queryDocumentSnapshots.isEmpty()) {

                            loadingPB.setVisibility(View.GONE);
                            List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();
                            for (DocumentSnapshot documentSnapshot : list) {

                                Users users = documentSnapshot.toObject(Users.class);
                                usersArrayList.add(users);
                            }

                            listRegisteredUserServiceProviderAdminRVAdapter.notifyDataSetChanged();
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
                    try {
                        if(documentSnapshot.getEmail().toLowerCase().contains(s.toString().toLowerCase())){
                            searchItems.add(documentSnapshot);
                        }
                        else if(documentSnapshot.getCompanyName().toLowerCase().contains(s.toString().toLowerCase())){
                            searchItems.add(documentSnapshot);
                        }
                        else if(documentSnapshot.getName().toLowerCase().contains(s.toString().toLowerCase())){
                            searchItems.add(documentSnapshot);
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                    }

                }
                listRegisteredUserServiceProviderAdminRVAdapter = new ListRegisteredUserServiceProviderAdminRVAdapter(searchItems, getApplicationContext());
                rvUser.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL,false));
                listRegisteredUserServiceProviderAdminRVAdapter.setClickListener(ListRegisteredUserServiceProviderAdmin.this);
                rvUser.setAdapter(listRegisteredUserServiceProviderAdminRVAdapter);

            }
        });



        btn_BackToHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), AdminManageUsers.class);
                startActivity(intent);
            }
        });


        // End Bracket
    }

    @Override
    public void onItemClick(View view, int position){

        String userID = listRegisteredUserServiceProviderAdminRVAdapter.getItem(position).getUserID();
        Intent intent = new Intent(getApplicationContext(), ListRegisteredUserDetailServiceProviderAdmin.class);
        intent.putExtra("userID",userID);
        startActivity(intent);

//        Toast.makeText(getApplicationContext(), "Test"+userID, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

// End Bracket
}