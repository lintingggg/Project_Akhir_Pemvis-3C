/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package finalboss;
import java.sql.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.sql.Timestamp;
import com.formdev.flatlaf.themes.FlatMacLightLaf;

/**
 *
 * @author faza
 */
public class MainFrame extends javax.swing.JFrame {
    private String username;
    private int userId;
    /**
     * Creates new form MainFrame
     */
    public MainFrame(String username, int userId) {
        initComponents();
        this.username = username;
        this.userId = userId;
        lb_username.setText(username + "!"); // Menampilkan username
        tampilkanDataScores();
        // Menambahkan ActionListener pada button kategori
        addCategoryButtonListeners();
    }

    // Method untuk menambahkan ActionListener pada setiap button
    private void addCategoryButtonListeners() {
        btnMusik.addActionListener(e -> startQuizWithCategory("Alat Musik"));
        btnMakanan.addActionListener(e -> startQuizWithCategory("Makanan Daerah"));
        btnPakaian.addActionListener(e -> startQuizWithCategory("Pakaian Adat"));
        btnRumah.addActionListener(e -> startQuizWithCategory("Rumah Adat"));
        btnTarian.addActionListener(e -> startQuizWithCategory("Tarian Tradisional"));
        btnSenjata.addActionListener(e -> startQuizWithCategory("Senjata Daerah"));
    }
    
    // Method untuk memulai quiz berdasarkan kategori
    private void startQuizWithCategory(String category) {
        // Menutup MainFrame dan membuka PertanyaanFrame
        dispose();
        new PertanyaanFrame(category, userId, username).setVisible(true);
    }
    
    // Method untuk menampilkan data dari tabel Scores ke JTable dengan username
    private void tampilkanDataScores() {
        // Kolom yang akan ditampilkan di JTable
        String[] kolom = {"Username", "Score", "Date Taken"};

        // DefaultTableModel untuk JTable
        DefaultTableModel model = new DefaultTableModel(kolom, 0);

        String query = """
                       SELECT s.score_id, u.username, s.score, s.date_taken 
                       FROM Scores s
                       JOIN Users u ON s.user_id = u.user_id
                       ORDER BY s.date_taken DESC
                       """;

        try (Connection conn = koneksi.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            // Mengisi data dari ResultSet ke model
            while (rs.next()) {
                int scoreId = rs.getInt("score_id");
                String username = rs.getString("username");
                int score = rs.getInt("score");
                Timestamp dateTaken = rs.getTimestamp("date_taken");

                // Tambahkan data ke dalam baris model
                Object[] data = {username, score, dateTaken};
                model.addRow(data);
            }

            // Tampilkan model di JTable
            tbl_score.setModel(model);

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Gagal menampilkan data: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void ExitButton() {
        int confirm = JOptionPane.showConfirmDialog(this, "Apakah Anda yakin ingin keluar?", "Konfirmasi Keluar", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            System.exit(0); // Menutup aplikasi
        }
    }
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        tabbedPaneCustom1 = new Custom.TabbedPaneCustom();
        jPanel2 = new javax.swing.JPanel();
        jLabel12 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        roundedPanel2 = new Custom.RoundedPanel();
        jLabel14 = new javax.swing.JLabel();
        lb_username = new javax.swing.JLabel();
        jLabel16 = new javax.swing.JLabel();
        jLabel17 = new javax.swing.JLabel();
        btnMusik = new javax.swing.JButton();
        jLabel18 = new javax.swing.JLabel();
        btnMakanan = new javax.swing.JButton();
        jLabel19 = new javax.swing.JLabel();
        btnPakaian = new javax.swing.JButton();
        jLabel20 = new javax.swing.JLabel();
        btnRumah = new javax.swing.JButton();
        jLabel21 = new javax.swing.JLabel();
        jLabel22 = new javax.swing.JLabel();
        btnTarian = new javax.swing.JButton();
        btnSenjata = new javax.swing.JButton();
        btnKeluar = new javax.swing.JButton();
        jPanel1 = new javax.swing.JPanel();
        roundedPanel1 = new Custom.RoundedPanel();
        jLabel24 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tbl_score = new javax.swing.JTable();
        jLabel15 = new javax.swing.JLabel();
        jLabel23 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        tabbedPaneCustom1.setSelectedColor(new java.awt.Color(84, 51, 16));

        jPanel2.setBackground(new java.awt.Color(84, 51, 16));

        jLabel12.setFont(new java.awt.Font("Poppins ExtraBold", 0, 36)); // NOI18N
        jLabel12.setForeground(new java.awt.Color(248, 244, 225));
        jLabel12.setText("BATIK");

        jLabel13.setFont(new java.awt.Font("Poppins Medium", 2, 18)); // NOI18N
        jLabel13.setForeground(new java.awt.Color(248, 244, 225));
        jLabel13.setText("Belajar Asyik Tentang Identitas Kebudayaan");

        roundedPanel2.setForeground(new java.awt.Color(248, 244, 225));
        roundedPanel2.setRoundTopLeft(90);
        roundedPanel2.setRoundTopRight(90);

        jLabel14.setFont(new java.awt.Font("Poppins Medium", 0, 14)); // NOI18N
        jLabel14.setForeground(new java.awt.Color(84, 51, 16));
        jLabel14.setText("Haiii");

        lb_username.setFont(new java.awt.Font("Poppins Medium", 0, 14)); // NOI18N
        lb_username.setForeground(new java.awt.Color(84, 51, 16));
        lb_username.setText("username");

        jLabel16.setFont(new java.awt.Font("Poppins Medium", 0, 14)); // NOI18N
        jLabel16.setForeground(new java.awt.Color(84, 51, 16));
        jLabel16.setText("Sebelum bermain pilih kategori dulu!");

        jLabel17.setIcon(new javax.swing.ImageIcon(getClass().getResource("/image/MusikTradisional.png"))); // NOI18N

        btnMusik.setBackground(new java.awt.Color(175, 143, 111));
        btnMusik.setFont(new java.awt.Font("Poppins Medium", 0, 12)); // NOI18N
        btnMusik.setText("Musik Tradisional");
        btnMusik.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);

        jLabel18.setIcon(new javax.swing.ImageIcon(getClass().getResource("/image/MakananTradisional.png"))); // NOI18N

        btnMakanan.setBackground(new java.awt.Color(175, 143, 111));
        btnMakanan.setFont(new java.awt.Font("Poppins Medium", 0, 12)); // NOI18N
        btnMakanan.setText("Makanan Tradisional");
        btnMakanan.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);

