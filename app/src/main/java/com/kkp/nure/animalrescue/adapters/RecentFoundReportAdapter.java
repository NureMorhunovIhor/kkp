package com.kkp.nure.animalrescue.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.kkp.nure.animalrescue.R;
import com.kkp.nure.animalrescue.api.ApiClient;
import com.kkp.nure.animalrescue.api.services.AnimalReportService;
import com.kkp.nure.animalrescue.entities.FoundReport;

import java.util.List;

public class RecentFoundReportAdapter extends FoundReportAdapter {
    private final AnimalReportService reportService;

    public RecentFoundReportAdapter(List<FoundReport> reports, Context context, AnimalReportService reportService) {
        super(reports, context);
        this.reportService = reportService;
    }

    @NonNull
    @Override
    public RecentReportViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_recent_found_report, parent, false);
        return new RecentReportViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReportViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);

        if(!(holder instanceof RecentReportViewHolder)) {
            return;
        }

        FoundReport report = reports.get(position);

        ((RecentReportViewHolder)holder).name.setText(report.getAnimal().getName());
        ((RecentReportViewHolder)holder).breed.setText(report.getAnimal().getBreed());
        ((RecentReportViewHolder)holder).gender.setText(report.getAnimal().getGender().strName);

        if(report.getAssignedTo() == null) {
            ((RecentReportViewHolder) holder).claimButton.setOnClickListener(v -> {
                reportService.claimReport(report.getId()).enqueue(new ApiClient.CustomCallback<>() {
                    @Override
                    public void onResponse(@NonNull FoundReport response) {
                        int adapterPosition = holder.getAdapterPosition();
                        reports.set(adapterPosition, response);
                        notifyItemChanged(adapterPosition);
                    }

                    @Override
                    public void onError(@NonNull String error, int code) {
                        Toast.makeText(context, context.getString(R.string.failed_to_assign_report, error), Toast.LENGTH_SHORT).show();
                    }
                });
            });
        } else {
            ((RecentReportViewHolder) holder).claimButton.setVisibility(View.GONE);
        }
    }

    public static class RecentReportViewHolder extends ReportViewHolder {
        TextView name, breed, gender;
        Button claimButton;

        public RecentReportViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.text_name);
            breed = itemView.findViewById(R.id.text_breed);
            gender = itemView.findViewById(R.id.text_gender);
            claimButton = itemView.findViewById(R.id.button_claim_report);
        }
    }
}

