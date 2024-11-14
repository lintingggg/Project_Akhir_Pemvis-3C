/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package AdminQuiz;

import java.awt.Image;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import javax.swing.JOptionPane;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
/**
 *
 * @author faza
 */
public class AdminDashboard extends javax.swing.JFrame {
    private byte[] gambarBytes = null;
    /**
     * Creates new form AdminDashboard
     */
    public AdminDashboard() {
        initComponents();
        readQuestionsToTable(tbl_question);
        loadCategories();
    }

    // Method untuk memuat kategori dari database ke JComboBox
    private void loadCategories() {
        try (Connection conn = koneksi.getConnection()) {
            String query = "SELECT DISTINCT category FROM questions";
            PreparedStatement pstmt = conn.prepareStatement(query);
            ResultSet rs = pstmt.executeQuery();
            
            // Hapus item default JComboBox sebelum diisi
            cmbKategori.removeAllItems();
            
            while (rs.next()) {
                String category = rs.getString("category");
                cmbKategori.addItem(category); // Menambahkan kategori ke JComboBox
            }
            
            rs.close();
            pstmt.close();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Gagal memuat kategori: " + e.getMessage());
        }
    }
    
    // Method untuk membaca semua pertanyaan dari tabel Questions dan menampilkannya di JTable
    public void readQuestionsToTable(JTable table) {
        // Buat model tabel dengan kolom-kolom yang sesuai
        DefaultTableModel model = new DefaultTableModel();
        model.addColumn("ID");
        model.addColumn("Question");
        model.addColumn("Option A");
        model.addColumn("Option B");
        model.addColumn("Option C");
        model.addColumn("Option D");
        model.addColumn("Correct Answer");
        model.addColumn("Category");

        String query = "SELECT * FROM Questions";

        try (Connection conn = koneksi.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            // Hapus semua baris yang ada di model tabel sebelum ditambahkan data baru
            model.setRowCount(0);

            // Loop untuk membaca setiap baris dari hasil query dan menambahkannya ke model
            while (rs.next()) {
                int questionId = rs.getInt("question_id");
                String questionText = rs.getString("question_text");
                String optionA = rs.getString("option_a");
                String optionB = rs.getString("option_b");
                String optionC = rs.getString("option_c");
                String optionD = rs.getString("option_d");
                char correctAnswer = rs.getString("correct_answer").charAt(0);
                String category = rs.getString("category");

                // Tambahkan data ke model tabel
                model.addRow(new Object[]{questionId, questionText, optionA, optionB, optionC, optionD, correctAnswer, category});
            }

            // Set model tabel ke JTable
            table.setModel(model);

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Gagal membaca data pertanyaan: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Method untuk menghapus pertanyaan berdasarkan question_id
    public void deleteQuestion(int questionId) {
        String query = "DELETE FROM Questions WHERE question_id = ?";

        try (Connection conn = koneksi.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, questionId);

            int rowsDeleted = pstmt.executeUpdate();
            if (rowsDeleted > 0) {
                JOptionPane.showMessageDialog(null, "Pertanyaan berhasil dihapus!");
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Gagal menghapus pertanyaan: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    // Method untuk memperbarui pertanyaan yang ada berdasarkan question_id
    // Method untuk memperbarui pertanyaan yang ada berdasarkan question_id, termasuk gambar
    public void updateQuestion(int questionId, String questionText, String optionA, String optionB, String optionC, String optionD, char correctAnswer, String category, File imageFile) throws FileNotFoundException, IOException {
        String query;
        boolean hasImage = (imageFile != null);

        // Jika gambar baru disediakan, sertakan kolom 'image' dalam query
        if (hasImage) {
            query = "UPDATE Questions SET question_text = ?, option_a = ?, option_b = ?, option_c = ?, option_d = ?, correct_answer = ?, category = ?, image = ? WHERE question_id = ?";
        } else {
            query = "UPDATE Questions SET question_text = ?, option_a = ?, option_b = ?, option_c = ?, option_d = ?, correct_answer = ?, category = ? WHERE question_id = ?";
        }

        try (Connection conn = koneksi.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            // Set nilai dari parameter query
            pstmt.setString(1, questionText);
            pstmt.setString(2, optionA);
            pstmt.setString(3, optionB);
            pstmt.setString(4, optionC);
            pstmt.setString(5, optionD);
            pstmt.setString(6, String.valueOf(correctAnswer));
            pstmt.setString(7, category);

            if (hasImage) {
                // Jika ada gambar baru, tambahkan gambar ke prepared statement
                try (FileInputStream fis = new FileInputStream(imageFile)) {
                    pstmt.setBinaryStream(8, fis, (int) imageFile.length());
                }
                pstmt.setInt(9, questionId);
            } else {
                // Jika tidak ada gambar baru, hanya gunakan 8 parameter
                pstmt.setInt(8, questionId);
            }

            // Eksekusi pembaruan
            int rowsUpdated = pstmt.executeUpdate();
            if (rowsUpdated > 0) {
                JOptionPane.showMessageDialog(null, "Pertanyaan berhasil diperbarui!");
            } else {
                JOptionPane.showMessageDialog(null, "Tidak ada perubahan yang disimpan.");
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Gagal memperbarui pertanyaan: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        } catch (FileNotFoundException e) {
            JOptionPane.showMessageDialog(null, "File gambar tidak ditemukan: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }


    private void uploadGambar() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("Image files", "jpg", "jpeg", "png"));

        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();

            try (FileInputStream fis = new FileInputStream(file)) {
                gambarBytes = fis.readAllBytes();

                // Tampilkan gambar di label (optional)
                ImageIcon imageIcon = new ImageIcon(gambarBytes);
                lbl_gambar.setIcon(new ImageIcon(imageIcon.getImage().getScaledInstance(150, 150, Image.SCALE_SMOOTH)));
            } catch (IOException e) {
                JOptionPane.showMessageDialog(this, "Gagal membaca file gambar: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
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

        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        idQuestionField = new javax.swing.JTextField();
        cmbKategori = new javax.swing.JComboBox<>();
        jScrollPane1 = new javax.swing.JScrollPane();
        txtPertanyaan = new javax.swing.JTextArea();
        jLabel5 = new javax.swing.JLabel();
        opsi1Field = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        opsi2Field = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        opsi3Field = new javax.swing.JTextField();
        jLabel8 = new javax.swing.JLabel();
        opsi4Field = new javax.swing.JTextField();
        jLabel9 = new javax.swing.JLabel();
        btnCreate = new javax.swing.JButton();
        btnUpdate = new javax.swing.JButton();
        btnDelete = new javax.swing.JButton();
        jScrollPane2 = new javax.swing.JScrollPane();
        tbl_question = new javax.swing.JTable();
        jawabanField = new javax.swing.JTextField();
        jLabel10 = new javax.swing.JLabel();
        lbl_gambar = new javax.swing.JLabel();
        imgPath = new javax.swing.JLabel();
        btn_uploadimg = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jLabel1.setText("Dashboard Admin");

        jLabel2.setText("Id");

        jLabel3.setText("Kategori");

        jLabel4.setText("Pertanyaan");

        cmbKategori.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        txtPertanyaan.setColumns(20);
        txtPertanyaan.setRows(5);
        jScrollPane1.setViewportView(txtPertanyaan);

        jLabel5.setText("Opsi 1");

        jLabel6.setText("Opsi 2");

        jLabel7.setText("Opsi 3");

        jLabel8.setText("Opsi 4");

        jLabel9.setText("Jawaban");

        btnCreate.setText("Create");
        btnCreate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCreateActionPerformed(evt);
            }
        });

        btnUpdate.setText("Edit");
        btnUpdate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnUpdateActionPerformed(evt);
            }
        });

        btnDelete.setText("Delete");
        btnDelete.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDeleteActionPerformed(evt);
            }
        });

