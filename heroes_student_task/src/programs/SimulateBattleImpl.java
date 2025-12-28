package com.heroes_task.programs;

import com.battle.heroes.army.Army;
import com.battle.heroes.army.Unit;
import com.battle.heroes.army.programs.PrintBattleLog;
import com.battle.heroes.army.programs.SimulateBattle;

import java.util.*;

public class SimulateBattleImpl implements SimulateBattle {

    private PrintBattleLog printBattleLog;

    @Override
    public void simulate(Army playerArmy, Army computerArmy) throws InterruptedException {
        if (playerArmy == null || computerArmy == null) return;

        List<Unit> playerUnits = playerArmy.getUnits();
        List<Unit> computerUnits = computerArmy.getUnits();
        if (playerUnits == null || computerUnits == null) return;

        while (hasAliveUnits(playerUnits) && hasAliveUnits(computerUnits)) {
            boolean roundHadAction = false;
            Set<Unit> processedThisRound = new HashSet<>();

            while (true) {
                List<Unit> turnOrder = createTurnOrder(playerUnits, computerUnits, processedThisRound);
                if (turnOrder.isEmpty()) break;

                Unit currentUnit = turnOrder.get(0);
                processedThisRound.add(currentUnit);

                Unit attackedTarget = null;
                try {
                    if (currentUnit.getProgram() != null) {
                        attackedTarget = currentUnit.getProgram().attack();
                }
                } catch (Exception ignored) {
                    attackedTarget = null;
                }

                if (attackedTarget != null) roundHadAction = true;

                if (printBattleLog != null) {
                    printBattleLog.printBattleLog(currentUnit, attackedTarget);
                }

                if (Thread.interrupted()) throw new InterruptedException();
            }

            if (!hasAliveUnits(playerUnits) || !hasAliveUnits(computerUnits)) break;
            if (!roundHadAction) break;
        }
    }

    private static List<Unit> createTurnOrder(List<Unit> army1, List<Unit> army2, Set<Unit> excluded) {
        List<Unit> order = new ArrayList<>();

        for (Unit u : army1) {
            if (u != null && u.isAlive() && !excluded.contains(u)) {
                order.add(u);
            }
        }
        for (Unit u : army2) {
            if (u != null && u.isAlive() && !excluded.contains(u)) {
                order.add(u);
            }
        }

        order.sort((u1, u2) -> {
            int attackDiff = Integer.compare(u2.getBaseAttack(), u1.getBaseAttack());
            if (attackDiff != 0) return attackDiff;
            return u1.getName().compareTo(u2.getName());
        });

        return order;
    }

    private static boolean hasAliveUnits(List<Unit> units) {
        for (Unit u : units) {
            if (u != null && u.isAlive()) return true;
        }
        return false;
        }
    }
