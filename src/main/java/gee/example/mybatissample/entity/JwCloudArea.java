package gee.example.mybatissample.entity;

import lombok.Data;

/**
 * @author Gee
 * @date 2020/11/12 14:05
 * @description area表对应实体类
 */
@Data
public class JwCloudArea {
    /**
     * Column: id
     */
    private String id;

    /**
     * Column: name
     */
    private String name;

    /**
     * Column: parent_id
     */
    private String parentId;

    /**
     * Column: path
     */
    private String path;

    private String[] pathArray;
}
