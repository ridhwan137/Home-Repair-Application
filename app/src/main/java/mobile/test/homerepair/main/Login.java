package mobile.test.homerepair.main;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import mobile.test.homerepair.admin.HomeAdmin;
import mobile.test.homerepair.client.ProfileClient;
import mobile.test.homerepair.R;
import mobile.test.homerepair.provider.ProfileServiceProvider;
import mobile.test.homerepair.provider.RegisterServiceProvider;
import mobile.test.homerepair.testDemo.TestMainHomePage;

public class Login extends AppCompatActivity {

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseAuth mAuth;


    TextView tvRegister, tvForgotPassword;
    Button btLogin, btn_toTestPage;
    EditText etEmail, etPassword;

    String email, password, userType;
    String registrationStatus;

    ImageView img_help;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();

        tvRegister = findViewById(R.id.tvRegister);
        tvForgotPassword = findViewById(R.id.tvForgotPassword);

        btLogin = findViewById(R.id.btLogin);

        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);

        btn_toTestPage = findViewById(R.id.btn_toTestPage);

        img_help = findViewById(R.id.img_help);


        img_help.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                db.collection("users")
                        .whereEqualTo("email", "home.repair.management@gmail.com")
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {

                                String adminName = null,
                                        adminEmail = null,
                                        adminPhone = null,
                                        adminAddress1 = null,
                                        adminAddress2 = null,
                                        adminAddressPostcode = null,
                                        adminAddressCity = null,
                                        adminAddressState = null;

                                if (task.isSuccessful()) {
                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                        Log.e("getAdminInfoFromUserDB->", document.getId() + " => " + document.getData());

                                        try {
                                            adminName = document.getData().get("name").toString();
                                            adminEmail = document.getData().get("email").toString();
                                            adminPhone = document.getData().get("phone").toString();
                                            adminAddress1 = document.getData().get("address1").toString();
                                            adminAddress2 = document.getData().get("address2").toString();
                                            adminAddressPostcode = document.getData().get("postcode").toString();
                                            adminAddressCity = document.getData().get("city").toString();
                                            adminAddressState = document.getData().get("state").toString();


                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }


                                    }

                                    AlertDialog.Builder dialog = new AlertDialog.Builder(Login.this);
                                    dialog.setCancelable(true);
                                    dialog.setTitle("Admin Helpline");
                                    dialog.setMessage(
                                            "Name: " +
                                            "\n"+adminName +
                                            "\n\nEmail: " +
                                            "\n"+adminEmail +
                                            "\n\nContact No: " +
                                            "\n"+adminPhone +
                                            "\n\nAddress: " +
                                            "\n"+adminAddress1 + ", " + adminAddress2 + ", " +
                                            adminAddressPostcode + " " + adminAddressCity + ", " + adminAddressState);


                                    final AlertDialog alert = dialog.create();
                                    alert.show();


                                } else {
                                    Log.e("updateFieldOnOtherCollection->", "Error getting documents: ", task.getException());
                                }
                            }
                        });


            }
        });


        tvForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ForgotPassword.class);
                startActivity(intent);
            }
        });


        tvRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toRegister();
            }
        });


        btLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userLogin();
            }
        });


        btn_toTestPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), TestMainHomePage.class);
                startActivity(intent);
            }
        });
    }

/*    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.

    }*/


    public void toRegister() {
        Intent intent = new Intent(getApplicationContext(), AccountType.class);
        startActivity(intent);
    }


    public void userLogin() {

        email = etEmail.getText().toString();
        password = etPassword.getText().toString();

        Log.e("email", email);
        Log.e("password", password);


        // <----- Validation Input
        if (email.isEmpty() && password.isEmpty()) {
            etEmail.setError("Required To Fill In");
            etPassword.setError("Required To Fill In");
            return;
        }

        //validation email
        if (email.isEmpty()) {
            etEmail.setError("Require to fill");
            return;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEmail.setError("Invalid email format");
            return;
        }

        //validation password
        if (password.isEmpty()) {
            etPassword.setError("Require to fill");
            return;
        }
        if (!password.matches("^(?=.*[0-9])(?=.*[a-z])(?=.*[!@#$%^&*+=?-]).{8,25}$")) {
            etPassword.setError("Password should contain 0~9, a~z, symbol, more than 8");
            return;
        }
        // -----> Validation Input


        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            //Log.d(TAG, "signInWithEmail:success");


                            FirebaseUser user = mAuth.getCurrentUser();

                            DocumentReference docRef = FirebaseFirestore.getInstance().collection("users").document(FirebaseAuth.getInstance().getCurrentUser().getUid());
                            docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                                    if (task.isSuccessful()) {
                                        DocumentSnapshot document = task.getResult();

                                        if (document.exists()) {

                                            try {
                                                userType = document.getData().get("userType").toString();
                                                registrationStatus = document.getData().get("registrationStatus").toString();
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }

                                            if (userType.equals("client")) {
                                                Toast.makeText(getApplicationContext(), "Authentication Success",
                                                        Toast.LENGTH_SHORT).show();

                                                Intent intent = new Intent(Login.this, ProfileClient.class);
                                                startActivity(intent);

                                            } else if (userType.equals("admin")) {
                                                Toast.makeText(getApplicationContext(), "Authentication Success",
                                                        Toast.LENGTH_SHORT).show();
                                                Intent intent = new Intent(Login.this, HomeAdmin.class);
                                                startActivity(intent);

                                            } else if (userType.equals("serviceProvider") && registrationStatus.equals("accept")) {

                                                Toast.makeText(getApplicationContext(), "Authentication Success",
                                                        Toast.LENGTH_SHORT).show();
                                                Intent intent = new Intent(Login.this, ProfileServiceProvider.class);
                                                startActivity(intent);

                                            } else if (userType.equals("serviceProvider") && registrationStatus.equals("reject")) {
                                                Toast.makeText(getApplicationContext(), "Sorry your registration has been reject",
                                                        Toast.LENGTH_SHORT).show();
                                            } else if (userType.equals("serviceProvider") && registrationStatus.equals("pending")) {
                                                Toast.makeText(getApplicationContext(), "Sorry your registration still in pending",
                                                        Toast.LENGTH_SHORT).show();
                                            } else {
                                                Toast.makeText(getApplicationContext(), "Authentication Failed",
                                                        Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    }

                                }
                            });


                        } else {
                            // If sign in fails, display a message to the user.
                            //Log.w(TAG, "signInWithEmail:failure", task.getException());
                            Toast.makeText(getApplicationContext(), "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();

                        }
                    }
                });
    }


}