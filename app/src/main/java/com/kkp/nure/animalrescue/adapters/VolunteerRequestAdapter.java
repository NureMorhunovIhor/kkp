package com.kkp.nure.animalrescue.adapters;

import static com.kkp.nure.animalrescue.utils.AndroidUtils.DATE_FMT;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.kkp.nure.animalrescue.R;
import com.kkp.nure.animalrescue.entities.VolunteerRequest;

import java.util.Date;
import java.util.List;

public class VolunteerRequestAdapter extends RecyclerView.Adapter<VolunteerRequestAdapter.RequestViewHolder> {
    private final List<VolunteerRequest> requests;
    private final Context context;

    public VolunteerRequestAdapter(List<VolunteerRequest> requests, Context context) {
        this.requests = requests;
        this.context = context;
    }

    public void setRequests(List<VolunteerRequest> newRequests) {
        requests.clear();
        requests.addAll(newRequests);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RequestViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_volunteer_request, parent, false);
        return new RequestViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RequestViewHolder holder, int position) {
        VolunteerRequest request = requests.get(position);

        holder.textStatus.setText(context.getString(R.string.status_fmt, request.getStatus().strName));
        holder.textRequestDate.setText(context.getString(R.string.request_date_fmt, DATE_FMT.format(new Date(request.getCreatedAt() * 1000))));

        if (request.getReviewedAt() != null) {
            holder.textReviewDate.setVisibility(View.VISIBLE);
            holder.textReviewText.setVisibility(View.VISIBLE);

            holder.textReviewDate.setText(context.getString(R.string.review_date_fmt, DATE_FMT.format(new Date(request.getReviewedAt() * 1000))));
            holder.textReviewText.setText(request.getReviewText());
        } else {
            holder.textReviewDate.setVisibility(View.GONE);
            holder.textReviewText.setVisibility(View.GONE);
        }

        holder.textFullName.setText(request.getFullName());
        holder.textPhoneNumber.setText(request.getPhoneNumber());
        holder.textCity.setText(request.getCity());
        holder.textDescription.setText(request.getText());
        holder.checkHasVehicle.setChecked(request.isHasVehicle());
        holder.checkAvailabilityWeekdays.setChecked((request.getAvailability() & VolunteerRequest.AVAILABILITY_WEEKDAYS) > 0);
        holder.checkAvailabilityWeekends.setChecked((request.getAvailability() & VolunteerRequest.AVAILABILITY_WEEKENDS) > 0);
        holder.checkHelpShelter.setChecked((request.getHelp() & VolunteerRequest.HELP_SHELTER) > 0);
        holder.checkHelpClinicDelivery.setChecked((request.getHelp() & VolunteerRequest.HELP_CLINIC_DELIVERY) > 0);
        holder.checkHelpOnsiteVisit.setChecked((request.getHelp() & VolunteerRequest.HELP_ONSITE_VISIT) > 0);
        holder.checkHelpMedicalCare.setChecked((request.getHelp() & VolunteerRequest.HELP_MEDICAL_CARE) > 0);
        holder.checkHelpInformation.setChecked((request.getHelp() & VolunteerRequest.HELP_INFORMATION) > 0);
        holder.textTelegramUsername.setText(request.getTelegramUsername());
        holder.textViberPhone.setText(request.getViberPhone());
        holder.textWhatsappPhone.setText(request.getWhatsappPhone());
    }

    @Override
    public int getItemCount() {
        return requests.size();
    }

    public static class RequestViewHolder extends RecyclerView.ViewHolder {
        TextView textFullName;
        TextView textPhoneNumber;
        TextView textCity;
        TextView textDescription;
        CheckBox checkHasVehicle;
        CheckBox checkAvailabilityWeekdays;
        CheckBox checkAvailabilityWeekends;
        CheckBox checkHelpShelter;
        CheckBox checkHelpClinicDelivery;
        CheckBox checkHelpOnsiteVisit;
        CheckBox checkHelpMedicalCare;
        CheckBox checkHelpInformation;
        TextView textTelegramUsername;
        TextView textViberPhone;
        TextView textWhatsappPhone;
        TextView textStatus;
        TextView textRequestDate;
        TextView textReviewDate;
        TextView textReviewText;

        public RequestViewHolder(@NonNull View itemView) {
            super(itemView);
            textFullName = itemView.findViewById(R.id.text_full_name);
            textPhoneNumber = itemView.findViewById(R.id.text_phone_number);
            textCity = itemView.findViewById(R.id.text_city);
            textDescription = itemView.findViewById(R.id.text_description);
            checkHasVehicle = itemView.findViewById(R.id.check_has_vehicle);
            checkAvailabilityWeekdays = itemView.findViewById(R.id.check_availability_weekdays);
            checkAvailabilityWeekends = itemView.findViewById(R.id.check_availability_weekends);
            checkHelpShelter = itemView.findViewById(R.id.check_help_shelter);
            checkHelpClinicDelivery = itemView.findViewById(R.id.check_help_clinic_delivery);
            checkHelpOnsiteVisit = itemView.findViewById(R.id.check_help_onsite_visit);
            checkHelpMedicalCare = itemView.findViewById(R.id.check_help_medical_care);
            checkHelpInformation = itemView.findViewById(R.id.check_help_information);
            textTelegramUsername = itemView.findViewById(R.id.text_telegram_username);
            textViberPhone = itemView.findViewById(R.id.text_viber_phone);
            textWhatsappPhone = itemView.findViewById(R.id.text_whatsapp_phone);
            textStatus = itemView.findViewById(R.id.text_status);
            textRequestDate = itemView.findViewById(R.id.text_request_date);
            textReviewDate = itemView.findViewById(R.id.text_review_date);
            textReviewText = itemView.findViewById(R.id.text_review_text);
        }
    }
}

