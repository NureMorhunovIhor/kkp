package com.kkp.nure.animalrescue.utils;

import static com.kkp.nure.animalrescue.utils.GlideUtils.loadMediaToImage;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.kkp.nure.animalrescue.R;
import com.kkp.nure.animalrescue.api.ApiClient;
import com.kkp.nure.animalrescue.api.requests.SendMessageRequest;
import com.kkp.nure.animalrescue.api.services.MessageService;
import com.kkp.nure.animalrescue.entities.BasicUser;
import com.kkp.nure.animalrescue.entities.Message;

public class MessageUtils {
    public static void showUserContactsDialog(Context context, BasicUser user) {
        BottomSheetDialog dialog = new BottomSheetDialog(context);
        View dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_contact_user, null, false);
        dialog.setContentView(dialogView);

        ImageView userPhoto = dialogView.findViewById(R.id.image_user_photo);
        TextView userName = dialogView.findViewById(R.id.text_user_name);
        LinearLayout telegramContainer = dialogView.findViewById(R.id.telegram_container);
        TextView textTelegram = dialogView.findViewById(R.id.text_telegram);
        LinearLayout viberContainer = dialogView.findViewById(R.id.viber_container);
        TextView textViber = dialogView.findViewById(R.id.text_viber);
        LinearLayout whatsappContainer = dialogView.findViewById(R.id.whatsapp_container);
        TextView textWhatsapp = dialogView.findViewById(R.id.text_whatsapp);
        Button sendMessage = dialogView.findViewById(R.id.button_send_message);
        Button close = dialogView.findViewById(R.id.button_close);

        loadMediaToImage(userPhoto, user.getPhoto());
        userName.setText(context.getString(R.string.full_name_fmt, user.getFirstName(), user.getLastName()));

        if(user.getTelegramUsername() != null) {
            telegramContainer.setVisibility(View.VISIBLE);
            textTelegram.setText(context.getString(R.string.telegram_username_fmt, user.getTelegramUsername()));
        } else {
            telegramContainer.setVisibility(View.GONE);
        }

        if(user.getViberPhone() != null) {
            viberContainer.setVisibility(View.VISIBLE);
            textViber.setText(user.getViberPhone());
        } else {
            viberContainer.setVisibility(View.GONE);
        }

        if(user.getWhatsappPhone() != null) {
            whatsappContainer.setVisibility(View.VISIBLE);
            textWhatsapp.setText(user.getWhatsappPhone());
        } else {
            whatsappContainer.setVisibility(View.GONE);
        }

        sendMessage.setOnClickListener(v -> showSendMessageDialog(context, user));
        close.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }

    public static void showSendMessageDialog(Context context, BasicUser user) {
        SharedPreferences prefs = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE);
        String token = prefs.getString("authToken", null);
        if(token == null) {
            Toast.makeText(context, R.string.you_need_to_login_to_send_messages, Toast.LENGTH_SHORT).show();
            return;
        }

        AlertDialog.Builder dialog = new AlertDialog.Builder(context);
        dialog.setTitle("Send message to "+user.getFirstName());

        LinearLayout layout = new LinearLayout(context);
        layout.setOrientation(LinearLayout.VERTICAL);

        EditText messageInput = new EditText(context);
        messageInput.setInputType(InputType.TYPE_TEXT_FLAG_MULTI_LINE);
        messageInput.setMaxLines(10);
        messageInput.setHint(R.string.text);
        layout.addView(messageInput);

        dialog.setView(layout);

        dialog.setPositiveButton(R.string.send, (di, arg1) -> {
            String message = messageInput.getText().toString();
            MessageService messageService = ApiClient.getAuthClient(token).create(MessageService.class);

            messageService.sendMessage(user.getId(), new SendMessageRequest(message, null)).enqueue(new ApiClient.CustomCallback<>() {
                @Override
                public void onResponse(@NonNull Message response) {
                    Toast.makeText(context, R.string.message_sent, Toast.LENGTH_SHORT).show();
                    di.dismiss();
                }

                @Override
                public void onError(@NonNull String error, int code) {
                    Toast.makeText(context, context.getString(R.string.failed_to_send_message_fmt, error), Toast.LENGTH_SHORT).show();
                }
            });
        });
        dialog.setNegativeButton(R.string.close, (di, arg1) -> {
            di.dismiss();
        });

        dialog.show();
    }
}
