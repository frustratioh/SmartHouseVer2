package com.example.smarthouse.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.smarthouse.R;
import com.example.smarthouse.model.dto.response.DeviceTypeResponse;
import java.util.List;

public class DeviceAddingAdapter extends RecyclerView.Adapter<DeviceAddingAdapter.ViewHolder> {
    private List<DeviceTypeResponse> deviceTypes;
    private OnDeviceTypeClickListener listener;
    private int selectedPosition = 0;
    private Context context;
    private List<Long> clickableIds;

    public interface OnDeviceTypeClickListener {
        void onDeviceTypeClick(Long deviceTypeId);
    }

    public DeviceAddingAdapter(List<DeviceTypeResponse> deviceTypes,
                               OnDeviceTypeClickListener listener,
                               List<Long> clickableIds) {
        this.deviceTypes = deviceTypes;
        this.listener = listener;
        this.clickableIds = clickableIds;
    }

    public void setSelectedPosition(int position) {
        this.selectedPosition = position;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.item_device_adding, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        DeviceTypeResponse type = deviceTypes.get(position);
        holder.tvName.setText(type.getName());
        
        holder.ivDevice.setImageResource(getDeviceIcon(type.getDeviceTypeId()));
        
        boolean isClickable = clickableIds == null || clickableIds.contains(type.getDeviceTypeId());
        boolean isSelected = (selectedPosition == position) && isClickable;

        if (isSelected) {
            holder.ivDevice.setBackgroundResource(R.drawable.circle_active);
            holder.tvName.setTextColor(context.getColor(R.color.blue));
        } else {
            holder.ivDevice.setBackgroundResource(R.drawable.circle_non_active);
            holder.tvName.setTextColor(context.getColor(R.color.gray));
        }
        
        holder.ivDevice.setColorFilter(context.getColor(R.color.white));

        if (isClickable) {
            holder.itemView.setAlpha(1.0f);
            holder.itemView.setOnClickListener(v -> {
                selectedPosition = position;
                listener.onDeviceTypeClick(type.getDeviceTypeId());
                notifyDataSetChanged();
            });
        } else {
            holder.itemView.setAlpha(0.4f);
            holder.itemView.setOnClickListener(null);
        }
    }

    private int getDeviceIcon(Long typeId) {
        if (typeId == null) return R.drawable.ic_default_room;
        if (typeId == 4) return R.drawable.ic_lock; // Дверь
        if (typeId == 5) return R.drawable.ic_kitchen; // Холодильник
        
        // Заглушки
        if (typeId == 102) return R.drawable.ic_warm_floo; // Как кондиционер
        if (typeId == 103) return R.drawable.ic_hood;
        if (typeId == 104) return R.drawable.ic_warm_floo; // Как температура

        return R.drawable.ic_default_room;
    }

    @Override
    public int getItemCount() {
        return deviceTypes.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivDevice;
        TextView tvName;
        ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivDevice = itemView.findViewById(R.id.ivDevice);
            tvName = itemView.findViewById(R.id.tvName);
        }
    }
}
