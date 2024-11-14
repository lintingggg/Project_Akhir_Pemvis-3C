/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package quizbudaya;
import java.awt.Image;
import javax.swing.*;
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
    public PertanyaanFrame(String selectedCategory,int userId,String username) {
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
            
            while (rs.next()) {
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
                gambarList.add(imageBytes); // Simpan gambar ke list
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
        jLabel5.setText(pertanyaanList.get(nomorPertanyaan));

        // Set opsi jawaban
        String[] pilihanJawaban = pilihanJawabanList.get(nomorPertanyaan);
        jRadioButton1.setText(pilihanJawaban[0]);
        jRadioButton2.setText(pilihanJawaban[1]);
        jRadioButton3.setText(pilihanJawaban[2]);
        jRadioButton4.setText(pilihanJawaban[3]);
        
        // Tampilkan gambar jika ada
        byte[] imageBytes = gambarList.get(nomorPertanyaan);
        if (imageBytes != null) {
            ImageIcon imageIcon = new ImageIcon(imageBytes);
            Image image = imageIcon.getImage();
            Image scaledImage = image.getScaledInstance(lbl_previewGambar.getWidth(), lbl_previewGambar.getHeight(), Image.SCALE_SMOOTH);
            lbl_previewGambar.setIcon(new ImageIcon(scaledImage));
        } else {
            lbl_previewGambar.setIcon(null); // Hapus gambar jika tidak ada
            lbl_previewGambar.setText("No Image Available");
        }
        
        // Reset pilihan sebelumnya
        buttonGroup1.clearSelection();

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
        return null; // Jika tidak ada jawaban yang dipilih
    }
    
    // Method untuk menangani tombol NEXT/SUBMIT
    private void handleNextButton() {
        // Ambil jawaban yang dipilih user
        selectedAnswer = getSelectedAnswer();

        if (selectedAnswer == null) {
            JOptionPane.showMessageDialog(this, "Silakan pilih salah satu jawaban terlebih dahulu!", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Hitung skor jika jawaban benar
        String jawabanBenar = jawabanBenarList.get(nomorPertanyaan);
        if (selectedAnswer.equals(jawabanBenar)) {
            score += 10; // Tambah skor
        }

        if (nomorPertanyaan < pertanyaanList.size() - 1) {
            nomorPertanyaan++;
            tampilkanPertanyaan();
        } else {
            // Simpan skor ke database setelah quiz selesai
            simpanScoreKeDatabase(score);

            JOptionPane.showMessageDialog(this, "Quiz Selesai! Skor Anda: " + score);
            this.dispose();
            new MainFrame(username, userId).setVisible(true);
        }
    }
    
    // Method untuk menyimpan skor ke tabel Scores (lebih sederhana)
    private void simpanScoreKeDatabase(int score) {
        try (Connection conn = koneksi.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     "INSERT INTO Scores (user_id, score) VALUES (?, ?)")) {

            stmt.setInt(1, userId); // Masukkan user_id dari pengguna yang login
            stmt.setInt(2, score);  // Masukkan skor yang dicapai
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

        buttonGroup1 = new javax.swing.ButtonGroup();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jRadioButton1 = new javax.swing.JRadioButton();
        jRadioButton2 = new javax.swing.JRadioButton();
        jRadioButton3 = new javax.swing.JRadioButton();
        jRadioButton4 = new javax.swing.JRadioButton();
        jButton1 = new javax.swing.JButton();
        lbl_previewGambar = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jLabel1.setText("Kategori : ");

        jLabel2.setText("namaKategori");

        jLabel3.setText("Nomor. ");

        jLabel4.setText("1/1");

        jLabel5.setText("pertanyaan");

        buttonGroup1.add(jRadioButton1);
        jRadioButton1.setText("opsi1");

        buttonGroup1.add(jRadioButton2);
        jRadioButton2.setText("opsi2");

        buttonGroup1.add(jRadioButton3);
        jRadioButton3.setText("opsi3");

        buttonGroup1.add(jRadioButton4);
        jRadioButton4.setText("opsi4");

        jButton1.setText("NEXT");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        lbl_previewGambar.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(15, 15, 15)
                .addComponent(jLabel1)
                .addGap(18, 18, 18)
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel4)
                .addContainerGap())
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGap(40, 40, 40)
                .addComponent(jRadioButton1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jRadioButton2)
                .addGap(140, 140, 140))
            .addGroup(layout.createSequentialGroup()
                .addGap(143, 143, 143)
                .addComponent(jRadioButton3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jRadioButton4)
                .addGap(25, 25, 25))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(227, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(jLabel5)
                        .addGap(239, 239, 239))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(jButton1)
                        .addGap(214, 214, 214))))
            .addGroup(layout.createSequentialGroup()
                .addGap(185, 185, 185)
                .addComponent(lbl_previewGambar, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(jLabel2)
                    .addComponent(jLabel3)
                    .addComponent(jLabel4))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(lbl_previewGambar, javax.swing.GroupLayout.PREFERRED_SIZE, 136, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 29, Short.MAX_VALUE)
                .addComponent(jLabel5)
                .addGap(34, 34, 34)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jRadioButton2)
                    .addComponent(jRadioButton1))
                .addGap(40, 40, 40)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jRadioButton3)
                    .addComponent(jRadioButton4))
                .addGap(36, 36, 36)
                .addComponent(jButton1)
                .addGap(29, 29, 29))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        // TODO add your handling code here:
        handleNextButton();
    }//GEN-LAST:event_jButton1ActionPerformed

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

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new PertanyaanFrame("Tari",1,"player1").setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JButton jButton1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JRadioButton jRadioButton1;
    private javax.swing.JRadioButton jRadioButton2;
    private javax.swing.JRadioButton jRadioButton3;
    private javax.swing.JRadioButton jRadioButton4;
    private javax.swing.JLabel lbl_previewGambar;
    // End of variables declaration//GEN-END:variables
}
