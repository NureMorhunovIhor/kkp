package com.kkp.nure.animalrescue.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.kkp.nure.animalrescue.R;
import com.kkp.nure.animalrescue.activities.AnimalInfoActivity;
import com.kkp.nure.animalrescue.entities.Animal;
import com.kkp.nure.animalrescue.utils.GlideMediaUrl;

import java.util.List;

public class AnimalsAdapter extends RecyclerView.Adapter<AnimalsAdapter.AnimalViewHolder> {

    private final List<Animal> animals;
    private final Context context;

    public AnimalsAdapter(Context context, List<Animal> animals) {
        this.context = context;
        this.animals = animals;
    }

    @NonNull
    @Override
    public AnimalViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_animal, parent, false);
        return new AnimalViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AnimalViewHolder holder, int position) {
        Animal animal = animals.get(position);

        holder.name.setText(animal.getName());
        holder.breed.setText(animal.getBreed());
        holder.gender.setText(animal.getGender().strName);

        if (!animal.getMedia().getResult().isEmpty()) {
            Glide.with(context)
                    .load(new GlideMediaUrl(animal.getMedia().getResult().get(0)))
                    .placeholder(R.drawable.baseline_hide_image_24)
                    .into(holder.photo);
        } else {
            holder.photo.setImageResource(R.drawable.baseline_hide_image_24);
        }

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, AnimalInfoActivity.class);
            intent.putExtra("animalId", animal.getId());
            context.startActivity(intent);
        });
    }

    public void addAnimals(List<Animal> newAnimals) {
        int start = animals.size();
        animals.addAll(newAnimals);
        notifyItemRangeInserted(start, newAnimals.size());
    }

    @Override
    public int getItemCount() {
        return animals.size();
    }

    public static class AnimalViewHolder extends RecyclerView.ViewHolder {
        ImageView photo;
        TextView name, breed, gender;

        public AnimalViewHolder(@NonNull View itemView) {
            super(itemView);
            photo = itemView.findViewById(R.id.image_animal_photo);
            name = itemView.findViewById(R.id.text_animal_name);
            breed = itemView.findViewById(R.id.text_animal_breed);
            gender = itemView.findViewById(R.id.text_animal_gender);
        }
    }
}

