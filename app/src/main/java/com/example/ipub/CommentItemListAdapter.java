package com.example.ipub;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

/*comment adapter for the rating comments.*/

public class CommentItemListAdapter extends ArrayAdapter<CommentInfo> {

    private Context mContext;
    private int mResource;
    private ArrayList<CommentInfo> commentsList;
    private String pub_name;
    private TinyDB tinyDB;

    public void setPub_name(String pub_name) {
        this.pub_name = pub_name;
    }


    public CommentItemListAdapter(@NonNull Context context, int resource, @NonNull List<CommentInfo> objects) {
        super(context, resource, objects);
        mContext = context;
        mResource = resource;
        tinyDB = new TinyDB(getContext());

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
        long timeStamp = getItem(position).getTimeStamp();

        LayoutInflater inflater = LayoutInflater.from(mContext);
        convertView = inflater.inflate(mResource , parent , false);

        String time = calculateTime(timeStamp);

        TextView viewName = convertView.findViewById(R.id.comment_item_name);
        TextView viewComment = convertView.findViewById(R.id.comment_item_comment);
        TextView viewTimeStamp = convertView.findViewById(R.id.comment_item_timestamp);
        RatingBar viewRating = convertView.findViewById(R.id.comment_item_rating_bar);
        ImageView delete_icon = convertView.findViewById(R.id.comment_item_delete_comment);

        if(timeStamp == tinyDB.getLong(pub_name , 0)){
            delete_icon.setVisibility(View.VISIBLE);
            delete_icon.setClickable(true);
            delete_icon.setOnClickListener(getItem(position).getBtnDeleteComment());
        }

        viewName.setText(name);
        viewComment.setText(comment);
        viewTimeStamp.setText(time);
        viewRating.setRating(rating);

        return convertView;

    }

    public String calculateTime(long commentTime){

        long different =    System.currentTimeMillis() - commentTime;

        long secondsInMilli = 1000;
        long minutesInMilli = secondsInMilli * 60;
        long hoursInMilli = minutesInMilli * 60;
        long daysInMilli = hoursInMilli * 24;
        long weeksInMilli = daysInMilli * 7;

        long elapsedWeeks = different / weeksInMilli;
        different = different % weeksInMilli;

        long elapsedDays = different / daysInMilli;
        different = different % daysInMilli;

        long elapsedHours = different / hoursInMilli;
        different = different % hoursInMilli;

        long elapsedMinutes = different / minutesInMilli;
        different = different % minutesInMilli;

        long elapsedSeconds = different / secondsInMilli;

        String output = "";
        if(elapsedWeeks > 0){output= "לפני " + elapsedWeeks + " שבועות"; return output;}
        if (elapsedDays > 0){ output = "לפני " + elapsedDays + " ימים"; return output;}
        if ( elapsedHours > 0) {output ="לפני "+ elapsedHours + " שעות"; return output;}
        if ( elapsedMinutes > 0) {output ="לפני "+ elapsedMinutes + " דקות"; return output;}
        if ( elapsedSeconds > 0) {output = "לפני " + elapsedSeconds + " שניות"; return output;}

        return output;

    }


}
