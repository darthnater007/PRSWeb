package com.prs.web;

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

import com.prs.business.product.Product;
import com.prs.business.product.ProductRepository;
import com.prs.util.PRSMaintenanceReturn;

@Controller    
@RequestMapping(path="/Products")
public class ProductController extends BaseController{
	@Autowired 
	private ProductRepository productRepository;

	@GetMapping(path="/List")
	public @ResponseBody Iterable<Product> getAllProducts() {
		// This returns a JSON or XML with the users
		return productRepository.findAll();
	}
	
	@GetMapping(path = "/Get")
	public @ResponseBody List<Product> getProduct(@RequestParam int id) {
		Optional<Product> p = productRepository.findById(id);
		return getReturnArray(p);
	}
	
	@PostMapping(path="/Add") 
	public @ResponseBody PRSMaintenanceReturn addNewProduct (@RequestBody Product product) {
		try {
			productRepository.save(product);
			return PRSMaintenanceReturn.getMaintReturn(product);
		}
		catch (DataIntegrityViolationException dive) {
			return PRSMaintenanceReturn.getMaintReturnError(product, dive.getRootCause().toString());
		}
		catch (Exception e) {
			e.printStackTrace();
			return PRSMaintenanceReturn.getMaintReturnError(product, e.getMessage());
		}
	}
	
	@GetMapping(path="/Remove") // Map ONLY GET Requests
	public @ResponseBody PRSMaintenanceReturn deleteProduct (@RequestParam int id) {
		// @ResponseBody means the returned String is the response, not a view name
		// @RequestParam means it is a parameter from the GET or POST request
		
		Optional<Product> product = productRepository.findById(id);
		try {
			productRepository.delete(product.get());
			return PRSMaintenanceReturn.getMaintReturn(product.get());
		}
		catch (DataIntegrityViolationException dive) {
			return PRSMaintenanceReturn.getMaintReturnError(product, dive.getRootCause().toString());
		}
		catch (Exception e) {
			return PRSMaintenanceReturn.getMaintReturnError(product, e.toString());
		}
		
	}

	@PostMapping(path="/Change") 
	public @ResponseBody PRSMaintenanceReturn updateProduct (@RequestBody Product product) {
		try {
			productRepository.save(product);
			return PRSMaintenanceReturn.getMaintReturn(product);
		}
		catch (DataIntegrityViolationException dive) {
			return PRSMaintenanceReturn.getMaintReturnError(product, dive.getRootCause().toString());
		}
		catch (Exception e) {
			return PRSMaintenanceReturn.getMaintReturnError(product, e.toString());
		}
		
	}
	
}