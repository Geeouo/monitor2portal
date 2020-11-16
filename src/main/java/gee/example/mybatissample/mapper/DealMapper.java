package gee.example.mybatissample.mapper;

import gee.example.mybatissample.entity.*;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author Gee
 * @date 2020/11/12 15:28
 * @description
 */
@Repository
public interface DealMapper {

     List<JwCloudOrg> findAllOrg();
     List<JwCloudArea> findAllArea() ;

     int insertQu(QuEntity quEntity);
     int insertJD(JDEntity jdEntity);
     int insertSQ(SQEntity sqEntity);


     @Select(value = "select id,name from jw_cloud_area WHERE name LIKE\"% %\" AND id>663;")
     List<JwCloudArea> selectSpaceSQName();
     @Update(value = "update jw_cloud_area  set name= REPLACE(name,\" \",\"\") \n" +
             "where id in (select temp.id from (select area.id from jw_cloud_area as area WHERE area.name LIKE \"% %\" AND id > 663 ) as temp where id not in (2885,3051));")
     int updateSpaceSQName();
}
