package jobs.rec.jobrecsoftware;

import javafx.concurrent.Task;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class QueryApplicationTask extends Task<List<Application>> {
    private final int user_id;

    public QueryApplicationTask(int user_id) {
        this.user_id = user_id;
    }


    @Override
    protected List<Application> call() throws Exception {
        Connection con = DatabaseDao.getConnection();

        String sql = "SELECT application.*, job.name, job.description, job.category, job.premium, job.price," +
                " job.keyword FROM application INNER JOIN job ON application.job_id = job.id WHERE application.user_id ="+user_id;
        Statement st = con.createStatement();

        ResultSet rs = st.executeQuery(sql);

        List<Application> list = new ArrayList<>();

        int num = 1;

        while(rs.next()){
            Application j = new Application();
            j.setNumber(num);
            j.setId(rs.getInt("id"));
            j.setJob_id(rs.getInt("job_id"));
            j.setUser_id(rs.getInt("user_id"));
            j.setName(rs.getString("name"));
            j.setDescription(rs.getString("description"));
            j.setCategory(rs.getString("category"));
            j.setType(rs.getString("premium"));
            j.setPrice(rs.getDouble("price"));
            j.setProcess(rs.getInt("process"));
            if(rs.getInt("process") == 0){
                j.setProcessString("Pending");
            }else if(rs.getInt("process") == 1){
                j.setProcessString("Rejected");
            }else{
                j.setProcessString("Accepted");
            }

            list.add(j);
            num++;
        }

        return list;
    }
}
