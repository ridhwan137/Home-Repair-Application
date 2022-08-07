package mobile.test.homerepair.admin;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;

import mobile.test.homerepair.R;

public class HomeAdmin extends AppCompatActivity {


    Button btn_adminToApprovalMenu,btn_adminToProfileMenu, btn_adminToUserProvider,
            btn_adminToUserClient,btn_adminToPendingAppointment,btn_adminToRejectAppointment,
            btn_adminToCancelAppointment,btn_adminToInProgressAppointment,btn_adminToCompleteAppointment;

    String currentUserID,userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_admin);

        // get Admin ID
        currentUserID = FirebaseAuth.getInstance().getCurrentUser().getUid();
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
        btn_adminToUserProvider = findViewById(R.id.btn_adminToUserProvider);
        btn_adminToUserClient = findViewById(R.id.btn_adminToUserClient);
        btn_adminToPendingAppointment = findViewById(R.id.btn_adminToPendingAppointment);
        btn_adminToRejectAppointment = findViewById(R.id.btn_adminToRejectAppointment);
        btn_adminToCancelAppointment = findViewById(R.id.btn_adminToCancelAppointment);
        btn_adminToInProgressAppointment = findViewById(R.id.btn_adminToInProgressAppointment);
        btn_adminToCompleteAppointment = findViewById(R.id.btn_adminToCompleteAppointment);


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

        btn_adminToUserProvider.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ListRegisteredUserServiceProviderAdmin.class);
                startActivity(intent);
//                Toast.makeText(getApplicationContext(), "Directing to Searching Service Menu", Toast.LENGTH_SHORT).show();
            }
        });


        btn_adminToUserClient.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ListRegisteredUserClientAdmin.class);
                startActivity(intent);
//                Toast.makeText(getApplicationContext(), "Directing to Searching Service Menu", Toast.LENGTH_SHORT).show();
            }
        });


        btn_adminToPendingAppointment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), PendingAppointmentList.class);
                startActivity(intent);
//                Toast.makeText(getApplicationContext(), "Directing to Searching Service Menu", Toast.LENGTH_SHORT).show();
            }
        });

        btn_adminToRejectAppointment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), RejectAppointmentList.class);
                startActivity(intent);
//                Toast.makeText(getApplicationContext(), "Directing to Searching Service Menu", Toast.LENGTH_SHORT).show();
            }
        });

        btn_adminToCancelAppointment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), CancelAppointmentList.class);
                startActivity(intent);
//                Toast.makeText(getApplicationContext(), "Directing to Searching Service Menu", Toast.LENGTH_SHORT).show();
            }
        });


        btn_adminToInProgressAppointment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), InProgressAppointmentList.class);
                startActivity(intent);
//                Toast.makeText(getApplicationContext(), "Directing to Searching Service Menu", Toast.LENGTH_SHORT).show();
            }
        });


        btn_adminToCompleteAppointment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), CompleteAppointmentList.class);
                startActivity(intent);
//                Toast.makeText(getApplicationContext(), "Directing to Searching Service Menu", Toast.LENGTH_SHORT).show();
            }
        });


    }

}