package net.coding.program.model.user;

import java.util.List;

/**
 * Created by anfs on 30/11/2016.
 */

public class ActiveModel {
    public List<ActivenessModel> daily_activeness;
    public long total;
    public long last_activity_at;
    public long total_with_seal_top_line;
    public String start_date;
    public String end_date;
    public ActiveDuratioModel longest_active_duration;
    public ActiveDuratioModel current_active_duration;
}
