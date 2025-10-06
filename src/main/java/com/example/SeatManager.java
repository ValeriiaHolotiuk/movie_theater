package com.example;

import java.util.Arrays;
import java.util.Optional;


public class SeatManager {
    private final char[][] seats;

    public SeatManager(int rows, int cols) {
        if (rows <= 0 || cols <= 0) {
            throw new IllegalArgumentException("rows/cols must be > 0");
        }
        seats = new char[rows][cols];
        for (int r = 0; r < rows; r++) {
            Arrays.fill(seats[r], 'O');
        }
    }

    public int getRows() { return seats.length; }
    public int getCols() { return seats[0].length; }

    public boolean isValid(int r, int c) {
        return r >= 0 && r < getRows() && c >= 0 && c < getCols();
    }

    public boolean isAvailable(int r, int c) {
        return isValid(r, c) && seats[r][c] == 'O';
    }

   
    public boolean reserve(int r, int c) {
        if (!isValid(r, c) || seats[r][c] == 'X') return false;
        seats[r][c] = 'X';
        return true;
    }

    public boolean cancel(int r, int c) {
        if (!isValid(r, c) || seats[r][c] == 'O') return false;
        seats[r][c] = 'O';
        return true;
    }

    public Optional<int[]> suggestFirstAvailable() {
        for (int r = 0; r < getRows(); r++) {
            for (int c = 0; c < getCols(); c++) {
                if (seats[r][c] == 'O') return Optional.of(new int[]{r, c});
            }
        }
        return Optional.empty();
    }

    public String render() {
        StringBuilder sb = new StringBuilder("Current Seating Chart (O=open, X=reserved)\n");
        for (int r = 0; r < getRows(); r++) {
            sb.append("Row ").append(r + 1).append(": ");
            for (int c = 0; c < getCols(); c++) {
                sb.append(seats[r][c]).append(' ');
            }
            sb.append('\n');
        }
        return sb.toString();
    }


    public String seatLabel(int r, int c) {
        return "" + (char)('A' + r) + (c + 1);
    }

    public char[][] snapshot() {
        char[][] copy = new char[getRows()][getCols()];
        for (int r = 0; r < getRows(); r++) {
            copy[r] = Arrays.copyOf(seats[r], seats[r].length);
        }
        return copy;
    }
}
