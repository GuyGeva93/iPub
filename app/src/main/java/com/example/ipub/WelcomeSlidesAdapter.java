package com.example.ipub;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class WelcomeSlidesAdapter extends RecyclerView.Adapter<WelcomeSlidesAdapter.WelcomeSlidesViewHolder> {

    private List<WelcomeSlidesItem> slidesList;

    public WelcomeSlidesAdapter(List<WelcomeSlidesItem> slidesList) {
        this.slidesList = slidesList;
    }

    @NonNull
    @Override
    public WelcomeSlidesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new WelcomeSlidesViewHolder(
                LayoutInflater.from(parent.getContext()).inflate(
                        R.layout.item_container_slides , parent , false
                )
        );
    }

    @Override
    public void onBindViewHolder(@NonNull WelcomeSlidesViewHolder holder, int position) {
        holder.setSlideDate(slidesList.get(position));
    }

    @Override
    public int getItemCount() {
        return slidesList.size();
    }

    class WelcomeSlidesViewHolder extends RecyclerView.ViewHolder{
        private TextView textTitle;
        private TextView textDescription;
        private ImageView image;

         WelcomeSlidesViewHolder(@NonNull View itemView) {
            super(itemView);
            textTitle = itemView.findViewById(R.id.WelcomeSlidesTextViewTitle);
            textDescription = itemView.findViewById(R.id.WelcomeSlidesTextViewDescription);
            image = itemView.findViewById(R.id.WelcomeSlidesImage);
        }
        void setSlideDate(WelcomeSlidesItem item){
            textTitle.setText(item.getTitle());
            textDescription.setText(item.getDescription());
            image.setImageResource(item.getImage());
        }
    }
}
