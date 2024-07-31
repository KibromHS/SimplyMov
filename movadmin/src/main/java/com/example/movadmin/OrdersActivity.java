package com.example.movadmin;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.example.movadmin.adapters.OngoingOrdersAdapter;
import com.example.movadmin.adapters.RecentOrdersAdapter;
import com.example.movadmin.models.Order;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class OrdersActivity extends AppCompatActivity {
    private RecyclerView rcOnGoing, rcRecent;
    private TextView orderTitle1, orderTitle2, noOrdersTxt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_orders);
        initView();

        ArrayList<Order> onGoingOrders = new ArrayList<>();
        ArrayList<Order> recentOrders = new ArrayList<>();

        RecyclerView.Adapter<OngoingOrdersAdapter.ViewHolder> ongoingAdapter = new OngoingOrdersAdapter(onGoingOrders);
        RecyclerView.Adapter<RecentOrdersAdapter.ViewHolder> recentAdapter = new RecentOrdersAdapter(recentOrders);

        rcOnGoing.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        rcOnGoing.setNestedScrollingEnabled(false);
        rcOnGoing.setAdapter(ongoingAdapter);

        rcRecent.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        rcRecent.setNestedScrollingEnabled(false);
        rcRecent.setAdapter(recentAdapter);

        FirebaseFirestore.getInstance().collection("orders").addSnapshotListener((value, error) -> {
            if (value == null) {
                orderTitle1.setVisibility(View.GONE);
                orderTitle2.setVisibility(View.GONE);
                noOrdersTxt.setVisibility(View.VISIBLE);
                return;
            }
            for (DocumentChange documentChange : value.getDocumentChanges()) {
                if (documentChange.getType().equals(DocumentChange.Type.ADDED)) {
                    Order order = documentChange.getDocument().toObject(Order.class);

                    if (order.getStatus().equals("DELIVERED")) {
                        recentOrders.add(order);
                    } else {
                        onGoingOrders.add(order);
                    }

                    if (onGoingOrders.size() == 0) {
                        orderTitle1.setVisibility(View.GONE);
                    } else {
                        orderTitle1.setVisibility(View.VISIBLE);
                    }
                    if (recentOrders.size() == 0) {
                        orderTitle2.setVisibility(View.GONE);
                    } else {
                        orderTitle2.setVisibility(View.VISIBLE);
                    }

                    if (onGoingOrders.size() == 0 && recentOrders.size() == 0) {
                        noOrdersTxt.setVisibility(View.VISIBLE);
                    } else {
                        noOrdersTxt.setVisibility(View.GONE);
                    }

                    ongoingAdapter.notifyDataSetChanged();
                    recentAdapter.notifyDataSetChanged();
                }
            }
        });
    }

    private void initView() {
        rcOnGoing = findViewById(R.id.rcOnGoing);
        rcRecent = findViewById(R.id.rcRecent);
        orderTitle1 = findViewById(R.id.orderTitle1);
        orderTitle2 = findViewById(R.id.orderTitle2);
        noOrdersTxt = findViewById(R.id.noOrdersTxt);
    }
}