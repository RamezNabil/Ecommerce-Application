package com.example.e_commerce;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DecimalFormat;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class CartRecyclerAdapter extends RecyclerView.Adapter<CartRecyclerAdapter.CartViewHolder> {

    Context context;
    List<String> names;
    List<Float> prices;
    List<Integer> quantities;
    List<Integer> ids;
    private static DecimalFormat df = new DecimalFormat("0.00");
    EcommerceDatabaseHelper db;
    TextView total_price_text;

    public CartRecyclerAdapter(Context context, List<String> names, List<Float> prices, List<Integer> quantities,
                               List<Integer> ids, EcommerceDatabaseHelper db, TextView total_price_text)
    {
        this.context = context;
        this.names = names;
        this.prices = prices;
        this.quantities = quantities;
        this.ids = ids;
        this.db = db;
        this.total_price_text = total_price_text;
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.pro_row,parent,false);
        return new CartViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {
        // Getting product info for each row
        String pro_name = names.get(position);
        float pro_price = prices.get(position);
        int pro_quantity = quantities.get(position);
        int id = ids.get(position);
        float total_price = pro_quantity*pro_price;

        // Setting product row info
        holder.name.setText(pro_name);
        holder.price.setText(df.format(pro_price) + " Egp");
        holder.items_left.setText(pro_quantity + " items in cart");
        holder.current_price.setText("");
        holder.number_ordered.setText("1");
        holder.availability.setText(df.format(total_price)+ " Egp");
        holder.remove_from_cart_btn.setText("Remove Item");

        // add item button
        holder.add_item_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int taken = Integer.parseInt(holder.number_ordered.getText().toString());
                if(taken < pro_quantity)
                {
                    taken++;
                    holder.number_ordered.setText(String.valueOf(taken));
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
                }
            }
        });

        // remove from cart button
        holder.remove_from_cart_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Updating Database and recycler view
                int taken = Integer.parseInt(holder.number_ordered.getText().toString());
                int new_quantity = 0;
                Cursor cursor = db.searchProductsID(id);
                while (!cursor.isAfterLast())
                {
                    new_quantity = cursor.getInt(2);
                    cursor.moveToNext();
                }
                new_quantity += taken;
                db.editProductQuantity(id,new_quantity);

                // Edit total price text
                String total_price_str = "";
                for(int i=0; i<total_price_text.getText().toString().length(); i++)
                {
                    if(total_price_text.getText().toString().charAt(i)!=' ')
                    {
                        total_price_str += total_price_text.getText().toString().charAt(i);
                    }
                    else
                        break;
                }
                float total_price = Float.parseFloat(total_price_str);
                total_price-=taken*pro_price;
                total_price_text.setText(df.format(total_price) + " Egp");

                // remove product from lists
                if(pro_quantity-taken == 0)
                {
                    names.remove(position);
                    prices.remove(position);
                    quantities.remove(position);
                    ids.remove(position);
                    notifyItemRemoved(position);
                    notifyItemRangeChanged(position, prices.size());
                }
                // just edit quantity
                else
                {
                    quantities.set(position, pro_quantity-taken);
                    notifyItemChanged(position);
                }

                // Updating shared prefrences
                SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putInt(String.valueOf(id),pro_quantity-taken);
                editor.apply();
            }
        });
    }

    @Override
    public int getItemCount() {
        return names.size();
    }

    public class CartViewHolder extends RecyclerView.ViewHolder {

        TextView name,price,current_price,availability,items_left,number_ordered;
        Button add_item_btn, remove_item_btn, remove_from_cart_btn;

        public CartViewHolder(@NonNull View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.product_name);
            price = itemView.findViewById(R.id.price_text);
            current_price = itemView.findViewById(R.id.current_price);
            availability = itemView.findViewById(R.id.stock_availabilty);
            items_left = itemView.findViewById(R.id.items_left);
            number_ordered = itemView.findViewById(R.id.number_ordered);

            add_item_btn = itemView.findViewById(R.id.add_button);
            remove_item_btn = itemView.findViewById(R.id.remove_button);
            remove_from_cart_btn = itemView.findViewById(R.id.add_to_cart_button);
        }
    }
}
