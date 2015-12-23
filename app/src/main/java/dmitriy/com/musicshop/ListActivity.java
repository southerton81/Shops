package dmitriy.com.musicshop;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.google.android.gms.maps.SupportMapFragment;
import dmitriy.com.musicshop.utils.Utils;

/**
 * Hosts ListFragment, and DetailsFragment on large smartphone screens
 */
public class ListActivity extends AppCompatActivity implements ListFragment.Listener {

    private boolean mTwoPane;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_musicshops);

        if (findViewById(R.id.detailsFragmentContainer) != null) {
            mTwoPane = true;
            ((ListFragment) getSupportFragmentManager().findFragmentById(R.id.fragmentList))
                    .highlightOnClick(true);

            if (savedInstanceState != null) {
                findViewById(R.id.detailsLayout).setVisibility(
                        savedInstanceState.getInt("R.id.detailsLayout", Integer.MIN_VALUE) == View.VISIBLE ?
                                View.VISIBLE : View.INVISIBLE);

                DetailsFragment detailsFragment = (DetailsFragment) getSupportFragmentManager()
                        .findFragmentByTag(DetailsFragment.class.getSimpleName());
                if (detailsFragment != null) {
                    SupportMapFragment mapFragment = ((SupportMapFragment)
                            getSupportFragmentManager().findFragmentById(R.id.map));
                    detailsFragment.setMapFragement(mapFragment);
                }
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_music_shops, menu);
        return true;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mTwoPane)
            outState.putInt("R.id.detailsLayout", findViewById(R.id.detailsLayout).getVisibility());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_refresh) {
            Utils.removeAllRows(this);
            ((ListFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.fragmentList))
                    .refresh();

            if (mTwoPane) {
                DetailsFragment detailsFragment = (DetailsFragment) getSupportFragmentManager()
                        .findFragmentByTag(DetailsFragment.class.getSimpleName());
                if (detailsFragment != null)
                    detailsFragment.refresh();
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onShopSelected(long shopId, String phone, String website, long lat, long lon) {
        Bundle bundle = new Bundle();
        bundle.putLong(DetailsFragment.ARG_SHOP_ID, shopId);
        bundle.putString(DetailsFragment.ARG_SHOP_PHONE, phone);
        bundle.putString(DetailsFragment.ARG_SHOP_WEB, website);
        bundle.putLong(DetailsFragment.ARG_LAT, lat);
        bundle.putLong(DetailsFragment.ARG_LON, lon);

        if (mTwoPane) {
            findViewById(R.id.detailsLayout).setVisibility(View.VISIBLE);
            SupportMapFragment mapFragment = ((SupportMapFragment)
                    getSupportFragmentManager().findFragmentById(R.id.map));

            DetailsFragment fragment = new DetailsFragment();
            fragment.setMapFragement(mapFragment);
            fragment.setArguments(bundle);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.detailsFragmentContainer, fragment, DetailsFragment.class.getSimpleName())
                    .commit();
        } else {
            Intent detailsIntent = new Intent(this, DetailsActivity.class);
            detailsIntent.putExtra(DetailsFragment.BUNDLE_ID, bundle);
            startActivity(detailsIntent);
        }
    }
}
