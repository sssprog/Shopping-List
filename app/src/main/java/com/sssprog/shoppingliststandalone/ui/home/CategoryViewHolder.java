package com.sssprog.shoppingliststandalone.ui.home;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.sssprog.shoppingliststandalone.R;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class CategoryViewHolder extends RecyclerView.ViewHolder {

    @InjectView(R.id.title)
    TextView title;

    public CategoryViewHolder(View view) {
        super(view);
        ButterKnife.inject(this, view);
    }

}
