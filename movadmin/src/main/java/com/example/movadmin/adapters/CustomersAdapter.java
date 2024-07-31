package com.example.movadmin.adapters;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.movadmin.R;
import com.example.movadmin.models.Dispatcher;
import com.example.movadmin.models.UserModel;
import com.example.movadmin.utils.Util;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class CustomersAdapter extends RecyclerView.Adapter<CustomersAdapter.ViewHolder> {

    ArrayList<UserModel> customers;

    public CustomersAdapter(ArrayList<UserModel> customers) {
        this.customers = customers;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.viewholder_customer, parent, false);
        return new ViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        UserModel customer = customers.get(position);

        Glide.with(holder.itemView.getContext()).load(customer.getProfilePic()).into(holder.pic);
        holder.name.setText(customer.getUsername());
        holder.phone.setText(customer.getPhoneNo());

        DecimalFormat decimalFormat = new DecimalFormat("#.##");
        String kms = decimalFormat.format(customer.getTotalKms()) + " kms";
        holder.totalKms.setText(kms);

        holder.numOfOrders.setText(customer.getNumOfOrders() + " orders");
    }

    @Override
    public int getItemCount() {
        return customers.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView pic;
        TextView name, phone, totalKms, numOfOrders;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            pic = itemView.findViewById(R.id.customerPic);
            name = itemView.findViewById(R.id.customerName);
            phone = itemView.findViewById(R.id.customerPhone);
            totalKms = itemView.findViewById(R.id.customerTotalKms);
            numOfOrders = itemView.findViewById(R.id.customerNumOfOrders);
        }
    }
}
