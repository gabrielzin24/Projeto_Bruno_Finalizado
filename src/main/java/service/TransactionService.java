package service;

import java.math.BigDecimal;
import model.Transaction;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import model.Category;

public class TransactionService {
    private static final Map<Integer, Transaction> transactions = new HashMap<>();
    private static int nextId = 1;

    public void addTransaction(Transaction t) {
        if (t.getId() == -1) {
            t.setId(nextId++);
        } else {
            if (t.getId() >= nextId) {
                nextId = t.getId() + 1;
            }
        }
        transactions.put(t.getId(), t);
    }

    private List<Transaction> getTransactionsByFilter(Predicate<Transaction> filter) {
        return transactions
            .values()
            .stream()
            .filter(filter)
            .collect(Collectors.toList());
    }
    
    public List<Transaction> getListTransactionsByUserId(int userId) {
        return getTransactionsByFilter(t -> t.getUserId() == userId);
    }
    
    public List<Transaction> getListTransactionsByType(int type, int userId) {
        if (type != Transaction.DESPESA && type != Transaction.RECEITA) {
            return new ArrayList<>();
        }
        
        return getTransactionsByFilter(t -> ((t.getType() == type) && (t.getUserId() == userId)));
    }

    public List<Transaction> getListTransactionsByCategory(Category c, int userId) {
        if (new CategoryService().getById(c.getId()) == null) {
            return new ArrayList<>();
        }
        
        return getTransactionsByFilter(t -> ((t.getCategory().getId() == c.getId()) && (t.getUserId() == userId)));
    }
    
    public List<Transaction> getLatestTransactions(int limit, int userId) {
        List<Transaction> allTransactions = getListTransactionsByUserId(userId);
        int fromIndex = Math.max(0, allTransactions.size() - limit);
        return allTransactions.subList(fromIndex, allTransactions.size());
    }
   
    public Transaction getTransactionById(int id){
        return this.transactions.get(id);
    }
    
    public BigDecimal getTotalExpenses(int userId) {
        BigDecimal total = new BigDecimal("0.0");
        for (Transaction t : transactions.values()) {
            if (t.getUserId() == userId && t.getType() == Transaction.DESPESA) {
                total = total.add(t.getValue());
            }
        }
        return total;
    }
    
     public BigDecimal getTotalIncome(int userId) {
        BigDecimal total = new BigDecimal("0.0");
        for (Transaction t : transactions.values()) {
            if (t.getUserId() == userId && t.getType() == Transaction.RECEITA) {
                total = total.add(t.getValue());
            }
        }
        return total;
    }

    public boolean updateById(int id, Transaction newTransaction){
        if (!transactions.containsKey(id)) return false;
        newTransaction.setId(id);
        transactions.put(id, newTransaction);
        return true;
    }
    
    public boolean deleteById(int id){
        return transactions.remove(id) != null;
    }

}