package src;

import java.awt.BorderLayout;
import java.awt.Font;
import java.util.Enumeration;
import javax.swing.*;
import model.User;

public class SistemaGestaoFinanceira extends JFrame {
    private JPanel currentPanel;
    private User currentUser;
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            setUIFont(new Font("Tahoma", Font.BOLD, 14));
            new SistemaGestaoFinanceira().configureInitialPanel();
        });
    }

    private static void setUIFont(Font f) {
        Enumeration<Object> keys = UIManager.getDefaults().keys();
        while (keys.hasMoreElements()) {
            Object key = keys.nextElement();
            Object value = UIManager.get(key);
            if (value instanceof javax.swing.plaf.FontUIResource){
                UIManager.put(key, new javax.swing.plaf.FontUIResource(f));
            }
        }
    }

    
    public void changePanel(JPanel newPanel) {
        if (currentPanel != null) {
            remove(currentPanel);
        }
        currentPanel = newPanel;
        add(currentPanel, BorderLayout.CENTER);
        revalidate();
        repaint();
    }
    
    private void configureInitialPanel() {
        setTitle("Lab POO");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        changePanel(new view.LoginView(this));

        setSize(720, 565);
        setLocationRelativeTo(null);
        setVisible(true);
    }
    
    public void setUser(User newUser){
        this.currentUser = newUser;
    }
    
    public User getUser(){
        return this.currentUser;
    }
}