        tbl_question.setModel(new javax.swing.table.DefaultTableModel(
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
        tbl_question.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tbl_questionMouseClicked(evt);
            }
        });
        jScrollPane2.setViewportView(tbl_question);

        jLabel10.setText("Gambar");

        lbl_gambar.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        btn_uploadimg.setLabel("Upload");
        btn_uploadimg.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_uploadimgActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel1)
                .addGap(323, 323, 323))
            .addGroup(layout.createSequentialGroup()
                .addGap(39, 39, 39)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel2)
                    .addComponent(jLabel3)
                    .addComponent(jLabel4)
                    .addComponent(jLabel5)
                    .addComponent(jLabel6)
                    .addComponent(jLabel7)
                    .addComponent(jLabel8)
                    .addComponent(jLabel9)
                    .addComponent(jLabel10))
                .addGap(76, 76, 76)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addComponent(idQuestionField)
                                .addComponent(cmbKategori, 0, 134, Short.MAX_VALUE))
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                .addComponent(jawabanField, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 137, Short.MAX_VALUE)
                                .addComponent(opsi4Field, javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(opsi3Field, javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(opsi2Field, javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(opsi1Field, javax.swing.GroupLayout.Alignment.LEADING)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 55, Short.MAX_VALUE)
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 410, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(29, 29, 29))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(lbl_gambar, javax.swing.GroupLayout.PREFERRED_SIZE, 137, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(imgPath, javax.swing.GroupLayout.PREFERRED_SIZE, 102, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btn_uploadimg, javax.swing.GroupLayout.PREFERRED_SIZE, 1, Short.MAX_VALUE)))
                        .addGap(65, 65, 65)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(btnCreate)
                            .addComponent(btnUpdate)
                            .addComponent(btnDelete))
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(17, 17, 17)
                .addComponent(jLabel1)
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jLabel2)
                                    .addComponent(idQuestionField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(18, 18, 18)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jLabel3)
                                    .addComponent(cmbKategori, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(18, 18, 18)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel4)
                                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(36, 36, 36)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel5)
                                    .addComponent(opsi1Field, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(18, 18, 18)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel6)
                                    .addComponent(opsi2Field, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(18, 18, 18)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel7)
                                    .addComponent(opsi3Field, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(18, 18, 18)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel8)
                                    .addComponent(opsi4Field, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(18, 18, 18)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel9)
                                    .addComponent(jawabanField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 375, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(22, 22, 22)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel10)
                            .addComponent(lbl_gambar, javax.swing.GroupLayout.PREFERRED_SIZE, 147, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(btnCreate)
                        .addGap(36, 36, 36)
                        .addComponent(btnUpdate)
                        .addGap(35, 35, 35)
                        .addComponent(btnDelete)
                        .addGap(21, 21, 21)))
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(imgPath, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btn_uploadimg, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(97, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void tbl_questionMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tbl_questionMouseClicked
        // TODO add your handling code here:
        // Pastikan ada baris yang diklik
        int selectedRow = tbl_question.getSelectedRow();
        if (selectedRow != -1) {
            try (Connection conn = koneksi.getConnection()) {
                // Mengambil data dari kolom yang relevan pada JTable
                String idQuestion = tbl_question.getValueAt(selectedRow, 0).toString();
                String pertanyaan = tbl_question.getValueAt(selectedRow, 1).toString();
                String opsi1 = tbl_question.getValueAt(selectedRow, 2).toString();
                String opsi2 = tbl_question.getValueAt(selectedRow, 3).toString();
                String opsi3 = tbl_question.getValueAt(selectedRow, 4).toString();
                String opsi4 = tbl_question.getValueAt(selectedRow, 5).toString();
                String jawaban = tbl_question.getValueAt(selectedRow, 6).toString();
                String kategori = tbl_question.getValueAt(selectedRow, 7).toString();

                // Menampilkan data teks ke form input
                idQuestionField.setText(idQuestion);
                cmbKategori.setSelectedItem(kategori);
                txtPertanyaan.setText(pertanyaan);
                opsi1Field.setText(opsi1);
                opsi2Field.setText(opsi2);
                opsi3Field.setText(opsi3);
                opsi4Field.setText(opsi4);
                jawabanField.setText(jawaban);

                // Mengambil gambar dari database berdasarkan idQuestion
                String query = "SELECT image FROM Questions WHERE question_id = ?";
                PreparedStatement pstmt = conn.prepareStatement(query);
                pstmt.setInt(1, Integer.parseInt(idQuestion));

                ResultSet rs = pstmt.executeQuery();
                if (rs.next()) {
                    byte[] imgBytes = rs.getBytes("image");
                    if (imgBytes != null) {
                        // Menampilkan gambar pada JLabel
                        ImageIcon icon = new ImageIcon(imgBytes);
                        Image img = icon.getImage().getScaledInstance(lbl_gambar.getWidth(), lbl_gambar.getHeight(), Image.SCALE_SMOOTH);
                        lbl_gambar.setIcon(new ImageIcon(img));
                    } else {
                        // Jika tidak ada gambar, set default atau kosongkan label
                        lbl_gambar.setIcon(null);
                        lbl_gambar.setText("No Image");
                    }
                }
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Gagal mengambil gambar: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }//GEN-LAST:event_tbl_questionMouseClicked

    private void btnDeleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDeleteActionPerformed
        // TODO add your handling code here:
        int idQuestion = Integer.parseInt(idQuestionField.getText());
        deleteQuestion(idQuestion);
    }//GEN-LAST:event_btnDeleteActionPerformed

    private void btnUpdateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnUpdateActionPerformed
        // Mengambil nilai dari setiap field input
        int idQuestion = Integer.parseInt(idQuestionField.getText());
        String category = cmbKategori.getSelectedItem().toString();
        String questionText = txtPertanyaan.getText();
        String optionA = opsi1Field.getText();
        String optionB = opsi2Field.getText();
        String optionC = opsi3Field.getText();
        String optionD = opsi4Field.getText();
        String jawaban = jawabanField.getText().trim().toUpperCase();

        // Validasi bahwa jawaban hanya boleh berupa satu karakter ('A', 'B', 'C', atau 'D')
        if (jawaban.length() != 1 || !"ABCD".contains(jawaban)) {
            JOptionPane.showMessageDialog(this, "Jawaban harus berupa satu karakter: A, B, C, atau D", "Input Error", JOptionPane.ERROR_MESSAGE);
            return; // Keluar dari method jika input tidak valid
        }
        // Pilih file gambar (opsional)
        File imageFile = null;
        JFileChooser fileChooser = new JFileChooser();
        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            imageFile = fileChooser.getSelectedFile();
        }
        try {
            // Panggil method updateQuestion dengan parameter yang telah disiapkan
            updateQuestion(idQuestion, questionText, optionA, optionB, optionC, optionD, jawaban.charAt(0), category, imageFile);
        } catch (IOException ex) {
            Logger.getLogger(AdminDashboard.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_btnUpdateActionPerformed

    private void btnCreateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCreateActionPerformed
        // Mengambil nilai dari setiap field input       
        String category = cmbKategori.getSelectedItem().toString();
        String questionText = txtPertanyaan.getText();
        String optionA = opsi1Field.getText();
        String optionB = opsi2Field.getText();
        String optionC = opsi3Field.getText();
        String optionD = opsi4Field.getText();
        String jawaban = jawabanField.getText().trim().toUpperCase();

        // Validasi bahwa jawaban hanya boleh berupa satu karakter ('A', 'B', 'C', atau 'D')
        if (jawaban.length() != 1 || !"ABCD".contains(jawaban)) {
            JOptionPane.showMessageDialog(this, "Jawaban harus berupa satu karakter: A, B, C, atau D", "Input Error", JOptionPane.ERROR_MESSAGE);
            return; // Keluar dari method jika input tidak valid
        }
        // Panggil method createQuestion dengan parameter jawaban yang valid
        createQuestion(questionText, optionA, optionB, optionC, optionD, jawaban, category, gambarBytes);

    }//GEN-LAST:event_btnCreateActionPerformed

    private void btn_uploadimgActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_uploadimgActionPerformed
        // TODO add your handling code here:
        uploadGambar();
    }//GEN-LAST:event_btn_uploadimgActionPerformed

    public void createQuestion(String questionText, String optionA, String optionB, String optionC, String optionD, String jawaban, String category, byte[] image) {
        try (Connection conn = koneksi.getConnection()) {
            String query = "INSERT INTO questions (question_text, option_a, option_b, option_c, option_d, correct_answer, category, image) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement pstmt = conn.prepareStatement(query);

            pstmt.setString(1, questionText);
            pstmt.setString(2, optionA);
            pstmt.setString(3, optionB);
            pstmt.setString(4, optionC);
            pstmt.setString(5, optionD);
            pstmt.setString(6, jawaban);  // Menyimpan jawaban yang benar
            pstmt.setString(7, category);
            // Jika gambar tidak diupload, set null
            if (image != null) {
                pstmt.setBytes(8, image);
            } else {
                pstmt.setNull(8, Types.BLOB);
            }
            pstmt.executeUpdate();
            JOptionPane.showMessageDialog(this, "Pertanyaan berhasil ditambahkan!", "Success", JOptionPane.INFORMATION_MESSAGE);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Gagal menambahkan pertanyaan: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }


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
            java.util.logging.Logger.getLogger(AdminDashboard.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(AdminDashboard.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(AdminDashboard.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(AdminDashboard.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new AdminDashboard().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnCreate;
    private javax.swing.JButton btnDelete;
    private javax.swing.JButton btnUpdate;
    private javax.swing.JButton btn_uploadimg;
    private javax.swing.JComboBox<String> cmbKategori;
    private javax.swing.JTextField idQuestionField;
    private javax.swing.JLabel imgPath;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTextField jawabanField;
    private javax.swing.JLabel lbl_gambar;
    private javax.swing.JTextField opsi1Field;
    private javax.swing.JTextField opsi2Field;
    private javax.swing.JTextField opsi3Field;
    private javax.swing.JTextField opsi4Field;
    private javax.swing.JTable tbl_question;
    private javax.swing.JTextArea txtPertanyaan;
    // End of variables declaration//GEN-END:variables
}
