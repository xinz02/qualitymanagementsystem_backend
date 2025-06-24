package com.qualitymanagementsystemfc.qualitymanagementsystem.utils;

import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Date;

@Component
public class DateUtils {

    public String convertISODateToDate(Date ISODate){
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        String formatted = sdf.format(ISODate);
        return formatted;
    }

    public String setTodayDate(){
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        String formatted = sdf.format(new Date());
        return formatted;
    }

    public String setTodayDateForTarikh() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String formatted = sdf.format(new Date());
        return formatted;
    }


}
