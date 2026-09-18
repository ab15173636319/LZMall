package org.lzmservice.entity;


import lombok.Data;
import lombok.ToString;
import org.lzmcommon.entity.BaseEntity;

@Data
@ToString
public class Test extends BaseEntity {

    private String name;
    private String gender;

}
