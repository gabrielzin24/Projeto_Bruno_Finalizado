package view;
import javax.swing.JOptionPane;
import model.User;
import service.UserService;
import src.SistemaGestaoFinanceira;

public class LoginView extends javax.swing.JPanel {
    private final SistemaGestaoFinanceira app;
    
    public LoginView(SistemaGestaoFinanceira app) {
        initComponents();
        this.app = app;
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        fieldEmail = new javax.swing.JTextField();
        fieldPassword = new javax.swing.JTextField();
        buttonLogin = new javax.swing.JButton();
        textSemConta = new javax.swing.JLabel();
        textCadastro = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();

        jLabel1.setFont(new java.awt.Font("sansserif", 0, 36)); // NOI18N
        jLabel1.setText("Sistema Financeiro");

        fieldEmail.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                fieldEmailActionPerformed(evt);
            }
        });

        buttonLogin.setText("Login");
        buttonLogin.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonLoginActionPerformed(evt);
            }
        });

        textSemConta.setText("Não tem conta?");

        textCadastro.setFont(new java.awt.Font("sansserif", 1, 13)); // NOI18N
        textCadastro.setForeground(new java.awt.Color(0, 102, 255));
        textCadastro.setText("Cadastrar-se");
        textCadastro.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        textCadastro.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                textCadastroMouseReleased(evt);
            }
        });

        jLabel2.setText("Password");

        jLabel3.setText("Email");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(130, 130, 130)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel3)
                            .addComponent(jLabel2)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                .addComponent(fieldPassword, javax.swing.GroupLayout.PREFERRED_SIZE, 439, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(fieldEmail, javax.swing.GroupLayout.PREFERRED_SIZE, 439, javax.swing.GroupLayout.PREFERRED_SIZE))))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(188, 188, 188)
                        .addComponent(jLabel1))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(242, 242, 242)
                        .addComponent(textSemConta)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(textCadastro))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(307, 307, 307)
                        .addComponent(buttonLogin)))
                .addContainerGap(132, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(45, 45, 45)
                .addComponent(jLabel1)
                .addGap(34, 34, 34)
                .addComponent(jLabel3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(fieldEmail, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(fieldPassword, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(31, 31, 31)
                .addComponent(buttonLogin)
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(textSemConta)
                    .addComponent(textCadastro))
                .addContainerGap(150, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void fieldEmailActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_fieldEmailActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_fieldEmailActionPerformed

    private void buttonLoginActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonLoginActionPerformed
        String email = fieldEmail.getText();
        String password = fieldPassword.getText();
        UserService us = new UserService();
        
        
        int authResult = us.authenticate(email, password);
        if (authResult == us.AUTH_USER_NOT_FOUND){
            JOptionPane.showMessageDialog(this, "Usuário inválido.");
            return;
        }
        
        if (authResult == us.AUTH_INVALID_PASSWORD){
            JOptionPane.showMessageDialog(this, "Senha inválida.");     
            return;
        }
        
        User u = us.getUserByEmail(email);
//        User user = new User(name, email, password);
        this.app.setUser(u);

        JOptionPane.showMessageDialog(this, "Login realizado com sucesso!");
//        this.app.changePanel(new view.HomeView(this.app));
        this.app.changePanel(new view.TransactionFormView(this.app));
    }//GEN-LAST:event_buttonLoginActionPerformed

    private void textCadastroMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_textCadastroMouseReleased
        this.app.changePanel(new view.RegisterPanel(this.app));
    }//GEN-LAST:event_textCadastroMouseReleased


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton buttonLogin;
    private javax.swing.JTextField fieldEmail;
    private javax.swing.JTextField fieldPassword;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel textCadastro;
    private javax.swing.JLabel textSemConta;
    // End of variables declaration//GEN-END:variables
}
