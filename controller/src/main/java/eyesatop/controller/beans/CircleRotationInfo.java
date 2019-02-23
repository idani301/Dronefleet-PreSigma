package eyesatop.controller.beans;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by Idan on 25/12/2017.
 */

public class CircleRotationInfo {

    private static final String ROTATION_TYPE = "rotationType";
    private static final String STARTING_DEGREE = "startingDegree";
    private static final String TOTAL_DEGREES_TO_COVER = "totalDegreesToCover";

    private final RotationType rotationType;
    private final Double startingDegree;
    private final Double totalDegreesToCover;

    public CircleRotationInfo(
            @JsonProperty(ROTATION_TYPE) RotationType rotationType,
            @JsonProperty(STARTING_DEGREE) Double startingDegree,
            @JsonProperty(TOTAL_DEGREES_TO_COVER) Double totalDegreesToCover) {
        this.rotationType = rotationType;
        this.startingDegree = startingDegree;
        this.totalDegreesToCover = totalDegreesToCover;
    }

    @JsonProperty(ROTATION_TYPE)
    public RotationType getRotationType() {
        return rotationType;
    }

    @JsonProperty(STARTING_DEGREE)
    public Double getStartingDegree() {
        return startingDegree;
    }

    @JsonProperty(TOTAL_DEGREES_TO_COVER)
    public Double getTotalDegreesToCover() {
        return totalDegreesToCover;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CircleRotationInfo that = (CircleRotationInfo) o;

        if (rotationType != that.rotationType) return false;
        if (startingDegree != null ? !startingDegree.equals(that.startingDegree) : that.startingDegree != null)
            return false;
        return totalDegreesToCover != null ? totalDegreesToCover.equals(that.totalDegreesToCover) : that.totalDegreesToCover == null;

    }
}
