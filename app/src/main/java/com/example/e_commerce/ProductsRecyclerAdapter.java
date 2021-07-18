package com.example.e_commerce;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Paint;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ProductsRecyclerAdapter extends RecyclerView.Adapter<ProductsRecyclerAdapter.ProductsViewHolder> {

    Context context;
    List<String> names;
    List<Float> prices;
    List<Integer> quantities;
    List<Integer> ids;
    private static DecimalFormat df = new DecimalFormat("0.00");
    EcommerceDatabaseHelper db;

    public ProductsRecyclerAdapter(Context context, List<String> names, List<Float> prices, List<Integer> quantities, List<Integer> ids, EcommerceDatabaseHelper db)
    {
        this.context = context;
        this.names = names;
        this.prices = prices;
        this.quantities = quantities;
        this.ids = ids;
        this.db = db;
    }

    // Inflating Recycler View with row made
    @NonNull
    @Override
    public ProductsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.pro_row,parent,false);
        return new ProductsViewHolder(view);
    }

    // Setting TextViews and Buttons
    @Override
    public void onBindViewHolder(@NonNull ProductsViewHolder holder, int position) {
        // Getting product info for each row
        String pro_name = names.get(position);
        float pro_price = prices.get(position);
        int pro_quantity = quantities.get(position);
        int id = ids.get(position);

        // Setting product row info
        holder.name.setText(pro_name);
        holder.price.setText(df.format(pro_price) + " Egp");
        holder.items_left.setText(pro_quantity + " items left");
        holder.current_price.setText(df.format(pro_price )+ " Egp");
        holder.number_ordered.setText("1");

        // Setting Stock Status
        if(pro_quantity > 0)
        {
            holder.availability.setText("In Stock");
            holder.availability.setTextColor(Color.GREEN);
        }
        else
        {
            holder.availability.setText("Out of Stock");
            holder.availability.setTextColor(Color.RED);
            holder.number_ordered.setText("0");
            holder.current_price.setText("");
        }

        // add item button
        holder.add_item_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int taken = Integer.parseInt(holder.number_ordered.getText().toString());
                if(taken < pro_quantity)
                {
                    taken++;
                    holder.number_ordered.setText(String.valueOf(taken));
                    holder.current_price.setText(df.format(taken * pro_price) + " Egp");
                }
            }
        });

        // remove item button
        holder.remove_item_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int taken = Integer.parseInt(holder.number_ordered.getText().toString());
                if(taken > 1)
                {
                    taken--;
                    holder.number_ordered.setText(String.valueOf(taken));
                    holder.current_price.setText(df.format(taken*pro_price) + " Egp");
                }
            }
        });

        // add to cart button
        holder.add_to_cart_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // Can't buy
                if(holder.number_ordered.getText().toString().equals("0"))
                {
                    return;
                }

                // Updating Database and recycler view
                int taken = Integer.parseInt(holder.number_ordered.getText().toString());
                db.editProductQuantity(id,pro_quantity-taken);
                quantities.set(position, pro_quantity-taken);
                notifyItemChanged(position);

                // Putting selected items in a shared pref
                SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
                int cart_items = sharedPreferences.getInt(String.valueOf(id),0);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putInt(String.valueOf(id),cart_items+taken);
                editor.apply();
            }
        });

    }

    @Override
    public int getItemCount() {
        return names.size();
    }

    // Retriving TextViews and Buttons
    public class ProductsViewHolder extends RecyclerView.ViewHolder {

        TextView name,price,current_price,availability,items_left,number_ordered;
        Button add_item_btn, remove_item_btn, add_to_cart_btn;

        public ProductsViewHolder(@NonNull View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.product_name);
            price = itemView.findViewById(R.id.price_text);
            current_price = itemView.findViewById(R.id.current_price);
            availability = itemView.findViewById(R.id.stock_availabilty);
            items_left = itemView.findViewById(R.id.items_left);
            number_ordered = itemView.findViewById(R.id.number_ordered);

            add_item_btn = itemView.findViewById(R.id.add_button);
            remove_item_btn = itemView.findViewById(R.id.remove_button);
            add_to_cart_btn = itemView.findViewById(R.id.add_to_cart_button);
        }
    }
}
