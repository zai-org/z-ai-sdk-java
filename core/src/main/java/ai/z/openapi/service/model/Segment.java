package ai.z.openapi.service.model;

import lombok.Data;

@Data
public class Segment {


    private Integer id;

    private Double start;

    private Double end;

    private String text;
}
