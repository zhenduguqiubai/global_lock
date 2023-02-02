package com.bjpowernode.distributed.mapper;

import com.bjpowernode.distributed.model.RedPacketRecord;
import com.bjpowernode.distributed.model.RedPacketRecordExample;
import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
@Mapper
public interface RedPacketRecordMapper {
    long countByExample(RedPacketRecordExample example);

    int deleteByExample(RedPacketRecordExample example);

    int deleteByPrimaryKey(Integer id);

    int insert(RedPacketRecord record);

    int insertSelective(RedPacketRecord record);

    List<RedPacketRecord> selectByExample(RedPacketRecordExample example);

    RedPacketRecord selectByPrimaryKey(Integer id);

    int updateByExampleSelective(@Param("record") RedPacketRecord record, @Param("example") RedPacketRecordExample example);

    int updateByExample(@Param("record") RedPacketRecord record, @Param("example") RedPacketRecordExample example);

    int updateByPrimaryKeySelective(RedPacketRecord record);

    int updateByPrimaryKey(RedPacketRecord record);
}