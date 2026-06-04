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
import com.example.smarthouse.model.dto.response.RoomResponse;
import com.example.smarthouse.model.dto.response.RoomTypeResponse;
import com.example.smarthouse.api.RetrofitClient;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RoomMainAdapter extends RecyclerView.Adapter<RoomMainAdapter.ViewHolder> {
    private List<RoomResponse> roomList;
    private Map<Long, String> roomTypeNames = new HashMap<>();
    private OnRoomClickListener clickListener;
    private OnRoomLongClickListener longClickListener;
    private Context context;

    public interface OnRoomClickListener { void onRoomClick(RoomResponse room); }
    public interface OnRoomLongClickListener { void onRoomLongClick(Long roomId); }

    public RoomMainAdapter(List<RoomResponse> roomList,
                           OnRoomClickListener clickListener,
                           OnRoomLongClickListener longClickListener) {
        this.roomList = roomList;
        this.clickListener = clickListener;
        this.longClickListener = longClickListener;
        loadRoomTypes();
    }

    private void loadRoomTypes() {
        RetrofitClient.getInstance().getRoomTypes().enqueue(new Callback<List<RoomTypeResponse>>() {
            @Override
            public void onResponse(Call<List<RoomTypeResponse>> call, Response<List<RoomTypeResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    for (RoomTypeResponse type : response.body()) {
                        roomTypeNames.put(type.getRoomTypeId(), type.getName());
                    }
                    notifyDataSetChanged();
                }
            }
            @Override
            public void onFailure(Call<List<RoomTypeResponse>> call, Throwable t) {}
        });
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.item_room_main, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        RoomResponse room = roomList.get(position);
        holder.tvName.setText(room.getName());

        String typeName = roomTypeNames.get(room.getRoomTypeId());
        if (typeName == null) {
            typeName = room.getName(); // Запасной вариант - сопоставление по имени
        }
        
        holder.ivRoomIcon.setImageResource(getRoomIcon(typeName));
        holder.ivRoomIcon.setColorFilter(context.getColor(R.color.white));

        holder.itemView.setOnClickListener(v -> clickListener.onRoomClick(room));
        holder.itemView.setOnLongClickListener(v -> {
            longClickListener.onRoomLongClick(room.getRoomId());
            return true;
        });
    }

    private int getRoomIcon(String name) {
        if (name == null) return R.drawable.ic_default_room;
        name = name.toLowerCase();
        if (name.contains("прихожая")) return R.drawable.hallway;
        if (name.contains("кухня")) return R.drawable.ic_kitchen;
        if (name.contains("гостиная") || name.contains("зал")) return R.drawable.ic_living;
        if (name.contains("ванная")) return R.drawable.ic_bath;
        if (name.contains("спальня")) return R.drawable.ic_bed;
        if (name.contains("кабинет") || name.contains("офис")) return R.drawable.ic_office;
        return R.drawable.ic_default_room;
    }

    @Override
    public int getItemCount() {
        return roomList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivRoomIcon;
        TextView tvName;
        ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivRoomIcon = itemView.findViewById(R.id.ivRoomIcon);
            tvName = itemView.findViewById(R.id.tvName);
        }
    }
}
