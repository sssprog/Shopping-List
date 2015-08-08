package com.sssprog.shoppingliststandalone.ui.dictionary;

import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.sssprog.shoppingliststandalone.R;
import com.sssprog.shoppingliststandalone.utils.ViewUtils;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class DictionaryViewHolder extends RecyclerView.ViewHolder {

    @InjectView(R.id.container)
    View container;
    @InjectView(R.id.title)
    TextView title;
    @InjectView(R.id.dividerTop)
    View dividerTop;
    @InjectView(R.id.dividerBottom)
    View dividerBottom;

    private final float dividersMaxDistance;
    private ItemListener listener;

    public DictionaryViewHolder(View view, ItemListener itemListener) {
        super(view);
        ButterKnife.inject(this, view);
        dividersMaxDistance = view.getResources().getDimension(R.dimen.history_swipe_to_delete_show_dividers_distance);
        ViewUtils.setBackground(container, ViewUtils.makeTouchFeedbackDrawable(view.getContext(), Color.WHITE));
        this.listener = itemListener;
        container.setOnLongClickListener(v -> listener.onItemLongClick(getAdapterPosition()));
    }

    public void move(float dx) {
        container.setTranslationX(dx);
        float alpha = Math.abs(dx) / dividersMaxDistance;
        alpha = Math.min(1, alpha);
        dividerBottom.setAlpha(alpha);
        dividerTop.setAlpha(alpha);
    }

    public void resetTranslation() {
        move(0);
    }

    public interface ItemListener {
        boolean onItemLongClick(int position);
    }
}
