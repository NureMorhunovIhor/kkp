package com.kkp.nure.animalrescue.adapters;

import static com.kkp.nure.animalrescue.utils.GlideUtils.loadMediaToImage;
import static com.kkp.nure.animalrescue.utils.MessageUtils.showUserContactsDialog;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.kkp.nure.animalrescue.R;
import com.kkp.nure.animalrescue.activities.AnimalInfoActivity;
import com.kkp.nure.animalrescue.activities.CreateTreatmentReportActivity;
import com.kkp.nure.animalrescue.entities.BasicUser;
import com.kkp.nure.animalrescue.entities.FoundReport;
import com.kkp.nure.animalrescue.utils.GlideMediaUrl;

import java.util.List;

public class TreatingAnimalsAdapter extends RecyclerView.Adapter<TreatingAnimalsAdapter.AnimalViewHolder> {

    private final List<FoundReport> reports;
    private final Context context;

    public TreatingAnimalsAdapter(Context context, List<FoundReport> reports) {
        this.context = context;
        this.reports = reports;
    }

    @NonNull
    @Override
    public AnimalViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_treating_animal, parent, false);
        return new AnimalViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AnimalViewHolder holder, int position) {
        FoundReport report = reports.get(position);

        holder.name.setText(report.getAnimal().getName());
        holder.breed.setText(report.getAnimal().getBreed());
        holder.gender.setText(report.getAnimal().getGender().strName);

        if (!report.getAnimal().getMedia().getResult().isEmpty()) {
            Glide.with(context)
                    .load(new GlideMediaUrl(report.getAnimal().getMedia().getResult().get(0)))
                    .placeholder(R.drawable.baseline_hide_image_24)
                    .into(holder.photo);
        } else {
            holder.photo.setImageResource(R.drawable.baseline_hide_image_24);
        }

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, AnimalInfoActivity.class);
            intent.putExtra("animalId", report.getAnimal().getId());
            context.startActivity(intent);
        });

        if(report.getReportedBy() != null) {
            BasicUser rUser = report.getReportedBy();
            holder.textReporterName.setText(context.getString(R.string.reported_by_fmt, rUser.getFirstName(), rUser.getLastName()));
            loadMediaToImage(holder.imageReporterAvatar, rUser.getPhoto());
            holder.reportedByContainer.setOnClickListener(v -> showUserContactsDialog(context, report.getReportedBy()));
        } else {
            holder.textReporterName.setText(context.getString(R.string.reported_by_user_without_account));
            loadMediaToImage(holder.imageReporterAvatar, null);
        }

        holder.createTreatmentReportButton.setOnClickListener(v -> {
            Intent intent = new Intent(context, CreateTreatmentReportActivity.class);
            intent.putExtra("reportId", report.getId());
            context.startActivity(intent);
        });
    }

    public void addReports(List<FoundReport> newReports) {
        int start = reports.size();
        reports.addAll(newReports);
        notifyItemRangeInserted(start, newReports.size());
    }

    @Override
    public int getItemCount() {
        return reports.size();
    }

    public static class AnimalViewHolder extends RecyclerView.ViewHolder {
        ImageView photo, imageReporterAvatar;
        TextView name, breed, gender, textReporterName;
        LinearLayout reportedByContainer;
        Button createTreatmentReportButton;

        public AnimalViewHolder(@NonNull View itemView) {
            super(itemView);
            photo = itemView.findViewById(R.id.image_animal_photo);
            name = itemView.findViewById(R.id.text_animal_name);
            breed = itemView.findViewById(R.id.text_animal_breed);
            gender = itemView.findViewById(R.id.text_animal_gender);
            textReporterName = itemView.findViewById(R.id.text_reporter_name);
            imageReporterAvatar = itemView.findViewById(R.id.image_reporter_avatar);
            reportedByContainer = itemView.findViewById(R.id.reported_container);
            createTreatmentReportButton = itemView.findViewById(R.id.button_create_treatment_report);
        }
    }
}

