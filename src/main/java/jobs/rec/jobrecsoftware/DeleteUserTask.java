package jobs.rec.jobrecsoftware;

import javafx.concurrent.Task;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Statement;

public class DeleteUserTask extends Task<Boolean> {
    private final int id;

    public DeleteUserTask(int id) {
        this.id = id;
    }

    @Override
    protected Boolean call() throws Exception {
        Connection con = DatabaseDao.getConnection();

        String sql = "DELETE FROM user WHERE id="+this.id;
        PreparedStatement ps = con.prepareStatement(sql);

        int row = ps.executeUpdate();

        if(row > 0){
            return true;
        }

        return false;
    }
}
