package com.example.ipub;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.mail.Quota;

public class GalleryActivity extends AppCompatActivity {

    GridView gridView;
    ImagesListAdapter adapter;
    StorageReference storageRef;
    List<String> uriList;
    String tempUriString;
    String pubName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);
        initVariables();
        downloadImages();



        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String uriString = uriList.get(position);
                ShowDialogBox(uriString);
            }
        });

    }

    private void downloadImages() {

        StorageReference MStorageRef = storageRef.child("images/" + pubName);

        MStorageRef.listAll()
                .addOnSuccessListener(new OnSuccessListener<ListResult>() {
                    @Override
                    public void onSuccess(ListResult listResult) {
//                        for (StorageReference prefix : listResult.getPrefixes()) {
//                            // All the prefixes under listRef.
//                            // You may call listAll() recursively on them.
//                        }

                        for (StorageReference item : listResult.getItems()) {
                            //  All the items under listRef.
                            item.getDownloadUrl()
                                    .addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri) {
                                            int i = 0;
                                            tempUriString = uri.toString();
                                            URI tempURI = URI.create(tempUriString);
                                            String validURLString = tempURI.toASCIIString();
//                                            Picasso.get().load(validURLString).into(imageView);
//                                            Uri myUri = Uri.parse(validURLString);
                                            uriList.add(validURLString);
//                                          imageView.setImageURI(myUri);

                                        }


                                    }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                                @Override
                                public void onComplete(@NonNull Task<Uri> task) {
                                    adapter = new ImagesListAdapter(uriList , getApplicationContext());
                                    gridView.setAdapter(adapter);
                                }
                            });


                        }


                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Uh-oh, an error occurred!
                    }
                });


    }

    private void initVariables() {
        gridView = findViewById(R.id.myGrid);
        pubName = getIntent().getStringExtra("pub_name");
        storageRef = FirebaseStorage.getInstance().getReference();
        uriList = new ArrayList<>();

//        adapter = new ImagesListAdapter(imagesList , this);
//        gridView.setAdapter(adapter);

    }

    public void ShowDialogBox(String uriString){

        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.custom_dialog);

        ImageView Image = dialog.findViewById(R.id.img);
        Picasso.get().load(uriString).into(Image);


        dialog.show();

    }
}