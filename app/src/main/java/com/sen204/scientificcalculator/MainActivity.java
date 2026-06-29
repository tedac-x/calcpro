package com.sen204.scientificcalculator;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * SEN 104 / SEN 214 — Scientific Calculator App
 * Author: Tedac
 *
 * Features:
 *  - Basic arithmetic: +, -, ×, ÷, %, +/-
 *  - Scientific: √, x², xʸ, n!, log, ln, π, e
 *  - Trigonometry: sin, cos, tan, sin⁻¹, cos⁻¹, tan⁻¹ (DEG & RAD)
 *  - Hyperbolic: sinh, cosh, tanh, sinh⁻¹, cosh⁻¹, tanh⁻¹
 *  - Statistics: Mean, Median, Mode, Std Dev, Variance, Sum
 *  - Combinatorics: nPr (Permutations), nCr (Combinations)
 */
public class MainActivity extends AppCompatActivity {

    // ── Display ──────────────────────────────────────────────────────────────
    private TextView tvResult, tvExpression, tvStatResult;

    // ── Panels ───────────────────────────────────────────────────────────────
    private LinearLayout panelBasic, panelTrig, panelHyp, panelStat;

    // ── Stat / Combo inputs ───────────────────────────────────────────────────
    private EditText etStatInput, etN, etR;

    // ── Calculator State ──────────────────────────────────────────────────────
    private String currentInput = "";       // number being typed
    private double firstOperand = 0;
    private String pendingOperator = "";
    private boolean resultJustShown = false;
    private boolean inDegreeMode = true;    // true = degrees, false = radians
    private boolean awaitingSecondOperand = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Bind display views
        tvResult     = findViewById(R.id.tvResult);
        tvExpression = findViewById(R.id.tvExpression);
        tvStatResult = findViewById(R.id.tvStatResult);

        // Bind panels
        panelBasic = findViewById(R.id.panelBasic);
        panelTrig  = findViewById(R.id.panelTrig);
        panelHyp   = findViewById(R.id.panelHyp);
        panelStat  = findViewById(R.id.panelStat);

        // Bind stat inputs
        etStatInput = findViewById(R.id.etStatInput);
        etN         = findViewById(R.id.etN);
        etR         = findViewById(R.id.etR);

