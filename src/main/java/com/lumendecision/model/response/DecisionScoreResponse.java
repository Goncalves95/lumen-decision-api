package com.lumendecision.model.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Result of a financial decision score calculation")
public class DecisionScoreResponse {

    @Schema(description = "Overall weighted score, 0-100", example = "72")
    private Integer overallScore;

    @Schema(description = "Letter grade derived from the overall score", example = "B")
    private String grade;

    @Schema(description = "One sentence summary of the financial health represented by this score",
            example = "Good financial health with room for improvement in key areas.")
    private String summary;

    @Schema(description = "Breakdown of the score by weighted component")
    private List<ScoreComponent> components;

    @Schema(description = "Top actionable recommendations to improve the score")
    private List<String> recommendations;

    @Schema(description = "Timestamp when the score was calculated")
    private Instant calculatedAt;

    @Schema(description = "Currency of the financial profile used in the calculation", example = "CHF")
    private String currency;
}
