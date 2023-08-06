package com.example.licentaapp;


import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;



import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;


import java.util.ArrayList;
import java.util.List;


public class SearchAct extends AppCompatActivity {
    private FirebaseUser user;
    private FirebaseFirestore db;
    private List<Pacient> listapacienti=new ArrayList<>();
    private ListView listView;

    private String numemedic;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        listView = findViewById(R.id.list_view);
        db = FirebaseFirestore.getInstance();
        CollectionReference mediciRef = db.collection("medici");
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();
        if (user != null) {
            String userEmail = user.getEmail();
            assert userEmail != null;
            String[] tokens = userEmail.split("@");
            numemedic = tokens[0];
        }
        DocumentReference medicRef = db.collection("medici").document(numemedic);
        medicRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                String listaPacString = documentSnapshot.getString("listapac");
                if (listaPacString != null) {
                    String[] pacientIds = listaPacString.split(",");
                    for (String pacientId : pacientIds) {
                        Query pacientQuery = db.collection("pacienti").whereEqualTo("id", pacientId);
                        pacientQuery.get().addOnSuccessListener(queryDocumentSnapshots -> {
                            for (QueryDocumentSnapshot pacientSnapshot : queryDocumentSnapshots) {
                                Pacient pacient = pacientSnapshot.toObject(Pacient.class);
                                listapacienti.add(pacient);
                            }
                        });
                    }
                }
            }
        });
        ArrayAdapter<Pacient> arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, listapacienti);
        listView.setAdapter(arrayAdapter);
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigation);
        bottomNavigationView.setSelectedItemId(R.id.bottom_search);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.bottom_home) {
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                finish();
                return true;
            } else if (itemId == R.id.bottom_search) {
                return true;
            }  else if (itemId == R.id.bottom_profile) {
                startActivity(new Intent(getApplicationContext(), ProfileAct.class));
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                finish();
                return true;
            }
            return false;
        });
    }
}