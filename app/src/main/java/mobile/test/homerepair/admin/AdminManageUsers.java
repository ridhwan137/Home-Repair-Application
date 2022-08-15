package mobile.test.homerepair.admin;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import mobile.test.homerepair.R;

public class AdminManageUsers extends AppCompatActivity {

    Button btn_adminToUserProvider, btn_adminToUserClient,btn_back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_manage_users);

        btn_adminToUserProvider = findViewById(R.id.btn_adminToUserProvider);
        btn_adminToUserClient = findViewById(R.id.btn_adminToUserClient);
        btn_back = findViewById(R.id.btn_back);


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

        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), HomeAdmin.class);
                startActivity(intent);
//                Toast.makeText(getApplicationContext(), "Directing to Searching Service Menu", Toast.LENGTH_SHORT).show();
            }
        });

    }
}