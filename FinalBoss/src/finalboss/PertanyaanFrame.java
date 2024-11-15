/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package finalboss;
import com.formdev.flatlaf.themes.FlatMacLightLaf;
import javax.swing.*;
import java.awt.*;
import java.sql.*;
import java.util.ArrayList;
/**
 *
 * @author faza
 */
public class PertanyaanFrame extends javax.swing.JFrame {
    private ArrayList<String> pertanyaanList;
    private ArrayList<String[]> pilihanJawabanList;
    private ArrayList<String> jawabanBenarList;
    private ArrayList<byte[]> gambarList = new ArrayList<>();
    private int nomorPertanyaan;
    private int score = 0;
    private String selectedAnswer;
    private String selectedCategory;
    private int userId;
    private String username;
    /**
     * Creates new form PertanyaanFrame
     */
    public PertanyaanFrame(String selectedCategory, int userId, String username) {
        initComponents();
        this.selectedCategory = selectedCategory;
        this.userId = userId;
        this.username = username;
        this.pertanyaanList = new ArrayList<>();
        this.pilihanJawabanList = new ArrayList<>();
        this.jawabanBenarList = new ArrayList<>();
        this.nomorPertanyaan = 0;
        ambilPertanyaanDariDatabase();
        tampilkanPertanyaan();
    }

