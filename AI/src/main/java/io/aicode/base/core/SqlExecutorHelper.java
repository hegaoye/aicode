package io.aicode.base.core;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SqlExecutorHelper {

    public static List<Map> queryForList(Connection conn, String sql, int limit) throws SQLException {
        PreparedStatement ps = conn.prepareStatement(sql.trim());
        ps.setMaxRows(limit);
        ps.setFetchDirection(ResultSet.FETCH_FORWARD);
        ResultSet rs = ps.executeQuery();
        return toListMap(limit, rs);
    }

    public static List<Map> toListMap(int limit, ResultSet rs) throws SQLException {
        ResultSetMetaData rsmd = rs.getMetaData();
        int count = 0;
        List<Map> list = new ArrayList<Map>();
        while (rs.next()) {
            Map row = new HashMap();
            for (int i = 1; i <= rsmd.getColumnCount(); i++) {
                row.put(rsmd.getColumnName(i), rs.getObject(i));
            }
            list.add(row);
            count++;
            if (count >= limit) {
                break;
            }
        }
        return list;
    }

}
