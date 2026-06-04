package com.example.smarthouse.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.smarthouse.R;
import com.example.smarthouse.model.dto.response.DeviceResponse;
import com.example.smarthouse.model.dto.response.DeviceSettingResponse;
import com.example.smarthouse.api.RetrofitClient;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DeviceMainAdapter extends RecyclerView.Adapter<DeviceMainAdapter.ViewHolder> {
    private List<DeviceResponse> deviceList;
    private Map<Long, Boolean> statusMap = new HashMap<>();
    private Set<Long> pendingUpdates = new HashSet<>();
    private OnDeviceClickListener clickListener;
    private OnDeviceSwitchListener switchListener;
    private OnDeviceLongClickListener longClickListener;
    private Context context;

    public interface OnDeviceClickListener {
        void onDeviceClick(DeviceResponse device);
    }

    public interface OnDeviceSwitchListener {
        void onSwitchClick(DeviceResponse device, boolean isOn, SwitchCallback callback);
    }

    public interface OnDeviceLongClickListener {
        void onDeviceLongClick(DeviceResponse device);
    }

    public interface SwitchCallback {
        void onSuccess();
        void onError();
    }

    public DeviceMainAdapter(List<DeviceResponse> deviceList,
                             OnDeviceClickListener clickListener,
                             OnDeviceSwitchListener switchListener,
                             OnDeviceLongClickListener longClickListener) {
        this.deviceList = deviceList;
        this.clickListener = clickListener;
        this.switchListener = switchListener;
        this.longClickListener = longClickListener;
        loadAllStatuses();
    }

    private void loadAllStatuses() {
        for (DeviceResponse device : deviceList) {
            RetrofitClient.getInstance().getDeviceSettings("eq." + device.getDeviceId())
                    .enqueue(new Callback<List<DeviceSettingResponse>>() {
                        @Override
                        public void onResponse(Call<List<DeviceSettingResponse>> call, Response<List<DeviceSettingResponse>> response) {
                            if (response.isSuccessful() && response.body() != null) {
                                for (DeviceSettingResponse setting : response.body()) {
                                    if ("status".equals(setting.getParameter())) {
                                        statusMap.put(device.getDeviceId(), "on".equals(setting.getValue()));
                                        notifyDataSetChanged();
                                        break;
                                    }
                                }
                            }
                        }
                        @Override
                        public void onFailure(Call<List<DeviceSettingResponse>> call, Throwable t) {}
                    });
        }
    }

    public void refreshAllStatuses() {
        loadAllStatuses();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.item_device_main, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        DeviceResponse device = deviceList.get(position);
        holder.tvName.setText(device.getName());

        holder.ivDevice.setImageResource(getDeviceIcon(device.getDeviceTypeId()));

        Boolean isOn = statusMap.get(device.getDeviceId());
        boolean status = isOn != null && isOn;
        holder.ivSwitch.setImageResource(status ? R.drawable.ic_btn_on : R.drawable.ic_btn_off);

        holder.ivSwitch.setEnabled(!pendingUpdates.contains(device.getDeviceId()));
        holder.ivSwitch.setAlpha(pendingUpdates.contains(device.getDeviceId()) ? 0.5f : 1.0f);

        holder.itemView.setOnClickListener(v -> clickListener.onDeviceClick(device));
        holder.itemView.setOnLongClickListener(v -> {
            longClickListener.onDeviceLongClick(device);
            return true;
        });

        holder.ivSwitch.setOnClickListener(v -> {
            if (pendingUpdates.contains(device.getDeviceId())) {
                return;
            }

            boolean newState = !status;
            final boolean oldStatus = status;

            pendingUpdates.add(device.getDeviceId());
            holder.ivSwitch.setEnabled(false);
            holder.ivSwitch.setAlpha(0.5f);

            holder.ivSwitch.setImageResource(newState ? R.drawable.ic_btn_on : R.drawable.ic_btn_off);
            statusMap.put(device.getDeviceId(), newState);

            switchListener.onSwitchClick(device, newState, new SwitchCallback() {
                @Override
                public void onSuccess() {
                    pendingUpdates.remove(device.getDeviceId());
                    holder.ivSwitch.setEnabled(true);
                    holder.ivSwitch.setAlpha(1.0f);
                }

                @Override
                public void onError() {
                    pendingUpdates.remove(device.getDeviceId());
                    holder.ivSwitch.setEnabled(true);
                    holder.ivSwitch.setAlpha(1.0f);
                    statusMap.put(device.getDeviceId(), oldStatus);
                    holder.ivSwitch.setImageResource(oldStatus ? R.drawable.ic_btn_on : R.drawable.ic_btn_off);
                    if (context != null) {
                        Toast.makeText(context, "Ошибка", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        });
    }

    private int getDeviceIcon(Long typeId) {
        if (typeId == 4) return R.drawable.ic_lock; // Дверь
        if (typeId == 5) return R.drawable.ic_kitchen; // Холодильник
        return R.drawable.ic_default_room;
    }

    @Override
    public int getItemCount() {
        return deviceList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivDevice, ivSwitch;
        TextView tvName;
        ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivDevice = itemView.findViewById(R.id.ivDevice);
            ivSwitch = itemView.findViewById(R.id.ivSwitch);
            tvName = itemView.findViewById(R.id.tvName);
        }
    }
}
