package org.effective.tests.util;

import org.effective.tests.EffectsAnalyzer;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class ResultsPrettyPrinter {

    // ANSI escape codes for colors
    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_GREEN = "\u001B[32m";

    public static void print(Map<Class<?>, EffectsAnalyzer<?>> analyzers) {
        Map<Class<?>, EffectsAnalyzer<?>> perfectCoverage = new HashMap<>();
        Map<Class<?>, EffectsAnalyzer<?>> missingCoverage = new HashMap<>();
        System.out.println("\n" + "~".repeat(10) + " Effective Tests Results " + "~".repeat(10));
        for (Map.Entry<Class<?>, EffectsAnalyzer<?>> entry : analyzers.entrySet()) {
            EffectsAnalyzer<?> analyzer = entry.getValue();
            if (analyzer.getUntestedEffects().isEmpty()) {
                perfectCoverage.put(entry.getKey(), entry.getValue());
            } else {
                missingCoverage.put(entry.getKey(), entry.getValue());
            }
        }

        if (perfectCoverage.size() != 0) {
            printPerfectCoverage(perfectCoverage.keySet());
        }
        if (missingCoverage.size() != 0) {
            printMissingCoverage(missingCoverage);
        }
    }

    private static void printPerfectCoverage(Set<Class<?>> classes) {
        System.out.println(ANSI_GREEN + "\nAll effects tested:" );
        System.out.print(String.join("",
                        classes.stream().map(clazz -> "└── " + clazz.getSimpleName() + "\n").toList()
                ) + ANSI_RESET);
    }

    private static void printMissingCoverage(Map<Class<?>, EffectsAnalyzer<?>> analyzers) {
        System.out.println(ANSI_RED + "\nUntested effects were found in these classes, at these line numbers:");

        for (Map.Entry<Class<?>, EffectsAnalyzer<?>> entry : analyzers.entrySet()) {
            System.out.println("└── " + entry.getKey().getSimpleName());
            System.out.print( String.join("",
                entry.getValue().getUntestedEffects().stream()
                        .sorted((str1, str2) -> {
                            String[] parts1 = str1.split(":");
                            String[] parts2 = str2.split(":");
                            int line1 = Integer.parseInt(parts1[1]);
                            int line2 = Integer.parseInt(parts2[1]);
                            return Integer.compare(line1, line2);
                        })
                        .map(str -> {
                    String[] parts = str.split(":");
                    return "     └── " + parts[0] + " at line " + parts[1] + "\n";
                }).toList()
            ) );
        }
        System.out.println(ANSI_RESET);
    }
}

