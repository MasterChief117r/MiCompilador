/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Codigo;

import java.awt.Color;
import java.awt.Component;
import java.util.ArrayList;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

/**
 *
 * @author MaterChief117
 */
public class RenderizadordeCeldasdeTabla extends DefaultTableCellRenderer {
    private ArrayList<Integer> wrong;

    public RenderizadordeCeldasdeTabla(ArrayList<Integer> wrong)
    {
        this.wrong = wrong;
    }
    
    @Override
    public Component getTableCellRendererComponent(JTable table, Object valor, boolean Seleccionado, boolean tieneEnfoque, int fila, int col)
    {
        // Cells are by default rendered as a JLabel
        JLabel l = (JLabel) super.getTableCellRendererComponent(table, valor, Seleccionado, tieneEnfoque, fila, col);
        
        if ( wrong.get(fila) == 1 )
            l.setBackground(Color.BLACK);
        else if ( fila % 2 == 0)
            l.setBackground(Color.LIGHT_GRAY);
        else
            l.setBackground(Color.white);
        
        // return the JLabel which renders the cell
        return l;
    }
}
