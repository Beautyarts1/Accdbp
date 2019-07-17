/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package accdbp.controller;

import accdbp.helper.Dbconnection;
import accdbp.helper.OneforAllfunc;
import accdbp.helper.Staticvar;
import accdbp.view.AccountsOpView;
import accdbp.view.AccountsView;
import accdbp.view.Home;
import java.awt.Color;
import java.awt.Dialog;
import java.awt.Font;
import java.awt.KeyEventDispatcher;
import java.awt.KeyboardFocusManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumnModel;

/**
 *
 * @author Minami
 */
public class AccountCn {

    AccountsView pane;
    Dbconnection c = new Dbconnection();
    DefaultTableModel dtm = new DefaultTableModel();

    public AccountCn(AccountsView pane) {
        this.pane = pane;
        setkeydis();
        KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(Staticvar.keydis);
        loadheader();
        loaddata();
        insertdata();
        updatedata();
        deletedata();
        finddata();
        reload();
    }

    private void loadheader() {
        pane.tabledata.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        pane.tabledata.setDefaultEditor(Object.class, null);
        JTableHeader jthead = pane.tabledata.getTableHeader();
        jthead.setFont(new Font("Century Gothic", Font.BOLD, 14));
        pane.tabledata.setRowHeight(23);
        dtm.addColumn("Account Code");
        dtm.addColumn("Account Name");
        dtm.addColumn("Opening Balance");
        dtm.addColumn("Account Type");
        dtm.addColumn("Account Group");
        dtm.addColumn("Description");
        pane.tabledata.setModel(dtm);
        DefaultTableCellRenderer rightrender = new DefaultTableCellRenderer();
        rightrender.setHorizontalAlignment(DefaultTableCellRenderer.RIGHT);
        pane.tabledata.getColumn("Opening Balance").setCellRenderer(rightrender);
    }

    private void loaddata() {
        pane.tabledata.clearSelection();
        dtm.getDataVector().removeAllElements();
        dtm.fireTableDataChanged();
        try {
            String query = "SELECT a.ACC_CODE, a.ACC_NAME, a.ACC_OPENING_BALANCE, a.ACC_TYPE, "
                 + "a.ACC_GROUP,a.ACC_DESC, a.DATE_CREATED FROM TB_ACC a ORDER BY a.ACC_CODE ASC";
            PreparedStatement pres = c.cn().prepareStatement(query);
            ResultSet res = pres.executeQuery();
            while (res.next()) {
                Object o[] = new Object[6];
                o[0] = res.getString("ACC_CODE");
                o[1] = res.getString("ACC_NAME");
                o[2] = OneforAllfunc.nf(res.getDouble("ACC_OPENING_BALANCE"));
                if (res.getInt("ACC_TYPE") == 0) {
                    o[3] = "Cash";
                } else {
                    o[3] = "Bank";
                }
                if (res.getInt("ACC_GROUP") == 0) {
                    o[4] = "Assets";
                } else if (res.getInt("ACC_GROUP") == 1) {
                    o[4] = "Income";
                } else if (res.getInt("ACC_GROUP") == 2) {
                    o[4] = "Liability";
                } else if (res.getInt("ACC_GROUP") == 3) {
                    o[4] = "Expenditure";
                }
                o[5] = res.getString("ACC_DESC");
                dtm.addRow(o);
            }
            pane.tabledata.setModel(dtm);
            c.dc();
        } catch (SQLException ex) {
            OneforAllfunc.info("Error", ex.getMessage());
            Logger.getLogger(AccountCn.class.getName()).log(Level.SEVERE, null, ex);

            c.dc();
        }
        pane.lcountdata.setText("Record Count : " + pane.tabledata.getRowCount());
        double total_amount = 0;
        for (int i = 0; i < pane.tabledata.getRowCount(); i++) {
            total_amount = total_amount + OneforAllfunc.doubleformat(String.valueOf(pane.tabledata.getValueAt(i, 2)));
        }
        pane.ltotalamount.setText("Total Opening Balance : " + OneforAllfunc.nfcurrency(total_amount));
    }

