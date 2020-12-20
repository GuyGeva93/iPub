package com.example.ipub;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.mail.Quota;

public class GalleryActivity extends AppCompatActivity {

    String pub_name;
    GridView gridView;
    ImagesListAdapter adapter;
    List<Integer> imagesList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);
        initVariables();



        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                int item_pos = imagesList.get(position);
                ShowDialogBox(item_pos);
            }
        });

    }

    private void initVariables() {
        pub_name = getIntent().getStringExtra("pub_name");
        gridView = findViewById(R.id.myGrid);
        imagesList = new ArrayList<>(Arrays.asList(
                R.drawable.artstreet_logo ,
                R.drawable.brown_logo,
                R.drawable.elispub_logo ,
                R.drawable.ilkabar_logo
        ));
        adapter = new ImagesListAdapter(imagesList , this);
        gridView.setAdapter(adapter);

    }

    public void ShowDialogBox(final int item_pos){

        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.custom_dialog);

        ImageView Image = dialog.findViewById(R.id.img);
        Image.setImageResource(item_pos);


        dialog.show();

    }
}