        setupModeButtons();
        setupBasicPanel();
        setupTrigPanel();
        setupHypPanel();
        setupStatPanel();
    }

    // ══════════════════════════════════════════════════════════════════════════
    //  MODE TABS
    // ══════════════════════════════════════════════════════════════════════════
    private void setupModeButtons() {
        findViewById(R.id.btnModeBasic).setOnClickListener(v -> showPanel("basic"));
        findViewById(R.id.btnModeTrig).setOnClickListener(v  -> showPanel("trig"));
        findViewById(R.id.btnModeHyp).setOnClickListener(v   -> showPanel("hyp"));
        findViewById(R.id.btnModeStat).setOnClickListener(v  -> showPanel("stat"));
    }

    private void showPanel(String panel) {
        panelBasic.setVisibility(panel.equals("basic") ? View.VISIBLE : View.GONE);
        panelTrig.setVisibility(panel.equals("trig")   ? View.VISIBLE : View.GONE);
        panelHyp.setVisibility(panel.equals("hyp")     ? View.VISIBLE : View.GONE);
        panelStat.setVisibility(panel.equals("stat")   ? View.VISIBLE : View.GONE);
    }

    // ══════════════════════════════════════════════════════════════════════════
    //  BASIC PANEL
    // ══════════════════════════════════════════════════════════════════════════
    private void setupBasicPanel() {
        // Digit buttons
        int[] digitIds = {R.id.btn0, R.id.btn1, R.id.btn2, R.id.btn3, R.id.btn4,
                          R.id.btn5, R.id.btn6, R.id.btn7, R.id.btn8, R.id.btn9};
        String[] digits = {"0","1","2","3","4","5","6","7","8","9"};
        for (int i = 0; i < digitIds.length; i++) {
            final String d = digits[i];
            findViewById(digitIds[i]).setOnClickListener(v -> appendDigit(d));
        }

        // Decimal point
        findViewById(R.id.btnDot).setOnClickListener(v -> appendDot());

        // Operators
        findViewById(R.id.btnAdd).setOnClickListener(v      -> handleOperator("+"));
        findViewById(R.id.btnSubtract).setOnClickListener(v -> handleOperator("-"));
        findViewById(R.id.btnMultiply).setOnClickListener(v -> handleOperator("×"));
        findViewById(R.id.btnDivide).setOnClickListener(v   -> handleOperator("÷"));

        // Utility
        findViewById(R.id.btnEquals).setOnClickListener(v  -> calculateResult());
        findViewById(R.id.btnClear).setOnClickListener(v   -> clearAll());
        findViewById(R.id.btnDel).setOnClickListener(v     -> deleteLastChar());
        findViewById(R.id.btnSign).setOnClickListener(v    -> toggleSign());
        findViewById(R.id.btnPercent).setOnClickListener(v -> applyPercent());

        // Scientific
        findViewById(R.id.btnSqrt).setOnClickListener(v     -> applyUnary("sqrt"));
        findViewById(R.id.btnSquare).setOnClickListener(v   -> applyUnary("square"));
        findViewById(R.id.btnPow).setOnClickListener(v      -> handleOperator("^"));
        findViewById(R.id.btnFactorial).setOnClickListener(v-> applyUnary("factorial"));
        findViewById(R.id.btnLog).setOnClickListener(v      -> applyUnary("log"));
        findViewById(R.id.btnLn).setOnClickListener(v       -> applyUnary("ln"));
        findViewById(R.id.btnPi).setOnClickListener(v       -> insertConstant(Math.PI));
        findViewById(R.id.btnE).setOnClickListener(v        -> insertConstant(Math.E));
    }

    // ── Core input logic ──────────────────────────────────────────────────────

    private void appendDigit(String digit) {
        if (resultJustShown) { currentInput = ""; resultJustShown = false; }
        if (currentInput.equals("0") && !digit.equals(".")) currentInput = "";
        currentInput += digit;
        tvResult.setText(currentInput);
    }

    private void appendDot() {
        if (resultJustShown) { currentInput = "0"; resultJustShown = false; }
        if (!currentInput.contains(".")) {
            if (currentInput.isEmpty()) currentInput = "0";
            currentInput += ".";
            tvResult.setText(currentInput);
        }
    }

    private void handleOperator(String op) {
        if (!currentInput.isEmpty()) {
            if (awaitingSecondOperand) {
                // Chain calculation
                calculateResult();
            }
            firstOperand = parseDouble(currentInput);
            pendingOperator = op;
            tvExpression.setText(formatNum(firstOperand) + " " + op);
            currentInput = "";
            awaitingSecondOperand = true;
        } else if (awaitingSecondOperand) {
            // Just change the operator
            pendingOperator = op;
            tvExpression.setText(formatNum(firstOperand) + " " + op);
        }
        resultJustShown = false;
    }

    private void calculateResult() {
        if (pendingOperator.isEmpty() || currentInput.isEmpty()) return;
        double second = parseDouble(currentInput);
        double result;
        switch (pendingOperator) {
            case "+": result = firstOperand + second; break;
            case "-": result = firstOperand - second; break;
            case "×": result = firstOperand * second; break;
            case "÷":
                if (second == 0) { showError("Cannot divide by zero"); return; }
                result = firstOperand / second;
                break;
            case "^": result = Math.pow(firstOperand, second); break;
            default:  result = second;
        }
        tvExpression.setText(formatNum(firstOperand) + " " + pendingOperator + " " + formatNum(second) + " =");
        currentInput = formatNum(result);
        tvResult.setText(currentInput);
        pendingOperator = "";
        awaitingSecondOperand = false;
        resultJustShown = true;
    }

    private void clearAll() {
        currentInput = "";
        firstOperand = 0;
        pendingOperator = "";
        awaitingSecondOperand = false;
        resultJustShown = false;
        tvResult.setText("0");
        tvExpression.setText("");
    }

    private void deleteLastChar() {
        if (!currentInput.isEmpty()) {
            currentInput = currentInput.substring(0, currentInput.length() - 1);
            tvResult.setText(currentInput.isEmpty() ? "0" : currentInput);
        }
    }

    private void toggleSign() {
        if (!currentInput.isEmpty() && !currentInput.equals("0")) {
            if (currentInput.startsWith("-")) currentInput = currentInput.substring(1);
            else currentInput = "-" + currentInput;
            tvResult.setText(currentInput);
        }
    }

    private void applyPercent() {
        if (!currentInput.isEmpty()) {
            double val = parseDouble(currentInput) / 100.0;
            currentInput = formatNum(val);
            tvResult.setText(currentInput);
        }
    }

    private void applyUnary(String op) {
        double val = currentInput.isEmpty() ? 0 : parseDouble(currentInput);
        double result;
        switch (op) {
            case "sqrt":
                if (val < 0) { showError("Invalid: √ of negative"); return; }
                result = Math.sqrt(val); break;
            case "square":
                result = val * val; break;
            case "factorial":
                if (val < 0 || val != Math.floor(val)) { showError("n! requires non-negative integer"); return; }
                result = factorial((long) val); break;
            case "log":
                if (val <= 0) { showError("log undefined for ≤ 0"); return; }
                result = Math.log10(val); break;
            case "ln":
                if (val <= 0) { showError("ln undefined for ≤ 0"); return; }
                result = Math.log(val); break;
            default: return;
        }
        tvExpression.setText(op + "(" + formatNum(val) + ") =");
        currentInput = formatNum(result);
        tvResult.setText(currentInput);
        resultJustShown = true;
    }

    private void insertConstant(double c) {
        currentInput = formatNum(c);
        tvResult.setText(currentInput);
        resultJustShown = false;
    }

    // ══════════════════════════════════════════════════════════════════════════
    //  TRIG PANEL
    // ══════════════════════════════════════════════════════════════════════════
    private void setupTrigPanel() {
        // Numpad for trig
        int[] tDigitIds = {R.id.btnT0,R.id.btnT1,R.id.btnT2,R.id.btnT3,R.id.btnT4,
                           R.id.btnT5,R.id.btnT6,R.id.btnT7,R.id.btnT8,R.id.btnT9};
        String[] tDigits = {"0","1","2","3","4","5","6","7","8","9"};
        for (int i = 0; i < tDigitIds.length; i++) {
            final String d = tDigits[i];
            findViewById(tDigitIds[i]).setOnClickListener(v -> appendDigit(d));
        }
        findViewById(R.id.btnTDot).setOnClickListener(v  -> appendDot());
        findViewById(R.id.btnTDel).setOnClickListener(v  -> deleteLastChar());
        findViewById(R.id.btnTClear).setOnClickListener(v -> clearAll());

        // Mode
        findViewById(R.id.btnDeg).setOnClickListener(v -> {
            inDegreeMode = true;
            tvExpression.setText("Mode: DEG");
        });
        findViewById(R.id.btnRad).setOnClickListener(v -> {
            inDegreeMode = false;
            tvExpression.setText("Mode: RAD");
        });

        // Trig functions
        findViewById(R.id.btnSin).setOnClickListener(v  -> applyTrig("sin",  false));
        findViewById(R.id.btnCos).setOnClickListener(v  -> applyTrig("cos",  false));
        findViewById(R.id.btnTan).setOnClickListener(v  -> applyTrig("tan",  false));
        findViewById(R.id.btnAsin).setOnClickListener(v -> applyTrig("asin", true));
        findViewById(R.id.btnAcos).setOnClickListener(v -> applyTrig("acos", true));
        findViewById(R.id.btnAtan).setOnClickListener(v -> applyTrig("atan", true));
    }

    private void applyTrig(String fn, boolean isInverse) {
        double val = currentInput.isEmpty() ? 0 : parseDouble(currentInput);
        double angleRad;
        double result;

        if (!isInverse) {
            // Forward: convert input angle to radians if in degree mode
            angleRad = inDegreeMode ? Math.toRadians(val) : val;
            switch (fn) {
                case "sin": result = Math.sin(angleRad); break;
                case "cos": result = Math.cos(angleRad); break;
                case "tan":
                    if (inDegreeMode && (val % 180 == 90)) { showError("tan undefined here"); return; }
                    result = Math.tan(angleRad); break;
                default: return;
            }
            tvExpression.setText(fn + "(" + formatNum(val) + "°) =");
        } else {
            // Inverse: result in radians, convert to degrees if needed
            switch (fn) {
                case "asin":
                    if (val < -1 || val > 1) { showError("sin⁻¹ requires input in [-1, 1]"); return; }
                    result = Math.asin(val); break;
                case "acos":
                    if (val < -1 || val > 1) { showError("cos⁻¹ requires input in [-1, 1]"); return; }
                    result = Math.acos(val); break;
                case "atan": result = Math.atan(val); break;
                default: return;
            }
            if (inDegreeMode) result = Math.toDegrees(result);
            tvExpression.setText(fn + "⁻¹(" + formatNum(val) + ") = " + (inDegreeMode ? "°" : "rad"));
        }

        // Fix floating-point near-zero artifacts (e.g. sin(180°) = ~1e-16)
        if (Math.abs(result) < 1e-10) result = 0;

        currentInput = formatNum(result);
        tvResult.setText(currentInput);
        resultJustShown = true;
    }

    // ══════════════════════════════════════════════════════════════════════════
    //  HYPERBOLIC PANEL
    // ══════════════════════════════════════════════════════════════════════════
    private void setupHypPanel() {
        int[] hDigitIds = {R.id.btnH0,R.id.btnH1,R.id.btnH2,R.id.btnH3,R.id.btnH4,
                           R.id.btnH5,R.id.btnH6,R.id.btnH7,R.id.btnH8,R.id.btnH9};
        String[] hDigits = {"0","1","2","3","4","5","6","7","8","9"};
        for (int i = 0; i < hDigitIds.length; i++) {
            final String d = hDigits[i];
            findViewById(hDigitIds[i]).setOnClickListener(v -> appendDigit(d));
        }
        findViewById(R.id.btnHDot).setOnClickListener(v  -> appendDot());
        findViewById(R.id.btnHDel).setOnClickListener(v  -> deleteLastChar());
        findViewById(R.id.btnHClear).setOnClickListener(v -> clearAll());

        findViewById(R.id.btnSinh).setOnClickListener(v  -> applyHyp("sinh",  false));
        findViewById(R.id.btnCosh).setOnClickListener(v  -> applyHyp("cosh",  false));
        findViewById(R.id.btnTanh).setOnClickListener(v  -> applyHyp("tanh",  false));
        findViewById(R.id.btnAsinh).setOnClickListener(v -> applyHyp("asinh", true));
        findViewById(R.id.btnAcosh).setOnClickListener(v -> applyHyp("acosh", true));
        findViewById(R.id.btnAtanh).setOnClickListener(v -> applyHyp("atanh", true));
    }

    private void applyHyp(String fn, boolean isInverse) {
        double val = currentInput.isEmpty() ? 0 : parseDouble(currentInput);
        double result;
        switch (fn) {
            case "sinh":  result = Math.sinh(val); break;
            case "cosh":  result = Math.cosh(val); break;
            case "tanh":  result = Math.tanh(val); break;
            case "asinh": result = Math.log(val + Math.sqrt(val * val + 1)); break;
            case "acosh":
                if (val < 1) { showError("cosh⁻¹ requires input ≥ 1"); return; }
                result = Math.log(val + Math.sqrt(val * val - 1)); break;
            case "atanh":
                if (val <= -1 || val >= 1) { showError("tanh⁻¹ requires input in (-1, 1)"); return; }
                result = 0.5 * Math.log((1 + val) / (1 - val)); break;
            default: return;
        }
        String label = isInverse ? fn + "⁻¹" : fn;
        tvExpression.setText(label + "(" + formatNum(val) + ") =");
        currentInput = formatNum(result);
        tvResult.setText(currentInput);
        resultJustShown = true;
    }

    // ══════════════════════════════════════════════════════════════════════════
    //  STATISTICS & COMBINATORICS PANEL
    // ══════════════════════════════════════════════════════════════════════════
    private void setupStatPanel() {
        findViewById(R.id.btnMean).setOnClickListener(v     -> computeStat("mean"));
        findViewById(R.id.btnMedian).setOnClickListener(v   -> computeStat("median"));
        findViewById(R.id.btnMode).setOnClickListener(v     -> computeStat("mode"));
        findViewById(R.id.btnStdDev).setOnClickListener(v   -> computeStat("stddev"));
        findViewById(R.id.btnVariance).setOnClickListener(v -> computeStat("variance"));
        findViewById(R.id.btnSum).setOnClickListener(v      -> computeStat("sum"));
        findViewById(R.id.btnNPR).setOnClickListener(v      -> computeCombo("nPr"));
        findViewById(R.id.btnNCR).setOnClickListener(v      -> computeCombo("nCr"));
    }

    private void computeStat(String op) {
        String raw = etStatInput.getText().toString().trim();
        if (raw.isEmpty()) { tvStatResult.setText("Please enter numbers"); return; }
        String[] parts = raw.split(",");
        double[] nums = new double[parts.length];
        try {
            for (int i = 0; i < parts.length; i++) nums[i] = Double.parseDouble(parts[i].trim());
        } catch (NumberFormatException e) {
            tvStatResult.setText("Invalid input. Use format: 2, 4, 6");
            return;
        }
        double result;
        String label;
        switch (op) {
            case "mean":
                result = mean(nums);
                label = "Mean";
                tvStatResult.setText(label + " = " + formatNum(result));
                break;
            case "median":
                result = median(nums);
                label = "Median";
                tvStatResult.setText(label + " = " + formatNum(result));
                break;
            case "mode":
                String modeStr = mode(nums);
                tvStatResult.setText("Mode = " + modeStr);
                return;
            case "stddev":
                result = stdDev(nums);
                label = "Std Dev (σ)";
                tvStatResult.setText(label + " = " + formatNum(result));
                break;
            case "variance":
                result = variance(nums);
                label = "Variance (σ²)";
                tvStatResult.setText(label + " = " + formatNum(result));
                break;
            case "sum":
                result = sum(nums);
                label = "Sum";
                tvStatResult.setText(label + " = " + formatNum(result));
                break;
            default:
                tvStatResult.setText("Unknown operation");
        }
    }

    private void computeCombo(String op) {
        String nStr = etN.getText().toString().trim();
        String rStr = etR.getText().toString().trim();
        if (nStr.isEmpty() || rStr.isEmpty()) {
            tvStatResult.setText("Enter both n and r");
            return;
        }
        long n, r;
        try {
            n = Long.parseLong(nStr);
            r = Long.parseLong(rStr);
        } catch (NumberFormatException e) {
            tvStatResult.setText("n and r must be whole numbers");
            return;
        }
        if (n < 0 || r < 0 || r > n) {
            tvStatResult.setText("Need: n ≥ r ≥ 0");
            return;
        }
        if (op.equals("nPr")) {
            double result = permutation(n, r);
            tvStatResult.setText("P(" + n + ", " + r + ") = " + formatNum(result));
        } else {
            double result = combination(n, r);
            tvStatResult.setText("C(" + n + ", " + r + ") = " + formatNum(result));
        }
    }

    // ══════════════════════════════════════════════════════════════════════════
    //  MATH HELPERS
    // ══════════════════════════════════════════════════════════════════════════

    private double factorial(long n) {
        if (n > 20) return Double.POSITIVE_INFINITY; // prevent overflow
        double f = 1;
        for (long i = 2; i <= n; i++) f *= i;
        return f;
    }

    private double permutation(long n, long r) {
        // nPr = n! / (n-r)!
        double result = 1;
        for (long i = n; i > n - r; i--) result *= i;
        return result;
    }

    private double combination(long n, long r) {
        // nCr = n! / (r! * (n-r)!)
        if (r > n - r) r = n - r; // symmetry optimization
        double result = 1;
        for (long i = 0; i < r; i++) {
            result = result * (n - i) / (i + 1);
        }
        return result;
    }

    private double sum(double[] nums) {
        double s = 0;
        for (double v : nums) s += v;
        return s;
    }

    private double mean(double[] nums) {
        return sum(nums) / nums.length;
    }

    private double median(double[] nums) {
        double[] sorted = nums.clone();
        Arrays.sort(sorted);
        int mid = sorted.length / 2;
        return (sorted.length % 2 == 0) ? (sorted[mid - 1] + sorted[mid]) / 2.0 : sorted[mid];
    }

    private String mode(double[] nums) {
        Map<Double, Integer> freq = new HashMap<>();
        for (double v : nums) freq.put(v, freq.getOrDefault(v, 0) + 1);
        int maxFreq = Collections.max(freq.values());
        if (maxFreq == 1) return "No mode (all values unique)";
        List<String> modes = new ArrayList<>();
        for (Map.Entry<Double, Integer> e : freq.entrySet()) {
            if (e.getValue() == maxFreq) modes.add(formatNum(e.getKey()));
        }
        Collections.sort(modes);
        return String.join(", ", modes);
    }

    private double variance(double[] nums) {
        double m = mean(nums);
        double sumSq = 0;
        for (double v : nums) sumSq += (v - m) * (v - m);
        return sumSq / nums.length;
    }

    private double stdDev(double[] nums) {
        return Math.sqrt(variance(nums));
    }

    // ══════════════════════════════════════════════════════════════════════════
    //  UTILITIES
    // ══════════════════════════════════════════════════════════════════════════

    private double parseDouble(String s) {
        try { return Double.parseDouble(s); }
        catch (NumberFormatException e) { return 0; }
    }

    /**
     * Format a double: show as integer if it is a whole number,
     * otherwise show up to 8 significant decimal digits.
     */
    private String formatNum(double val) {
        if (Double.isNaN(val))      return "Error";
        if (Double.isInfinite(val)) return val > 0 ? "∞" : "-∞";
        if (val == Math.floor(val) && Math.abs(val) < 1e15) {
            return String.valueOf((long) val);
        }
        // Strip trailing zeros
        String s = String.format("%.8f", val);
        s = s.replaceAll("0*$", "").replaceAll("\\.$", "");
        return s;
    }

    private void showError(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
        tvExpression.setText("Error: " + msg);
    }
}
