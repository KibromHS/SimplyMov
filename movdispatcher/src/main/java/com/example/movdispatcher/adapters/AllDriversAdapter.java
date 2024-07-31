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

import java.text.DecimalFormat;
import java.util.ArrayList;

public class AllDriversAdapter extends RecyclerView.Adapter<AllDriversAdapter.ViewHolder> {
    ArrayList<Driver> drivers;

    public AllDriversAdapter(ArrayList<Driver> drivers) {
        this.drivers = drivers;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.viewholder_all_drivers, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Driver driver = drivers.get(position);
        Glide.with(holder.itemView.getContext()).load(driver.getDriverPic()).into(holder.driverImage);
        holder.name.setText(driver.getDriverName());
        holder.email.setText(driver.getDriverEmail());
        holder.phone.setText(driver.getPhoneNumber());
        holder.status.setText(driver.getDriverStatus());
        holder.size.setText(driver.getTruckSize());
        holder.totalOrders.setText(String.valueOf(driver.getNumOfOrders()));

        DecimalFormat decimalFormat = new DecimalFormat("#.##");
        String kms = decimalFormat.format(driver.getTotalKms()) + " Kms";
        holder.totalKms.setText(kms);
    }

    @Override
    public int getItemCount() {
        return drivers.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView driverImage;
        TextView name, email, phone, status, size, totalOrders, totalKms;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            driverImage = itemView.findViewById(R.id.driversImage);
            name = itemView.findViewById(R.id.driversName);
            email = itemView.findViewById(R.id.driversEmail);
            phone = itemView.findViewById(R.id.driversPhone);
            status = itemView.findViewById(R.id.driversStatus);
            size = itemView.findViewById(R.id.driversTruckSize);
            totalOrders = itemView.findViewById(R.id.driversTotalOrders);
            totalKms = itemView.findViewById(R.id.driversTotalKms);
        }
    }
}
