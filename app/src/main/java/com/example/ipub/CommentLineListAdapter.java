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

import java.util.List;

public class CommentLineListAdapter extends ArrayAdapter<CommentInfo> {

    private List<CommentInfo> commentsList;
    private Context mContext;
    private int mResource;


    public CommentLineListAdapter(@NonNull Context context, int resource, @NonNull List<CommentInfo> objects) {
        super(context, resource, objects);
        mContext = context;
        mResource = resource;
        commentsList = objects;
    }



    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        String name = getItem(position).getName();
        String comment = getItem(position).getComment();
        Float rating = getItem(position).getRating();


        CommentInfo temp = new CommentInfo(name , comment , rating);

        LayoutInflater inflater = LayoutInflater.from(mContext);
        convertView = inflater.inflate(mResource , parent , false);

        TextView nameView = (TextView) convertView.findViewById(R.id.name_comment_line);
        TextView commentView = (TextView) convertView.findViewById(R.id.comment_comment_line);
        RatingBar ratingBarView = (RatingBar) convertView.findViewById(R.id.rating_bar_comment_line);

        nameView.setText(name);
        commentView.setText(comment);
        ratingBarView.setRating(rating);

        return convertView;

    }

    public void setCommentsList(List<CommentInfo> list){
        commentsList = list;
    }
}
