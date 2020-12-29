package com.example.ipub;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.drawable.AdaptiveIconDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.DrawableContainer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ramotion.foldingcell.FoldingCell;
//import com.ramotion.foldingcell.examples.R;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import org.w3c.dom.Text;

/**
 * Simple example of ListAdapter for using with Folding Cell
 * Adapter holds indexes of unfolded elements for correct work with default reusable views behavior
 */
@SuppressWarnings({"WeakerAccess", "unused"})
public class FoldingCellListAdapter extends ArrayAdapter<Pub> implements Filterable {

    private List<Pub> pubList;
    private List<Pub> fullPubList;
    private List<Pub> sortPubList;
    FirebaseDatabase database;
    DatabaseReference mRef;
    int count = 0;
    int sum = 0;

    private HashSet<Integer> unfoldedIndexes = new HashSet<>();

    public FoldingCellListAdapter(Context context, List<Pub> objects) {
        super(context, 0, objects);
        this.pubList = objects;
    }

    public void setFullPubList(List<Pub> fullPubList) {
        this.fullPubList = fullPubList;
    }

    public void setSortPubList(List<Pub> sortPubList) {
        this.sortPubList = sortPubList;
    }


    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        // get item for selected view
        Pub pub = getItem(position);
        // if cell is exists - reuse it, if not - create the new one from resource
        FoldingCell cell = (FoldingCell) convertView;
        final ViewHolder viewHolder;
        if (cell == null) {
            viewHolder = new ViewHolder();
            LayoutInflater vi = LayoutInflater.from(getContext());
            cell = (FoldingCell) vi.inflate(R.layout.cell, parent, false);
            // binding view parts to view holder
            viewHolder.TitleImage = cell.findViewById(R.id.title_image);
            viewHolder.Name = cell.findViewById(R.id.pub_name);
            viewHolder.distance = cell.findViewById(R.id.title_distance);
            viewHolder.Address = cell.findViewById(R.id.pub_address);
            viewHolder.Telephone = cell.findViewById(R.id.title_Phone);
            viewHolder.Kosher = cell.findViewById(R.id.title_kosher);
            viewHolder.contentPubName = cell.findViewById(R.id.content_headline_pubName);
            viewHolder.contentMainImage = cell.findViewById(R.id.content_head_image);

            //buttons
            viewHolder.contentRequestBtn = cell.findViewById(R.id.content_make_call_btn);
            viewHolder.btnGoToWebsite = cell.findViewById(R.id.btn_content_website);
            viewHolder.btnNavigateToPub = cell.findViewById(R.id.btn_content_navigate);
            viewHolder.btnAddToFavorites = cell.findViewById(R.id.btn_content_favorites);
            viewHolder.btnComment = cell.findViewById(R.id.btn_comments);
            viewHolder.btnRatePub = cell.findViewById(R.id.btn_rate);
            viewHolder.btnGallery = cell.findViewById(R.id.btn_gallery);

            //Opening hours
            viewHolder.sunday_hours = cell.findViewById(R.id.sunday_hours);
            viewHolder.monday_hours = cell.findViewById(R.id.monday_hours);
            viewHolder.tuesday_hours = cell.findViewById(R.id.tuesday_hours);
            viewHolder.wednesday_hours = cell.findViewById(R.id.wednesday_hours);
            viewHolder.thursday_hours = cell.findViewById(R.id.thursday_hours);
            viewHolder.friday_hours = cell.findViewById(R.id.friday_hours);
            viewHolder.saturday_hours = cell.findViewById(R.id.saturday_hours);
            viewHolder.ratingBar = cell.findViewById(R.id.ratingBar);

            cell.setTag(viewHolder);
        } else {
            // for existing cell set valid valid state(without animation)
            if (unfoldedIndexes.contains(position)) {
                cell.unfold(true);
            } else {
                cell.fold(true);
            }
            viewHolder = (ViewHolder) cell.getTag();
        }

