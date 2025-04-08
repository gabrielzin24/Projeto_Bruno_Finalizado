package view;

import java.awt.HeadlessException;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import model.Category;
import model.Transaction;
import service.CategoryService;
import service.TransactionService;
import src.SistemaGestaoFinanceira;
import src.ButtonEditor;
import src.ButtonRenderer;
import util.DateUtils;

public final class TransactionFormView extends javax.swing.JPanel {
    private final SistemaGestaoFinanceira app;

    public TransactionFormView(SistemaGestaoFinanceira app) {
        initComponents();
        this.app = app;

        this.loadCategoriesToTable();
        this.loadTransactionsToTable();
        this.loadCategoriesToCombo();
        
        radioButtonExpense.addActionListener(e -> loadTransactionsToTable());
        radioButtonIncome.addActionListener(e -> loadTransactionsToTable());
        fieldDataFim.addActionListener(e -> loadTransactionsToTable());
        comboCategoryFilter.addActionListener(e -> loadTransactionsToTable());
    }
    
    public void loadCategoriesToCombo() {
        comboCategory.removeAllItems();
        comboCategoryFilter.removeAllItems();

        comboCategoryFilter.addItem("Default");

        List<Category> categoryList = new CategoryService().getAllCategories();
        for (Category category : categoryList) {
            String name = category.getName();
            comboCategory.addItem(name);
            comboCategoryFilter.addItem(name);
        }        
    }
    
    public void loadCategoriesToTable() {
        List<Category> categoryList = new CategoryService().getAllCategories();
        DefaultTableModel model = (DefaultTableModel) tableCategories.getModel();
        model.setRowCount(0);
       
        for (Category category : categoryList) {
            model.addRow(new Object[]{
                category.getId(),
                category.getName(),
                "Salvar",
                "Apagar"
            });
        }
        
        CategoryService cs = new CategoryService();
        TransactionService ts = new TransactionService();
        
        tableCategories.getColumn("Salvar").setCellRenderer(new ButtonRenderer("Salvar"));
        tableCategories.getColumn("Apagar").setCellRenderer(new ButtonRenderer("Apagar"));

        tableCategories.getColumn("Salvar").setCellEditor(
            new ButtonEditor(new JCheckBox(), tableCategories, (tbl, rowIdx) -> {
                int id = (int) tbl.getValueAt(rowIdx, 0);
                String newName = (String) tbl.getValueAt(rowIdx, 1);
                cs.updateCategory(id, newName);

                Transaction t = ts.getTransactionById(id);
                if (t != null){
                    t.setCategory(new Category(id, newName));
                }
                
                JOptionPane.showMessageDialog(tbl, "Categoria atualizada!");
                this.loadTransactionsToTable();
//                this.loadCategoriesToCombo();
            })
        );

        tableCategories.getColumn("Apagar").setCellEditor(
            new ButtonEditor(new JCheckBox(), tableCategories, (tbl, rowIdx) -> {
                int id = (int) tbl.getValueAt(rowIdx, 0);
                cs.removeCategory(id);
                ((DefaultTableModel) tbl.getModel()).removeRow(rowIdx);
            })
        );

        loadCategoriesToCombo();
        loadTransactionsToTable();
    }
    
    public void loadTransactionsToTable() {
        List<Transaction> transactionList = new TransactionService()
            .getListTransactionsByUserId(this.app.getUser().getId());

        DefaultTableModel model = (DefaultTableModel) tableTransactions.getModel();
        model.setRowCount(0);

        boolean filterExpense = radioButtonExpense.isSelected();
        boolean filterIncome = radioButtonIncome.isSelected();

        String categoryFilter = (String) comboCategoryFilter.getSelectedItem();
        
        LocalDate startDateFilter;
        LocalDate endDateFilter;

        try {
            startDateFilter = parseDateFromField(fieldDataInicio);
            endDateFilter = parseDateFromField(fieldDataFim);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Data inválida no filtro.");
            return;
        }

        fillTableWithTransactions(
            transactionList, model,
            filterExpense, filterIncome, 
            categoryFilter, startDateFilter, endDateFilter
        );
        
        setupTableButtons();
    }
    
