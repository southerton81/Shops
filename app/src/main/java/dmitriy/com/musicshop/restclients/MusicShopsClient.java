package dmitriy.com.musicshop.restclients;

import java.util.List;

import dmitriy.com.musicshop.models.InstrumentModel;
import dmitriy.com.musicshop.models.MusicShopModel;
import retrofit.RestAdapter;
import retrofit.http.GET;
import retrofit.http.Path;
import rx.Observable;

public interface MusicShopsClient {

    class Factory {

        public static MusicShopsClient create() {
            return new RestAdapter.Builder()
                    .setEndpoint("http://aschoolapi.appspot.com/")
                    .build()
                    .create(MusicShopsClient.class);
        }
    }

    @GET("/stores")
    Observable<List<MusicShopModel>> getMusicShops();

    @GET("/stores/{storeId}/instruments")
    Observable<List<InstrumentModel>> getInstruments(@Path("storeId") long id);
}
