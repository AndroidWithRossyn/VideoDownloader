package com.prox1.video1.download1.fragment;

import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.prox1.video1.download1.R;
import com.prox1.video1.download1.activity.FullViewActivity;
import com.prox1.video1.download1.activity.GalleryActivity;
import com.prox1.video1.download1.adapter.FileListAdapter;
import com.prox1.video1.download1.adapter.WhatsappStatusAdapter;
import com.prox1.video1.download1.databinding.FragmentHistoryBinding;
import com.prox1.video1.download1.databinding.FragmentWhatsappImageBinding;
import com.prox1.video1.download1.interfaces.FileListClickInterface;
import com.prox1.video1.download1.interfaces.FileListWhatsappClickInterface;
import com.prox1.video1.download1.model.WhatsappStatusModel;

import org.jetbrains.annotations.NotNull;

import static com.prox1.video1.download1.util.Utils.RootDirectoryLikeeShow;
import java.io.File;
import java.util.ArrayList;

public class WhatsappQVideoFragment extends Fragment implements FileListWhatsappClickInterface {
    private File[] allfiles;
    FragmentWhatsappImageBinding binding;
    private ArrayList<Uri> fileArrayList;
    ArrayList<WhatsappStatusModel> statusModelArrayList;
    private WhatsappStatusAdapter whatsappStatusAdapter;

    public void getPosition(int i) {
    }

    public WhatsappQVideoFragment(ArrayList<Uri> arrayList) {
        this.fileArrayList = arrayList;
    }

    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        this.binding = (FragmentWhatsappImageBinding) DataBindingUtil.inflate(layoutInflater, R.layout.fragment_whatsapp_image, viewGroup, false);
        initViews();
        return this.binding.getRoot();
    }

    private void initViews() {
        this.statusModelArrayList = new ArrayList<>();
        getData();
        this.binding.swiperefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            public final void onRefresh() {
                WhatsappQVideoFragment.this.lambda$initViews$0$WhatsappQVideoFragment();
            }
        });
    }

    public /* synthetic */ void lambda$initViews$0$WhatsappQVideoFragment() {
        this.statusModelArrayList = new ArrayList<>();
        getData();
        this.binding.swiperefresh.setRefreshing(false);
    }

    private void getData() {
        if (Build.VERSION.SDK_INT > 29) {
            int i = 0;
            while (i < this.fileArrayList.size()) {
                try {
                    Uri uri = this.fileArrayList.get(i);
                    if (uri.toString().endsWith(".mp4")) {
                        this.statusModelArrayList.add(new WhatsappStatusModel("WhatsStatus: " + (i + 1), uri, new File(uri.toString()).getAbsolutePath(), new File(uri.toString()).getName()));
                    }
                    i++;
                } catch (Exception e) {
                    e.printStackTrace();
                    return;
                }
            }
            if (this.statusModelArrayList.size() != 0) {
                this.binding.tvNoResult.setVisibility(8);
            } else {
                this.binding.tvNoResult.setVisibility(0);
            }
            this.whatsappStatusAdapter = new WhatsappStatusAdapter(getActivity(), this.statusModelArrayList, this);
            this.binding.rvFileList.setAdapter(this.whatsappStatusAdapter);
        }
    }
}
