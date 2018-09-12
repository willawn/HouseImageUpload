package com.zushou365.houseimageupload.dbmanager;

import org.apache.commons.dbcp.ConnectionFactory;
import org.apache.commons.dbcp.PoolableConnectionFactory;
import org.apache.commons.dbcp.PoolingDriver;
import org.apache.commons.pool.impl.GenericObjectPool;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import java.util.StringTokenizer;




public class DBConnectionManager {
    private static class PooledConnectionFactory implements ConnectionFactory {

        private String _poolName;

        public Connection createConnection() throws SQLException {
            return DriverManager.getConnection(_poolName);
        }

        public PooledConnectionFactory(ConnectionFactory connectionFactory, String name, String validationQuery) {
            GenericObjectPool connectionPool = new GenericObjectPool(((org.apache.commons.pool.PoolableObjectFactory) (null)));
            connectionPool.setTestOnBorrow(true);
            connectionPool.setTestOnReturn(true);
            new PoolableConnectionFactory(connectionFactory, ((org.apache.commons.pool.ObjectPool) (connectionPool)), ((org.apache.commons.pool.KeyedObjectPoolFactory) (null)), validationQuery, false, true);
            try {
                Class.forName("org.apache.commons.dbcp.PoolingDriver");
            }
            catch(ClassNotFoundException e) {
                e.printStackTrace();
            }
            PoolingDriver driver = null;
            try {
                driver = (PoolingDriver)DriverManager.getDriver("jdbc:apache:commons:dbcp:");
            }
            catch(SQLException e) {
                e.printStackTrace();
            }
            PoolingDriver.setAccessToUnderlyingConnectionAllowed(true);
            driver.registerPool(name, ((org.apache.commons.pool.ObjectPool) (connectionPool)));
            _poolName = (new StringBuilder()).append("jdbc:apache:commons:dbcp:").append(name).toString();
        }
    }

    private static class NonPooledDBConnectionFactory implements ConnectionFactory {

        private String password;
        private String URL;
        private String user;
        private String characterEncoding;

        public synchronized Connection createConnection() throws SQLException {
            if(user == null)
                return DriverManager.getConnection(URL);
            if(characterEncoding == null) {
                return DriverManager.getConnection(URL, user, password);
            } else {
                Properties info = new Properties();
                info.put("user", ((Object) (user)));
                info.put("password", ((Object) (password)));
                info.put("charSet", ((Object) (characterEncoding)));
                info.put("characterEncoding", ((Object) (characterEncoding)));
                info.put("useUnicode", "TRUE");
                return DriverManager.getConnection(URL, info);
            }
        }

        public NonPooledDBConnectionFactory(String URL, String user, String password, String characterEncoding) {
            this.URL = URL;
            this.user = user;
            this.password = password;
            this.characterEncoding = characterEncoding;
        }
    }


    private static DBConnectionManager _instance;
    private HashMap<String, Object> _drivers;
    private HashMap<String, Object> _factories;
    private boolean _initialized;
    private Object _initSyncObject;


    public static synchronized DBConnectionManager getInstance() {
        if(_instance == null)
            _instance = new DBConnectionManager();
        return _instance;
    }

    private DBConnectionManager() {
        _drivers = new HashMap<String, Object>();
        _factories = new HashMap<String, Object>();
        _initialized = false;
        _initSyncObject = new Object();
    }

    public Connection getConnection(String name) throws SQLException {
        try{
        ConnectionFactory factory;
        synchronized(_initSyncObject) {
            if(!_initialized) {
                init();
                _initialized = true;
            }
        }
        
        if(!_factories.containsKey(name)) {            
            synchronized(_initSyncObject) {
                 init();
            }            
        }
        
        factory = (ConnectionFactory)_factories.get(((Object) (name)));
        if(factory == null)
            throw new SQLException((new StringBuilder()).append("Unable to open a connection to '").append(name).append("'. No database by that name. ").append("Check that you are using the correct name or configure a database with that name in RoboSuite Settings.").toString());
            
            return DBUtil.wrapConnectionIfNeeded(factory.createConnection());
        }catch(SQLException e){        
            throw new SQLException((new StringBuilder()).append("Unable to open a connection to '").append(name).append("'. ").append(e.getMessage()).toString());
        }
    }

