package com.example.movdispatcher;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.movdispatcher.adapters.HistoryOrdersAdapter;
import com.example.movdispatcher.adapters.NewOrdersAdapter;
import com.example.movdispatcher.models.Order;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class OrdersNewFragment extends Fragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_orders_new, container, false);

        ArrayList<Order> newOrders = new ArrayList<>();

        TextView noNewOrders = view.findViewById(R.id.noNewOrders);
        RecyclerView rcNewOrders = view.findViewById(R.id.rcNewOrders);
        RecyclerView.Adapter<NewOrdersAdapter.ViewHolder> newOrdersAdapter = new NewOrdersAdapter(newOrders);

        rcNewOrders.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        rcNewOrders.setAdapter(newOrdersAdapter);

        FirebaseFirestore.getInstance().collection("orders").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (value == null) return;
                for (DocumentChange documentChange : value.getDocumentChanges()) {
                    if (documentChange.getType().equals(DocumentChange.Type.ADDED)) {
                        Order order = documentChange.getDocument().toObject(Order.class);
                        if (!order.getStatus().toString().equals("DELIVERED")) {
                            if (!(order.getDriverId() == null && order.getStatus().toString().equals("CANCELLED"))) {
                                newOrders.add(order);
                            }
                        }
                    }
                }

                newOrdersAdapter.notifyDataSetChanged();

                if (newOrders.size() == 0) {
                    noNewOrders.setVisibility(View.VISIBLE);
                } else {
                    noNewOrders.setVisibility(View.GONE);
                }
            }
        });

        return view;
    }
}