package mobile.test.homerepair.main;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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

import mobile.test.homerepair.admin.HomeAdmin;
import mobile.test.homerepair.client.ProfileClient;
import mobile.test.homerepair.R;
import mobile.test.homerepair.provider.ProfileServiceProvider;
import mobile.test.homerepair.testDemo.TestMainHomePage;

public class Login extends AppCompatActivity {

    //private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    
    
    TextView tvRegister,tvForgotPassword;
    Button btLogin,btn_toTestPage;
    EditText etEmail,etPassword;

    String email,password,userType;


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


    public void toRegister(){
        Intent intent = new Intent(getApplicationContext(), AccountType.class);
        startActivity(intent);
    }


    public void userLogin(){

        email = etEmail.getText().toString();
        password = etPassword.getText().toString();

        Log.e("email", email);
        Log.e("password", password);


        // <----- Validation Input
        if (email.isEmpty() && password.isEmpty()){
            etEmail.setError("Required To Fill In");
            etPassword.setError("Required To Fill In");
            return;
        }

        //validation email
        if (email.isEmpty()){
            etEmail.setError("Require to fill");
            return;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            etEmail.setError("Invalid email format");
            return;
        }

        //validation password
        if (password.isEmpty()){
            etPassword.setError("Require to fill");
            return;
        }
        if (!password.matches("^(?=.*[0-9])(?=.*[a-z])(?=.*[!@#$%^&*+=?-]).{8,25}$")){
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

                                    if(task.isSuccessful()){
                                        DocumentSnapshot document = task.getResult();

                                        if(document.exists()){
                                            userType = document.getData().get("userType").toString();

                                            if(userType.equals("client")){
                                                Toast.makeText(getApplicationContext(), "Authentication Success.",
                                                        Toast.LENGTH_SHORT).show();

                                                Intent intent = new Intent(Login.this, ProfileClient.class);
                                                startActivity(intent);

                                            }else if (userType.equals("serviceProvider")){

                                                Toast.makeText(getApplicationContext(), "Authentication Success.",
                                                        Toast.LENGTH_SHORT).show();
                                                Intent intent = new Intent(Login.this, ProfileServiceProvider.class);
                                                startActivity(intent);

                                            }else{
                                                Toast.makeText(getApplicationContext(), "Authentication Success.",
                                                        Toast.LENGTH_SHORT).show();
                                                Intent intent = new Intent(Login.this, HomeAdmin.class);
                                                startActivity(intent);
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