package gee.example.mybatissample.service.impl;

import gee.example.mybatissample.entity.*;
import gee.example.mybatissample.mapper.DealMapper;
import gee.example.mybatissample.service.DealService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author Gee
 * @date 2020/11/12 15:13
 * @description
 */
@Service
public class DealServiceImpl implements DealService {

    @Autowired
    DealMapper dealMapper;

    @Override
    public List<JwCloudOrg> findAllOrg() {
        return dealMapper.findAllOrg();
    }

    @Override
    public List<JwCloudArea> findAllArea() {
        return dealMapper.findAllArea();
    }

    @Override
    public int insertQu(QuEntity quEntity) {
        return dealMapper.insertQu(quEntity);
    }

    @Override
    public int insertJD(JDEntity jdEntity) {
        return dealMapper.insertJD(jdEntity);
    }

    @Override
    public int insertSQ(SQEntity sqEntity) {
        return dealMapper.insertSQ(sqEntity);
    }

    @Override
    public int updateSpaceSQName() {
        return dealMapper.updateSpaceSQName();
    }

    @Override
    public List<JwCloudArea> selectSpaceSQName() {
        return dealMapper.selectSpaceSQName();
    }
}
