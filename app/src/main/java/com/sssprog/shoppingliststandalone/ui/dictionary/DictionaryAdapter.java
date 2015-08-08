package com.sssprog.shoppingliststandalone.ui.dictionary;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.common.collect.Iterables;
import com.sssprog.shoppingliststandalone.R;
import com.sssprog.shoppingliststandalone.api.database.ModelWithId;
import com.sssprog.shoppingliststandalone.api.database.ModelWithName;
import com.sssprog.shoppingliststandalone.utils.Utils;

import java.util.ArrayList;
import java.util.List;

public class DictionaryAdapter<Model extends ModelWithId & ModelWithName>
        extends RecyclerView.Adapter<DictionaryViewHolder>
        implements DictionaryViewHolder.ItemListener {

    private final Context context;
    private List<Model> items = new ArrayList<>();
    private final DictionaryAdapterListener<Model> listener;

    public DictionaryAdapter(Context context, DictionaryAdapterListener<Model> listener) {
        this.context = context;
        this.listener = listener;
    }

    public void setItems(List<Model> items) {
        this.items = items;
        Utils.sortByName(items);
        itemsUpdated();
    }

    public void itemsUpdated() {
        Utils.sortByName(items);
        notifyDataSetChanged();
    }

    public Model removeItem(int position) {
        Model item = items.remove(position);
        notifyDataSetChanged();
        return item;
    }

    public void addItem(Model item) {
        items.add(item);
        Utils.sortByName(items);
        notifyDataSetChanged();
    }

    public Model getItem(final long itemId) {
        return Iterables.find(items, input -> input.getId() == itemId, null);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public boolean isEmpty() {
        return getItemCount() == 0;
    }

    @Override
    public DictionaryViewHolder onCreateViewHolder(ViewGroup container, int position) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_dictionary, container, false);
        return new DictionaryViewHolder(view, this);
    }

    @Override
    public void onBindViewHolder(DictionaryViewHolder holder, int position) {
        Model item = items.get(position);
        holder.resetTranslation();
        holder.title.setText(item.getName());
    }

    @Override
    public boolean onItemLongClick(int position) {
        listener.onItemLongClick(items.get(position));
        return true;
    }

    public interface DictionaryAdapterListener<Model> {
        void onItemLongClick(Model item);
    }

}
