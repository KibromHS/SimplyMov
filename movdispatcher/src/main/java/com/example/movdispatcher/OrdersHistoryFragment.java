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
import com.example.movdispatcher.models.Order;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class OrdersHistoryFragment extends Fragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_orders_history, container, false);

        ArrayList<Order> historyOrders = new ArrayList<>();

        TextView noHistoryOrders = view.findViewById(R.id.noHistoryOrders);
        RecyclerView rcHistoryOrders = view.findViewById(R.id.rcHistoryOrders);
        RecyclerView.Adapter<HistoryOrdersAdapter.ViewHolder> historyOrdersAdapter = new HistoryOrdersAdapter(historyOrders);

        rcHistoryOrders.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        rcHistoryOrders.setAdapter(historyOrdersAdapter);

        FirebaseFirestore.getInstance().collection("orders").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (value == null) return;
                for (DocumentChange documentChange : value.getDocumentChanges()) {
                    if (documentChange.getType().equals(DocumentChange.Type.ADDED)) {
                        Order order = documentChange.getDocument().toObject(Order.class);

                        if (order.getStatus().toString().equals("DELIVERED") || (order.getDriverId() == null && order.getStatus().toString().equals("CANCELLED"))) {
                            historyOrders.add(order);
                        }

                        historyOrdersAdapter.notifyDataSetChanged();
                    }
                }

                if (historyOrders.size() == 0) {
                    noHistoryOrders.setVisibility(View.VISIBLE);
                } else {
                    noHistoryOrders.setVisibility(View.GONE);
                }
            }
        });

        return view;
    }
}