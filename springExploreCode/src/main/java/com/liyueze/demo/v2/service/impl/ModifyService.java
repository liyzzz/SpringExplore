package com.liyueze.demo.v2.service.impl;

import com.liyueze.demo.v2.service.IModifyService;
import com.liyueze.mvcFrameworkCode.v2.annotation.ServiceV2;
import lombok.extern.slf4j.Slf4j;

/**
 * 增删改业务
 * @author Tom
 *
 */
@ServiceV2
@Slf4j
public class ModifyService implements IModifyService {

	/**
	 * 增加
	 */
	public String add(String name,String addr) {
		log.info("test");
		return "modifyService add,name=" + name + ",addr=" + addr;
	}

	/**
	 * 修改
	 */
	public String edit(Integer id,String name) {
		return "modifyService edit,id=" + id + ",name=" + name;
	}

	/**
	 * 删除
	 */
	public String remove(Integer id) {
		return "modifyService id=" + id;
	}
	
}
