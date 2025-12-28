package com.heroes_task.programs;

import com.battle.heroes.army.Unit;
import com.battle.heroes.army.programs.SuitableForAttackUnitsFinder;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SuitableForAttackUnitsFinderImpl implements SuitableForAttackUnitsFinder {

    @Override
    public List<Unit> getSuitableUnits(List<List<Unit>> unitsByRow, boolean isLeftArmyTarget) {
        List<Unit> suitable = new ArrayList<>();
        if (unitsByRow == null) return suitable;

        Set<Long> occupiedPositions = new HashSet<>();
        List<Unit> allAliveUnits = new ArrayList<>();

        for (List<Unit> row : unitsByRow) {
            if (row == null) continue;
            for (Unit u : row) {
                if (u != null && u.isAlive()) {
                    allAliveUnits.add(u);
                    occupiedPositions.add(encodePosition(u.getxCoordinate(), u.getyCoordinate()));
                }
            }
        }

        int directionOffset = isLeftArmyTarget ? -1 : +1;

        for (Unit u : allAliveUnits) {
            long adjacentPos = encodePosition(u.getxCoordinate(), u.getyCoordinate() + directionOffset);
            if (!occupiedPositions.contains(adjacentPos)) {
                suitable.add(u);
            }
        }

        return suitable;
    }

    private static long encodePosition(int x, int y) {
        return (((long) x) << 32) ^ (y & 0xffffffffL);
    }
}
