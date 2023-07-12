package account.CustomDeserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class CustomMonthYearDeserializer extends JsonDeserializer<Date> {

    private static final String DATE_FORMAT = "MM-yyyy";

    @Override
    public Date deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
        String dateStr = jsonParser.getText();
        SimpleDateFormat formatter = new SimpleDateFormat(DATE_FORMAT);
        formatter.setLenient(false);

        try {
            Date date = formatter.parse(dateStr); //wrong month ko ye parse hi nai krpayega aur exception throw krdega islie hamne alag se month ko validate kn eki zarurt nai hai
            System.out.println("Date after formating: "+date);
//            validateMonthYear(date);
            return date;
        } catch (ParseException e) {
            throw new IOException("Failed to parse date due to wrong month or year: " + dateStr, e);
        }
    }

//    private void validateMonthYear(Date date) throws IOException {
//        Calendar calendar = Calendar.getInstance();
//        calendar.setTime(date);
//        int month = calendar.get(Calendar.MONTH) + 1; // Adding 1 to match with human-readable month values
//
//        if (month < 1 || month > 12) {
//            throw new IOException("Invalid month: " + month);
//        }
//    }
}