    // Method untuk mengambil pertanyaan dari database
    private void ambilPertanyaanDariDatabase() {
        try (Connection conn = koneksi.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     "SELECT question_text, option_a, option_b, option_c, option_d, correct_answer, image FROM Questions WHERE category = ?")) {

            stmt.setString(1, selectedCategory); // Set kategori berdasarkan pilihan user
            ResultSet rs = stmt.executeQuery();

            boolean hasQuestions = false;
            while (rs.next()) {
                hasQuestions = true;
                // Ambil pertanyaan dan pilihan jawaban
                String questionText = rs.getString("question_text");
                String optionA = rs.getString("option_a");
                String optionB = rs.getString("option_b");
                String optionC = rs.getString("option_c");
                String optionD = rs.getString("option_d");
                String correctAnswer = rs.getString("correct_answer");
                
                // Ambil gambar (jika ada)
                byte[] imageBytes = rs.getBytes("image");

                pertanyaanList.add(questionText);
                pilihanJawabanList.add(new String[]{optionA, optionB, optionC, optionD});
                jawabanBenarList.add(correctAnswer);
                gambarList.add(imageBytes);
            }
            
            rs.close();

            if (!hasQuestions) {
                JOptionPane.showMessageDialog(this, "Tidak ada pertanyaan untuk kategori ini", "Info", JOptionPane.INFORMATION_MESSAGE);
                this.dispose();
                new MainFrame(username, userId).setVisible(true);
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Gagal mengambil data dari database: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    // Method untuk menampilkan pertanyaan
    private void tampilkanPertanyaan() {
        if (pertanyaanList.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Tidak ada pertanyaan untuk kategori ini", "Info", JOptionPane.INFORMATION_MESSAGE);
            this.dispose();
            return;
        }

        // Atur label kategori dan nomor pertanyaan
        jLabel2.setText(selectedCategory);
        jLabel4.setText((nomorPertanyaan + 1) + "/" + pertanyaanList.size());

        // Set pertanyaan
        jLabel6.setText(pertanyaanList.get(nomorPertanyaan));

        // Set opsi jawaban
        String[] pilihanJawaban = pilihanJawabanList.get(nomorPertanyaan);
        jRadioButton1.setText(pilihanJawaban[0]);
        jRadioButton2.setText(pilihanJawaban[1]);
        jRadioButton3.setText(pilihanJawaban[2]);
        jRadioButton4.setText(pilihanJawaban[3]);

        // Tampilkan gambar jika ada
        byte[] imageBytes = gambarList.get(nomorPertanyaan);
        if (imageBytes != null && imageBytes.length > 0) {
            ImageIcon imageIcon = new ImageIcon(imageBytes);
            Image image = imageIcon.getImage();
            Image scaledImage = image.getScaledInstance(roundedPanel2.getWidth(), roundedPanel2.getHeight(), Image.SCALE_SMOOTH);
            lbl_previewGambar.setIcon(new ImageIcon(scaledImage));
            lbl_previewGambar.setText("");
        } else {
            ImageIcon defaultImageIcon = new ImageIcon(getClass().getResource("/image/3dChar.png"));
            Image defaultImage = defaultImageIcon.getImage();
            Image scaledDefaultImage = defaultImage.getScaledInstance(lbl_previewGambar.getWidth(), lbl_previewGambar.getHeight(), Image.SCALE_SMOOTH);
            lbl_previewGambar.setIcon(new ImageIcon(scaledDefaultImage));
        }

        // Reset pilihan sebelumnya
        rb_pilihan.clearSelection();

        // Ganti teks tombol menjadi "Submit" jika pertanyaan terakhir
        if (nomorPertanyaan == pertanyaanList.size() - 1) {
            jButton1.setText("Submit");
        } else {
            jButton1.setText("NEXT");
        }
    }
    
    // Method untuk mendapatkan jawaban yang dipilih user
    private String getSelectedAnswer() {
        if (jRadioButton1.isSelected()) {
            return "A";
        } else if (jRadioButton2.isSelected()) {
            return "B";
        } else if (jRadioButton3.isSelected()) {
            return "C";
        } else if (jRadioButton4.isSelected()) {
            return "D";
        }
        return null;
    }
    
    // Method untuk menangani tombol NEXT/SUBMIT
    private void handleNextButton() {
        selectedAnswer = getSelectedAnswer();

        if (selectedAnswer == null) {
            JOptionPane.showMessageDialog(this, "Silakan pilih salah satu jawaban terlebih dahulu!", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Hitung skor jika jawaban benar
        String jawabanBenar = jawabanBenarList.get(nomorPertanyaan);
        if (selectedAnswer.equalsIgnoreCase(jawabanBenar)) {
            score += 10;
        }

        if (nomorPertanyaan < pertanyaanList.size() - 1) {
            nomorPertanyaan++;
            tampilkanPertanyaan();
        } else {
            simpanScoreKeDatabase(score);
            JOptionPane.showMessageDialog(this, "Quiz Selesai! Skor Anda: " + score);
            this.dispose();
            new MainFrame(username, userId).setVisible(true);
        }
    }
    // Method untuk menangani tombol Previous
    private void handlePreviousButton() {
        if (nomorPertanyaan > 0) {
            nomorPertanyaan--;
            tampilkanPertanyaan();
        } else {
            JOptionPane.showMessageDialog(this, "Ini adalah pertanyaan pertama!", "Info", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    
    // Method untuk menyimpan skor ke tabel Scores
    private void simpanScoreKeDatabase(int score) {
        try (Connection conn = koneksi.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     "INSERT INTO Scores (user_id, score, date_taken) VALUES (?, ?, NOW())")) {

            stmt.setInt(1, userId);
            stmt.setInt(2, score);
            stmt.executeUpdate();

            JOptionPane.showMessageDialog(this, "Skor berhasil disimpan!", "Sukses", JOptionPane.INFORMATION_MESSAGE);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Gagal menyimpan skor: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
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
        java.awt.GridBagConstraints gridBagConstraints;

        rb_pilihan = new javax.swing.ButtonGroup();
        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        roundedPanel2 = new Custom.RoundedPanel();
        lbl_previewGambar = new javax.swing.JLabel();
        roundedPanel3 = new Custom.RoundedPanel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        roundedPanel7 = new Custom.RoundedPanel();
        jRadioButton1 = new javax.swing.JRadioButton();
        roundedPanel8 = new Custom.RoundedPanel();
        jRadioButton2 = new javax.swing.JRadioButton();
        roundedPanel9 = new Custom.RoundedPanel();
        jRadioButton3 = new javax.swing.JRadioButton();
        roundedPanel10 = new Custom.RoundedPanel();
        jRadioButton4 = new javax.swing.JRadioButton();
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel1.setBackground(new java.awt.Color(84, 51, 16));

        jLabel1.setFont(new java.awt.Font("Poppins Medium", 0, 18)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(248, 244, 225));
        jLabel1.setText("Kategori : ");

        jLabel2.setFont(new java.awt.Font("Poppins Medium", 0, 18)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(248, 244, 225));
        jLabel2.setText("jLabel2");

        jLabel3.setFont(new java.awt.Font("Poppins Medium", 0, 18)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(248, 244, 225));
        jLabel3.setText("Nomor : ");

        jLabel4.setFont(new java.awt.Font("Poppins Medium", 0, 18)); // NOI18N
        jLabel4.setForeground(new java.awt.Color(248, 244, 225));
        jLabel4.setText("1/1");

        roundedPanel2.setForeground(new java.awt.Color(248, 244, 225));
        roundedPanel2.setRoundBottomLeft(60);
        roundedPanel2.setRoundBottomRight(60);
        roundedPanel2.setRoundTopLeft(60);
        roundedPanel2.setRoundTopRight(60);

        javax.swing.GroupLayout roundedPanel2Layout = new javax.swing.GroupLayout(roundedPanel2);
        roundedPanel2.setLayout(roundedPanel2Layout);
        roundedPanel2Layout.setHorizontalGroup(
            roundedPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, roundedPanel2Layout.createSequentialGroup()
                .addContainerGap(17, Short.MAX_VALUE)
                .addComponent(lbl_previewGambar, javax.swing.GroupLayout.PREFERRED_SIZE, 151, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(15, 15, 15))
        );
        roundedPanel2Layout.setVerticalGroup(
            roundedPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, roundedPanel2Layout.createSequentialGroup()
                .addContainerGap(18, Short.MAX_VALUE)
                .addComponent(lbl_previewGambar, javax.swing.GroupLayout.PREFERRED_SIZE, 151, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(15, 15, 15))
        );

        roundedPanel3.setForeground(new java.awt.Color(248, 244, 225));
        roundedPanel3.setRoundBottomLeft(40);
        roundedPanel3.setRoundBottomRight(40);
        roundedPanel3.setRoundTopLeft(40);
        roundedPanel3.setRoundTopRight(40);

        jLabel5.setText("Pertanyaan");

        javax.swing.GroupLayout roundedPanel3Layout = new javax.swing.GroupLayout(roundedPanel3);
        roundedPanel3.setLayout(roundedPanel3Layout);
        roundedPanel3Layout.setHorizontalGroup(
            roundedPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, roundedPanel3Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel5)
                .addGap(227, 227, 227))
            .addGroup(roundedPanel3Layout.createSequentialGroup()
                .addGap(24, 24, 24)
                .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 486, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(19, Short.MAX_VALUE))
        );
        roundedPanel3Layout.setVerticalGroup(
            roundedPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(roundedPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel5)
                .addGap(18, 18, 18)
                .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(25, Short.MAX_VALUE))
        );

        roundedPanel7.setForeground(new java.awt.Color(248, 244, 225));
        roundedPanel7.setRoundBottomLeft(20);
        roundedPanel7.setRoundBottomRight(20);
        roundedPanel7.setRoundTopLeft(20);
        roundedPanel7.setRoundTopRight(20);

        rb_pilihan.add(jRadioButton1);

        javax.swing.GroupLayout roundedPanel7Layout = new javax.swing.GroupLayout(roundedPanel7);
        roundedPanel7.setLayout(roundedPanel7Layout);
        roundedPanel7Layout.setHorizontalGroup(
            roundedPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(roundedPanel7Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jRadioButton1, javax.swing.GroupLayout.DEFAULT_SIZE, 208, Short.MAX_VALUE)
                .addContainerGap())
        );
        roundedPanel7Layout.setVerticalGroup(
            roundedPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(roundedPanel7Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jRadioButton1)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        roundedPanel8.setForeground(new java.awt.Color(248, 244, 225));
        roundedPanel8.setRoundBottomLeft(20);
        roundedPanel8.setRoundBottomRight(20);
        roundedPanel8.setRoundTopLeft(20);
        roundedPanel8.setRoundTopRight(20);

        rb_pilihan.add(jRadioButton2);

        javax.swing.GroupLayout roundedPanel8Layout = new javax.swing.GroupLayout(roundedPanel8);
        roundedPanel8.setLayout(roundedPanel8Layout);
        roundedPanel8Layout.setHorizontalGroup(
            roundedPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(roundedPanel8Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jRadioButton2, javax.swing.GroupLayout.DEFAULT_SIZE, 208, Short.MAX_VALUE)
                .addContainerGap())
        );
        roundedPanel8Layout.setVerticalGroup(
            roundedPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, roundedPanel8Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jRadioButton2)
                .addContainerGap())
        );

        roundedPanel9.setForeground(new java.awt.Color(248, 244, 225));
        roundedPanel9.setRoundBottomLeft(20);
        roundedPanel9.setRoundBottomRight(20);
        roundedPanel9.setRoundTopLeft(20);
        roundedPanel9.setRoundTopRight(20);

        rb_pilihan.add(jRadioButton3);

        javax.swing.GroupLayout roundedPanel9Layout = new javax.swing.GroupLayout(roundedPanel9);
        roundedPanel9.setLayout(roundedPanel9Layout);
        roundedPanel9Layout.setHorizontalGroup(
            roundedPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(roundedPanel9Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jRadioButton3, javax.swing.GroupLayout.DEFAULT_SIZE, 208, Short.MAX_VALUE)
                .addContainerGap())
        );
        roundedPanel9Layout.setVerticalGroup(
            roundedPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, roundedPanel9Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jRadioButton3)
                .addContainerGap())
        );

        roundedPanel10.setForeground(new java.awt.Color(248, 244, 225));
        roundedPanel10.setRoundBottomLeft(20);
        roundedPanel10.setRoundBottomRight(20);
        roundedPanel10.setRoundTopLeft(20);
        roundedPanel10.setRoundTopRight(20);

        rb_pilihan.add(jRadioButton4);

        javax.swing.GroupLayout roundedPanel10Layout = new javax.swing.GroupLayout(roundedPanel10);
        roundedPanel10.setLayout(roundedPanel10Layout);
        roundedPanel10Layout.setHorizontalGroup(
            roundedPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(roundedPanel10Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jRadioButton4, javax.swing.GroupLayout.DEFAULT_SIZE, 208, Short.MAX_VALUE)
                .addContainerGap())
        );
        roundedPanel10Layout.setVerticalGroup(
            roundedPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(roundedPanel10Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jRadioButton4)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jButton1.setText("Kembali");

        jButton2.setText("Lanjut");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel3)
                .addGap(18, 18, 18)
                .addComponent(jLabel4)
                .addGap(15, 15, 15))
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(98, 98, 98)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(roundedPanel9, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(roundedPanel10, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addComponent(roundedPanel7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(roundedPanel8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(roundedPanel3, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(73, Short.MAX_VALUE))
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(60, 60, 60)
                .addComponent(jButton1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jButton2)
                .addGap(38, 38, 38))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(roundedPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(238, 238, 238))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(jLabel2)
                    .addComponent(jLabel3)
                    .addComponent(jLabel4))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(roundedPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(roundedPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(roundedPanel7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(roundedPanel8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(roundedPanel9, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(roundedPanel10, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jButton1)
                    .addComponent(jButton2))
                .addContainerGap(12, Short.MAX_VALUE))
        );

        getContentPane().add(jPanel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 700, 500));

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        handleNextButton();
    }//GEN-LAST:event_jButton2ActionPerformed

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
            java.util.logging.Logger.getLogger(PertanyaanFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(PertanyaanFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(PertanyaanFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(PertanyaanFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        FlatMacLightLaf.setup();
        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new PertanyaanFrame("Tari",1,"player1").setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JRadioButton jRadioButton1;
    private javax.swing.JRadioButton jRadioButton2;
    private javax.swing.JRadioButton jRadioButton3;
    private javax.swing.JRadioButton jRadioButton4;
    private javax.swing.JLabel lbl_previewGambar;
    private javax.swing.ButtonGroup rb_pilihan;
    private Custom.RoundedPanel roundedPanel10;
    private Custom.RoundedPanel roundedPanel2;
    private Custom.RoundedPanel roundedPanel3;
    private Custom.RoundedPanel roundedPanel7;
    private Custom.RoundedPanel roundedPanel8;
    private Custom.RoundedPanel roundedPanel9;
    // End of variables declaration//GEN-END:variables
}
