package model;

import java.math.BigDecimal;
import java.time.LocalDate;

public class Transaction {
    public static final int RECEITA = 0;
    public static final int DESPESA = 1; 

    private int id;
    private int userId;
    private int type;
    private BigDecimal value;
    private LocalDate date;
    private Category category;
    private String description; 
    
    public Transaction(
        int id,
        int userId,
        int type,
        BigDecimal value,
        LocalDate date,
        Category category,
        String description
    ) {
        this(userId, type, value, date, category, description);
        this.id = id;
    }
    
    public Transaction(
        int userId,
        int type,
        BigDecimal value,
        LocalDate date,
        Category category,
        String description
    ) {
        this.id = -1;
        this.userId = userId;
        
        if (type == RECEITA || type == DESPESA) {
            this.type = type;
        }
        this.value = value;
        this.date = date;
        this.category = category;
        this.description = description;
    }

    public int getId() {
        return id;
    }
    
    public int getUserId(){
        return this.userId;
    }


    public int getType() {
        return type;
    }

    public BigDecimal getValue() {
        return value;
    }

    public LocalDate getDate() {
        return date;
    }

    public Category getCategory() {
        return category;
    }

    public String getDescription() {
        return description;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setType(int type) {
        this.type = type;
    }

    public void setValue(BigDecimal value) {
        this.value = value;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString(){
        return category.getName() 
            + ": "
            + ((type == DESPESA) ? ("-" + value) : (value))
            + " R$";
    }
}