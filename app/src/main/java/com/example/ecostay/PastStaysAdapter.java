package com.example.ecostay;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class PastStaysAdapter extends RecyclerView.Adapter<PastStaysAdapter.PastStayViewHolder> {

    private List<PastStay> stayList;

    public PastStaysAdapter(List<PastStay> stayList) {
        this.stayList = stayList;
    }

    @NonNull
    @Override
    public PastStayViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_past_stay, parent, false);
        return new PastStayViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PastStayViewHolder holder, int position) {
        PastStay stay = stayList.get(position);
        holder.tvRoomName.setText(stay.getRoomName());
        holder.tvStayDates.setText(stay.getDates());
        holder.tvTotalPrice.setText(stay.getTotalPrice());
    }

    @Override
    public int getItemCount() {
        return stayList.size();
    }

    public static class PastStayViewHolder extends RecyclerView.ViewHolder {
        TextView tvRoomName, tvStayDates, tvTotalPrice;

        public PastStayViewHolder(@NonNull View itemView) {
            super(itemView);
            tvRoomName = itemView.findViewById(R.id.tvRoomName);
            tvStayDates = itemView.findViewById(R.id.tvStayDates);
            tvTotalPrice = itemView.findViewById(R.id.tvTotalPrice);
        }
    }
}