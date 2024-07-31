package com.example.simplymov.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.simplymov.R;
import com.example.simplymov.models.Order;
import com.example.simplymov.models.OrderStatus;
import com.example.simplymov.util.FirebaseUtil;
import com.google.firebase.firestore.FieldValue;
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

        if (order.getStatus().equals(OrderStatus.CANCELLED)) {
            holder.cancelBtn.setText("CANCELLED");
            holder.cancelBtn.setTextSize(14);
            holder.cancelBtn.setEnabled(false);
        }

        holder.orderDate.setText(FirebaseUtil.timestampToDateString(order.getDateTime()));
        holder.orderTime.setText(FirebaseUtil.timestampToTimeString(order.getDateTime()));
        holder.pickupLocation.setText(order.getFrom());
        holder.dropOffLocation.setText(order.getTo());
        holder.orderSize.setText(order.getSize());
        holder.waitingTime.setText(order.getWaitingTime());
        holder.orderStatus.setText(order.getStatus().toString());

        DecimalFormat df = new DecimalFormat("#.##");
        String formattedPrice = df.format(order.getPrice()) + " ETB";
        String formattedKms = df.format(order.getKms()) + " KM";

        holder.orderKms.setText(formattedKms);
        holder.orderPrice.setText(formattedPrice);

        holder.cancelBtn.setOnClickListener(v -> {
            FirebaseFirestore.getInstance().collection("orders").document(order.getOrderId()).update("status", "CANCELLED").addOnSuccessListener(unused -> {
                order.setStatus(OrderStatus.CANCELLED);
                FirebaseFirestore.getInstance().collection("users").document(order.getUserId()).update("balance", FieldValue.increment(order.getPrice()));
                notifyDataSetChanged();
            });
        });
    }

    @Override
    public int getItemCount() {
        return onGoingOrders.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView orderDate, orderTime, pickupLocation, dropOffLocation, orderSize, orderKms, waitingTime, orderPrice, orderStatus, cancelBtn;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            orderDate = itemView.findViewById(R.id.bookOrderDate);
            orderTime = itemView.findViewById(R.id.bookOrderTime);
            pickupLocation = itemView.findViewById(R.id.bookPickup);
            dropOffLocation = itemView.findViewById(R.id.bookDropOff);
            orderSize = itemView.findViewById(R.id.bookOrderSize);
            orderKms = itemView.findViewById(R.id.bookKms);
            waitingTime = itemView.findViewById(R.id.bookWaitingTime);
            orderPrice = itemView.findViewById(R.id.bookOrderPrice);
            orderStatus = itemView.findViewById(R.id.orderStatus);
            cancelBtn = itemView.findViewById(R.id.cancelBtn);
        }
    }
}