        if (pub == null)
            return cell;

        // set title logo image and match to the correct pub
        String publogo = pub.getTitleName().toLowerCase().replace(" ", "").replace("-", "").replace("'", "") + "_logo";
        Resources Res = this.getContext().getResources();
        int drawableId = Res.getIdentifier(publogo, "drawable", "com.example.ipub");
        Drawable drawable = ContextCompat.getDrawable(getContext(), drawableId);
        viewHolder.TitleImage.setBackground(drawable);

        // set content logo image and match to the correct pub
        Drawable drawable1 = ContextCompat.getDrawable(getContext(), drawableId);
        viewHolder.contentMainImage.setBackground(drawable1);


        // bind data from selected element to view through view holder
        viewHolder.Name.setText(pub.getName());
        viewHolder.distance.setText(String.valueOf(pub.getDistance()));
        viewHolder.distance.setText(new DecimalFormat(".#").format(pub.getDistance()) + " קילומטר ממך");
        viewHolder.Address.setText(pub.getAddress());
        viewHolder.Telephone.setText(pub.getTelephone());
        viewHolder.Kosher.setText(pub.getKosher());
        viewHolder.contentPubName.setText(pub.getName());
        viewHolder.sunday_hours.setText(pub.getSunday());
        viewHolder.monday_hours.setText(pub.getMonday());
        viewHolder.tuesday_hours.setText(pub.getTuesday());
        viewHolder.wednesday_hours.setText(pub.getWednesday());
        viewHolder.thursday_hours.setText(pub.getThursday());
        viewHolder.friday_hours.setText(pub.getFriday());
        viewHolder.saturday_hours.setText(pub.getSaturday());
        viewHolder.ratingBar.setRating(pub.getRating());

        // set custom btn handler for list item from that item
        if (pub.getRequestBtnClickListener() != null) {
            viewHolder.contentRequestBtn.setOnClickListener(pub.getRequestBtnClickListener());
        }

        // set custom btn handler for list item from that item
        if (pub.getBtnGoToWebsite() != null) {
            viewHolder.btnGoToWebsite.setOnClickListener(pub.getBtnGoToWebsite());
        }

        if (pub.getBtnNavigateTopub() != null) {
            viewHolder.btnNavigateToPub.setOnClickListener(pub.getBtnNavigateTopub());
        }

        if (pub.getBtnNavigateTopub() != null) {
            viewHolder.btnAddToFavorites.setOnClickListener(pub.getBtnAddToFavorites());
        }

        if (pub.getBtnComments() != null) {
            viewHolder.btnComment.setOnClickListener(pub.getBtnComments());
        }

        if (pub.getBtnRatePub() != null) {
            viewHolder.btnRatePub.setOnClickListener(pub.getBtnRatePub());
        }

        if(pub.getBtnGallery() !=null){
            viewHolder.btnGallery.setOnClickListener(pub.getBtnGallery());
        }

