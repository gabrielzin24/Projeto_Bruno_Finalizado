package view;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import javax.swing.ButtonGroup;
import javax.swing.DefaultListModel;
import javax.swing.JOptionPane;
import model.Category;
import model.Transaction;
import service.CategoryService;
import src.SistemaGestaoFinanceira;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.general.DefaultPieDataset;
import service.TransactionService;

public class HomeView extends javax.swing.JPanel {
    private final SistemaGestaoFinanceira app;
    private int uid; // facilitar

    public HomeView(SistemaGestaoFinanceira app) {
        initComponents();
        this.app = app;
        this.uid = app.getUser().getId();
        
        this.initDefaultCategories();
        this.initFinancialSummary();        
        this.initLatestTransactions();

        this.textBemVindo.setText("Bem Vindo, " + app.getUser().getName() + ".");
        
        radioButtonPorCategoria.setSelected(true);
        this.setupTransactionListRenderer();

        ButtonGroup toggleGroup = new ButtonGroup();
        toggleGroup.add(radioButtonPorTipo);
        toggleGroup.add(radioButtonPorCategoria);
        
        radioButtonPorTipo.addActionListener(e -> initFinancialSummary());
        radioButtonPorCategoria.addActionListener(e -> initFinancialSummary());
    }
    
    private void initDefaultCategories() {
        CategoryService categoryService = new CategoryService();
        categoryService.addCategory("Faculdade");
        categoryService.addCategory("Transporte");
        categoryService.addCategory("Alimentação");
    }

    private void initFinancialSummary() {
        TransactionService ts = new TransactionService();
        
        final double totalExpense = ts.getTotalExpenses(uid).doubleValue();
        final double totalIncome = ts.getTotalIncome(uid).doubleValue();

        if (radioButtonPorTipo.isSelected()){
            generatePizzaChart(totalIncome, totalExpense);
        } else {
            generateCategoriaPorcentagemChart();        
        }

        textGastoTotalValor.setText(totalExpense + " R$");
        textReceitaTotalValor.setText(totalIncome + " R$");
        textSaldoAtualValor.setText((totalIncome - totalExpense) + " R$");
    }

    private void generatePizzaChart(double receita, double despesa) {
        DefaultPieDataset dataset = new DefaultPieDataset();
        dataset.setValue("Receita", receita);
        dataset.setValue("Despesa", despesa);

        JFreeChart graficoPizza = ChartFactory.createPieChart(
                "Resumo Financeiro", dataset, true, true, false);

        ChartPanel chartPanel = new ChartPanel(graficoPizza);
        chartPanel.setPreferredSize(new java.awt.Dimension(400, 280));

        panelGraficoFinanceiro.removeAll();
        panelGraficoFinanceiro.setLayout(new java.awt.BorderLayout());
        panelGraficoFinanceiro.add(chartPanel, java.awt.BorderLayout.CENTER);
        panelGraficoFinanceiro.validate();
    }
    
    private void generateCategoriaPorcentagemChart() {
        TransactionService transactionService = new TransactionService();
        CategoryService categoryService = new CategoryService();
        DefaultPieDataset dataset = new DefaultPieDataset();

        BigDecimal totalDespesas = transactionService.getTotalExpenses(uid);

        for (Category category : categoryService.getAllCategories()) {
            List<Transaction> transacoes = transactionService.getListTransactionsByCategory(category, uid);

            BigDecimal totalCategoria = BigDecimal.ZERO;
            for (Transaction t : transacoes) {
                if (t.getType() == Transaction.DESPESA) {
                    totalCategoria = totalCategoria.add(t.getValue());
                }
            }

            if (totalCategoria.compareTo(BigDecimal.ZERO) > 0) {
                double porcentagem = totalCategoria.divide(totalDespesas, 4, RoundingMode.HALF_UP).doubleValue() * 100;
                dataset.setValue(category.getName(), porcentagem);
            }
        }

        JFreeChart graficoPizza = ChartFactory.createPieChart(
            "Despesas por Categoria (%)", dataset, true, true, false);

        ChartPanel chartPanel = new ChartPanel(graficoPizza);
        chartPanel.setPreferredSize(new java.awt.Dimension(400, 280));

        panelGraficoFinanceiro.removeAll();
        panelGraficoFinanceiro.setLayout(new java.awt.BorderLayout());
        panelGraficoFinanceiro.add(chartPanel, java.awt.BorderLayout.CENTER);
        panelGraficoFinanceiro.validate();
    }
    
    
    private void initLatestTransactions() {
        List<Transaction> latest = new TransactionService()
            .getLatestTransactions(5, uid);

        DefaultListModel<String> model = new DefaultListModel<>();

        for (Transaction t : latest) {
            model.addElement(t.toString());
        }

        if (latest.isEmpty()) {
            textLatestTransactions.setText("Nenhuma transação realizada.");
        } else {
            textLatestTransactions.setText("Últimas " + latest.size() + " transações:");
        }

        listLatestTransactions.setModel(model);
    }
    
