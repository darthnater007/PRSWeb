package com.prs.web;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.prs.business.purchaserequest.PurchaseRequest;
import com.prs.business.purchaserequest.PurchaseRequestLineItem;
import com.prs.business.purchaserequest.PurchaseRequestLineItemRepository;
import com.prs.business.purchaserequest.PurchaseRequestRepository;
import com.prs.util.PRSMaintenanceReturn;

@Controller    
@RequestMapping(path="/PurchaseRequestLineItems")
public class PurchaseRequestLineItemController extends BaseController{
	@Autowired 
	private PurchaseRequestLineItemRepository purchaseRequestLineItemRepository;
	@Autowired
	private PurchaseRequestRepository prRepository;

	@GetMapping(path="/List")
	public @ResponseBody Iterable<PurchaseRequestLineItem> getAllPurchaseRequestLineItems() {
		// This returns a JSON or XML with the users
		return purchaseRequestLineItemRepository.findAll();
	}
	
	@GetMapping(path = "/Get")
	public @ResponseBody List<PurchaseRequestLineItem> getVendor(@RequestParam int id) {
		Optional<PurchaseRequestLineItem> purchaseRequestLineItem = purchaseRequestLineItemRepository.findById(id);
		return getReturnArray(purchaseRequestLineItem);
	}
	
	@GetMapping(path="/LinesForPR")
	public @ResponseBody Iterable<PurchaseRequestLineItem> getAllLineItemsForPR(@RequestParam int id) {
		// This returns a JSON or XML with the users
		return purchaseRequestLineItemRepository.findAllByPurchaseRequestId(id);
	}
	
	@PostMapping(path="/Add") 
	public @ResponseBody PRSMaintenanceReturn addNewPurchaseRequestLineItem (@RequestBody PurchaseRequestLineItem purchaseRequestLineItem) {
		try {
			purchaseRequestLineItemRepository.save(purchaseRequestLineItem);
			updatePRTotal(purchaseRequestLineItem);
			return PRSMaintenanceReturn.getMaintReturn(purchaseRequestLineItem);
		}
		catch (DataIntegrityViolationException dive) {
			return PRSMaintenanceReturn.getMaintReturnError(purchaseRequestLineItem, dive.getRootCause().toString());
		}
		catch (Exception e) {
			e.printStackTrace();
			return PRSMaintenanceReturn.getMaintReturnError(purchaseRequestLineItem, e.getMessage());
		}
	}
	
	@GetMapping(path="/Remove") // Map ONLY GET Requests
	public @ResponseBody PRSMaintenanceReturn deletePurchaseRequestLineItem (@RequestParam int id) {
		// @ResponseBody means the returned String is the response, not a view name
		// @RequestParam means it is a parameter from the GET or POST request
		
		Optional<PurchaseRequestLineItem> purchaseRequestLineItem = purchaseRequestLineItemRepository.findById(id);
		try {
			purchaseRequestLineItemRepository.delete(purchaseRequestLineItem.get());
			updatePRTotal(purchaseRequestLineItem.get());
			return PRSMaintenanceReturn.getMaintReturn(purchaseRequestLineItem.get());
		}
		catch (DataIntegrityViolationException dive) {
			return PRSMaintenanceReturn.getMaintReturnError(purchaseRequestLineItem, dive.getRootCause().toString());
		}
		catch (Exception e) {
			return PRSMaintenanceReturn.getMaintReturnError(purchaseRequestLineItem, e.toString());
		}
		
	}

	@PostMapping(path="/Change") 
	public @ResponseBody PRSMaintenanceReturn updatePurchaseRequest (@RequestBody PurchaseRequestLineItem purchaseRequestLineItem) {
		try {
			purchaseRequestLineItemRepository.save(purchaseRequestLineItem);
			updatePRTotal(purchaseRequestLineItem);
			return PRSMaintenanceReturn.getMaintReturn(purchaseRequestLineItem);
		}
		catch (DataIntegrityViolationException dive) {
			return PRSMaintenanceReturn.getMaintReturnError(purchaseRequestLineItem, dive.getRootCause().toString());
		}
		catch (Exception e) {
			return PRSMaintenanceReturn.getMaintReturnError(purchaseRequestLineItem, e.toString());
		}
		
	}
	
	public void updatePRTotal(PurchaseRequestLineItem purchaseRequestLineItem) {
		Optional<PurchaseRequest> prOpt = prRepository.findById(purchaseRequestLineItem.getPurchaseRequest().getId());
		PurchaseRequest pr = prOpt.get();
		List<PurchaseRequestLineItem> prlis = new ArrayList<>();
		prlis = purchaseRequestLineItemRepository.findAllByPurchaseRequestId(pr.getId());
		double total = 0;
		for (PurchaseRequestLineItem prli : prlis) {
			total += prli.getProduct().getPrice()*prli.getQuantity();
		}
		pr.setTotal(total);
		prRepository.save(pr);
	}
	
}