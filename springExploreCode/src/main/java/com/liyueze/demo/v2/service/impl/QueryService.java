package com.liyueze.demo.v2.service.impl;


import com.liyueze.demo.v2.service.IModifyService;
import com.liyueze.demo.v2.service.IQueryService;
import com.liyueze.mvcFrameworkCode.v2.annotation.AutowiredV2;
import com.liyueze.mvcFrameworkCode.v2.annotation.ServiceV2;
import lombok.extern.slf4j.Slf4j;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 查询业务
 *
 */
@ServiceV2
@Slf4j
public class QueryService implements IQueryService {
	@AutowiredV2
	private IModifyService iModifyService;
	/**
	 * 查询
	 */
	public String query(String name) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String time = sdf.format(new Date());
		String json = "{name:\"" + name + "\",time:\"" + time + "\"}";
		log.debug("这是在业务方法中打印的：" + json);
		return json;
	}

	@Override
	public String queryAll() {
		iModifyService.edit(1,"liyzzz");
		System.out.println("{liyueze,liyzzz,Analyze}");
		return "{liyueze,liyzzz,Analyze}";
	}

	@Override
	public String queryError(){
		int i=1/0;
		return "这个方法是故意抛错用来测试的";
	}
}
