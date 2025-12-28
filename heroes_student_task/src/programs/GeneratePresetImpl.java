package com.heroes_task.programs;

import com.battle.heroes.army.Army;
import com.battle.heroes.army.Unit;
import com.battle.heroes.army.programs.GeneratePreset;

import java.util.*;

public class GeneratePresetImpl implements GeneratePreset {

    private static final int WIDTH = 3;
    private static final int HEIGHT = 21;
    private final Random random = new Random();

    @Override
    public Army generate(List<Unit> unitList, int maxPoints) {
        List<Unit> sortedUnits = new ArrayList<>(unitList);
        sortedUnits.sort((u1, u2) -> {
            double efficiency1 = (double) u1.getBaseAttack() / u1.getCost();
            double efficiency2 = (double) u2.getBaseAttack() / u2.getCost();
            int cmp = Double.compare(efficiency2, efficiency1);
            if (cmp != 0) return cmp;
            double durability1 = (double) u1.getHealth() / u1.getCost();
            double durability2 = (double) u2.getHealth() / u2.getCost();
            return Double.compare(durability2, durability1);
        });

        LinkedList<Unit> availableUnits = new LinkedList<>(sortedUnits);
        Map<String, Integer> countsByType = new HashMap<>();
        List<Unit> generatedArmy = new ArrayList<>();
        int spentPoints = 0;

        while (maxPoints > 0 && !availableUnits.isEmpty()) {
            Unit template = availableUnits.peek();
            String unitType = template.getUnitType();
            int currentCount = countsByType.getOrDefault(unitType, 0);

            if (currentCount >= 11) {
                availableUnits.poll();
                continue;
            }

            if (maxPoints < template.getCost()) {
                availableUnits.poll();
                continue;
            }

            int[] position = findFreePosition(generatedArmy);
            if (position == null) {
                break;
            }

            currentCount++;
            countsByType.put(unitType, currentCount);
            Unit newUnit = new Unit(
                    unitType + " " + currentCount,
                    unitType,
                    template.getHealth(),
                    template.getBaseAttack(),
                    template.getCost(),
                    template.getAttackType(),
                    duplicateMap(template.getAttackBonuses()),
                    duplicateMap(template.getDefenceBonuses()),
                    position[0], position[1]
            );

            generatedArmy.add(newUnit);
            spentPoints += template.getCost();
            maxPoints -= template.getCost();
        }

        Army result = new Army(generatedArmy);
        result.setPoints(spentPoints);
        return result;
    }

    private int[] findFreePosition(List<Unit> existingUnits) {
        int attempts = 0;
        while (attempts < 100) {
            int x = random.nextInt(WIDTH);
            int y = random.nextInt(HEIGHT);

            boolean occupied = existingUnits.stream()
                    .anyMatch(u -> u.getxCoordinate() == x && u.getyCoordinate() == y);

            if (!occupied) {
                return new int[]{x, y};
            }
            attempts++;
        }
        return null;
    }

    private static Map<String, Double> duplicateMap(Map<String, Double> original) {
        return (original != null) ? new HashMap<>(original) : new HashMap<>();
    }
}
