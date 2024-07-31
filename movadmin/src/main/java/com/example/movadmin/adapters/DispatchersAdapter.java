package com.example.movadmin.adapters;

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
import com.example.movadmin.utils.Util;

import java.util.ArrayList;

public class DispatchersAdapter extends RecyclerView.Adapter<DispatchersAdapter.ViewHolder> {

    ArrayList<Dispatcher> dispatchers;

    public DispatchersAdapter(ArrayList<Dispatcher> dispatchers) {
        this.dispatchers = dispatchers;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.viewholder_dispatcher, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Dispatcher disp = dispatchers.get(position);
        Glide.with(holder.itemView.getContext()).load(disp.getImgUrl()).into(holder.pic);
        holder.name.setText(disp.getName());
        holder.phone.setText(disp.getPhoneNumber());
        holder.email.setText(disp.getEmail());
        holder.removeBtn.setOnClickListener(view -> Util.showDispatcherConfirmationDialog(holder.itemView.getContext(), disp.getDispatcherId()));
    }

    @Override
    public int getItemCount() {
        return dispatchers.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView pic;
        TextView name, phone, email, removeBtn;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            pic = itemView.findViewById(R.id.customerPic);
            name = itemView.findViewById(R.id.customerName);
            phone = itemView.findViewById(R.id.customerPhone);
            email = itemView.findViewById(R.id.customerEmail);
            removeBtn = itemView.findViewById(R.id.removeDispBtn);
        }
    }
}
