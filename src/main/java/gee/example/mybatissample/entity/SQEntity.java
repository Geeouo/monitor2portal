package gee.example.mybatissample.entity;

import lombok.Data;

/**
 * @author Gee
 * @date 2020/11/13 14:37
 * @description
 */

@Data
public class SQEntity{
    private boolean isNew;
    private String sqId;
    private String sqName;
    private String parentId;
    private String path;
    private String[] pathArray;
}
