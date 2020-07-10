package me.naptie.bukkit.rank.tools;

import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Created by SimplyRin on 2018/08/14.
 * <p>
 * Copyright (c) 2018 SimplyRin
 * <p>
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * <p>
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * <p>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
public class MySQL {

    private Connection connection;

    private String username, password, address, database, table, timezone;
    private boolean useSSL;

    public MySQL(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public MySQL(String username, String password, String address, String database, String table, String timezone, boolean useSSL) {
        this.username = username;
        this.password = password;
        this.address = address;
        this.database = database;
        this.table = table;
        this.timezone = timezone;
        this.useSSL = useSSL;
    }

    public MySQL setAddress(String address) {
        this.address = address;
        return this;
    }

    public MySQL setDatabase(String database) {
        this.database = database;
        return this;
    }

    public MySQL setTable(String table) {
        this.table = table;
        return this;
    }

    public MySQL setTimezone(String timezone) {
        this.timezone = timezone;
        return this;
    }

    public MySQL setUseSSL(boolean useSSL) {
        this.useSSL = useSSL;
        return this;
    }

    public Editor connect() throws SQLException {
        this.connection = DriverManager.getConnection("jdbc:mysql://" + this.address + "/" + this.database + "?useSSL=" + this.useSSL + "&serverTimezone=" + this.timezone, this.username, this.password);
        Statement statement = this.connection.createStatement();
        return new Editor(statement, this.table);
    }

    public void disconnect() {
        try {
            this.connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Editor reconnect() throws SQLException {
        this.disconnect();
        return this.connect();
    }

    public class Editor {

        private Statement statement;
        private String table;

        public Editor(Statement statement, String table) {
            this.statement = statement;
            this.table = table;
            try {
                this.statement.executeUpdate("create table if not exists " + this.table + " (identifier varchar(4098), value varchar(4098)) charset=utf8;");
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        public MySQL getMySQL() {
            return MySQL.this;
        }

        public boolean set(String key, List<String> list) {
            if (list.size() == 0) {
                return this.set(key, "[]");
            }
            StringBuilder object = new StringBuilder();
            for (String content : list) {
                object.append(content).append(",&%$%&,");
            }
            object = new StringBuilder(object.substring(0, object.length() - ",&%$%&,".length()));
            return this.set(key, object.toString());
        }

        public boolean set(String key, String object) {
            int result;

            if (object == null) {
                try {
                    this.statement.executeUpdate("delete from " + this.table + " where identifier = '" + key + "';");
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }

            try {
                result = this.statement.executeUpdate("update " + this.table + " set value = '" + object + "' where identifier ='" + key + "'");
            } catch (SQLException e) {
                return false;
            }

            if (result == 0) {
                try {
                    result = this.statement.executeUpdate("insert into " + this.table + " values ('" + key + "', '" + object + "');");
                } catch (SQLException e) {
                    e.printStackTrace();
                    return false;
                }
                return result == 1;
            }

            return false;
        }

        public boolean contains(String key) {
            ResultSet resultSet;
            try {
                resultSet = this.statement.executeQuery("select * from " + this.table + ";");
                while (resultSet.next()) {
                    if (resultSet.getString("identifier").equals(key)) {
                        String value = resultSet.getString("value");
                        return !value.equals("null");
                    }
                }
            } catch (SQLException ignored) {
            }
            return false;
        }

        public String get(String key) {
            ResultSet resultSet;
            try {
                resultSet = this.statement.executeQuery("select * from " + this.table + ";");
                while (resultSet.next()) {
                    if (resultSet.getString("identifier").equals(key)) {
                        String value = resultSet.getString("value");
                        if (value.equals("null")) {
                            return null;
                        }
                        return resultSet.getString("value");
                    }
                }
            } catch (SQLException ignored) {
            }
            return null;
        }

        public List<String> getAllKeys() {
            try {
                ResultSet resultSet = this.statement.executeQuery("select identifier from " + this.table + ";");
                List<String> list = new ArrayList<>();
                while (resultSet.next()) {
                    list.add(resultSet.getString(1));
                }
                return list;
            } catch (SQLException e) {
            }
            return null;
        }

        public List<String> getList(String key) {
            String value = this.get(key);
            if (value == null || value.equals("[]")) {
                return new ArrayList<>();
            }
            String[] result = value.split(Pattern.quote(",&%$%&,"));
            return new ArrayList<>(Arrays.asList(result));
        }

    }

}
