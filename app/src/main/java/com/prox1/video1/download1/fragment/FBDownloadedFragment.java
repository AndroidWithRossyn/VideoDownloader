package com.prox1.video1.download1.fragment;

import static androidx.databinding.DataBindingUtil.inflate;


import static com.prox1.video1.download1.util.Utils.RootDirectoryFacebookShow;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;


import com.prox1.video1.download1.R;
import com.prox1.video1.download1.activity.FullViewActivity;
import com.prox1.video1.download1.activity.GalleryActivity;
import com.prox1.video1.download1.adapter.FileListAdapter;
import com.prox1.video1.download1.databinding.FragmentHistoryBinding;
import com.prox1.video1.download1.interfaces.FileListClickInterface;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.ArrayList;

public class FBDownloadedFragment extends Fragment implements FileListClickInterface {
    private FragmentHistoryBinding binding;
    private FileListAdapter fileListAdapter;
    private ArrayList<File> fileArrayList;
    private GalleryActivity activity;
    public static FBDownloadedFragment newInstance(String param1) {
        FBDownloadedFragment fragment = new FBDownloadedFragment();
        Bundle args = new Bundle();
        args.putString("m", param1);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(@NotNull Context _context) {
        super.onAttach(_context);
        activity = (GalleryActivity) _context;
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            String mParam1 = getArguments().getString("m");
        }
    }
    @Override
    public void onResume() {
        super.onResume();
        activity = (GalleryActivity) getActivity();
        getAllFiles();

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = inflate(inflater, R.layout.fragment_history, container, false);
        initViews();
        return binding.getRoot();
    }
    private void initViews(){
        binding.swiperefresh.setOnRefreshListener(() -> {
            getAllFiles();
            binding.swiperefresh.setRefreshing(false);
        });
    }

    private void getAllFiles(){
        fileArrayList = new ArrayList<>();
        File[] files = RootDirectoryFacebookShow.listFiles();
        if (files!=null) {
            for (File file : files) {
                fileArrayList.add(file);
            }
            if(fileArrayList.size()==0)
            {
                binding.tvNoResult.setVisibility(View.VISIBLE);
            }
            fileListAdapter = new FileListAdapter(activity, fileArrayList, FBDownloadedFragment.this);
            binding.rvFileList.setAdapter(fileListAdapter);
        }

    }

    @Override
    public void getPosition(int position, File file) {
        Intent inNext = new Intent(activity, FullViewActivity.class);
        inNext.putExtra("ImageDataFile", fileArrayList);
        inNext.putExtra("Position", position);
        activity.startActivity(inNext);
    }
}
