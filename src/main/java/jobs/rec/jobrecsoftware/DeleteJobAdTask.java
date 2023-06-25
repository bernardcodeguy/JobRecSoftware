package jobs.rec.jobrecsoftware;

import javafx.concurrent.Task;

import java.sql.Connection;
import java.sql.PreparedStatement;

public class DeleteJobAdTask extends Task<Boolean> {
    private final int id;

    public DeleteJobAdTask(int id) {
        this.id = id;
    }

    @Override
    protected Boolean call() throws Exception {
        Connection con = DatabaseDao.getConnection();

        String sql = "DELETE FROM job WHERE id="+this.id;
        PreparedStatement ps = con.prepareStatement(sql);

        int row = ps.executeUpdate();

        if(row > 0){
            return true;
        }

        return false;
    }
}
