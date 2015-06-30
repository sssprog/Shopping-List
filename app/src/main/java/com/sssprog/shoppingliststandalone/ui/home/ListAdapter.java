package com.sssprog.shoppingliststandalone.ui.home;

import android.content.Context;
import android.graphics.Paint;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;
import com.sssprog.shoppingliststandalone.R;
import com.sssprog.shoppingliststandalone.api.SimpleRxSubscriber;
import com.sssprog.shoppingliststandalone.api.database.ItemModel;
import com.sssprog.shoppingliststandalone.api.services.ItemService;
import com.sssprog.shoppingliststandalone.utils.NumberUtils;
import com.sssprog.shoppingliststandalone.utils.Prefs;
import com.sssprog.shoppingliststandalone.utils.Utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements ListViewHolder.ListItemListener {

    private final Context context;
    private List<ItemModel> listItems = new ArrayList<>();
    private List<AdapterItem> items = new ArrayList<>();
    private final ListAdapterListener listener;

    private int titleTextColor;
    private int titleStruckTextColor;
    private int oneLineHeight;
    private int twoLineHeight;
    private boolean useAlwaysTwoLineHeight;

    public ListAdapter(Context context, ListAdapterListener listener) {
        this.context = context;
        this.listener = listener;
        titleTextColor = context.getResources().getColor(R.color.text_primary);
        titleStruckTextColor = context.getResources().getColor(R.color.text_secondary);
        oneLineHeight = context.getResources().getDimensionPixelSize(R.dimen.list_item_height_one_line);
        twoLineHeight = context.getResources().getDimensionPixelSize(R.dimen.list_item_height_two_lines);
    }

    public void setItems(List<ItemModel> items) {
        this.listItems = items;
        update();
    }

    public ItemModel removeItem(int position) {
        AdapterItem item = items.get(position);
        listItems.remove(item.item);
        update();
        return item.item;
    }

    public void addItem(ItemModel item) {
        listItems.add(item);
        update();
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public boolean isEmpty() {
        return getItemCount() == 0;
    }

    @Override
    public int getItemViewType(int position) {
        return items.get(position).item != null ? 0 : 1;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup container, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        return viewType == 0 ?
                new ListViewHolder(inflater.inflate(R.layout.item_in_list, container, false), this) :
                new CategoryViewHolder(inflater.inflate(R.layout.list_subheader, container, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        AdapterItem item = items.get(position);
        if (item.item != null) {
            onBindItem((ListViewHolder) holder, item.item);
        } else {
            CategoryViewHolder categoryHolder = (CategoryViewHolder) holder;
            categoryHolder.title.setText(item.category);
        }
    }

    private void onBindItem(ListViewHolder holder, ItemModel item) {
        holder.resetTranslation();
        holder.title.setText(item.getName());
        if (item.isStruckOut()) {
            holder.title.setTextColor(titleStruckTextColor);
            holder.title.setPaintFlags(holder.title.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        } else {
            holder.title.setTextColor(titleTextColor);
            holder.title.setPaintFlags(holder.title.getPaintFlags() & ~Paint.STRIKE_THRU_TEXT_FLAG);
        }
        String text = getDetailsText(item);
        if (TextUtils.isEmpty(text)) {
            holder.info.setVisibility(View.GONE);
            holder.root.getLayoutParams().height = useAlwaysTwoLineHeight ? twoLineHeight : oneLineHeight;
        } else {
            holder.info.setVisibility(View.VISIBLE);
            holder.info.setText(text);
            holder.root.getLayoutParams().height = twoLineHeight;
        }
    }

    private String getDetailsText(ItemModel item) {
        String text = "";
        if (NumberUtils.numberGreater(item.getQuantity(), 0) &&
                (!NumberUtils.numberEquals(item.getQuantity(), 1) || NumberUtils.numberGreater(item.getPrice(), 0))) {
            text = NumberUtils.quantityToString(item.getQuantity());
            if (item.getQuantityUnit() != null) {
                text += " " + item.getQuantityUnit().getName();
            } else {
                text = context.getString(R.string.qty) + " " + text;
            }
            // price
            if (NumberUtils.numberGreater(item.getPrice(), 0)) {
                text += "  =  " + NumberUtils.priceWithCurrency(item.getTotalPrice());
            }
        }

        if (!TextUtils.isEmpty(item.getNote())) {
            if (!TextUtils.isEmpty(text)) {
                text += ", ";
            }
            String note = item.getNote().replace('\n', ' ');
            text += note;
        }
        return text;
    }

    private void updateAdapterItems() {
        Utils.sortByName(listItems);
        items.clear();
        boolean struckOutAtBottom = Prefs.getBoolean(R.string.prefs_move_striked_out_items_to_bottom);
        ListMultimap<String, ItemModel> map = ArrayListMultimap.create();
        List<ItemModel> noCategory = new ArrayList<>();
        List<ItemModel> struckOut = new ArrayList<>();
        for (ItemModel item : listItems) {
            if (struckOutAtBottom && item.isStruckOut()) {
                struckOut.add(item);
            } else if (item.getCategory() == null) {
                noCategory.add(item);
            } else {
                map.put(item.getCategory().getName(), item);
            }
        }

        List<String> categories = new ArrayList<>(map.keySet());
        Collections.sort(categories);
        for (String category : categories) {
            items.add(new AdapterItem(null, category));
            addItems(map.get(category));
        }
        if (!noCategory.isEmpty()) {
            items.add(new AdapterItem(null, context.getString(R.string.default_category)));
            addItems(noCategory);
        }
        if (!struckOut.isEmpty()) {
            items.add(new AdapterItem(null, context.getString(R.string.crossed_off)));
            addItems(struckOut);
        }
    }

    private void addItems(List<ItemModel> list) {
        for (ItemModel item : list) {
            items.add(new AdapterItem(item, null));
        }
    }

    public void update() {
        updateAdapterItems();
        useAlwaysTwoLineHeight = Prefs.isListHeightAlwaysLarge();
        notifyDataSetChanged();
    }

    @Override
    public void onClick(int position) {
        AdapterItem item = items.get(position);
        item.item.setStruckOut(!item.item.isStruckOut());
        ItemService.getInstance().save(item.item).subscribe(new SimpleRxSubscriber<ItemModel>());
        update();
    }

    @Override
    public boolean onLongClick(int position) {
        return listener.onItemLongClick(items.get(position).item);
    }

    public interface ListAdapterListener {
        boolean onItemLongClick(ItemModel item);
    }

    private static class AdapterItem {
        ItemModel item;
        String category;

        public AdapterItem(ItemModel item, String category) {
            this.item = item;
            this.category = category;
        }
    }

}
