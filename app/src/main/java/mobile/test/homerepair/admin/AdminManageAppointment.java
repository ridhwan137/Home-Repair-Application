package mobile.test.homerepair.admin;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import mobile.test.homerepair.R;

public class AdminManageAppointment extends AppCompatActivity {

    Button btn_adminToPendingAppointment,btn_adminToRejectAppointment,
            btn_adminToCancelAppointment,btn_adminToInProgressAppointment,btn_adminToCompleteAppointment,
            btn_back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_manage_appointment);

        btn_adminToPendingAppointment = findViewById(R.id.btn_adminToPendingAppointment);
        btn_adminToRejectAppointment = findViewById(R.id.btn_adminToRejectAppointment);
        btn_adminToCancelAppointment = findViewById(R.id.btn_adminToCancelAppointment);
        btn_adminToInProgressAppointment = findViewById(R.id.btn_adminToInProgressAppointment);
        btn_adminToCompleteAppointment = findViewById(R.id.btn_adminToCompleteAppointment);

        btn_back = findViewById(R.id.btn_back);


        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), HomeAdmin.class);
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