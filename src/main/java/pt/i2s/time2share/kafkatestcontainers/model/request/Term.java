package pt.i2s.time2share.kafkatestcontainers.model.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

import java.io.Serializable;

@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
public class Term implements Serializable {

    public static final String TERM_FIELD = "term";

    private static final long serialVersionUID = 3013477380340060956L;

    @JsonProperty(value = TERM_FIELD)
    private Object term;
}
