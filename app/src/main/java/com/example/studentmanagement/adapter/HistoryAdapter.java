package com.example.studentmanagement.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.studentmanagement.R;
import com.example.studentmanagement.models.LoginHistory;

import java.util.ArrayList;

public class HistoryAdapter extends RecyclerView.Adapter <HistoryAdapter.RecyclerViewHolder>{

    private Context context;
    private ArrayList<LoginHistory> data;

    public HistoryAdapter(ArrayList<LoginHistory> data, Context context){
        this.data = data;
        this.context = context;
    }

    @NonNull
    @Override
    public RecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_history_login, parent, false);

        return new HistoryAdapter.RecyclerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerViewHolder holder, int position) {
        LoginHistory lg = data.get(position);
        holder.txt_date.setText(lg.getDateTime());
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public class RecyclerViewHolder extends RecyclerView.ViewHolder {

        TextView txt_date;

        public RecyclerViewHolder(@NonNull View itemView) {
            super(itemView);
            txt_date = itemView.findViewById(R.id.txt_date);
        }
    }
}
