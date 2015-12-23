package dmitriy.com.musicshop.restclients;


import java.util.List;

import dmitriy.com.musicshop.models.InstrumentModel;
import dmitriy.com.musicshop.models.MusicShopModel;

public enum MusicShopsObservable {
    INSTANCE;
    rx.Observable<List<MusicShopModel>> musicShopsObservable;
    rx.Observable<List<InstrumentModel>> instrumentsObservable;

    public static MusicShopsObservable getInstance() {
        return INSTANCE;
    }

    public rx.Observable<List<MusicShopModel>> getMusicShopsObservable() {
        if (musicShopsObservable == null) {
            musicShopsObservable = MusicShopsClient.Factory.create()
                    .getMusicShops()
                    .cache();
        }

        return musicShopsObservable;
    }

    public rx.Observable<List<InstrumentModel>> getInstrumentsObservable(long shopId) {
        if (instrumentsObservable == null) {
            instrumentsObservable = MusicShopsClient.Factory.create()
                    .getInstruments(shopId)
                    .cache();
        }

        return instrumentsObservable;
    }


}
