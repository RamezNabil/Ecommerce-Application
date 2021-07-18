package com.example.e_commerce;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Dictionary;
import java.util.Hashtable;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.constraintlayout.solver.state.State;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

public class CategoriesRecyclerAdapter extends RecyclerView.Adapter<CategoriesRecyclerAdapter.CategoriesViewHolder> {

    List<String> cats_list;
    int images[];
    Context context;
    Dictionary categories;
    String[] customer;

    public CategoriesRecyclerAdapter(Context context, List<String> cats_list, int images[], Dictionary categories, String[] customer)
    {
        this.context = context;
        this.cats_list = cats_list;
        this.images = images;
        this.categories = categories;
        this.customer = customer;
    }

    @NonNull
    @Override
    public CategoriesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.cat_row,parent,false);
        return new CategoriesViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoriesViewHolder holder, int position) {
        holder.name.setText(cats_list.get(position));
        holder.image.setImageResource(images[position]);
        int cat_id = (int)categories.get(cats_list.get(position));

        holder.cat_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, Products.class);
                // Passing category id to products
                intent.putExtra("cat_id",cat_id);
                intent.putExtra("customer",customer);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return images.length;
    }

    public class CategoriesViewHolder extends RecyclerView.ViewHolder {

        TextView name;
        ImageView image;
        ConstraintLayout cat_layout;

        public CategoriesViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.cat_name);
            image = itemView.findViewById(R.id.cat_img);
            cat_layout = itemView.findViewById(R.id.cat_row_layout);
        }
    }
}
