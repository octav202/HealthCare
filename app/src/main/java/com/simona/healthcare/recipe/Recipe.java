package com.simona.healthcare.recipe;

public class Recipe {

    private int id;
    private String name;
    private String time;
    private String description;
    private String imagePath;

    public Recipe(int id, String name, String time, String description, String imagePath) {
        this.id = id;
        this.name = name;
        this.time = time;
        this.description = description;
        this.imagePath = imagePath;
    }

    public Recipe() {
        this.id = 0;
        this.name = "";
        this.time = "";
        this.description = "";
        this.imagePath = "";
    }

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

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    @Override
    public String toString() {
        return "Recipe{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", time='" + time + '\'' +
                ", description='" + description + '\'' +
                ", imagePath='" + imagePath + '\'' +
                '}';
    }
}
