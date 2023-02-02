package com.bjpowernode.distributed.model;

import java.util.Date;

public class RedPacketRecord {
    private Integer id;

    private Integer userId;

    private Integer redId;

    private Date createTime;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Integer getRedId() {
        return redId;
    }

    public void setRedId(Integer redId) {
        this.redId = redId;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
}