package com.example.licentaapp;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.FirebaseUserMetadata;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ProfileAct extends AppCompatActivity {
    private RadioGroup statusRadioGroup;
    private Button updateButton;
    private Button butonLogout;
    private TextView semail;
    private FirebaseUser user;
    private TextView datainregistrare;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        statusRadioGroup = findViewById(R.id.statusRadioGroup);
        butonLogout = findViewById(R.id.buton_logout);
        updateButton = findViewById(R.id.updateButton);
        semail=findViewById(R.id.text_afis);
        datainregistrare=findViewById(R.id.data_afis);
        user= FirebaseAuth.getInstance().getCurrentUser();
        if (user != null)
        {
            FirebaseUserMetadata metadata = user.getMetadata();
            if (metadata != null) {
                long registrationTimestamp = metadata.getCreationTimestamp();
                Date registrationDate = new Date(registrationTimestamp);
                SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());
                String registrationDateString = dateFormat.format(registrationDate);
                datainregistrare.setText(registrationDateString);
            }
            String userEmail = user.getEmail();
            semail.setText(userEmail);
        }
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigation);
        bottomNavigationView.setSelectedItemId(R.id.bottom_profile);
        butonLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProfileAct.this, LoginAct.class);
                startActivity(intent);
                finish();
            }
        });
        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int selectedId = statusRadioGroup.getCheckedRadioButtonId();
                RadioButton selectedRadioButton = findViewById(selectedId);

                if (selectedRadioButton != null) {
                    String selectedStatus = selectedRadioButton.getText().toString();
                    updateStatus(selectedStatus);
                } else {
                    Toast.makeText(ProfileAct.this, "Selectează o stare!", Toast.LENGTH_SHORT).show();
                }
            }

            private void updateStatus(String selectedStatus) {
                Toast.makeText(ProfileAct.this, "Starea selectată: " + selectedStatus, Toast.LENGTH_SHORT).show();
            }
        });
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.bottom_home) {
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                finish();
                return true;
            } else if (itemId == R.id.bottom_search) {
                startActivity(new Intent(getApplicationContext(), SearchAct.class));
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                finish();
                return true;
            } else if (itemId == R.id.bottom_profile) {
                return true;
            }
            return false;
        });
    }
}