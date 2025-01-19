package com.example.kisanbuddy;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.material.card.MaterialCardView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class MandiAdapter extends RecyclerView.Adapter<MandiAdapter.MandiViewHolder> {

    private final List<Mandi> mandis;
    private final Context context;

    // Constructor to pass the list of mandis and context
    public MandiAdapter(List<Mandi> mandis, Context context) {
        this.mandis = mandis;
        this.context = context;
    }

    @NonNull
    @Override
    public MandiViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate the card view layout
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.mandi_card, parent, false);
        return new MandiViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MandiViewHolder holder, int position) {
        // Get the current mandi object
        Mandi mandi = mandis.get(position);

        // Bind the mandi details to the views
        holder.name.setText("Market: " + mandi.getName());
        holder.cropName.setText("Crop: " + mandi.getCropName());
        holder.price.setText("Price: ₹" + mandi.getPrice());
        holder.profit.setText("Profit: ₹" + mandi.getProfit());
        holder.district.setText("District: " + mandi.getDistrict());
        holder.state.setText("State: " + mandi.getState());
        // Highlight the first card with a max profit
        if (position == 0) {
            ((MaterialCardView) holder.itemView).setCardBackgroundColor(Color.parseColor("#008000")); // Green background
            holder.name.setText("Market: " + mandi.getName() + " (Recommended)");
            holder.cropName.setTextColor(Color.WHITE);
            holder.price.setTextColor(Color.WHITE);
            holder.profit.setTextColor(Color.WHITE);
            holder.district.setTextColor(Color.WHITE);
            holder.state.setTextColor(Color.WHITE);
        } else {
            // Reset for other cards
            ((MaterialCardView) holder.itemView).setCardBackgroundColor(Color.WHITE); // Default white background
            holder.name.setTextColor(Color.BLACK);
            holder.cropName.setTextColor(Color.BLACK);
            holder.price.setTextColor(Color.BLACK);
            holder.profit.setTextColor(Color.BLACK);
            holder.district.setTextColor(Color.BLACK);
            holder.state.setTextColor(Color.BLACK);
        }

        // Set click listener for the card
        holder.itemView.setOnClickListener(v -> {
            try {
                String query = mandi.getName() + ", " + mandi.getDistrict() + ", " + mandi.getState();
                Uri gmmIntentUri = Uri.parse("geo:0,0?q=" + Uri.encode(query));
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                mapIntent.setPackage("com.google.android.apps.maps");

                // Attempt to launch Google Maps
                context.startActivity(mapIntent);
            } catch (Exception e) {
                // Fall back to a browser if Google Maps is unavailable
                String query = mandi.getName() + ", " + mandi.getDistrict() + ", " + mandi.getState();
                Uri browserUri = Uri.parse("https://www.google.com/maps/search/?api=1&query=" + Uri.encode(query));
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, browserUri);
                context.startActivity(browserIntent);
            }
        });
    }

    @Override
    public int getItemCount() {
        // Return the size of the mandis list
        return mandis.size();
    }

    // Inner ViewHolder class
    public static class MandiViewHolder extends RecyclerView.ViewHolder {
        TextView name, cropName, price, profit, district, state;

        public MandiViewHolder(@NonNull View itemView) {
            super(itemView);

            // Initialize the views
            name = itemView.findViewById(R.id.mandiName);
            cropName = itemView.findViewById(R.id.mandiCropName);
            price = itemView.findViewById(R.id.mandiPrice);
            profit = itemView.findViewById(R.id.mandiProfit);
            district = itemView.findViewById(R.id.mandiDistrict);
            state = itemView.findViewById(R.id.mandiState);
        }
    }
}
