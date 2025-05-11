package com.example.hisaabkitaab;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder> {

    private Context context;
    private List<Category> categoryList;
    private final OnCategoryEditListener editListener;
    private final OnCategoryDeleteListener deleteListener;

    public interface OnCategoryEditListener {
        void onEdit(Category category);
    }

    public interface OnCategoryDeleteListener {
        void onDelete(Category category);
    }

    public CategoryAdapter(Context context, List<Category> categoryList,
                           OnCategoryEditListener editListener,
                           OnCategoryDeleteListener deleteListener) {
        this.context = context;
        this.categoryList = categoryList;
        this.editListener = editListener;
        this.deleteListener = deleteListener;
    }

    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_category, parent, false);
        return new CategoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {
        Category category = categoryList.get(position);
        holder.textViewCategoryName.setText(category.getName());

        holder.buttonEditCategory.setOnClickListener(v -> {
            if (editListener != null) {
                editListener.onEdit(category);
            }
        });

        holder.buttonDeleteCategory.setOnClickListener(v -> {
            if (deleteListener != null) {
                deleteListener.onDelete(category);
            }
        });
    }

    @Override
    public int getItemCount() {
        return categoryList.size();
    }

    public static class CategoryViewHolder extends RecyclerView.ViewHolder {
        TextView textViewCategoryName;
        ImageButton buttonEditCategory, buttonDeleteCategory;

        public CategoryViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewCategoryName = itemView.findViewById(R.id.textViewCategoryName);
            buttonEditCategory = itemView.findViewById(R.id.buttonEditCategory);
            buttonDeleteCategory = itemView.findViewById(R.id.buttonDeleteCategory);
        }
    }
}