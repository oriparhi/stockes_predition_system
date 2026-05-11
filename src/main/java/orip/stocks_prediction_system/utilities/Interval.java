package orip.stocks_prediction_system.utilities;

public enum Interval {
    MIN_1("1min"),
    MIN_5("5min"),
    MIN_15("15min"),
    MIN_30("30min"),
    MIN_45("45min"),
    HOUR_1("1h"),
    HOUR_2("2h"),
    HOUR_4("4h"),
    HOUR_8("8h"),
    DAY_1("1day"),
    WEEK_1("1week"),
    MONTH_1("1month");

    private final String apiValue;

    // בנאי המקבל את הערך עבור ה-API
    Interval(String apiValue) {
        this.apiValue = apiValue;
    }

    // מתודה לשליפת הערך
    public String getApiValue() {
        return apiValue;
    }
}