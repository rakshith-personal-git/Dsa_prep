package DSA_Interview_Questions.Fivetran;



import java.time.LocalDateTime;

/**
 * Domain Entity: Product
 * 
 * Represents a product in the inventory system.
 * This is an immutable value object after creation to ensure thread-safety.
 * Stock quantity is managed separately through thread-safe operations.
 */

public class Product {
    private final String id;
    private final String name;
    private final String description;
    private final double price;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;
    
    // Stock quantity - mutable, must be protected by locks in repository
    private volatile int stockQuantity;
    
    public Product(String id, String name, String description, double price, LocalDateTime createdAt, LocalDateTime updatedAt, int stockQuantity) {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("Product ID cannot be null or empty");
        }
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Product name cannot be null or empty");
        }
        if (price < 0) {
            throw new IllegalArgumentException("Product price cannot be negative");
        }
        if (stockQuantity < 0) {
            throw new IllegalArgumentException("Stock quantity cannot be negative");
        }
        
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.stockQuantity = stockQuantity;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
    
    /**
     * Creates a copy with updated stock quantity.
     * Used for thread-safe updates without modifying the original object.
     */
    public Product withStockQuantity(int newStockQuantity) {
        if (newStockQuantity < 0) {
            throw new IllegalArgumentException("Stock quantity cannot be negative");
        }
        return new Product(this.id, this.name, this.description, this.price, createdAt, updatedAt, newStockQuantity);
    }
    
    /**
     * Thread-safe getter for stock quantity.
     * Uses volatile to ensure visibility across threads.
     * Note: @Getter annotation provides the getter, but we keep this comment for documentation.
     */
    
    /**
     * Package-private setter for stock quantity.
     * Only called from repository with proper lock protection.
     */
    void setStockQuantity(int stockQuantity) {
        if (stockQuantity < 0) {
            throw new IllegalArgumentException("Stock quantity cannot be negative");
        }
        this.stockQuantity = stockQuantity;
    }
}