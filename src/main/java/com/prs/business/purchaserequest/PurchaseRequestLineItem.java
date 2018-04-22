package com.prs.business.purchaserequest;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import com.prs.business.product.Product;

@Entity
public class PurchaseRequestLineItem {
	
	@Id
	@GeneratedValue (strategy = GenerationType.IDENTITY)
	private int id;
	@ManyToOne
	@JoinColumn(name="PurchaseRequestId")
	private PurchaseRequest purchaseRequest;
	@ManyToOne
	@JoinColumn(name = "ProductID")
	private Product product;
	private int quantity;
	
	public PurchaseRequestLineItem() {
	}
	
	public PurchaseRequestLineItem(int iD, PurchaseRequest purchaseRequest, Product product, int quantity) {
		super();
		id = iD;
		this.purchaseRequest = purchaseRequest;
		this.product = product;
		this.quantity = quantity;
	}
	
	public PurchaseRequestLineItem(PurchaseRequest purchaseRequest, Product product, int quantity) {
		super();
		this.purchaseRequest = purchaseRequest;
		this.product = product;
		this.quantity = quantity;
	}
	
	public int getID() {
		return id;
	}
	
	public void setID(int iD) {
		id = iD;
	}
	
	public PurchaseRequest getPurchaseRequest() {
		return purchaseRequest;
	}
	
	public void setPurchaseRequest(PurchaseRequest purchaseRequest) {
		this.purchaseRequest = purchaseRequest;
	}
	
	public Product getProduct() {
		return product;
	}
	
	public void setProduct(Product product) {
		this.product = product;
	}
	
	public int getQuantity() {
		return quantity;
	}
	
	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}
	
	@Override
	public String toString() {
		return "PurchaseRequestLineItem [ID=" + id + ", purchaseRequest=" + purchaseRequest + ", product=" + product
				+ ", quantity=" + quantity + "]";
	}
	
	
	
}
