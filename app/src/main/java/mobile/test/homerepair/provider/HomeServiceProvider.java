package mobile.test.homerepair.provider;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

import mobile.test.homerepair.R;
import mobile.test.homerepair.provider.unnecessary.AddServices;

public class HomeServiceProvider extends AppCompatActivity {

    Button btn_providerToAddServiceMenu,btn_providerToAppointmentMenu,btn_providerToHistoryRecordMenu,
            btn_providerToNotificationMenu,btn_providerToProfileMenu;

    String currentUserID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_service_provider);

        currentUserID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        Log.e("testGetCurrentUserID", currentUserID);

        btn_providerToAddServiceMenu = findViewById(R.id.btn_providerToAddServiceMenu);
        btn_providerToAppointmentMenu = findViewById(R.id.btn_providerToAppointmentMenu);
        btn_providerToHistoryRecordMenu = findViewById(R.id.btn_providerToHistoryRecordMenu);
        btn_providerToNotificationMenu = findViewById(R.id.btn_providerToNotificationMenu);
        btn_providerToProfileMenu = findViewById(R.id.btn_providerToProfileMenu);


        btn_providerToAddServiceMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), AddServices.class);
                startActivity(intent);
                Toast.makeText(getApplicationContext(), "Directing to Add Service Menu", Toast.LENGTH_SHORT).show();
            }
        });

        btn_providerToAppointmentMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), AppointmentScheduleServiceProvider.class);

                String providerID = currentUserID;
                Log.e("testPassProviderUserID",providerID);
                intent.putExtra("userID",providerID);
                startActivity(intent);


                Toast.makeText(getApplicationContext(), "nothing set yet", Toast.LENGTH_SHORT).show();
            }
        });

        btn_providerToHistoryRecordMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Toast.makeText(getApplicationContext(), "nothing set yet", Toast.LENGTH_SHORT).show();
            }
        });

        btn_providerToNotificationMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "nothing set yet", Toast.LENGTH_SHORT).show();
            }
        });

        btn_providerToProfileMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ProfileServiceProvider.class);
                startActivity(intent);
                Toast.makeText(getApplicationContext(), "Directing to Profile Menu", Toast.LENGTH_SHORT).show();
            }
        });


    }
}