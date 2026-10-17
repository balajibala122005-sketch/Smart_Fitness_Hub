package com.example.gymhub.dto;

import com.example.gymhub.entity.User;

public class UserDto {

    private Long id;
    private String name;
    private String email;
    private String role;
    private Integer age;
    private Double height;
    private Double weight;
    private String goal;

    public UserDto() {
    }

    public UserDto(Long id, String name, String email, String role, Integer age, Double height, Double weight, String goal) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.role = role;
        this.age = age;
        this.height = height;
        this.weight = weight;
        this.goal = goal;
    }

    public static UserDto fromEntity(User user) {
        if (user == null) {
            return null;
        }
        return new UserDto(
            user.getId(),
            user.getName(),
            user.getEmail(),
            user.getRole().name(),
            user.getAge(),
            user.getHeight(),
            user.getWeight(),
            user.getGoal()
        );
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

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public Double getHeight() {
        return height;
    }

    public void setHeight(Double height) {
        this.height = height;
    }

    public Double getWeight() {
        return weight;
    }

    public void setWeight(Double weight) {
        this.weight = weight;
    }

    public String getGoal() {
        return goal;
    }

    public void setGoal(String goal) {
        this.goal = goal;
    }
}
