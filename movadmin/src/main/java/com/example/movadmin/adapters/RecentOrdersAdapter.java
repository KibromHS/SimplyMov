package com.example.movadmin.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.movadmin.CustomerReceived;
import com.example.movadmin.R;
import com.example.movadmin.models.Order;
import com.example.movadmin.models.UserModel;
import com.example.movadmin.utils.Util;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class RecentOrdersAdapter extends RecyclerView.Adapter<RecentOrdersAdapter.ViewHolder> {
    ArrayList<Order> onGoingOrders;

    public RecentOrdersAdapter(ArrayList<Order> onGoingOrders) {
        this.onGoingOrders = onGoingOrders;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.viewholder_recent, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Order order = onGoingOrders.get(position);

        getCustomer(order.getUserId(), customer -> {
            holder.customerName.setText(customer.getUsername());
            Glide.with(holder.itemView.getContext()).load(customer.getProfilePic()).into(holder.customerImage);

            holder.orderDate.setText(Util.timestampToDateString(order.getDateTime()));
            holder.orderTime.setText(Util.timestampToTimeString(order.getDateTime()));
            holder.pickupLocation.setText(order.getFrom());
            holder.dropOffLocation.setText(order.getTo());
            holder.orderSize.setText(order.getSize());
            holder.ratingBar.setRating(order.getRate());

            DecimalFormat df = new DecimalFormat("#.##");
            String formattedPrice = df.format(order.getPrice()) + " ETB";
            String formattedKms = df.format(order.getKms()) + " KM";

            holder.orderKms.setText(formattedKms);
            holder.orderPrice.setText(formattedPrice);
        });
    }

    private void getCustomer(String id, CustomerReceived callback) {
        FirebaseFirestore.getInstance().collection("users").document(id).get().addOnSuccessListener(documentSnapshot -> {
            UserModel customer = documentSnapshot.toObject(UserModel.class);
            callback.onCustomerReceived(customer);
        });
    }

    @Override
    public int getItemCount() {
        return onGoingOrders.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView orderDate, orderTime, pickupLocation, dropOffLocation, orderSize, orderKms, orderPrice, customerName;
        ImageView customerImage;
        RatingBar ratingBar;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            orderDate = itemView.findViewById(R.id.orderDate);
            orderTime = itemView.findViewById(R.id.orderTime);
            pickupLocation = itemView.findViewById(R.id.pickupLocation);
            dropOffLocation = itemView.findViewById(R.id.dropoffLocation);
            orderSize = itemView.findViewById(R.id.orderSize);
            orderKms = itemView.findViewById(R.id.orderKms);
            orderPrice = itemView.findViewById(R.id.orderPrice);
            ratingBar = itemView.findViewById(R.id.recentRatingBar);
            customerName = itemView.findViewById(R.id.recentCustomersName);
            customerImage = itemView.findViewById(R.id.recentCustomersImage);
        }
    }
}
