package com.example.mtu.view;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.mtu.R;
import com.example.mtu.adapter.adapter;
import com.example.mtu.databinding.ActivityKullaniciekraniBinding;
import com.example.mtu.post.post;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Map;

public class kullaniciekrani extends AppCompatActivity {

    ArrayList<post>postArrayList;
    private FirebaseAuth auth;
    private FirebaseFirestore firebaseFirestore;
    private ActivityKullaniciekraniBinding binding2;
    adapter Adapter;
    public String id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding2=ActivityKullaniciekraniBinding.inflate(getLayoutInflater());
        ConstraintLayout view=binding2.getRoot();
        setContentView(view);

        postArrayList=new ArrayList<>();
        auth=FirebaseAuth.getInstance();
        firebaseFirestore=FirebaseFirestore.getInstance();
        getdata();
        binding2.receyleview.setLayoutManager(new LinearLayoutManager(this));

        Adapter=new adapter(postArrayList);
        binding2.receyleview.setAdapter(Adapter);




    }

//verileri çekme
    public void getdata(){
        //where filtreleme
        firebaseFirestore.collection("post").orderBy("date", Query.Direction.DESCENDING).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if(error!=null){
                    Toast.makeText(kullaniciekrani.this,error.getLocalizedMessage(),Toast.LENGTH_LONG).show();
                }

                if(value!=null){
                    for(DocumentSnapshot snapshot:value.getDocuments()){
                        Map<String,Object>data=snapshot.getData();

                        String eposta=(String) data.get("useremail");
                        String resimadresi=(String) data.get("url");
                        String yorum=(String) data.get("yorum");
                         id=(String)data.get("resimadres") ;
                        System.out.println("id"+id);
                        System.out.println(resimadresi);
                        post Post=new post(eposta,resimadresi,yorum,id);
                        postArrayList.add(Post);

                        System.out.println(eposta);

                    }

                    Adapter.notifyDataSetChanged();
                }
            }


        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater=getMenuInflater();
        menuInflater.inflate(R.menu.menu,menu);

        return super.onCreateOptionsMenu(menu);

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId()==R.id.yükle){

            Intent intent=new Intent(kullaniciekrani.this, yuklemeekrani.class);
            startActivity(intent);


        }else if(item.getItemId()==R.id.çıkış){
            auth.signOut();

            Intent intent=new Intent(kullaniciekrani.this, MainActivity.class);
            startActivity(intent);
            finish();

        }

        return super.onOptionsItemSelected(item);
    }

    public void yorum(View view){
        Intent intent=new Intent(kullaniciekrani.this,yorumekrani.class);
        intent.putExtra("id",id);
        startActivity(intent);
    }
}