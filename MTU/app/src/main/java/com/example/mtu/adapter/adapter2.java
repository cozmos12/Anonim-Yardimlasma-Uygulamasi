package com.example.mtu.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mtu.databinding.Receylerrow2Binding;
import com.example.mtu.post.post2;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;


public class adapter2 extends RecyclerView.Adapter<adapter2.postholder2>{

    private ArrayList<post2>arrayList2;

    public adapter2(ArrayList<post2> arrayList) {
        this.arrayList2 = arrayList;
    }

    @NonNull
    @Override
    public postholder2 onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Receylerrow2Binding binding2 = Receylerrow2Binding.inflate(LayoutInflater.from(parent.getContext()),parent,false);
        return new postholder2(binding2);
    }

    @Override
    public void onBindViewHolder(@NonNull postholder2 holder, int position) {

        holder.binding2.yorkullaniciadi.setText(arrayList2.get(position).eposta2);
        holder.binding2.kullaniciyorumyorum.setText(arrayList2.get(position).yorum2);
        Picasso.get().load(arrayList2.get(position).resimadresi2).into(holder.binding2.kullaniciresimyorum);
    // telefon kapandı
        //bele getme
        //kalbimizi viran etme

    }

    @Override
    public int getItemCount() {
        System.out.println("deneme"+arrayList2.size());
        return arrayList2.size();
    }

     class postholder2 extends RecyclerView.ViewHolder{

       private Receylerrow2Binding binding2;

        public postholder2(Receylerrow2Binding binding2) {
            super(binding2.getRoot());

            this.binding2=binding2;
        }
    }



}
