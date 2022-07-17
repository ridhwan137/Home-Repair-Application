package mobile.test.homerepair.admin;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

import mobile.test.homerepair.R;
import mobile.test.homerepair.client.SearchServices;

public class HomeAdmin extends AppCompatActivity {


    Button btn_adminToApprovalMenu,btn_adminToProfileMenu,btn_adminToUserListMenu;

    String currentUserID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_admin);

        currentUserID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        Log.e("testGetCurrentUserID", currentUserID);


        btn_adminToProfileMenu = findViewById(R.id.btn_adminToProfileMenu);
        btn_adminToApprovalMenu = findViewById(R.id.btn_adminToApprovalMenu);
        btn_adminToUserListMenu = findViewById(R.id.btn_adminToUserListMenu);


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

        btn_adminToUserListMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ListRegisteredUserAdmin.class);
                startActivity(intent);
//                Toast.makeText(getApplicationContext(), "Directing to Searching Service Menu", Toast.LENGTH_SHORT).show();
            }
        });

    }

}