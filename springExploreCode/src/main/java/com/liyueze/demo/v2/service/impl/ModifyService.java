package com.liyueze.demo.v2.service.impl;

import com.liyueze.demo.v2.service.IModifyService;
import com.liyueze.demo.v2.service.IQueryService;
import com.liyueze.mvcFrameworkCode.v2.annotation.AutowiredV2;
import com.liyueze.mvcFrameworkCode.v2.annotation.ServiceV2;
import lombok.extern.slf4j.Slf4j;

/**
 * 增删改业务
 *
 */
@ServiceV2
@Slf4j
public class ModifyService implements IModifyService {
	@AutowiredV2
	private IQueryService iQueryService;
	@AutowiredV2
	private DeleteService deleteService;

	/**
	 * 增加
	 */
	public String add(String name,String addr) {
		log.debug("testAdd");
		iQueryService.query("liyueze");
		return "modifyService add,name=" + name + ",addr=" + addr;
	}

	/**
	 * 修改
	 */
	public String edit(Integer id,String name) {
		System.out.println("modifyService edit,id=" + id + ",name=" + name);
		return "modifyService edit,id=" + id + ",name=" + name;
	}

	/**
	 * 删除
	 */
	public String remove(Integer id) {
		System.out.println("---------------");
		return deleteService.deleteById(id);
	}
	
}
