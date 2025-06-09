package com.kkp.nure.animalrescue.adapters;

import static com.kkp.nure.animalrescue.utils.AndroidUtils.DATE_FMT;
import static com.kkp.nure.animalrescue.utils.GlideUtils.loadMediaToImage;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.kkp.nure.animalrescue.R;
import com.kkp.nure.animalrescue.entities.BasicUser;
import com.kkp.nure.animalrescue.entities.Donation;

import java.util.List;

public class DonationsAdapter extends RecyclerView.Adapter<DonationsAdapter.DonationViewHolder> {
    private final List<Donation> donations;
    private final Context context;

    public DonationsAdapter(Context context, List<Donation> donations) {
        this.context = context;
        this.donations = donations;
    }

    @NonNull
    @Override
    public DonationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_donation, parent, false);
        return new DonationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DonationViewHolder holder, int position) {
        Donation donation = donations.get(position);
        if(donation.getUser() != null) {
            BasicUser dUser = donation.getUser();
            loadMediaToImage(holder.userAvatar, dUser.getPhoto());
            holder.textUserName.setText(context.getString(R.string.full_name_fmt, dUser.getFirstName(), dUser.getLastName()));
        } else {
            loadMediaToImage(holder.userAvatar, null);
            holder.textUserName.setText(R.string.anonymous);
        }
        holder.textAmount.setText(String.valueOf(donation.getAmount()));
        holder.textComment.setText(donation.getComment());
        holder.textDate.setText(DATE_FMT.format(donation.getDate() * 1000));
    }

    public void addDonations(List<Donation> newDonations) {
        int start = donations.size();
        donations.addAll(newDonations);
        notifyItemRangeInserted(start, newDonations.size());
    }

    @Override
    public int getItemCount() {
        return donations.size();
    }

    public static class DonationViewHolder extends RecyclerView.ViewHolder {
        ImageView userAvatar;
        TextView textUserName, textAmount, textComment, textDate;

        public DonationViewHolder(@NonNull View itemView) {
            super(itemView);

            userAvatar = itemView.findViewById(R.id.image_user_avatar);
            textUserName = itemView.findViewById(R.id.text_user_name);
            textAmount = itemView.findViewById(R.id.text_amount);
            textComment = itemView.findViewById(R.id.text_comment);
            textDate = itemView.findViewById(R.id.text_date);
        }
    }
}