        jLabel19.setIcon(new javax.swing.ImageIcon(getClass().getResource("/image/PakaianAdat.png"))); // NOI18N
        jLabel19.setText("jLabel8");

        btnPakaian.setBackground(new java.awt.Color(175, 143, 111));
        btnPakaian.setFont(new java.awt.Font("Poppins Medium", 0, 12)); // NOI18N
        btnPakaian.setText("Pakaian Adat");
        btnPakaian.setBorderPainted(false);
        btnPakaian.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        btnPakaian.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);

        jLabel20.setIcon(new javax.swing.ImageIcon(getClass().getResource("/image/RumahAdat.png"))); // NOI18N

        btnRumah.setBackground(new java.awt.Color(175, 143, 111));
        btnRumah.setFont(new java.awt.Font("Poppins Medium", 0, 12)); // NOI18N
        btnRumah.setText("Rumah Adat");

        jLabel21.setIcon(new javax.swing.ImageIcon(getClass().getResource("/image/TariTradisional.png"))); // NOI18N

        jLabel22.setIcon(new javax.swing.ImageIcon(getClass().getResource("/image/SenjataTradisional.png"))); // NOI18N

        btnTarian.setBackground(new java.awt.Color(175, 143, 111));
        btnTarian.setFont(new java.awt.Font("Poppins Medium", 0, 12)); // NOI18N
        btnTarian.setText("Tari Tradisional");

        btnSenjata.setBackground(new java.awt.Color(175, 143, 111));
        btnSenjata.setFont(new java.awt.Font("Poppins Medium", 0, 12)); // NOI18N
        btnSenjata.setText("Senjata Tradisional");

        btnKeluar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/image/logout_3622_1.png"))); // NOI18N
        btnKeluar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnKeluarActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout roundedPanel2Layout = new javax.swing.GroupLayout(roundedPanel2);
        roundedPanel2.setLayout(roundedPanel2Layout);
        roundedPanel2Layout.setHorizontalGroup(
            roundedPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(roundedPanel2Layout.createSequentialGroup()
                .addGap(33, 33, 33)
                .addGroup(roundedPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(btnRumah, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel20, javax.swing.GroupLayout.DEFAULT_SIZE, 158, Short.MAX_VALUE)
                    .addComponent(jLabel17, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnMusik, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGroup(roundedPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(roundedPanel2Layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(roundedPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jLabel21, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(btnMakanan, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(btnTarian, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, roundedPanel2Layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 80, Short.MAX_VALUE)
                        .addComponent(jLabel18)
                        .addGap(71, 71, 71)))
                .addGroup(roundedPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel22, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, roundedPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(btnPakaian, javax.swing.GroupLayout.PREFERRED_SIZE, 151, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel19, javax.swing.GroupLayout.PREFERRED_SIZE, 151, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(btnSenjata, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(57, 57, 57))
            .addGroup(roundedPanel2Layout.createSequentialGroup()
                .addGroup(roundedPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(roundedPanel2Layout.createSequentialGroup()
                        .addGap(277, 277, 277)
                        .addComponent(jLabel14)
                        .addGap(18, 18, 18)
                        .addComponent(lb_username))
                    .addGroup(roundedPanel2Layout.createSequentialGroup()
                        .addGap(204, 204, 204)
                        .addComponent(jLabel16, javax.swing.GroupLayout.PREFERRED_SIZE, 272, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(roundedPanel2Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(btnKeluar)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        roundedPanel2Layout.setVerticalGroup(
            roundedPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(roundedPanel2Layout.createSequentialGroup()
                .addGap(22, 22, 22)
                .addGroup(roundedPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lb_username)
                    .addComponent(jLabel14))
                .addGap(18, 18, 18)
                .addComponent(jLabel16)
                .addGap(18, 18, 18)
                .addGroup(roundedPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel17)
                    .addComponent(jLabel18)
                    .addComponent(jLabel19))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(roundedPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnMusik)
                    .addGroup(roundedPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(btnMakanan)
                        .addComponent(btnPakaian)))
                .addGap(18, 18, 18)
                .addGroup(roundedPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel20)
                    .addComponent(jLabel22)
                    .addComponent(jLabel21))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(roundedPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnRumah)
                    .addComponent(btnTarian)
                    .addComponent(btnSenjata))
                .addGap(18, 18, 18)
                .addComponent(btnKeluar)
                .addContainerGap(12, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(294, 294, 294)
                .addComponent(jLabel12)
                .addContainerGap(311, Short.MAX_VALUE))
            .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel2Layout.createSequentialGroup()
                    .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel13)
                    .addGap(0, 0, Short.MAX_VALUE))
                .addComponent(roundedPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel12)
                .addContainerGap(502, Short.MAX_VALUE))
            .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel2Layout.createSequentialGroup()
                    .addGap(0, 54, Short.MAX_VALUE)
                    .addComponent(jLabel13)
                    .addGap(22, 22, 22)
                    .addComponent(roundedPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(0, 6, Short.MAX_VALUE)))
        );

        tabbedPaneCustom1.addTab("Game", jPanel2);

        jPanel1.setBackground(new java.awt.Color(84, 51, 16));
        jPanel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        roundedPanel1.setForeground(new java.awt.Color(248, 244, 225));
        roundedPanel1.setRoundTopLeft(90);
        roundedPanel1.setRoundTopRight(90);

        jLabel24.setFont(new java.awt.Font("Poppins Medium", 0, 14)); // NOI18N
        jLabel24.setForeground(new java.awt.Color(84, 51, 16));
        jLabel24.setText("Riwayat Bermain");

        tbl_score.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jScrollPane1.setViewportView(tbl_score);

        javax.swing.GroupLayout roundedPanel1Layout = new javax.swing.GroupLayout(roundedPanel1);
        roundedPanel1.setLayout(roundedPanel1Layout);
        roundedPanel1Layout.setHorizontalGroup(
            roundedPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(roundedPanel1Layout.createSequentialGroup()
                .addContainerGap(165, Short.MAX_VALUE)
                .addGroup(roundedPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, roundedPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel24)
                        .addGap(274, 274, 274))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, roundedPanel1Layout.createSequentialGroup()
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(93, 93, 93))))
        );
        roundedPanel1Layout.setVerticalGroup(
            roundedPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(roundedPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel24)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 344, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(56, Short.MAX_VALUE))
        );

        jPanel1.add(roundedPanel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 120, 710, 440));

        jLabel15.setFont(new java.awt.Font("Poppins ExtraBold", 0, 36)); // NOI18N
        jLabel15.setForeground(new java.awt.Color(248, 244, 225));
        jLabel15.setText("BATIK");
        jPanel1.add(jLabel15, new org.netbeans.lib.awtextra.AbsoluteConstraints(290, 10, -1, -1));

        jLabel23.setFont(new java.awt.Font("Poppins Medium", 2, 18)); // NOI18N
        jLabel23.setForeground(new java.awt.Color(248, 244, 225));
        jLabel23.setText("Belajar Asyik Tentang Identitas Kebudayaan");
        jPanel1.add(jLabel23, new org.netbeans.lib.awtextra.AbsoluteConstraints(160, 70, -1, -1));

        tabbedPaneCustom1.addTab("History", jPanel1);

        getContentPane().add(tabbedPaneCustom1, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 720, 600));

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnKeluarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnKeluarActionPerformed
        ExitButton();
    }//GEN-LAST:event_btnKeluarActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(MainFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(MainFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(MainFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(MainFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        FlatMacLightLaf.setup();
        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new MainFrame("Player123",1).setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnKeluar;
    private javax.swing.JButton btnMakanan;
    private javax.swing.JButton btnMusik;
    private javax.swing.JButton btnPakaian;
    private javax.swing.JButton btnRumah;
    private javax.swing.JButton btnSenjata;
    private javax.swing.JButton btnTarian;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lb_username;
    private Custom.RoundedPanel roundedPanel1;
    private Custom.RoundedPanel roundedPanel2;
    private Custom.TabbedPaneCustom tabbedPaneCustom1;
    private javax.swing.JTable tbl_score;
    // End of variables declaration//GEN-END:variables
}
