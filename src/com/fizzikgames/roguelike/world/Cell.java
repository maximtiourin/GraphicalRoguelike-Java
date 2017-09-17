package com.fizzikgames.roguelike.world;

public class Cell {
    private int row;
    private int column;
    
    public Cell(int r, int c) {
        this.row = r;
        this.column = c;
    }
    
    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        
        if (other instanceof Cell) {
            Cell ocell = (Cell) other;
            if (getRow() == ocell.getRow() && getColumn() == ocell.getColumn()) {
                return true;
            }
        }
        
        return false;
    }
    
    @Override
    public int hashCode() {
        return (getRow() * getColumn()) + getRow() - getColumn();
    }
    
    public int getRow() { return row; }
    public void setRow(int r) { this.row = r; }
    public int getColumn() { return column; }  
    public void setColumn(int c) { this.column = c; }
    public void addRow(int amount) { this.row += amount; }
    public void addColumn(int amount) { this.column += amount; }
}
