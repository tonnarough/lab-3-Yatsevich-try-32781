package com.company;
import javax.swing.*;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;


public class GornerTableCell  implements TableCellRenderer{

    private String search=null;
    private boolean searchFrom=false;
    private JPanel panel=new JPanel();
    private JLabel label=new JLabel();
    private DecimalFormat formatter = (DecimalFormat) NumberFormat.getInstance();

    public GornerTableCell(){
        formatter.setMaximumFractionDigits(5);
        formatter.setGroupingUsed(false);
        DecimalFormatSymbols dottedDouble = formatter.getDecimalFormatSymbols();
        dottedDouble.setDecimalSeparator('.');
        formatter.setDecimalFormatSymbols(dottedDouble);
        panel.add(label);
        panel.setLayout(new FlowLayout(FlowLayout.LEFT));
    }
    public static boolean IsPalindrome(String s) {
        int i = 0, j = s.length() - 1;
        while(i < j) {
            if (s.charAt(i) != s.charAt(j)) return false;
            i++; j--;
        }
        return true;
    }

    @Override
    public Component getTableCellRendererComponent(JTable jTable, Object value, boolean b, boolean b1, int row, int col) {
        String formattedDouble = formatter.format(value);
        label.setText(formattedDouble);

        if((col==1||col==2) && search!=null && search.equals(formattedDouble)){
            panel.setBackground(Color.RED);
            label.setForeground(Color.BLACK);
            formatter.setGroupingUsed(false);

        }
        else if(((col==1 || col==3) && row%2!=0) || ((col==0|| col==2) && row%2==0)){
            panel.setBackground(Color.WHITE);
            label.setForeground(Color.BLACK);
        }
        else {
            panel.setBackground(Color.BLACK);
            label.setForeground(Color.WHITE);
        }

        if(searchFrom==true  && (col==2 || col==1)  &&     // диапазон
                IsPalindrome(formattedDouble)) {
            panel.setBackground(Color.BLUE);
            label.setForeground(Color.WHITE);
        }
        return panel;
    }

    public void setSearch(String search) {
        this.search = search;
    }
    public void setdiap(boolean searchFrom) {
        this.searchFrom = searchFrom;
    }
}