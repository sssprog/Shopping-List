package com.sssprog.shoppingliststandalone.ui.home;

import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.sssprog.shoppingliststandalone.R;
import com.sssprog.shoppingliststandalone.utils.ViewUtils;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class ListViewHolder extends RecyclerView.ViewHolder {

    View root;
    @InjectView(R.id.container)
    View container;
    @InjectView(R.id.title)
    TextView title;
    @InjectView(R.id.info)
    TextView info;

    private ListItemListener listener;

    public ListViewHolder(View view, ListItemListener itemListener) {
        super(view);
        ButterKnife.inject(this, view);
        root = view;
        ViewUtils.setBackground(container, ViewUtils.makeTouchFeedbackDrawable(view.getContext(), Color.WHITE));
        this.listener = itemListener;
        container.setOnClickListener(v -> listener.onClick(getAdapterPosition()));
        container.setOnLongClickListener(v -> listener.onLongClick(getAdapterPosition()));
    }

    public void move(float dx) {
        container.setTranslationX(dx);
    }

    public void resetTranslation() {
        move(0);
    }

    public interface ListItemListener {
        void onClick(int position);

        boolean onLongClick(int position);
    }
}
