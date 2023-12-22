package com.louis.springbootinit.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;

/**
 * @author louis
 * @version 1.0
 * @date 2023/12/22 10:47
 */
@Data
@TableName(value = "drug")
public class Drug implements Serializable {
    @TableField(exist = false)
    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Integer Id;
    /**
     * 药品名
     */
    private String DrugName;
    /**
     * 库存
     * 默认100000
     */
    private Integer Count;
    /**
     * 药瓶类型
     */
    private String Type;
    /**
     * 单价
     */
    private BigDecimal Price;
    /**
     * （首次）添加药品时间
     */
    private Timestamp CreatedAt;
    /**
     * 更新药品时间
     */
    private Timestamp UpdatedAt;

}
