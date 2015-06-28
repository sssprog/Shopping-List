package com.sssprog.shoppingliststandalone.ui.home;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sssprog.shoppingliststandalone.R;
import com.sssprog.shoppingliststandalone.api.database.ItemModel;
import com.sssprog.shoppingliststandalone.utils.LogHelper;

import java.util.ArrayList;
import java.util.List;

public class ListAdapter extends RecyclerView.Adapter<ListViewHolder> implements ListViewHolder.ListItemListener {

    private final Context context;
    private List<ItemModel> items = new ArrayList<>();
    private final ListAdapterListener listener;

    public ListAdapter(Context context, ListAdapterListener listener) {
        this.context = context;
        this.listener = listener;
    }

    public void setItems(List<ItemModel> items) {
        this.items = items;
        notifyDataSetChanged();
    }

    public ItemModel removeItem(int position) {
        ItemModel item = items.remove(position);
        notifyDataSetChanged();
        return item;
    }

    public void addItem(ItemModel item) {
        items.add(item);
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public boolean isEmpty() {
        return getItemCount() == 0;
    }

    @Override
    public ListViewHolder onCreateViewHolder(ViewGroup container, int position) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_in_list, container, false);
        return new ListViewHolder(view, this);
    }

    @Override
    public void onBindViewHolder(ListViewHolder holder, int position) {
        ItemModel item = items.get(position);
        holder.resetTranslation();
        LogHelper.i("-tag-", "reset translation");
        holder.title.setText(item.getName());
        holder.info.setText("1 kl = 3$, Buy ripe ones");
//        holder.info.setVisibility(View.GONE);
    }

    @Override
    public void onClick(int position) {

    }

    @Override
    public boolean onLongClick(int position) {
        return listener.onItemLongClick(items.get(position));
    }

    public interface ListAdapterListener {
        boolean onItemLongClick(ItemModel item);
    }

}
