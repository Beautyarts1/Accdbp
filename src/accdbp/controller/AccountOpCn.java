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
import java.awt.KeyEventDispatcher;
import java.awt.KeyboardFocusManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JDialog;

/**
 *
 * @author Minami
 */
public final class AccountOpCn {

    public static String id = "";
    AccountsOpView pane;
    Dbconnection c = new Dbconnection();

    public AccountOpCn(AccountsOpView pane) {
        this.pane = pane;
        Staticvar.keydis = new KeyEventDispatcher() {
            @Override
            public boolean dispatchKeyEvent(KeyEvent e) {
                if (e.getID() == KeyEvent.KEY_PRESSED) {
                    if (e.getKeyCode() == KeyEvent.VK_F4) {
                        pane.bcancel.doClick();
                    } else if ((e.getKeyCode() == KeyEvent.VK_S) && (e.isControlDown())) {
                        pane.bsave.doClick();
                    }

                }
                return false;
            }
        };
        KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(Staticvar.keydis);
        if (id.equals("")) {

        } else {
            loaddata();
        }
        savedata();
        cancel();
        autoformatnumber();
    }

    private void loaddata() {
        try {
            String query = "SELECT a.ACC_CODE, a.ACC_NAME, a.ACC_OPENING_BALANCE, a.ACC_TYPE, "
                 + "a.ACC_GROUP,a.ACC_DESC, a.DATE_CREATED FROM TB_ACC a WHERE a.ACC_CODE=?";
            PreparedStatement pres = c.cn().prepareStatement(query);
            pres.setString(1, id);
            ResultSet res = pres.executeQuery();
            while (res.next()) {
                pane.edaccount_master_code.setText(res.getString("ACC_CODE"));
                pane.edaccount_master_name.setText(res.getString("ACC_NAME"));
                pane.edopening_balance.setText(OneforAllfunc.nf(res.getDouble("ACC_OPENING_BALANCE")));
                pane.tadesc.setText(res.getString("ACC_DESC"));
                if (res.getInt("ACC_TYPE") == 0) {
                    pane.cmbaccount_type.setSelectedIndex(0);
                } else {
                    pane.cmbaccount_type.setSelectedIndex(1);
                }

                switch (res.getInt("ACC_GROUP")) {
                    case 0:
                        pane.cmbaccount_group.setSelectedIndex(0);
                        break;
                    case 1:
                        pane.cmbaccount_type.setSelectedIndex(1);
                        break;
                    case 2:
                        pane.cmbaccount_type.setSelectedIndex(2);
                        break;
                    default:
                        pane.cmbaccount_type.setSelectedIndex(3);
                        break;
                }

            }
            c.dc();
        } catch (SQLException ex) {
            OneforAllfunc.info("Error", ex.getMessage());
            Logger.getLogger(AccountOpCn.class.getName()).log(Level.SEVERE, null, ex);

        }
    }

    private void savedata() {
        pane.bsave.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (pane.edaccount_master_code.getText().equals("") || pane.edaccount_master_name.getText().equals("")) {
                    OneforAllfunc.info("Operation Failed", "Please Fill Account Code and Account name");
                } else {
                    if (id.equals("")) {
                        try {
                            String query = "INSERT INTO TB_ACC (ACC_CODE, ACC_NAME, ACC_OPENING_BALANCE, "
                                 + "ACC_TYPE, ACC_GROUP, ACC_DESC) VALUES (?,?,?,?,?,?);";
                            PreparedStatement pres = c.cn().prepareStatement(query);
                            pres.setString(1, pane.edaccount_master_code.getText());
                            pres.setString(2, pane.edaccount_master_name.getText());
                            pres.setDouble(3, OneforAllfunc.doubleformat(pane.edopening_balance.getText()));
                            int acctype = 0;
                            if (pane.cmbaccount_type.getSelectedIndex() == 0) {
                                acctype = 0;
                            } else {
                                acctype = 1;
                            }
                            pres.setInt(4, acctype);

                            int accgroup = 0;
                            switch (pane.cmbaccount_group.getSelectedIndex()) {
                                case 0:
                                    accgroup = 0;
                                    break;
                                case 1:
                                    accgroup = 1;
                                    break;
                                case 2:
                                    accgroup = 2;
                                    break;
                                default:
                                    accgroup = 3;
                                    break;
                            }
                            pres.setInt(5, accgroup);
                            pres.setString(6, pane.tadesc.getText());
                            pres.executeUpdate();
                            c.dc();
                            OneforAllfunc.info("Operation Success", "Data has been added");
                            KeyboardFocusManager.getCurrentKeyboardFocusManager().removeKeyEventDispatcher(Staticvar.keydis);
                            Staticvar.isupdate = true;
                            JDialog jd = (JDialog) pane.getRootPane().getParent();
                            jd.dispose();
                        } catch (SQLException ex) {
                            OneforAllfunc.info("Error", ex.getMessage());
                            Logger.getLogger(AccountOpCn.class.getName()).log(Level.SEVERE, null, ex);
                        }

                    } else {
                        try {
                            String query = "UPDATE TB_ACC SET ACC_CODE=?, ACC_NAME=?, ACC_OPENING_BALANCE=?, "
                                 + "ACC_TYPE=?, ACC_GROUP=?, ACC_DESC=? WHERE ACC_CODE=?";
                            PreparedStatement pres = c.cn().prepareStatement(query);
                            pres.setString(1, pane.edaccount_master_code.getText());
                            pres.setString(2, pane.edaccount_master_name.getText());
                            pres.setDouble(3, OneforAllfunc.doubleformat(pane.edopening_balance.getText()));
                            int acctype = 0;
                            if (pane.cmbaccount_type.getSelectedIndex() == 0) {
                                acctype = 0;
                            } else {
                                acctype = 1;
                            }
                            pres.setInt(4, acctype);

                            int accgroup = 0;
                            switch (pane.cmbaccount_group.getSelectedIndex()) {
                                case 0:
                                    accgroup = 0;
                                    break;
                                case 1:
                                    accgroup = 1;
                                    break;
                                case 2:
                                    accgroup = 2;
                                    break;
                                default:
                                    accgroup = 3;
                                    break;
                            }
                            pres.setInt(5, accgroup);
                            pres.setString(6, pane.tadesc.getText());
                            pres.setString(7, id);
                            pres.executeUpdate();
                            c.dc();
                            OneforAllfunc.info("Operation Success", "Data has been update");
                            KeyboardFocusManager.getCurrentKeyboardFocusManager().removeKeyEventDispatcher(Staticvar.keydis);
                            Staticvar.isupdate = true;
                            JDialog jd = (JDialog) pane.getRootPane().getParent();
                            jd.dispose();
                        } catch (SQLException ex) {
                            OneforAllfunc.info("Error", ex.getMessage());
                            Logger.getLogger(AccountOpCn.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                }
            }
        });

    }

    private void cancel() {
        pane.bcancel.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Staticvar.isupdate = false;
                KeyboardFocusManager.getCurrentKeyboardFocusManager().removeKeyEventDispatcher(Staticvar.keydis);
                JDialog jd = (JDialog) pane.getRootPane().getParent();
                jd.dispose();
            }
        });
    }

    private void autoformatnumber() {
        KeyAdapter adap = new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                pane.edopening_balance.setText(OneforAllfunc.nf(OneforAllfunc.doubleformat(pane.edopening_balance.getText())));
            }
        };
        pane.edopening_balance.addKeyListener(adap);
    }
}
