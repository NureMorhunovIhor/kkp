package com.kkp.nure.animalrescue.adapters;

import static com.kkp.nure.animalrescue.utils.AndroidUtils.DATE_FMT;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.kkp.nure.animalrescue.R;
import com.kkp.nure.animalrescue.activities.DialogActivity;
import com.kkp.nure.animalrescue.entities.BasicUser;
import com.kkp.nure.animalrescue.entities.Dialog;
import com.kkp.nure.animalrescue.entities.MinMessage;

import java.util.Date;
import java.util.List;

public class DialogAdapter extends RecyclerView.Adapter<DialogAdapter.MessageViewHolder> {

    private final List<Dialog> dialogList;
    private final Context context;

    public DialogAdapter(List<Dialog> dialogList, Context context) {
        this.dialogList = dialogList;
        this.context = context;
    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_message, parent, false);
        return new MessageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MessageViewHolder holder, int position) {
        Dialog dialog = dialogList.get(position);
        BasicUser dUser = dialog.getUser();

        holder.nameTextView.setText(context.getString(R.string.full_name_fmt, dUser.getFirstName(), dUser.getLastName()));
        if(dialog.getLastMessage() != null) {
            MinMessage lastMessage = dialog.getLastMessage();
            holder.timeTextView.setText(DATE_FMT.format(new Date(lastMessage.getDate() * 1000)));
            if(lastMessage.getText().isEmpty() && lastMessage.isHasMedia()) {
                holder.messageTextView.setText(R.string.media_message);
            } else if(lastMessage.getText().isEmpty()) {
                holder.messageTextView.setText(R.string.empty);
            } else if(lastMessage.isHasMedia()) {
                holder.messageTextView.setText(context.getString(R.string.media_message_fmt, lastMessage.getText()));
            } else {
                holder.messageTextView.setText(lastMessage.getText());
            }
        } else {
            holder.messageTextView.setText(R.string.no_message);
            holder.timeTextView.setText("");
        }
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, DialogActivity.class);
            intent.putExtra("userId", dialog.getUser().getId());
            context.startActivity(intent);
        });
    }

    public void addDialogs(List<Dialog> newDialogs) {
        int start = dialogList.size();
        dialogList.addAll(newDialogs);
        notifyItemRangeInserted(start, newDialogs.size());
    }

    @Override
    public int getItemCount() {
        return dialogList.size();
    }

    public static class MessageViewHolder extends RecyclerView.ViewHolder {
        ImageView avatarImageView;
        TextView nameTextView;
        TextView messageTextView;
        TextView timeTextView;

        public MessageViewHolder(@NonNull View itemView) {
            super(itemView);
            avatarImageView = itemView.findViewById(R.id.image_avatar);
            nameTextView = itemView.findViewById(R.id.text_user_name);
            messageTextView = itemView.findViewById(R.id.text_last_message);
            timeTextView = itemView.findViewById(R.id.text_time);
        }
    }
}