    private LocalDate parseDateFromField(JTextField field) throws Exception {
        String text = field.getText().trim();
        return text.isEmpty() ? null : DateUtils.parseDate(text);
    }

    private void fillTableWithTransactions(
        List<Transaction> transactions, DefaultTableModel model,
        boolean filterExpense, boolean filterIncome, String categoryfilter,
        LocalDate startDateFilter, LocalDate endDateFilter
    ){
        for (Transaction t : transactions) {
            if (shouldAddTransaction(t, filterExpense, filterIncome, categoryfilter, startDateFilter, endDateFilter)) {
                model.addRow(new Object[]{
                    t.getId(),
                    t.getValue(),
                    t.getDate(),
                    (t.getType() == Transaction.DESPESA) ? "DESPESA" : "RECEITA",
                    t.getCategory().getName(),
                    "Salvar",
                    "Apagar"
                });
            }
        }
    }

    private boolean shouldAddTransaction(
        Transaction t, boolean filterExpense, boolean filterIncome,
        String categoryFilter, LocalDate start, LocalDate end
    ){
        boolean matchesType = (!filterExpense && !filterIncome)
            || (filterExpense && t.getType() == Transaction.DESPESA)
            || (filterIncome && t.getType() == Transaction.RECEITA);

        boolean matchesCategory = 
            categoryFilter.equals("Default") ||
            t.getCategory().getName().equals(categoryFilter);
                
        boolean matchesDate = true;
        if (start != null && end != null) {
            matchesDate = DateUtils.isBetween(t.getDate(), start, end);
        }

        return matchesType && matchesDate && matchesCategory;
    }

    private void setupTableButtons() {
        TransactionService ts = new TransactionService();

        tableTransactions.getColumn("Salvar").setCellRenderer(new ButtonRenderer("Salvar"));
        tableTransactions.getColumn("Salvar").setCellEditor(
            new ButtonEditor(new JCheckBox(), tableTransactions, (tbl, rowIdx) -> onSaveTransaction(tbl, rowIdx, ts))
        );

        tableTransactions.getColumn("Apagar").setCellRenderer(new ButtonRenderer("Apagar"));
        tableTransactions.getColumn("Apagar").setCellEditor(
            new ButtonEditor(new JCheckBox(), tableTransactions, (tbl, rowIdx) -> onDeleteTransaction(tbl, rowIdx, ts))
        );
    }

    private void onSaveTransaction(JTable tbl, int rowIdx, TransactionService ts) {
        try {
            int id = (int) tbl.getValueAt(rowIdx, 0);
            BigDecimal value = new BigDecimal("" + ((Double) tbl.getValueAt(rowIdx, 1)));
            String typeStr = (String) tbl.getValueAt(rowIdx, 3);
            int type = typeStr.equals("DESPESA") ? Transaction.DESPESA : Transaction.RECEITA;

            Object dateObj = tbl.getValueAt(rowIdx, 2);
            LocalDate date = (dateObj instanceof LocalDate) ? (LocalDate) dateObj : DateUtils.parseDate(dateObj.toString());

            Category category = new CategoryService().getByName((String) tbl.getValueAt(rowIdx, 4));

            int userId = app.getUser().getId();
            Transaction transaction = new Transaction(id, userId, type, value, date, category, "");

            if (ts.updateById(id, transaction)) {
                JOptionPane.showMessageDialog(tbl, "Transação atualizada!");
            } else {
                JOptionPane.showMessageDialog(tbl, "Transação inexistente (Logo, não foi atualizada)!");
            }
        } catch (HeadlessException e) {
            JOptionPane.showMessageDialog(tbl, "Erro ao atualizar transação: " + e.getMessage());
        }
    }

