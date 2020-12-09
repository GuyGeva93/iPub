package com.example.ipub;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

public class CommentItemListAdapter extends ArrayAdapter<CommentInfo> {

    private Context mContext;
    private int mResource;
    private ArrayList<CommentInfo> commentsList;



    public CommentItemListAdapter(@NonNull Context context, int resource, @NonNull List<CommentInfo> objects) {
        super(context, resource, objects);
        mContext = context;
        mResource = resource;

    }

    public void SetList(ArrayList<CommentInfo> list){
        this.commentsList = list;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        String name = getItem(position).getName();
        String comment = getItem(position).getComment();
        Float rating = getItem(position).getRating();

        LayoutInflater inflater = LayoutInflater.from(mContext);
        convertView = inflater.inflate(mResource , parent , false);

        TextView viewName = (TextView) convertView.findViewById(R.id.comment_item_name);
        TextView viewComment = (TextView) convertView.findViewById(R.id.comment_item_comment);
        RatingBar viewRating = (RatingBar) convertView.findViewById(R.id.comment_item_rating_bar);

        viewName.setText(name);
        viewComment.setText(comment);
        viewRating.setRating(rating);

        return convertView;

    }

}
