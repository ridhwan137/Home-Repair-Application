package mobile.test.homerepair.main;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import net.steamcrafted.materialiconlib.MaterialIconView;

import mobile.test.homerepair.R;

public class ForgotPassword extends AppCompatActivity {

    Button btnEmailResend;
    EditText emailResend;
    FirebaseFirestore db;
    FirebaseAuth mAuth;
    MaterialIconView mvBackBtn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        btnEmailResend = findViewById(R.id.btnEmailResend);
        emailResend = findViewById(R.id.emailResend);
        mvBackBtn = findViewById(R.id.mvBackBtn);

        db = FirebaseFirestore.getInstance();

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        btnEmailResend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkExistingUser();
            }
        });

        mvBackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), Login.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });

        ////////
    }

    public void checkExistingUser(){

        String email = emailResend.getText().toString().trim();
        String getUserEmailInput = emailResend.getText().toString();


        if(!email.isEmpty()) {

            if(email.contains("@") && email.contains(".com")){

                Log.e("TAG", "Valid Email.");

                db.collection("users")
                        .whereEqualTo("email", getUserEmailInput)
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                        Log.e("TAG", document.getId() + " => " + document.getData());

                                        String user = document.getString("email");

                                        if(user.equals(getUserEmailInput)){
                                            Log.e("TAG", "User Exists");

                                            // if the user is exist in database, then password
                                            // reset will be send to the user's email
                                            sendPasswordReset();

                                        }
                                    }
                                }
                                if(task.getResult().size() == 0 ) {
                                    Log.d("TAG", "User not Exists");

                                    Toast.makeText(getApplicationContext(), "User does not exist!", Toast.LENGTH_LONG).show();

                                }
                            }
                        });

            }
            else{

                Toast.makeText(getApplicationContext(), "Invalid email format.", Toast.LENGTH_LONG).show();

            }

        }
        else{

            Toast.makeText(getApplicationContext(), "Please enter your email first before submit.", Toast.LENGTH_LONG).show();

        }


    }

    public void sendPasswordReset() {

        String emailAddress = emailResend.getText().toString().trim();

        mAuth.sendPasswordResetEmail(emailAddress)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.e("TAG", "Email sent.");
                            Toast.makeText(getApplicationContext(), "Email sent.", Toast.LENGTH_LONG).show();

                            Intent intent = new Intent(getApplicationContext(), Login.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);

                        }
                    }
                });
    }

////////////////////
}