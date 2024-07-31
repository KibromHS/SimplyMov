package com.example.movdispatcher;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.movdispatcher.adapters.AvailableDriversAdapter;
import com.example.movdispatcher.models.Driver;
import com.example.movdispatcher.models.Order;
import com.example.movdispatcher.utils.Util;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class AssignOrderActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_assign_order);

        Order order = (Order) getIntent().getSerializableExtra("order");

        TextView pickupLocation = findViewById(R.id.pickupLocation);
        TextView dropOffLocation = findViewById(R.id.dropoffLocation);
        TextView orderSize = findViewById(R.id.orderSize);
        TextView orderKms = findViewById(R.id.orderKms);
        TextView waitingTime = findViewById(R.id.waitingTime);
        TextView orderPrice = findViewById(R.id.orderPrice);
        TextView orderDate = findViewById(R.id.orderDate);
        TextView orderTime = findViewById(R.id.orderTime);

        pickupLocation.setText(order.getFrom());
        dropOffLocation.setText(order.getTo());
        orderSize.setText(order.getSize());

        DecimalFormat decimalFormat = new DecimalFormat("#.##");
        String kms = decimalFormat.format(order.getKms()) + " Kms";
        orderKms.setText(kms);

        waitingTime.setText(order.getWaitingTime());

        String priceStr = "ETB " + decimalFormat.format(order.getPrice());
        orderPrice.setText(priceStr);
        orderDate.setText(Util.timestampToDateString(order.getDateTime()));
        orderTime.setText(Util.timestampToTimeString(order.getDateTime()));

        ArrayList<Driver> availableDrivers = new ArrayList<>();

        RecyclerView rcAvailableDrivers = findViewById(R.id.rcAvailableDrivers);
        RecyclerView.Adapter<AvailableDriversAdapter.ViewHolder> availableDriversAdapter = new AvailableDriversAdapter(availableDrivers, order);

        rcAvailableDrivers.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        rcAvailableDrivers.setAdapter(availableDriversAdapter);

        FirebaseFirestore.getInstance().collection("drivers").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (value == null) return;
                for (DocumentChange documentChange : value.getDocumentChanges()) {
                    if (documentChange.getType().equals(DocumentChange.Type.ADDED)) {
                        Driver driver = documentChange.getDocument().toObject(Driver.class);

                        if (driver.getDriverStatus().equals("AVAILABLE") && driver.getTruckSize().equals(order.getSize())) {
                            availableDrivers.add(driver);
                        }

                        availableDriversAdapter.notifyDataSetChanged();
                    }
                }
            }
        });

        ImageView backBtn = findViewById(R.id.assignBackBtn);

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }
}