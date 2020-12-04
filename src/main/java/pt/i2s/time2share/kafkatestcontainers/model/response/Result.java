package pt.i2s.time2share.kafkatestcontainers.model.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.RequiredArgsConstructor;

import java.io.Serializable;

@RequiredArgsConstructor
public class Result implements Serializable {

    public static final String RESULT_FIELD = "result";

    private static final long serialVersionUID = 7692485648272765968L;

    @JsonProperty(value = RESULT_FIELD)
    private final String result;
}
