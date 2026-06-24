package com.lumendecision.controller;

import com.lumendecision.model.request.FinancialProfileRequest;
import com.lumendecision.model.response.ApiResponse;
import com.lumendecision.model.response.DecisionScoreResponse;
import com.lumendecision.service.DecisionScoreService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Map;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@Tag(name = "Decision Score", description = "Financial decision scoring endpoints")
public class DecisionScoreController {

    private final DecisionScoreService decisionScoreService;

    @PostMapping(value = "/score", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(
            summary = "Calculate a financial decision score",
            description = "Accepts a financial profile and returns a weighted decision score (0-100), "
                    + "letter grade, component breakdown and actionable recommendations. "
                    + "Rate limited to 100 requests per hour per IP.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            examples = @ExampleObject(
                                    name = "Typical profile",
                                    value = "{\n"
                                            + "  \"creditScore\": 720,\n"
                                            + "  \"creditLimit\": 15000,\n"
                                            + "  \"creditUsed\": 3000,\n"
                                            + "  \"monthlyIncome\": 5000,\n"
                                            + "  \"monthlyExpenses\": 3200,\n"
                                            + "  \"hasSavingsAccount\": true,\n"
                                            + "  \"hasInvestmentAccount\": false,\n"
                                            + "  \"accountsCount\": 3,\n"
                                            + "  \"avgAccountAgeYears\": 4.5,\n"
                                            + "  \"paymentHistoryPercent\": 95.0,\n"
                                            + "  \"currency\": \"CHF\"\n"
                                            + "}"
                            )
                    )
            )
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Score calculated successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Validation error in request payload"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "429", description = "Rate limit exceeded")
    })
    public ResponseEntity<ApiResponse<DecisionScoreResponse>> calculateScore(
            @Valid @RequestBody(required = false) FinancialProfileRequest request) {
        FinancialProfileRequest profile = request != null ? request : FinancialProfileRequest.builder().build();
        DecisionScoreResponse response = decisionScoreService.calculateScore(profile);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    @GetMapping(value = "/score/example", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(
            summary = "Get an example request and response",
            description = "Returns a sample financial profile alongside the score it produces, for documentation purposes."
    )
    public ResponseEntity<ApiResponse<Map<String, Object>>> getExample() {
        FinancialProfileRequest exampleRequest = FinancialProfileRequest.builder()
                .creditScore(720)
                .creditLimit(BigDecimal.valueOf(15000))
                .creditUsed(BigDecimal.valueOf(3000))
                .monthlyIncome(BigDecimal.valueOf(5000))
                .monthlyExpenses(BigDecimal.valueOf(3200))
                .hasSavingsAccount(true)
                .hasInvestmentAccount(false)
                .accountsCount(3)
                .avgAccountAgeYears(BigDecimal.valueOf(4.5))
                .paymentHistoryPercent(BigDecimal.valueOf(95.0))
                .currency("CHF")
                .build();

        DecisionScoreResponse exampleResponse = decisionScoreService.calculateScore(exampleRequest);

        Map<String, Object> payload = Map.of(
                "request", exampleRequest,
                "response", exampleResponse
        );

        return ResponseEntity.ok(ApiResponse.ok(payload));
    }

    @GetMapping(value = "/health", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Health check", description = "Returns the API status. Useful for uptime monitoring.")
    public ResponseEntity<Map<String, Object>> health() {
        return ResponseEntity.ok(Map.of(
                "status", "UP",
                "service", "lumen-decision-api",
                "timestamp", Instant.now()
        ));
    }
}
