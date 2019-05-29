package com.liyueze.demo.v2.service.impl;


import com.liyueze.demo.v2.service.IModifyService;
import com.liyueze.mvcFrameworkCode.v2.annotation.AutowiredV2;
import com.liyueze.mvcFrameworkCode.v2.annotation.ServiceV2;
import lombok.extern.slf4j.Slf4j;

/**
 * 删除
 * 为了测试Cglib
 *
 */
@ServiceV2
@Slf4j
public class DeleteService {
	@AutowiredV2
	private IModifyService iModifyService;

	public void delete(){
		String[] ids={"1","2"};
		for(String id:ids){
			iModifyService.remove(Integer.parseInt(id));
		}
	}

	public String deleteById(Integer id){
		return "deleteById id=" + id;
	}

}
