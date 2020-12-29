package com.example.ipub;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;


import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class GalleryManagement extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemClickListener {

    StorageReference storageRef;
    String pubName;
    Button uploadImageBtn;
    Uri imageUri;
    GridView gridView;
    List<String> uriList;
    String tempUriString;
    ImagesListAdapter adapter;


    private static final int IMAGE_PICK_CODE = 1000;
    private static final int PERMISSION_CODE = 1001;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery_mangement);


        initVariables();
        initViews();
        downloadImages();

        uploadImageBtn.setOnClickListener(this);
        gridView.setOnItemClickListener(this);

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
                                            uriList.add(validURLString);

                                        }


                                    }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                                @Override
                                public void onComplete(@NonNull Task<Uri> task) {
                                    adapter = new ImagesListAdapter(uriList, getApplicationContext());
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
        storageRef = FirebaseStorage.getInstance().getReference();
        pubName = getIntent().getStringExtra("pubName");
        uriList = new ArrayList<>();


    }

    private void initViews() {
        uploadImageBtn = findViewById(R.id.uploadImageBtn);
        gridView = findViewById(R.id.gallery_management_gridView);

    }

    public void pickImageFromGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, IMAGE_PICK_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PERMISSION_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //permission was granted
                    pickImageFromGallery();
                } else {
                    //permission denied.
                }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == IMAGE_PICK_CODE && data != null && data.getData() != null) {
            imageUri = data.getData();
            uploadImage();
        }

    }

    private void uploadImage() {

        final String randomKey = UUID.randomUUID().toString();

        String path = "images/" + pubName + "/" + randomKey;
        StorageReference mStorageRef = storageRef.child(path);

        mStorageRef.putFile(imageUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Snackbar.make(findViewById(android.R.id.content), "התמונה עלתה בהצלחה", Snackbar.LENGTH_SHORT).show();
                        uriList.clear();
                        downloadImages();

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        Snackbar.make(findViewById(android.R.id.content), "העלאה נכשלה", Snackbar.LENGTH_SHORT).show();

                    }
                });
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.uploadImageBtn:
                checkOrRequestPermission();
                break;

        }

    }

    private void checkOrRequestPermission() {
        //check runtime permission
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
                //permission not granted, request it.
                String[] permissions = {Manifest.permission.READ_EXTERNAL_STORAGE};
                //show popup for runtime permission
                requestPermissions(permissions, PERMISSION_CODE);
            } else {
                //permission already granted
                pickImageFromGallery();
            }
        } else {
            //system os is less then marshmallow
            pickImageFromGallery();
        }
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        String uriString = uriList.get(position);
        ShowDialogBox(uriString);


    }

    private void ShowDialogBox(final String uriString) {
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.custom_dialog2);

        ImageView Image = dialog.findViewById(R.id.img);
        Picasso.get().load(uriString).into(Image);
        Button btn = dialog.findViewById(R.id.delete_image_from_storage);


        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StorageReference photoRef = FirebaseStorage.getInstance().getReferenceFromUrl(uriString);
                photoRef.delete();
                Toast.makeText(getApplicationContext() ,"התמונה נמחקה"  ,Toast.LENGTH_SHORT ).show();
                uriList.remove(uriString);
                adapter.notifyDataSetChanged();
                gridView.setAdapter(adapter);
                dialog.cancel();


            }
        });


        dialog.show();
    }
}