    private void setupTransactionListRenderer() {
        listLatestTransactions.setCellRenderer(new javax.swing.DefaultListCellRenderer() {
            @Override
            public java.awt.Component getListCellRendererComponent(
                    javax.swing.JList<?> list, Object value, int index,
                    boolean isSelected, boolean cellHasFocus) {

                java.awt.Component c = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

                if (value instanceof String) {
                    String transactionText = (String) value;

                    if (transactionText.contains("-")) {
                        c.setBackground(new java.awt.Color(255, 204, 204)); // vermelho
                    } else {
                        c.setBackground(new java.awt.Color(204, 255, 204)); // verde
                    }

                    if (isSelected) {
                        c.setBackground(c.getBackground().darker());
                    }
                }

                return c;
            }
        });
    }


    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        textAppName = new javax.swing.JLabel();
        buttonExit = new javax.swing.JButton();
        buttonTotalHistory = new javax.swing.JButton();
        jPanel1 = new javax.swing.JPanel();
        textSaldoAtual = new javax.swing.JLabel();
        textSaldoAtualValor = new javax.swing.JLabel();
        textGastoTotal = new javax.swing.JLabel();
        textGastoTotalValor = new javax.swing.JLabel();
        textReceitaTotal = new javax.swing.JLabel();
        textReceitaTotalValor = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        listLatestTransactions = new javax.swing.JList<>();
        textLatestTransactions = new javax.swing.JLabel();
        textCreateCategory = new javax.swing.JLabel();
        textNewCategoryName = new javax.swing.JTextField();
        panelGraficoFinanceiro = new javax.swing.JPanel();
        buttonCreateCategory = new javax.swing.JButton();
        radioButtonPorTipo = new javax.swing.JRadioButton();
        radioButtonPorCategoria = new javax.swing.JRadioButton();
        textBemVindo = new javax.swing.JLabel();

        textAppName.setFont(new java.awt.Font("Bitstream Charter", 0, 36)); // NOI18N
        textAppName.setText("Sistema Financeiro");

