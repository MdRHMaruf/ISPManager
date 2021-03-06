package com.manager.example.controller;

import java.rmi.activation.ActivateFailedException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.manager.inventory.entity.Customer;
import com.manager.inventory.entity.Employee;
import com.manager.inventory.services.CustomerService;
import com.manager.inventory.services.PackageService;
import com.manager.security.entityModel.MyUserDetails;
import com.manager.support.entity.ActivationTMS;
import com.manager.support.entity.ComplainTMS;
import com.manager.support.entity.ConnectionPoint;
import com.manager.support.entity.McInformation;
import com.manager.support.entity.OltInformation;
import com.manager.support.services.ActivationTMSService;
import com.manager.support.services.ComplainTMSService;
import com.manager.support.services.ConnectionPointService;
import com.manager.support.services.McInfoService;
import com.manager.support.services.OltInfoService;
import com.manager.support.services.PPPoEService;

@Controller
public class SupportController {

	@Autowired
	ConnectionPointService connPointService;
	@Autowired
	OltInfoService oltInfoService;
	@Autowired
	McInfoService mcInfoService;
	@Autowired
	ActivationTMSService activationService;
	@Autowired
	PackageService packageService;
	@Autowired
	ComplainTMSService complainService;
	@Autowired
	CustomerService customerService;
	@Autowired
	PPPoEService pppoeService;
	
	//Activation TMS
	@RequestMapping(value={"/support/activation-tms"})
	public ModelAndView activation_tms(ModelMap map,HttpSession session) {
		Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		ModelAndView view = new ModelAndView("support/activation-tms");
		map.addAttribute("packageList",packageService.getServiceList());
		//map.addAttribute("resourceList",resourceService.getResourceList());
		return view;
	}

	@RequestMapping(value= {"/submitActivationTMSRequest"},method=RequestMethod.POST)
	public @ResponseBody Map<String, Object> submitActivationTMSRequest(Customer customer,ActivationTMS activationTms) {
		Map<String, Object> obj = new HashMap();
		MyUserDetails userDetails = (MyUserDetails)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		
		customer.setCustomerId(customerService.getMaxCustomerId());
		customer.setEntryTime(new Timestamp(new Date().getTime()));
		customer.setEntryBy(userDetails.getId());
		
		if(customerService.saveCustomer(customer)!=null) {
			activationTms.setTmsNo(activationService.getMaxTMSNo());
			activationTms.setCustomerId(customer.getCustomerId());
			activationTms.setSubject("Activation: "+userDetails.getUsername());
			activationTms.setEntryTime(new Timestamp(new Date().getTime()));
			activationTms.setPriority("1");
			activationTms.setStatus("1");
			activationTms.setLastFollowupBy((int)userDetails.getId());
			activationTms.setLastFollowupTime(new java.sql.Date(new Date().getTime()));
			activationTms.setEntryBy(userDetails.getId());
			if(activationService.saveActivationTMS(activationTms) != null) {
				obj.put("result", "successfull");
			}
		}else {
			obj.put("result","something wrong");
		}
		
		return obj;
	}


	//Complain TMS
	@RequestMapping(value={"/support/complain-tms"})
	public ModelAndView complain_tms(ModelMap map,HttpSession session) {
		Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		ModelAndView view = new ModelAndView("support/complain-tms");
		//map.addAttribute("roleList",roleService.getRoleList());
		//map.addAttribute("resourceList",resourceService.getResourceList());
		return view;
	}
	
	@RequestMapping(value= {"/submitComplainTMS"},method=RequestMethod.POST)
	public @ResponseBody Map<String, Object> submitComplainTMS(ComplainTMS complainTms) {
		Map<String, Object> obj = new HashMap();
		MyUserDetails userDetails = (MyUserDetails)SecurityContextHolder.getContext().getAuthentication().getPrincipal();

		complainTms.setEntryTime(new Timestamp(new Date().getTime()));
		complainTms.setEntryBy(userDetails.getId());
		obj.put("result", complainService.saveComplainTMS(complainTms));
		return obj;
	}

