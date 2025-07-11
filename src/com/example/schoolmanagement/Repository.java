package com.example.schoolmanagement;// com.example.schoolmanagement.Repository.java
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Repository<T> implements Serializable {
    private List<T> items;

    public Repository() {
        items = new ArrayList<>();
    }

    public void add(T item) {
        items.add(item);
        System.out.println(item.getClass().getSimpleName() + " added to the repository.");
    }

    public void remove(T item) {
        if (items.remove(item)) {
            System.out.println(item.getClass().getSimpleName() + " removed from the repository.");
        } else {
            System.out.println("Error: " + item.getClass().getSimpleName() + " not found in the repository.");
        }
    }

    public List<T> getAll() {
        return items;
    }
}
