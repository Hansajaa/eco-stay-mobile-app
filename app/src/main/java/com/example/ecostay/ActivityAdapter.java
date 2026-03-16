package com.example.ecostay;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ActivityAdapter extends RecyclerView.Adapter<ActivityAdapter.ActivityViewHolder> {

    private List<ActivityModel> activityList;
    private OnBookNowClickListener listener;

    public interface OnBookNowClickListener {
        void onBookNowClick(ActivityModel activity);
    }

    public void setOnBookNowClickListener(OnBookNowClickListener listener) {
        this.listener = listener;
    }

    public ActivityAdapter(List<ActivityModel> activityList) {
        this.activityList = activityList;
    }

    public void updateList(List<ActivityModel> newList) {
        this.activityList = newList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ActivityViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_activity_card, parent, false);
        return new ActivityViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ActivityViewHolder holder, int position) {
        ActivityModel activity = activityList.get(position);
        holder.tvActivityName.setText(activity.getName());
        holder.tvActivityDescription.setText(activity.getDescription());
        holder.tvActivityPrice.setText(activity.getPrice());
        holder.tvStartTime.setText(activity.getStartTime());
        holder.tvDuration.setText(activity.getDuration());
        holder.ivActivityImage.setImageResource(activity.getImageResId());
        
        if (activity.getName().contains("Sunrise")) {
            holder.tvBestSeller.setVisibility(View.VISIBLE);
        } else {
            holder.tvBestSeller.setVisibility(View.GONE);
        }

        holder.btnBookActivity.setOnClickListener(v -> {
            if (listener != null) {
                listener.onBookNowClick(activity);
            }
        });
    }

    @Override
    public int getItemCount() {
        return activityList.size();
    }

    public static class ActivityViewHolder extends RecyclerView.ViewHolder {
        ImageView ivActivityImage;
        TextView tvActivityName, tvActivityDescription, tvActivityPrice, tvStartTime, tvDuration, tvBestSeller;
        Button btnBookActivity;

        public ActivityViewHolder(@NonNull View itemView) {
            super(itemView);
            ivActivityImage = itemView.findViewById(R.id.ivActivityImage);
            tvActivityName = itemView.findViewById(R.id.tvActivityName);
            tvActivityDescription = itemView.findViewById(R.id.tvActivityDescription);
            tvActivityPrice = itemView.findViewById(R.id.tvActivityPrice);
            tvStartTime = itemView.findViewById(R.id.tvStartTime);
            tvDuration = itemView.findViewById(R.id.tvDuration);
            tvBestSeller = itemView.findViewById(R.id.tvBestSeller);
            btnBookActivity = itemView.findViewById(R.id.btnBookActivity);
        }
    }
}