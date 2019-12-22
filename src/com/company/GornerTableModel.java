package com.company;
import javax.swing.table.AbstractTableModel;


class GornerTable extends AbstractTableModel {

    private Double[] coeff;
    private Double from;
    private Double to;
    private Double step;
    private String search=null;
    private Boolean A;

    public GornerTable(Double from, Double to, Double step, Double[] coeff)
    {
        this.from = from;
        this.to = to;
        this.step = step;
        this.coeff = coeff;
    }



    public Double getFrom() {
        return from;
    }

    public Double getTo() {
        return to;
    }

    public Double getStep() {
        return step;
    }

    @Override
    public int getRowCount() {
        if(((to-from)/step)-(int)((to-from)/step)==0.0)
            return new Double(Math.ceil((to-from)/step)).intValue()+1;
        else
            return new Double(Math.ceil((to-from)/step)).intValue();
    }

    @Override
    public int getColumnCount() {
        return 4;
    }

    @Override
    public String getColumnName(int col) {
        switch (col)
        {
            case 0:
                return "Значение X";
            case 1:
                return "Значение многочлена при прямом порядке коэффицентов";
            case 2:
                return "Значение многочлена при float";
            default:
                return "Разница между значениями";
        }
    }

    @Override
    public Class<?> getColumnClass(int col) {
        return Double.class;
    }

    @Override
    public Object getValueAt(int row, int col) {
        Double x=from+step*row;
        Double result1=0.0;
        float result2= Float.valueOf(0);

        for(int i=0;i<coeff.length-1;i++)
        {
            Double temp=coeff[i]+result1;
            result1=temp*x;
        }
        result1+=coeff[coeff.length-1];


        for(int i=0; i<  coeff.length -1; i++)
        {

            Float temp = coeff[i].floatValue()+result2;
            result2=temp*x.floatValue();;
        }
        result2+=coeff[coeff.length-1].floatValue();;

        if(col==0)
            return x;

        else if(col==1)
            return result1;
        else if (col==2){

            return result2;}
        else
            return result2-result1;
    }
    public void setSearch(String search) {
        this.search = search;
    }
}
