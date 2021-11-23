package org.vosystems.covidvaccinationcenterslocator.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.vosystems.covidvaccinationcenterslocator.Models.Reminder;
import org.vosystems.covidvaccinationcenterslocator.R;

import java.util.ArrayList;

public class ReminderAdapter extends RecyclerView.Adapter<ReminderAdapter.MyViewHolder> {
    ArrayList<Reminder> dataholder = new ArrayList<Reminder>();

    public ReminderAdapter(ArrayList<Reminder> dataholder) {
        this.dataholder = dataholder;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.reminder_file, parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.tvTitle.setText(dataholder.get(position).getTitle());
        holder.tvDate.setText(dataholder.get(position).getDate());
        holder.tvTime.setText(dataholder.get(position).getTime());
    }

    @Override
    public int getItemCount() {
        return dataholder.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        TextView tvTitle, tvDate, tvTime;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            tvTitle = itemView.findViewById(R.id.reminderTitle);
            tvDate = itemView.findViewById(R.id.reminderDate);
            tvTime = itemView.findViewById(R.id.reminderTime);
        }
    }

}

