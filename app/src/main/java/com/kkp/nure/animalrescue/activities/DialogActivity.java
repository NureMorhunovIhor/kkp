package com.kkp.nure.animalrescue.activities;

import static com.kkp.nure.animalrescue.utils.AndroidUtils.getLongExtraOrDie;
import static com.kkp.nure.animalrescue.utils.AndroidUtils.getTokenOrDie;
import static com.kkp.nure.animalrescue.utils.UriUtil.readUriToByteArray;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.kkp.nure.animalrescue.R;
import com.kkp.nure.animalrescue.adapters.MessageAdapter;
import com.kkp.nure.animalrescue.api.ApiClient;
import com.kkp.nure.animalrescue.api.requests.SendMessageRequest;
import com.kkp.nure.animalrescue.api.responses.PaginatedResponse;
import com.kkp.nure.animalrescue.api.services.MediaService;
import com.kkp.nure.animalrescue.api.services.MessageService;
import com.kkp.nure.animalrescue.entities.BasicUser;
import com.kkp.nure.animalrescue.entities.Media;
import com.kkp.nure.animalrescue.entities.Message;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class DialogActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private MessageAdapter adapter;
    private List<Message> messages = new ArrayList<>();
    private EditText editMessage;
    private Toolbar toolbar;

    private boolean isLoading = false;
    private Long minMessageId = null;
    private final int limit = 10;
    private boolean hasMore = true;

    private long dialogUserId;
    private MessageService messageService;
    private MediaService mediaService;
    private BasicUser otherUser = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_dialog);

        String token = getTokenOrDie(this);
        if(token == null) return;

        Long dialogUserIdOpt = getLongExtraOrDie(this, "userId");
        if(dialogUserIdOpt == null) return;
        dialogUserId = dialogUserIdOpt;

        messageService = ApiClient.getAuthClient(token).create(MessageService.class);
        mediaService = ApiClient.getAuthClient(token).create(MediaService.class);

        toolbar = findViewById(R.id.chat_toolbar);
        toolbar.setTitle(R.string.other_user);
        setSupportActionBar(toolbar);

        recyclerView = findViewById(R.id.recycler_messages);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(false);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new MessageAdapter(this, messages);
        recyclerView.setAdapter(adapter);

        editMessage = findViewById(R.id.edit_message);
        findViewById(R.id.button_send).setOnClickListener(v -> sendMessage());
        findViewById(R.id.button_send_media).setOnClickListener(v -> sendMedia());

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                LinearLayoutManager lm = (LinearLayoutManager) recyclerView.getLayoutManager();
                if (!isLoading && lm != null && lm.findLastVisibleItemPosition() >= messages.size() - 1) {
                    loadMoreMessages();
                }
            }
        });

        loadMoreMessages();

        // TODO: poll for new messages every 10 seconds
    }

    private void realSendMessage(String text, Long mediaId, boolean clearText) {
        messageService.sendMessage(dialogUserId, new SendMessageRequest(text, mediaId)).enqueue(new ApiClient.CustomCallback<>() {
            @Override
            public void onResponse(@NonNull Message response) {
                messages.add(0, response);
                adapter.notifyItemInserted(0);
                Toast.makeText(DialogActivity.this, R.string.message_sent, Toast.LENGTH_SHORT).show();

                recyclerView.scrollToPosition(0);
                if(clearText) {
                    editMessage.setText("");
                }
            }

            @Override
            public void onError(@NonNull String error, int code) {
                Toast.makeText(DialogActivity.this, getString(R.string.failed_to_send_message_fmt, error), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void sendMessage() {
        String text = editMessage.getText().toString().trim();
        if (text.isEmpty()) {
            return;
        }

        realSendMessage(text, null, true);
    }

    private void sendMedia() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("*/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, false);
        intent.putExtra(Intent.EXTRA_MIME_TYPES, new String[]{"image/*"});
        startActivityForResult(Intent.createChooser(intent, getString(R.string.select_media)), 1001);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1001 && resultCode == RESULT_OK && data != null) {
            Uri selectedMedia = data.getData();
            byte[] mediaToUpload = readUriToByteArray(DialogActivity.this, selectedMedia);
            if(mediaToUpload == null) {
                Toast.makeText(DialogActivity.this, R.string.failed_to_read_image, Toast.LENGTH_SHORT).show();
                return;
            }

            new MediaService.Uploader(mediaService).uploadPhoto(mediaToUpload, new MediaService.PhotoUploadedCallback() {
                @Override
                public void onSuccess(Media media) {
                    realSendMessage("", media.getId(), false);
                }

                @Override
                public void onError(String error) {
                    Toast.makeText(DialogActivity.this, getString(R.string.failed_to_upload_photo_fmt, error), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void loadMoreMessages() {
        isLoading = true;
        if(!hasMore)
            return;

        messageService.getMessages(dialogUserId, minMessageId, limit).enqueue(new ApiClient.CustomCallback<>() {
            @Override
            public void onResponse(@NonNull PaginatedResponse<Message> response) {
                if(!response.getResult().isEmpty() && otherUser == null) {
                    otherUser = response.getResult().get(0).getDialog().getUser();
                    toolbar.setTitle(getString(R.string.full_name_fmt, otherUser.getFirstName(), otherUser.getLastName()));
                    adapter.setOtherUser(otherUser);
                    adapter.notifyDataSetChanged();
                }

                int oldSize = messages.size();
                messages.addAll(response.getResult());
                adapter.notifyItemRangeInserted(oldSize, response.getResult().size());

                minMessageId = response.getResult().stream().map(Message::getId).min(Comparator.comparingLong(aLong -> aLong)).orElse(0L);

                if(messages.size() >= response.getCount()) {
                    hasMore = false;
                }

                isLoading = false;
            }

            @Override
            public void onError(@NonNull String error, int code) {
                Toast.makeText(DialogActivity.this, getString(R.string.failed_to_fetch_messages, error), Toast.LENGTH_SHORT).show();
            }
        });
    }
}