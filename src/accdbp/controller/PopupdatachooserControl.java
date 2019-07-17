/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package accdbp.controller;

import accdbp.helper.Dbconnection;
import accdbp.helper.OneforAllfunc;
import accdbp.helper.Staticvar;
import accdbp.view.PopupdatachooserView;
import java.awt.Color;
import java.awt.Font;
import java.awt.KeyEventDispatcher;
import java.awt.KeyboardFocusManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.AbstractAction;
import javax.swing.JDialog;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;

/**
 *
 * @author Minami
 */
public class PopupdatachooserControl {

    Dbconnection c = new Dbconnection();
    PopupdatachooserView pane;
    DefaultTableModel dtm = new DefaultTableModel();

    KeyEventDispatcher keydis = new KeyEventDispatcher() {
        @Override
        public boolean dispatchKeyEvent(KeyEvent e) {
            if (e.getID() == KeyEvent.KEY_PRESSED) {
                if ((e.isControlDown()) && (e.getKeyCode() == KeyEvent.VK_F)) {
                    pane.edfind.requestFocus();
                } else if (e.getKeyCode() == KeyEvent.VK_F5) {
                    loaddata();
                } else if (e.getKeyCode() == KeyEvent.VK_F4) {
                    pane.bcancel.doClick();
                } else if (e.getKeyCode() == KeyEvent.VK_DOWN) {
                    pane.tabledata.requestFocus();
                } else if (e.getKeyCode() == KeyEvent.VK_UP) {
                    pane.tabledata.requestFocus();
                }
            }
            return false;
        }
    };

    public PopupdatachooserControl(PopupdatachooserView pane) {
        this.pane = pane;
        KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(keydis);
        loadheader();
        loaddata();
        finddata();
        selectdata();
        close();
    }

    private void loadheader() {
        pane.tabledata.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        pane.tabledata.setDefaultEditor(Object.class, null);
        JTableHeader jthead = pane.tabledata.getTableHeader();
        jthead.setFont(new Font("Century Gothic", Font.BOLD, 14));
        pane.tabledata.setRowHeight(23);
        dtm.addColumn("Account Code");
        dtm.addColumn("Account Name");
        pane.tabledata.setModel(dtm);
    }

    private void loaddata() {
        pane.tabledata.clearSelection();
        dtm.getDataVector().removeAllElements();
        dtm.fireTableDataChanged();
        try {
            String query = "SELECT ACC_CODE,ACC_NAME FROM TB_ACC ORDER BY ACC_CODE ASC";
            PreparedStatement pres = c.cn().prepareStatement(query);
            ResultSet res = pres.executeQuery();
            while (res.next()) {
                Object o[] = new Object[2];
                o[0] = res.getString("ACC_CODE");
                o[1] = res.getString("ACC_NAME");
                dtm.addRow(o);
            }
            pane.tabledata.setModel(dtm);
            c.dc();
        } catch (SQLException ex) {
            OneforAllfunc.info("Error", ex.getMessage());
            Logger.getLogger(CashPaymentCn.class.getName()).log(Level.SEVERE, null, ex);

            c.dc();
        }
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
                        String query = "SELECT ACC_CODE,ACC_NAME FROM TB_ACC WHERE lower(ACC_NAME) LIKE ? ORDER BY ACC_CODE ASC";
                        PreparedStatement pres = c.cn().prepareStatement(query);
                        pres.setString(1, "%" + pane.edfind.getText().toLowerCase() + "%");
                        ResultSet res = pres.executeQuery();
                        while (res.next()) {
                            Object o[] = new Object[2];
                            o[0] = res.getString("ACC_CODE");
                            o[1] = res.getString("ACC_NAME");
                            dtm.addRow(o);
                        }
                        pane.tabledata.setModel(dtm);
                        c.dc();
                    } catch (SQLException ex) {
                        OneforAllfunc.info("Error", ex.getMessage());
                        Logger.getLogger(CashPaymentCn.class.getName()).log(Level.SEVERE, null, ex);

                        c.dc();
                    }
                }
            }

        };

        pane.edfind.addKeyListener(keyadap);
        pane.edfind.addFocusListener(fodap);

    }

    private void selectdata() {
        pane.bselect.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                KeyboardFocusManager.getCurrentKeyboardFocusManager().removeKeyEventDispatcher(keydis);
                Staticvar.isupdate = true;
                int selectrow = pane.tabledata.getSelectedRow();
                if (selectrow >= 0) {
                    Staticvar.global_val = String.valueOf(pane.tabledata.getValueAt(selectrow, 0));
                    Staticvar.global_name = String.valueOf(pane.tabledata.getValueAt(selectrow, 1));
                    JDialog jd = (JDialog) pane.getRootPane().getParent();
                    jd.dispose();
                }
            }
        });

        MouseAdapter madap = new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    pane.bselect.doClick();
                }
            }

        };
        pane.tabledata.addMouseListener(madap);

        pane.tabledata.getInputMap().put(KeyStroke.getKeyStroke("ENTER"), "enter");
        pane.tabledata.getActionMap().put("enter", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                pane.bselect.doClick();
            }
        });
    }

    private void close() {
        pane.bcancel.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                KeyboardFocusManager.getCurrentKeyboardFocusManager().removeKeyEventDispatcher(keydis);
                Staticvar.isupdate = false;
                JDialog jd = (JDialog) pane.getRootPane().getParent();
                jd.dispose();
            }
        });

    }

}
