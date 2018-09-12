package com.zushou365.houseimageupload.dbmanager;

import java.lang.reflect.*;
import java.sql.*;
import java.util.HashMap;
import org.apache.commons.dbcp.DelegatingConnection;

public class DBUtil {
    private static class MySQLStatementInvocationHandler
        implements InvocationHandler {

        Statement _statement;

        public Object invoke(Object proxy, Method method, Object args[]) throws SQLException {
            try{
                if(method.getName().toLowerCase().startsWith("execute") && args.length > 0 && (args[0] instanceof String)) {
                    Object argsCopy[] = new Object[args.length];
                    System.arraycopy(((Object) (args)), 0, ((Object) (argsCopy)), 0, args.length);
                    argsCopy[0] = ((Object) (((String)argsCopy[0]).replace("\\", "\\\\")));
                    args = argsCopy;
                }            
                return method.invoke(((Object) (_statement)), args);
            }catch(IllegalAccessException e){            
                throw (SQLException)e.getCause();
            }catch(InvocationTargetException e){          
                throw (SQLException)((InvocationTargetException) (e)).getCause();
            }
        }

        public MySQLStatementInvocationHandler(Statement statement) {
            _statement = statement;
        }
    }

    private static class MySQLConnectionInvocationHandler
        implements InvocationHandler {

        private final Connection _connection;

        public Object invoke(Object proxy, Method method, Object args[]) throws SQLException {
            InvocationHandler handler;
            if("createStatement".equals(((Object) (method.getName())))){
                try{
                    handler = ((InvocationHandler) (new MySQLStatementInvocationHandler(_connection.createStatement())));
                    return DBUtil._MYSQL_STATEMENT_PROXY_CLASS.getConstructor(new Class[] {
                        java.lang.reflect.InvocationHandler.class
                    }).newInstance(new Object[] {
                        handler
                    });
                }catch(Exception e){                
                    throw (SQLException)e.getCause();
                }
            }
            try{
                return method.invoke(((Object) (_connection)), args);            
            }catch(IllegalAccessException e){            
                throw (SQLException)(((Throwable) (e)));
            }
            catch(InvocationTargetException e){          
                throw (SQLException)((InvocationTargetException) (e)).getCause();
            }
        }

        public MySQLConnectionInvocationHandler(Connection connection) {
            _connection = connection;
        }
    }


    public static final int SYBASE = 1;
    public static final int MYSQL = 2;
    public static final int SQLSERVER = 3;
    public static final int DB2 = 4;
    public static final int ORACLE = 5;
    public static final int POINTBASE = 6;
    public static final int SOLID = 7;
    public static final int HSQL = 8;
    public static final int POSTGRESQL = 9;
    private static Class _MYSQL_CONNECTION_PROXY_CLASS = Proxy.getProxyClass(DBUtil.class.getClassLoader(), new Class[] {
        java.sql.Connection.class
    });
    private static Class _MYSQL_STATEMENT_PROXY_CLASS = Proxy.getProxyClass(DBUtil.class.getClassLoader(), new Class[] {
        java.sql.Statement.class
    });

    public DBUtil() {
    }

    public static HashMap loadMetaDataColumnNumberMap(ResultSetMetaData metaData) throws SQLException {
        HashMap columnNumberMap = new HashMap(metaData.getColumnCount() * 2 + 2);
        for(int i = 1; i < metaData.getColumnCount() + 1; i++)
            columnNumberMap.put(((Object) (metaData.getColumnName(i).toUpperCase())), ((Object) (new Integer(i))));

        return columnNumberMap;
    }


    public static String asDBString(String str) {
        return asSybaseDBString(str);
    }

    public static String asSybaseDBString(String str) {
        StringBuffer s = new StringBuffer("'");
        if(str.indexOf('\'') != -1) {
            for(int i = 0; i < str.length(); i++) {
                char c;
                if((c = str.charAt(i)) == '\'')
                    s.append("''");
                else
                    s.append(c);
            }

        } else {
            s.append(str);
        }
        s.append('\'');
        return s.toString();
    }

    public static String asDBString(String str, Connection conn) {
        return asSybaseDBString(str);
    }

    public static int getDatabaseType(Connection conn) {
        if(conn instanceof DelegatingConnection)
            conn = ((DelegatingConnection)conn).getInnermostDelegate();
        String databaseURL = ((Object) (conn)).getClass().getName();
        if(_MYSQL_CONNECTION_PROXY_CLASS.isAssignableFrom(((Object) (conn)).getClass()) || databaseURL.indexOf("mysql") != -1)
            return 2;
        if(databaseURL.startsWith("com.inet.tds"))
            return 3;
        if(databaseURL.startsWith("com.microsoft"))
            return 3;
        if(databaseURL.startsWith("oracle"))
            return 5;
        if(databaseURL.indexOf("ibm.db2") != -1)
            return 4;
        if(databaseURL.indexOf("com.pointbase") != -1)
            return 6;
        if(databaseURL.startsWith("solid.jdbc"))
            return 7;
        if(databaseURL.startsWith("org.hsqldb"))
            return 8;
        return !databaseURL.startsWith("org.postgresql") ? 1 : 9;
    }

    public static Connection getInnermostDelegate(Connection conn) {
        if(conn instanceof DelegatingConnection)
            conn = ((DelegatingConnection)conn).getInnermostDelegate();
        return conn;
    }

    static Connection wrapConnectionIfNeeded(Connection connection) {
        InvocationHandler handler;
        if(2 == getDatabaseType(connection)){
            try{
            handler = ((InvocationHandler) (new MySQLConnectionInvocationHandler(connection)));
            return (Connection)_MYSQL_CONNECTION_PROXY_CLASS.getConstructor(new Class[] {
                java.lang.reflect.InvocationHandler.class
            }).newInstance(new Object[] {
                handler
            });
            }catch(Exception e){            
                ;
            }
        }
        return connection;
    }


}
