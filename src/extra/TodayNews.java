package extra;

import ecommander.pages.Command;
import ecommander.pages.ResultPE;

import java.util.Date;

public class TodayNews extends Command {
    private static final long DAY = 1000 * 60 * 60 * 72;

    @Override
    public ResultPE execute() throws Exception {
        return null;
    }

    public ResultPE today() throws Exception {
        long now = new Date().getTime();
        long yesterday = (now / DAY) * DAY;
        setPageVariable("yesterday", String.valueOf(yesterday));
        return null;
    }
}
