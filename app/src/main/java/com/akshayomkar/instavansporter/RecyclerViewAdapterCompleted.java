package com.akshayomkar.instavansporter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Akshay on 1/9/2016.
 */
public class RecyclerViewAdapterCompleted extends RecyclerView.Adapter<RecyclerViewAdapterCompleted.RecyclerViewHolder>{

    private static final String TAG = "RecyclAdptrDiscovery";
    private LayoutInflater inflater;
    private ArrayList<Job> discoveryList;
    private Context context;
    private RecycleClickListnerDiscovery clickListener;

    //for adding fonts
    private Typeface tfRobotoThin,tfRobotoThinItalic,tfRobotoLight;

    public RecyclerViewAdapterCompleted(Context context, ArrayList<Job> list){
        inflater = LayoutInflater.from(context);
        this.discoveryList = list;
        this.context = context;

        //set Typefaces fonts
        tfRobotoLight = Typeface.createFromAsset(context.getAssets(),"RobotoLight.ttf");
        tfRobotoThin = Typeface.createFromAsset(context.getAssets(),"RobotoThin.ttf");
        tfRobotoThinItalic = Typeface.createFromAsset(context.getAssets(),"RobotoThinItalic.ttf");
    }

    @Override
    public RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = inflater.inflate(R.layout.job_card_layout, parent, false);
        RecyclerViewHolder myViewHolder = new RecyclerViewHolder(context,view);
        return myViewHolder;
    }


    @Override
    public void onBindViewHolder(RecyclerViewHolder holder, int position) {
        Job current = discoveryList.get(position);

        //set pic first since it will take time to load

        //set name(title)
        holder.tv_address.setText(current.getStr_src());
        String[] tt = current.getSrc_time().split(":");
        holder.tv_time.setText("Time: "+tt[0]+"-"+tt[1]);
        holder.tv_date.setText("Date: "+tt[2]+"-"+tt[3]);
        holder.tv_amount.setText("Rs. "+current.getAmount());

        switch (current.getStatus()){
            case "available":
                holder.tv_status.setBackgroundColor(Color.GREEN);
                break;
            case "inprocess":
                holder.tv_status.setBackgroundColor(Color.YELLOW);
                break;
            case "completed":
                holder.tv_status.setBackgroundColor(Color.RED);
                break;
        }
    }


    public void setClickListener(RecycleClickListnerDiscovery clickListener){
        this.clickListener = clickListener;
    }


    @Override
    public int getItemCount() {
        Log.d(TAG, "list count "+discoveryList.size());
        return discoveryList.size();
    }

    ///////////////////
    //////*********************
    ///////////////////

    class RecyclerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView tv_address, tv_amount,tv_date,tv_time,tv_status;
        RelativeLayout relativeLayout;
        //ImageView iv_image;
        Context context;

        public RecyclerViewHolder(final Context context,View itemView) {
            super(itemView);

            this.context = context;

            itemView.setOnClickListener(this);

            tv_address = (TextView) itemView.findViewById(R.id.tv_job_card_address);
            tv_amount = (TextView) itemView.findViewById(R.id.tv_job_card_amount);
            tv_date = (TextView) itemView.findViewById(R.id.tv_job_card_date);
            tv_time = (TextView) itemView.findViewById(R.id.tv_job_card_time);
            tv_status = (TextView) itemView.findViewById(R.id.tv_card_status_indicator);
            //set typeface
            tv_address.setTypeface(tfRobotoLight);
            tv_date.setTypeface(tfRobotoLight);tv_time.setTypeface(tfRobotoLight);
            tv_amount.setTypeface(tfRobotoThin);
        }

        @Override
        public void onClick(View v) {
            if(clickListener!=null)
            {
                clickListener.itemClick(v,getAdapterPosition());
            }
        }
    }

    public interface RecycleClickListnerDiscovery{

        public void itemClick(View view, int position);

    }
}
