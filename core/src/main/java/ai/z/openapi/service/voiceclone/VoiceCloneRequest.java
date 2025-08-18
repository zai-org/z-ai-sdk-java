package com.wd.paas.api.domain.v4.voiceclone;

import com.wd.paas.api.domain.constant.CommonPaasConstant;
import com.wd.paas.api.domain.v4.sensitive.SensitiveWordCheck;

import lombok.Data;

import java.io.Serializable;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
public class VoiceCloneRequest implements Serializable {
    private static final long serialVersionUID = -4483756338945311635L;

    /** 音色名称 */
    @NotBlank(message = "音色名称不能为空")
    @Size(max = 50, message = "音色名称长度不能超过50个字符")
    private String voice_name;

    /** 示例音频对应的文本内容 */
    @NotBlank(message = "示例音频文本不能为空")
    @Size(max = 50, message = "示例音频文本长度不能超过50个字符")
    private String voice_text_input;

    /** 试听文本输出内容 */
    @NotBlank(message = "试听音频文本不能为空")
    @Size(max = 500, message = "试听音频文本长度不能超过500个字符")
    private String voice_text_output;

    /** 音频文件的 file_id */
    @NotBlank(message = "音频文件不能为空")
    private String file_id;

    private Integer auto_save = 1;

    private String request_id;

    /** 将音色克隆封装成一种模型工具, 对齐管理后台上的【模型管理】项 */
    private String model = CommonPaasConstant.VOICE_CLONE_COGTTS;

    /** 将音色克隆封装成一种应用组件, 对齐管理后台上的【开放能力】项 */
    private String abilityType = CommonPaasConstant.VOICE_CLONE_COGTTS;

    /** 风控api key(备注：配置中存在风控key 不做敏感词校验) */
    @Valid private SensitiveWordCheck sensitive_word_check;
}
