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
import com.example.movadmin.models.Dispatcher;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class DispatchersActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dispatchers);

        TextView addDispatcherBtn = findViewById(R.id.addDispatcherBtn);
        RecyclerView rcDispatchers = findViewById(R.id.rcDispatchers);

        ArrayList<Dispatcher> dispatchers = new ArrayList<>();
        DispatchersAdapter adapter = new DispatchersAdapter(dispatchers);

        rcDispatchers.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        rcDispatchers.setAdapter(adapter);

        FirebaseFirestore.getInstance().collection("dispatchers").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (value == null) return;
                for (DocumentChange documentChange : value.getDocumentChanges()) {
                    if (documentChange.getType().equals(DocumentChange.Type.ADDED)) {
                        Dispatcher dispatcher = documentChange.getDocument().toObject(Dispatcher.class);
                        dispatchers.add(dispatcher);
                    }
                }

                adapter.notifyDataSetChanged();
            }
        });

        addDispatcherBtn.setOnClickListener(v -> {
            Intent intent = new Intent(this, AddDispatcherActivity.class);
            startActivity(intent);
        });
    }
}