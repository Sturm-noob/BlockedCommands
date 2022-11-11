package me.sturm.blockedcommands;

import me.sturm.blockedcommands.context.EqualsMode;
import org.bukkit.Bukkit;

import java.util.LinkedList;

public class Utils {

    public static boolean parseLogicString(String expression) throws UnsupportedOperationException {
        if (expression == null) return false;
        expression = expression.trim();

        LinkedList<Integer> indicesOfOpen = new LinkedList<>();
        for (int i = 0; i < expression.length(); i++) {
            if (expression.charAt(i) == '(') indicesOfOpen.add(i);
            else if (expression.charAt(i) == ')') {
                if (indicesOfOpen.isEmpty()) throw new UnsupportedOperationException("Brackets error: " + expression);
                int lastOpenIndex = indicesOfOpen.removeLast();
                String oldSubstring = expression.substring(lastOpenIndex + 1, i);
                boolean subResult = parseLogicString(oldSubstring);
                expression = expression.substring(0, lastOpenIndex) + subResult + expression.substring(i+1);
                i -= oldSubstring.length() - (subResult ? 2 : 3);
            }
        }

        if (expression.equalsIgnoreCase("true")) return true;
        if (expression.equalsIgnoreCase("false")) return false;
        if (expression.contains("||")) {
            String[] cmds = expression.split("\\|\\|");
            for (String otherCmd : cmds)
                if (parseLogicString(otherCmd)) return true;
            return false;
        }
        if (expression.contains("&&")) {
            String[] cmds = expression.split("&&");
            for (String otherCmd : cmds)
                if (!parseLogicString(otherCmd)) return false;
            return true;
        }
        if (expression.length() > 1 && expression.charAt(0) == '!' && expression.charAt(1) == '!') {
            return !parseLogicString(expression.substring(2));
        }

        EqualsMode mode = null;
        if (expression.contains("!=")) mode = EqualsMode.NON_EQUALS;
        else if (expression.contains("==")) mode = EqualsMode.WEAK_EQUALS;
        else if (expression.contains(">=")) mode = EqualsMode.NO_LESS_THAN;
        else if (expression.contains("<=")) mode = EqualsMode.NO_MORE_THAN;
        else if (expression.contains("=")) mode = EqualsMode.EQUALS;
        else if (expression.contains(">")) mode = EqualsMode.MORE;
        else if (expression.contains("<")) mode = EqualsMode.LESS;

        if (mode == null) {
            Bukkit.getLogger().info("Parser found wrong expression");
            Bukkit.getLogger().info("In string " + expression);
            throw new UnsupportedOperationException(expression);
        }

        String[] values = expression.split(mode.getStringValue());

        if (values.length != 2) {
            Bukkit.getLogger().info("Parser found wrong expression");
            Bukkit.getLogger().info("Expected 2 args, found " + values.length);
            Bukkit.getLogger().info("In string " + expression);
            throw new UnsupportedOperationException(expression);
        }
        values[0] = values[0].trim();
        values[1] = values[1].trim();

        if (mode == EqualsMode.EQUALS) {
            if (values[1].equals("int")) return isInt(values[0]);
            if (values[1].equals("double")) return isDouble(values[0]);
            if (values[1].equals("bool")) return isBoolean(values[0]);
            return values[0].equals(values[1]);
        }

        double value1;
        double value2;

        if (isDouble(values[0]) && isDouble(values[1])) {
            value1 = Double.parseDouble(values[0]);
            value2 = Double.parseDouble(values[1]);
        }
        else {
            if (mode == EqualsMode.WEAK_EQUALS) return values[0].equalsIgnoreCase(values[1]);
            if (isBoolean(values[0]) && isBoolean(values[1])) {
                value1 = values[0].equalsIgnoreCase("true") ? 1 : 0;
                value2 = values[1].equalsIgnoreCase("true") ? 1 : 0;
            }
            else {
                value1 = values[0].length();
                value2 = values[1].length();
            }
        }

        if (mode == EqualsMode.WEAK_EQUALS) return Math.abs(value1 - value2) <= 1;
        if (mode == EqualsMode.LESS) return value1 < value2;
        if (mode == EqualsMode.MORE) return value1 > value2;
        if (mode == EqualsMode.NO_LESS_THAN) return value1 >= value2;
        if (mode == EqualsMode.NO_MORE_THAN) return value1 <= value2;
        return value1 != value2;
    }

    public static boolean isInt(String s) {
        try {
            Integer.parseInt(s);
            return true;
        }
        catch (NumberFormatException e) {
            return false;
        }
    }

    public static boolean isDouble(String s) {
        try {
            Double.parseDouble(s);
            return true;
        }
        catch (NumberFormatException e) {
            return false;
        }
    }

    public static boolean isBoolean(String s) {
        return s.equalsIgnoreCase("true") || s.equalsIgnoreCase("false");
    }

}