    private void onDeleteTransaction(JTable tbl, int rowIdx, TransactionService ts) {
        int id = (int) tbl.getValueAt(rowIdx, 0);

        if (ts.deleteById(id)) {
            JOptionPane.showConfirmDialog(tbl, "Transação apagada com sucesso.");
        }

        ((DefaultTableModel) tbl.getModel()).removeRow(rowIdx);
    }


    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jTextField1 = new javax.swing.JTextField();
        jPanel1 = new javax.swing.JPanel();
        buttonSaveInsertCategory = new javax.swing.JButton();
        comboCategory = new javax.swing.JComboBox<>();
        fieldPrice = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        comboType = new javax.swing.JComboBox<>();
        jScrollPane3 = new javax.swing.JScrollPane();
        textAreaDescription = new javax.swing.JTextArea();
        fieldData = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tableCategories = new javax.swing.JTable();
        jPanel3 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        tableTransactions = new javax.swing.JTable();
        radioButtonExpense = new javax.swing.JRadioButton();
        radioButtonIncome = new javax.swing.JRadioButton();
        jButton1 = new javax.swing.JButton();
        fieldDataInicio = new javax.swing.JTextField();
        fieldDataFim = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        comboCategoryFilter = new javax.swing.JComboBox<>();

        jTextField1.setText("jTextField1");

