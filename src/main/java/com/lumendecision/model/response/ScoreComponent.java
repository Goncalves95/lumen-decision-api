package com.lumendecision.model.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "An individual weighted component of the overall decision score")
public class ScoreComponent {

    @Schema(description = "Component name", example = "Credit Health")
    private String name;

    @Schema(description = "Raw score for this component, 0-100", example = "78")
    private Integer score;

    @Schema(description = "Weight of this component in the overall score", example = "0.25")
    private Double weight;

    @Schema(description = "Score contribution after applying the weight", example = "19.5")
    private Double weightedScore;

    @Schema(description = "Human-readable description of this component", example = "Reflects credit score, utilization and payment history")
    private String description;
}
