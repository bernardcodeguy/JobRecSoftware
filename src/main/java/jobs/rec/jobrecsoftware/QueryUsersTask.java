package jobs.rec.jobrecsoftware;

import javafx.concurrent.Task;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class QueryUsersTask extends Task<List<User>> {
    @Override
    protected List<User> call() throws Exception {

        Connection con = DatabaseDao.getConnection();

        String sql = "SELECT * FROM user WHERE email != 'admin@gmail.com'";
        Statement st = con.createStatement();

        ResultSet rs = st.executeQuery(sql);

        List<User> list = new ArrayList<>();

        int num = 1;
        String gender = "";
        String type = "";
        while(rs.next()){
            User u = new User();
            u.setNumber(num);
            u.setId(rs.getInt("id"));
            u.setFull_name(rs.getString("full_name"));
            u.setEmail(rs.getString("email"));
            if(rs.getString("gender").equals("M")){
                gender = "Male";
            }else if(rs.getString("gender").equals("F")){
                gender = "Female";
            }else{
                gender = "Other";
            }
            u.setGender(gender);

            if(rs.getString("usertype").equals("S")){
                type = "Student";
            }else if(rs.getString("usertype").equals("R")){
                type = "Company Representative";
            }
            u.setUsertype(type);

            list.add(u);
            num++;
        }

        return list;
    }
}
