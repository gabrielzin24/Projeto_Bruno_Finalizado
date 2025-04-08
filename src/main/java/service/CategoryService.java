package service;

import model.Category;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;

public class CategoryService {
    private static Map<Integer, Category> categories = new HashMap<>();
    private static int nextId = 1;

    public Category addCategory(String name) {
        // ja existe.
        Category c = getByName(name);
        if (c != null) return c;
        
        // nao existe adiciona
        Category category = new Category(nextId, name);
        categories.put(category.getId(), category);
        nextId++;
        
        return category;
    }
    
    public Category getById(int id) {
        return categories.get(id);
    }

    public List<Category> getAllCategories() {
        return new ArrayList<>(categories.values());
    }

    public boolean removeCategory(int id) {
        return categories.remove(id) != null;
    }

    public boolean updateCategory(int id, String newName) {
        Category category = categories.get(id);
        if (category != null) {
            category.setName(newName);
            return true;
        }
        return false;
    }
    
    public Category getByName(String name) {
        for (Category category : categories.values()) {
            if (category.getName().equalsIgnoreCase(name)) {
                return category;
            }
        }
        return null;
    }

}
