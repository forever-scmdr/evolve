package extra;

import ecommander.pages.Command;
import ecommander.pages.ResultPE;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

public class TodayNews extends Command {
    @Override
    public ResultPE execute() throws Exception {
       return null;
    }

    public ResultPE today() throws Exception {
        LocalDate today = LocalDate.now();
        LocalDate yesterday = today.minusDays(1);
        LocalDate tomorrow = today.plusDays(1);
        ZoneId zoneId = ZoneId.of("UTC");
        long yesterdayMs = yesterday.atStartOfDay(zoneId).toEpochSecond();
        System.out.println(new Date(yesterdayMs));
        long tomorrowMs =  tomorrow.atStartOfDay(zoneId).toEpochSecond();
        setPageVariable("yesterday", String.valueOf(yesterdayMs));
        setPageVariable("tomorrow", String.valueOf(tomorrowMs));
        return null;
    }
}
