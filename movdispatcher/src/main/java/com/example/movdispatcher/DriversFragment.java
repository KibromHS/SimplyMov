package com.example.movdispatcher;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.movdispatcher.adapters.AllDriversAdapter;
import com.example.movdispatcher.models.Driver;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class DriversFragment extends Fragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_drivers, container, false);

        RecyclerView rcAllDrivers = view.findViewById(R.id.rcAllDrivers);
        ArrayList<Driver> drivers = new ArrayList<>();
        RecyclerView.Adapter<AllDriversAdapter.ViewHolder> adapter = new AllDriversAdapter(drivers);
        rcAllDrivers.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        rcAllDrivers.setAdapter(adapter);

        FirebaseFirestore.getInstance().collection("drivers").addSnapshotListener(new EventListener<QuerySnapshot>() {
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

        return view;
    }
}