    private void createFactories(Properties props) {
        Enumeration propNames = props.propertyNames();
        do {
            if(!propNames.hasMoreElements())
                break;
            String name = (String)propNames.nextElement();
            if(name.endsWith(".url")) {
                String factoryName = name.substring(0, name.lastIndexOf("."));
                if(!_factories.containsKey(factoryName)) {
                    String url = props.getProperty((new StringBuilder()).append(factoryName).append(".url").toString());
                    if(url != null) {
                        String user = props.getProperty((new StringBuilder()).append(factoryName).append(".user").toString());
                        String password = props.getProperty((new StringBuilder()).append(factoryName).append(".password").toString());
                        String characterEncoding = props.getProperty((new StringBuilder()).append(factoryName).append(".characterEncoding").toString(), ((String) (null)));
                        ConnectionFactory factory = ((ConnectionFactory) (new NonPooledDBConnectionFactory(url, user, password, characterEncoding)));
                        boolean connectionPooling = Boolean.valueOf(props.getProperty((new StringBuilder()).append(factoryName).append(".pooling").toString(), "false")).booleanValue();
                        if(url.trim().toLowerCase().startsWith("jdbc:pointbase"))
                            connectionPooling = false;
                        if(connectionPooling) {
                            String validationQuery = "select 1";
                            if(url.trim().toLowerCase().startsWith("jdbc:db2"))
                                validationQuery = "SELECT 1 FROM SYSIBM.SYSDUMMY1";
                            if(url.trim().toLowerCase().startsWith("jdbc:oracle"))
                                validationQuery = "SELECT 1 FROM DUAL";
                            factory = ((ConnectionFactory) (new PooledConnectionFactory(factory, factoryName, validationQuery)));
                        }
                        _factories.put(factoryName, factory);
                    }
                }
            }
        } while(true);
    }

    private void init() throws SQLException {
        Properties dbProps = new Properties();
        try {
        	URL dbPropsUrl = DBConnectionManager.class.getResource("db.properties");
            //String dbpropertiesfile = getDBPropertiesFileName();
            //java.io.InputStream is = ((java.io.InputStream) (new FileInputStream(dbpropertiesfile)));
            
            InputStream s = dbPropsUrl.openStream();
            dbProps.load(s);
            s.close();
        }
        catch(IOException e) {
        	System.out.println(e.toString());
            throw new SQLException((new StringBuilder()).append("Unable to read the db properties file. ").append(e.getMessage()).toString());
        }
        loadDrivers(dbProps);
        createFactories(dbProps);
    }

    private void loadDrivers(Properties props) throws SQLException {
        String driverClasses = props.getProperty("drivers");
        StringTokenizer st = new StringTokenizer(driverClasses);
        LinkedList driverClassNames = new LinkedList();
        for(; st.hasMoreElements(); driverClassNames.add(((Object) (st.nextToken().trim()))));
        String sqlDriverName = "com.microsoft.sqlserver.jdbc.SQLServerDriver";
        if(driverClassNames.contains(((Object) (sqlDriverName)))) {
            driverClassNames.remove(((Object) (sqlDriverName)));
            driverClassNames.addFirst(((Object) (sqlDriverName)));
        }
        for(Iterator i$ = driverClassNames.iterator(); i$.hasNext();) {
            String driverClassName = (String)i$.next();
            try {
                if(!_drivers.containsKey(driverClassName)) {
                    Driver driver = (Driver)Class.forName(driverClassName).newInstance();
                    DriverManager.registerDriver(driver);
                    _drivers.put(driverClassName, driver);
                }
            }
            catch(Exception e) {
                throw new SQLException((new StringBuilder()).append("Unable to register the database driver ").append(driverClassName).append(". You need to install a driver for the database. ").append(e.getMessage()).toString());
            }
        }

    }
}
