package com.wd.paas.api.domain.v4.voiceclone;

import lombok.Data;

import java.io.Serializable;

@Data
public class VoiceCloneResponse implements Serializable {

    private static final long serialVersionUID = 7717182948782963979L;

    /** 音色ID */
    private String voice_id;

    /** 音频试听文件ID */
    private String file_id;

    /** 文件 purpose */
    private String file_purpose;

    //    /** 音频试听文件下载URL */
    //    private String download_url;

    private String request_id;
}
