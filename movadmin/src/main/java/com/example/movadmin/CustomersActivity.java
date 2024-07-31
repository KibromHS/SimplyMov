package com.example.movadmin;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import com.example.movadmin.adapters.CustomersAdapter;
import com.example.movadmin.adapters.DispatchersAdapter;
import com.example.movadmin.adapters.DriversAdapter;
import com.example.movadmin.models.Dispatcher;
import com.example.movadmin.models.Driver;
import com.example.movadmin.models.UserModel;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class CustomersActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customers);

        RecyclerView rcCustomers = findViewById(R.id.rcCustomers);

        ArrayList<UserModel> customers = new ArrayList<>();
        CustomersAdapter adapter = new CustomersAdapter(customers);

        rcCustomers.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        rcCustomers.setAdapter(adapter);

        FirebaseFirestore.getInstance().collection("users").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (value == null) return;
                for (DocumentChange documentChange : value.getDocumentChanges()) {
                    if (documentChange.getType().equals(DocumentChange.Type.ADDED)) {
                        UserModel customer = documentChange.getDocument().toObject(UserModel.class);
                        customers.add(customer);
                    }
                }

                adapter.notifyDataSetChanged();
            }
        });
    }
}