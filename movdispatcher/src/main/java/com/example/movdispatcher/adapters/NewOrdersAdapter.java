package com.example.movdispatcher.adapters;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.movdispatcher.AssignOrderActivity;
import com.example.movdispatcher.R;
import com.example.movdispatcher.models.Order;
import com.example.movdispatcher.utils.Util;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class NewOrdersAdapter extends RecyclerView.Adapter<NewOrdersAdapter.ViewHolder> {
    ArrayList<Order> onGoingOrders;

    public NewOrdersAdapter(ArrayList<Order> onGoingOrders) {
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
        holder.orderDate.setText(Util.timestampToDateString(order.getDateTime()));
        holder.orderTime.setText(Util.timestampToTimeString(order.getDateTime()));
        holder.pickupLocation.setText(order.getFrom());
        holder.dropOffLocation.setText(order.getTo());
        holder.orderSize.setText(order.getSize());
        holder.waitingTime.setText(order.getWaitingTime());

        DecimalFormat df = new DecimalFormat("#.##");
        String formattedPrice = df.format(order.getPrice()) + " ETB";
        String formattedKms = df.format(order.getKms()) + " KM";

        holder.orderKms.setText(formattedKms);
        holder.orderPrice.setText(formattedPrice);

        if (order.getStatus().toString().equals("PROCESSING")) {
            holder.assignDriver.setText("Processing...");
            holder.assignDriver.setEnabled(false);
        }

        holder.assignDriver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(view.getContext(), AssignOrderActivity.class);
                i.putExtra("order", order);
                view.getContext().startActivity(i);
            }
        });
    }

    @Override
    public int getItemCount() {
        return onGoingOrders.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView orderDate, orderTime, pickupLocation, dropOffLocation, orderSize, orderKms, waitingTime, orderPrice, assignDriver;

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
            assignDriver = itemView.findViewById(R.id.assignBtn);
        }
    }
}