        jPanel1.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));

        buttonSaveInsertCategory.setText("Salvar");
        buttonSaveInsertCategory.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonSaveInsertCategoryActionPerformed(evt);
            }
        });

        comboCategory.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        fieldPrice.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                fieldPriceActionPerformed(evt);
            }
        });

        jLabel1.setText("Inserir Transação");

        jLabel2.setText("Preço");

        comboType.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "DESPESA", "RECEITA" }));

        textAreaDescription.setColumns(20);
        textAreaDescription.setRows(5);
        jScrollPane3.setViewportView(textAreaDescription);

        fieldData.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                fieldDataActionPerformed(evt);
            }
        });

        jLabel3.setText("Data");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(17, 17, 17)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(comboCategory, javax.swing.GroupLayout.PREFERRED_SIZE, 123, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(comboType, javax.swing.GroupLayout.PREFERRED_SIZE, 89, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jLabel1)
                    .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 218, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addGap(7, 7, 7)
                        .addComponent(fieldData, javax.swing.GroupLayout.PREFERRED_SIZE, 83, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(jLabel2)
                                .addComponent(jLabel3)
                                .addComponent(buttonSaveInsertCategory, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addComponent(fieldPrice, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 84, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap(15, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(15, 15, 15)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(jLabel2))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(comboCategory, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(comboType, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(fieldPrice, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 111, Short.MAX_VALUE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(6, 6, 6)
                        .addComponent(jLabel3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(fieldData, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(buttonSaveInsertCategory, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(10, Short.MAX_VALUE))
        );

        jPanel2.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));

        tableCategories.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "ID", "Nome", "Salvar", "Apagar"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Integer.class, java.lang.String.class, java.lang.Object.class, java.lang.Object.class
            };
            boolean[] canEdit = new boolean [] {
                false, true, true, true
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane1.setViewportView(tableCategories);
        if (tableCategories.getColumnModel().getColumnCount() > 0) {
            tableCategories.getColumnModel().getColumn(0).setResizable(false);
            tableCategories.getColumnModel().getColumn(0).setPreferredWidth(1);
        }

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 291, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                .addContainerGap())
        );

        jPanel3.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));

        tableTransactions.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null}
            },
            new String [] {
                "ID", "Valor", "Data", "Tipo", "Categoria", "Salvar", "Apagar"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Integer.class, java.lang.Double.class, java.lang.Object.class, java.lang.String.class, java.lang.String.class, java.lang.Object.class, java.lang.Object.class
            };
            boolean[] canEdit = new boolean [] {
                false, true, true, false, false, true, true
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane2.setViewportView(tableTransactions);
        if (tableTransactions.getColumnModel().getColumnCount() > 0) {
            tableTransactions.getColumnModel().getColumn(0).setResizable(false);
            tableTransactions.getColumnModel().getColumn(0).setPreferredWidth(1);
            tableTransactions.getColumnModel().getColumn(1).setResizable(false);
        }

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 653, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 214, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        radioButtonExpense.setText("Saída");

        radioButtonIncome.setText("Entrada");

        jButton1.setText("Voltar");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        fieldDataFim.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                fieldDataFimActionPerformed(evt);
            }
        });

        jLabel4.setText("Entre os dias: ");

        jLabel5.setText("e");

        comboCategoryFilter.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        comboCategoryFilter.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                comboCategoryFilterActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(radioButtonExpense)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(radioButtonIncome)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel4)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(fieldDataInicio, javax.swing.GroupLayout.PREFERRED_SIZE, 89, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel5)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(fieldDataFim, javax.swing.GroupLayout.PREFERRED_SIZE, 89, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(comboCategoryFilter, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jButton1))
                    .addComponent(jPanel3, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(15, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(14, 14, 14)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(radioButtonExpense)
                    .addComponent(radioButtonIncome)
                    .addComponent(jButton1)
                    .addComponent(fieldDataInicio, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(fieldDataFim, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4)
                    .addComponent(jLabel5)
                    .addComponent(comboCategoryFilter, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(69, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void fieldPriceActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_fieldPriceActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_fieldPriceActionPerformed

    private void fieldDataActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_fieldDataActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_fieldDataActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        this.app.changePanel(new view.HomeView(this.app));
    }//GEN-LAST:event_jButton1ActionPerformed

    private void buttonSaveInsertCategoryActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonSaveInsertCategoryActionPerformed
        try {
            BigDecimal value = new BigDecimal(fieldPrice.getText());
            LocalDate date = DateUtils.parseDate(fieldData.getText());
            String description = textAreaDescription.getText();
            String typeStr = (String) comboType.getSelectedItem();
            int type = typeStr.equals("DESPESA") ? Transaction.DESPESA : Transaction.RECEITA;
            Category category = new CategoryService().getByName((String) comboCategory.getSelectedItem());
                
            if (category == null) {
                JOptionPane.showMessageDialog(this, "Categoria não encontrada!");
                return;
            }

            new TransactionService()
                .addTransaction(new Transaction(
                        this.app.getUser().getId(),
                        type, 
                        value,
                        date,
                        category, 
                        description
                    )
                );
                
            JOptionPane.showMessageDialog(this, "Transação inserida com sucesso!");
            loadTransactionsToTable();

            fieldPrice.setText("");
            fieldData.setText("");
            textAreaDescription.setText("");
            comboType.setSelectedIndex(0);
            comboCategory.setSelectedIndex(0);
        } catch (HeadlessException ex) {
            JOptionPane.showMessageDialog(this, "Erro ao inserir transação: " + ex.getMessage());
        }
    }//GEN-LAST:event_buttonSaveInsertCategoryActionPerformed

    private void fieldDataFimActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_fieldDataFimActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_fieldDataFimActionPerformed

    private void comboCategoryFilterActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_comboCategoryFilterActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_comboCategoryFilterActionPerformed
    

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton buttonSaveInsertCategory;
    private javax.swing.JComboBox<String> comboCategory;
    private javax.swing.JComboBox<String> comboCategoryFilter;
    private javax.swing.JComboBox<String> comboType;
    private javax.swing.JTextField fieldData;
    private javax.swing.JTextField fieldDataFim;
    private javax.swing.JTextField fieldDataInicio;
    private javax.swing.JTextField fieldPrice;
    private javax.swing.JButton jButton1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JRadioButton radioButtonExpense;
    private javax.swing.JRadioButton radioButtonIncome;
    private javax.swing.JTable tableCategories;
    private javax.swing.JTable tableTransactions;
    private javax.swing.JTextArea textAreaDescription;
    // End of variables declaration//GEN-END:variables
}
