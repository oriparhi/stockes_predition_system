package orip.stocks_prediction_system.utilities;

import java.util.List;

import orip.stocks_prediction_system.datamodels.DataPoints;

public record ReadCsvResponse( List<DataPoints> data, String title) {
   }
