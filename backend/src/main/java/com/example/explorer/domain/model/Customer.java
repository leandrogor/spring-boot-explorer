package com.example.explorer.domain.model;

public class Customer {
    private Long id;
    private String name;
    private String email;
    private CustomerType type;
    private String userId;

    public Customer() {
    }

    public Customer(Long id, String name, String email, CustomerType type, String userId) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.type = type;
        this.userId = userId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public CustomerType getType() {
        return type;
    }

    public void setType(CustomerType type) {
        this.type = type;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
