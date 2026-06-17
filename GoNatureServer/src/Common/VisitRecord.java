package Common;

import java.io.Serializable;
import java.time.LocalDateTime;

@SuppressWarnings("serial")
public class VisitRecord implements Serializable{

    public LocalDateTime entry;
    public LocalDateTime exit;
    public int count;

    public VisitRecord(LocalDateTime entry, LocalDateTime exit, int count) {
        this.entry = entry;
        this.exit = exit;
        this.count = count;
    }
}