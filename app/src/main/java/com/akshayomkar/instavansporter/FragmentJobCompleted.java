package com.akshayomkar.instavansporter;

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
public class FragmentJobCompleted extends Fragment implements RecyclerViewAdapterCompleted.RecycleClickListnerDiscovery{

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public static FragmentJobCompleted newInstance() {
        return new FragmentJobCompleted();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_tab_completed_jobs,
                container,
                false);

        RecyclerView mRecyclerView = (RecyclerView) view.findViewById(R.id.recyclerView_completed);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(layoutManager);
        RecyclerViewAdapterCompleted mAdapter = new RecyclerViewAdapterCompleted(getActivity().getApplicationContext(), MainActivity.sJobListCompleted);
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

    }
}
