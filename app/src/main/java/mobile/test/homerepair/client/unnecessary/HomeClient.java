package mobile.test.homerepair.client.unnecessary;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

import mobile.test.homerepair.R;
import mobile.test.homerepair.client.AppointmentScheduleClient;
import mobile.test.homerepair.client.ProfileClient;
import mobile.test.homerepair.client.SearchServices;

public class HomeClient extends AppCompatActivity {

    Button btn_clientToSearchMenu,btn_clientToAppointmentMenu,btn_clientToHistoryRecordMenu,
            btn_clientToNotificationMenu, btn_clientToProfileMenu;

    String currentUserID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_client);

        currentUserID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        Log.e("testGetCurrentUserID", currentUserID);


        btn_clientToSearchMenu = findViewById(R.id.btn_clientToSearchMenu);
        btn_clientToAppointmentMenu = findViewById(R.id.btn_clientToAppointmentMenu);
        btn_clientToHistoryRecordMenu = findViewById(R.id.btn_clientToHistoryRecordMenu);
        btn_clientToNotificationMenu = findViewById(R.id.btn_clientToNotificationMenu);
        btn_clientToProfileMenu = findViewById(R.id.btn_clientToProfileMenu);

        btn_clientToSearchMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), SearchServices.class);
                startActivity(intent);
                Toast.makeText(getApplicationContext(), "Directing to Searching Service Menu", Toast.LENGTH_SHORT).show();
            }
        });

        btn_clientToAppointmentMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), AppointmentScheduleClient.class);

                String clientID = currentUserID;
                Log.e("testPassClientUserID",clientID);
                intent.putExtra("userID",clientID);
                startActivity(intent);


                Toast.makeText(getApplicationContext(), "nothing set yet", Toast.LENGTH_SHORT).show();
            }
        });

        btn_clientToHistoryRecordMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "nothing set yet", Toast.LENGTH_SHORT).show();
            }
        });

        btn_clientToNotificationMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "nothing set yet", Toast.LENGTH_SHORT).show();
            }
        });

        btn_clientToProfileMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ProfileClient.class);
                startActivity(intent);
                Toast.makeText(getApplicationContext(), "Directing to Profile Menu", Toast.LENGTH_SHORT).show();
            }
        });
    }
}