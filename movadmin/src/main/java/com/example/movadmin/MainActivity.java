package com.example.movadmin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.example.movadmin.models.Driver;
import com.example.movadmin.models.Order;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.DecimalFormat;

public class MainActivity extends AppCompatActivity {
    TextView numOfCustomers, numOfDispatchers, numOfDrivers, numOfOrders;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ConstraintLayout customers = findViewById(R.id.customers);
        ConstraintLayout dispatchers = findViewById(R.id.dispatchers);
        ConstraintLayout drivers = findViewById(R.id.drivers);
        ConstraintLayout orders = findViewById(R.id.orders);
        TextView totalIncome = findViewById(R.id.totalIncome);
        TextView totalRating = findViewById(R.id.totalRating);

        numOfCustomers = findViewById(R.id.numOfCustomers);
        numOfDispatchers = findViewById(R.id.numOfDispatchers);
        numOfDrivers = findViewById(R.id.numOfDrivers);
        numOfOrders = findViewById(R.id.numOfOrders);

        customers.setOnClickListener(view -> startActivity(new Intent(this, CustomersActivity.class)));

        dispatchers.setOnClickListener(view -> startActivity(new Intent(this, DispatchersActivity.class)));

        drivers.setOnClickListener(view -> startActivity(new Intent(this, DriversActivity.class)));

        orders.setOnClickListener(view -> startActivity(new Intent(this, OrdersActivity.class)));

        FirebaseFirestore.getInstance().collection("orders").get().addOnSuccessListener(queryDocumentSnapshots -> {
            if (queryDocumentSnapshots.isEmpty()) {
                totalIncome.setText("ETB 0.0");
                return;
            }
            double totalPrice = 0;
            for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                Order order = documentSnapshot.toObject(Order.class);
                assert order != null;
                if (!(order.getDriverId() == null && order.getStatus().equals("CANCELLED"))) {
                    totalPrice += order.getPrice();
                }
            }

            DecimalFormat decimalFormat = new DecimalFormat("#.##");
            String price = "ETB " + decimalFormat.format(totalPrice);
            totalIncome.setText(price);
        });

        FirebaseFirestore.getInstance().collection("orders").get().addOnSuccessListener(queryDocumentSnapshots -> {
            if (queryDocumentSnapshots.size() == 0) {
                totalRating.setText("0.0");
                return;
            }
            double rating = 0.0;
            int count = 0;
            for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                Order order = documentSnapshot.toObject(Order.class);
                assert order != null;
                if (order.getRate() > 0) {
                    rating += order.getRate();
                    count++;
                }
            }
            double finalRate = count == 0 ? 0.0 : rating / count;
            DecimalFormat decimalFormat = new DecimalFormat("#.#");
            String rate = decimalFormat.format(finalRate);
            totalRating.setText(rate);
        });

        FirebaseFirestore.getInstance().collection("users")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            QuerySnapshot querySnapshot = task.getResult();
                            if (querySnapshot != null) {
                                int count = querySnapshot.size();
                                numOfCustomers.setText(String.valueOf(count));
                            } else {
                                numOfCustomers.setText("0");
                            }
                        } else {
                            Exception e = task.getException();
                            if (e != null) {
                                Toast.makeText(MainActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });

        FirebaseFirestore.getInstance().collection("dispatchers")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            QuerySnapshot querySnapshot = task.getResult();
                            if (querySnapshot != null) {
                                int count = querySnapshot.size();
                                numOfDispatchers.setText(String.valueOf(count));
                            } else {
                                numOfDispatchers.setText("0");
                            }
                        } else {
                            Exception e = task.getException();
                            if (e != null) {
                                Toast.makeText(MainActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });


        FirebaseFirestore.getInstance().collection("drivers")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            QuerySnapshot querySnapshot = task.getResult();
                            if (querySnapshot != null) {
                                int count = querySnapshot.size();
                                numOfDrivers.setText(String.valueOf(count));
                            } else {
                                numOfDrivers.setText("0");
                            }
                        } else {
                            Exception e = task.getException();
                            if (e != null) {
                                Toast.makeText(MainActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });

        FirebaseFirestore.getInstance().collection("orders")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        QuerySnapshot querySnapshot = task.getResult();
                        if (querySnapshot != null) {
                            int count = querySnapshot.size();
                            numOfOrders.setText(String.valueOf(count));
                        } else {
                            numOfOrders.setText("0");
                        }
                    } else {
                        Exception e = task.getException();
                        if (e != null) {
                            Toast.makeText(MainActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });

    }

    @Override
    protected void onResume() {
        super.onResume();
        FirebaseFirestore.getInstance().collection("users")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            QuerySnapshot querySnapshot = task.getResult();
                            if (querySnapshot != null) {
                                int count = querySnapshot.size();
                                numOfCustomers.setText(String.valueOf(count));
                            } else {
                                numOfCustomers.setText("0");
                            }
                        } else {
                            Exception e = task.getException();
                            if (e != null) {
                                Toast.makeText(MainActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });

        FirebaseFirestore.getInstance().collection("dispatchers")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            QuerySnapshot querySnapshot = task.getResult();
                            if (querySnapshot != null) {
                                int count = querySnapshot.size();
                                numOfDispatchers.setText(String.valueOf(count));
                            } else {
                                numOfDispatchers.setText("0");
                            }
                        } else {
                            Exception e = task.getException();
                            if (e != null) {
                                Toast.makeText(MainActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });


        FirebaseFirestore.getInstance().collection("drivers")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            QuerySnapshot querySnapshot = task.getResult();
                            if (querySnapshot != null) {
                                int count = querySnapshot.size();
                                numOfDrivers.setText(String.valueOf(count));
                            } else {
                                numOfDrivers.setText("0");
                            }
                        } else {
                            Exception e = task.getException();
                            if (e != null) {
                                Toast.makeText(MainActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });

        FirebaseFirestore.getInstance().collection("orders")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        QuerySnapshot querySnapshot = task.getResult();
                        if (querySnapshot != null) {
                            int count = querySnapshot.size();
                            numOfOrders.setText(String.valueOf(count));
                        } else {
                            numOfOrders.setText("0");
                        }
                    } else {
                        Exception e = task.getException();
                        if (e != null) {
                            Toast.makeText(MainActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}