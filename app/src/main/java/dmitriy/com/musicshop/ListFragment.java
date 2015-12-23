package dmitriy.com.musicshop;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SimpleCursorAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.util.List;

import dmitriy.com.musicshop.db.DbHelper;
import dmitriy.com.musicshop.db.MusicShopsTable;
import dmitriy.com.musicshop.db.ShopsContentProvider;
import dmitriy.com.musicshop.loaders.LoadersRoster;
import dmitriy.com.musicshop.models.MusicShopModel;
import dmitriy.com.musicshop.restclients.MusicShopsObservable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;

/**
 * Shops list fragment
 */
public class ListFragment extends android.support.v4.app.ListFragment
        implements LoaderManager.LoaderCallbacks<Cursor> {

    public interface Listener {
        void onShopSelected(long id, String phone, String website, long lat, long lon);
    }

    private static Listener sDefaultListener = new Listener() {
        @Override
        public void onShopSelected(long shopId, String phone, String website, long lat, long lon) {
        }
    };

    private Subscription mSubscription;
    private Listener mListener = sDefaultListener;
    private SimpleCursorAdapter mCursorAdapter;

    public ListFragment() {
    }

    public void highlightOnClick(boolean highlightOnClick) {
        getListView().setChoiceMode(highlightOnClick ? ListView.CHOICE_MODE_SINGLE
                : ListView.CHOICE_MODE_NONE);
    }

    public void refresh() {
        setListAdapter(null);
        obtainDataFromService();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setEmptyText(getResources().getString(R.string.no_music_shops_data));
        createCursorAdapter();
        getLoaderManager().initLoader(LoadersRoster.SHOPSLIST_LOADER_ID, null, this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof Listener)
            mListener = (Listener)context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = sDefaultListener;
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mSubscription != null)
            mSubscription.unsubscribe();
        mSubscription = null;
    }

    @Override
    public void onListItemClick(ListView listView, View view, int position, long id) {
        super.onListItemClick(listView, view, position, id);
        Cursor c = (Cursor) getListAdapter().getItem(position);
        long shopId = c.getLong(c.getColumnIndex(MusicShopsTable.COLUMN_ID));
        String phone = c.getString(c.getColumnIndex(MusicShopsTable.COLUMN_PHONE));
        String website = c.getString(c.getColumnIndex(MusicShopsTable.COLUMN_WEBSITE));
        long lat = c.getLong(c.getColumnIndex(MusicShopsTable.COLUMN_LATITUDE));
        long lon = c.getLong(c.getColumnIndex(MusicShopsTable.COLUMN_LONGITUDE));

        mListener.onShopSelected(shopId, phone, website, lat, lon);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(getActivity(), ShopsContentProvider.LISTSHOPS_URI,
        /*projection*/ null, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if(mCursorAdapter != null)
            mCursorAdapter.swapCursor(data);

        if (mCursorAdapter == null || mCursorAdapter.getCount() == 0)
            obtainDataFromService();
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        if (mCursorAdapter != null)
            mCursorAdapter.swapCursor(null);
    }

    private void obtainDataFromService() {
        mSubscription = MusicShopsObservable.getInstance().getMusicShopsObservable()
                .map(new Func1<List<MusicShopModel>, Void>() {
                    @Override
                    public Void call(List<MusicShopModel> models) {
                        cacheShops(models, getActivity());
                        return null;
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        new Action1<Void>() {
                            @Override
                            public void call(Void v) {
                                setListAdapter(mCursorAdapter);
                                getLoaderManager().restartLoader(LoadersRoster.SHOPSLIST_LOADER_ID, null,
                                        ListFragment.this);
                            }
                        },
                        new Action1<Throwable>() {
                            @Override
                            public void call(Throwable throwable) {
                                Log.e(ListFragment.this.getClass().getSimpleName(), "Error! " + throwable);
                            }
                        });
    }

    void cacheShops(List<MusicShopModel> models, Activity context) {
        DbHelper dbHelper = new DbHelper(context);
        SQLiteDatabase database = dbHelper.getWritableDatabase();
        database.beginTransaction();

        database.delete(MusicShopsTable.TABLE_NAME, null, null);

        try {
            for (MusicShopModel m : models) {
                ContentValues values = new ContentValues();
                values.put(MusicShopsTable.COLUMN_ID, m.getId());
                values.put(MusicShopsTable.COLUMN_NAME, m.getName());
                values.put(MusicShopsTable.COLUMN_ADDRESS, m.getAddress());
                values.put(MusicShopsTable.COLUMN_PHONE, m.getPhone());
                values.put(MusicShopsTable.COLUMN_WEBSITE, m.getWebsite());
                values.put(MusicShopsTable.COLUMN_LATITUDE, m.getLocation().getLatitude());
                values.put(MusicShopsTable.COLUMN_LONGITUDE, m.getLocation().getLongitude());
                long rowId = database.insert(MusicShopsTable.TABLE_NAME, null, values);
            }

            database.setTransactionSuccessful();
        } finally {
            database.endTransaction();
            dbHelper.close();
        }
    }

    private void createCursorAdapter() {
        String[] uiBindFrom = {MusicShopsTable.COLUMN_NAME, MusicShopsTable.COLUMN_ADDRESS};
        int[] uiBindTo = { android.R.id.text1, android.R.id.text2 };
        mCursorAdapter = new SimpleCursorAdapter(this.getActivity(),
                android.R.layout.simple_list_item_activated_2, null,
                uiBindFrom, uiBindTo, 0);
        setListAdapter(mCursorAdapter);
    }
}
