package dmitriy.com.musicshop;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SimpleCursorAdapter;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;

import dmitriy.com.musicshop.db.DbHelper;
import dmitriy.com.musicshop.db.InstrumentsTable;
import dmitriy.com.musicshop.db.ShopsContentProvider;
import dmitriy.com.musicshop.loaders.LoadersRoster;
import dmitriy.com.musicshop.models.InstrumentModel;
import dmitriy.com.musicshop.models.MusicShopModel;
import dmitriy.com.musicshop.restclients.MusicShopsObservable;
import dmitriy.com.musicshop.utils.LocationProvider;
import dmitriy.com.musicshop.utils.Utils;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;

/**
 * Shop instruments fragment
 */
public class DetailsFragment extends android.support.v4.app.ListFragment
        implements LoaderManager.LoaderCallbacks<Cursor> {

    public static final String BUNDLE_ID = "DETAILS_FRAGMENT_BUNDLE";
    public static final String ARG_SHOP_ID = "ARG_SHOP_ID";
    public static final String ARG_SHOP_PHONE = "ARG_SHOP_PHONE";
    public static final String ARG_SHOP_WEB = "ARG_SHOP_WEB";
    public static final String ARG_LAT = "ARG_LAT";
    public static final String ARG_LON = "ARG_LON";

    private static final String LISTVIEW_BUNDLEKEY = "LISTVIEW_BUNDLEKEY";

    private Subscription mSubscription;
    private SimpleCursorAdapter mCursorAdapter;
    Parcelable mListViewState;
    long shopId;
    String phone;
    String website;
    SupportMapFragment mapFragment;
    LocationProvider locationProvider = new LocationProvider();

    public void setMapFragement(SupportMapFragment mapFragment) {
        this.mapFragment = mapFragment;
    }

    public void refresh() {
        setListAdapter(null);
        obtainDataFromService();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle bundle = getArguments();
        shopId = bundle.getLong(ARG_SHOP_ID);
        phone = bundle.getString(ARG_SHOP_PHONE);
        website = bundle.getString(ARG_SHOP_WEB);
    }

    private void onShowMarkerOnMap(double latitude, double longitude) {
        if (mapFragment != null) {
            final LatLng shopLocation = new LatLng(latitude, longitude);
            mapFragment.getMapAsync(new OnMapReadyCallback() {
                @Override
                public void onMapReady(GoogleMap googleMap) {
                    googleMap.addMarker(new MarkerOptions().position(shopLocation));
                    googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(shopLocation, 15));
                }
            });
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_details, container, false);
        buttonHandlers(v);
        return v;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        locationProvider.start(getActivity());
        locateShopOnMap();
        createCursorAdapter();
        getLoaderManager().restartLoader(LoadersRoster.SHOPINSTRUMENTS_LOADER_ID, null, this);
        if (savedInstanceState != null)
            mListViewState = savedInstanceState.getParcelable(LISTVIEW_BUNDLEKEY);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Parcelable listviewState = getListView().onSaveInstanceState();
        outState.putParcelable(LISTVIEW_BUNDLEKEY, listviewState);
    }

    private void buttonHandlers(View v) {
        Button callButton = (Button) v.findViewById(R.id.callButton);
        callButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!TextUtils.isEmpty(phone))
                    Utils.dialPhoneNumber(getActivity(), phone);
            }
        });

        Button urlButton = (Button) v.findViewById(R.id.urlButton);
        urlButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!TextUtils.isEmpty(website))
                    Utils.visitWebite(getActivity(), website);
            }
        });

        Button emailButton = (Button) v.findViewById(R.id.emailButton);
        emailButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.sendMail(getActivity(), "dummy@email.address.com");
            }
        });

        final Button positionButton = (Button) v.findViewById(R.id.positionButton);
        positionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Object tag = positionButton.getTag(R.string.key_show_gps_location);
                if (tag == null || tag.equals(Boolean.FALSE)) {
                    Location location = locationProvider.getLocation();
                    if (location.hasAccuracy()) {
                        onShowMarkerOnMap(location.getLatitude(), location.getLongitude());
                        positionButton.setTag(R.string.key_show_gps_location, Boolean.TRUE);
                        positionButton.setText(getActivity().getResources().getString(R.string.shop_loc));
                    } else {
                        Toast.makeText(getActivity(),
                                getActivity().getResources().getString(R.string.no_loc),
                                Toast.LENGTH_SHORT).show();
                    }
                } else {
                    locateShopOnMap();
                    positionButton.setText(getActivity().getResources().getString(R.string.me));
                    positionButton.setTag(R.string.key_show_gps_location, Boolean.FALSE);
                }
            }
        });
    }

    private void locateShopOnMap() {
        Bundle bundle = getArguments();
        long latitude = bundle.getLong(ARG_LAT, Long.MIN_VALUE);
        long longitude = bundle.getLong(ARG_LON, Long.MIN_VALUE);
        if (latitude != Long.MIN_VALUE && longitude != Long.MIN_VALUE)
            onShowMarkerOnMap(Utils.decodeLocation(latitude),
                    Utils.decodeLocation(longitude));
        else
            Toast.makeText(getActivity(),
                    getActivity().getResources().getString(R.string.no_loc),
                    Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mSubscription != null)
            mSubscription.unsubscribe();
        mSubscription = null;
        locationProvider.stop();
    }

    private void obtainDataFromService() {
        mSubscription = MusicShopsObservable.getInstance().getInstrumentsObservable(shopId)
                .doOnNext(new Action1<List<InstrumentModel>>() {
                    @Override
                    public void call(List<InstrumentModel> models) {
                        cacheInstruments(models, shopId, getActivity());
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        new Action1<List<InstrumentModel>>() {
                            @Override
                            public void call(List<InstrumentModel> m) {
                                setListAdapter(mCursorAdapter);
                                getLoaderManager().restartLoader(
                                        LoadersRoster.SHOPINSTRUMENTS_LOADER_ID,
                                        null, DetailsFragment.this);
                            }
                        },
                        new Action1<Throwable>() {
                            @Override
                            public void call(Throwable throwable) {
                                Log.e(DetailsFragment.this.getClass().getSimpleName(),
                                        "Error! " + throwable);
                            }
                        }
                );
    }

    private void createCursorAdapter() {
        String[] uiBindFrom = {InstrumentsTable.COLUMN_TYPEOFINSTRUMENT, InstrumentsTable.COLUMN_BRAND,
                InstrumentsTable.COLUMN_MODEL, InstrumentsTable.COLUMN_PRICE,
                InstrumentsTable.COLUMN_QUANTITY};

        int[] uiBindTo = {R.id.type, R.id.brand, R.id.model, R.id.price, R.id.quantity};
        mCursorAdapter = new SimpleCursorAdapter(this.getActivity(), R.layout.adapter_listinstruments, null,
                uiBindFrom, uiBindTo, 0);
        setListAdapter(mCursorAdapter);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String selection = InstrumentsTable.COLUMN_SHOPID + "=?";
        String[] selectionArgs = new String[]{String.valueOf(shopId)};

        return new CursorLoader(getActivity(), ShopsContentProvider.LISTINSTRUMENTS_URI,
        /*projection*/ null, selection, selectionArgs, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (mCursorAdapter != null) {
            mCursorAdapter.swapCursor(data);

            if (mListViewState != null) {
                getListView().onRestoreInstanceState(mListViewState);
                mListViewState = null;
            }
        }

        if (mCursorAdapter == null || mCursorAdapter.getCount() == 0)
            obtainDataFromService();
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        if (mCursorAdapter != null)
            mCursorAdapter.swapCursor(null);
    }

    private void cacheInstruments(List<InstrumentModel> models, long shopId, Context context) {
        DbHelper dbHelper = new DbHelper(context);
        SQLiteDatabase database = dbHelper.getWritableDatabase();
        database.beginTransaction();

        database.delete(InstrumentsTable.TABLE_NAME,
                InstrumentsTable.COLUMN_SHOPID + "=?", new String[]{String.valueOf(shopId)});

        try {
            for (InstrumentModel m : models) {
                ContentValues values = new ContentValues();

                InstrumentModel.Instrument instrument = m.getInstrument();

                values.put(InstrumentsTable.COLUMN_BRAND, instrument.getBrand());
                values.put(InstrumentsTable.COLUMN_MODEL, instrument.getModel());
                values.put(InstrumentsTable.COLUMN_INSTRUMENTID, instrument.getId());
                values.put(InstrumentsTable.COLUMN_PRICE, instrument.getPrice());
                values.put(InstrumentsTable.COLUMN_TYPEOFINSTRUMENT, instrument.getType());
                values.put(InstrumentsTable.COLUMN_QUANTITY, m.getQuantity());
                values.put(InstrumentsTable.COLUMN_SHOPID, shopId);

                long rowId = database.insert(InstrumentsTable.TABLE_NAME, null, values);
            }

            database.setTransactionSuccessful();
        } finally {
            database.endTransaction();
            dbHelper.close();
        }
    }
}
