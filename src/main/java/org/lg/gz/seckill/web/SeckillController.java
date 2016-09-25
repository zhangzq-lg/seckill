package org.lg.gz.seckill.web;

import org.lg.gz.seckill.dto.Exposer;
import org.lg.gz.seckill.dto.SeckillExecution;
import org.lg.gz.seckill.dto.SeckillResult;
import org.lg.gz.seckill.entity.Seckill;
import org.lg.gz.seckill.enums.SeckillStateEnum;
import org.lg.gz.seckill.exception.RepeatKillException;
import org.lg.gz.seckill.exception.SeckillCloseException;
import org.lg.gz.seckill.service.SeckillService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

/**
 * Created by zzq_eason on 2016/9/20.
 */
@Controller
@RequestMapping("/seckill") // url:/模块/资源/{id}/细分（这属于Restful风格设计url）
public class SeckillController {

	private static final Logger LOGGER = LoggerFactory.getLogger(SeckillController.class);

	@Autowired
	private SeckillService seckillService;

	@RequestMapping(value = "/list", method = RequestMethod.GET)
	public String list(Model model) {
		// list.jsp + model = ModelAndView
		List<Seckill> list = seckillService.getSeckillList();
		model.addAttribute("list", list);
		return "list";
	}

	@RequestMapping(value = "/{seckillId}/detail", method = RequestMethod.GET)
	public String detail(@PathVariable("seckillId") Long seckillId, Model model) {
		if (seckillId == null) {
			return "redirect:/seckill/list";
		}
		Seckill seckill = seckillService.getById(seckillId);
		if (seckill == null) {
			return "forward:/seckill/list";
		}
		model.addAttribute("seckill", seckill);
		return "detail";
	}

	/**
	 * 以下是ajax接口
	 * 返回格式：json
	 * 注意要使用produces规定好响应头信息（即数据交互格式及编码）
	 */
	@RequestMapping(value = "/{seckillId}/exposer",
			method = RequestMethod.POST,
			produces = {"application/json;charset=UTF-8"})
	@ResponseBody
	public SeckillResult<Exposer> exposer(@PathVariable Long seckillId) {
		SeckillResult<Exposer> result;
		try {
			Exposer exposer = seckillService.exportSeckillUrl(seckillId);
			result = new SeckillResult<Exposer>(true, exposer);

		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
			result = new SeckillResult<Exposer>(false, e.getMessage());
		}
		return result;
	}

	@RequestMapping(value = "/{seckillId}/{md5}/execution",
			method = RequestMethod.POST,
			produces = {"application/json;charset=UTF-8"})
	@ResponseBody
	public SeckillResult<SeckillExecution> execute(@PathVariable("seckillId") Long seckillId,
												   @PathVariable("md5") String md5,
												   @CookieValue(value = "killPhone", required = false) Long phone) {
		// 可以使用spring自带的验证
		if (phone == null) {
			return new SeckillResult<SeckillExecution>(false, "未注册");
		}
		try {
			SeckillExecution execution = seckillService.executeSeckillProcedure(seckillId, phone, md5);//seckillService.executeSeckill(seckillId, phone, md5);
			LOGGER.info(execution.toString());
			return new SeckillResult<SeckillExecution>(true, execution);
		} catch (SeckillCloseException e) {
			SeckillExecution execution = new SeckillExecution(seckillId, SeckillStateEnum.END);
			return new SeckillResult<SeckillExecution>(true, execution);
		} catch (RepeatKillException e) {
			SeckillExecution execution = new SeckillExecution(seckillId, SeckillStateEnum.REPEATE_KILL);
			return new SeckillResult<SeckillExecution>(true, execution);
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
			SeckillExecution execution = new SeckillExecution(seckillId, SeckillStateEnum.INNER_ERROR);
			return new SeckillResult<SeckillExecution>(true, execution);
		}
	}

	@RequestMapping(value = "/time/now", method = RequestMethod.GET)
	@ResponseBody
	public SeckillResult<Long> time() {
		Date nowTime = new Date();
		return new SeckillResult<Long>(true, nowTime.getTime());
	}
}