	@RequestMapping(value={"/support/activation-ticket-list"})
	public ModelAndView activation_ticket_list(ModelMap map,HttpSession session) {
		Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		ModelAndView view = new ModelAndView("support/activation-ticket-list");
		//map.addAttribute("roleList",roleService.getRoleList());
		//map.addAttribute("resourceList",resourceService.getResourceList());
		return view;
	}
	
	@RequestMapping(value={"/support/complain-ticket-list"})
	public ModelAndView complain_ticket_list(ModelMap map,HttpSession session) {
		Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		ModelAndView view = new ModelAndView("support/complain-ticket-list");
		//map.addAttribute("roleList",roleService.getRoleList());
		//map.addAttribute("resourceList",resourceService.getResourceList());
		return view;
	}

	@RequestMapping(value={"/activation-tms-details/{ticketNo}"})
	public ModelAndView tms_details(ModelMap map,HttpSession session,@PathVariable("ticketNo") String ticketNo) {
		Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		ModelAndView view = new ModelAndView("support/activation-tms-details");
		//map.addAttribute("roleList",roleService.getRoleList());
		//map.addAttribute("resourceList",resourceService.getResourceList());
		return view;
	}
	
	@RequestMapping(value={"/complain-ticket-details/{ticketNo}"})
	public ModelAndView complain_ticket_details(ModelMap map,HttpSession session,@PathVariable("ticketNo") String ticketNo) {
		Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		ModelAndView view = new ModelAndView("support/complain-ticket-details");
		//map.addAttribute("roleList",roleService.getRoleList());
		//map.addAttribute("resourceList",resourceService.getResourceList());
		return view;
	}

	//Connection Point
	@RequestMapping(value={"/support/connection-point"})
	public ModelAndView connection_point(ModelMap map,HttpSession session) {
		Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		ModelAndView view = new ModelAndView("support/connection-point");
		map.addAttribute("connectionList",connPointService.getConnectionPointList());
		//map.addAttribute("resourceList",resourceService.getResourceList());
		return view;
	}

	@RequestMapping(value= {"/saveConnectionPoint"},method=RequestMethod.POST)
	public @ResponseBody Map<String, Object> saveConnectionPoint(ConnectionPoint connectionPoint) {
		Map<String, Object> obj = new HashMap();
		MyUserDetails userDetails = (MyUserDetails)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		if(!connPointService.isConnectionPointExist(connectionPoint.getConnectionPointName(), 0)) {
			connectionPoint.setEntryTime(new Timestamp(new Date().getTime()));
			connectionPoint.setEntryBy(userDetails.getId());
			obj.put("result", connPointService.saveConnectionPoint(connectionPoint));
			obj.put("connectionList",connPointService.getConnectionPointList());
		}else {
			obj.put("result", "duplicate");
		}


		return obj;
	}

	@RequestMapping(value= {"/editConnectionPoint"},method=RequestMethod.POST)
	public @ResponseBody Map<String, Object> editConnectionPoint(ConnectionPoint connectionPoint) {
		Map<String, Object> obj = new HashMap();
		MyUserDetails userDetails = (MyUserDetails)SecurityContextHolder.getContext().getAuthentication().getPrincipal();

		if(!connPointService.isConnectionPointExist(connectionPoint.getConnectionPointName(), connectionPoint.getId())) {
			connectionPoint.setEntryTime(new Timestamp(new Date().getTime()));
			connectionPoint.setEntryBy(userDetails.getId());
			obj.put("result", connPointService.saveConnectionPoint(connectionPoint));
			obj.put("connectionList",connPointService.getConnectionPointList());
		}else {
			obj.put("result", "duplicate");
		}

		return obj;
	}


	@RequestMapping(value= {"/getConnectionPoint"},method=RequestMethod.GET)
	public @ResponseBody Map<String, Object> getConnectionPoint(String id){
		Map<String, Object> obj = new HashMap();
		obj.put("connectionPointInfo",connPointService.findConnectionPoint(Long.valueOf(id)));
		return obj;
	}


