import com.google.ortools.sat.*;
import java.util.*;

public class HierarchicalKnapsackMip {
    public static void main(String[] args) {/*
        // Загрузка нативной библиотеки OR-Tools
        System.loadLibrary("jniortools");

        CpModel model = new CpModel();

        // === Данные задачи ===
        int[] weights = {10, 20, 15, 25, 30, 10};
        int[] values = {60, 100, 80, 90, 120, 50};
        int[] groups = {0, 0, 1, 1, 1, 1}; // 2 группы

        int capacity = 60;
        int n = weights.length;

        // Правила бонусов: для каждой группы — список {порог, бонус_прирост}
        Map<Integer, List<int[]>> groupBonusRules = new HashMap<>();
        groupBonusRules.put(0, Arrays.asList(new int[]{2, 30})); // группа 0: ≥2 → +30
        groupBonusRules.put(1, Arrays.asList(
                new int[]{2, 10}, // ≥2 → +10
                new int[]{3, 15}, // дополнительно за ≥3 → +15 (итого +25)
                new int[]{4, 20}  // дополнительно за ≥4 → +20 (итого +45)
        ));

        int numGroups = groupBonusRules.size();

        // === Переменные: выбираем предмет или нет (BoolVar) ===
        BoolVar[] x = new BoolVar[n];
        for (int i = 0; i < n; i++) {
            x[i] = model.newBoolVar("x" + i);
        }

        // === Ограничение по весу ===
        LinearExpr totalWeight = LinearExpr.scalProd(x, weights);
        model.addLessOrEqual(totalWeight, capacity);

        // === Базовая ценность ===
        LinearExpr baseValue = LinearExpr.scalProd(x, values);

        // === Бонусы за группы ===
        List<IntVar> bonusContributions = new ArrayList<>();

        for (int g = 0; g < numGroups; g++) {
            // Собираем предметы из группы g
            List<BoolVar> groupItems = new ArrayList<>();
            for (int i = 0; i < n; i++) {
                if (groups[i] == g) {
                    groupItems.add(x[i]);
                }
            }

            // Переменная: сколько предметов выбрано из группы
            IntVar count = model.newIntVar(0, groupItems.size(), "count_g" + g);
            model.addEquality(count, LinearExpr.sum(groupItems.toArray(new BoolVar[0])));

            // Обрабатываем каждый бонусный порог
            List<int[]> rules = groupBonusRules.get(g);
            for (int[] rule : rules) {
                int threshold = rule[0];
                int bonusAmount = rule[1];

                BoolVar bonusFlag = model.newBoolVar("bonus_g" + g + "_t" + threshold);

                // bonusFlag ⇔ (count >= threshold)
                model.addGreaterOrEqual(count, threshold).onlyEnforceIf(bonusFlag);
                model.addLessOrEqual(count, threshold - 1).onlyEnforceIf(bonusFlag.not());

                // Вклад бонуса в целевую функцию
                IntVar contribution = model.newIntVar(0, bonusAmount, "contrib_g" + g + "_t" + threshold);
                model.addEquality(contribution, LinearExpr.term(bonusFlag, bonusAmount));
                bonusContributions.add(contribution);
            }
        }

        // === Целевая функция: максимизировать базовую ценность + все бонусы ===
        LinearExpr totalBonus = LinearExpr.sum(bonusContributions.toArray(new IntVar[0]));
        LinearExpr objective = LinearExpr.sum(new LinearExpr[]{baseValue, totalBonus});
        model.setMaximize(objective);

        // === Решение ===
        CpSolver solver = new CpSolver();
        CpSolverStatus status = solver.solve(model);

        if (status == CpSolverStatus.OPTIMAL || status == CpSolverStatus.FEASIBLE) {
            System.out.println("✅ Найдено решение:");
            System.out.println("Общая ценность: " + solver.objectiveValue());
            System.out.println("Использованный вес: " + solver.value(totalWeight));

            System.out.println("\nВыбранные предметы:");
            for (int i = 0; i < n; i++) {
                if (solver.booleanValue(x[i])) {
                    System.out.printf("  Предмет %d: вес=%d, ценность=%d, группа=%d%n",
                            i, weights[i], values[i], groups[i]);
                }
            }

            System.out.println("\nБонусы по группам:");
            for (int g = 0; g < numGroups; g++) {
                int count = 0;
                for (int i = 0; i < n; i++) {
                    if (groups[i] == g && solver.booleanValue(x[i])) {
                        count++;
                    }
                }
                int totalBonusGroup = 0;
                List<int[]> rules = groupBonusRules.get(g);
                System.out.printf("  Группа %d: выбрано %d предметов → бонусы: ", g, count);
                for (int[] rule : rules) {
                    if (count >= rule[0]) {
                        totalBonusGroup += rule[1];
                        System.out.printf("+%d (≥%d) ", rule[1], rule[0]);
                    }
                }
                System.out.println("= +" + totalBonusGroup);
            }
        } else {
            System.out.println("❌ Решение не найдено.");
        }*/
    }
}