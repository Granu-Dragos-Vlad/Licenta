package com.example.licentaapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GetTokenResult;

public class SignUpAct extends AppCompatActivity {
    private FirebaseAuth auth;
    private EditText signupEmail, signupPassword;
    private Button signupButton;
    private TextView loginRedirectText;
    private boolean OK=true;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        auth = FirebaseAuth.getInstance();
        signupEmail = findViewById(R.id.signup_email);
        signupPassword = findViewById(R.id.signup_password);
        signupButton = findViewById(R.id.signup_button);
        loginRedirectText = findViewById(R.id.loginRedirectText);
        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String user = signupEmail.getText().toString().trim();
                String[] nume=user.split("@");
                String pass = signupPassword.getText().toString().trim();
                auth.fetchSignInMethodsForEmail(user)
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                Task<GetTokenResult> getTokenResultTask = auth.getAccessToken(true);
                                getTokenResultTask.addOnCompleteListener(task1 -> {
                                    if (task1.isSuccessful()) {
                                        GetTokenResult getTokenResult = task1.getResult();
                                        // Utilizatorul exista deja
                                        if (getTokenResult != null && getTokenResult.getSignInProvider() != null) {
                                            OK=false;

                                        }
                                    }
                                });
                            }
                        });
                if (user.isEmpty()){
                    signupEmail.setError("Email cannot be empty");
                }
                if (pass.isEmpty()){
                    signupPassword.setError("Password cannot be empty");
                } else{
                    if(OK==true) {
                        auth.createUserWithEmailAndPassword(user, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(SignUpAct.this, "SignUp Successful", Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(SignUpAct.this, LoginAct.class));
                                } else {
                                    Toast.makeText(SignUpAct.this, "SignUp Failed" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                    else {
                        Toast.makeText(SignUpAct.this, "Utilizator existent", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(SignUpAct.this, LoginAct.class));
                    }
                }
            }
        });
        loginRedirectText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SignUpAct.this, LoginAct.class));
            }
        });
    }
}