package jobs.rec.jobrecsoftware;

import java.security.MessageDigest;
import java.sql.*;

public class DatabaseDao {

    public static Connection getConnection() throws ClassNotFoundException, SQLException{
        String usernameDB = "root";
        String passwordDB = "1234";
        String urlPort = "localhost:3307";
        String databaseName = "jobrec";
        String url = "jdbc:mysql://"+urlPort+"/"+databaseName;
        String dbDriver = "com.mysql.cj.jdbc.Driver";
        Class.forName(dbDriver);
        Connection connection = DriverManager.getConnection(url,usernameDB,passwordDB);
        return connection;
    }

    public static String testConnection() throws SQLException {
        Connection connection = null;
        String message = "";
        try {
            connection = DatabaseDao.getConnection();

            if(!connection.isClosed()){

                message = "Connection Success";
                connection.close();
            }

        } catch (Exception e) {
            message = "Connection Failed";
            //connection.close();
        }

        return message;
    }


    public static String getHashPass(final String pwd) {
        try{
            final MessageDigest digest = MessageDigest.getInstance("SHA-256");
            final byte[] hash = digest.digest(pwd.getBytes("UTF-8"));
            final StringBuilder hexString = new StringBuilder();
            for (int i = 0; i < hash.length; i++) {
                final String hex = Integer.toHexString(0xff & hash[i]);
                if(hex.length() == 1)
                    hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch(Exception ex){
            throw new RuntimeException(ex);
        }
    }

    public static boolean emailExist(String email) throws ClassNotFoundException, SQLException {
        String sql = "SELECT * FROM user WHERE email=? LIMIT 1";

        Connection con = DatabaseDao.getConnection();

        PreparedStatement ps = con.prepareStatement(sql);

        ps.setString(1, email);

        ResultSet rs = ps.executeQuery();
        if(rs.next()) {
            con.close();
            return true;
        }else {
            con.close();
            return false;
        }
    }

    public static boolean login(String email,String password) throws ClassNotFoundException, SQLException{
        String hash_value_pwd = DatabaseDao.getHashPass(password);
        String sql = "SELECT * FROM user WHERE email=?";

        Connection con = DatabaseDao.getConnection();

        PreparedStatement ps = con.prepareStatement(sql);
        ps.setString(1, email);


        ResultSet rs = ps.executeQuery();

        if(rs.next()) {
            String pwd = rs.getString("password");

            if(hash_value_pwd.equals(pwd)) {
                return true;
            }
        }

        return false;
    }

    public static int createAdmin() throws ClassNotFoundException, SQLException{

        String sql = "INSERT INTO user(full_name, email, password, gender, usertype)\n" +
                "SELECT ?, ?, ?, ?, ?\n" +
                "WHERE NOT EXISTS (\n" +
                "    SELECT 1 FROM user WHERE email = ?" +
                ")";

        Connection con = DatabaseDao.getConnection();

        String pwd = DatabaseDao.getHashPass("admin");

        PreparedStatement ps = con.prepareStatement(sql);
        ps.setString(1,"Administrator");
        ps.setString(2,"admin@gmail.com");
        ps.setString(3,pwd);
        ps.setString(4,"M");
        ps.setString(5,"A");
        ps.setString(6,"admin@gmail.com");


        int row = ps.executeUpdate();

        return row;

    }

    public static String getUserType(String email) throws ClassNotFoundException, SQLException{

        String sql = "SELECT usertype FROM user WHERE email=?";

        Connection con = DatabaseDao.getConnection();

        PreparedStatement ps = con.prepareStatement(sql);
        ps.setString(1,email);

        ResultSet rs = ps.executeQuery();

        String userType = null;
        while (rs.next()){
            userType = rs.getString("usertype");
        }

       return userType;
    }

    public static int getUserID(String email) throws ClassNotFoundException, SQLException{

        String sql = "SELECT id FROM user WHERE email=?";

        Connection con = DatabaseDao.getConnection();

        PreparedStatement ps = con.prepareStatement(sql);
        ps.setString(1,email);

        ResultSet rs = ps.executeQuery();

        int id = 0;
        while (rs.next()){
            id = rs.getInt("id");
        }

        return id;
    }

    public static int insertUser(String fullName,String email,String gender,String userType) throws ClassNotFoundException, SQLException{

        String sql = "INSERT INTO user(full_name,email,password,gender,usertype) VALUES(?,?,?,?,?)";

        Connection con = DatabaseDao.getConnection();

        String password = DatabaseDao.getHashPass("1234");
        PreparedStatement ps = con.prepareStatement(sql);
        ps.setString(1,fullName);
        ps.setString(2,email);
        ps.setString(3,password);
        ps.setString(4,gender);
        ps.setString(5,userType);

        int row = ps.executeUpdate();

        return row;

    }

    public static int insertUser2(String fullName,String email,String pwd,String gender,String userType) throws ClassNotFoundException, SQLException{

        String sql = "INSERT INTO user(full_name,email,password,gender,usertype) VALUES(?,?,?,?,?)";

        Connection con = DatabaseDao.getConnection();

        String password = DatabaseDao.getHashPass(pwd);
        PreparedStatement ps = con.prepareStatement(sql);
        ps.setString(1,fullName);
        ps.setString(2,email);
        ps.setString(3,password);
        ps.setString(4,gender);
        ps.setString(5,userType);

        int row = ps.executeUpdate();

        return row;

    }


    public static Profile getUserInfo(int id) throws ClassNotFoundException, SQLException{

        String sql = "SELECT * FROM user WHERE id="+id;

        Connection con = DatabaseDao.getConnection();


        PreparedStatement ps = con.prepareStatement(sql);

        ResultSet rs = ps.executeQuery();

        Profile p = new Profile();
        while (rs.next()){
            p.setFull_name(rs.getString("full_name"));
            p.setEmail(rs.getString("email"));

            if(rs.getString("gender").equals("M")){
                p.setGender("Male");
            }else if(rs.getString("gender").equals("F")){
                p.setGender("Female");
            }else{
                p.setGender("Other");
            }
        }
        return p;
    }

    public static void updateName(int id,String full_name) throws ClassNotFoundException, SQLException{

        String sql = "UPDATE user SET full_name=? WHERE id="+id;

        Connection con = DatabaseDao.getConnection();

        PreparedStatement ps = con.prepareStatement(sql);
        ps.setString(1,full_name);

        ps.executeUpdate();
    }

    public static void updateEmail(int id,String email) throws ClassNotFoundException, SQLException{

        String sql = "UPDATE user SET email=? WHERE id="+id;

        Connection con = DatabaseDao.getConnection();

        PreparedStatement ps = con.prepareStatement(sql);
        ps.setString(1,email);

        ps.executeUpdate();
    }


    public static void updateGender(int id,String gender) throws ClassNotFoundException, SQLException{

        String sql = "UPDATE user SET gender=? WHERE id="+id;

        Connection con = DatabaseDao.getConnection();

        PreparedStatement ps = con.prepareStatement(sql);

        if(gender.equals("Male")){
            ps.setString(1,"M");
        }else if(gender.equals("Female")){
            ps.setString(1,"F");
        }else{
            ps.setString(1,"N");
        }

        ps.executeUpdate();
    }


    public static void insertUpdate(int id,String name) throws ClassNotFoundException, SQLException{

        String sql = "SELECT * FROM company WHERE rep_id="+id;

        Connection con = DatabaseDao.getConnection();

        PreparedStatement ps = con.prepareStatement(sql);

        ResultSet rs = ps.executeQuery();

        if(rs.next()){
            String updateSql = "UPDATE company SET name = ? WHERE rep_id="+id;
            Connection con2 = DatabaseDao.getConnection();
            PreparedStatement ps2 = con2.prepareStatement(updateSql);
            ps2.setString(1, name);
            ps2.executeUpdate();
            con2.close();
        }else{
            String insertSql = "INSERT INTO company(name,rep_id) VALUES(?,?)";
            Connection con2 = DatabaseDao.getConnection();
            PreparedStatement ps2 = con2.prepareStatement(insertSql);
            ps2.setString(1, name);
            ps2.setInt(2, id);
            ps2.executeUpdate();
            con2.close();
        }
    }

    public static int getCompanyID(int rep_id) throws ClassNotFoundException, SQLException{

        String sql = "SELECT id FROM company WHERE rep_id=?";

        Connection con = DatabaseDao.getConnection();

        PreparedStatement ps = con.prepareStatement(sql);
        ps.setInt(1,rep_id);

        ResultSet rs = ps.executeQuery();

        int id = 0;
        while (rs.next()){
            id = rs.getInt("id");
        }

        return id;
    }


    public static String getCompanyName(int rep_id) throws ClassNotFoundException, SQLException{

        String sql = "SELECT name FROM company WHERE rep_id=?";

        Connection con = DatabaseDao.getConnection();

        PreparedStatement ps = con.prepareStatement(sql);
        ps.setInt(1,rep_id);

        ResultSet rs = ps.executeQuery();

        String  name = "";
        while (rs.next()){
            name = rs.getString("name");
        }

        return name;
    }


    public static int insertJobAd(int company_id,String name,String description,String category,String premium,double price,String keyword) throws ClassNotFoundException, SQLException{

        String sql = "INSERT INTO job(company_id,name,description,category,premium,price,keyword) VALUES(?,?,?,?,?,?,?)";

        Connection con = DatabaseDao.getConnection();

        String password = DatabaseDao.getHashPass("1234");
        PreparedStatement ps = con.prepareStatement(sql);
        ps.setInt(1,company_id);
        ps.setString(2,name);
        ps.setString(3,description);
        ps.setString(4,category);
        ps.setString(5,premium);
        ps.setDouble(6,price);
        ps.setString(7,keyword);
        int row = ps.executeUpdate();
        return row;
    }


    public static Credential getUserCredentials(int user_id) throws ClassNotFoundException, SQLException{

        String sql = "SELECT * FROM credentials WHERE user_id="+user_id;

        Connection con = DatabaseDao.getConnection();


        PreparedStatement ps = con.prepareStatement(sql);

        ResultSet rs = ps.executeQuery();

        Credential c = new Credential();
        while (rs.next()){
           c.setId(rs.getInt("id"));
           c.setUser_id(rs.getInt("user_id"));
           c.setEducation(rs.getString("education"));
           c.setCategory(rs.getString("category"));
           c.setExperience(rs.getString("experience"));
           c.setCeification(rs.getString("ceification"));
           c.setDocument(rs.getBlob("document"));
        }
        return c;
    }


    public static void insertUpdateCredentials(int user_id,String data,String column) throws ClassNotFoundException, SQLException{

        String sql = "SELECT ? FROM credentials WHERE user_id="+user_id;

        Connection con = DatabaseDao.getConnection();

        PreparedStatement ps = con.prepareStatement(sql);
        ps.setString(1,column);

        ResultSet rs = ps.executeQuery();

        if(rs.next()){
            String updateSql = null;

            if(column.equals("education")){
                updateSql = "UPDATE credentials SET education = ? WHERE user_id="+user_id;
            }else if(column.equals("category")) {
                updateSql = "UPDATE credentials SET category = ? WHERE user_id="+user_id;
            }else if(column.equals("experience")) {
                updateSql = "UPDATE credentials SET experience = ? WHERE user_id="+user_id;
            }else if(column.equals("ceification")) {
                updateSql = "UPDATE credentials SET ceification = ? WHERE user_id="+user_id;
            }else{

            }



            Connection con2 = DatabaseDao.getConnection();
            PreparedStatement ps2 = con2.prepareStatement(updateSql);
            ps2.setString(1, data);
            ps2.executeUpdate();
            con2.close();
        }else{
            String insertSql = null;
            if(column.equals("education")){
                insertSql = "INSERT INTO credentials(user_id,education) VALUES(?,?)";
            }else if(column.equals("category")){
                insertSql = "INSERT INTO credentials(user_id,category) VALUES(?)";
            }else if(column.equals("experience")){
                insertSql = "INSERT INTO credentials(user_id,experience) VALUES(?)";
            }else if(column.equals("ceification")){
                insertSql = "INSERT INTO credentials(user_id,ceification) VALUES(?)";
            }else{

            }

            Connection con2 = DatabaseDao.getConnection();
            PreparedStatement ps2 = con2.prepareStatement(insertSql);
            ps2.setInt(1,user_id);
            ps2.setString(2, data);
            ps2.executeUpdate();
            con2.close();
        }
    }


    public static int recommendToStudent(int user_id,int job_id) throws SQLException, ClassNotFoundException {
        String sql = "INSERT INTO recommendation(job_id,user_id) VALUES(?,?)";

        Connection con = DatabaseDao.getConnection();

        PreparedStatement ps = con.prepareStatement(sql);
        ps.setInt(1,job_id);
        ps.setInt(2,user_id);


        int row = ps.executeUpdate();


        return row;
    }

    public static int applyJob(int user_id,int job_id) throws SQLException, ClassNotFoundException {
        String sql = "INSERT INTO application(user_id, job_id) SELECT * FROM (SELECT ? AS user_id, ? AS job_id) AS new_value WHERE NOT EXISTS (SELECT user_id FROM application WHERE job_id = ?) LIMIT 1;";

        Connection con = DatabaseDao.getConnection();

        PreparedStatement ps = con.prepareStatement(sql);
        ps.setInt(1,user_id);
        ps.setInt(2,job_id);
        ps.setInt(3,job_id);

        int row = ps.executeUpdate();


        return row;
    }

    public static String getCompanyInfo(int job_id) throws ClassNotFoundException, SQLException{

        String sql = "SELECT job.*, company.name AS company_name, company.rep_id FROM job INNER JOIN company ON job.company_id = company.id WHERE job.id ="+job_id;

        Connection con = DatabaseDao.getConnection();


        PreparedStatement ps = con.prepareStatement(sql);

        ResultSet rs = ps.executeQuery();

        String  company_name = "";
        while (rs.next()){
            company_name = rs.getString("company_name");
        }
        return company_name;
    }


    public static int acceptApplication(int id) throws ClassNotFoundException, SQLException{

        String sql = "UPDATE application SET process=2 WHERE id="+id;

        Connection con = DatabaseDao.getConnection();

        String password = DatabaseDao.getHashPass("1234");
        PreparedStatement ps = con.prepareStatement(sql);


        int row = ps.executeUpdate();

        return row;

    }

    public static int rejectApplication(int id) throws ClassNotFoundException, SQLException{

        String sql = "UPDATE application SET process=1 WHERE id="+id;

        Connection con = DatabaseDao.getConnection();

        String password = DatabaseDao.getHashPass("1234");
        PreparedStatement ps = con.prepareStatement(sql);


        int row = ps.executeUpdate();

        return row;

    }

}
