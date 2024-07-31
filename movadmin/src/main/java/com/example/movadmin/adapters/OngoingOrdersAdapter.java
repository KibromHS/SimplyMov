package com.example.movadmin.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.movadmin.CustomerReceived;
import com.example.movadmin.R;
import com.example.movadmin.models.Order;
import com.example.movadmin.models.UserModel;
import com.example.movadmin.utils.Util;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class OngoingOrdersAdapter extends RecyclerView.Adapter<OngoingOrdersAdapter.ViewHolder> {
    ArrayList<Order> onGoingOrders;

    public OngoingOrdersAdapter(ArrayList<Order> onGoingOrders) {
        this.onGoingOrders = onGoingOrders;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.viewholder_orders, parent, false);
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
            holder.waitingTime.setText(order.getWaitingTime());
            holder.orderStatus.setText(order.getStatus());

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
        TextView orderDate, orderTime, pickupLocation, dropOffLocation, orderSize, orderKms, waitingTime, orderPrice, orderStatus, customerName;
        ImageView customerImage;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            orderDate = itemView.findViewById(R.id.orderDate);
            orderTime = itemView.findViewById(R.id.orderTime);
            pickupLocation = itemView.findViewById(R.id.pickupLocation);
            dropOffLocation = itemView.findViewById(R.id.dropoffLocation);
            orderSize = itemView.findViewById(R.id.orderSize);
            orderKms = itemView.findViewById(R.id.orderKms);
            waitingTime = itemView.findViewById(R.id.waitingTime);
            orderPrice = itemView.findViewById(R.id.orderPrice);
            orderStatus = itemView.findViewById(R.id.orderStatus);
            customerName = itemView.findViewById(R.id.customersName);
            customerImage = itemView.findViewById(R.id.customersImage);
        }
    }
}
