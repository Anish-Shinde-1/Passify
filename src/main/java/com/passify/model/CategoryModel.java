package com.passify.model;

public class CategoryModel {
    private int categoryId; // category_id
    private String categoryName; // category_name
    private int userId; // user_id

    // Constructor
    public CategoryModel(int categoryId, String categoryName, int userId) {
        this.categoryId = categoryId;
        this.categoryName = categoryName;
        this.userId = userId;
    }

    // Default constructor
    public CategoryModel() {
    }

    // Getters and Setters
    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    @Override
    public String toString() {
        return "CategoryModel{" +
                "categoryId=" + categoryId +
                ", categoryName='" + categoryName + '\'' +
                ", userId=" + userId +
                '}';
    }
}
