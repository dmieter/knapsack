# Knapsack

- `src/` — библиотека `local.knapsack.group:local.knapsack.artifact:1.0`.
- `cpsat-maven-project/` — приложение на Google OR-Tools.

## Сборка

Сначала собирается библиотека Knapsack, затем она используется в проекте `cpsat-maven-project`:

```
mvn -f src/pom.xml clean install
mvn -f cpsat-maven-project/pom.xml clean install
```
