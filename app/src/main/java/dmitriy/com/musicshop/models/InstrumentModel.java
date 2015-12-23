package dmitriy.com.musicshop.models;

import java.math.BigDecimal;

public class InstrumentModel {
    Instrument instrument;
    long quantity;

    public class Instrument {
        long id;
        String brand;
        String model;
        String type;
        String price;

        public String getPrice() {
            return price;
        }

        public String getType() {
            return type;
        }

        public String getModel() {
            return model;
        }

        public String getBrand() {
            return brand;
        }

        public long getId() {
            return id;
        }
    }

    public Instrument getInstrument() {
        return instrument;
    }

    public long getQuantity() {
        return quantity;
    }
}
