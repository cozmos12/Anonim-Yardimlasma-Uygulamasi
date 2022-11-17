package com.example.mtu.view;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Toast;

import com.example.mtu.databinding.ActivityYuklemeekraniBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.UUID;

public class yuklemeekrani extends AppCompatActivity {

    Uri resimadresi;
    ActivityResultLauncher<Intent> activityResultLauncher;
    ActivityResultLauncher<String> permissionLauncher;
    private ActivityYuklemeekraniBinding binding;
   private FirebaseStorage firebaseStorage;
   private FirebaseAuth auth;
   private FirebaseFirestore firebaseFirestore;
   private StorageReference storageReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityYuklemeekraniBinding.inflate(getLayoutInflater());


        View view=binding.getRoot();
        setContentView(view);
        firebaseStorage=FirebaseStorage.getInstance();
        auth=FirebaseAuth.getInstance();
        firebaseFirestore=FirebaseFirestore.getInstance();
        storageReference=firebaseStorage.getReference();
        yüklemeayarları();

    }
//iznleri alma
public void resimseç(View view) {
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

    public void paylaş(View view){

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

                            String yorum=binding.commenttext.getText().toString();

                            FirebaseUser user=auth.getCurrentUser();
                            String emil=user.getEmail();
                            String id=resim;

                            HashMap<String,Object> postdata=new HashMap<>();
                            postdata.put("useremail",emil);
                            postdata.put("url",dowloadurl);
                            postdata.put("yorum",yorum);
                            postdata.put("date", FieldValue.serverTimestamp());
                            postdata.put("resimadres",id);

                            firebaseFirestore.collection("post").add(postdata).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                @Override
                                public void onSuccess(DocumentReference documentReference) {

                                    Intent intent=new Intent(yuklemeekrani.this, kullaniciekrani.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    startActivity(intent);

                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(yuklemeekrani.this,e.getLocalizedMessage(),Toast.LENGTH_LONG).show();
                                }
                            });


                        }
                    });

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                    Toast.makeText(yuklemeekrani.this,e.getLocalizedMessage(),Toast.LENGTH_LONG).show();
                }
            });
        }

    }
//galeriden gelen adresi alma
    private void yüklemeayarları(){

        //izin verildiğinde ne yapılacağı

        activityResultLauncher=registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
                if(result.getResultCode()==RESULT_OK){
                    Intent intentfromresult=result.getData();
                    if(intentfromresult!=null){
                        resimadresi=intentfromresult.getData();
                        binding.imageView.setImageURI(resimadresi);
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
                    Toast.makeText(yuklemeekrani.this,"izin gerkli",Toast.LENGTH_LONG).show();
                }
            }
        });

    }
}