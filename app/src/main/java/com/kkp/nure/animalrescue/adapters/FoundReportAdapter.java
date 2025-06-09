package com.kkp.nure.animalrescue.adapters;

import static com.kkp.nure.animalrescue.utils.GlideUtils.loadMediaToImage;
import static com.kkp.nure.animalrescue.utils.MessageUtils.showUserContactsDialog;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.kkp.nure.animalrescue.R;
import com.kkp.nure.animalrescue.entities.BasicUser;
import com.kkp.nure.animalrescue.entities.FoundReport;

import org.osmdroid.api.IMapController;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;

import java.util.List;

public class FoundReportAdapter extends RecyclerView.Adapter<FoundReportAdapter.ReportViewHolder> {

    protected final List<FoundReport> reports;
    protected final Context context;

    public FoundReportAdapter(List<FoundReport> reports, Context context) {
        this.reports = reports;
        this.context = context;
    }

    public void addReports(List<FoundReport> newReports) {
        int start = reports.size();
        reports.addAll(newReports);
        notifyItemRangeInserted(start, newReports.size());
    }

    @NonNull
    @Override
    public ReportViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_animal_found_report, parent, false);
        return new ReportViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReportViewHolder holder, int position) {
        FoundReport report = reports.get(position);

        holder.status.setText(report.getAnimal().getStatus().strName);
        holder.description.setText(report.getNotes());

        MapView map = holder.mapView;
        map.setTileSource(TileSourceFactory.MAPNIK);
        map.setMultiTouchControls(true);
        IMapController controller = map.getController();
        controller.setZoom(15.0);
        GeoPoint point = new GeoPoint(report.getLocation().getLatitude(), report.getLocation().getLongitude());
        controller.setCenter(point);
        map.getOverlays().clear();
        Marker marker = new Marker(map);
        marker.setPosition(point);
        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        map.getOverlays().add(marker);

        if(report.getReportedBy() != null) {
            BasicUser rUser = report.getReportedBy();
            holder.textReporterName.setText(context.getString(R.string.reported_by_fmt, rUser.getFirstName(), rUser.getLastName()));
            loadMediaToImage(holder.imageReporterAvatar, rUser.getPhoto());
            holder.reportedByContainer.setOnClickListener(v -> showUserContactsDialog(context, rUser));
        } else {
            holder.textReporterName.setText(context.getString(R.string.reported_by_user_without_account));
            loadMediaToImage(holder.imageReporterAvatar, null);
        }

        if(report.getAssignedTo() != null) {
            BasicUser aUser = report.getAssignedTo();
            holder.textAssignedName.setText(context.getString(R.string.assigned_to_fmt, aUser.getFirstName(), aUser.getLastName()));
            loadMediaToImage(holder.imageAssignedAvatar, aUser.getPhoto());
            holder.assignedToContainer.setOnClickListener(v -> showUserContactsDialog(context, aUser));
        } else {
            holder.textAssignedName.setText(context.getString(R.string.not_assigned_to_anyone_yet));
            loadMediaToImage(holder.imageAssignedAvatar, null);
        }

        holder.photoAdapter = new PhotoCarouselAdapter(report.getMedia(), context);
        holder.photoCarousel.setAdapter(holder.photoAdapter);
    }

    @Override
    public int getItemCount() {
        return reports.size();
    }

    public static class ReportViewHolder extends RecyclerView.ViewHolder {
        TextView status, description, textReporterName, textAssignedName;
        MapView mapView;
        ImageView imageReporterAvatar, imageAssignedAvatar;
        ViewPager2 photoCarousel;
        PhotoCarouselAdapter photoAdapter;
        LinearLayout reportedByContainer, assignedToContainer;

        public ReportViewHolder(@NonNull View itemView) {
            super(itemView);
            status = itemView.findViewById(R.id.text_status);
            description = itemView.findViewById(R.id.text_description);
            mapView = itemView.findViewById(R.id.item_map);
            textReporterName = itemView.findViewById(R.id.text_reporter_name);
            textAssignedName = itemView.findViewById(R.id.text_assigned_name);
            imageReporterAvatar = itemView.findViewById(R.id.image_reporter_avatar);
            imageAssignedAvatar = itemView.findViewById(R.id.image_assigned_avatar);
            photoCarousel = itemView.findViewById(R.id.photo_carousel);
            reportedByContainer = itemView.findViewById(R.id.reported_container);
            assignedToContainer = itemView.findViewById(R.id.assigned_container);
        }
    }
}

