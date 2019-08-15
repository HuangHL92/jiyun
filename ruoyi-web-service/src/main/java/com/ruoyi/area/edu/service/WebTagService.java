package com.ruoyi.area.edu.service;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.ruoyi.area.edu.domain.Tag;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.system.domain.SysDictData;
import com.ruoyi.system.domain.SysDictType;
import com.ruoyi.system.service.ISysDictDataService;
import com.ruoyi.system.service.ISysDictTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * RuoYi首创 html调用 thymeleaf 实现字典读取
 *
 * @author ruoyi
 */
@Service("tag")
public class WebTagService {
    @Autowired
    private ITagService tagService;
    @Autowired
    private ISysDictDataService sysDictDataService;

    /**
     * 根据字典类型查询字典数据信息
     *
     * @param tagTypes 字典类型
     * @return 参数键值
     */
    public Map<String, Object> getTagList(String... tagTypes) {
        Map result = new HashMap();
        if (tagTypes != null) {
            for (String tagType : tagTypes) {
                if (StrUtil.isNotEmpty(tagType)) {
                    String dictLabel = sysDictDataService.selectDictLabel("edu_tag_type", tagType);
                    if (StrUtil.isNotEmpty(dictLabel)) {
                        QueryWrapper<Tag> query = new QueryWrapper();
                        query.lambda().eq(Tag::getType, tagType);
                        result.put(dictLabel, tagService.list(query));
                    }
                }
            }
        }
        return result;
    }

    /**
     * 根据字典类型和字典键值查询字典数据信息
     *
     * @param dictType 字典类型
     * @param dictValue 字典键值
     * @return 字典标签
     */
//    public String getLabel(String dictType, String dictValue)
//    {
//
//        return dictDataService.selectDictLabel(dictType, dictValue);
//    }
}
