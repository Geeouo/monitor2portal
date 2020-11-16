package gee.example.mybatissample.entity;

import lombok.Data;

/**
 * @author Gee
 * @date 2020/11/12 14:06
 * @description
 */
@Data
public class JwCloudOrg {
    /**
     * Column: id
     */
    private String id;

    /**
     * Column: name
     */
    private String title;

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
