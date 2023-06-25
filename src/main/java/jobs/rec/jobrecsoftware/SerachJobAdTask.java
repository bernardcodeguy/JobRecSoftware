package jobs.rec.jobrecsoftware;

import javafx.concurrent.Task;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class SerachJobAdTask extends Task<List<Job>> {
    private final String query;

    public SerachJobAdTask(String query) {
        this.query = query;
    }

    @Override
    protected List<Job> call() throws Exception {
        Connection con = DatabaseDao.getConnection();

       String sql = "SELECT * FROM job WHERE LOCATE(?, name) > 0  OR LOCATE(?, premium) > 0 OR LOCATE(?, keyword)> 0 OR LOCATE(?, category) > 0";

        //String sql = "SELECT * FROM job";
        PreparedStatement st = con.prepareStatement(sql);
        st.setString(1,this.query);
        st.setString(2,this.query);
        st.setString(3,this.query);
        st.setString(4,this.query);

        ResultSet rs = st.executeQuery();



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
