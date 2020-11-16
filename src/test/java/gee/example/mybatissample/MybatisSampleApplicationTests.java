package gee.example.mybatissample;

import com.alibaba.fastjson.JSONObject;
import gee.example.mybatissample.entity.*;
import gee.example.mybatissample.service.DealService;
import gee.example.mybatissample.utils.TextUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;

/**
 * README
 * 1.找出所有org表中已存在或需要新增到area表中的数据,通过对比path的length长度来区分区县，街道，社区；
 * 1).在这个过程中添加了过滤条件
 * a).替换街道监督组字样为“”空串
 * b).社区带有+ 、，的需要拆分
 * 2).将最终数据以实体QuEntity、JDEntity、SQEntity来体现
 * <p>
 * 2.将List<QuEntity>以及List<JDEntity>、List<SQEntity>的所有id,parentId,path等清空；
 * <p>
 * 3.将area表中存在的数据的id,parentId,path等数据灌入到List<QuEntity>以及List<JDEntity>、List<SQEntity>中；
 * <p>
 * 4.遍历List<QuEntity>以及List<JDEntity>、List<SQEntity>插入数据到area表中；
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class MybatisSampleApplicationTests {

    @Autowired
    DealService dealService;

    @Test
    public void deal() {
        //取出所有的机构信息  此list包含市直，所以需要进一步处理数据
        List<JwCloudOrg> preAllOrg = dealService.findAllOrg();
        List<JwCloudOrg> allOrg = new ArrayList<>();
        //处理数据开始，只提取区县数据
        for (JwCloudOrg jwCloudOrg : preAllOrg) {
            //2524,2525,2526,2526代表区县层级开始，对应area表的沈阳市
            // ***只关注此条件成立时的数据***
            if (TextUtils.isEmpty(jwCloudOrg.getPath()))
                continue;
            if (jwCloudOrg.getPath().startsWith("2524,2525,2526,")) {
                //trim一下
                jwCloudOrg.setPath(jwCloudOrg.getPath().trim());
                allOrg.add(jwCloudOrg);
            }
        }

        //设置org的pathArray
        for (JwCloudOrg jwCloudOrg : allOrg) {
            if (TextUtils.isEmpty(jwCloudOrg.getPath()))
                continue;
            String[] split = jwCloudOrg.getPath().split(",");
            jwCloudOrg.setPathArray(split);
        }
        //设置area的pathArray
        List<JwCloudArea> allArea = dealService.findAllArea();
        for (JwCloudArea jwCloudArea : allArea) {
            if (TextUtils.isEmpty(jwCloudArea.getPath()))
                continue;
            //trim一下
            jwCloudArea.setPath(jwCloudArea.getPath().trim());
            String[] split = jwCloudArea.getPath().split(",");
            jwCloudArea.setPathArray(split);
        }

        //替换街道title“监督组”字样为“”空串
//        for (JwCloudOrg jwCloudOrg : allOrg) {
//            if (jwCloudOrg.getPathArray().length == 4 && jwCloudOrg.getTitle().contains("监督组")) {
//                jwCloudOrg.setTitle(jwCloudOrg.getTitle().replaceAll("监督组", ""));
//            }
//        }

        List<JwCloudArea> allQuArea = new ArrayList<>();
        List<JwCloudArea> allJDArea = new ArrayList<>();
        List<JwCloudArea> allSQArea = new ArrayList<>();

        for (int i = 0; i < allArea.size(); i++) {
            //2代表区县,3代表街道，4代表社区
            if (allArea.get(i).getPathArray() == null) {
                continue;
            }
            if (allArea.get(i).getPathArray().length == 2) {
                allQuArea.add(allArea.get(i));
            } else if (allArea.get(i).getPathArray().length == 3) {
                allJDArea.add(allArea.get(i));
            } else if (allArea.get(i).getPathArray().length == 4) {
                allSQArea.add(allArea.get(i));
            } else continue;
        }

        List<JwCloudOrg> allQuExistOrg = new ArrayList<>();
        List<JwCloudOrg> allQuInsertOrg = new ArrayList<>();
        List<JwCloudOrg> allQuOrg = new ArrayList<>();

        List<JwCloudOrg> allJDExistOrg = new ArrayList<>();
        List<JwCloudOrg> allJDInsertOrg = new ArrayList<>();
        List<JwCloudOrg> preAllJDOrg = new ArrayList<>();
        List<JwCloudOrg> allJDOrg = new ArrayList<>();

        List<JwCloudOrg> allSQExistOrg = new ArrayList<>();
        List<JwCloudOrg> allSQInsertOrg = new ArrayList<>();
        List<JwCloudOrg> preAllSQOrg = new ArrayList<>();
        List<JwCloudOrg> allSQOrg = new ArrayList<>();

        for (int i = 0; i < allOrg.size(); i++) {
            //3代表区县,4代表街道，5代表社区
            if (allOrg.get(i).getPathArray() == null) {
                continue;
            }
            if (allOrg.get(i).getPathArray().length == 3) {
                allQuOrg.add(allOrg.get(i));
            } else if (allOrg.get(i).getPathArray().length == 4) {
                preAllJDOrg.add(allOrg.get(i));
            } else if (allOrg.get(i).getPathArray().length == 5) {
                preAllSQOrg.add(allOrg.get(i));
            } else continue;
        }

        //todo
        //处理街道下无社区的街道
        for (JwCloudOrg preJD : preAllJDOrg) {
            boolean isHaveChild = false;
            for (JwCloudOrg preSQ : preAllSQOrg) {
                if (preSQ.getParentId().equals(preJD.getId())){
                    isHaveChild = true;
                    break;
                }
            }
            if (isHaveChild){
                allJDOrg.add(preJD);
            }
        }

        //处理空格/顿号/加号连接的社区
        for (JwCloudOrg jwCloudOrg : preAllSQOrg) {
            List<JwCloudOrg> splitList = new ArrayList<>();
            jwCloudOrg.setTitle(jwCloudOrg.getTitle().replaceAll("，", "+").replaceAll("、", "+"));

            if (jwCloudOrg.getTitle().contains("+")) {
                String[] titles = jwCloudOrg.getTitle().split("\\+");
                for (int i = 0; i < titles.length; i++) {
                    JwCloudOrg newJwCloudOrg = new JwCloudOrg();
                    newJwCloudOrg.setId(TextUtils.isEmpty(jwCloudOrg.getId()) ? "" : jwCloudOrg.getId());
                    newJwCloudOrg.setTitle(TextUtils.isEmpty(titles[i]) ? "" : titles[i]);
                    newJwCloudOrg.setParentId(TextUtils.isEmpty(jwCloudOrg.getParentId()) ? "" : jwCloudOrg.getParentId());
                    newJwCloudOrg.setPath(TextUtils.isEmpty(jwCloudOrg.getPath()) ? "" : jwCloudOrg.getPath());
                    newJwCloudOrg.setPathArray(jwCloudOrg.getPathArray() == null ? null : jwCloudOrg.getPathArray());
                    splitList.add(newJwCloudOrg);
                }
            } else {
                splitList.add(jwCloudOrg);
            }
            allSQOrg.addAll(splitList);
        }


        for (JwCloudOrg quOrg : allQuOrg) {
            boolean isNew = true;
            for (JwCloudArea quArea : allQuArea) {
                if (quOrg.getTitle().contains(quArea.getName())) {
                    isNew = false;
                    break;
                }
            }

            if (isNew)
                allQuInsertOrg.add(quOrg);
            else
                allQuExistOrg.add(quOrg);
        }

        for (JwCloudOrg jdOrg : allJDOrg) {
            boolean isNew = true;
            for (JwCloudArea jdArea : allJDArea) {
                if (jdOrg.getTitle().contains(jdArea.getName())) {
                    isNew = false;
                    break;
                }
            }

            if (isNew)
                allJDInsertOrg.add(jdOrg);
            else
                allJDExistOrg.add(jdOrg);
        }


        for (JwCloudOrg sqOrg : allSQOrg) {
            boolean isNew = true;
            for (JwCloudArea sqArea : allSQArea) {
                if (sqOrg.getTitle().contains(sqArea.getName())) {
                    isNew = false;
                    break;
                }
            }

            if (isNew)
                allSQInsertOrg.add(sqOrg);
            else
                allSQExistOrg.add(sqOrg);
        }

        List<QuEntity> quEntityList = new ArrayList<>();

        for (JwCloudOrg jwCloudOrg : allQuExistOrg) {
            QuEntity entity = new QuEntity();
            entity.setNew(false);
            entity.setQuId(jwCloudOrg.getId());
            entity.setQuName(jwCloudOrg.getTitle());
            entity.setParentId(jwCloudOrg.getParentId());
            entity.setPath(jwCloudOrg.getPath());
            entity.setPathArray(jwCloudOrg.getPathArray());
            quEntityList.add(entity);
        }

        for (JwCloudOrg jwCloudOrg : allQuInsertOrg) {
            QuEntity entity = new QuEntity();
            entity.setNew(true);
            entity.setQuId(jwCloudOrg.getId());
            entity.setQuName(jwCloudOrg.getTitle());
            entity.setParentId(jwCloudOrg.getParentId());
            entity.setPath(jwCloudOrg.getPath());
            entity.setPathArray(jwCloudOrg.getPathArray());
            quEntityList.add(entity);
        }

        for (JwCloudOrg jwCloudOrg : allJDExistOrg) {
            JDEntity jdEntity = new JDEntity();
            jdEntity.setNew(false);
            jdEntity.setJdId(jwCloudOrg.getId());
            jdEntity.setJdName(jwCloudOrg.getTitle());
            jdEntity.setParentId(jwCloudOrg.getParentId());
            jdEntity.setPath(jwCloudOrg.getPath());
            jdEntity.setPathArray(jwCloudOrg.getPathArray());
            //jdEntityList.add(jdEntity);

            for (QuEntity entity : quEntityList) {
                if (entity.getQuId().equals(jwCloudOrg.getParentId())) {
                    List<JDEntity> jdEntities = entity.getJdEntityList();
                    if (jdEntities == null)
                        jdEntities = new ArrayList<>();
                    jdEntities.add(jdEntity);
                    entity.setJdEntityList(jdEntities);
                    break;
                }
            }
        }

        for (JwCloudOrg jwCloudOrg : allJDInsertOrg) {
            JDEntity jdEntity = new JDEntity();
            jdEntity.setNew(true);
            jdEntity.setJdId(jwCloudOrg.getId());
            jdEntity.setJdName(jwCloudOrg.getTitle());
            jdEntity.setParentId(jwCloudOrg.getParentId());
            jdEntity.setPath(jwCloudOrg.getPath());
            jdEntity.setPathArray(jwCloudOrg.getPathArray());
            //jdEntityList.add(jdEntity);

            for (QuEntity entity : quEntityList) {
                if (entity.getQuId().equals(jwCloudOrg.getParentId())) {
                    List<JDEntity> jdEntities = entity.getJdEntityList();
                    if (jdEntities == null)
                        jdEntities = new ArrayList<>();
                    jdEntities.add(jdEntity);
                    entity.setJdEntityList(jdEntities);
                    break;
                }
            }
        }

        for (JwCloudOrg jwCloudOrg : allSQExistOrg) {
            SQEntity sqEntity = new SQEntity();
            sqEntity.setNew(false);
            sqEntity.setSqId(jwCloudOrg.getId());
            sqEntity.setSqName(jwCloudOrg.getTitle());
            sqEntity.setParentId(jwCloudOrg.getParentId());
            sqEntity.setPath(jwCloudOrg.getPath());
            sqEntity.setPathArray(jwCloudOrg.getPathArray());
            //sqEntityList.add(sqEntity);

            for (QuEntity entity : quEntityList) {
                if (entity.getJdEntityList() == null || entity.getJdEntityList().isEmpty())
                    continue;
                for (JDEntity jdEntity : entity.getJdEntityList()) {
                    if (jdEntity.getJdId().equals(sqEntity.getParentId())) {
                        List<SQEntity> sqEntities = jdEntity.getSqEntityList();
                        if (sqEntities == null)
                            sqEntities = new ArrayList<>();
                        sqEntities.add(sqEntity);
                        jdEntity.setSqEntityList(sqEntities);
                    }
                }
            }
        }

        for (JwCloudOrg jwCloudOrg : allSQInsertOrg) {
            SQEntity sqEntity = new SQEntity();
            sqEntity.setNew(true);
            sqEntity.setSqId(jwCloudOrg.getId());
            sqEntity.setSqName(jwCloudOrg.getTitle());
            sqEntity.setParentId(jwCloudOrg.getParentId());
            sqEntity.setPath(jwCloudOrg.getPath());
            sqEntity.setPathArray(jwCloudOrg.getPathArray());
            //sqEntityList.add(sqEntity);

            for (QuEntity entity : quEntityList) {
                if (entity.getJdEntityList() == null || entity.getJdEntityList().isEmpty())
                    continue;
                for (JDEntity jdEntity : entity.getJdEntityList()) {
                    if (jdEntity.getJdId().equals(sqEntity.getParentId())) {
                        List<SQEntity> sqEntities = jdEntity.getSqEntityList();
                        if (sqEntities == null)
                            sqEntities = new ArrayList<>();
                        sqEntities.add(sqEntity);
                        jdEntity.setSqEntityList(sqEntities);
                    }
                }
            }
        }


//*********************************至此已经取出所有的org列表************************************************

        //先清空所有数据id/parentId/path
        for (QuEntity entity : quEntityList) {
            entity.setQuId("");
            entity.setParentId("");
            entity.setPath("");
            entity.setPathArray(null);
            if (entity.getJdEntityList() != null && !quEntityList.isEmpty()) {
                for (JDEntity jdEntity : entity.getJdEntityList()) {
                    jdEntity.setJdId("");
                    jdEntity.setParentId("");
                    jdEntity.setPath("");
                    jdEntity.setPathArray(null);
                    if (jdEntity.getSqEntityList() != null && !jdEntity.getSqEntityList().isEmpty()) {
                        for (SQEntity sqEntity : jdEntity.getSqEntityList()) {
                            sqEntity.setSqId("");
                            sqEntity.setParentId("");
                            sqEntity.setPath("");
                            sqEntity.setPathArray(null);
                        }
                    }
                }
            }
        }


        //设置原有数据的id/parentId/path
        for (JwCloudArea jwCloudArea : allQuArea) {
            for (QuEntity entity : quEntityList) {
                if (!entity.isNew() && entity.getQuName().contains(jwCloudArea.getName())) {
                    entity.setQuId(jwCloudArea.getId());
                    entity.setParentId(jwCloudArea.getParentId());
                    entity.setPath(jwCloudArea.getPath());
                }
            }
        }

        for (JwCloudArea jwCloudArea : allJDArea) {
            for (QuEntity entity : quEntityList) {
                if (entity.getJdEntityList() != null && !entity.getJdEntityList().isEmpty()) {
                    for (JDEntity jdEntity : entity.getJdEntityList()) {
                        if (!jdEntity.isNew() && jdEntity.getJdName().contains(jwCloudArea.getName())) {
                            jdEntity.setJdId(jwCloudArea.getId());
                            jdEntity.setParentId(jwCloudArea.getParentId());
                            jdEntity.setPath(jwCloudArea.getPath());
                        }
                    }
                }
            }
        }

        for (JwCloudArea jwCloudArea : allSQArea) {
            for (QuEntity entity : quEntityList) {
                if (entity.getJdEntityList() != null && !entity.getJdEntityList().isEmpty()) {
                    for (JDEntity jdEntity : entity.getJdEntityList()) {
                        if (jdEntity.getSqEntityList() != null && !jdEntity.getSqEntityList().isEmpty()) {
                            for (SQEntity sqEntity : jdEntity.getSqEntityList()) {
                                if (!sqEntity.isNew() && sqEntity.getSqName().contains(jwCloudArea.getName())) {
                                    sqEntity.setSqId(jwCloudArea.getId());
                                    sqEntity.setParentId(jwCloudArea.getParentId());
                                    sqEntity.setPath(jwCloudArea.getPath());
                                }
                            }
                        }
                    }
                }
            }
        }

        //*********************执行插入*********************
        for (QuEntity quEntity : quEntityList) {
            if (quEntity.isNew()) {
                //暂时不处理，因为没有
            } else {
                if (quEntity.getJdEntityList() != null && !quEntity.getJdEntityList().isEmpty()) {
                    for (JDEntity jdEntity : quEntity.getJdEntityList()) {
                        if (jdEntity.isNew()) {
                            //如果街道是新增的，就调用插入方法
                            jdEntity.setParentId(quEntity.getQuId());
                            jdEntity.setPath(quEntity.getPath() + jdEntity.getParentId() + ",");
                            dealService.insertJD(jdEntity);
                        }
                    }
                }
            }
        }

        for (QuEntity quEntity : quEntityList) {
            if (quEntity.isNew()) {
                //暂时不处理，因为没有
            } else {
                if (quEntity.getJdEntityList() != null && !quEntity.getJdEntityList().isEmpty()) {
                    for (JDEntity jdEntity : quEntity.getJdEntityList()) {
                        if (jdEntity.getSqEntityList() != null && !jdEntity.getSqEntityList().isEmpty()) {
                            for (SQEntity sqEntity : jdEntity.getSqEntityList()) {
                                if (sqEntity.isNew()) {
                                    sqEntity.setParentId(jdEntity.getJdId());
                                    sqEntity.setPath(jdEntity.getPath() + sqEntity.getParentId() + ",");
                                    dealService.insertSQ(sqEntity);
                                }
                            }
                        }
                    }
                }
            }
        }

        String jo = JSONObject.toJSONString(quEntityList);
        System.out.println(jo);
    }

    @Test
    public void selectSpaceSQName(){
        List<JwCloudArea> strings = dealService.selectSpaceSQName();
        System.out.println(strings.toString());
    }

    @Test
    public void updateSpaceSQName(){
        dealService.updateSpaceSQName();
    }

}
