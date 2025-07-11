package ai.z.openapi.service.model;

import lombok.Data;

/**
 * Meta information for hyper-realistic conversations.
 * Contains role and user information data where:
 * - user_info: user information
 * - bot_info: role/bot information
 * - bot_name: role/bot name
 * - user_name: user name
 */
@Data
public class ChatMeta {

    /**
     * User information.
     */
    private String user_info;
    
    /**
     * Bot/role information.
     */
    private String bot_info;
    
    /**
     * Bot/role name.
     */
    private String bot_name;
    
    /**
     * User name.
     */
    private String user_name;

}
