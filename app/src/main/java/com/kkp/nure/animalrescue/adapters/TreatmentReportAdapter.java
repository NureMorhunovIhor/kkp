package com.kkp.nure.animalrescue.adapters;

import static com.kkp.nure.animalrescue.utils.AndroidUtils.DATE_FMT;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.kkp.nure.animalrescue.R;
import com.kkp.nure.animalrescue.entities.TreatmentReport;

import java.util.Date;
import java.util.List;

public class TreatmentReportAdapter extends RecyclerView.Adapter<TreatmentReportAdapter.ReportViewHolder> {
    private final List<TreatmentReport> reports;
    private final Context context;

    public TreatmentReportAdapter(List<TreatmentReport> reports, Context context) {
        this.reports = reports;
        this.context = context;
    }

    public void addReports(List<TreatmentReport> newReports) {
        int start = reports.size();
        reports.addAll(newReports);
        notifyItemRangeInserted(start, newReports.size());
    }

    @NonNull
    @Override
    public ReportViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_treatment_report, parent, false);
        return new ReportViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReportViewHolder holder, int position) {
        TreatmentReport report = reports.get(position);
        holder.textDescription.setText(report.getDescription());
        holder.textMoney.setText(context.getString(R.string.money_spent_fmt, report.getMoneySpent()));
        holder.textDate.setText(context.getString(R.string.date_fmt, DATE_FMT.format(new Date(report.getCreatedAt() * 1000))));
    }

    @Override
    public int getItemCount() {
        return reports.size();
    }

    public static class ReportViewHolder extends RecyclerView.ViewHolder {
        TextView textDescription, textMoney, textDate;

        public ReportViewHolder(@NonNull View itemView) {
            super(itemView);
            textDescription = itemView.findViewById(R.id.text_description);
            textMoney = itemView.findViewById(R.id.text_money);
            textDate = itemView.findViewById(R.id.text_date);
        }
    }
}

