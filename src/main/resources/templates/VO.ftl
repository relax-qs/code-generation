package ${package_name}.api.vo;

import com.dplat.framework.common.domain.*;
import java.util.Date;
import java.math.BigDecimal;
import lombok.Data;

/**
* @description
* @author ${author}
* @date ${date}
*/
@Data
public class ${table_name}Vo {
    <#if model_column?exists>
        <#list model_column as model>
    /**
    *${model.columnComment!}
    */
    private ${model.javaType} ${model.changeColumnName?uncap_first};
        
        </#list>
    </#if>
}
