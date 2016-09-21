package ru.innopolis.mputilov;

import java.security.SecureRandom;
import java.util.*;
import java.util.stream.Collectors;

public class KruskalMaze {
    private static final String VERTICAL_PATH = "| ";
    private static final String EMPTY_VERTICAL_PATH = "  ";
    private static final String HORIZONTAL_PATH = "—";
    private static final String EMPTY_HORIZONTAL_PATH = " ";
    private static final String ROOM = "◊";
    final List<Integer> activeSet;
    final boolean[] alreadySeenCells;
    final Map<Integer, List<Integer>> paths = new HashMap<>();
    private final SecureRandom sr;
    private final int size;

    public KruskalMaze() {
        this(30);
    }

    public KruskalMaze(int size) {
        this.size = size;
        alreadySeenCells = new boolean[size * size];
        activeSet = new ArrayList<>();
        activeSet.add(0);
        alreadySeenCells[0] = true;
        sr = new SecureRandom();
        while (!activeSet.isEmpty()) {
            doIteration();
        }
    }

    protected Integer getAnyCellFromActiveSet() {
        int i = sr.nextInt(activeSet.size());
        return activeSet.get(i);
    }

    private Integer getAnyCellFromNeighbors(List<Integer> neighbors) {
        int i = sr.nextInt(neighbors.size());
        return neighbors.get(i);
    }

    protected void doIteration() {
        while (!activeSet.isEmpty()) {
            Integer randomTarget = getAnyCellFromActiveSet();
            List<Integer> unvisitedNeighbors = getUnvisitedNeighbors(randomTarget);
            if (unvisitedNeighbors.isEmpty()) {
                removeFromActiveSet(randomTarget);
                continue;
            }
            Integer neighbor = getAnyCellFromNeighbors(unvisitedNeighbors);
            activeSet.add(neighbor);
            alreadySeenCells[neighbor] = true;
            paths.putIfAbsent(randomTarget, new ArrayList<>());
            paths.get(randomTarget).add(neighbor);
        }
    }

    protected void removeFromActiveSet(Integer target) {
        activeSet.remove(target);
    }

    private boolean isUnvisitedCell(int cell) {
        return !alreadySeenCells[cell];
    }

    protected List<Integer> getUnvisitedNeighbors(int target) {
        return getNeighbors(target).stream().filter(this::isUnvisitedCell).collect(Collectors.toList());
    }

    private List<Integer> getNeighbors(int target) {
        List<Integer> neighbors = new ArrayList<>(4);
        int top = target - size;
        if (top >= 0) {
            neighbors.add(top);
        }
        int right = target + 1;
        if (right % size != 0) {
            neighbors.add(right);
        }
        int left = target - 1;
        if (target % size != 0) {
            neighbors.add(left);
        }
        int bottom = target + size;
        if (bottom <= size * size - 1) {
            neighbors.add(bottom);
        }
        return neighbors;
    }

    public String prettyPrint() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < size - 1; i++) {
            appendHorizontalRow(sb, i);
            appendVerticalPaths(sb, i);
        }
        appendHorizontalRow(sb, size - 1);
        return sb.toString();
    }

    private void appendVerticalPaths(StringBuilder sb, int row) {
        for (int column = 0; column < size; column++) {
            int current = getAbsoluteIndex(row, column);
            @SuppressWarnings("unchecked")
            List<Integer> connected = paths.getOrDefault(current, Collections.EMPTY_LIST);
            int next = getAbsoluteIndex(row + 1, column);
            @SuppressWarnings("unchecked")
            List<Integer> connected2 = paths.getOrDefault(next, Collections.EMPTY_LIST);

            sb.append(connected.contains(next) || connected2.contains(current) ? VERTICAL_PATH : EMPTY_VERTICAL_PATH);
        }
        sb.append("\n");
    }

    private void appendHorizontalRow(StringBuilder sb, int row) {
        for (int column = 0; column < size - 1; column++) {
            sb.append(ROOM);
            int current = getAbsoluteIndex(row, column);
            @SuppressWarnings("unchecked")
            List<Integer> connected = paths.getOrDefault(current, Collections.EMPTY_LIST);
            int next = getAbsoluteIndex(row, column + 1);
            @SuppressWarnings("unchecked")
            List<Integer> connected2 = paths.getOrDefault(next, Collections.EMPTY_LIST);
            sb.append(connected.contains(next) || connected2.contains(current) ? HORIZONTAL_PATH : EMPTY_HORIZONTAL_PATH);
        }
        sb.append(ROOM);
        sb.append('\n');
    }

    private int getAbsoluteIndex(int row, int column) {
        return size * row + column;
    }
}
