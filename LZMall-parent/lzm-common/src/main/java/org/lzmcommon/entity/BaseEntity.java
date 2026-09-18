package org.lzmcommon.entity;

import lombok.Data;

import java.sql.Timestamp;

@Data
public class BaseEntity {

    private long id;
    private Timestamp createTime;
    private Timestamp updateTime;
    private boolean isDelete;

}
