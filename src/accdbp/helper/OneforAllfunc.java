/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package accdbp.helper;

import accdbp.view.CConfirmDialog;
import accdbp.view.CMessageDialog;
import accdbp.view.Home;
import java.awt.Dialog;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JDialog;

/**
 *
 * @author Minami
 */
public class OneforAllfunc {

    public static String datetodb(Date ref) {
        String sdate = new SimpleDateFormat("yyyy-MM-dd").format(ref);
        return sdate;
    }

    public static String dateviewtable(Date ref) {
        String sdate = new SimpleDateFormat("dd/MM/yyyy").format(ref);
        return sdate;
    }

    public static Date datefromdb(String ref) {
        Date ddate = null;
        try {
            ddate = new SimpleDateFormat("yyyy-MM-dd").parse(ref);
        } catch (ParseException ex) {
            Logger.getLogger(OneforAllfunc.class.getName()).log(Level.SEVERE, null, ex);
        }
        return ddate;
    }

    public static double doubleformat(String ref) {
        double res = 0;
        try {
            res = Double.parseDouble(ref.replace(".", ""));
        } catch (Exception e) {
            res = 0;
        }
        return res;
    }

    public static int intformat(String ref) {
        int res = 0;
        try {
            res = Integer.parseInt(ref);
        } catch (Exception e) {
            res = 0;
        }
        return res;
    }

    public static String nfcurrency(double ref) {
        String res = "0";
        try {
            NumberFormat nf = NumberFormat.getCurrencyInstance();
            res = nf.format(ref);
        } catch (Exception e) {
            res = "0";
        }

        return res;
    }

    public static String nf(double ref) {
        String res = "0";
        try {
            NumberFormat nf = NumberFormat.getInstance();
            res = nf.format(ref);
        } catch (Exception e) {
            res = "0";
        }

        return res;
    }

    public static void info(String header, String detail) {
        JDialog jd = new JDialog(new Home());
        jd.setResizable(false);
        jd.add(new CMessageDialog(header, detail));
        jd.pack();
        jd.setTitle("Information");
        jd.setModalityType(Dialog.ModalityType.APPLICATION_MODAL);
        jd.setLocationRelativeTo(null);
        jd.setVisible(true);
    }

    public static void confirm(String header, String detail) {
        JDialog jd = new JDialog(new Home());
        jd.setResizable(false);
        jd.add(new CConfirmDialog(header, detail));
        jd.pack();
        jd.setTitle("Confirmation");
        jd.setModalityType(Dialog.ModalityType.APPLICATION_MODAL);
        jd.setLocationRelativeTo(null);
        jd.setVisible(true);
    }

    public static boolean accountcheck(String id) {
        boolean resl = false;
        int counrow = 0;
        Dbconnection dcon = new Dbconnection();
        try {
            PreparedStatement pres = dcon.cn().prepareStatement("SELECT COUNT(ACC_CODE) AS RESL FROM TB_ACC WHERE ACC_CODE=? ;");
            pres.setString(1, id);
            ResultSet res = pres.executeQuery();
            while (res.next()) {
                counrow = res.getInt("RESL");
            }
            if (counrow > 0) {
                resl = true;
            } else {
                resl = false;
            }
        } catch (SQLException ex) {
            Logger.getLogger(OneforAllfunc.class.getName()).log(Level.SEVERE, null, ex);
        }
        return resl;
    }

}
