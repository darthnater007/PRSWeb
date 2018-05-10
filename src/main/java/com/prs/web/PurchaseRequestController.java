package com.prs.web;

import java.sql.Timestamp;
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

import com.prs.business.purchaserequest.PurchaseRequest;
import com.prs.business.purchaserequest.PurchaseRequestRepository;
import com.prs.util.PRSMaintenanceReturn;

@CrossOrigin
@Controller    
@RequestMapping(path="/PurchaseRequests")
public class PurchaseRequestController extends BaseController{
	@Autowired 
	private PurchaseRequestRepository purchaseRequestRepository;

	@GetMapping(path="/List")
	public @ResponseBody Iterable<PurchaseRequest> getAllPurchaseRequests() {
		return purchaseRequestRepository.findAll();
	}

	@GetMapping(path = "/Get")
	public @ResponseBody List<PurchaseRequest> getPurchaseRequest(@RequestParam int id) {
		Optional<PurchaseRequest> pr = purchaseRequestRepository.findById(id);
		return getReturnArray(pr.get());
	}

	@PostMapping(path="/Add") 
	public @ResponseBody int addNewPurchaseRequest (@RequestBody PurchaseRequest purchaserequest) {

		try {
			Timestamp ts = new Timestamp(System.currentTimeMillis());
			purchaserequest.setSubmittedDate(ts);
			purchaserequest.setStatus(PurchaseRequest.STATUS_NEW);
			purchaseRequestRepository.save(purchaserequest);
			PRSMaintenanceReturn.getMaintReturn(purchaserequest);
			return purchaserequest.getId();
		}
		catch (DataIntegrityViolationException dive) {
			PRSMaintenanceReturn.getMaintReturnError(purchaserequest, dive.getRootCause().toString());
			return purchaserequest.getId();
		}
		catch (Exception e) {
			e.printStackTrace();
			PRSMaintenanceReturn.getMaintReturnError(purchaserequest, e.getMessage());
			return purchaserequest.getId();
		}
	}

	@GetMapping(path="/Remove") // Map ONLY GET Requests
	public @ResponseBody PRSMaintenanceReturn deletePurchaseRequest (@RequestParam int id) {
		// @ResponseBody means the returned String is the response, not a view name
		// @RequestParam means it is a parameter from the GET or POST request

		Optional<PurchaseRequest> purchaseRequest = purchaseRequestRepository.findById(id);
		try {
			purchaseRequestRepository.delete(purchaseRequest.get());
			return PRSMaintenanceReturn.getMaintReturn(purchaseRequest.get());
		}
		catch (DataIntegrityViolationException dive) {
			return PRSMaintenanceReturn.getMaintReturnError(purchaseRequest, dive.getRootCause().toString());
		}
		catch (Exception e) {
			return PRSMaintenanceReturn.getMaintReturnError(purchaseRequest, e.toString());
		}
	}

	@PostMapping(path="/Change") 
	public @ResponseBody PRSMaintenanceReturn updatePurchaseRequest (@RequestBody PurchaseRequest purchaseRequest) {
		try {
			purchaseRequestRepository.save(purchaseRequest);
			return PRSMaintenanceReturn.getMaintReturn(purchaseRequest);
		}
		catch (DataIntegrityViolationException dive) {
			return PRSMaintenanceReturn.getMaintReturnError(purchaseRequest, dive.getRootCause().toString());
		}
		catch (Exception e) {
			return PRSMaintenanceReturn.getMaintReturnError(purchaseRequest, e.toString());
		}
	}
	
	@PostMapping(path="/Approve") 
	public @ResponseBody PRSMaintenanceReturn approvePurchaseRequest (@RequestBody PurchaseRequest purchaseRequest) {
		try {
			purchaseRequest.setStatus(PurchaseRequest.STATUS_APPROVED);
			purchaseRequestRepository.save(purchaseRequest);
			return PRSMaintenanceReturn.getMaintReturn(purchaseRequest);
		}
		catch (DataIntegrityViolationException dive) {
			return PRSMaintenanceReturn.getMaintReturnError(purchaseRequest, dive.getRootCause().toString());
		}
		catch (Exception e) {
			return PRSMaintenanceReturn.getMaintReturnError(purchaseRequest, e.toString());
		}
	}
	
	@PostMapping(path="/Reject") 
	public @ResponseBody PRSMaintenanceReturn rejectPurchaseRequest (@RequestBody PurchaseRequest purchaseRequest) {
		try {
			purchaseRequest.setStatus(PurchaseRequest.STATUS_REJECTED);
			purchaseRequestRepository.save(purchaseRequest);
			return PRSMaintenanceReturn.getMaintReturn(purchaseRequest);
		}
		catch (DataIntegrityViolationException dive) {
			return PRSMaintenanceReturn.getMaintReturnError(purchaseRequest, dive.getRootCause().toString());
		}
		catch (Exception e) {
			return PRSMaintenanceReturn.getMaintReturnError(purchaseRequest, e.toString());
		}
	}

	@PostMapping(path = "/Submit")
	public @ResponseBody PRSMaintenanceReturn submitForReview(@RequestBody PurchaseRequest purchaseRequest) {
			Optional<PurchaseRequest> prOpt = purchaseRequestRepository.findById(purchaseRequest.getId());
			Timestamp ts = new Timestamp(System.currentTimeMillis());
			purchaseRequest = prOpt.get();
			if(purchaseRequest.getTotal() < 50.0) {
				purchaseRequest.setStatus(PurchaseRequest.STATUS_APPROVED);
			}else {
				purchaseRequest.setStatus(PurchaseRequest.STATUS_REVIEW);
			}
			purchaseRequest.setSubmittedDate(ts);
		try {
			purchaseRequestRepository.save(purchaseRequest);
			return PRSMaintenanceReturn.getMaintReturn(purchaseRequest);
		}
		catch (DataIntegrityViolationException dive) {
			return PRSMaintenanceReturn.getMaintReturnError(purchaseRequest, dive.getRootCause().toString());
		}
		catch (Exception e) {
			return PRSMaintenanceReturn.getMaintReturnError(purchaseRequest, e.toString());
		}
	}
	
	@GetMapping(path = "/GetRequestReview")
	public @ResponseBody Iterable<PurchaseRequest> getRequestReview(@RequestParam int id, @RequestParam String status){
		return purchaseRequestRepository.findAllByUserIdNotAndStatus(id, status);
	}
	
}