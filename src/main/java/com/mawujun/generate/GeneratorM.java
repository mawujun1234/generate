package com.mawujun.generate;

import java.io.IOException;

public class GeneratorM {
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		GeneratorMT generatorMT=new GeneratorMT();
		//generatorMT.generateM("com.mawujun","E:\\eclipse\\aaa\\knpcrm\\src\\main\\java","com.mawujun.utils");
		//System.out.println(GeneratorM.class.getResource("/").getPath());
		//System.out.println(System.getProperty("user.dir"));
		String scan_package="com.sunland";
		String save_package="com.sunland.qogir.common.utils";//保存的包名
		//String project_src=generatorMT.getClass().getResource("/").getPath()+"../../src/main/java";
		String project_src="E:\\workspace\\zhzj\\commons\\common-core\\src\\main\\java";
		generatorMT.generateM(scan_package,project_src,save_package);
		
		
		generatorMT.generateFK(scan_package,"E:\\workspace\\zhzj\\misc\\msic-admin\\gen");
		
		generatorMT.generaterCSRD("com.sunland.qogir","E:\\workspace\\zhzj\\misc\\msic-admin\\gen","com.sunland.qogir");
		//generatorMT.generaterCSRD("E:\\workspace\\zhzj\\zhzj\\qoqw-common\\gen","com.sunland.qoqw",Preinstall.class,Camera.class,PreinstallList.class);
	}
}
