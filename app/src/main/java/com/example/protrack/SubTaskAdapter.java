package com.example.protrack;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;
import com.example.protrack.database.SubTaskEntity;

public class SubTaskAdapter extends ListAdapter<SubTaskEntity, SubTaskAdapter.SubTaskViewHolder> {

    private final OnSubTaskActionListener actionListener;

    public interface OnSubTaskActionListener {
        void onSubTaskToggle(SubTaskEntity subTask);
        void onSubTaskDelete(SubTaskEntity subTask);
    }

    public SubTaskAdapter(OnSubTaskActionListener actionListener) {
        super(new DiffUtil.ItemCallback<SubTaskEntity>() {
            @Override
            public boolean areItemsTheSame(@NonNull SubTaskEntity oldItem, @NonNull SubTaskEntity newItem) {
                return oldItem.getId() == newItem.getId();
            }

            @Override
            public boolean areContentsTheSame(@NonNull SubTaskEntity oldItem, @NonNull SubTaskEntity newItem) {
                return oldItem.isCompleted() == newItem.isCompleted() &&
                        oldItem.getTitle().equals(newItem.getTitle());
            }
        });
        this.actionListener = actionListener;
    }

    @NonNull
    @Override
    public SubTaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_subtask, parent, false);
        return new SubTaskViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SubTaskViewHolder holder, int position) {
        holder.bind(getItem(position));
    }

    class SubTaskViewHolder extends RecyclerView.ViewHolder {
        private final CheckBox cbSubTask;
        private final ImageView ivDeleteSubTask;

        public SubTaskViewHolder(@NonNull View itemView) {
            super(itemView);
            cbSubTask = itemView.findViewById(R.id.cbSubTask);
            ivDeleteSubTask = itemView.findViewById(R.id.ivDeleteSubTask);
        }

        public void bind(SubTaskEntity subTask) {
            cbSubTask.setOnCheckedChangeListener(null); // Prevent trigger during binding
            cbSubTask.setText(subTask.getTitle());
            cbSubTask.setChecked(subTask.isCompleted());
            
            // Set strike-through effect if completed
            if (subTask.isCompleted()) {
                cbSubTask.setPaintFlags(cbSubTask.getPaintFlags() | android.graphics.Paint.STRIKE_THRU_TEXT_FLAG);
            } else {
                cbSubTask.setPaintFlags(cbSubTask.getPaintFlags() & (~android.graphics.Paint.STRIKE_THRU_TEXT_FLAG));
            }

            cbSubTask.setOnCheckedChangeListener((buttonView, isChecked) -> {
                subTask.setCompleted(isChecked);
                if (isChecked) {
                    cbSubTask.setPaintFlags(cbSubTask.getPaintFlags() | android.graphics.Paint.STRIKE_THRU_TEXT_FLAG);
                } else {
                    cbSubTask.setPaintFlags(cbSubTask.getPaintFlags() & (~android.graphics.Paint.STRIKE_THRU_TEXT_FLAG));
                }
                actionListener.onSubTaskToggle(subTask);
            });

            ivDeleteSubTask.setOnClickListener(v -> actionListener.onSubTaskDelete(subTask));
        }
    }
}
