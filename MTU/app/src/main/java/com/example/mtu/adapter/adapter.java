package com.example.mtu.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mtu.databinding.ReceylerowBinding;
import com.example.mtu.post.post;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class adapter extends RecyclerView.Adapter<adapter.postholder> {

    private ArrayList<post>poarrayList;

    public adapter(ArrayList<post> arrayList) {
        this.poarrayList = arrayList;
    }


    @NonNull
    @Override
    public postholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
       ReceylerowBinding receylerowBinding=ReceylerowBinding.inflate(LayoutInflater.from(parent.getContext()),parent,false);
       return new postholder(receylerowBinding);

    }

    @Override
    public void onBindViewHolder(@NonNull postholder holder, int position) {

        holder.receylerowBinding.kullaniciadi.setText(poarrayList.get(position).eposta);

        holder.receylerowBinding.kullanCYorum.setText(poarrayList.get(position).yorum);
        Picasso.get().load(poarrayList.get(position).resimadresi).into(holder.receylerowBinding.kullanCResim);

    }

    @Override
    public int getItemCount() {
        return poarrayList.size();
    }

    class postholder extends RecyclerView.ViewHolder{
        ReceylerowBinding receylerowBinding;
        public postholder(ReceylerowBinding receylerowBinding) {
            super(receylerowBinding.getRoot());
            this.receylerowBinding = receylerowBinding;
        }
    }
}
