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
import com.example.smarthouse.model.dto.response.CustomRoomTypeResponse;
import com.example.smarthouse.model.dto.response.RoomTypeResponse;
import java.util.List;

public class RoomAddingAdapter extends RecyclerView.Adapter<RoomAddingAdapter.ViewHolder> {
    private List<Object> items;
    private OnItemClickListener listener;
    private OnItemLongClickListener longClickListener;
    private int selectedPosition = 0;
    private Context context;

    public interface OnItemClickListener {
        void onItemClick(Object item);
    }

    public interface OnItemLongClickListener {
        void onItemLongClick(Object item);
    }

    public RoomAddingAdapter(List<Object> items,
                             OnItemClickListener listener,
                             OnItemLongClickListener longClickListener) {
        this.items = items;
        this.listener = listener;
        this.longClickListener = longClickListener;
    }

    public void setSelectedPosition(int position) {
        this.selectedPosition = position;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.item_room_adding, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Object item = items.get(position);

        if (item instanceof RoomTypeResponse) {
            RoomTypeResponse type = (RoomTypeResponse) item;
            holder.tvName.setText(type.getName());
            holder.ivRoom.setImageResource(getRoomIcon(type.getName()));
        } else if (item instanceof CustomRoomTypeResponse) {
            CustomRoomTypeResponse type = (CustomRoomTypeResponse) item;
            holder.tvName.setText(type.getName());
            holder.ivRoom.setImageResource(R.drawable.ic_default_room);
        }

        boolean isSelected = (selectedPosition == position);
        if (isSelected) {
            holder.ivRoom.setBackgroundResource(R.drawable.circle_active);
            holder.tvName.setTextColor(context.getColor(R.color.blue));
        } else {
            holder.ivRoom.setBackgroundResource(R.drawable.circle_non_active);
            holder.tvName.setTextColor(context.getColor(R.color.gray));
        }
        
        // Красим иконку в белый, как на макете
        holder.ivRoom.setColorFilter(context.getColor(R.color.white));

        holder.itemView.setOnClickListener(v -> {
            selectedPosition = position;
            listener.onItemClick(item);
            notifyDataSetChanged();
        });

        holder.itemView.setOnLongClickListener(v -> {
            longClickListener.onItemLongClick(item);
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
        return items.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivRoom;
        TextView tvName;
        ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivRoom = itemView.findViewById(R.id.ivRoom);
            tvName = itemView.findViewById(R.id.tvName);
        }
    }
}
