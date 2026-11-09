package com.iemes.controller.workshop;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;

import net.sf.json.JSONObject;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.session.Session;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.iemes.annotation.SystemLog;
import com.iemes.controller.base.ConstantController;
import com.iemes.controller.index.BaseController;
import com.iemes.entity.BomFormMap;
import com.iemes.entity.ItemBomFormMap;
import com.iemes.entity.UserFormMap;
import com.iemes.plugin.PageView;
import com.iemes.score.GradeCalculateService;
import com.iemes.util.BomComponentValidator;
import com.iemes.util.Common;

/**
 * 
 * @author lanyuan 2014-11-19
 * @Email: mmm333zzz520@163.com
 * @version 3.0v
 */
@Controller
@RequestMapping("/item_resource/")
public class BomController extends BaseController {
	
	@Inject
	private GradeCalculateService gradeCalculateService;
	
	@RequestMapping("bom_list")
	public String listUI(Model model) throws Exception {
		model.addAttribute("res", findByRes());
		return Common.BACKGROUND_PATH + "/item_resource/bom/bom_list";
	}
	
	@ResponseBody
	@RequestMapping("bom_findByPage")
	public PageView findByPage(String pageNow,
			String pageSize) throws Exception {
		BomFormMap bomFormMap = getFormMap(BomFormMap.class);
		bomFormMap=toFormMap(bomFormMap, pageNow, pageSize,bomFormMap.getStr("orderby"));
		Session session = SecurityUtils.getSubject().getSession();
		bomFormMap.put("site", session.getAttribute("site"));
		pageView.setRecords(baseMapper.findByPage(bomFormMap));
		return pageView;
	}
	
	@RequestMapping("bom_addUI")
	public String addUI(Model model) throws Exception {
		return Common.BACKGROUND_PATH + "/item_resource/bom/bom_add";
	}
	
	@ResponseBody
	@RequestMapping("bom_addEntity")
	@Transactional(readOnly=false)
	@SystemLog(module="生产物料控制",methods="物料维护-新增物料清单")//凡需要处理业务逻辑的.都需要记录操作日志
	public String addEntity() throws Exception {
		BomFormMap bomFormMap = getFormMap(BomFormMap.class);
		
		// 获取request
		HttpServletRequest request = ((ServletRequestAttributes)RequestContextHolder.getRequestAttributes()).getRequest();  
		// 通过工具类获取当前登录的bean
		UserFormMap userFormMap = (UserFormMap)Common.findUserSession(request);
		Session session = SecurityUtils.getSubject().getSession();
		String[] itemNoArr = request.getParameterValues("item_no_1");
		String[] itemUseNumberArr = request.getParameterValues("item_use_number_1");
		String[] itemBalanceUpArr = request.getParameterValues("item_balance_up_1");
		String[] itemBalanceDownArr = request.getParameterValues("item_balance_down_1");
		try {
			BomComponentValidator.validate(itemNoArr, itemUseNumberArr, itemBalanceUpArr, itemBalanceDownArr);
		} catch (IllegalArgumentException invalid) {
			JSONObject error = new JSONObject();
			error.put("result", ConstantController.ERROR);
			error.put("message", invalid.getMessage());
			return error.toString();
		}
		bomFormMap.put("site", session.getAttribute("site"));
		bomFormMap.put("by_user", userFormMap.getStr("accountName"));
		baseMapper.addEntity(bomFormMap);
		
		// 物料清单子项
		String itemBomNo = request.getParameter("bomFormMap.item_bom_no");
		if(null != itemNoArr ){
			for(int i = 0 ;i<itemNoArr.length;i++){
				ItemBomFormMap itemBomFormMap = new ItemBomFormMap();
				itemBomFormMap.put("item_bom_no", itemBomNo);
				itemBomFormMap.put("item_no", itemNoArr[i]);
				itemBomFormMap.put("use_number", itemUseNumberArr[i]);
				itemBomFormMap.put("balance_up", itemBalanceUpArr[i]);
				itemBomFormMap.put("balance_down", itemBalanceDownArr[i]);
				itemBomFormMap.put("site", bomFormMap.getStr("site"));
				baseMapper.addEntity(itemBomFormMap);
			}
		}

		JSONObject json = new JSONObject();
		String rs = null;
		try {
			rs = gradeCalculateService.GradeCalculate("物料组维护", null);
			json.put("result", ConstantController.SUCCESS);
			json.put("score", rs);
		} catch (Exception e) {
			json.put("result", ConstantController.ERROR);
		}
		return json.toString();
	}
	
