package pt.i2s.time2share.kafkatestcontainers.model.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.RequiredArgsConstructor;

import java.io.Serializable;

@RequiredArgsConstructor
public class Error implements Serializable {

    public static final String CODE_FIELD = "code";
    public static final String MESSAGE_FIELD = "message";

    private static final long serialVersionUID = -278830013431310604L;

    @JsonProperty(value = CODE_FIELD)
    private final int code;

    @JsonProperty(value = MESSAGE_FIELD)
    private final String message;
}
