package com.sssprog.shoppingliststandalone.ui.history;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sssprog.shoppingliststandalone.R;
import com.sssprog.shoppingliststandalone.api.database.ItemModel;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryViewHolder> implements HistoryViewHolder.HistoryItemListener {

    private final Context context;
    private List<ItemModel> items = new ArrayList<>();
    private final HashSet<Long> selectedItems;
    private final HistoryAdapterListener listener;

    public HistoryAdapter(Context context, HashSet<Long> selectedItems, HistoryAdapterListener listener) {
        this.context = context;
        this.selectedItems = selectedItems;
        this.listener = listener;
    }

    public void setItems(List<ItemModel> items) {
        this.items = items;
        notifyDataSetChanged();
    }

    public void selectItem(ItemModel item) {
        selectedItems.add(item.getId());
    }

    public HashSet<Long> getSelectedItems() {
        return selectedItems;
    }

    public ItemModel removeItem(int position) {
        ItemModel item = items.remove(position);
        notifyDataSetChanged();
        return item;
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    @Override
    public HistoryViewHolder onCreateViewHolder(ViewGroup container, int position) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_history, container, false);
        return new HistoryViewHolder(view, this);
    }

    @Override
    public void onBindViewHolder(HistoryViewHolder holder, int position) {
        ItemModel item = items.get(position);
        holder.resetTranslation();
        holder.checkbox.setChecked(selectedItems.contains(item.getId()));
        holder.title.setText(item.getName());
    }

    @Override
    public void onClick(int position) {
        ItemModel item = items.get(position);
        if (selectedItems.contains(item.getId())) {
            selectedItems.remove(item.getId());
        } else {
            selectedItems.add(item.getId());
        }
    }

    @Override
    public boolean onLongClick(int position) {
        return listener.onItemLongClick(items.get(position));
    }

    public interface HistoryAdapterListener {
        boolean onItemLongClick(ItemModel item);
    }

}