        return cell;
    }

    // simple methods for register cell state changes
    public void registerToggle(int position) {
        if (unfoldedIndexes.contains(position))
            registerFold(position);
        else
            registerUnfold(position);
    }

    public void registerFold(int position) {
        unfoldedIndexes.remove(position);
    }

    public void registerUnfold(int position) {
        unfoldedIndexes.add(position);
    }


    // View lookup cache
    private static class ViewHolder {

        //Card title variables
        TextView Name;
        TextView TitleImage;
        TextView Address;
        TextView Telephone;
        TextView Kosher;
        TextView distance;

        //Card content variables
        TextView contentPubName;
        ImageView contentMainImage;

        //Opening hours
        TextView sunday_hours;
        TextView monday_hours;
        TextView tuesday_hours;
        TextView wednesday_hours;
        TextView thursday_hours;
        TextView friday_hours;
        TextView saturday_hours;


        //buttons
        TextView contentRequestBtn;
        TextView btnComment;
        TextView btnRatePub;
        TextView btnGallery;
        ImageView btnGoToWebsite;
        ImageView btnNavigateToPub;
        ImageView btnAddToFavorites;


        RatingBar ratingBar;

    }

    @NonNull
    @Override
    public Filter getFilter() {
        return pubFilter;
    }

    private Filter pubFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<Pub> filteredList = new ArrayList<>();
            if (constraint == null || constraint.length() == 0) {
                filteredList.addAll(fullPubList);
            } else {
                String filterPattern = constraint.toString().toLowerCase();
                for (Pub pub : fullPubList) {
                    if (pub.getName().contains(filterPattern)) {
                        filteredList.add(pub);
                    }
                }
            }
            FilterResults results = new FilterResults();
            results.values = filteredList;
            results.count = filteredList.size();
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {

            pubList.clear();
            pubList.addAll((List) results.values);
            notifyDataSetChanged();

        }
    };


    public Filter getSpinnerFilter() {
        return pubSpinnerFilter;
    }

    private Filter pubSpinnerFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<Pub> filteredList = new ArrayList<>();
            String kosher = String.valueOf(constraint.toString().subSequence(0, constraint.toString().indexOf("-")));
            String area = String.valueOf(constraint.toString().subSequence(constraint.toString().indexOf("-") + 1, constraint.toString().indexOf("+")));
            String stars = constraint.toString().substring(constraint.toString().indexOf("+") + 1);
            if (kosher.equals("כשרות") && area.equals("איזור") && stars.equals("דירוג")) {
                fullPubList.sort(new DistanceComparator());
                filteredList.addAll(fullPubList);
            } else {
                if (kosher.equals("כשרות") && !area.equals("איזור") && stars.equals("דירוג")) {
                    for (Pub pub : fullPubList) {
                        if (pub.getArea().equals(area)) {
                            filteredList.add(pub);
                        }
                    }
                } else if (!kosher.equals("כשרות") && area.equals("איזור") && stars.equals("דירוג")) {
                    for (Pub pub : fullPubList) {
                        if (pub.getKosher().equals(kosher)) {
                            filteredList.add(pub);
                        }
                    }
                } else if (kosher.equals("כשרות") && area.equals("איזור") && !stars.equals("דירוג")) {
                    for (Pub pub : fullPubList) {
                        if (String.valueOf((int)pub.getRating()).equals(stars)) {
                            filteredList.add(pub);
                        }
                    }
                } else if (!kosher.equals("כשרות") && area.equals("איזור") && !stars.equals("דירוג")) {
                    for (Pub pub : fullPubList) {
                        if (String.valueOf((int)pub.getRating()).equals(stars) && pub.getKosher().equals(kosher)) {
                            filteredList.add(pub);
                        }
                    }
                } else if (kosher.equals("כשרות") && !area.equals("איזור") && !stars.equals("דירוג")) {
                    for (Pub pub : fullPubList) {
                        if (String.valueOf((int)pub.getRating()).equals(stars) && pub.getArea().equals(area)) {
                            filteredList.add(pub);
                        }
                    }

                } else if (!kosher.equals("כשרות") && !area.equals("איזור") && stars.equals("דירוג")) {
                    for (Pub pub : fullPubList) {
                        if (pub.getKosher().equals(kosher) && pub.getArea().equals(area)) {
                            filteredList.add(pub);
                        }
                    }
                } else {
                    for (Pub pub : fullPubList) {
                        if (pub.getKosher().equals(kosher) && pub.getArea().equals(area) && String.valueOf((int)(pub.getRating())).equals(stars)) {
                            filteredList.add(pub);
                        }
                    }
                }
            }
            FilterResults results = new FilterResults();
            filteredList.sort(new DistanceComparator());
            results.values = filteredList;
            results.count = filteredList.size();
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {

            pubList.clear();
            pubList.addAll((List) results.values);
            notifyDataSetChanged();

        }

    };


}
