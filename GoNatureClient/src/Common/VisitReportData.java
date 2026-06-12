package Common;

import java.io.Serializable;

@SuppressWarnings("serial")
public class VisitReportData implements Serializable {

    private int individualVisitors;
    private int groupVisitors;

    public VisitReportData(int individualVisitors, int groupVisitors) {
        this.individualVisitors = individualVisitors;
        this.groupVisitors = groupVisitors;
    }

    public int getIndividualVisitors() {
        return individualVisitors;
    }

    public int getGroupVisitors() {
        return groupVisitors;
    }
}