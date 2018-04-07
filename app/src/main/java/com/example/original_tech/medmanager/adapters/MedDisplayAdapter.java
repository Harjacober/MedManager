package com.example.original_tech.medmanager.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.original_tech.medmanager.MainActivity;
import com.example.original_tech.medmanager.R;
import com.example.original_tech.medmanager.data.SearchResultActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Original-Tech on 3/24/2018.
 */

public class MedDisplayAdapter extends RecyclerView.Adapter<MedDisplayAdapter.ViewHolder> {


    public interface ListItemCLickListener{
        void onItemCLickListener(String clickedItemUniqueId);
    }
    private ListItemCLickListener mlistener;
    private ArrayList<JSONObject> data;
    private Context mContext;

    public MedDisplayAdapter(ArrayList<JSONObject> data, Context mContext, ListItemCLickListener mlistener) {
        this.data = data;
        this.mContext = mContext;
        this.mlistener = mlistener;
    }

    @Override
    public MedDisplayAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).
                inflate(R.layout.item_medication_list_recyclerview, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MedDisplayAdapter.ViewHolder holder, int position) {
        try {
            holder.bind(position);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView medName;
        TextView medDescription;
        TextView medInterval;
        public ViewHolder(View itemView) {
            super(itemView);
            medName = itemView.findViewById(R.id.med_name);
            medDescription = itemView.findViewById(R.id.med_description);
            medInterval = itemView.findViewById(R.id.med_interval);
        }

        public void bind(int position) throws JSONException{
            medName.setText(data.get(position).getString(SearchResultActivity.KEY_NAME));
            medDescription.setText(data.get(position).getString(SearchResultActivity.KEY_DESC));
            medInterval.setText(data.get(position).getString(MainActivity.KEY_INTERVAL));
            final String uniqueId = data.get(position).getString(SearchResultActivity.UNIQUE_ID);
            //When the cardView of each medication is clicked
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mlistener.onItemCLickListener(uniqueId);
                }
            });
        }
    }
    public void update(ArrayList<JSONObject> mdata) {
        data = mdata;
        notifyDataSetChanged();
    }

}
