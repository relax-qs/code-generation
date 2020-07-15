package ${package_name}.service.impl;

import ${package_name}.service.entity.${table_name};
import ${package_name}.service.mapper.${table_name}Mapper;
import ${package_name}.service.I${table_name}LocalService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


/**
* @description  服务实现层
* @author ${author}
* @date ${date}
*/
@Service
public class ${table_name}LocalServiceImpl implements I${table_name}LocalService {

    @Autowired
    private ${table_name}Mapper ${table_name?uncap_first}Mapper;

}