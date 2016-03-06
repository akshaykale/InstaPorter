package com.akshayomkar.instavansporter;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by Akshay on 3/5/2016.
 */
public class FragmentJobAvailable extends Fragment implements RecyclerViewAdapterAvailable.RecycleClickListnerDiscovery{

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public static FragmentJobAvailable newInstance() {
        return new FragmentJobAvailable();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_tab_available_jobs,
                container,
                false);

        RecyclerView mRecyclerView = (RecyclerView) view.findViewById(R.id.recyclerView_available);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(layoutManager);
        RecyclerViewAdapterAvailable mAdapter = new RecyclerViewAdapterAvailable(getActivity().getApplicationContext(), MainActivity.sJobListAvailable);
        //RecyclerViewAdapterShoutout mAdapter = new RecyclerViewAdapterShoutout(mContext, BlogPostParserXML.al_AllCat);
        mAdapter.setClickListener(this);
        mRecyclerView.setAdapter(mAdapter);

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void itemClick(View view, int position) {

        Intent intent = new Intent(getActivity(),JobDetailsActivity.class);
        intent.putExtra("pos",position);
        startActivity(intent);


    }
}
