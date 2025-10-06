Built by Valeriia Holotiuk as a project focusing on 2D arrays, clean separation of logic/UI, and testing.

A small project for my Data Structures & Algorithms class that simulates a movie theater seating system using a 2D array. I built both a console app (to show the core logic clearly) and a JavaFX GUI (for a nicer, clickable experience).

---


- Displays a seating chart
  Each seat is represented by a char in a 2D array (`'O' = open`, `'X' = reserved`).

- Reserves a seat
  Enter the row/column (console) or click a seat (GUI). If a seat is taken, the app suggests the next available one.

- Cancels a reservation 
  Toggle a reserved seat back to open.

- Always shows the updated chart 
  After each action, you see the latest state.

---


- The core data structure is a 2D array(`char[][]`), which keeps the logic simple and efficient.
- Operations are intentionally O(1) for reserve/cancel on a known index, and O(n·m) for the “first available” scan.
- I separated concerns:
  - `SeatManager` → pure logic (easy to test)
  - `MovieTheaterApp` → console UI
  - `TheaterFX` → JavaFX UI

---

-Tech used:

- Java 17
- Maven
- JavaFX (controls, graphics)
- JUnit 5

---

~Projectt structure~

```
src/
  main/java/com/example/
    SeatManager.java        # 2D array logic
    MovieTheaterApp.java    # console UI
    TheaterFX.java          # JavaFX GUI
  test/java/com/example/
    SeatManagerTest.java    # unit tests 
```

---


~To run :
```bash
mvn exec:java
```

~GUI:
```bash
mvn javafx:run
```

~Test:
```bash
mvn test
```


~How to build~

```bash
mvn clean package

```


![alt text](<Screenshot 2025-10-06 at 6.35.38 PM.png>