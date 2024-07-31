package com.example.movadmin;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import com.example.movadmin.adapters.DispatchersAdapter;
import com.example.movadmin.adapters.DriversAdapter;
import com.example.movadmin.models.Dispatcher;
import com.example.movadmin.models.Driver;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class DriversActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drivers);

        TextView addDriverBtn = findViewById(R.id.addDriverBtn);
        RecyclerView rcDrivers = findViewById(R.id.rcDrivers);

        ArrayList<Driver> drivers = new ArrayList<>();
        DriversAdapter adapter = new DriversAdapter(drivers);

        rcDrivers.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        rcDrivers.setAdapter(adapter);

        FirebaseFirestore.getInstance().collection("drivers").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (value == null) return;
                for (DocumentChange documentChange : value.getDocumentChanges()) {
                    if (documentChange.getType().equals(DocumentChange.Type.ADDED)) {
                        Driver driver = documentChange.getDocument().toObject(Driver.class);
                        drivers.add(driver);
                    }
                }

                adapter.notifyDataSetChanged();
            }
        });

        addDriverBtn.setOnClickListener(v -> {
            Intent intent = new Intent(this, AddDriverActivity.class);
            startActivity(intent);
        });
    }
}