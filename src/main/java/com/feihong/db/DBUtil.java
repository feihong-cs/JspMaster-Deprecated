package com.feihong.db;

import com.feihong.bean.ShellEntry;
import com.feihong.util.BasicSetting;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DBUtil {
    public static Connection getConnection() throws SQLException, ClassNotFoundException {
        Class.forName("org.sqlite.JDBC");
        String db = BasicSetting.getInstance().dbFile;
        Connection conn = DriverManager.getConnection("jdbc:sqlite:" + db);
        return conn;
    }

    public static void save(ShellEntry  entry) throws SQLException, ClassNotFoundException {
        Connection connection = getConnection();
        PreparedStatement preparedStatement;

        if(entry.getId() != 0){
            String sql = "update shells set url=?, password=?, type=?, create_time=?, lastvisit_time=?, remarks=?, headers=?, is_encrypt=?, encrypt_key=? ,iv = ? where id=?";
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, entry.getUrl());
            preparedStatement.setString(2, entry.getPassword());
            preparedStatement.setString(3, entry.getType());
            preparedStatement.setString(4, entry.getCreateTime());
            preparedStatement.setString(5, entry.getLastvisitTime());
            preparedStatement.setString(6,entry.getRemarks());
            preparedStatement.setString(7, mapToString(entry.getHeaders()));
            preparedStatement.setInt(8, entry.getIsEncrypt());
            preparedStatement.setString(9, entry.getEncryptKey());
            preparedStatement.setString(10, entry.getIV());
            preparedStatement.setInt(11, entry.getId());

            preparedStatement.execute();

        }else{
            String sql = "insert into shells(url, password, type, create_time, lastvisit_time, remarks, headers, is_encrypt, encrypt_key, iv) values(?,?,?,?,?,?,?,?,?,?)";
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, entry.getUrl());
            preparedStatement.setString(2, entry.getPassword());
            preparedStatement.setString(3, entry.getType());
            preparedStatement.setString(4, entry.getCreateTime());
            preparedStatement.setString(5, entry.getLastvisitTime());
            preparedStatement.setString(6,entry.getRemarks());
            preparedStatement.setString(7, mapToString(entry.getHeaders()));
            preparedStatement.setInt(8, entry.getIsEncrypt());
            preparedStatement.setString(9, entry.getEncryptKey());
            preparedStatement.setString(10, entry.getIV());

            preparedStatement.execute();
        }

        preparedStatement.close();
        connection.close();
    }

    public static void delete(ShellEntry entry) throws SQLException, ClassNotFoundException {
        String sql = "delete from shells where id=?";
        Connection connection = getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        preparedStatement.setInt(1, entry.getId());
        preparedStatement.execute();
        preparedStatement.close();
        connection.close();
    }

    public static ShellEntry query(int id) throws SQLException, ClassNotFoundException {
        // 如果数据库中未查到响应的记录，返回一个 null
        Connection connection = getConnection();

        String sql = "select * from shells where id=?";
        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        preparedStatement.setInt(1, id);

        ResultSet resultSet = preparedStatement.executeQuery();
        ShellEntry entry = null;
        if(resultSet.next()){
            entry = new ShellEntry();
            entry.setId(id);
            entry.setUrl(resultSet.getString("url"));
            entry.setPassword(resultSet.getString("password"));
            entry.setType(resultSet.getString("type"));
            entry.setCreateTime(resultSet.getString("create_time"));
            entry.setLastvisitTime(resultSet.getString("lastvisit_time"));
            entry.setRemarks(resultSet.getString("remarks"));
            entry.setHeaders(stringToMap(resultSet.getString("headers")));
            entry.setIsEncrypt(resultSet.getInt("is_encrypt"));
            entry.setEncryptKey(resultSet.getString("encrypt_key"));
            entry.setIV(resultSet.getString("iv"));
        }

        resultSet.close();
        preparedStatement.close();
        connection.close();
        return entry;
    }

    public static List<ShellEntry> queryAll() throws SQLException, ClassNotFoundException {
        //如果数据库里面没有数据，返回一个空的 ArrayList
        Connection connection = getConnection();

        try{
            String sql = "select * from shells";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            ResultSet resultSet = preparedStatement.executeQuery();

            List<ShellEntry> list = new ArrayList<>();
            while(resultSet.next()){
                ShellEntry shellEntry = new ShellEntry();
                shellEntry.setId(resultSet.getInt("id"));
                shellEntry.setUrl(resultSet.getString("url"));
                shellEntry.setPassword(resultSet.getString("password"));
                shellEntry.setType(resultSet.getString("type"));
                shellEntry.setCreateTime(resultSet.getString("create_time"));
                shellEntry.setLastvisitTime(resultSet.getString("lastvisit_time"));
                shellEntry.setRemarks(resultSet.getString("remarks"));
                shellEntry.setHeaders(stringToMap(resultSet.getString("headers")));
                shellEntry.setIsEncrypt(resultSet.getInt("is_encrypt"));
                shellEntry.setEncryptKey(resultSet.getString("encrypt_key"));
                shellEntry.setIV(resultSet.getString("iv"));

                list.add(shellEntry);
            }

            resultSet.close();
            preparedStatement.close();
            connection.close();
            return list;
        }catch(SQLException e){
            //这里之所以这么写的原因是因为在打开程序的时候会对数据库做解密操作，随后调用 DBUtil.queryAll() 方法确认是否解密成功
            //如果解密不成功，则会抛 SQLException，导致 conn.close() 未得到执行，从而资源未释放导致 data.db 无法删除
            //这里这么写的目的就是保证用户在输入密码错误时，data.db 能够被正常删除
            connection.close();
            throw  e;
        }
    }

    public static String mapToString(Map<String, String> map){
        StringBuilder sb = new StringBuilder();
        if(map != null){
            for(Map.Entry<String, String> entry : map.entrySet()){
                sb.append(entry.getKey());
                sb.append("=");
                sb.append(entry.getValue());
                sb.append("&");
            }

            if(sb.length() > 0){
                sb.deleteCharAt(sb.length()-1);
            }

            return sb.toString();
        }

        return "";

    }

    public static Map<String, String> stringToMap(String str){
        Map<String, String> map = new HashMap<>();

        if(str != null && str.length() > 0) {
            String[] params = str.split("&");
            for (String str1 : params) {
                String[] temp = str1.split("=");
                map.put(temp[0], temp[1]);
            }
        }

        return map;
    }
}
