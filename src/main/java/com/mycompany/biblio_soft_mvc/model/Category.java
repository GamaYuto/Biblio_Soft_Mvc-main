package com.mycompany.biblio_soft_mvc.model;

/**
 * Representa una categoria de clasificacion para los libros.
 */
public class Category {
    private int idCategory;
    private String name;
    private String description;

    public Category() {}

    public Category(int idCategory, String name, String description) {
        this.idCategory = idCategory;
        this.name = name;
        this.description = description;
    }

    public int getIdCategory() {
        return idCategory;
    }

    public void setIdCategory(int idCategory) {
        this.idCategory = idCategory;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
