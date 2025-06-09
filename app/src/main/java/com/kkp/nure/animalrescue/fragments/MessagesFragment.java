package com.kkp.nure.animalrescue.fragments;

import static com.kkp.nure.animalrescue.utils.AndroidUtils.getTokenOrDie;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.kkp.nure.animalrescue.R;
import com.kkp.nure.animalrescue.adapters.DialogAdapter;
import com.kkp.nure.animalrescue.api.ApiClient;
import com.kkp.nure.animalrescue.api.responses.PaginatedResponse;
import com.kkp.nure.animalrescue.api.services.MessageService;
import com.kkp.nure.animalrescue.entities.Dialog;

import java.util.ArrayList;

public class MessagesFragment extends CustomFragment {

    private RecyclerView recyclerView;
    private DialogAdapter adapter;
    private boolean isLoading = false;
    private int page = 1;
    private final int pageSize = 10;
    private boolean hasMore = true;

    private MessageService messageService;

    public MessagesFragment() {
        super(R.layout.fragment_messages);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        String token = getTokenOrDie(requireActivity());
        if(token == null) return;
        messageService = ApiClient.getAuthClient(token).create(MessageService.class);

        recyclerView = view.findViewById(R.id.recycler_view_messages);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        adapter = new DialogAdapter(new ArrayList<>(), getContext());
        recyclerView.setAdapter(adapter);

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                if (!isLoading && !recyclerView.canScrollVertically(1)) {
                    loadNextPage();
                }
            }
        });

        loadNextPage();
    }

    private void loadNextPage() {
        isLoading = true;
        if(!hasMore)
            return;

        messageService.getDialogs(page, pageSize).enqueue(new ApiClient.CustomCallback<>() {
            @Override
            public void onResponse(@NonNull PaginatedResponse<Dialog> response) {
                adapter.addDialogs(response.getResult());
                page++;

                if(adapter.getItemCount() >= response.getCount()) {
                    hasMore = false;
                }

                isLoading = false;
            }

            @Override
            public void onError(@NonNull String error, int code) {
                Toast.makeText(getContext(), getString(R.string.failed_to_fetch_dialogs, error), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
