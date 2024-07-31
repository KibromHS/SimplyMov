package com.example.simplymov;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.simplymov.adapters.OngoingOrdersAdapter;
import com.example.simplymov.adapters.RecentOrdersAdapter;
import com.example.simplymov.models.Driver;
import com.example.simplymov.models.Order;
import com.example.simplymov.models.OrderStatus;
import com.example.simplymov.models.UserModel;
import com.example.simplymov.util.FirebaseUtil;
import com.example.simplymov.util.UserPreferences;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class OrdersFragment extends Fragment {
    private RecyclerView rcOnGoing, rcRecent;
    private TextView orderTitle1, orderTitle2, noOrdersTxt;

    private final UserModel userModel = UserPreferences.getUser();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_orders, container, false);
        initView(view);

        ArrayList<Order> onGoingOrders = new ArrayList<>();
        ArrayList<Order> recentOrders = new ArrayList<>();

        RecyclerView.Adapter<OngoingOrdersAdapter.ViewHolder> ongoingAdapter = new OngoingOrdersAdapter(onGoingOrders);
        RecyclerView.Adapter<RecentOrdersAdapter.ViewHolder> recentAdapter = new RecentOrdersAdapter(recentOrders);

        rcOnGoing.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        rcOnGoing.setNestedScrollingEnabled(false);
        rcOnGoing.setAdapter(ongoingAdapter);

        rcRecent.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        rcRecent.setNestedScrollingEnabled(false);
        rcRecent.setAdapter(recentAdapter);

        FirebaseFirestore.getInstance().collection("orders").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (value == null) {
                    orderTitle1.setVisibility(View.GONE);
                    orderTitle2.setVisibility(View.GONE);
                    noOrdersTxt.setVisibility(View.VISIBLE);
                    return;
                }
                for (DocumentChange documentChange : value.getDocumentChanges()) {
                    if (documentChange.getType().equals(DocumentChange.Type.ADDED)) {
                        Order order = documentChange.getDocument().toObject(Order.class);

                        assert userModel != null;
                        if (order.getUserId().equals(userModel.getUserId())) {
                            if (order.getStatus().toString().equals(OrderStatus.DELIVERED.toString())) {
                                recentOrders.add(order);

                                if (order.getRate() == 0) {
                                    showCustomDialog(order);
                                }
                            } else {
                                onGoingOrders.add(order);
                            }
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
            }
        });

        return view;
    }

    private void showCustomDialog(Order order) {
        final Dialog dialog = new Dialog(getContext());
        dialog.setContentView(R.layout.popup_rating);

        TextView driverName = dialog.findViewById(R.id.driverName);
        TextView orderDate = dialog.findViewById(R.id.bookOrderDate);
        TextView orderTime = dialog.findViewById(R.id.bookOrderTime);
        TextView pickup = dialog.findViewById(R.id.bookPickup);
        TextView dropOff = dialog.findViewById(R.id.bookDropOff);
        TextView orderPrice = dialog.findViewById(R.id.bookOrderPrice);
        TextView cancelRating = dialog.findViewById(R.id.cancelRating);
        ImageView driverImage = dialog.findViewById(R.id.driverImage);
        RatingBar ratingBar = dialog.findViewById(R.id.orderRatingBar);
        Button rateBtn = dialog.findViewById(R.id.rateBtn);

        getDriver(order.getDriverId(), driver -> {
            driverName.setText(driver.getDriverName());
            Glide.with(requireContext()).load(driver.getDriverPic()).into(driverImage);
        });

        orderDate.setText(FirebaseUtil.timestampToDateString(order.getDateTime()));
        orderTime.setText(FirebaseUtil.timestampToTimeString(order.getDateTime()));
        pickup.setText(order.getFrom());
        dropOff.setText(order.getTo());

        DecimalFormat decimalFormat = new DecimalFormat("#.##");
        String priceStr = "ETB" + decimalFormat.format(order.getPrice());
        orderPrice.setText(priceStr);

        cancelRating.setOnClickListener(view -> dialog.dismiss());

        ratingBar.setOnRatingBarChangeListener((ratingBar1, v, b) -> {
            rateBtn.setEnabled(true);
        });

        rateBtn.setOnClickListener(view -> {
            FirebaseFirestore.getInstance().collection("orders").document(order.getOrderId()).update("rate", ratingBar.getRating());
            FirebaseFirestore.getInstance().collection("drivers").document(order.getDriverId()).update("numOfRates", FieldValue.increment(1)).addOnSuccessListener(unused -> {
                FirebaseFirestore.getInstance().collection("drivers").document(order.getDriverId()).get().addOnSuccessListener(documentSnapshot -> {
                    Driver driver = documentSnapshot.toObject(Driver.class);
                    assert driver != null;
                    double rating = driver.getRating();
                    int numOfRates = driver.getNumOfRates();
                    double total = (rating + ratingBar.getRating()) / numOfRates;
                    FirebaseFirestore.getInstance().collection("drivers").document(order.getDriverId()).update("rating", total);
                    dialog.dismiss();
                    startActivity(new Intent(getContext(), MainActivity.class));
                });
            });
        });

        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        dialog.show();
    }

    private void getDriver(String id, DriverReceived callback) {
        FirebaseFirestore.getInstance().collection("drivers").document(id).get().addOnSuccessListener(documentSnapshot -> {
            Driver driver = documentSnapshot.toObject(Driver.class);
            callback.onDriverReceived(driver);
        });
    }

    private void initView(View view) {
        rcOnGoing = view.findViewById(R.id.rcOnGoing);
        rcRecent = view.findViewById(R.id.rcRecent);
        orderTitle1 = view.findViewById(R.id.orderTitle1);
        orderTitle2 = view.findViewById(R.id.orderTitle2);
        noOrdersTxt = view.findViewById(R.id.noOrdersTxt);
    }
}