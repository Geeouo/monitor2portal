package gee.example.mybatissample.entity;

import lombok.Data;

import java.util.List;

/**
 * @author Gee
 * @date 2020/11/13 14:37
 * @description
 */
@Data
public  class JDEntity{
    private boolean isNew;
    private String jdId;
    private String jdName;
    private String parentId;
    private String path;
    private String[] pathArray;
    private List<SQEntity> sqEntityList;
}
