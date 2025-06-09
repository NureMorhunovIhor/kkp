package com.kkp.nure.animalrescue.fragments;

import static com.kkp.nure.animalrescue.utils.AndroidUtils.getTokenOrDie;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.kkp.nure.animalrescue.R;
import com.kkp.nure.animalrescue.adapters.AnimalsAdapter;
import com.kkp.nure.animalrescue.api.ApiClient;
import com.kkp.nure.animalrescue.api.responses.PaginatedResponse;
import com.kkp.nure.animalrescue.api.services.SubscriptionService;
import com.kkp.nure.animalrescue.entities.Animal;

import java.util.ArrayList;

public class MyAnimalsFragment extends CustomFragment {

    private RecyclerView recyclerView;
    private AnimalsAdapter adapter;
    private boolean isLoading = false;
    private int page = 1;
    private final int pageSize = 10;
    private boolean hasMore = true;

    private SubscriptionService subscriptionService;

    public MyAnimalsFragment() {
        super(R.layout.fragment_my_animals);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_my_animals, container, false);

        String token = getTokenOrDie(requireActivity());
        if(token == null) return view;
        subscriptionService = ApiClient.getAuthClient(token).create(SubscriptionService.class);

        recyclerView = view.findViewById(R.id.my_animals_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new AnimalsAdapter(getContext(), new ArrayList<>());
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

        return view;
    }

    private void loadNextPage() {
        isLoading = true;
        if(!hasMore)
            return;

        subscriptionService.getSubscriptions(page, pageSize).enqueue(new ApiClient.CustomCallback<>() {
            @Override
            public void onResponse(@NonNull PaginatedResponse<Animal> response) {
                adapter.addAnimals(response.getResult());
                page++;

                if(adapter.getItemCount() >= response.getCount()) {
                    hasMore = false;
                }

                isLoading = false;
            }

            @Override
            public void onError(@NonNull String error, int code) {
                Toast.makeText(getContext(), getString(R.string.failed_to_fetch_animal_reports, error), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
