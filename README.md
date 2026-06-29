# CalcPro 🧮

A scientific calculator Android app built with Java and XML as part of the SEN 104 / SEN 214 Mobile App Development course at Obafemi Awolowo University.

---

## What it does

CalcPro started as a basic calculator assignment and I ended up extending it to cover most of what a standard scientific calculator can do. It's split into four modes so the UI doesn't get cluttered.

---

## Modes

### Basic
The standard stuff — addition, subtraction, multiplication, division. Also has percentage, sign toggle, square root, square, power, factorial, log, ln, and the constants π and e.

### Trig
Handles sin, cos, tan and their inverses (sin⁻¹, cos⁻¹, tan⁻¹). You can switch between degree and radian mode depending on what you're working with.

### Hyp
Hyperbolic functions — sinh, cosh, tanh — and their inverses too.

### Stat
This one's a bit different. You type in a list of numbers separated by commas and it computes:
- Mean, Median, Mode
- Standard Deviation and Variance
- Sum

It also has nPr (permutations) and nCr (combinations) where you just enter n and r.

---

## Tech Stack

- **Language:** Java
- **UI:** XML layouts
- **IDE:** Android Studio
- **Min SDK:** API 24 (Android 7.0) — covers about 95% of devices

---

## Project Structure

```
app/
├── src/
│   └── main/
│       ├── java/com/sen204/scientificcalculator/
│       │   └── MainActivity.java
│       └── res/
│           └── layout/
│               └── activity_main.xml
```

---

## How to run it

1. Clone the repo or download the ZIP
2. Open in Android Studio
3. Let Gradle sync
4. Run on an emulator or physical Android device (API 24+)

---

## Course Info

**Course:** SEN 104 / SEN 214 — Introduction to Mobile App Development  
**Institution:** Obafemi Awolowo University, Ile-Ife  
**Developer:** ODUKOYA IBUKUNOLUWA EMMANUEL(Tedac)
