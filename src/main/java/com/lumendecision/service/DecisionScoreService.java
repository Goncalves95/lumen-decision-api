package com.lumendecision.service;

import com.lumendecision.model.request.FinancialProfileRequest;
import com.lumendecision.model.response.DecisionScoreResponse;
import com.lumendecision.model.response.ScoreComponent;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Service
public class DecisionScoreService {

    private static final BigDecimal HUNDRED = BigDecimal.valueOf(100);

    public DecisionScoreResponse calculateScore(FinancialProfileRequest request) {
        double creditHealth = creditHealthScore(request);
        double incomeStability = incomeStabilityScore(request);
        double spendingDiscipline = spendingDisciplineScore(request);
        double accountDiversity = accountDiversityScore(request);
        double creditAgeHistory = creditAgeHistoryScore(request);

        List<ScoreComponent> components = List.of(
                buildComponent("Credit Health", creditHealth, 0.25,
                        "Reflects credit score, utilization and payment history"),
                buildComponent("Income Stability", incomeStability, 0.25,
                        "Reflects how much income remains after monthly expenses"),
                buildComponent("Spending Discipline", spendingDiscipline, 0.20,
                        "Reflects the ratio of expenses to income"),
                buildComponent("Account Diversity", accountDiversity, 0.15,
                        "Reflects the variety and number of financial accounts held"),
                buildComponent("Credit Age & History", creditAgeHistory, 0.15,
                        "Reflects how long accounts have been open and payment reliability")
        );

        double overall = components.stream()
                .mapToDouble(ScoreComponent::getWeightedScore)
                .sum();

        int overallScore = clamp((int) Math.round(overall));
        String grade = gradeFor(overallScore);
        String summary = summaryFor(grade);
        List<String> recommendations = buildRecommendations(request);

        return DecisionScoreResponse.builder()
                .overallScore(overallScore)
                .grade(grade)
                .summary(summary)
                .components(components)
                .recommendations(recommendations)
                .calculatedAt(Instant.now())
                .currency(request.getCurrency())
                .build();
    }

    private double creditHealthScore(FinancialProfileRequest request) {
        double normalized = (request.getCreditScore() - 300) / 550.0 * 100;
        double usage = creditUsagePercent(request);
        double usageScore = 100 - usage;
        double paymentHistory = request.getPaymentHistoryPercent().doubleValue();

        double score = (normalized * 0.5) + (paymentHistory * 0.3) + (usageScore * 0.2);
        return clamp(score);
    }

    private double incomeStabilityScore(FinancialProfileRequest request) {
        double income = request.getMonthlyIncome().doubleValue();
        double expenses = request.getMonthlyExpenses().doubleValue();

        if (income <= 0) {
            return 0;
        }

        double savingsRate = (income - expenses) / income * 100;
        return clamp(Math.min(savingsRate * 1.5, 100));
    }

    private double spendingDisciplineScore(FinancialProfileRequest request) {
        double income = request.getMonthlyIncome().doubleValue();
        double expenses = request.getMonthlyExpenses().doubleValue();

        if (income <= 0) {
            return 25;
        }

        double expenseRatio = expenses / income;
        if (expenseRatio <= 0.5) {
            return 100;
        } else if (expenseRatio <= 0.7) {
            return 75;
        } else if (expenseRatio <= 0.9) {
            return 50;
        } else {
            return 25;
        }
    }

    private double accountDiversityScore(FinancialProfileRequest request) {
        double base = Math.min(request.getAccountsCount() * 20, 60);
        if (Boolean.TRUE.equals(request.getHasSavingsAccount())) {
            base += 20;
        }
        if (Boolean.TRUE.equals(request.getHasInvestmentAccount())) {
            base += 20;
        }
        return clamp(Math.min(base, 100));
    }

    private double creditAgeHistoryScore(FinancialProfileRequest request) {
        double ageScore = Math.min(request.getAvgAccountAgeYears().doubleValue() * 20, 100);
        double paymentHistory = request.getPaymentHistoryPercent().doubleValue();
        return clamp((ageScore * 0.5) + (paymentHistory * 0.5));
    }

    private double creditUsagePercent(FinancialProfileRequest request) {
        BigDecimal limit = request.getCreditLimit();
        if (limit == null || limit.compareTo(BigDecimal.ZERO) <= 0) {
            return 0;
        }
        return request.getCreditUsed()
                .divide(limit, 6, RoundingMode.HALF_UP)
                .multiply(HUNDRED)
                .doubleValue();
    }

    private ScoreComponent buildComponent(String name, double score, double weight, String description) {
        int roundedScore = clamp((int) Math.round(score));
        double weightedScore = roundedScore * weight;
        return ScoreComponent.builder()
                .name(name)
                .score(roundedScore)
                .weight(weight)
                .weightedScore(round2(weightedScore))
                .description(description)
                .build();
    }

    private List<String> buildRecommendations(FinancialProfileRequest request) {
        List<String> recommendations = new ArrayList<>();

        double usage = creditUsagePercent(request);
        if (usage > 30) {
            recommendations.add("Reduce credit utilization below 30% for significant score improvement");
        }

        double income = request.getMonthlyIncome().doubleValue();
        double savingsRate = income > 0
                ? (income - request.getMonthlyExpenses().doubleValue()) / income * 100
                : 0;
        if (savingsRate < 20) {
            recommendations.add("Increase monthly savings rate to at least 20% of income");
        }

        if (request.getPaymentHistoryPercent().doubleValue() < 90) {
            recommendations.add("Prioritise on-time payments — payment history is the strongest credit factor");
        }

        if (request.getAccountsCount() < 3) {
            recommendations.add("Diversify with additional account types (savings, investment)");
        }

        if (request.getAvgAccountAgeYears().doubleValue() < 3) {
            recommendations.add("Avoid closing old accounts — account age strengthens your profile");
        }

        if (recommendations.isEmpty()) {
            recommendations.add("Maintain current habits and monitor your score monthly");
        }

        return recommendations.stream().limit(3).toList();
    }

    private String gradeFor(int score) {
        if (score >= 80) {
            return "A";
        } else if (score >= 65) {
            return "B";
        } else if (score >= 50) {
            return "C";
        } else if (score >= 35) {
            return "D";
        } else {
            return "F";
        }
    }

    private String summaryFor(String grade) {
        return switch (grade) {
            case "A" -> "Excellent financial health. You are well-positioned for major financial decisions.";
            case "B" -> "Good financial health with room for improvement in key areas.";
            case "C" -> "Fair financial health. Focus on reducing expenses and improving credit usage.";
            case "D" -> "Below average financial health. Prioritise debt reduction and savings.";
            default -> "Poor financial health. Immediate action needed to stabilise your finances.";
        };
    }

    private int clamp(int value) {
        return Math.max(0, Math.min(100, value));
    }

    private double clamp(double value) {
        return Math.max(0, Math.min(100, value));
    }

    private double round2(double value) {
        return Math.round(value * 100) / 100.0;
    }
}
