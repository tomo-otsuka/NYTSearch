package com.codepath.nytsearch.adapters;

import com.codepath.nytsearch.R;
import com.codepath.nytsearch.activities.ArticleActivity;
import com.codepath.nytsearch.models.Article;
import com.squareup.picasso.Picasso;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ArticlesAdapter extends RecyclerView.Adapter<ArticlesAdapter.ViewHolder> {

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        @BindView(R.id.ivImage) ImageView ivImage;
        @BindView(R.id.tvHeadline) TextView tvHeadline;

        public ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int position = getLayoutPosition();
            Article article = mArticles.get(position);

            Intent intent = new Intent(getContext(), ArticleActivity.class);
            intent.putExtra("article", article);

            getContext().startActivity(intent);
        }
    }

    private List<Article> mArticles;
    private Context mContext;

    public List<Article> getArticles() {
        return mArticles;
    }

    public Context getContext() {
        return mContext;
    }

    public ArticlesAdapter(Context context, List<Article> articles) {
        mArticles = articles;
        mContext = context;
    }

    // Usually involves inflating a layout from XML and returning the holder
    @Override
    public ArticlesAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the custom layout
        View articleView = inflater.inflate(R.layout.item_article, parent, false);

        // Return a new holder instance
        ViewHolder viewHolder = new ViewHolder(articleView);
        return viewHolder;
    }

    // Involves populating data into the item through holder
    @Override
    public void onBindViewHolder(ArticlesAdapter.ViewHolder viewHolder, int position) {
        // Get the data model based on position
        Article article = mArticles.get(position);

        // Set item views based on your views and data model
        TextView tvHeadline = viewHolder.tvHeadline;
        tvHeadline.setText(article.getHeadline());
        ImageView ivImage = viewHolder.ivImage;
        String thumbnail = article.getThumbnail();
        if (!TextUtils.isEmpty(thumbnail)) {
            Picasso.with(getContext()).load(thumbnail).fit().centerCrop()
                    .into(ivImage);
        }
    }

    // Returns the total count of items in the list
    @Override
    public int getItemCount() {
        return mArticles.size();
    }

}
