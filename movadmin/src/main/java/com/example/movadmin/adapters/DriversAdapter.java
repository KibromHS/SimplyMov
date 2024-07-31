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
import com.example.movadmin.R;
import com.example.movadmin.models.Dispatcher;
import com.example.movadmin.models.Driver;
import com.example.movadmin.utils.Util;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class DriversAdapter extends RecyclerView.Adapter<DriversAdapter.ViewHolder> {

    ArrayList<Driver> drivers;

    public DriversAdapter(ArrayList<Driver> drivers) {
        this.drivers = drivers;
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
        Glide.with(holder.itemView.getContext()).load(driver.getDriverPic()).into(holder.pic);
        holder.name.setText(driver.getDriverName());
        holder.phone.setText(driver.getPhoneNumber());
        holder.email.setText(driver.getDriverEmail());

        DecimalFormat decimalFormat = new DecimalFormat("#.##");
        String kms = decimalFormat.format(driver.getTotalKms()) + " kms";
        holder.totalKms.setText(kms);

        holder.status.setText(driver.getDriverStatus());
        if (driver.getDriverStatus().equals("AVAILABLE")) {
            holder.status.setBackgroundColor(0xFF00FF00);
        } else {
            holder.status.setBackgroundColor(0xFFFF0000);
        }

        holder.size.setText(driver.getTruckSize());
        holder.ratingBar.setRating((float) driver.getRating());

        holder.removeBtn.setOnClickListener(view -> Util.showDriverConfirmationDialog(holder.itemView.getContext(), driver.getDriverId()));
    }

    @Override
    public int getItemCount() {
        return drivers.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView pic;
        TextView name, phone, email, removeBtn, status, size, totalKms;
        RatingBar ratingBar;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            pic = itemView.findViewById(R.id.driverPic);
            name = itemView.findViewById(R.id.driverName);
            phone = itemView.findViewById(R.id.driverPhone);
            email = itemView.findViewById(R.id.driverEmail);
            removeBtn = itemView.findViewById(R.id.removeDispBtn);
            status = itemView.findViewById(R.id.driverStatus);
            size = itemView.findViewById(R.id.driverTruckSize);
            totalKms = itemView.findViewById(R.id.driverTotalKms);
            ratingBar = itemView.findViewById(R.id.driverRatingBar);
        }
    }
}
