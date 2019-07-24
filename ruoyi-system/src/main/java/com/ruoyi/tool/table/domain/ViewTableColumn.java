package com.ruoyi.tool.table.domain;

import lombok.Data;

@Data
public class ViewTableColumn {

    private String title;
    private String align;
    private int width;
    private String field;
    private boolean visible;

}