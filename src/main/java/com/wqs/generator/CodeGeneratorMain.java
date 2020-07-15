package com.wqs.generator;


import com.wqs.generator.util.CodeGeneratorUtil;

public class CodeGeneratorMain {

    public static void main(String[] args) throws Exception {
        CodeGeneratorUtil codeGeneratorUtil = new CodeGeneratorUtil();
//        //指定目录,包路径,作者

        codeGeneratorUtil.init("/Users/relax/Desktop/java/", "com.wqs", "wangqingsong", false);
        codeGeneratorUtil.generate();

    }

}
