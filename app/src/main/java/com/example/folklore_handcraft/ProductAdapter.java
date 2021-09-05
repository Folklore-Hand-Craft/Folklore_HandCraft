package com.example.folklore_handcraft;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.amplifyframework.datastore.generated.model.AddProduct;

import java.util.List;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ViewHolder> {
    private final List<AddProduct> productsItemLists;
    private OnTaskItemClickListener listener;

    public ProductAdapter(List<AddProduct> taskItemLists, OnTaskItemClickListener listener) {
        this.productsItemLists = taskItemLists;
        this.listener = listener;
    }

    public interface OnTaskItemClickListener {
        void onItemClicked(int position);
    }
    @NonNull
    @Override
    public ProductAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_layout, parent, false);
        return  new ViewHolder(view, listener);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductAdapter.ViewHolder holder, int position) {
        AddProduct item = productsItemLists.get(position);
        holder.name.setText(item.getName());
        holder.description.setText(item.getDescription());
        holder.price.setText(item.getPrice());
        holder.contact.setText(item.getContact());
//        holder.image.setImageResource(item.getImage());

    }

    @Override
    public int getItemCount() {
        return productsItemLists.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        private ImageView image;
        private TextView name;
        private TextView description;
        private TextView price;
        private TextView contact;

        ViewHolder (@NonNull View itemView,  OnTaskItemClickListener listener){
            super(itemView);

            image = itemView.findViewById(R.id.image_list);
            name = itemView.findViewById(R.id.title_list);
            description = itemView.findViewById(R.id.body_list);
            price = itemView.findViewById(R.id.state_list);
            contact = itemView.findViewById(R.id.delete_list);



            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClicked(getAdapterPosition());
                }
            });

        }
    }

}
