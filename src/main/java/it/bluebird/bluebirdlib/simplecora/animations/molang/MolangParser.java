package it.bluebird.bluebirdlib.simplecora.animations.molang;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MolangParser {
    private static final Pattern SIN_PATTERN = Pattern.compile("math\\.sin\\(([^)]+)\\)");
    private static final Pattern COS_PATTERN = Pattern.compile("math\\.cos\\(([^)]+)\\)");
    private static final Pattern MIN_PATTERN = Pattern.compile("math\\.min\\(([^,]+),([^)]+)\\)");
    private static final Pattern MAX_PATTERN = Pattern.compile("math\\.max\\(([^,]+),([^)]+)\\)");

    public static float calculate(String expression, float animTime) {
        try {
            String timeString = Float.toString(animTime);
            expression = expression.replace("query.anim_time", timeString)
                    .replace("q.anim_time", timeString);

            expression = replaceSingleArgFunction(expression, SIN_PATTERN, val -> Math.sin(Math.toRadians(val)));
            expression = replaceSingleArgFunction(expression, COS_PATTERN, val -> Math.cos(Math.toRadians(val)));
            expression = replaceDoubleArgFunction(expression, MIN_PATTERN, Math::min);
            expression = replaceDoubleArgFunction(expression, MAX_PATTERN, Math::max);

            return (float) parseArithmetic(expression);
        } catch (Exception e) {
            System.err.println("Molang parse error: " + e.getMessage() + " in '" + expression + "'");
            return 0.0F;
        }
    }

    private static String replaceSingleArgFunction(String expr, Pattern pattern, SingleArgFunction func) {
        Matcher matcher = pattern.matcher(expr);
        StringBuilder sb = new StringBuilder();

        while (matcher.find()) {
            String insideExpression = matcher.group(1);
            double evaluatedValue = parseArithmetic(insideExpression);
            double result = func.apply(evaluatedValue);
            matcher.appendReplacement(sb, Float.toString((float) result));
        }

        matcher.appendTail(sb);
        return sb.toString();
    }

    private static String replaceDoubleArgFunction(String expr, Pattern pattern, DoubleArgFunction func) {
        Matcher matcher = pattern.matcher(expr);
        StringBuilder sb = new StringBuilder();

        while (matcher.find()) {
            String insideFirst = matcher.group(1);
            String insideSecond = matcher.group(2);

            double value1 = parseArithmetic(insideFirst);
            double value2 = parseArithmetic(insideSecond);
            double result = func.apply(value1, value2);

            matcher.appendReplacement(sb, Float.toString((float) result));
        }

        matcher.appendTail(sb);
        return sb.toString();
    }

    private static double parseArithmetic(String expr) {
        return new ExpressionEvaluator(expr).parse();
    }

    private static class ExpressionEvaluator {
        private final String expr;
        private int pos = -1;
        private int ch;

        public ExpressionEvaluator(String expr) {
            this.expr = expr;
        }

        void nextChar() {
            this.ch = (++this.pos < expr.length()) ? expr.charAt(this.pos) : -1;
        }

        boolean eat(int charToEat) {
            while (this.ch == ' ') {
                this.nextChar();
            }

            if (this.ch == charToEat) {
                this.nextChar();
                return true;
            }
            return false;
        }

        double parse() {
            this.nextChar();
            double result = this.parseExpression();
            if (this.pos < expr.length()) {
                throw new RuntimeException("Unexpected character: " + (char) this.ch);
            }
            return result;
        }

        double parseExpression() {
            double x = this.parseTerm();
            while (true) {
                if (this.eat('+')) {
                    x += this.parseTerm();
                } else if (this.eat('-')) {
                    x -= this.parseTerm();
                } else {
                    return x;
                }
            }
        }

        double parseTerm() {
            double x = this.parseFactor();
            while (true) {
                if (this.eat('*')) {
                    x *= this.parseFactor();
                } else if (this.eat('/')) {
                    x /= this.parseFactor();
                } else {
                    return x;
                }
            }
        }

        double parseFactor() {
            if (this.eat('+')) {
                return this.parseFactor();
            }
            if (this.eat('-')) {
                return -this.parseFactor();
            }

            double x;
            int startPos = this.pos;

            if (this.eat('(')) {
                x = this.parseExpression();
                if (!this.eat(')')) {
                    throw new RuntimeException("Missing closing parenthesis ')'");
                }
            } else {
                if ((this.ch < '0' || this.ch > '9') && this.ch != '.' && this.ch != '-') {
                    throw new RuntimeException("Unexpected character: " + (char) this.ch);
                }

                while ((this.ch >= '0' && this.ch <= '9') || this.ch == '.' || this.ch == '-' || this.ch == 'e' || this.ch == 'E' || this.ch == '+') {
                    if ((this.ch == '-' || this.ch == '+') && expr.charAt(this.pos - 1) != 'e' && expr.charAt(this.pos - 1) != 'E') {
                        break;
                    }
                    this.nextChar();
                }

                x = Double.parseDouble(expr.substring(startPos, this.pos));
            }

            return x;
        }
    }

    @FunctionalInterface
    private interface SingleArgFunction {
        double apply(double value);
    }

    @FunctionalInterface
    private interface DoubleArgFunction {
        double apply(double val1, double val2);
    }
}