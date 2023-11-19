package com.example.studentmanagement.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.studentmanagement.R;
import com.example.studentmanagement.adapter.HistoryAdapter;
import com.example.studentmanagement.models.LoginHistory;

import java.util.ArrayList;

public abstract class DialogLoginHistory  extends Dialog {
    private ArrayList<LoginHistory> list;
    private HistoryAdapter adapter;
    public DialogLoginHistory(Context context,
                      ArrayList<LoginHistory> list)
    {
        super(context);
        this.list = list;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState != null
                ? savedInstanceState
                : new Bundle());

        View view
                = LayoutInflater.from(getContext())
                .inflate(R.layout.dialog_list, null);

        setContentView(view);

        setCanceledOnTouchOutside(true);

        setCancelable(true);
        setUpRecyclerView(view);
    }
    private void setUpRecyclerView(View view)
    {

        RecyclerView recyclerView
                = view.findViewById(R.id.rvList);
        recyclerView.setLayoutManager(
                new LinearLayoutManager(getContext()));

        adapter = new HistoryAdapter( list, getContext());
        recyclerView.setAdapter(adapter);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(getContext(),DividerItemDecoration.VERTICAL);
        recyclerView.addItemDecoration(dividerItemDecoration);
    }
}
