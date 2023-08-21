package com.prox1.video1.download1.chip;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.prox1.video1.download1.R;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class InterestAdapter extends RecyclerView.Adapter<InterestAdapter.InterestViewHolder> {

    private Context mContext;
    private List<UserListData> listUserData;
    private boolean isMultiChoice;
    private int selectedPosition = -1;

    InterestAdapter(Context context, List<UserListData> listGuestUserData, boolean isMultiChoice) {
        this.listUserData = listGuestUserData;
        this.mContext = context;
        this.isMultiChoice = isMultiChoice;

    }


    @Override
    public InterestViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_data, parent, false);
        return new InterestViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final InterestViewHolder holder, final int position) {
       if (isMultiChoice) {
           handleMultiChoiceSelection(holder,position,holder.tvName);
       }
       else {
          handleSingleChoiceSelection(holder,position,holder.tvName);
       }
    }

    @Override
    public int getItemCount() {
        return listUserData.size();
    }


    class InterestViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.tv_name)
        TextView tvName;
        InterestViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

    private void handleMultiChoiceSelection(RecyclerView.ViewHolder holder, final int position, TextView textView) {
        if (listUserData.get(position).isSelected()) {
            textView.setBackgroundResource(R.drawable.user_data_bg_selected);
            textView.setTextColor(Color.parseColor("#ffffff"));
        } else {
            textView.setBackgroundResource(R.drawable.user_data_bg_unselected);
            textView.setTextColor(Color.parseColor("#000000"));
        }
        textView.setText(listUserData.get(position).getName());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (listUserData.get(position).isSelected()) {
                    listUserData.get(position).setSelected(false);
                    textView.setTextColor(Color.parseColor("#ffffff"));
                } else {
                    listUserData.get(position).setSelected(true);
                    textView.setTextColor(Color.parseColor("#000000"));
                }
                if (mContext instanceof SelectChipActivity) {
                    ((SelectChipActivity) mContext).selectGuestUserListData(listUserData);
                }
                notifyDataSetChanged();
            }
        });
    }

    private void handleSingleChoiceSelection(RecyclerView.ViewHolder holder, final int position, TextView textView) {
        if (selectedPosition == position) {
            textView.setBackgroundResource(R.drawable.user_data_bg_selected);
        } else {
            textView.setBackgroundResource(R.drawable.user_data_bg_unselected);
        }
        textView.setText(listUserData.get(position).getName());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectedPosition = position;
                if (mContext instanceof SelectChipActivity) {
                    ((SelectChipActivity) mContext).selectGuestUserListData(listUserData);
                }
                notifyDataSetChanged();
            }
        });
    }
}
