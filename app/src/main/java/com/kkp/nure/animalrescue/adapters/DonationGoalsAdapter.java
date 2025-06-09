package com.kkp.nure.animalrescue.adapters;

import static com.kkp.nure.animalrescue.utils.AndroidUtils.DATE_FMT;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.kkp.nure.animalrescue.R;
import com.kkp.nure.animalrescue.activities.DonationGoalActivity;
import com.kkp.nure.animalrescue.entities.DonationGoal;

import java.text.DateFormat;
import java.util.Date;
import java.util.List;

public class DonationGoalsAdapter extends RecyclerView.Adapter<DonationGoalsAdapter.DonationGoalViewHolder> {
    private final List<DonationGoal> donationGoals;
    private final Context context;

    public DonationGoalsAdapter(Context context, List<DonationGoal> donationGoals) {
        this.context = context;
        this.donationGoals = donationGoals;
    }

    @NonNull
    @Override
    public DonationGoalViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_donation_goal, parent, false);
        return new DonationGoalViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DonationGoalViewHolder holder, int position) {
        DonationGoal goal = donationGoals.get(position);
        holder.textName.setText(goal.getName());
        holder.textDescription.setText(goal.getDescription());
        holder.textAmounts.setText(context.getString(R.string.raised_fmt, goal.getGotAmount(), goal.getNeedAmount()));

        String datesText;
        String from = DATE_FMT.format(new Date(goal.getCreatedAt() * 1000));
        if(goal.getEndedAt() != null) {
            datesText = context.getString(R.string.from_to, from, DATE_FMT.format(new Date(goal.getEndedAt() * 1000)));
        } else {
            datesText = context.getString(R.string.from, from);
        }
        holder.textDates.setText(datesText);

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, DonationGoalActivity.class);
            intent.putExtra("donationGoalId", goal.getId());
            context.startActivity(intent);
        });
    }

    public void addDonationGoals(List<DonationGoal> newDonationGoals) {
        int start = donationGoals.size();
        donationGoals.addAll(newDonationGoals);
        notifyItemRangeInserted(start, newDonationGoals.size());
    }

    @Override
    public int getItemCount() {
        return donationGoals.size();
    }

    public static class DonationGoalViewHolder extends RecyclerView.ViewHolder {
        TextView textName, textDescription, textAmounts, textDates;

        public DonationGoalViewHolder(@NonNull View itemView) {
            super(itemView);
            textName = itemView.findViewById(R.id.text_name);
            textDescription = itemView.findViewById(R.id.text_description);
            textAmounts = itemView.findViewById(R.id.text_amounts);
            textDates = itemView.findViewById(R.id.text_dates);
        }
    }
}

