package com.codepath.nytsearch.adapters;

import com.codepath.nytsearch.R;
import com.codepath.nytsearch.activities.ArticleActivity;
import com.codepath.nytsearch.models.Article;
import com.squareup.picasso.Picasso;

import org.parceler.Parcels;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ArticlesAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final int NORMAL = 0, HEADLINE_ONLY = 1;

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
            intent.putExtra("article", Parcels.wrap(article));

            getContext().startActivity(intent);
        }
    }

    public class HeadlineOnlyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        @BindView(R.id.tvHeadline) TextView tvHeadline;

        public HeadlineOnlyViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int position = getLayoutPosition();
            Article article = mArticles.get(position);

            Intent intent = new Intent(getContext(), ArticleActivity.class);
            intent.putExtra("article", Parcels.wrap(article));

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

    @Override
    public int getItemViewType(int position) {
        if (mArticles.get(position).getThumbnail() == "") {
            return HEADLINE_ONLY;
        }
        return NORMAL;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        RecyclerView.ViewHolder viewHolder = null;
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());

        switch (viewType) {
            case NORMAL:
                View normalView = inflater.inflate(R.layout.item_article, viewGroup, false);
                viewHolder = new ViewHolder(normalView);
                break;
            case HEADLINE_ONLY:
                View headlineOnlyView = inflater.inflate(R.layout.item_article_headline_only, viewGroup, false);
                viewHolder = new HeadlineOnlyViewHolder(headlineOnlyView);
                break;
        }

        return viewHolder;
    }

    // Involves populating data into the item through holder
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        // Get the data model based on position
        Article article = mArticles.get(position);

        switch (viewHolder.getItemViewType()) {
            case NORMAL:
                ViewHolder normalViewHolder = (ViewHolder) viewHolder;
                configureNormalViewHolder(normalViewHolder, article);
                break;
            case HEADLINE_ONLY:
                HeadlineOnlyViewHolder headlineOnlyViewHolder = (HeadlineOnlyViewHolder) viewHolder;
                configureHeadlineOnlyViewHolder(headlineOnlyViewHolder, article);
                break;
        }
    }

    private void configureNormalViewHolder(ViewHolder normalViewHolder, Article article) {
        TextView tvHeadline = normalViewHolder.tvHeadline;
        tvHeadline.setText(article.getHeadline());
        ImageView ivImage = normalViewHolder.ivImage;
        ivImage.setImageResource(0);
        String thumbnail = article.getThumbnail();
        Picasso.with(getContext()).load(thumbnail)
                .into(ivImage);
    }

    private void configureHeadlineOnlyViewHolder(HeadlineOnlyViewHolder headlineOnlyViewHolder, Article article) {
        TextView tvHeadline = headlineOnlyViewHolder.tvHeadline;
        tvHeadline.setText(article.getHeadline());
    }

    // Returns the total count of items in the list
    @Override
    public int getItemCount() {
        return mArticles.size();
    }
}