	@RequestMapping("bom_editUI")
	public String editUI(Model model) throws Exception {
		String id = getPara("id");
		BomFormMap bomFormMap = findCurrentSiteBom(id);
		if (bomFormMap == null) return "redirect:/denied.jsp";
		model.addAttribute("bom", bomFormMap);
		ItemBomFormMap itemBomFormMap = new ItemBomFormMap();
		itemBomFormMap.put("item_bom_no", bomFormMap.getStr("item_bom_no"));
		itemBomFormMap.put("site", currentSite());
		List<ItemBomFormMap> itemBom = baseMapper.findByNames(itemBomFormMap);
		model.addAttribute("item_bom", itemBom);
		return Common.BACKGROUND_PATH + "/item_resource/bom/bom_edit";
	}

	private String currentSite() {
		return String.valueOf(SecurityUtils.getSubject().getSession().getAttribute("site"));
	}

	private BomFormMap findCurrentSiteBom(String id) {
		if (Common.isEmpty(id)) return null;
		BomFormMap bom = baseMapper.findbyFrist("id", id, BomFormMap.class);
		return bom != null && currentSite().equals(bom.getStr("site")) ? bom : null;
	}
	
	@ResponseBody
	@RequestMapping("bom_editEntity")
	@Transactional(readOnly=false)//需要事务操作必须加入此注解
	@SystemLog(module="生产物料控制",methods="物料维护-修改物料清单")//凡需要处理业务逻辑的.都需要记录操作日志
	public String editEntity(HttpServletRequest request) throws Exception {
		BomFormMap bomFormMap = getFormMap(BomFormMap.class);
		BomFormMap existing = findCurrentSiteBom(bomFormMap.getStr("id"));
		if (existing == null || !existing.getStr("item_bom_no").equals(bomFormMap.getStr("item_bom_no"))) return "error";
		String itemBomNo = existing.getStr("item_bom_no");
		String[] itemNoArr = request.getParameterValues("item_no_1");
		String[] itemUseNumberArr = request.getParameterValues("item_use_number_1");
		String[] itemBalanceUpArr = request.getParameterValues("item_balance_up_1");
		String[] itemBalanceDownArr = request.getParameterValues("item_balance_down_1");
		try {
			BomComponentValidator.validate(itemNoArr, itemUseNumberArr, itemBalanceUpArr, itemBalanceDownArr);
		} catch (IllegalArgumentException invalid) {
			return "error";
		}
		BomFormMap update = new BomFormMap();
		update.put("id", existing.get("id"));
		for (String field : new String[] { "item_bom_name", "item_bom_desc", "state" }) {
			if (bomFormMap.containsKey(field)) update.put(field, bomFormMap.get(field));
		}
		if (update.size() == 1) return "error";
		baseMapper.editEntity(update);
		ItemBomFormMap oldChildren = new ItemBomFormMap();
		oldChildren.put("item_bom_no", "'" + itemBomNo + "'");
		oldChildren.put("site", "'" + currentSite() + "'");
		baseMapper.deleteByNames(oldChildren);
		// 用服务器会话站点重建清单子项，不能信任表单中的 site。
		if(null != itemNoArr ){
			for(int i = 0 ;i<itemNoArr.length;i++){
				ItemBomFormMap itemBomFormMap = new ItemBomFormMap();
				itemBomFormMap.put("item_bom_no", itemBomNo);
				itemBomFormMap.put("item_no", itemNoArr[i]);
				itemBomFormMap.put("use_number", itemUseNumberArr[i]);
				itemBomFormMap.put("balance_up", itemBalanceUpArr[i]);
				itemBomFormMap.put("balance_down", itemBalanceDownArr[i]);
				itemBomFormMap.put("site", currentSite());
				baseMapper.addEntity(itemBomFormMap);
			}
		}
		return "success";
	}
	
	@ResponseBody
	@RequestMapping("bom_deleteEntity")
	@Transactional(readOnly=false)//需要事务操作必须加入此注解
	@SystemLog(module="生产物料控制",methods="物料维护-删除物料清单")//凡需要处理业务逻辑的.都需要记录操作日志
	public String deleteEntity() throws Exception {
		String[] ids = getParaValues("ids");
		if (ids == null || ids.length == 0) return "error";
		List<BomFormMap> boms = new ArrayList<BomFormMap>();
		for (String id : ids) {
			BomFormMap bom = findCurrentSiteBom(id);
			if (bom == null) return "error";
			boms.add(bom);
		}
		for (BomFormMap bom : boms) {
			ItemBomFormMap itemBomFormMap = new ItemBomFormMap();
			itemBomFormMap.put("item_bom_no", "'" + bom.getStr("item_bom_no") + "'");
			itemBomFormMap.put("site", "'" + currentSite() + "'");
			baseMapper.deleteByNames(itemBomFormMap);
			baseMapper.deleteByAttribute("id", String.valueOf(bom.get("id")), BomFormMap.class);
		}
		return "success";
	}

}
