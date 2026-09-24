package database;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

public class TableData {

    public enum QUERY_TYPE {
        MIN, MAX
    }

    private DbAccess db;

    public TableData(DbAccess db) {
        this.db = db;
    }

    public List<Example> getTransazioni(String table) throws SQLException, EmptySetException {
        LinkedList<Example> transSet = new LinkedList<Example>();
        Statement statement;
        TableSchema tSchema = new TableSchema(db, table);

        String query = "SELECT ";

        for (int i = 0; i < tSchema.getNumberOfAttributes(); i++) {
            Column c = tSchema.getColumn(i);
            if (i > 0)
                query += ",";
            query += c.getColumnName();
        }
        if (tSchema.getNumberOfAttributes() == 0)
            throw new SQLException("La tabella non ha colonne o non esiste");
        query += (" FROM " + table);

        statement = db.getConnection().createStatement();
        ResultSet rs = statement.executeQuery(query);
        boolean empty = true;
        while (rs.next()) {
            empty = false;
            Example currentTuple = new Example();
            for (int i = 0; i < tSchema.getNumberOfAttributes(); i++) {
                if (tSchema.getColumn(i).isNumber())
                    currentTuple.add(rs.getDouble(i + 1));
                else
                    currentTuple.add(rs.getString(i + 1));
            }
            transSet.add(currentTuple);
        }
        rs.close();
        statement.close();
        if (empty)
            throw new EmptySetException("Nessuna tupla restituita dalla tabella " + table);

        return transSet;
    }

    public Set<Object> getDistinctColumnValues(String table, Column column) throws SQLException {
        Set<Object> set = new TreeSet<Object>();
        Statement statement = db.getConnection().createStatement();
        String query = "SELECT DISTINCT " + column.getColumnName() + " FROM " + table + " ORDER BY "
                + column.getColumnName() + " ASC;";
        ResultSet rs = statement.executeQuery(query);
        while (rs.next()) {
            if (column.isNumber()) {
                set.add(rs.getDouble(1));
            } else {
                set.add(rs.getString(1));
            }
        }
        rs.close();
        statement.close();
        return set;
    }

    public Object getAggregateColumnValue(String table, Column column, QUERY_TYPE aggregate)
            throws SQLException, EmptySetException {
        Statement statement = db.getConnection().createStatement();
        String query = "SELECT " + aggregate + "(" + column.getColumnName() + ") FROM " + table + ";";
        ResultSet rs = statement.executeQuery(query);
        Object value = null;
        if (rs.next()) {
            if (column.isNumber()) {
                value = rs.getDouble(1);
            } else {
                value = rs.getString(1);
            }
        }
        rs.close();
        statement.close();
        if (value == null) {
            throw new EmptySetException("Valore aggregato nullo per " + column.getColumnName());
        }
        return value;
    }
}
