package mobile.test.homerepair.admin;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;

import mobile.test.homerepair.R;

public class HomeAdmin extends AppCompatActivity {


    Button btn_adminToApprovalMenu,btn_adminToProfileMenu;

    Button btn_adminToManageService, btn_adminToManageUser,btn_adminToManageAppointment;

    String currentUserID,userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_admin);

        // get Admin ID
        currentUserID = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
        Log.e("testGetCurrentUserID", currentUserID);


        // get Client/Customer/User ID
        try {
            Intent intent = getIntent();
            userID = intent.getStringExtra("userID");
            Log.e("testUserID",userID);

        }catch (Exception e){
            e.printStackTrace();
        }

        btn_adminToProfileMenu = findViewById(R.id.btn_adminToProfileMenu);
        btn_adminToApprovalMenu = findViewById(R.id.btn_adminToApprovalMenu);
        btn_adminToManageService = findViewById(R.id.btn_adminToManageService);
        btn_adminToManageUser = findViewById(R.id.btn_adminToManageUser);
        btn_adminToManageAppointment = findViewById(R.id.btn_adminToManageAppointment);



        btn_adminToProfileMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ProfileAdmin.class);
                startActivity(intent);
//                Toast.makeText(getApplicationContext(), "Directing to Searching Service Menu", Toast.LENGTH_SHORT).show();
            }
        });

        btn_adminToApprovalMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ApproveRegistrationAdmin.class);
                startActivity(intent);
//                Toast.makeText(getApplicationContext(), "Directing to Searching Service Menu", Toast.LENGTH_SHORT).show();
            }
        });


        btn_adminToManageService.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), AdminAddService.class);
                startActivity(intent);
//                Toast.makeText(getApplicationContext(), "Directing to Searching Service Menu", Toast.LENGTH_SHORT).show();
            }
        });


        btn_adminToManageUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), AdminManageUsers.class);
                startActivity(intent);
//                Toast.makeText(getApplicationContext(), "Directing to Searching Service Menu", Toast.LENGTH_SHORT).show();
            }
        });


        btn_adminToManageAppointment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), AdminManageAppointment.class);
                startActivity(intent);
//                Toast.makeText(getApplicationContext(), "Directing to Searching Service Menu", Toast.LENGTH_SHORT).show();
            }
        });


    }

}