package com.kkp.nure.animalrescue.adapters;

import android.app.Activity;
import android.content.Context;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.kkp.nure.animalrescue.R;
import com.kkp.nure.animalrescue.entities.BasicUser;
import com.kkp.nure.animalrescue.entities.Message;
import com.kkp.nure.animalrescue.utils.GlideMediaUrl;

import java.util.List;

import lombok.Setter;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder> {

    private static final int VIEW_TYPE_ME = 1;
    private static final int VIEW_TYPE_OTHER = 2;

    private final List<Message> messages;
    private final Context context;
    @Setter
    private BasicUser otherUser;

    public MessageAdapter(Context context, List<Message> messages) {
        this.context = context;
        this.messages = messages;
        otherUser = null;
    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(
                viewType == VIEW_TYPE_ME ? R.layout.item_message_me : R.layout.item_message_other,
                parent, false);
        return new MessageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MessageViewHolder holder, int position) {
        Message message = messages.get(position);
        holder.text.setText(message.getText());
        if(message.getMedia() != null) {
            holder.image.setVisibility(View.VISIBLE);
            holder.image.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
            holder.image.setAdjustViewBounds(true);

            DisplayMetrics displayMetrics = new DisplayMetrics();
            ((Activity)context).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

            LinearLayout messageContainer = (LinearLayout)holder.image.getParent();
            ViewGroup.LayoutParams layoutParams = messageContainer.getLayoutParams();
            //layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
            layoutParams.width = (int)(displayMetrics.widthPixels * 0.75);
            messageContainer.setLayoutParams(layoutParams);
            messageContainer.requestLayout();

            if(message.getText().trim().isEmpty()) {
                holder.text.setVisibility(View.GONE);
            }

            Glide.with(context)
                    .load(new GlideMediaUrl(message.getMedia()))
                    .placeholder(R.drawable.baseline_hide_image_24)
                    .into(holder.image);
        } else {
            holder.image.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemViewType(int position) {
        return (otherUser != null && messages.get(position).getAuthor().getId() != otherUser.getId()) ? VIEW_TYPE_ME : VIEW_TYPE_OTHER;
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    public static class MessageViewHolder extends RecyclerView.ViewHolder {
        TextView text;
        ImageView image;

        public MessageViewHolder(@NonNull View itemView) {
            super(itemView);
            text = itemView.findViewById(R.id.text_message);
            image = itemView.findViewById(R.id.message_media);
        }
    }
}