    private void insertdata() {
        pane.badd.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                KeyboardFocusManager.getCurrentKeyboardFocusManager().removeKeyEventDispatcher(Staticvar.keydis);
                AccountOpCn.id = "";
                JDialog jd = new JDialog(new Home());
                jd.setResizable(false);
                jd.setTitle("Account Operation");
                jd.add(new AccountsOpView());
                jd.pack();
                jd.setModalityType(Dialog.ModalityType.APPLICATION_MODAL);
                jd.setLocationRelativeTo(null);
                jd.setVisible(true);
                jd.toFront();
                if (Staticvar.isupdate == true) {
                    Staticvar.isupdate = false;
                    loaddata();
                }
                setkeydis();
                KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(Staticvar.keydis);
            }
        });

    }

    private void updatedata() {
        pane.bedit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int row = pane.tabledata.getSelectedRow();
                if (row < 0) {
                    OneforAllfunc.info("Operation Failed", "Please Select Data");
                } else {
                    KeyboardFocusManager.getCurrentKeyboardFocusManager().removeKeyEventDispatcher(Staticvar.keydis);
                    String val = String.valueOf(pane.tabledata.getValueAt(row, 0));
                    AccountOpCn.id = val;
                    JDialog jd = new JDialog(new Home());
                    jd.setResizable(false);
                    jd.setTitle("Account Operation");
                    jd.add(new AccountsOpView());
                    jd.pack();
                    jd.setModalityType(Dialog.ModalityType.APPLICATION_MODAL);
                    jd.setLocationRelativeTo(null);
                    jd.setVisible(true);
                    jd.toFront();
                    if (Staticvar.isupdate == true) {
                        Staticvar.isupdate = false;
                        loaddata();
                    }
                    setkeydis();
                    KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(Staticvar.keydis);
                }
            }
        });
    }

    private void deletedata() {
        pane.bdelete.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (pane.tabledata.getSelectedRow() < 0) {
                    OneforAllfunc.info("Operation Failed", "Please Select Data");
                } else {
                    try {
                        OneforAllfunc.confirm("Are You sure to delete this data?", "Deleted Data Cannot be recover");
                        if (Staticvar.isyes == true) {
                            Staticvar.isyes = false;
                            String query = "DELETE FROM TB_ACC a WHERE a.ACC_CODE = ?";
                            PreparedStatement pres = c.cn().prepareStatement(query);
                            int row = pane.tabledata.getSelectedRow();
                            String value = String.valueOf(pane.tabledata.getValueAt(row, 0));
                            pres.setString(1, value);
                            pres.executeUpdate();
                            c.dc();
                            loaddata();
                        }

                    } catch (SQLException ex) {
                        OneforAllfunc.info("Error", ex.getMessage());
                        Logger.getLogger(AccountCn.class.getName()).log(Level.SEVERE, null, ex);

                    }
                }
            }
        });

    }

    private void finddata() {

        FocusAdapter fodap = new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                pane.edfind.setText("");
                pane.edfind.setForeground(Color.BLACK);
            }

            @Override
            public void focusLost(FocusEvent e) {
                pane.edfind.setText("Type and Enter to Find Data");
                pane.edfind.setForeground(Color.GRAY);
            }

        };

        KeyAdapter keyadap = new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    pane.tabledata.clearSelection();
                    dtm.getDataVector().removeAllElements();
                    dtm.fireTableDataChanged();
                    try {
                        String query = "SELECT a.ACC_CODE, a.ACC_NAME, a.ACC_OPENING_BALANCE, a.ACC_TYPE, "
                             + "a.ACC_GROUP,a.ACC_DESC, a.DATE_CREATED FROM TB_ACC a "
                             + " WHERE a.ACC_CODE LIKE ? "
                             + "OR lower(ACC_NAME) LIKE ? "
                             + "ORDER BY a.ACC_CODE ASC";
                        PreparedStatement pres = c.cn().prepareStatement(query);
                        pres.setString(1, "%" + pane.edfind.getText() + "%");
                        pres.setString(2, "%" + pane.edfind.getText().toLowerCase() + "%");
                        ResultSet res = pres.executeQuery();
                        while (res.next()) {
                            Object o[] = new Object[6];
                            o[0] = res.getString("ACC_CODE");
                            o[1] = res.getString("ACC_NAME");
                            o[2] = OneforAllfunc.nf(res.getDouble("ACC_OPENING_BALANCE"));
                            if (res.getInt("ACC_TYPE") == 0) {
                                o[3] = "Cash";
                            } else {
                                o[3] = "Bank";
                            }
                            if (res.getInt("ACC_GROUP") == 0) {
                                o[4] = "Assets";
                            } else if (res.getInt("ACC_GROUP") == 1) {
                                o[4] = "Income";
                            } else if (res.getInt("ACC_GROUP") == 2) {
                                o[4] = "Liability";
                            } else if (res.getInt("ACC_GROUP") == 3) {
                                o[4] = "Expenditure";
                            }
                            o[5] = res.getString("ACC_DESC");
                            dtm.addRow(o);
                        }
                        pane.tabledata.setModel(dtm);
                        c.dc();
                    } catch (SQLException ex) {
                        OneforAllfunc.info("Error", ex.getMessage());
                        Logger.getLogger(AccountCn.class.getName()).log(Level.SEVERE, null, ex);

                        c.dc();
                    }
                    pane.lcountdata.setText("Record Count : " + pane.tabledata.getRowCount());
                    double total_amount = 0;
                    for (int i = 0; i < pane.tabledata.getRowCount(); i++) {
                        total_amount = total_amount + OneforAllfunc.doubleformat(String.valueOf(pane.tabledata.getValueAt(i, 2)));
                    }
                    pane.ltotalamount.setText("Total Opening Balance : " + OneforAllfunc.nfcurrency(total_amount));

                }
            }

        };
        pane.edfind.addKeyListener(keyadap);
        pane.edfind.addFocusListener(fodap);
    }

    private void reload() {
        pane.brefresh.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
                loaddata();
                pane.brefresh.requestFocus();
            }
        });
    }

    private void setkeydis() {
        Staticvar.keydis = new KeyEventDispatcher() {
            @Override
            public boolean dispatchKeyEvent(KeyEvent e) {
                if (e.getID() == KeyEvent.KEY_PRESSED) {
                    if ((e.isControlDown()) && (e.getKeyCode() == KeyEvent.VK_F)) {
                        pane.edfind.requestFocus();
                    } else if (e.getKeyCode() == KeyEvent.VK_F5) {
                        loaddata();
                    } else if ((e.isControlDown()) && (e.getKeyCode() == KeyEvent.VK_A)) {
                        pane.badd.doClick();
                    } else if ((e.isControlDown()) && (e.getKeyCode() == KeyEvent.VK_E)) {
                        pane.bedit.doClick();
                    }
                    if (e.getKeyCode() == KeyEvent.VK_DELETE) {
                        pane.bdelete.doClick();
                    }
                    if (e.getKeyCode() == KeyEvent.VK_DOWN) {
                        pane.tabledata.requestFocus();
                    }
                }
                return false;
            }
        };
    }

}
