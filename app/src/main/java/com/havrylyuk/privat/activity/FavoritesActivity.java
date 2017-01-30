package com.havrylyuk.privat.activity;

import android.app.ActivityOptions;
import android.app.SearchManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.ProgressBar;


import android.net.Uri;

import com.havrylyuk.privat.R;
import com.havrylyuk.privat.adapter.FavoriteAdapter;
import com.havrylyuk.privat.data.source.local.AcquiringContract;
import com.havrylyuk.privat.data.source.local.AcquiringContract.AcquiringEntry;
import com.havrylyuk.privat.util.Utility;

import static com.havrylyuk.privat.activity.DetailActivity.DETAIL_COLUMNS;


/**
 *
 * Created by Igor Havrylyuk on 29.01.2017.
 */
public class FavoritesActivity extends BaseActivity implements LoaderManager.LoaderCallbacks<Cursor>{

    private static final int POINTS_LOADER = 2202;

    private boolean isTwoPane = false;
    private FavoriteAdapter favoritesAdapter;
    private SwipeRefreshLayout swipeRefreshLayout;
    private MenuItem searchItem;
    private SearchView searchView;
    private String searchQuery;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        progressBar = (ProgressBar) findViewById(R.id.data_progress_bar);
        initToolBar();
        getSupportLoaderManager().initLoader(POINTS_LOADER, null, this);
        initRecycler();
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_to_refresh);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getSupportLoaderManager().restartLoader(POINTS_LOADER, null, FavoritesActivity.this);
                swipeRefreshLayout.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        swipeRefreshLayout.setRefreshing(false);

                    }
                }, 2000);
            }
        });

        swipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

    }
    private void initToolBar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            ActionBar actionBar = getSupportActionBar();
            if (actionBar != null) {
                actionBar.setDisplayHomeAsUpEnabled(true);
                actionBar.setDisplayShowHomeEnabled(true);
            }
        }
    }
    @Override
    protected int getLayout() {
        return R.layout.activity_favorites;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void initRecycler() {
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        int spanCount;
        int orientation = getResources().getConfiguration().orientation;
        if (!isTwoPane && Configuration.ORIENTATION_LANDSCAPE == orientation) {
            spanCount = 2;
        } else {
            spanCount = 1;
        }
        GridLayoutManager layoutManager = new GridLayoutManager(this, spanCount);
        RecyclerView.ItemAnimator itemAnimator = new DefaultItemAnimator();
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(itemAnimator);
        favoritesAdapter = new FavoriteAdapter(this);
        favoritesAdapter.setListener(listener);
        recyclerView.setAdapter(favoritesAdapter);
        ItemTouchHelper.SimpleCallback simpleItemTouchCallback =
                new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {

                    @Override
                    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder,
                                          RecyclerView.ViewHolder target) {
                        return false;
                    }

                    @Override
                    public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {
                        int location = viewHolder.getAdapterPosition();
                        ContentValues cv = new ContentValues();
                        /*cv.put(AcquiringContract.AcquiringEntry.ACQ_FAV, 1);
                        getContentResolver().update(AcquiringContract.AcquiringEntry.CONTENT_URI, cv, AcquiringContract.AcquiringEntry._ID + " = ?", new String[]{String.valueOf(pointId)});
                         if (favoritesAdapter.getCurrentPosition() > location) {
                            favoritesAdapter.setCurrentPosition(favoritesAdapter.getCurrentPosition() - 1);
                        }*/
                    }
                };

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);
    }

    private FavoriteAdapter.OnItemSelectedListener listener = new FavoriteAdapter.OnItemSelectedListener() {
        @Override
        public void onItemSelected(Uri uri, FavoriteAdapter.FruitViewHolder vh) {
            Bundle args = new Bundle();

                Intent intent = new Intent(FavoritesActivity.this, DetailActivity.class);
                args.putParcelable(DetailActivity.DETAIL_POINT_URI, uri);

                if (getResources().getConfiguration().orientation == OrientationHelper.VERTICAL &&
                        Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    args.putString(DetailActivity.TRANSITION_NAME, getString(R.string.detail_icon_transition_name));
                    intent.putExtras(args);
                    ActivityOptions transitionActivityOptions =
                            ActivityOptions.makeSceneTransitionAnimation(FavoritesActivity.this,
                                    new android.util.Pair<View, String>(vh.image,getString(R.string.detail_icon_transition_name)));
                    ActivityCompat.startActivity(FavoritesActivity.this, intent, transitionActivityOptions.toBundle());
                } else {
                    intent.putExtras(args);
                    startActivity(intent);
                }

        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        searchItem = menu.findItem(R.id.action_search);
        setupSearchView(searchItem);
        return true;
    }


    private void setupSearchView(MenuItem searchItem) {
        MenuItemCompat.setOnActionExpandListener(searchItem, new MenuItemCompat.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                searchQuery = null;
                updateQuery();
                return true;
            }

            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                return true;
            }
        });

        SearchManager searchManager = (SearchManager) (getSystemService(Context.SEARCH_SERVICE));
        if (searchItem != null) {
            searchView = (SearchView) searchItem.getActionView();
        }
        if (searchView != null) {
            searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
            searchView.setImeOptions(EditorInfo.IME_ACTION_SEARCH);
            searchView.clearFocus();
            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    searchQuery = query;
                    searchView.clearFocus();
                    updateQuery();
                    return true;
                }
                @Override
                public boolean onQueryTextChange(String query) {
                    searchQuery = query;
                    updateQuery();
                    return true;
                }
            });
        }
    }

    private void updateQuery() {
         getSupportLoaderManager().restartLoader(POINTS_LOADER, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if (id == POINTS_LOADER) {
           String selection = searchQuery != null ?
                            AcquiringEntry.TABLE_NAME + "."
                            + AcquiringEntry.ACQ_FULL_ADR+ " LIKE ? "
                            + "AND "+AcquiringEntry.ACQ_FAV + " =? ": AcquiringEntry.ACQ_FAV + " =? ";
           String[] selAgrs = searchQuery != null ? new String[]{Utility.toCapsWord(searchQuery) + "%","1"} : new String[]{"1"};
            return new CursorLoader(this,
                    AcquiringEntry.CONTENT_URI,
                    DETAIL_COLUMNS,
                    selection,
                    selAgrs,
                    null);
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (loader.getId() == POINTS_LOADER) {
            if (favoritesAdapter !=null) favoritesAdapter.swapCursor(data);
        }

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        if (loader.getId() == POINTS_LOADER) {
            if (favoritesAdapter !=null) favoritesAdapter.swapCursor(null);
        }
    }


}
