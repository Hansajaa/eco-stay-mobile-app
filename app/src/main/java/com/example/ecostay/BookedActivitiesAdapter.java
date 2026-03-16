package com.example.ecostay;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class BookedActivitiesAdapter extends RecyclerView.Adapter<BookedActivitiesAdapter.BookedActivityViewHolder> {

    private List<BookedActivity> activityList;

    public BookedActivitiesAdapter(List<BookedActivity> activityList) {
        this.activityList = activityList;
    }

    @NonNull
    @Override
    public BookedActivityViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_booked_activity, parent, false);
        return new BookedActivityViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BookedActivityViewHolder holder, int position) {
        BookedActivity activity = activityList.get(position);
        holder.tvActivityName.setText(activity.getName());
        holder.tvBookingDate.setText("Date: " + activity.getDate());
        holder.tvStartTime.setText(activity.getStartTime());
        holder.tvPrice.setText(activity.getPrice());
    }

    @Override
    public int getItemCount() {
        return activityList.size();
    }

    public static class BookedActivityViewHolder extends RecyclerView.ViewHolder {
        TextView tvActivityName, tvBookingDate, tvStartTime, tvPrice;

        public BookedActivityViewHolder(@NonNull View itemView) {
            super(itemView);
            tvActivityName = itemView.findViewById(R.id.tvActivityName);
            tvBookingDate = itemView.findViewById(R.id.tvBookingDate);
            tvStartTime = itemView.findViewById(R.id.tvStartTime);
            tvPrice = itemView.findViewById(R.id.tvPrice);
        }
    }
}