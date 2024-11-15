/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package Admin;

import java.awt.Image;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.*;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author faza
 */
public class FormMusik extends javax.swing.JPanel {
    private byte[] gambarBytes = null;
    /**
     * Creates new form FormMakanan
     */
    public FormMusik() {
        initComponents();
        idQuestionField.setEditable(false);
        btnTambahEdit.setText("Tambah");
        btnHapus.setVisible(false);
        readQuestionsToTable(jTable1);
        loadCategories();
    }

    // Method untuk upload gambar
    private void uploadGambar() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new FileNameExtensionFilter("Image files", "jpg", "jpeg", "png"));

        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            try (FileInputStream fis = new FileInputStream(file)) {
                gambarBytes = fis.readAllBytes();
                ImageIcon imageIcon = new ImageIcon(gambarBytes);
                lbl_gambar.setIcon(new ImageIcon(imageIcon.getImage().getScaledInstance(150, 150, Image.SCALE_SMOOTH)));
            } catch (IOException e) {
                JOptionPane.showMessageDialog(this, "Gagal membaca file gambar: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    // Method untuk memuat kategori dari database ke JComboBox
    private void loadCategories() {
        try (Connection conn = koneksi.getConnection()) {
            String query = "SELECT DISTINCT category FROM questions";
            PreparedStatement pstmt = conn.prepareStatement(query);
            ResultSet rs = pstmt.executeQuery();
            
            cmbKategori.removeAllItems();
            
            while (rs.next()) {
                cmbKategori.addItem(rs.getString("category"));
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Gagal memuat kategori: " + e.getMessage());
        }
    }
    
    // Method untuk membaca semua pertanyaan dari tabel ke JTable
    public void readQuestionsToTable(JTable table) {
        DefaultTableModel model = new DefaultTableModel();
        model.addColumn("ID");
        model.addColumn("Question");
        model.addColumn("Option A");
        model.addColumn("Option B");
        model.addColumn("Option C");
        model.addColumn("Option D");
        model.addColumn("Correct Answer");
        model.addColumn("Category");

        try (Connection conn = koneksi.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM questions WHERE category = 'Alat Musik'")) {

            model.setRowCount(0);
            while (rs.next()) {
                model.addRow(new Object[]{
                    rs.getInt("question_id"),
                    rs.getString("question_text"),
                    rs.getString("option_a"),
                    rs.getString("option_b"),
                    rs.getString("option_c"),
                    rs.getString("option_d"),
                    rs.getString("correct_answer"),
                    rs.getString("category")
                });
            }
            table.setModel(model);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Gagal membaca data pertanyaan: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    public void createQuestion(String questionText, String optionA, String optionB, String optionC, String optionD, String category, byte[] image, String correctAnswer) {
        String query = "INSERT INTO questions (question_text, option_a, option_b, option_c, option_d, correct_answer, category, image) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = koneksi.getConnection(); PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, questionText);
            pstmt.setString(2, optionA);
            pstmt.setString(3, optionB);
            pstmt.setString(4, optionC);
            pstmt.setString(5, optionD);
            pstmt.setString(6, correctAnswer);
            pstmt.setString(7, category);
            if (image != null) {
                pstmt.setBytes(8, image);
            } else {
                pstmt.setNull(8, Types.BLOB);
            }

            int rowsInserted = pstmt.executeUpdate();
            if (rowsInserted > 0) {
                JOptionPane.showMessageDialog(null, "Pertanyaan berhasil ditambahkan!");
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Gagal menambahkan pertanyaan: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
        readQuestionsToTable(jTable1);
    }
    
    // Method untuk memperbarui pertanyaan dengan JComboBox
    public void updateQuestion(int questionId, String questionText, String optionA, String optionB, String optionC, String optionD, String correctAnswer, String category, byte[] image) {
        // Mengambil jawaban dari JComboBox
        correctAnswer = (String) answerComboBox.getSelectedItem(); // Jawaban selalu valid (A, B, C, D)

        String query = "UPDATE questions SET question_text = ?, option_a = ?, option_b = ?, option_c = ?, option_d = ?, correct_answer = ?, category = ?, image = ? WHERE question_id = ?";

        try (Connection conn = koneksi.getConnection(); PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, questionText);
            pstmt.setString(2, optionA);
            pstmt.setString(3, optionB);
            pstmt.setString(4, optionC);
            pstmt.setString(5, optionD);
            pstmt.setString(6, correctAnswer); // Langsung dari JComboBox
            pstmt.setString(7, category);

            // Menyimpan gambar atau null jika tidak ada gambar
            if (image != null) {
                pstmt.setBytes(8, image);
            } else {
                pstmt.setNull(8, Types.BLOB);
            }
            pstmt.setInt(9, questionId);

            int rowsUpdated = pstmt.executeUpdate();
            if (rowsUpdated > 0) {
                JOptionPane.showMessageDialog(null, "Pertanyaan berhasil diperbarui!");
            } else {
                JOptionPane.showMessageDialog(null, "Tidak ada perubahan yang disimpan.");
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Gagal memperbarui pertanyaan: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
        readQuestionsToTable(jTable1);
    }
    private void clearForm() {
        txtPertanyaan.setText("");
        opsi1Field.setText("");
        opsi2Field.setText("");
        opsi3Field.setText("");
        opsi4Field.setText("");
        cmbKategori.setSelectedIndex(0);
        answerComboBox.setSelectedIndex(0);
        gambarBytes = null;
        lbl_gambar.setIcon(null);
        idQuestionField.setText(""); // Kosongkan ID pertanyaan
    }
    private void fillFormWithSelectedRowData(int selectedRow) {
        if (selectedRow != -1) {
            // Mendapatkan nilai dari JTable berdasarkan kolom
            String idQuestion = jTable1.getValueAt(selectedRow, 0).toString();
            String pertanyaan = jTable1.getValueAt(selectedRow, 1).toString();
            String opsi1 = jTable1.getValueAt(selectedRow, 2).toString();
            String opsi2 = jTable1.getValueAt(selectedRow, 3).toString();
            String opsi3 = jTable1.getValueAt(selectedRow, 4).toString();
            String opsi4 = jTable1.getValueAt(selectedRow, 5).toString();
            String jawaban = jTable1.getValueAt(selectedRow, 6).toString();
            String kategori = jTable1.getValueAt(selectedRow, 7).toString();

            // Menampilkan data ke dalam form
            idQuestionField.setText(idQuestion);
            txtPertanyaan.setText(pertanyaan);
            opsi1Field.setText(opsi1);
            opsi2Field.setText(opsi2);
            opsi3Field.setText(opsi3);
            opsi4Field.setText(opsi4);
            answerComboBox.setSelectedItem(jawaban);
            cmbKategori.setSelectedItem(kategori);

            // Jika Anda menyimpan gambar dalam database, Anda bisa menambahkan kode ini:
            try (Connection conn = koneksi.getConnection()) {
                String query = "SELECT image FROM questions WHERE question_id = ?";
                PreparedStatement pstmt = conn.prepareStatement(query);
                pstmt.setInt(1, Integer.parseInt(idQuestion));
                ResultSet rs = pstmt.executeQuery();

                if (rs.next()) {
                    byte[] imageBytes = rs.getBytes("image");
                    if (imageBytes != null) {
                        ImageIcon imageIcon = new ImageIcon(imageBytes);
                        lbl_gambar.setIcon(new ImageIcon(imageIcon.getImage().getScaledInstance(150, 150, Image.SCALE_SMOOTH)));
                        gambarBytes = imageBytes; // Simpan ke variabel untuk digunakan kembali
                    } else {
                        lbl_gambar.setIcon(null);
                        gambarBytes = null;
                    }
                }
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Gagal memuat gambar: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
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

        jPanel1 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jLabel1 = new javax.swing.JLabel();
        btnTambahEdit = new javax.swing.JButton();
        btnHapus = new javax.swing.JButton();
        tambahBarang = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        btnSimpan = new javax.swing.JButton();
        btnBatal = new javax.swing.JButton();
        jPanel3 = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        idQuestionField = new javax.swing.JTextField();
        cmbKategori = new javax.swing.JComboBox<>();
        jScrollPane3 = new javax.swing.JScrollPane();
        txtPertanyaan = new javax.swing.JTextArea();
        jLabel6 = new javax.swing.JLabel();
        opsi1Field = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        lbl_gambar = new javax.swing.JLabel();
        imgPath = new javax.swing.JLabel();
        btn_uploadimg = new javax.swing.JButton();
        opsi2Field = new javax.swing.JTextField();
        jLabel8 = new javax.swing.JLabel();
        opsi3Field = new javax.swing.JTextField();
        jLabel9 = new javax.swing.JLabel();
        opsi4Field = new javax.swing.JTextField();
        jLabel11 = new javax.swing.JLabel();
        answerComboBox = new javax.swing.JComboBox<>();

        setLayout(new java.awt.CardLayout());

        jPanel1.setLayout(new java.awt.CardLayout());

        jPanel2.setBackground(new java.awt.Color(248, 244, 225));

        jTable1.setFont(new java.awt.Font("Poppins Medium", 0, 12)); // NOI18N
        jTable1.setModel(new javax.swing.table.DefaultTableModel(
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
        jTable1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTable1MouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(jTable1);

        jLabel1.setFont(new java.awt.Font("Poppins SemiBold", 0, 18)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(84, 51, 16));
        jLabel1.setText("Daftar Pertanyaan");

        btnTambahEdit.setBackground(new java.awt.Color(84, 51, 16));
        btnTambahEdit.setFont(new java.awt.Font("Poppins Medium", 0, 14)); // NOI18N
        btnTambahEdit.setForeground(new java.awt.Color(255, 255, 255));
        btnTambahEdit.setText("Tambah");
        btnTambahEdit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnTambahEditActionPerformed(evt);
            }
        });

        btnHapus.setBackground(new java.awt.Color(84, 51, 16));
        btnHapus.setFont(new java.awt.Font("Poppins Medium", 0, 14)); // NOI18N
        btnHapus.setForeground(new java.awt.Color(255, 255, 255));
        btnHapus.setText("Hapus");
        btnHapus.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnHapusActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 585, Short.MAX_VALUE)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel1)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(btnTambahEdit, javax.swing.GroupLayout.PREFERRED_SIZE, 106, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(28, 28, 28)
                                .addComponent(btnHapus, javax.swing.GroupLayout.PREFERRED_SIZE, 105, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(btnTambahEdit, javax.swing.GroupLayout.DEFAULT_SIZE, 43, Short.MAX_VALUE)
                    .addComponent(btnHapus, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(18, 18, 18)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 511, Short.MAX_VALUE)
                .addGap(61, 61, 61))
        );

        jPanel1.add(jPanel2, "card2");

        tambahBarang.setBackground(new java.awt.Color(248, 244, 225));

        jLabel2.setFont(new java.awt.Font("Poppins SemiBold", 0, 18)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(84, 51, 16));
        jLabel2.setText("Tambah Pertanyaan");

        btnSimpan.setBackground(new java.awt.Color(84, 51, 16));
        btnSimpan.setFont(new java.awt.Font("Poppins", 0, 12)); // NOI18N
        btnSimpan.setForeground(new java.awt.Color(255, 255, 255));
        btnSimpan.setText("Simpan");
        btnSimpan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSimpanActionPerformed(evt);
            }
        });

        btnBatal.setBackground(new java.awt.Color(84, 51, 16));
        btnBatal.setFont(new java.awt.Font("Poppins", 0, 12)); // NOI18N
        btnBatal.setForeground(new java.awt.Color(255, 255, 255));
        btnBatal.setText("Batal");
        btnBatal.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBatalActionPerformed(evt);
            }
        });

        jPanel3.setBackground(new java.awt.Color(248, 244, 225));

        jLabel3.setFont(new java.awt.Font("Poppins Medium", 0, 14)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(84, 51, 16));
        jLabel3.setText("Id");

        jLabel4.setFont(new java.awt.Font("Poppins Medium", 0, 14)); // NOI18N
        jLabel4.setForeground(new java.awt.Color(84, 51, 16));
        jLabel4.setText("Kategori");

        jLabel5.setFont(new java.awt.Font("Poppins Medium", 0, 14)); // NOI18N
        jLabel5.setForeground(new java.awt.Color(84, 51, 16));
        jLabel5.setText("Pertanyaan");

        idQuestionField.setFont(new java.awt.Font("Poppins Medium", 0, 14)); // NOI18N
        idQuestionField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                idQuestionFieldActionPerformed(evt);
            }
        });

        cmbKategori.setFont(new java.awt.Font("Poppins Medium", 0, 14)); // NOI18N
        cmbKategori.setForeground(new java.awt.Color(84, 51, 16));
        cmbKategori.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        txtPertanyaan.setColumns(20);
        txtPertanyaan.setFont(new java.awt.Font("Poppins Medium", 0, 12)); // NOI18N
        txtPertanyaan.setRows(5);
        jScrollPane3.setViewportView(txtPertanyaan);

        jLabel6.setFont(new java.awt.Font("Poppins Medium", 0, 14)); // NOI18N
        jLabel6.setForeground(new java.awt.Color(84, 51, 16));
        jLabel6.setText("Opsi 1");

        opsi1Field.setFont(new java.awt.Font("Poppins Medium", 0, 14)); // NOI18N

        jLabel7.setFont(new java.awt.Font("Poppins Medium", 0, 14)); // NOI18N
        jLabel7.setForeground(new java.awt.Color(84, 51, 16));
        jLabel7.setText("Opsi 2");

        jLabel10.setFont(new java.awt.Font("Poppins Medium", 0, 14)); // NOI18N
        jLabel10.setForeground(new java.awt.Color(84, 51, 16));
        jLabel10.setText("Gambar");

        lbl_gambar.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        btn_uploadimg.setBackground(new java.awt.Color(84, 51, 16));
        btn_uploadimg.setFont(new java.awt.Font("Poppins Medium", 0, 14)); // NOI18N
        btn_uploadimg.setForeground(new java.awt.Color(255, 255, 255));
        btn_uploadimg.setLabel("Upload");
        btn_uploadimg.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_uploadimgActionPerformed(evt);
            }
        });

        opsi2Field.setFont(new java.awt.Font("Poppins Medium", 0, 14)); // NOI18N

        jLabel8.setFont(new java.awt.Font("Poppins Medium", 0, 14)); // NOI18N
        jLabel8.setForeground(new java.awt.Color(84, 51, 16));
        jLabel8.setText("Opsi 3");

        opsi3Field.setFont(new java.awt.Font("Poppins Medium", 0, 14)); // NOI18N

        jLabel9.setFont(new java.awt.Font("Poppins Medium", 0, 14)); // NOI18N
        jLabel9.setForeground(new java.awt.Color(84, 51, 16));
        jLabel9.setText("Opsi 4");

        opsi4Field.setFont(new java.awt.Font("Poppins Medium", 0, 14)); // NOI18N

        jLabel11.setFont(new java.awt.Font("Poppins Medium", 0, 14)); // NOI18N
        jLabel11.setForeground(new java.awt.Color(84, 51, 16));
        jLabel11.setText("Jawaban");

        answerComboBox.setFont(new java.awt.Font("Poppins Medium", 0, 14)); // NOI18N
        answerComboBox.setForeground(new java.awt.Color(84, 51, 16));
        answerComboBox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "A", "B", "C", "D" }));

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(19, 19, 19)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel3)
                    .addComponent(jLabel4)
                    .addComponent(jLabel5)
                    .addComponent(jLabel6)
                    .addComponent(jLabel7)
                    .addComponent(jLabel8)
                    .addComponent(jLabel9)
                    .addComponent(jLabel11))
                .addGap(76, 76, 76)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addComponent(idQuestionField)
                                .addComponent(cmbKategori, javax.swing.GroupLayout.PREFERRED_SIZE, 134, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 190, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                .addComponent(opsi4Field, javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(opsi3Field, javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(opsi2Field, javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(opsi1Field, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 137, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(52, 52, 52)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(imgPath, javax.swing.GroupLayout.PREFERRED_SIZE, 102, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(lbl_gambar, javax.swing.GroupLayout.PREFERRED_SIZE, 137, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel10)
                            .addComponent(btn_uploadimg, javax.swing.GroupLayout.PREFERRED_SIZE, 137, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addComponent(answerComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, 137, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(31, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel3)
                            .addComponent(idQuestionField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel10))
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addGap(18, 18, 18)
                                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jLabel4)
                                    .addComponent(cmbKategori, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(18, 18, 18)
                                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel5)
                                    .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addGap(9, 9, 9)
                                .addComponent(lbl_gambar, javax.swing.GroupLayout.PREFERRED_SIZE, 147, javax.swing.GroupLayout.PREFERRED_SIZE))))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGap(18, 18, 18)
                        .addComponent(imgPath, javax.swing.GroupLayout.PREFERRED_SIZE, 21, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGap(24, 24, 24)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel6)
                            .addComponent(opsi1Field, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(21, 21, 21))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                        .addComponent(btn_uploadimg, javax.swing.GroupLayout.PREFERRED_SIZE, 21, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)))
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel7)
                    .addComponent(opsi2Field, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel8)
                    .addComponent(opsi3Field, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel9)
                    .addComponent(opsi4Field, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel11)
                    .addComponent(answerComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(208, 208, 208))
        );

        javax.swing.GroupLayout tambahBarangLayout = new javax.swing.GroupLayout(tambahBarang);
        tambahBarang.setLayout(tambahBarangLayout);
        tambahBarangLayout.setHorizontalGroup(
            tambahBarangLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(tambahBarangLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(tambahBarangLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(tambahBarangLayout.createSequentialGroup()
                        .addGroup(tambahBarangLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel2)
                            .addGroup(tambahBarangLayout.createSequentialGroup()
                                .addComponent(btnSimpan, javax.swing.GroupLayout.PREFERRED_SIZE, 84, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btnBatal, javax.swing.GroupLayout.PREFERRED_SIZE, 84, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        tambahBarangLayout.setVerticalGroup(
            tambahBarangLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, tambahBarangLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(tambahBarangLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnBatal, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnSimpan, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, 500, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(110, 110, 110))
        );

        jPanel1.add(tambahBarang, "card2");

        add(jPanel1, "card2");
    }// </editor-fold>//GEN-END:initComponents

    private void btn_uploadimgActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_uploadimgActionPerformed
        // TODO add your handling code here:
        uploadGambar();
    }//GEN-LAST:event_btn_uploadimgActionPerformed

    private void btnSimpanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSimpanActionPerformed
        String category = cmbKategori.getSelectedItem().toString();
        String questionText = txtPertanyaan.getText();
        String optionA = opsi1Field.getText();
        String optionB = opsi2Field.getText();
        String optionC = opsi3Field.getText();
        String optionD = opsi4Field.getText();
        String correctAnswer = (String) answerComboBox.getSelectedItem();

        // Validasi untuk memastikan semua field diisi
        if (questionText.isEmpty() || optionA.isEmpty() || optionB.isEmpty() ||
            optionC.isEmpty() || optionD.isEmpty() || correctAnswer == null) {
            JOptionPane.showMessageDialog(this, "Harap lengkapi semua kolom!", "Input Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (btnSimpan.getText().equals("Simpan")) {
            // Tambahkan pertanyaan baru ke database
            createQuestion(questionText, optionA, optionB, optionC, optionD, category, gambarBytes, correctAnswer);
        } else if (btnSimpan.getText().equals("Update")) {
            // Update pertanyaan yang ada di database
            int idQuestion = Integer.parseInt(idQuestionField.getText());
            updateQuestion(idQuestion, questionText, optionA, optionB, optionC, optionD, correctAnswer, category, gambarBytes);
        }

        // Refresh JTable dan kembali ke JPanel tabel
        readQuestionsToTable(jTable1);
        jPanel1.removeAll();
        jPanel1.repaint();
        jPanel1.revalidate();

        jPanel1.add(jPanel2);
        jPanel1.repaint();
        jPanel1.revalidate();
    }//GEN-LAST:event_btnSimpanActionPerformed

    private void btnTambahEditActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnTambahEditActionPerformed
        if (btnTambahEdit.getText().equals("Tambah")) {
            // Bersihkan form sebelum tampil
            clearForm();
            jPanel1.removeAll();
            jPanel1.repaint();
            jPanel1.revalidate();

            jPanel1.add(tambahBarang);
            jPanel1.repaint();
            jPanel1.revalidate();
            btnSimpan.setText("Simpan"); // Ubah teks tombol simpan
        } else if (btnTambahEdit.getText().equals("Edit")) {
            // Isi form dengan data yang dipilih dari JTable
            int selectedRow = jTable1.getSelectedRow();
            if (selectedRow != -1) {
                fillFormWithSelectedRowData(selectedRow);
                jPanel1.removeAll();
                jPanel1.repaint();
                jPanel1.revalidate();

                jPanel1.add(tambahBarang);
                jPanel1.repaint();
                jPanel1.revalidate();
                btnSimpan.setText("Update");
            }
        }
    }//GEN-LAST:event_btnTambahEditActionPerformed

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
        readQuestionsToTable(jTable1);
    }
    
    private void btnBatalActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBatalActionPerformed
        // TODO add your handling code here:
        jPanel1.removeAll();
        jPanel1.repaint();
        jPanel1.revalidate();
        
        jPanel1.add(jPanel2);
        jPanel1.repaint();
        jPanel1.revalidate();
    }//GEN-LAST:event_btnBatalActionPerformed

    private void jTable1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTable1MouseClicked
        int selectedRow = jTable1.getSelectedRow();
        if (selectedRow != -1) {
            btnTambahEdit.setText("Edit");
            btnHapus.setVisible(true);

            // Menampilkan data yang diklik ke form
            String idQuestion = jTable1.getValueAt(selectedRow, 0).toString();
            String pertanyaan = jTable1.getValueAt(selectedRow, 1).toString();
            String opsi1 = jTable1.getValueAt(selectedRow, 2).toString();
            String opsi2 = jTable1.getValueAt(selectedRow, 3).toString();
            String opsi3 = jTable1.getValueAt(selectedRow, 4).toString();
            String opsi4 = jTable1.getValueAt(selectedRow, 5).toString();
            String jawaban = jTable1.getValueAt(selectedRow, 6).toString();
            String kategori = jTable1.getValueAt(selectedRow, 7).toString();

            // Menampilkan data ke form input
            idQuestionField.setText(idQuestion);
            txtPertanyaan.setText(pertanyaan);
            opsi1Field.setText(opsi1);
            opsi2Field.setText(opsi2);
            opsi3Field.setText(opsi3);
            opsi4Field.setText(opsi4);
            answerComboBox.setSelectedItem(jawaban);
            cmbKategori.setSelectedItem(kategori);
        }
    }//GEN-LAST:event_jTable1MouseClicked

    private void btnHapusActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnHapusActionPerformed
        // TODO add your handling code here:
        int idQuestion = Integer.parseInt(idQuestionField.getText());
        deleteQuestion(idQuestion);
    }//GEN-LAST:event_btnHapusActionPerformed

    private void idQuestionFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_idQuestionFieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_idQuestionFieldActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox<String> answerComboBox;
    private javax.swing.JButton btnBatal;
    private javax.swing.JButton btnHapus;
    private javax.swing.JButton btnSimpan;
    private javax.swing.JButton btnTambahEdit;
    private javax.swing.JButton btn_uploadimg;
    private javax.swing.JComboBox<String> cmbKategori;
    private javax.swing.JTextField idQuestionField;
    private javax.swing.JLabel imgPath;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JTable jTable1;
    private javax.swing.JLabel lbl_gambar;
    private javax.swing.JTextField opsi1Field;
    private javax.swing.JTextField opsi2Field;
    private javax.swing.JTextField opsi3Field;
    private javax.swing.JTextField opsi4Field;
    private javax.swing.JPanel tambahBarang;
    private javax.swing.JTextArea txtPertanyaan;
    // End of variables declaration//GEN-END:variables
}
