package ai.z.openapi.service.file;

import lombok.Data;

@Data
public class FileDelResponse {
    private int code;
    private String msg;
    private boolean success;

    private FileDeleted data;
}
