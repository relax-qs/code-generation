package ${package_name}.service.entity;

import javax.persistence.*;
import java.util.Date;
import java.math.BigDecimal;

import com.dplat.framework.common.domain.*;

/**
* @description
* @author ${author}
* @date ${date}
*/
@Entity
@Table(name="${table_name_small}")
public class ${table_name} {

    <#if model_column?exists>
        <#list model_column as model>
    /**
    *${model.columnComment!}
    */
    @Column(name = "${model.columnName}")
    private ${model.javaType} ${model.changeColumnName?uncap_first};
        </#list>
    </#if>

<#if model_column?exists>
<#list model_column as model>
    public ${model.javaType} get${model.changeColumnName}() {
        return this.${model.changeColumnName?uncap_first};
    }

    public void set${model.changeColumnName}(${model.javaType} ${model.changeColumnName?uncap_first}) {
        this.${model.changeColumnName?uncap_first} = ${model.changeColumnName?uncap_first};
    }
</#list>
</#if>

}