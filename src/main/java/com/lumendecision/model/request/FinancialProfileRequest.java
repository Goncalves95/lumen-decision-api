package com.lumendecision.model.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Financial profile used to calculate a decision score")
public class FinancialProfileRequest {

    @Min(value = 300, message = "creditScore must be at least 300")
    @Max(value = 850, message = "creditScore must be at most 850")
    @Schema(description = "FICO-style credit score", example = "650", defaultValue = "650")
    @Builder.Default
    private Integer creditScore = 650;

    @DecimalMin(value = "0", message = "creditLimit must be at least 0")
    @Schema(description = "Total available credit limit across all accounts", example = "10000", defaultValue = "10000")
    @Builder.Default
    private BigDecimal creditLimit = BigDecimal.valueOf(10000);

    @DecimalMin(value = "0", message = "creditUsed must be at least 0")
    @Schema(description = "Total credit currently used across all accounts", example = "0", defaultValue = "0")
    @Builder.Default
    private BigDecimal creditUsed = BigDecimal.ZERO;

    @DecimalMin(value = "0", message = "monthlyIncome must be at least 0")
    @Schema(description = "Net monthly income", example = "3000", defaultValue = "3000")
    @Builder.Default
    private BigDecimal monthlyIncome = BigDecimal.valueOf(3000);

    @DecimalMin(value = "0", message = "monthlyExpenses must be at least 0")
    @Schema(description = "Total monthly expenses", example = "2000", defaultValue = "2000")
    @Builder.Default
    private BigDecimal monthlyExpenses = BigDecimal.valueOf(2000);

    @Schema(description = "Whether the person has a savings account", example = "false", defaultValue = "false")
    @Builder.Default
    @JsonProperty(defaultValue = "false")
    private Boolean hasSavingsAccount = false;

    @Schema(description = "Whether the person has an investment account", example = "false", defaultValue = "false")
    @Builder.Default
    @JsonProperty(defaultValue = "false")
    private Boolean hasInvestmentAccount = false;

    @Min(value = 1, message = "accountsCount must be at least 1")
    @Schema(description = "Number of open financial accounts", example = "1", defaultValue = "1")
    @Builder.Default
    private Integer accountsCount = 1;

    @Schema(description = "Average age of accounts, in years", example = "2.0", defaultValue = "2.0")
    @Builder.Default
    private BigDecimal avgAccountAgeYears = BigDecimal.valueOf(2.0);

    @DecimalMin(value = "0", message = "paymentHistoryPercent must be at least 0")
    @Max(value = 100, message = "paymentHistoryPercent must be at most 100")
    @Schema(description = "Percentage of on-time payments", example = "80.0", defaultValue = "80.0")
    @Builder.Default
    private BigDecimal paymentHistoryPercent = BigDecimal.valueOf(80.0);

    @Schema(description = "Currency code for the response", example = "CHF", defaultValue = "CHF")
    @Builder.Default
    private String currency = "CHF";
}
