package DSA_Interview_Questions.Fivetran;



import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DatabaseConnector {
    
    // JDBC Driver demonstration
    static {
        try {
            // Explicitly load PostgreSQL JDBC driver
            // Modern JDBC 4.0+ drivers auto-register, but this demonstrates driver familiarity
            Class.forName("org.postgresql.Driver");
            System.out.println("PostgreSQL JDBC Driver registered successfully");
        } catch (ClassNotFoundException e) {
            System.err.println("PostgreSQL JDBC Driver not found!");
            e.printStackTrace();
        }
    }
    public static void main(String[] args) {
        String url = "jdbc:postgresql://localhost:5432/inv_ord_system";
        String username = "postgres";
        String password = "root";

        try {
            Connection connection = DriverManager.getConnection(url, username, password);
            System.out.println("Connected to database successfully");
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("select * from orders");
            while (resultSet.next()) {
                System.out.println("entry from orders " + resultSet.getString(1));
            }

            //Explore Schema Details:
            DatabaseMetaData databaseMetaData = connection.getMetaData();
            ResultSet tables = databaseMetaData.getTables(null, null, "%", null);
//            while (tables.next()) {
//                System.out.println("Table: " + tables.getString("TABLE_NAME"));
//            }
            ResultSetMetaData tablesMetaData = tables.getMetaData();
            int tableColumnCount = tablesMetaData.getColumnCount();

            while (tables.next()) {
                if (!tables.getString("TABLE_SCHEM").equalsIgnoreCase("public")) {
                    continue;
                }
                System.out.print("Complete Row: ");
                for (int i = 1; i <= tableColumnCount; i++) {
                    System.out.print(tablesMetaData.getColumnName(i) + "=" + tables.getString(i));
                    if (i < tableColumnCount) {
                        System.out.print(", ");
                    }
                }
                System.out.println();
            }

            // Extract products from database and convert to Java objects
            List<DSA_Interview_Questions.Fivetran.Product> products = extractProductsFromDatabase(connection);
            System.out.println("\n=== Products List ===");
            products.forEach(System.out::println);

            // Build internal schema representation from information_schema
            Map<String, List<ColumnInfo>> schemaRepresentation = buildSchemaRepresentation(connection);
            System.out.println("\nTotal tables in schema: " + schemaRepresentation.size());

            // Ingest data and submit to internal API
            ingestDataAndSubmitToAPI(connection);
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Extracts product data from the database and converts it into Product objects.
     * 
     * @param connection Active database connection
     * @return List of Product objects from the products table
     * @throws SQLException if database access error occurs
     */
    public static List<DSA_Interview_Questions.Fivetran.Product> extractProductsFromDatabase(Connection connection) throws SQLException {
        List<DSA_Interview_Questions.Fivetran.Product> products = new ArrayList<>();
        String query = "SELECT * FROM products";
       // String query = "SELECT id, name, description, price, stock_quantity FROM products";

        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {
            
            System.out.println("\n=== Extracting Products from Database ===");
            
            while (resultSet.next()) {
                // Extract data from ResultSet
                String id = resultSet.getString("id");
                String name = resultSet.getString("name");
                String description = resultSet.getString("description");
                double price = resultSet.getDouble("price");
                int stockQuantity = resultSet.getInt("stock_quantity");
                LocalDateTime createdAt = resultSet.getTimestamp("created_at").toLocalDateTime();
                LocalDateTime updatedAt = resultSet.getTimestamp("updated_at").toLocalDateTime();
                
                // Create Product object
                Product product = new Product(id, name, description, price, createdAt, updatedAt, stockQuantity);
                products.add(product);
                
                // Print extracted product
                //System.out.println("Extracted Product: " + product);
            }
            
            System.out.println("Total products extracted: " + products.size());
        }
        
        return products;
    }

    /**
     * Query information schema to build an internal representation of database structure.
     * This creates a structured map of tables and their columns.
     * 
     * @param connection Active database connection
     * @return Map of table names to their column information
     * @throws SQLException if database access error occurs
     */
    public static Map<String, List<ColumnInfo>> buildSchemaRepresentation(Connection connection) throws SQLException {
        Map<String, List<ColumnInfo>> schemaMap = new HashMap<>();
        
        System.out.println("\n=== Building Internal Schema Representation ===");
        
        // Query information_schema to get table and column details
        String query = "SELECT table_name, column_name, data_type, is_nullable, column_default " +
                      "FROM information_schema.columns " +
                      "WHERE table_schema = 'public' " +
                      "ORDER BY table_name, ordinal_position";
        
        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {
            
            while (resultSet.next()) {
                String tableName = resultSet.getString("table_name");
                String columnName = resultSet.getString("column_name");
                String dataType = resultSet.getString("data_type");
                String isNullable = resultSet.getString("is_nullable");
                String columnDefault = resultSet.getString("column_default");
                
                ColumnInfo columnInfo = new ColumnInfo(columnName, dataType, isNullable, columnDefault);
                
                schemaMap.computeIfAbsent(tableName, k -> new ArrayList<>()).add(columnInfo);
            }
        }
        
        // Print the internal representation
        System.out.println("\nSchema Representation:");
        schemaMap.forEach((tableName, columns) -> {
            System.out.println("\nTable: " + tableName);
            columns.forEach(col -> System.out.println("  - " + col));
        });
        
        return schemaMap;
    }

    /**
     * Ingest data and submit it to an internal API.
     * This demonstrates taking extracted data and submitting it to your application's service layer.
     * 
     * @param connection Active database connection
     * @throws SQLException if database access error occurs
     */
    public static void ingestDataAndSubmitToAPI(Connection connection) throws SQLException {
        System.out.println("\n=== Ingesting Data and Submitting to Internal API ===");
        
        // Extract products from database
        List<Product> products = extractProductsFromDatabase(connection);
        
        // Simulate submitting to internal API
        // In a real scenario, you would inject a service and call its methods
        System.out.println("\nSubmitting " + products.size() + " products to internal API...");
        
        for (Product product : products) {
            // Simulate API call
            submitProductToInternalAPI(product);
        }
        
        System.out.println("Data ingestion complete!");
    }

    /**
     * Simulates submitting a product to an internal API.
     * In a real application, this would call your InventoryService or similar.
     * 
     * @param product Product to submit
     */
    private static void submitProductToInternalAPI(Product product) {
        // This simulates what would happen in a real scenario:
        // Example: inventoryService.createProduct(product);
        // or: restTemplate.postForObject("http://internal-api/products", product, Product.class);
        
        System.out.println("API Call: POST /api/products - " + product + " ");
        
        // Simulate processing delay
        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    /**
     * Inner class to represent column information from information_schema.
     * This is part of building an internal representation of the database schema.
     */

    static class ColumnInfo {
        // Getters
        private final String name;
        private final String dataType;
        private final String isNullable;
        private final String defaultValue;

        public ColumnInfo(String name, String dataType, String isNullable, String defaultValue) {
            this.name = name;
            this.dataType = dataType;
            this.isNullable = isNullable;
            this.defaultValue = defaultValue;
        }

        @Override
        public String toString() {
            return String.format("Column{name='%s', type='%s', nullable=%s, default='%s'}",
                               name, dataType, isNullable, defaultValue);
        }

    }
}
