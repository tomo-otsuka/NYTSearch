package com.codepath.nytsearch.activities;

import com.codepath.nytsearch.R;
import com.codepath.nytsearch.adapters.ArticlesAdapter;
import com.codepath.nytsearch.fragments.SettingsDialogFragment;
import com.codepath.nytsearch.models.Article;
import com.codepath.nytsearch.utils.EndlessRecyclerViewScrollListener;
import com.codepath.nytsearch.utils.Network;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import cz.msebera.android.httpclient.Header;

public class SearchActivity extends AppCompatActivity {

    @BindView(R.id.rvArticles) RecyclerView rvArticles;

    ArrayList<Article> articles;
    ArticlesAdapter articleAdapter;
    String mQuery;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ButterKnife.bind(this);

        StaggeredGridLayoutManager gridLayoutManager =
                new StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL);
        rvArticles.setLayoutManager(gridLayoutManager);

        setEventListeners();
    }

    public void showSettingsDialog(MenuItem mi) {
        FragmentManager fm = getSupportFragmentManager();
        SettingsDialogFragment settingDialogFragment = SettingsDialogFragment.newInstance("Filter Settings");
        settingDialogFragment.show(fm, "fragment_edit_name");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_search, menu);

        MenuItem searchItem = menu.findItem(R.id.action_search);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // perform query here
                mQuery = query;
                articleSearch();
                // workaround to avoid issues with some emulators and keyboard devices firing twice if a keyboard enter is used
                // see https://code.google.com/p/android/issues/detail?id=24599
                searchView.clearFocus();

                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                mQuery = newText;
                articleSearch();
                return true;
            }
        });

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        switch (id) {
            case R.id.action_settings:
                showSettingsDialog(item);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void articleSearch() {
        boolean doReset = false;
        if (articles != null) {
            doReset = true;
        }
        articles = new ArrayList<>();
        articleAdapter = new ArticlesAdapter(this, articles);
        rvArticles.setAdapter(articleAdapter);

        if (doReset) {
            articleAdapter.notifyDataSetChanged();
        }

        fetchArticlesAsync(0);
    }

    private void fetchArticlesAsync(final int page) {
        final String url = "http://api.nytimes.com/svc/search/v2/articlesearch.json";
        RequestParams params = new RequestParams();
        params.put("api-key", "80230c8b90574180a1de9425e2d5dbcd");
        params.put("page", page);

        String beginDate = getBeginDateFromSettings();
        if (beginDate != null) {
            params.put("begin_date", beginDate);
        }

        String sortOrder = getSortOrderFromSettings();
        params.put("sort", sortOrder);

        String query = mQuery;
        String querySettings = queryStringFromSettings();
        if (querySettings != null) {
            query += " AND " + querySettings;
        }
        params.put("fq", query);

        if (Network.isNetworkAvailable(this) && Network.isOnline()) {
            AsyncHttpClient client = new AsyncHttpClient();
            client.get(url, params, new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    try {
                        JSONArray articleJsonResults = response.getJSONObject("response").getJSONArray("docs");
                        articles.addAll(Article.fromJSONArray(articleJsonResults));
                        articleAdapter.notifyItemRangeInserted(page * 10, 10);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                    Toast.makeText(SearchActivity.this, "Failed to retrieve results", Toast.LENGTH_LONG).show();
                }
            });
        } else {
            Toast.makeText(this, "Network Error", Toast.LENGTH_LONG).show();
        }
    }

    private void setEventListeners() {
        rvArticles.addOnScrollListener(new EndlessRecyclerViewScrollListener((StaggeredGridLayoutManager) rvArticles.getLayoutManager()) {
            @Override
            public void onLoadMore(int page, int totalItemsCount) {
                fetchArticlesAsync(page);
            }
        });
    }

    private String queryStringFromSettings() {
        SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
        Boolean newsDeskArts = sharedPref.getBoolean("newsDeskArts", false);
        Boolean newsDeskFashion = sharedPref.getBoolean("newsDeskFashion", false);
        Boolean newsDeskSports = sharedPref.getBoolean("newsDeskSports", false);

        if (!newsDeskArts && !newsDeskFashion && !newsDeskSports) {
            return null;
        }

        String query = "news_desk:(";
        if (newsDeskArts) {
            query += "\"Arts\" ";
        }
        if (newsDeskFashion) {
            query += "\"Fashion\" ";
        }
        if (newsDeskSports) {
            query += "\"Sports\" ";
        }

        query = query.substring(0, query.length() - 1);
        query += ")";

        return query;
    }

    private String getBeginDateFromSettings() {
        SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
        String beginDate = sharedPref.getString("beginDate", "");
        return beginDate.replaceAll("-", "");
    }

    private String getSortOrderFromSettings() {
        SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
        int sortOrder = sharedPref.getInt("sortOrder", 0);
        if (sortOrder == 0) {
            return "newest";
        } else {
            return "oldest";
        }
    }
}
