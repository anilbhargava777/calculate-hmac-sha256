package net.bzium.shopify.pojo;

public class Cart {

	private String id;
	private CartItem[] line_items;
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public CartItem[] getLine_items() {
		return line_items;
	}
	public void setLine_items(CartItem[] line_items) {
		this.line_items = line_items;
	}
}