        buttonExit.setText("Sair");
        buttonExit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonExitActionPerformed(evt);
            }
        });

        buttonTotalHistory.setText("Histórico Total");
        buttonTotalHistory.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonTotalHistoryActionPerformed(evt);
            }
        });

        jPanel1.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));

        textSaldoAtual.setText("Saldo Atual: ");

        textSaldoAtualValor.setText("0 R$");

        textGastoTotal.setText("Despesa Total: ");

        textGastoTotalValor.setText("0 R$");

        textReceitaTotal.setText("Receita Total:");

        textReceitaTotalValor.setText("0 R$");

        listLatestTransactions.setModel(new javax.swing.AbstractListModel<String>() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public String getElementAt(int i) { return strings[i]; }
        });
        jScrollPane1.setViewportView(listLatestTransactions);

        textLatestTransactions.setText("Últimas 5 transações");

        textCreateCategory.setText("Criar Categorias");

        textNewCategoryName.setText("Nome");
        textNewCategoryName.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                textNewCategoryNameActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout panelGraficoFinanceiroLayout = new javax.swing.GroupLayout(panelGraficoFinanceiro);
        panelGraficoFinanceiro.setLayout(panelGraficoFinanceiroLayout);
        panelGraficoFinanceiroLayout.setHorizontalGroup(
            panelGraficoFinanceiroLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 414, Short.MAX_VALUE)
        );
        panelGraficoFinanceiroLayout.setVerticalGroup(
            panelGraficoFinanceiroLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 255, Short.MAX_VALUE)
        );

        buttonCreateCategory.setText("Criar");
        buttonCreateCategory.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonCreateCategoryActionPerformed(evt);
            }
        });

        radioButtonPorTipo.setText("Por Tipo");

        radioButtonPorCategoria.setText("Por Categoria");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(26, 26, 26)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addComponent(textSaldoAtual)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addComponent(textSaldoAtualValor))
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addComponent(textReceitaTotal)
                                        .addGap(18, 18, 18)
                                        .addComponent(textReceitaTotalValor))
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addComponent(textGastoTotal)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addComponent(textGastoTotalValor)))
                                .addGap(75, 75, 75))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(textLatestTransactions)
                                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 191, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(18, 18, 18)))
                        .addComponent(panelGraficoFinanceiro, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(textNewCategoryName, javax.swing.GroupLayout.PREFERRED_SIZE, 129, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(buttonCreateCategory)
                        .addGap(41, 41, 41)
                        .addComponent(radioButtonPorTipo)
                        .addGap(18, 18, 18)
                        .addComponent(radioButtonPorCategoria))
                    .addComponent(textCreateCategory))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(0, 8, Short.MAX_VALUE)
                        .addComponent(panelGraficoFinanceiro, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(17, 17, 17)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(textSaldoAtualValor)
                            .addComponent(textSaldoAtual, javax.swing.GroupLayout.Alignment.TRAILING))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(textGastoTotal)
                            .addComponent(textGastoTotalValor))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(textReceitaTotal)
                            .addComponent(textReceitaTotalValor))
                        .addGap(18, 18, 18)
                        .addComponent(textLatestTransactions)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 114, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(textCreateCategory)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(textNewCategoryName, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(buttonCreateCategory, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(radioButtonPorTipo)
                    .addComponent(radioButtonPorCategoria))
                .addGap(25, 25, 25))
        );

        textBemVindo.setFont(new java.awt.Font("sansserif", 0, 18)); // NOI18N
        textBemVindo.setText("Bem vindo, ");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(24, 24, 24)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(textBemVindo)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap(50, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(textAppName)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(buttonExit)
                        .addGap(41, 41, 41))))
            .addGroup(layout.createSequentialGroup()
                .addGap(301, 301, 301)
                .addComponent(buttonTotalHistory)
                .addGap(0, 0, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(textAppName)
                    .addComponent(buttonExit))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(textBemVindo)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 15, Short.MAX_VALUE)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(buttonTotalHistory)
                .addGap(44, 44, 44))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void buttonExitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonExitActionPerformed
        this.app.setUser(null);
        this.app.changePanel(new view.LoginView(this.app));
    }//GEN-LAST:event_buttonExitActionPerformed

    private void buttonTotalHistoryActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonTotalHistoryActionPerformed
        this.app.changePanel(new view.TransactionFormView(this.app));
    }//GEN-LAST:event_buttonTotalHistoryActionPerformed

    private void textNewCategoryNameActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_textNewCategoryNameActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_textNewCategoryNameActionPerformed

    private void buttonCreateCategoryActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonCreateCategoryActionPerformed
        String s = textNewCategoryName.getText(); 
        
        if (!s.isBlank()){
            new CategoryService().addCategory(s);
        }
        
        textNewCategoryName.setText("");
    }//GEN-LAST:event_buttonCreateCategoryActionPerformed
    

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton buttonCreateCategory;
    private javax.swing.JButton buttonExit;
    private javax.swing.JButton buttonTotalHistory;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JList<String> listLatestTransactions;
    private javax.swing.JPanel panelGraficoFinanceiro;
    private javax.swing.JRadioButton radioButtonPorCategoria;
    private javax.swing.JRadioButton radioButtonPorTipo;
    private javax.swing.JLabel textAppName;
    private javax.swing.JLabel textBemVindo;
    private javax.swing.JLabel textCreateCategory;
    private javax.swing.JLabel textGastoTotal;
    private javax.swing.JLabel textGastoTotalValor;
    private javax.swing.JLabel textLatestTransactions;
    private javax.swing.JTextField textNewCategoryName;
    private javax.swing.JLabel textReceitaTotal;
    private javax.swing.JLabel textReceitaTotalValor;
    private javax.swing.JLabel textSaldoAtual;
    private javax.swing.JLabel textSaldoAtualValor;
    // End of variables declaration//GEN-END:variables
}
