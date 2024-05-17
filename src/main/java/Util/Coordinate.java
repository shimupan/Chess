package Util;

public class Coordinate {
    public int row, col;

    public Coordinate(int row, int col) {
        this.row = row;
        this.col = col;
    }

    public Coordinate(Coordinate other) {
        this.row = other.row;
        this.col = other.col;
    }

    @Override
    public boolean equals(Object o) {
        if(!(o instanceof Coordinate)) {return false;}
        Coordinate c = (Coordinate) o;
        return this.row == c.row && this.col == c.col;
    }

    @Override
    public int hashCode() {
        int result = 17;
        result = 31 * result + row;
        result = 31 * result + col;
        return result;
    }

    @Override
    public String toString() {
        return "Coordinate{" + "row=" + row + ", col=" + col + '}';
    }
}