package mobile.test.homerepair.main;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import mobile.test.homerepair.R;
import mobile.test.homerepair.admin.HomeAdmin;
import mobile.test.homerepair.client.ProfileClient;
import mobile.test.homerepair.main.Login;
import mobile.test.homerepair.provider.ProfileServiceProvider;

public class SplashScreen extends AppCompatActivity {

    Intent intent;
    FirebaseAuth mAuth = FirebaseAuth.getInstance();

    ImageView splashImg;

    String userType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);


        splashImg = findViewById(R.id.splashImg);
        splashImg.animate().translationY(-1600).setStartDelay(2000).setDuration(1000);

        //intent = new Intent(this, LoginActivity.class);

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){

            DocumentReference docRef = FirebaseFirestore.getInstance().collection("users").document(FirebaseAuth.getInstance().getCurrentUser().getUid());
            docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                    if(task.isSuccessful()){
                        DocumentSnapshot document = task.getResult();

                        if(document.exists()){
                            userType = document.getData().get("userType").toString();

                            if(userType.equals("client")){
//                                Toast.makeText(getApplicationContext(), "Authentication Success",
//                                        Toast.LENGTH_SHORT).show();

                                intent = new Intent(SplashScreen.this, ProfileClient.class);

                            }else if (userType.equals("serviceProvider")){

//                                Toast.makeText(getApplicationContext(), "Authentication Success",
//                                        Toast.LENGTH_SHORT).show();
                                intent = new Intent(SplashScreen.this, ProfileServiceProvider.class);

                            }else{
//                                Toast.makeText(getApplicationContext(), "Authentication Success",
//                                        Toast.LENGTH_SHORT).show();
                                intent = new Intent(SplashScreen.this, HomeAdmin.class);
                            }
                        }
                    }

                }
            });
        }else{

            intent = new Intent(getApplicationContext(), Login.class);

        }


        Thread thread = new Thread(){
            public void run(){


                try{
                    Thread.sleep(3000);
                    startActivity(intent);
                }
                catch(Exception e){
                }

            }};
        thread.start();

    }
}