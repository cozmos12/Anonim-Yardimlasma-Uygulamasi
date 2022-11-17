package com.example.mtu.view;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Toast;

import com.example.mtu.adapter.adapter2;
import com.example.mtu.databinding.ActivityYorumekraniBinding;
import com.example.mtu.post.post2;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class yorumekrani extends AppCompatActivity {

    Uri resimadresi;
    ActivityResultLauncher<Intent> activityResultLauncher;
    ActivityResultLauncher<String> permissionLauncher;
    private ActivityYorumekraniBinding binding;
    private FirebaseStorage firebaseStorage;
    private FirebaseAuth auth;
    private FirebaseFirestore firebaseFirestore;
    private StorageReference storageReference;
    ArrayList<post2> postArrayList2;
    adapter2 Adapter2;
    post2 Post2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding= ActivityYorumekraniBinding.inflate(getLayoutInflater());


        View view=binding.getRoot();
        postArrayList2=new ArrayList<>();
        setContentView(view);
        firebaseStorage=FirebaseStorage.getInstance();
        auth=FirebaseAuth.getInstance();
        firebaseFirestore=FirebaseFirestore.getInstance();
        storageReference=firebaseStorage.getReference();
        yüklemeayarlarıyorum();
        getdata();

        Adapter2=new adapter2(postArrayList2);
        LinearLayoutManager manager = new LinearLayoutManager(this);
        binding.receyleview2.setLayoutManager(manager);
        binding.receyleview2.setHasFixedSize(true);
        binding.receyleview2.setAdapter(Adapter2);



    }

    public void yorumresim(View view) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            if(ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.READ_EXTERNAL_STORAGE)) {
                Snackbar.make(view,"Permission needed for gallery", Snackbar.LENGTH_INDEFINITE).setAction("Give Permission", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE);

                    }
                }).show();
            } else {
                permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE);
            }
        } else {
            Intent intentToGallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            activityResultLauncher.launch(intentToGallery);


        }

    }

    public void paylaşyorum(View view){

        if(resimadresi!=null){

            UUID uuid=UUID.randomUUID();
            String resim="resim/"+uuid+".jpg";

            storageReference.child(resim).putFile(resimadresi).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                    StorageReference newreference=firebaseStorage.getReference(resim);
                    newreference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {

                            String dowloadurl=uri.toString();

                            String yorum=binding.yorumekle.getText().toString();
                            String id=resim;

                            FirebaseUser user=auth.getCurrentUser();
                            String emil=user.getEmail();

                            HashMap<String,Object> postdata=new HashMap<>();
                            postdata.put("useremail2",emil);
                            postdata.put("url2",dowloadurl);
                            postdata.put("yorum2",yorum);
                            postdata.put("date2", FieldValue.serverTimestamp());
                            postdata.put("resimadres",id);

                            firebaseFirestore.collection("post2").add(postdata).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                @Override
                                public void onSuccess(DocumentReference documentReference) {



                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(yorumekrani.this,e.getLocalizedMessage(),Toast.LENGTH_LONG).show();
                                }
                            });


                        }
                    });

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                    Toast.makeText(yorumekrani.this,e.getLocalizedMessage(),Toast.LENGTH_LONG).show();
                }
            });
        }

    }

    private void yüklemeayarlarıyorum(){

        //izin verildiğinde ne yapılacağı

        activityResultLauncher=registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
                if(result.getResultCode()==RESULT_OK){
                    Intent intentfromresult=result.getData();
                    if(intentfromresult!=null){
                        resimadresi=intentfromresult.getData();
                        binding.yorumresim.setImageURI(resimadresi);
                    }
                }
            }
        });

        permissionLauncher=registerForActivityResult(new ActivityResultContracts.RequestPermission(), new ActivityResultCallback<Boolean>() {
            @Override
            public void onActivityResult(Boolean result) {
                if(result){
                    Intent intentToGallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

                    activityResultLauncher.launch(intentToGallery);
                }else{
                    Toast.makeText(yorumekrani.this,"izin gerkli",Toast.LENGTH_LONG).show();
                }
            }
        });

    }

    public void getdata(){
        //where filtreleme
        firebaseFirestore.collection("post2").orderBy("date2", Query.Direction.DESCENDING).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if(error!=null){
                    Toast.makeText(yorumekrani.this,error.getLocalizedMessage(),Toast.LENGTH_LONG).show();
                }

                if(value!=null){
                    for(DocumentSnapshot snapshot:value.getDocuments()){
                        Map<String,Object> data=snapshot.getData();


                        String eposta2=(String) data.get("useremail2");
                        String resimadresi2=(String) data.get("url2");
                        String yorum2=(String) data.get("yorum2");


                        Post2=new post2(eposta2,resimadresi2,yorum2);
                        postArrayList2.add(Post2);

                        System.out.println("yorum"+eposta2);
                        System.out.println(resimadresi2);
                        System.out.println(yorum2);


                    }
                    Adapter2.notifyDataSetChanged();


                }
            }


        });
    }


}