package gee.example.mybatissample.service;

import gee.example.mybatissample.entity.*;

import java.util.List;

/**
 * @author Gee
 * @date 2020/11/12 15:13
 * @description
 */
public interface DealService {
    List<JwCloudOrg> findAllOrg();
    List<JwCloudArea> findAllArea();
    int insertQu(QuEntity quEntity);
    int insertJD(JDEntity jdEntity);
    int insertSQ(SQEntity sqEntity);


    List<JwCloudArea> selectSpaceSQName();
    int updateSpaceSQName();
}
