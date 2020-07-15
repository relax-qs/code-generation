package ${package_name}.service.impl;

import ${package_name}.service.mapper.${table_name}Mapper;
import ${package_name}.api.I${table_name}Service;

import org.springframework.stereotype.Service;

/**
* @description  服务实现层
* @author ${author}
* @date ${date}
*/
@Service
public class ${table_name}ServiceImpl  implements I${table_name}Service {

    @Autowired
    private ${table_name}Mapper ${table_name?uncap_first}Mapper;

}