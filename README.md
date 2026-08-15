# Personal Finance Tracker

Applicazione web per la gestione personale di spese, categorie, budget mensili e obiettivi di risparmio. I dati sono separati per utente autenticato.

Il repository contiene il codice sorgente, le storie utente, i criteri di accettazione e i test automatici richiesti per la valutazione del progetto.

## Funzionalità

- registrazione, login e logout;
- dashboard con riepiloghi, grafici e avvisi sui budget;
- inserimento, modifica, eliminazione e filtro delle spese;
- gestione di categorie e budget mensili;
- gestione di obiettivi di risparmio e contributi.

## Tecnologie

Java 17, Spring Boot 3.2.5, Spring MVC, Thymeleaf, Spring Security, Spring Data JPA, PostgreSQL e Maven. I test usano JUnit 5, Mockito, Cucumber, Selenium e un database H2 in memoria.

## Avvio rapido

Sono necessari JDK 17, Maven e PostgreSQL. Creare il database `finance_tracker`, quindi impostare la password:

```powershell
$env:DB_PASSWORD = "<password-postgresql>"
mvn spring-boot:run
```

L'applicazione è disponibile su [http://localhost:8080](http://localhost:8080). `DB_URL` e `DB_USERNAME` sono personalizzabili usando le variabili mostrate in [`.env.example`](.env.example). Spring Boot non carica automaticamente il file `.env`: le variabili devono essere esportate nella shell o configurate nell'IDE.

## Test

I 25 test unitari si eseguono con:

```powershell
mvn test
```

La verifica completa esegue anche 26 scenari Cucumber/Selenium, per un totale di 51 test:

```powershell
mvn clean verify -Pacceptance
```

I test usano H2 in memoria e non modificano il database PostgreSQL applicativo. L'ultima verifica completa, eseguita il 15/08/2026, si è conclusa con 51 test superati e nessun errore.

## Storie e test di accettazione

Ogni feature contiene la storia nella forma `As / I want / So that` e i relativi criteri di accettazione eseguibili come scenari `Given / When / Then`.

| Storia | Feature | Scenari |
|---|---|---:|
| UC01 - Registrazione | [`UC01_register.feature`](src/test/resources/features/UC01_register.feature) | 3 |
| UC02 - Login | [`UC02_login.feature`](src/test/resources/features/UC02_login.feature) | 3 |
| UC03 - Logout | [`UC03_logout.feature`](src/test/resources/features/UC03_logout.feature) | 1 |
| UC04 - Inserimento spesa | [`UC04_add_expense.feature`](src/test/resources/features/UC04_add_expense.feature) | 2 |
| UC05 - Modifica spesa | [`UC05_edit_expense.feature`](src/test/resources/features/UC05_edit_expense.feature) | 1 |
| UC06 - Eliminazione spesa | [`UC06_delete_expense.feature`](src/test/resources/features/UC06_delete_expense.feature) | 1 |
| UC07 - Elenco e filtri spese | [`UC07_view_expense_list.feature`](src/test/resources/features/UC07_view_expense_list.feature) | 2 |
| UC08 - Gestione categorie | [`UC08_manage_categories.feature`](src/test/resources/features/UC08_manage_categories.feature) | 4 |
| UC09 - Budget mensili | [`UC09_set_budget.feature`](src/test/resources/features/UC09_set_budget.feature) | 3 |
| UC10 - Progresso budget | [`UC10_view_budget_progress.feature`](src/test/resources/features/UC10_view_budget_progress.feature) | 1 |
| UC11 - Alert budget | [`UC11_budget_alert.feature`](src/test/resources/features/UC11_budget_alert.feature) | 1 |
| UC12 - Obiettivi di risparmio | [`UC12_manage_savings_goals.feature`](src/test/resources/features/UC12_manage_savings_goals.feature) | 4 |
| **Totale** | **12 storie** | **26** |

I test unitari dei service sono in [`src/test/java/com/financetracker/unit/`](src/test/java/com/financetracker/unit/).
