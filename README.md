# Knapsack

- `knapsack-mvn/` — библиотека `local.knapsack.group:local.knapsack.artifact:1.0`.
- `cpsat-maven-project/` — приложение на Google OR-Tools, реализующее Иерархический групповой рюкзак.
- `src/` — легаси-проект Knapsack (сборка Ant), скопирован/перенесен в мавен проект `knapsack-mvn` 22/09/2026.

## Сборка

Сначала собирается библиотека Knapsack, затем она через m2 используется в проекте `cpsat-maven-project`:

```
mvn -f knapsack-mvn/pom.xml clean install
mvn -f cpsat-maven-project/pom.xml clean install
```
