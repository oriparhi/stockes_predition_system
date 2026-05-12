package orip.stocks_prediction_system.utilities;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public enum TopStocks 
{
    NVDA("NVDA"), AAPL("AAPL"), MSFT("MSFT"), GOOGL("GOOGL"), AMZN("AMZN"),
    META("META"), AVGO("AVGO"), TSLA("TSLA"), COST("COST"), NFLX("NFLX"),
    AMD("AMD"), PEP("PEP"), ADBE("ADBE"), CSCO("CSCO"), TMUS("TMUS"),
    QCOM("QCOM"), INTU("INTU"), AMAT("AMAT"), TXN("TXN"), MU("MU"),
    AMGN("AMGN"), ISRG("ISRG"), HON("HON"), KLAC("KLAC"), SBUX("SBUX"),

    // NYSE
    RTX("RTX"), LLY("LLY"), JPM("JPM"), V("V"), WMT("WMT"),
    XOM("XOM"), MA("MA"), UNH("UNH"), JNJ("JNJ"), PG("PG"),
    HD("HD"), ORCL("ORCL"), ABBV("ABBV"), MRK("MRK"), BAC("BAC"),
    CVX("CVX"), KO("KO"), CRM("CRM"), MCD("MCD"), DIS("DIS"),
    PFE("PFE"), IBM("IBM"), GE("GE"), GS("GS"), CAT("CAT");
    
    private final String apiValue;

    TopStocks(String apiValue) {
        this.apiValue = apiValue;
    }

    public String getApiValue() {
        return apiValue;
    }

    /**
     * מחזירה רשימה של כל ה-tickers (מחרוזות) לשימוש ב-ComboBox
     */
    public static List<String> getAllTickers() {
        return Arrays.stream(TopStocks.values())
                .map(TopStocks::getApiValue)
                .collect(Collectors.toList());
    }
}
