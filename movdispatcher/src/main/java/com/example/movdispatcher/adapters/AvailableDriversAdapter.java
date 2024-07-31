package com.example.movdispatcher.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.movdispatcher.R;
import com.example.movdispatcher.models.Driver;
import com.example.movdispatcher.models.Order;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class AvailableDriversAdapter extends RecyclerView.Adapter<AvailableDriversAdapter.ViewHolder> {
    ArrayList<Driver> drivers;
    Order order;

    public AvailableDriversAdapter(ArrayList<Driver> drivers, Order order) {
        this.drivers = drivers;
        this.order = order;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.viewholder_driver, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Driver driver = drivers.get(position);
        Glide.with(holder.itemView.getContext()).load(driver.getDriverPic()).into(holder.driverPic);
        holder.driverName.setText(driver.getDriverName());

        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseFirestore.getInstance().collection("orders").document(order.getOrderId()).update("driverId", driver.getDriverId());
                FirebaseFirestore.getInstance().collection("orders").document(order.getOrderId()).update("status", "PROCESSING");
                FirebaseFirestore.getInstance().collection("drivers").document(driver.getDriverId()).update("driverStatus", "NOT AVAILABLE");
                Toast.makeText(holder.itemView.getContext(), "Driver Assigned", Toast.LENGTH_SHORT).show();
                holder.assignBtn.setText("Assigned");
                holder.assignBtn.setBackgroundColor(0xFF800000);
                holder.assignBtn.setTextColor(0xFFFFFFFF);
                holder.assignBtn.setEnabled(false);
                driver.setDriverStatus("NOT AVAILABLE");
                notifyDataSetChanged();
            }
        };

        holder.itemView.setOnClickListener(listener);
        holder.assignBtn.setOnClickListener(listener);
    }

    @Override
    public int getItemCount() {
        return drivers.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView driverPic;
        TextView driverName, assignBtn;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            driverPic = itemView.findViewById(R.id.driverPic);
            driverName = itemView.findViewById(R.id.driverName);
            assignBtn = itemView.findViewById(R.id.assignBtn);
        }
    }
}
