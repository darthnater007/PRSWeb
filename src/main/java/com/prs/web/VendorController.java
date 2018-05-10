package com.prs.web;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.prs.business.vendor.Vendor;
import com.prs.business.vendor.VendorRepository;
import com.prs.util.PRSMaintenanceReturn;

@CrossOrigin
@Controller    
@RequestMapping(path="/Vendors")
public class VendorController extends BaseController{
	@Autowired 
	private VendorRepository vendorRepository;

	@GetMapping(path="/List")
	public @ResponseBody Iterable<Vendor> getAllVendors() {
		// This returns a JSON or XML with the users
		return vendorRepository.findAll();
	}
	
	@GetMapping(path = "/Get")
	public @ResponseBody List<Vendor> getVendor(@RequestParam int id) {
		Optional<Vendor> u = vendorRepository.findById(id);
		return getReturnArray(u);
	}
	
	@PostMapping(path="/Add") 
	public @ResponseBody PRSMaintenanceReturn addNewVendor (@RequestBody Vendor vendor) {
		try {
			vendorRepository.save(vendor);
			return PRSMaintenanceReturn.getMaintReturn(vendor);
		}
		catch (DataIntegrityViolationException dive) {
			return PRSMaintenanceReturn.getMaintReturnError(vendor, dive.getRootCause().toString());
		}
		catch (Exception e) {
			e.printStackTrace();
			return PRSMaintenanceReturn.getMaintReturnError(vendor, e.getMessage());
		}
	}
	
	@GetMapping(path="/Remove") // Map ONLY GET Requests
	public @ResponseBody PRSMaintenanceReturn deleteUser (@RequestParam int id) {
		// @ResponseBody means the returned String is the response, not a view name
		// @RequestParam means it is a parameter from the GET or POST request
		
		Optional<Vendor> vendor = vendorRepository.findById(id);
		try {
			vendorRepository.delete(vendor.get());
			return PRSMaintenanceReturn.getMaintReturn(vendor.get());
		}
		catch (DataIntegrityViolationException dive) {
			return PRSMaintenanceReturn.getMaintReturnError(vendor, dive.getRootCause().toString());
		}
		catch (Exception e) {
			return PRSMaintenanceReturn.getMaintReturnError(vendor, e.toString());
		}
		
	}

	@PostMapping(path="/Change") 
	public @ResponseBody PRSMaintenanceReturn updateVendor (@RequestBody Vendor vendor) {
		try {
			vendorRepository.save(vendor);
			return PRSMaintenanceReturn.getMaintReturn(vendor);
		}
		catch (DataIntegrityViolationException dive) {
			return PRSMaintenanceReturn.getMaintReturnError(vendor, dive.getRootCause().toString());
		}
		catch (Exception e) {
			return PRSMaintenanceReturn.getMaintReturnError(vendor, e.toString());
		}
		
	}
	
}