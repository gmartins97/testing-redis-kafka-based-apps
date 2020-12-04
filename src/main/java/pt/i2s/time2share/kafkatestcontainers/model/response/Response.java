package pt.i2s.time2share.kafkatestcontainers.model.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.RequiredArgsConstructor;
import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

import java.io.Serializable;

@RequiredArgsConstructor
public class Response<T extends Serializable> implements Serializable {

    public static final String DATA_FIELD = "data";
    public static final String ERROR_FIELD = "error";

    private static final long serialVersionUID = 8328603395830191972L;

    @JsonProperty(value = DATA_FIELD)
    @Schema(type = SchemaType.OBJECT)
    private final T data;

    @JsonProperty(value = ERROR_FIELD)
    private final Error error;
}
