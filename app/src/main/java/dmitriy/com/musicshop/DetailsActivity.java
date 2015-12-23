package dmitriy.com.musicshop;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.gms.maps.SupportMapFragment;

import dmitriy.com.musicshop.utils.Utils;

/**
 * Hosts DetailsFragment on medium smartphone screens
 */
public class DetailsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        SupportMapFragment mapFragment = ((SupportMapFragment)
                getSupportFragmentManager().findFragmentById(R.id.map));

        DetailsFragment detailsFragment = null;
        if (savedInstanceState == null) {
            detailsFragment = new DetailsFragment();
            detailsFragment.setArguments(getIntent().getBundleExtra(DetailsFragment.BUNDLE_ID));
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.detailsFragmentContainer, detailsFragment, DetailsFragment.class.getSimpleName())
                            //.add(android.R.id.content, detailsFragment)
                    .commit();
        }

        if (detailsFragment == null)
            detailsFragment = ((DetailsFragment) getSupportFragmentManager()
                    .findFragmentByTag(DetailsFragment.class.getSimpleName()));

        detailsFragment.setMapFragement(mapFragment);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_music_shops, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_refresh) {
            Utils.removeAllRows(this);
            ((DetailsFragment) getSupportFragmentManager()
                    .findFragmentByTag(DetailsFragment.class.getSimpleName()))
                    .refresh();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
