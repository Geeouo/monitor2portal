package gee.example.mybatissample.entity;

import lombok.Data;

import java.util.List;

/**
 * @author Gee
 * @date 2020/11/12 16:12
 * @description
 */
@Data
public class QuEntity {
    private boolean isNew; //是插入还是更新
    private String quId; //区县id
    private String quName;//区县名称

    private String parentId;
    private String path;
    private String[] pathArray;//根据path拆分出来的数组
    private List<JDEntity> jdEntityList;//街道数组

}
