package ai.z.openapi.service.tools;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/** 知识库检索V2返回结果 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class KnowledgeV2Result implements Serializable {

    /** 数据来源列表 */
    private List<Content> contents;

    /** query重写结果 */
    private RewrittenQuery rewritten_query;

    private String type;

    /** 数据来源 */
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Content {

        /** 切片ID（uuid） */
        private String id;

        /** 模态类型：text、image_url、video_url */
        private String type;

        /** 文本内容 */
        private String text;

        /** 文本中的媒体文件列表 */
        private List<Media> medias;

        /** 图像URL对象 */
        private ImageUrl image_url;

        /** 视频URL对象 */
        private VideoUrl video_url;

        /** 召回位次（rankIndex） */
        private Integer index;

        /** 召回分数（rankScore） */
        private Double score;

        /** 重排位次（rerankIndex） */
        private Integer rerank_index;

        /** 重排分数（rerankScore） */
        private Double rerank_score;

        /** 元数据 */
        private MetadataDTO metadata;
    }

    /** 媒体文件 */
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Media {

        /** 图片ID */
        private String id;

        /** 图片URL */
        private String url;

        /** 图片描述 */
        private String description;
    }

    /** 图像URL对象 */
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ImageUrl {

        /** URL */
        private String url;
    }

    /** 视频URL对象 */
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class VideoUrl {

        /** URL */
        private String url;
    }

    /** 元数据 */
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class MetadataDTO {

        /** 切片ID */
        private String _id;

        /** 知识库ID */
        private String know_id;

        /** 文档ID */
        private String doc_id;

        /** 文档类型（dtype） */
        private String doc_type;

        /** 文档名称（filename） */
        private String doc_name;

        /** 文档URL */
        private String doc_url;

        /** 切片下标 */
        private Integer index;

        /** 文档页码 */
        private Integer page_index;

        /** 视频切片下标 */
        private Integer clip_index;

        /** 首帧时间戳 */
        private Long start_time;

        /** 尾帧时间戳 */
        private Long end_time;

        /** 视频切片时长 */
        private Long duration;

        /** 关键帧列表 */
        private List<Object> frames;
    }

    /** query重写结果 */
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class RewrittenQuery {

        /** 生效query */
        private String primary_query;

        /** 备选query列表 */
        private List<String> multi_queries;
    }
}
