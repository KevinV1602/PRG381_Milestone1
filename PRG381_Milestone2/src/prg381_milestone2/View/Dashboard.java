/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package prg381_milestone2.View;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.PageAttributes;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;

import prg381_milestone2.Controller.AppointmentController;
import prg381_milestone2.Model.Appointment;
import prg381_milestone2.Controller.CounselorController;
import prg381_milestone2.Model.Counselor;
import javax.swing.JOptionPane;
import java.sql.Date; 
import java.util.List; 
import javax.swing.table.DefaultTableModel; 
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener; 
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import prg381_milestone2.Controller.FeedbackController;
import prg381_milestone2.Model.Feedback;

/**
 *
 * @author squis
 */
public class Dashboard extends javax.swing.JFrame {
    
    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(Dashboard.class.getName());
    
     private  AppointmentController appointmentController;
     private  CounselorController counselorController;
     
     private void loadCounselorsIntoTable() {
        DefaultTableModel model = (DefaultTableModel) tblCounselor.getModel();
        model.setRowCount(0); // Clear existing rows

        if (counselorController == null) {
            logger.warning("CounselorController is null, cannot load counselors into table.");
            return; // Exit if controller isn't initialized
        }

        try {
            List<Counselor> counselors = counselorController.getAllCounselors();

            for (Counselor c : counselors) {
                model.addRow(new Object[]{
                    c.getId(), // Assuming getId() returns the counselor_id
                    c.getName(),
                    c.getSpecialization(),
                    c.getAvailability()
                });
            }
        } catch (SQLException ex) { // Use SQLException for database errors
            JOptionPane.showMessageDialog(this, "Error loading counselors: " + ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
            logger.log(Level.SEVERE, "Error loading counselors", ex);
        } catch (Exception ex) { // Catch any other unexpected exceptions
            JOptionPane.showMessageDialog(this, "An unexpected error occurred while loading counselors: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            logger.log(Level.SEVERE, "Unexpected error loading counselors", ex);
        }
    }
    private void loadAppointmentsIntoTable() {
        DefaultTableModel model = (DefaultTableModel) tblAppointment.getModel();
        model.setRowCount(0);

        try {
            List<Appointment> appointments = appointmentController.getAllAppointments();

            for (Appointment appt : appointments) {
                model.addRow(new Object[]{
                    appt.getId(),
                    appt.getStudentId(),
                    appt.getStudentName(),
                    appt.getCounselorName(),
                    appt.getAppointmentDate().toString(),
                    appt.getAppointmentTime(),
                    appt.getStatus()
                });
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error loading appointments: " + ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }
    
    private void loadAllFeedback() {
    try {
        FeedbackController controller = new FeedbackController();
        List<Feedback> list = controller.getAllFeedback();
        DefaultTableModel model = (DefaultTableModel) tblStudents.getModel();
        model.setRowCount(0); // Clear existing rows

        for (Feedback fb : list) {
            model.addRow(new Object[]{
                fb.getStudentId(),
                fb.getCounselor(),
                fb.getRating(),
                fb.getComments()
            });
        }
    } catch (Exception e) {
        e.printStackTrace();
        JOptionPane.showMessageDialog(this, "Could not load feedback.", "Error", JOptionPane.ERROR_MESSAGE);
    }
}


    /**
     * Creates new form Dashboard
     */
    public Dashboard() {
        initComponents();
        
         setTitle("Wellness Management Dashboard");
        setSize(1050, 700);     
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        btnSubmitFeedback.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
        String studentId = txtStudentID.getText().trim();
        String counselor = cmbCounselor1.getSelectedItem().toString();
        int rating = (int) spnRating.getValue();
        String comments = txtComments.getText().trim();

        if (studentId.isEmpty() || counselor.isEmpty() || comments.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Please fill in all fields.", "Input Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Feedback feedback = new Feedback(studentId, counselor, rating, comments);
        FeedbackController controller = new FeedbackController();

        try {
            controller.submitFeedback(feedback);
            JOptionPane.showMessageDialog(null, "Feedback submitted successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);

            // Clear fields
            txtStudentID.setText("");
            txtComments.setText("");
            spnRating.setValue(1);
            cmbCounselor1.setSelectedIndex(0);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Failed to submit feedback.\n" + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
});
        btnViewAll.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
        loadAllFeedback();
    }  
});
        btnSearchAction.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
        String studentId = txtSearchbyID.getText().trim();
        if (studentId.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Enter a Student ID to search.", "Input Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            FeedbackController controller = new FeedbackController();
            List<Feedback> list = controller.searchFeedbackByStudentId(studentId);
            DefaultTableModel model = (DefaultTableModel) tblStudents.getModel();
            model.setRowCount(0);

            for (Feedback fb : list) {
                model.addRow(new Object[]{
                    fb.getStudentId(),
                    fb.getCounselor(),
                    fb.getRating(),
                    fb.getComments()
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(Dashboard.this, "Search failed.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
});
        
        btnEditFeedback.addActionListener(new java.awt.event.ActionListener() {
    public void actionPerformed(java.awt.event.ActionEvent evt) {
        int selectedRow = tblStudents.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(null, "Please select a row to edit.");
            return;
        }

        // Get updated values from input fields
        String studentId = txtStudentID.getText().trim();
        String counselor = cmbCounselor1.getSelectedItem().toString();
        int rating;
        try {
            rating = Integer.parseInt(spnRating.getValue().toString());
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(null, "Invalid rating. Please enter a number.");
            return;
        }
        String comments = txtComments.getText().trim();

        if (studentId.isEmpty() || comments.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Student ID and comments are required.");
            return;
        }

        try {
            FeedbackController controller = new FeedbackController();
            
            // Create a feedback object with updated data
            Feedback updatedFeedback = new Feedback(studentId, counselor, rating, comments);
            
            // Call update function in the controller
            boolean success = controller.updateFeedback(updatedFeedback);

            if (success) {
                JOptionPane.showMessageDialog(null, "Feedback updated successfully.");
                loadAllFeedback(); // Refresh the table
            } else {
                JOptionPane.showMessageDialog(null, "No matching feedback record to update.");
            }

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Update failed: " + e.getMessage());
        }
    }
});

        
        btnDeleteFeedback.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
        int selectedRow = tblStudents.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(null, "Please select a row to delete.");
            return;
        }

        // Change: Get Student ID from the selected row
        String studentId = tblStudents.getValueAt(selectedRow, 0).toString(); // Index 0 must be Student ID

        int confirm = JOptionPane.showConfirmDialog(null, "Are you sure you want to delete this feedback?", "Confirm", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                FeedbackController controller = new FeedbackController();

                // Change: Call the method that deletes by studentId
                controller.deleteFeedback(studentId);

                JOptionPane.showMessageDialog(null, "Feedback deleted.");
                loadAllFeedback(); // Refresh the table
            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(null, "Failed to delete feedback.");
            }
        }
    }
});
        
        cmbCounselor.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Select Counselor", "John Doe", "Jane Smith", "Emily White", "Dr. Alex Lee" }));
        cmbStatus.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Pending", "Confirmed", "Cancelled", "Completed" }));
        cmbTime.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Select Time", "09:00 AM", "10:00 AM", "11:00 AM", "01:00 PM", "02:00 PM", "03:00 PM", "04:00 PM" }));
        
        cmbCounselor1.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Select Counselor", "John Doe", "Jane Smith", "Emily White", "Dr. Alex Lee" }));

        cmbCounselorAvailability.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] {
        "Select Availability",
        "Monday - Friday (Full-time)",
        "Monday, Wednesday, Friday (Part-time)",
        "Tuesday, Thursday (Part-time)",
        "Weekends Only",
        "Flexible (by appointment)"
    }));
        tblAppointment.setModel(new DefaultTableModel(
            new Object [][] {},
            new String [] {
                "ID", "Student ID", "Student Name", "Counselor", "Date", "Time", "Status"
            }
        ) {
            @Override // Override annotation here
            public boolean isCellEditable(int rowIndex, int columnIndex) {
                boolean[] canEdit = new boolean [] { // Declaration moved inside method
                    false, false, false, false, false, false, false
                };
                return canEdit [columnIndex];
            }
        });

        tblCounselor.setModel(new DefaultTableModel(
            new Object [][] {},
            new String [] {
                "ID", "Name", "Specialization", "Availability"
            }
        ) {
            @Override // Override annotation here
            public boolean isCellEditable(int rowIndex, int columnIndex) {
                boolean[] canEdit = new boolean [] { // Declaration moved inside method
                    false, false, false, false
                };
                return canEdit [columnIndex];
            }
        });
        
         // Set up button texts and add action listeners
        btnBookAppointment.setText("Book Appointment"); 
        btnBookAppointment.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        btnSearch.setText("Search");
        btnSearch.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        btnUpdate.setText("Update");
        btnUpdate.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });

        btnCancel.setText("Cancel");
        btnCancel.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton4ActionPerformed(evt);
            }
        });

        btnRefresh.setText("Refresh");
        btnRefresh.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton5ActionPerformed(evt);
            }
        });
        
        JScrollPane.setViewportView(tblAppointment);
        
        appointmentController = new AppointmentController();
        
         try {
        counselorController = new CounselorController();
        logger.info("CounselorController initialized successfully.");
    } catch (SQLException e) {
        // Log the full stack trace to help diagnose the Derby issue
        logger.log(Level.SEVERE, "Failed to initialize Counselor Controller due to SQL error. Counselor features disabled.", e);
        JOptionPane.showMessageDialog(this, "Failed to initialize Counselor Controller (Database Error): " + e.getMessage() + "\nCounselor features might be unavailable.", "Database Error", JOptionPane.ERROR_MESSAGE);
        counselorController = null; // Explicitly set to null if initialization fails
        // Optionally disable UI elements related to counselors if initialization fails
        if (btnAddCounselor != null) btnAddCounselor.setEnabled(false);
        if (btnUpdateCounselor != null) btnUpdateCounselor.setEnabled(false);
        if (btnDeleteCounselor != null) btnDeleteCounselor.setEnabled(false);
        if (txtCounselorName != null) txtCounselorName.setEnabled(false);
        if (txtCounselorSpecialization != null) txtCounselorSpecialization.setEnabled(false);
        if (cmbCounselorAvailability != null) cmbCounselorAvailability.setEnabled(false);
        if (tblCounselor != null) tblCounselor.setEnabled(false);
    } catch (Exception e) { // Catch any other unexpected exceptions
        logger.log(Level.SEVERE, "An unexpected error occurred during Counselor Controller initialization. Counselor features disabled.", e);
        JOptionPane.showMessageDialog(this, "An unexpected error occurred during Counselor Controller initialization: " + e.getMessage() + "\nCounselor features might be unavailable.", "Error", JOptionPane.ERROR_MESSAGE);
        counselorController = null;
        // Disable UI elements as above
        if (btnAddCounselor != null) btnAddCounselor.setEnabled(false);
        if (btnUpdateCounselor != null) btnUpdateCounselor.setEnabled(false);
        if (btnDeleteCounselor != null) btnDeleteCounselor.setEnabled(false);
        if (txtCounselorName != null) txtCounselorName.setEnabled(false);
        if (txtCounselorSpecialization != null) txtCounselorSpecialization.setEnabled(false);
        if (cmbCounselorAvailability != null) cmbCounselorAvailability.setEnabled(false);
        if (tblCounselor != null) tblCounselor.setEnabled(false);
    }
        
        if (cmbStatus.getItemCount() > 0) {
            cmbStatus.setSelectedItem("Pending");
        }
        
        loadAppointmentsIntoTable();
        
        tblAppointment.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting() && tblAppointment.getSelectedRow() != -1) {
                    int selectedRow = tblAppointment.getSelectedRow();
                    
                    String selectedStudentId = (String) tblAppointment.getValueAt(selectedRow, 1);
                    String selectedStudentName = (String) tblAppointment.getValueAt(selectedRow, 2);
                    String selectedCounselor = (String) tblAppointment.getValueAt(selectedRow, 3);
                    String selectedDateStr = (String) tblAppointment.getValueAt(selectedRow, 4);
                    String selectedTime = (String) tblAppointment.getValueAt(selectedRow, 5);
                    String selectedStatus = (String) tblAppointment.getValueAt(selectedRow, 6);

                    txtStudentId.setText(selectedStudentId);
                    
                    txtStudentName.setText((String) tblAppointment.getValueAt(selectedRow, 2));
                    cmbCounselor.setSelectedItem(selectedCounselor);

                    try {
                        java.util.Date date = java.sql.Date.valueOf(selectedDateStr);
                        dateChooserAppointment.setDate(date);
                    } catch (IllegalArgumentException ex) {
                        System.err.println("Error parsing date: " + selectedDateStr + " - " + ex.getMessage());
                        dateChooserAppointment.setDate(null);
                    }

                    cmbTime.setSelectedItem(selectedTime);
                    cmbStatus.setSelectedItem(selectedStatus);
                } else if (tblAppointment.getSelectedRow() == -1) {
                    // Clear fields if no row is selected
                    txtStudentId.setText("");
                    txtStudentName.setText(""); 
                    cmbCounselor.setSelectedIndex(0);
                    dateChooserAppointment.setDate(null);
                    cmbTime.setSelectedIndex(0);
                    cmbStatus.setSelectedIndex(0);
                }
            }
        });
        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        
       
        
       
    }

   
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jTabbedPane2 = new javax.swing.JTabbedPane();
        jPanel1 = new javax.swing.JPanel();
        jPanel4 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        txtStudentId = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        txtStudentName = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        cmbCounselor = new javax.swing.JComboBox<>();
        jLabel5 = new javax.swing.JLabel();
        cmbStatus = new javax.swing.JComboBox<>();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        cmbTime = new javax.swing.JComboBox<>();
        dateChooserAppointment = new com.toedter.calendar.JDateChooser();
        btnBookAppointment = new javax.swing.JButton();
        jPanel5 = new javax.swing.JPanel();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        txtSearchID = new javax.swing.JTextField();
        JScrollPane = new javax.swing.JScrollPane();
        tblAppointment = new javax.swing.JTable();
        btnSearch = new javax.swing.JButton();
        btnUpdate = new javax.swing.JButton();
        btnCancel = new javax.swing.JButton();
        btnRefresh = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        jPanel9 = new javax.swing.JPanel();
        jLabel20 = new javax.swing.JLabel();
        jLabel21 = new javax.swing.JLabel();
        jLabel22 = new javax.swing.JLabel();
        txtCounselorName = new javax.swing.JTextField();
        txtCounselorSpecialization = new javax.swing.JTextField();
        cmbCounselorAvailability = new javax.swing.JComboBox<>();
        btnAddCounselor = new javax.swing.JButton();
        btnUpdateCounselor = new javax.swing.JButton();
        btnDeleteCounselor = new javax.swing.JButton();
        jPanel10 = new javax.swing.JPanel();
        jScrollPane3 = new javax.swing.JScrollPane();
        tblCounselor = new javax.swing.JTable();
        jPanel3 = new javax.swing.JPanel();
        jPanel7 = new javax.swing.JPanel();
        jLabel15 = new javax.swing.JLabel();
        jLabel16 = new javax.swing.JLabel();
        jLabel17 = new javax.swing.JLabel();
        txtStudentID = new javax.swing.JTextField();
        cmbCounselor1 = new javax.swing.JComboBox<>();
        jLabel13 = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        txtComments = new javax.swing.JTextArea();
        jButton7 = new javax.swing.JButton();
        spnRating = new javax.swing.JSpinner();
        jButton12 = new javax.swing.JButton();
        btnSubmitFeedback = new javax.swing.JButton();
        jPanel8 = new javax.swing.JPanel();
        jLabel18 = new javax.swing.JLabel();
        jLabel19 = new javax.swing.JLabel();
        btnSearchAction = new javax.swing.JButton();
        btnViewAll = new javax.swing.JButton();
        txtSearchbyID = new javax.swing.JTextField();
        jScrollPane2 = new javax.swing.JScrollPane();
        tblStudents = new javax.swing.JTable();
        btnEditFeedback = new javax.swing.JButton();
        btnDeleteFeedback = new javax.swing.JButton();
        jPanel6 = new javax.swing.JPanel();
        jLabel10 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setBackground(new java.awt.Color(255, 102, 102));
        setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        setForeground(new java.awt.Color(51, 51, 255));

        jTabbedPane2.setBackground(new java.awt.Color(153, 153, 255));
        jTabbedPane2.setForeground(new java.awt.Color(255, 255, 255));
        jTabbedPane2.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        jTabbedPane2.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jTabbedPane2.setOpaque(true);

        jPanel1.setBackground(new java.awt.Color(153, 102, 255));

        jPanel4.setBackground(new java.awt.Color(167, 153, 206));

        jLabel1.setText("Book Appointment:");

        jLabel2.setText("Student ID:");

        jLabel3.setText("Student name:");

        jLabel4.setText("Counsellor:");

        cmbCounselor.setBackground(new java.awt.Color(255, 168, 139));
        cmbCounselor.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        jLabel5.setText("Status:");

        cmbStatus.setBackground(new java.awt.Color(255, 168, 139));
        cmbStatus.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        jLabel6.setText("Date:");

        jLabel7.setText("Time:");

        cmbTime.setBackground(new java.awt.Color(255, 168, 139));
        cmbTime.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        cmbTime.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmbTimeActionPerformed(evt);
            }
        });

        btnBookAppointment.setBackground(new java.awt.Color(255, 168, 139));
        btnBookAppointment.setText("Book Appointment");

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGap(21, 21, 21)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel1)
                            .addGroup(jPanel4Layout.createSequentialGroup()
                                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel2)
                                    .addComponent(jLabel4)
                                    .addComponent(jLabel6))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(txtStudentId)
                                    .addComponent(cmbCounselor, 0, 100, Short.MAX_VALUE)
                                    .addComponent(dateChooserAppointment, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                .addGap(18, 18, 18)
                                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addGroup(jPanel4Layout.createSequentialGroup()
                                        .addComponent(jLabel5)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(cmbStatus, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(jPanel4Layout.createSequentialGroup()
                                        .addComponent(jLabel3)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addComponent(txtStudentName, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(jPanel4Layout.createSequentialGroup()
                                        .addComponent(jLabel7)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(cmbTime, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                        .addContainerGap(952, Short.MAX_VALUE))
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addComponent(btnBookAppointment)
                        .addGap(0, 0, Short.MAX_VALUE))))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(txtStudentId, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3)
                    .addComponent(txtStudentName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(cmbCounselor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel5)
                    .addComponent(cmbStatus, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(cmbTime, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel7))
                    .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                        .addComponent(dateChooserAppointment, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel6)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 26, Short.MAX_VALUE)
                .addComponent(btnBookAppointment)
                .addGap(18, 18, 18))
        );

        jPanel5.setBackground(new java.awt.Color(167, 153, 206));

        jLabel8.setText("View / Update / Cancel Appointments:");

        jLabel9.setText("Search by StudentID:");

        tblAppointment.setModel(new javax.swing.table.DefaultTableModel(
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
        JScrollPane.setViewportView(tblAppointment);

        btnSearch.setBackground(new java.awt.Color(255, 168, 139));
        btnSearch.setText("Search");

        btnUpdate.setBackground(new java.awt.Color(255, 168, 139));
        btnUpdate.setText("Update");

        btnCancel.setBackground(new java.awt.Color(255, 168, 139));
        btnCancel.setText("Cancel");

        btnRefresh.setBackground(new java.awt.Color(255, 168, 139));
        btnRefresh.setText("Refresh");

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(JScrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, 1011, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel5Layout.createSequentialGroup()
                                .addComponent(jLabel9)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(txtSearchID, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(jLabel8))
                        .addGap(152, 152, 152)
                        .addComponent(btnSearch, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addComponent(btnUpdate, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(btnCancel, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(btnRefresh, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(335, Short.MAX_VALUE))
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addComponent(jLabel8)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel9)
                    .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(txtSearchID, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(btnSearch)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(JScrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, 111, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnUpdate)
                    .addComponent(btnCancel)
                    .addComponent(btnRefresh))
                .addContainerGap(12, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(127, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(133, Short.MAX_VALUE))
        );

        jTabbedPane2.addTab("Appointment Management", jPanel1);

        jPanel2.setBackground(new java.awt.Color(153, 102, 255));

        jPanel9.setBackground(new java.awt.Color(167, 153, 206));

        jLabel20.setText("Name:");

        jLabel21.setText("Specialization:");

        jLabel22.setText("Availibility:");

        cmbCounselorAvailability.setBackground(new java.awt.Color(255, 168, 139));
        cmbCounselorAvailability.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        btnAddCounselor.setBackground(new java.awt.Color(255, 168, 139));
        btnAddCounselor.setText("Add");
        btnAddCounselor.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAddCounselorActionPerformed(evt);
            }
        });

        btnUpdateCounselor.setBackground(new java.awt.Color(255, 168, 139));
        btnUpdateCounselor.setText("Update");
        btnUpdateCounselor.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnUpdateCounselorActionPerformed(evt);
            }
        });

        btnDeleteCounselor.setBackground(new java.awt.Color(255, 168, 139));
        btnDeleteCounselor.setText("Delete");
        btnDeleteCounselor.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDeleteCounselorActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel9Layout = new javax.swing.GroupLayout(jPanel9);
        jPanel9.setLayout(jPanel9Layout);
        jPanel9Layout.setHorizontalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel9Layout.createSequentialGroup()
                .addGap(45, 45, 45)
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel20)
                    .addComponent(jLabel21)
                    .addComponent(jLabel22))
                .addGap(77, 77, 77)
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanel9Layout.createSequentialGroup()
                        .addComponent(btnAddCounselor)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btnUpdateCounselor)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED))
                    .addGroup(jPanel9Layout.createSequentialGroup()
                        .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(cmbCounselorAvailability, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addComponent(txtCounselorName, javax.swing.GroupLayout.DEFAULT_SIZE, 132, Short.MAX_VALUE)
                                .addComponent(txtCounselorSpecialization)))
                        .addGap(165, 165, 165)))
                .addComponent(btnDeleteCounselor)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel9Layout.setVerticalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel9Layout.createSequentialGroup()
                .addGap(26, 26, 26)
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel20)
                    .addComponent(txtCounselorName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel21)
                    .addComponent(txtCounselorSpecialization, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel22)
                    .addComponent(cmbCounselorAvailability, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnAddCounselor)
                    .addComponent(btnUpdateCounselor)
                    .addComponent(btnDeleteCounselor))
                .addContainerGap(62, Short.MAX_VALUE))
        );

        jPanel10.setBackground(new java.awt.Color(167, 153, 206));

        tblCounselor.setModel(new javax.swing.table.DefaultTableModel(
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
        jScrollPane3.setViewportView(tblCounselor);

        javax.swing.GroupLayout jPanel10Layout = new javax.swing.GroupLayout(jPanel10);
        jPanel10.setLayout(jPanel10Layout);
        jPanel10Layout.setHorizontalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel10Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 707, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(318, Short.MAX_VALUE))
        );
        jPanel10Layout.setVerticalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel10Layout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 206, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(60, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(jPanel10, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel9, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(448, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel9, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanel10, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(38, Short.MAX_VALUE))
        );

        jTabbedPane2.addTab("Counselor Management", jPanel2);

        jPanel3.setBackground(new java.awt.Color(153, 102, 255));

        jPanel7.setBackground(new java.awt.Color(167, 153, 206));

        jLabel15.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel15.setText("Provide Feedback");

        jLabel16.setText("Student ID:");

        jLabel17.setText("Counselor:");

        txtStudentID.setActionCommand("<Not Set>");

        cmbCounselor1.setBackground(new java.awt.Color(255, 168, 139));
        cmbCounselor1.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        jLabel13.setText("Rating:");

        jLabel14.setText("Comments");

        txtComments.setColumns(20);
        txtComments.setRows(5);
        jScrollPane1.setViewportView(txtComments);

        jButton7.setText("Submit Feedback");

        spnRating.setModel(new javax.swing.SpinnerNumberModel(1, 1, 5, 1));

        jButton12.setText("jButton12");

        btnSubmitFeedback.setBackground(new java.awt.Color(255, 168, 139));
        btnSubmitFeedback.setText("Submit Feedback");

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addGap(35, 35, 35)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel13, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addComponent(jLabel17, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel16, javax.swing.GroupLayout.DEFAULT_SIZE, 70, Short.MAX_VALUE)))
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel7Layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jButton12)
                        .addGap(703, 703, 703))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel7Layout.createSequentialGroup()
                        .addGap(37, 37, 37)
                        .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(txtStudentID)
                            .addComponent(cmbCounselor1, 0, 126, Short.MAX_VALUE)
                            .addComponent(spnRating, javax.swing.GroupLayout.PREFERRED_SIZE, 51, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel7Layout.createSequentialGroup()
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 204, Short.MAX_VALUE)
                                .addComponent(jLabel14, javax.swing.GroupLayout.PREFERRED_SIZE, 69, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(901, 901, 901))
                            .addGroup(jPanel7Layout.createSequentialGroup()
                                .addGap(196, 196, 196)
                                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 400, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))))
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel7Layout.createSequentialGroup()
                        .addGap(509, 509, 509)
                        .addComponent(jButton7))
                    .addGroup(jPanel7Layout.createSequentialGroup()
                        .addGap(584, 584, 584)
                        .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(btnSubmitFeedback, javax.swing.GroupLayout.PREFERRED_SIZE, 131, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel15, javax.swing.GroupLayout.PREFERRED_SIZE, 121, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel7Layout.createSequentialGroup()
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel7Layout.createSequentialGroup()
                        .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel7Layout.createSequentialGroup()
                                .addGap(43, 43, 43)
                                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(txtStudentID, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel16, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(21, 21, 21)
                                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(cmbCounselor1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGroup(jPanel7Layout.createSequentialGroup()
                                        .addGap(2, 2, 2)
                                        .addComponent(jLabel17, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jLabel13)
                                    .addComponent(spnRating, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(13, 13, 13))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel7Layout.createSequentialGroup()
                                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jLabel14)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 129, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)))
                        .addComponent(btnSubmitFeedback)
                        .addGap(18, 18, 18)
                        .addComponent(jButton12))
                    .addGroup(jPanel7Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jLabel15)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jButton7)))
                .addGap(18, 18, 18))
        );

        jPanel8.setBackground(new java.awt.Color(167, 153, 206));

        jLabel18.setText("View / Edit / Delete Feedback:");

        jLabel19.setText("Search by Student ID:");

        btnSearchAction.setBackground(new java.awt.Color(255, 168, 139));
        btnSearchAction.setText("Search");

        btnViewAll.setBackground(new java.awt.Color(255, 168, 139));
        btnViewAll.setText("View All");
        btnViewAll.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnViewAllActionPerformed(evt);
            }
        });

        tblStudents.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "StudentID", "Counselor", "Rating", "Comments"
            }
        ));
        jScrollPane2.setViewportView(tblStudents);

        btnEditFeedback.setBackground(new java.awt.Color(255, 168, 139));
        btnEditFeedback.setText("Edit");

        btnDeleteFeedback.setBackground(new java.awt.Color(255, 168, 139));
        btnDeleteFeedback.setText("Delete");

        javax.swing.GroupLayout jPanel8Layout = new javax.swing.GroupLayout(jPanel8);
        jPanel8.setLayout(jPanel8Layout);
        jPanel8Layout.setHorizontalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel8Layout.createSequentialGroup()
                        .addGap(20, 20, 20)
                        .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel18)
                            .addGroup(jPanel8Layout.createSequentialGroup()
                                .addComponent(jLabel19, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(txtSearchbyID, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(83, 83, 83)
                                .addComponent(btnSearchAction, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(33, 33, 33)
                                .addComponent(btnViewAll, javax.swing.GroupLayout.PREFERRED_SIZE, 91, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(31, 31, 31)
                                .addComponent(btnEditFeedback, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(btnDeleteFeedback, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE))))
                    .addGroup(jPanel8Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 1033, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel8Layout.setVerticalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel18)
                .addGap(18, 18, 18)
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(btnViewAll)
                        .addComponent(btnSearchAction)
                        .addComponent(btnEditFeedback)
                        .addComponent(btnDeleteFeedback))
                    .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel19)
                        .addComponent(txtSearchbyID, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(18, 18, 18)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 121, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(51, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel8, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jPanel7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 31, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel7, javax.swing.GroupLayout.PREFERRED_SIZE, 211, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 35, Short.MAX_VALUE)
                .addComponent(jPanel8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(64, 64, 64))
        );

        jTabbedPane2.addTab("Feedback Management", jPanel3);

        jPanel6.setBackground(new java.awt.Color(167, 153, 206));

        jLabel10.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        jLabel10.setForeground(new java.awt.Color(255, 255, 255));
        jLabel10.setText("Wellness Dashboard");

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addGap(31, 31, 31)
                .addComponent(jLabel10)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addGap(21, 21, 21)
                .addComponent(jLabel10)
                .addContainerGap(27, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jTabbedPane2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(63, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTabbedPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 604, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(144, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void cmbTimeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmbTimeActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_cmbTimeActionPerformed

    private void btnAddCounselorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAddCounselorActionPerformed
        if (counselorController == null) {
            JOptionPane.showMessageDialog(this, "Counselor features are currently unavailable due to a previous error.", "Error", JOptionPane.ERROR_MESSAGE);
            logger.severe("Attempted to add counselor while counselorController is null.");
            return;
        }
        try {
            String name = txtCounselorName.getText().trim();
            String specialization = txtCounselorSpecialization.getText().trim();
            String availability = (String) cmbCounselorAvailability.getSelectedItem();

            if (name.isEmpty() || specialization.isEmpty() || availability == null || availability.equals("Select Availability")) {
                JOptionPane.showMessageDialog(this, "Please fill in all counselor fields.", "Validation Error", JOptionPane.WARNING_MESSAGE);
                return;
            }

            // ID is 0 for new counselors, as it will be auto-generated by the database
            Counselor newCounselor = new Counselor(0, name, specialization, availability);

            counselorController.addCounselor(newCounselor);
            JOptionPane.showMessageDialog(this, "Counselor added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            loadCounselorsIntoTable(); // Refresh the table
            // Clear input fields after adding
            txtCounselorName.setText("");
            txtCounselorSpecialization.setText("");
            cmbCounselorAvailability.setSelectedIndex(0); // Reset to "Select Availability"

        } catch (IllegalArgumentException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Validation Error", JOptionPane.WARNING_MESSAGE);
            logger.log(Level.WARNING, "Validation error when adding counselor", e);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error adding counselor: " + ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
            logger.log(Level.SEVERE, "Error adding counselor to database", ex);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "An unexpected error occurred: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            logger.log(Level.SEVERE, "Unexpected error in btnAddCounsellorActionPerformed", ex);
        }
    }//GEN-LAST:event_btnAddCounselorActionPerformed

    private void btnUpdateCounselorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnUpdateCounselorActionPerformed
        if (counselorController == null) {
            JOptionPane.showMessageDialog(this, "Counselor features are currently unavailable due to a previous error.", "Error", JOptionPane.ERROR_MESSAGE);
            logger.severe("Attempted to update counselor while counselorController is null.");
            return;
        }
        int selectedRow = tblCounselor.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a counselor to update.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            // Get the ID from the selected row in the table (assuming ID is column 0)
            int id = (int) tblCounselor.getValueAt(selectedRow, 0);
            String name = txtCounselorName.getText().trim();
            String specialization = txtCounselorSpecialization.getText().trim();
            String availability = (String) cmbCounselorAvailability.getSelectedItem();

            if (name.isEmpty() || specialization.isEmpty() || availability == null || availability.equals("Select Availability")) {
                JOptionPane.showMessageDialog(this, "Please fill in all counselor fields.", "Validation Error", JOptionPane.WARNING_MESSAGE);
                return;
            }

            Counselor updatedCounselor = new Counselor(id, name, specialization, availability);

            counselorController.updateCounselor(updatedCounselor);
            JOptionPane.showMessageDialog(this, "Counselor updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            loadCounselorsIntoTable(); // Refresh the table
            // Clear input fields after updating
            txtCounselorName.setText("");
            txtCounselorSpecialization.setText("");
            cmbCounselorAvailability.setSelectedIndex(0);

        } catch (IllegalArgumentException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Validation Error", JOptionPane.WARNING_MESSAGE);
            logger.log(Level.WARNING, "Validation error when updating counselor", e);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error updating counselor: " + ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
            logger.log(Level.SEVERE, "Error updating counselor in database", ex);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "An unexpected error occurred: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            logger.log(Level.SEVERE, "Unexpected error in btnUpdateCounsellorActionPerformed", ex);
        }
    }//GEN-LAST:event_btnUpdateCounselorActionPerformed

    private void btnDeleteCounselorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDeleteCounselorActionPerformed
        if (counselorController == null) {
            JOptionPane.showMessageDialog(this, "Counselor features are currently unavailable due to a previous error.", "Error", JOptionPane.ERROR_MESSAGE);
            logger.severe("Attempted to delete counselor while counselorController is null.");
            return;
        }
        int selectedRow = tblCounselor.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a counselor to delete.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete this counselor?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                // Get the ID from the selected row in the table
                int id = (int) tblCounselor.getValueAt(selectedRow, 0);
                counselorController.deleteCounselor(id);
                JOptionPane.showMessageDialog(this, "Counselor deleted successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                loadCounselorsIntoTable(); // Refresh the table
                // Clear input fields after deleting
                txtCounselorName.setText("");
                txtCounselorSpecialization.setText("");
                cmbCounselorAvailability.setSelectedIndex(0);

            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Error deleting counselor: " + ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
                logger.log(Level.SEVERE, "Error deleting counselor from database", ex);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "An unexpected error occurred: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                logger.log(Level.SEVERE, "Unexpected error in btnDeleteCounsellorActionPerformed", ex);
            }
        }
    }//GEN-LAST:event_btnDeleteCounselorActionPerformed

    private void btnViewAllActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnViewAllActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnViewAllActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {
    String studentId = txtStudentId.getText().trim();
    String studentName = txtStudentName.getText().trim();
    String counselorName = (String) cmbCounselor.getSelectedItem();
    java.util.Date utilDate = dateChooserAppointment.getDate();
    String time = (String) cmbTime.getSelectedItem();
    String status = (String) cmbStatus.getSelectedItem();

    if (studentId.isEmpty()) {
        JOptionPane.showMessageDialog(this, "Student ID cannot be empty.", "Input Error", JOptionPane.ERROR_MESSAGE);
        return;
    }
    if (studentName.isEmpty()) {
        JOptionPane.showMessageDialog(this, "Student Name cannot be empty.", "Input Error", JOptionPane.ERROR_MESSAGE);
        return;
    }
    if (counselorName == null || counselorName.trim().isEmpty() || "Select Counselor".equals(counselorName)) {
        JOptionPane.showMessageDialog(this, "Please select a Counselor.", "Input Error", JOptionPane.ERROR_MESSAGE);
        return;
    }
    if (utilDate == null) {
        JOptionPane.showMessageDialog(this, "Please select an Appointment Date.", "Input Error", JOptionPane.ERROR_MESSAGE);
        return;
    }

    java.util.Date today = new java.util.Date();
    java.util.Calendar calSelectedDate = java.util.Calendar.getInstance();
    calSelectedDate.setTime(utilDate);
    calSelectedDate.set(java.util.Calendar.HOUR_OF_DAY, 0);
    calSelectedDate.set(java.util.Calendar.MINUTE, 0);
    calSelectedDate.set(java.util.Calendar.SECOND, 0);
    calSelectedDate.set(java.util.Calendar.MILLISECOND, 0);

    java.util.Calendar calToday = java.util.Calendar.getInstance();
    calToday.setTime(today);
    calToday.set(java.util.Calendar.HOUR_OF_DAY, 0);
    calToday.set(java.util.Calendar.MINUTE, 0);
    calToday.set(java.util.Calendar.SECOND, 0);
    calToday.set(java.util.Calendar.MILLISECOND, 0);

    if (calSelectedDate.before(calToday)) {
        JOptionPane.showMessageDialog(this, "Appointment date cannot be in the past.", "Input Error", JOptionPane.ERROR_MESSAGE);
        return;
    }

    if (time == null || time.trim().isEmpty() || "Select Time".equals(time)) {
        JOptionPane.showMessageDialog(this, "Please select an Appointment Time.", "Input Error", JOptionPane.ERROR_MESSAGE);
        return;
    }
    if (status == null || status.trim().isEmpty() || "Select Status".equals(status)) {
        JOptionPane.showMessageDialog(this, "Please select an Appointment Status.", "Input Error", JOptionPane.ERROR_MESSAGE);
        return;
    }

    java.sql.Date sqlDate = new java.sql.Date(utilDate.getTime());

    try {
        if (appointmentController.isStudentIdTaken(studentId)) {
            JOptionPane.showMessageDialog(this, "Student ID '" + studentId + "' already exists. A student can only have one studentID.", "Duplicate Student ID", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        if (appointmentController.isCounselorAvailable(counselorName, sqlDate, time)) {
            JOptionPane.showMessageDialog(this, "Counselor '" + counselorName + "' is already booked at " + time + " on " + utilDate + ". Please choose another time or counselor.", "Booking Conflict", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Appointment newAppointment = new Appointment(studentId, studentName, counselorName, sqlDate, time, status);
        appointmentController.bookAppointment(newAppointment);

        JOptionPane.showMessageDialog(this, "Appointment booked successfully!", "Booking Confirmed", JOptionPane.INFORMATION_MESSAGE);

        txtStudentId.setText("");
        txtStudentName.setText("");
        cmbCounselor.setSelectedIndex(0);
        dateChooserAppointment.setDate(null);
        cmbTime.setSelectedIndex(0);
        cmbStatus.setSelectedItem("Pending");

        loadAppointmentsIntoTable();

    } catch (Exception ex) {
        JOptionPane.showMessageDialog(this, "Error booking appointment: " + ex.getMessage(), "Booking Error", JOptionPane.ERROR_MESSAGE);
        ex.printStackTrace();
    }
}
    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {                                         
        String searchStudentId = txtSearchID.getText().trim(); // Assuming jTextField3 is your search input field
        DefaultTableModel model = (DefaultTableModel) tblAppointment.getModel();
        model.setRowCount(0); // Clear existing rows

        try {
            List<Appointment> appointments;
            if (searchStudentId.isEmpty()) {
                appointments = appointmentController.getAllAppointments(); // If search field is empty, show all
            } else {
                appointments = appointmentController.searchAppointmentsByStudentId(searchStudentId);
            }

            if (appointments.isEmpty() && !searchStudentId.isEmpty()) {
                JOptionPane.showMessageDialog(this, "No appointments found for Student ID: " + searchStudentId, "Search Result", JOptionPane.INFORMATION_MESSAGE);
            }

            for (Appointment appt : appointments) {
                model.addRow(new Object[]{
                    appt.getId(),
                    appt.getStudentId(),
                    appt.getStudentName(),
                    appt.getCounselorName(),
                    appt.getAppointmentDate().toString(),
                    appt.getAppointmentTime(),
                    appt.getStatus()
                });
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error searching appointments: " + ex.getMessage(), "Search Error", JOptionPane.ERROR_MESSAGE);
            logger.log(java.util.logging.Level.SEVERE, "Error searching appointments", ex);
        }
    }     
      private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {                                         
        int selectedRow = tblAppointment.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select an appointment to update.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            // Get appointment ID from the selected table row (assuming it's in column 0)
            int appointmentId = (int) tblAppointment.getValueAt(selectedRow, 0);
            String studentId = txtStudentId.getText().trim();
            String studentName = txtStudentName.getText().trim(); 
            String counselorName = (String) cmbCounselor.getSelectedItem();
            java.util.Date utilDate = dateChooserAppointment.getDate();
            String time = (String) cmbTime.getSelectedItem();
            String status = (String) cmbStatus.getSelectedItem();

            if (studentId.isEmpty() || counselorName == null || "Select Counselor".equals(counselorName.trim()) || utilDate == null || time == null || "Select Time".equals(time.trim()) || status == null || status.trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "All fields must be filled and valid selections made to update an appointment.", "Input Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            java.util.Date today = new java.util.Date();
        java.util.Calendar calSelectedDate = java.util.Calendar.getInstance();
        calSelectedDate.setTime(utilDate);
        calSelectedDate.set(java.util.Calendar.HOUR_OF_DAY, 0);
        calSelectedDate.set(java.util.Calendar.MINUTE, 0);
        calSelectedDate.set(java.util.Calendar.SECOND, 0);
        calSelectedDate.set(java.util.Calendar.MILLISECOND, 0);

        java.util.Calendar calToday = java.util.Calendar.getInstance();
        calToday.setTime(today);
        calToday.set(java.util.Calendar.HOUR_OF_DAY, 0);
        calToday.set(java.util.Calendar.MINUTE, 0);
        calToday.set(java.util.Calendar.SECOND, 0);
        calToday.set(java.util.Calendar.MILLISECOND, 0);

        if (calSelectedDate.before(calToday)) {
            JOptionPane.showMessageDialog(this, "Appointment date cannot be in the past.", "Input Error", JOptionPane.ERROR_MESSAGE);
            return; // Stop the update process if date is in the past
        }

            java.sql.Date sqlDate = new java.sql.Date(utilDate.getTime());
            // Pass null for studentName if not updated from input field
            Appointment updatedAppointment = new Appointment(appointmentId, studentId, studentName, counselorName, sqlDate, time, status);

            appointmentController.updateAppointment(updatedAppointment);
            JOptionPane.showMessageDialog(this, "Appointment updated successfully!", "Update Confirmed", JOptionPane.INFORMATION_MESSAGE);
            loadAppointmentsIntoTable(); // Refresh table
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error updating appointment: " + ex.getMessage(), "Update Error", JOptionPane.ERROR_MESSAGE);
            logger.log(java.util.logging.Level.SEVERE, "Error updating appointment", ex);
        }
    } 
      private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {                                         
        int selectedRow = tblAppointment.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select an appointment to cancel.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to cancel this appointment?", "Confirm Cancellation", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                // Get appointment ID from the selected table row (assuming it's in column 0)
                int appointmentId = (int) tblAppointment.getValueAt(selectedRow, 0);
                appointmentController.cancelAppointment(appointmentId);
                JOptionPane.showMessageDialog(this, "Appointment cancelled successfully!", "Cancellation Confirmed", JOptionPane.INFORMATION_MESSAGE);
                loadAppointmentsIntoTable(); // Refresh table
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error cancelling appointment: " + ex.getMessage(), "Cancellation Error", JOptionPane.ERROR_MESSAGE);
                logger.log(java.util.logging.Level.SEVERE, "Error cancelling appointment", ex);
            }
        }
    }      
     private void jButton5ActionPerformed(java.awt.event.ActionEvent evt) {                                         
        loadAppointmentsIntoTable(); // Reload all appointments
        JOptionPane.showMessageDialog(this, "Appointment list refreshed.", "Refresh", JOptionPane.INFORMATION_MESSAGE);
    }         
    
     
   
    public static void main(String args[]) {
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ReflectiveOperationException | javax.swing.UnsupportedLookAndFeelException ex) {
            logger.log(java.util.logging.Level.SEVERE, null, ex);
        }
        java.awt.EventQueue.invokeLater(() -> new Dashboard().setVisible(true));
    }

 



    

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JScrollPane JScrollPane;
    private javax.swing.JButton btnAddCounselor;
    private javax.swing.JButton btnBookAppointment;
    private javax.swing.JButton btnCancel;
    private javax.swing.JButton btnDeleteCounselor;
    private javax.swing.JButton btnDeleteFeedback;
    private javax.swing.JButton btnEditFeedback;
    private javax.swing.JButton btnRefresh;
    private javax.swing.JButton btnSearch;
    private javax.swing.JButton btnSearchAction;
    private javax.swing.JButton btnSubmitFeedback;
    private javax.swing.JButton btnUpdate;
    private javax.swing.JButton btnUpdateCounselor;
    private javax.swing.JButton btnViewAll;
    private javax.swing.JComboBox<String> cmbCounselor;
    private javax.swing.JComboBox<String> cmbCounselor1;
    private javax.swing.JComboBox<String> cmbCounselorAvailability;
    private javax.swing.JComboBox<String> cmbStatus;
    private javax.swing.JComboBox<String> cmbTime;
    private com.toedter.calendar.JDateChooser dateChooserAppointment;
    private javax.swing.JButton jButton12;
    private javax.swing.JButton jButton7;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel10;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JTabbedPane jTabbedPane2;
    private javax.swing.JSpinner spnRating;
    private javax.swing.JTable tblAppointment;
    private javax.swing.JTable tblCounselor;
    private javax.swing.JTable tblStudents;
    private javax.swing.JTextArea txtComments;
    private javax.swing.JTextField txtCounselorName;
    private javax.swing.JTextField txtCounselorSpecialization;
    private javax.swing.JTextField txtSearchID;
    private javax.swing.JTextField txtSearchbyID;
    private javax.swing.JTextField txtStudentID;
    private javax.swing.JTextField txtStudentId;
    private javax.swing.JTextField txtStudentName;
    // End of variables declaration//GEN-END:variables

    private static class dateChooserAppointment {

        private static java.util.Date getDate() {
            throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
        }

        private static void setDate(Object object) {
            throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
        }

        public dateChooserAppointment() {
        }
    }
}
