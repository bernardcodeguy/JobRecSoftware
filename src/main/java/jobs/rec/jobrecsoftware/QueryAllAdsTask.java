package jobs.rec.jobrecsoftware;

import javafx.concurrent.Task;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class QueryAllAdsTask extends Task<List<Job>> {
    @Override
    protected List<Job> call() throws Exception {
        Connection con = DatabaseDao.getConnection();

        String sql = "SELECT * FROM job";
        Statement st = con.createStatement();

        ResultSet rs = st.executeQuery(sql);

        List<Job> list = new ArrayList<>();

        int num = 1;

        while(rs.next()){
            Job j = new Job();
            j.setNumber(num);
            j.setId(rs.getInt("id"));
            j.setCompany_id(rs.getInt("company_id"));
            j.setName(rs.getString("name"));
            j.setDescription(rs.getString("description"));
            j.setCategory(rs.getString("category"));
            j.setType(rs.getString("premium"));
            j.setPrice(rs.getDouble("price"));
            j.setKeyword(rs.getString("keyword"));
            list.add(j);
            num++;
        }

        return list;
    }
}
