package com.ppxytest.miniodemo.model;

public class UserMinio {
    private int id;  // 这个字段通常由数据库自动设置
    private String name;
    private int age;
    private String email;

    // 修改后的构造函数，不包含id字段
    public UserMinio(String name, int age, String email) {
        this.name = name;
        this.age = age;
        this.email = email;
    }

    // Getter 和 Setter 方法
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}