	// OLT Position
	@RequestMapping(value={"/support/olt-information"})
	public ModelAndView olt_position(ModelMap map,HttpSession session) {
		Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		ModelAndView view = new ModelAndView("support/olt-information");
		map.addAttribute("oltInfoList",oltInfoService.getOltInformations());
		//map.addAttribute("resourceList",resourceService.getResourceList());
		return view;
	}

	@RequestMapping(value= {"/saveOltInfo"},method=RequestMethod.POST)
	public @ResponseBody Map<String, Object> saveOltInfo(OltInformation oltMcPosition) {
		Map<String, Object> obj = new HashMap();
		MyUserDetails userDetails = (MyUserDetails)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		oltMcPosition.setEntryTime(new Timestamp(new Date().getTime()));
		oltMcPosition.setEntryBy(userDetails.getId());
		obj.put("result", oltInfoService.saveOltInformation(oltMcPosition));
		obj.put("oltInfoList",oltInfoService.getOltInformations());
		return obj;
	}

	@RequestMapping(value= {"/editOltInfo"},method=RequestMethod.POST)
	public @ResponseBody Map<String, Object> editOltInfo(OltInformation oltMcPosition) {
		Map<String, Object> obj = new HashMap();
		MyUserDetails userDetails = (MyUserDetails)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		oltMcPosition.setEntryTime(new Timestamp(new Date().getTime()));
		oltMcPosition.setEntryBy(userDetails.getId());
		obj.put("result", oltInfoService.saveOltInformation(oltMcPosition));
		obj.put("oltInfoList",oltInfoService.getOltInformations());
		return obj;
	}

	@RequestMapping(value= {"/getOltInfo"},method=RequestMethod.GET)
	public @ResponseBody Map<String, Object> getOltInfo(String id){
		Map<String, Object> obj = new HashMap();
		obj.put("oltInfo",oltInfoService.getOltInformation(Long.valueOf(id)));
		return obj;
	}
	
	
	// MC Position
		@RequestMapping(value={"/support/mc-information"})
		public ModelAndView mc_position(ModelMap map,HttpSession session) {
			Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
			ModelAndView view = new ModelAndView("support/mc-information");
			map.addAttribute("mcInfoList",mcInfoService.getMcInformations());
			//map.addAttribute("resourceList",resourceService.getResourceList());
			return view;
		}

		@RequestMapping(value= {"/saveMcInfo"},method=RequestMethod.POST)
		public @ResponseBody Map<String, Object> saveMcInfo(McInformation mcInfo) {
			Map<String, Object> obj = new HashMap();
			MyUserDetails userDetails = (MyUserDetails)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
			mcInfo.setEntryTime(new Timestamp(new Date().getTime()));
			mcInfo.setEntryBy(userDetails.getId());
			obj.put("result", mcInfoService.saveMcInformation(mcInfo));
			obj.put("mcInfoList",mcInfoService.getMcInformations());
			return obj;
		}

		@RequestMapping(value= {"/editMcInfo"},method=RequestMethod.POST)
		public @ResponseBody Map<String, Object> editMcInfo(McInformation mcInfo) {
			Map<String, Object> obj = new HashMap();
			MyUserDetails userDetails = (MyUserDetails)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
			mcInfo.setEntryTime(new Timestamp(new Date().getTime()));
			mcInfo.setEntryBy(userDetails.getId());
			obj.put("result", mcInfoService.saveMcInformation(mcInfo));
			obj.put("mcInfoList",mcInfoService.getMcInformations());
			return obj;
		}

		@RequestMapping(value= {"/getMcInfo"},method=RequestMethod.GET)
		public @ResponseBody Map<String, Object> getMcInfo(String id){
			Map<String, Object> obj = new HashMap();
			obj.put("mcInfo",mcInfoService.getMcInformation(Long.valueOf(id)));
			return obj;
		}
		
		// PPPoE & Password
				@RequestMapping(value={"/support/customer-pppoe-id-password"})
				public ModelAndView ppoe_password(ModelMap map,HttpSession session) {
					Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
					ModelAndView view = new ModelAndView("support/pppoe-password");
					map.addAttribute("pppoeList",pppoeService.getPPPoEList());
					//map.addAttribute("resourceList",resourceService.getResourceList());
					return view;
				}
}
