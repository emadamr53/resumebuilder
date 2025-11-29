package utils;

import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Database manager for SQLite database operations
 * @author habib
 */
public class DatabaseManager {
    private static final String DB_URL = "jdbc:sqlite:resume_builder.db";
    private static final Logger logger = Logger.getLogger(DatabaseManager.class.getName());
    
    /**
     * Get database connection
     */
    public static Connection getConnection() throws SQLException {
        try {
            Class.forName("org.sqlite.JDBC");
            Connection conn = DriverManager.getConnection(DB_URL);
            return conn;
        } catch (ClassNotFoundException e) {
            logger.log(Level.SEVERE, "SQLite JDBC driver not found", e);
            throw new SQLException("Database driver not found", e);
        }
    }
    
    /**
     * Initialize database schema
     */
    public static void initializeDatabase() {
        try (Connection conn = getConnection()) {
            // Create users table
            String createUsersTable = """
                CREATE TABLE IF NOT EXISTS users (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    username TEXT UNIQUE NOT NULL,
                    email TEXT UNIQUE NOT NULL,
                    password TEXT NOT NULL,
                    full_name TEXT,
                    created_at DATETIME DEFAULT CURRENT_TIMESTAMP
                )
            """;
            
            // Create resumes table (updated with all fields)
            String createResumesTable = """
                CREATE TABLE IF NOT EXISTS resumes (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    user_id INTEGER NOT NULL,
                    name TEXT,
                    email TEXT,
                    phone TEXT,
                    address TEXT,
                    institution TEXT,
                    degree TEXT,
                    year TEXT,
                    job_title TEXT,
                    company TEXT,
                    duration TEXT,
                    description TEXT,
                    skills TEXT,
                    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
                    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP,
                    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
                )
            """;
            
            
            // Create education table
            String createEducationTable = """
                CREATE TABLE IF NOT EXISTS education (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    resume_id INTEGER NOT NULL,
                    institution TEXT,
                    degree TEXT,
                    field_of_study TEXT,
                    graduation_year TEXT,
                    gpa TEXT,
                    description TEXT,
                    FOREIGN KEY (resume_id) REFERENCES resumes(id) ON DELETE CASCADE
                )
            """;
            
            // Create experience table
            String createExperienceTable = """
                CREATE TABLE IF NOT EXISTS experience (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    resume_id INTEGER NOT NULL,
                    job_title TEXT,
                    company TEXT,
                    location TEXT,
                    start_date TEXT,
                    end_date TEXT,
                    is_current_job INTEGER DEFAULT 0,
                    description TEXT,
                    FOREIGN KEY (resume_id) REFERENCES resumes(id) ON DELETE CASCADE
                )
            """;
            
            try (Statement stmt = conn.createStatement()) {
                stmt.execute(createUsersTable);
                stmt.execute(createResumesTable);
                stmt.execute(createEducationTable);
                stmt.execute(createExperienceTable);
                logger.info("Database tables created");
            }
            
            // Add missing columns to existing resumes table (if they don't exist)
            addColumnIfNotExists(conn, "resumes", "institution", "TEXT");
            addColumnIfNotExists(conn, "resumes", "degree", "TEXT");
            addColumnIfNotExists(conn, "resumes", "year", "TEXT");
            addColumnIfNotExists(conn, "resumes", "job_title", "TEXT");
            addColumnIfNotExists(conn, "resumes", "company", "TEXT");
            addColumnIfNotExists(conn, "resumes", "duration", "TEXT");
            addColumnIfNotExists(conn, "resumes", "description", "TEXT");
            
            logger.info("Database initialized successfully");
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error initializing database", e);
        }
    }
    
    /**
     * Add column to table if it doesn't exist
     */
    private static void addColumnIfNotExists(Connection conn, String tableName, String columnName, String columnType) {
        try {
            // Check if column exists by trying to query it
            try (Statement stmt = conn.createStatement()) {
                stmt.executeQuery("SELECT " + columnName + " FROM " + tableName + " LIMIT 1");
            }
            // Column exists, do nothing
        } catch (SQLException e) {
            // Column doesn't exist, add it
            try (Statement stmt = conn.createStatement()) {
                stmt.execute("ALTER TABLE " + tableName + " ADD COLUMN " + columnName + " " + columnType);
                logger.info("Added column " + columnName + " to " + tableName);
            } catch (SQLException ex) {
                logger.log(Level.WARNING, "Error adding column " + columnName + ": " + ex.getMessage());
            }
        }
    }
    
    /**
     * Close connection
     */
    public static void closeConnection(Connection conn) {
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException e) {
                logger.log(Level.WARNING, "Error closing connection", e);
            }
        }
    }
}


