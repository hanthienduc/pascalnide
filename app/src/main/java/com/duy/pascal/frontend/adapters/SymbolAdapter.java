package com.duy.pascal.frontend.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.duy.pascal.frontend.R;
import com.duy.pascal.frontend.view.SymbolListView;


/**
 * Created by Duy on 11-Feb-17.
 */

public class SymbolAdapter extends RecyclerView.Adapter<SymbolAdapter.ViewHolder> {
    private String[] mList;
    private SymbolListView.OnKeyListener listener;

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_key, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.text.setText(mList[position]);
        holder.text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) listener.onKeyClick(v,((TextView) v).getText().toString());
            }
        });
    }

    @Override
    public int getItemCount() {
        return mList.length;
    }


    public void setListKey(String[] mList) {
        this.mList = mList;
    }

    public void setListener(SymbolListView.OnKeyListener listener) {
        this.listener = listener;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView text;

        public ViewHolder(View itemView) {
            super(itemView);
            text = (TextView) itemView.findViewById(R.id.text_view);
        }
    }
}
