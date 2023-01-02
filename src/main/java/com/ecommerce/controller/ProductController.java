package com.ecommerce.controller;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

import javax.annotation.security.RolesAllowed;
import javax.validation.Valid;

import com.ecommerce.entity.*;
import com.ecommerce.model.*;
import com.ecommerce.repository.*;
import com.ecommerce.service.UserService;
import org.springframework.aop.scope.ScopedProxyUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin
@RestController
@RequestMapping("/product")
public class ProductController {

	@Autowired
	private ProductRepository productRepository;

	@Autowired
	private ProductSaleRepository productSaleRepository;

	@Autowired
	private CategoryRepository categoryRepository;

	@Autowired
	private ProductSpecificationRepository productSpecificationRepository;

	@Autowired
	private UserService userService;

	@PostMapping
	@RolesAllowed({"ROLE_USER"})
	public ResponseEntity<?> createProduct(@RequestBody @Valid CreateProduct createProduct) {

		User user;
		try {
			user = userService.getUserByAuthentication();
		}catch (Exception e) {
			return new ResponseEntity<>(e.getMessage(),HttpStatus.OK);
		}

		Product savedProduct;

        Product product = new Product();
        product.setProductName(createProduct.getProductName());
        product.setProductDesc(createProduct.getProductDesc());
        product.setProductPrice(createProduct.getProductPrice());
		product.setProductImage(createProduct.getProductImage());

		Category category;

		try{
			category = categoryRepository.findByCategoryName(createProduct.getCategory());
		}catch (Exception e){
			System.out.println(e);
			return new ResponseEntity<>("Category not found in database",HttpStatus.BAD_REQUEST);
		}

		product.setCategory(category);
		product.setProductDiscount(createProduct.getProductDiscount());
		product.setProductEnabled(Boolean.TRUE);

		ProductSale productSale = new ProductSale();
		productSale.setProduct(product);
		productSale.setProductCreateDate(LocalDate.now());
		productSale.setUser(user);

		try {
			savedProduct = productRepository.save(product);
			productSaleRepository.save(productSale);
		}catch (Exception e){
			System.out.println(e);
			return new ResponseEntity<>("Product creation failed",HttpStatus.BAD_REQUEST);
		}

		for (int i = 0; i < createProduct.getProductSpecificationModels().size() ; i++) {
			ProductSpecification productSpecification = new ProductSpecification(); //  Java optimizes memory usage for short-lived objects, create object inside loop is better than outside
			productSpecification.setProduct(savedProduct);
			productSpecification.setSpecification(createProduct.getProductSpecificationModels().get(i).getSpecification());
			productSpecification.setProductQuantity(createProduct.getProductSpecificationModels().get(i).getQuantity());

			try{
				productSpecificationRepository.save(productSpecification);
			}catch (Exception e){
				System.out.println(e);
				return new ResponseEntity<>("Product specification creation failed",HttpStatus.BAD_REQUEST);
			}
		}
		return new ResponseEntity<>("Product created", HttpStatus.OK);
	}

	@GetMapping
	public ArrayList<ProductDetail> getAllProducts() {
		return productRepository.findAllEnabledProduct();
	}

	@GetMapping("/{id}")
	public ProductDetail getProductById(@PathVariable Integer id) {
		return productRepository.findEnabledProductById(id).get();
	}

	@GetMapping("/order/{id}")
	@RolesAllowed({"ROLE_USER"})
	public ProductDetail getProductByOrderId(@PathVariable Integer id) {
		return productRepository.findEnabledProductByOrderId(id).get();
	}

	@GetMapping("/options/{id}")
	public List<Option> getOptionsById(@PathVariable Integer id) {
		return productRepository.findOptionsById(id);
	}

	@PutMapping("/{productId}")
	@RolesAllowed({"ROLE_USER","ROLE_ADMIN"})
	public ResponseEntity<?> updateProduct(@PathVariable Integer productId,@RequestBody UpdatedProduct updatedProduct){

		if(productId < 0){
			return new ResponseEntity<>("error productId",HttpStatus.BAD_REQUEST);
		}
		User user;
		try {
			user = userService.getUserByAuthentication();
		}catch (Exception e) {
			return new ResponseEntity<>(e.getMessage(),HttpStatus.OK);
		}

		ProductSale productSale = productSaleRepository.findByProductId(productId);
		if (productSale.getUser().getUsername().equals(user.getUsername()) || user.getAuthorities().contains("ROLE_ADMIN")) {
			System.out.println("Checked is seller or Admin");
			Optional<Product> product = productRepository.findById(productId);
			if(product.isPresent()) {
				System.out.println("Debug Product Controller - toString value get : " + updatedProduct.toString());
				System.out.println("Product is found");
				Product savedProduct = product.get();
				savedProduct.setProductName(updatedProduct.getProductName());
				savedProduct.setProductPrice(updatedProduct.getProductPrice());
				savedProduct.setProductDesc(updatedProduct.getProductDesc());
				savedProduct.setProductImage(updatedProduct.getProductImage());
				savedProduct.setProductDiscount(updatedProduct.getProductDiscount());
				System.out.println("Debug Product Controller - line 147: updatedProduct.getProductEnabled is " + updatedProduct.getProductEnabled());
				if(updatedProduct.getProductEnabled().equals("true")){
					savedProduct.setProductEnabled(true);
				}else{
					savedProduct.setProductEnabled(false);
				}
				try {
					System.out.println("search for matched category");
					savedProduct.setCategory(categoryRepository.findByCategoryName(updatedProduct.getCategory()));
				}catch (Exception e){
					System.out.println(e);
					return new ResponseEntity<>("Category not found in database",HttpStatus.BAD_REQUEST);
				}

				List<ProductSpecification> productSpecificationList;

				try {
					System.out.println("collect the current specification(options) from database");
					productSpecificationList = productSpecificationRepository.findProductSpecificationByProductId(productId);
				}catch (Exception e){
					System.out.println(e);
					return new ResponseEntity<>("Database error, product specification searching has error",HttpStatus.BAD_REQUEST);
				}

				for(int i = 0; i < updatedProduct.getProductSpecificationModels().size(); i++){
					System.out.println("Size of update product Specification :"+updatedProduct.getProductSpecificationModels().size());
					System.out.println("Updated product specification loop time:" + i );
					for (int j = 0; j < productSpecificationList.size() ; j++){
						System.out.println("Size of existing product Specification :"+productSpecificationList.size());
						System.out.println("existing product specification loop time :" + j);
						if(productSpecificationList.get(j).getSpecification().equals(updatedProduct.getProductSpecificationModels().get(i).getSpecification())){
							System.out.println("find matched product specification with updated product specification at index i :" + i + ", j :" + j);
							//if updated specification exist in searched specification. Update it
							productSpecificationList.get(j).setProductQuantity(updatedProduct.getProductSpecificationModels().get(i).getQuantity());
							System.out.println("after match update the quantity");
						}
					}
					//if updated specification not exist in searched specification, Add it
					ProductSpecification addedProductSpecification = new ProductSpecification();
					addedProductSpecification.setProduct(productSpecificationList.get(0).getProduct());
					addedProductSpecification.setSpecification(updatedProduct.getProductSpecificationModels().get(i).getSpecification());
					addedProductSpecification.setProductQuantity(updatedProduct.getProductSpecificationModels().get(i).getQuantity());

					productSpecificationList.add(addedProductSpecification);
				}

				ArrayList<ProductSpecification> updatedProductSpecificationEntityList = new ArrayList<>();

				for(int i = 0 ; i < updatedProduct.getProductSpecificationModels().size() ; i++) {
					updatedProductSpecificationEntityList.add(new ProductSpecification(productSpecificationList.get(0).getProduct(),updatedProduct.getProductSpecificationModels().get(i).getQuantity(),updatedProduct.getProductSpecificationModels().get(i).getSpecification()));
				}

				//if updated specification not exist,but searched specification exist, Delete it
				Set<String> existedOptions =
						updatedProductSpecificationEntityList.stream()
								.map(ProductSpecification::getSpecification)
								.collect(Collectors.toSet());

				List<ProductSpecification> finalProductSpecification =  productSpecificationList.stream()
						.filter(c -> !existedOptions.contains(c.getSpecification()))
						.collect(Collectors.toList());

				try {
					productRepository.save(savedProduct);
					productSpecificationRepository.deleteAll(finalProductSpecification);
					for( int i = 0 ; i < updatedProduct.getProductSpecificationModels().size() ; i++ ){
						ProductSpecification productSpecification = new ProductSpecification();
						productSpecification.setSpecification(updatedProduct.getProductSpecificationModels().get(i).getSpecification());
						productSpecification.setProduct(savedProduct);
						productSpecification.setProductQuantity(updatedProduct.getProductSpecificationModels().get(i).getQuantity());

						//after update and delete, the options still will remain the option that already updated
						//if simply use save() in JPA will create a repeat column which already been updated at previous
						List<ProductSpecification> specificationAfterUpdate = productSpecificationRepository.findProductSpecificationByProductId(savedProduct.getId());
						ArrayList<String> arrayListOfSpecification = new ArrayList<>();
						for (ProductSpecification specification : specificationAfterUpdate){
							arrayListOfSpecification.add(specification.getSpecification());
						}
						if(!arrayListOfSpecification.contains(productSpecification.getSpecification())){
							productSpecificationRepository.save(productSpecification);
						}
					}
				}catch (Exception e){
					return new ResponseEntity<>("Product update failed",HttpStatus.BAD_REQUEST);
				}
			}else{
				return new ResponseEntity<>("Product not found",HttpStatus.BAD_REQUEST);
			}
		}else{
			return new ResponseEntity<>("Current username is not allowed to make update to this product",HttpStatus.UNAUTHORIZED);
		}
		return new ResponseEntity<>("Product updated successfully",HttpStatus.OK);
	}

	@GetMapping("/seller")
	@RolesAllowed({"ROLE_USER"})
	public ResponseEntity<?> getAllSellerProductBySellerId(){
		User user;
		try {
			user = userService.getUserByAuthentication();
		}catch (Exception e) {
			return new ResponseEntity<>(e.getMessage(),HttpStatus.OK);
		}
		List<SellerAndAdminProductListModel> productListOfSeller = productRepository.findAllProductBySeller(user.getUsername());
		return new ResponseEntity<>(productListOfSeller,HttpStatus.OK);
	}

	@CrossOrigin
	@GetMapping("/seller/{productId}")
	@RolesAllowed({"ROLE_USER","ROLE_USER"})
	public ResponseEntity<?> getSellerProductByProductId(@PathVariable Integer productId){
		User user;
		try {
			user = userService.getUserByAuthentication();
		}catch (Exception e) {
			return new ResponseEntity<>(e.getMessage(),HttpStatus.OK);
		}
		SellerProductDetail productListOfSeller = productRepository.findSellerEditInfoByProductId(productId);
		return new ResponseEntity<>(productListOfSeller,HttpStatus.OK);
	}

	@DeleteMapping("/{productId}")
	@RolesAllowed({"ROLE_USER","ROLE_ADMIN"})
	public ResponseEntity<?> deleteProduct( @PathVariable Integer productId){

		User user;
		try {
			user = userService.getUserByAuthentication();
		}catch (Exception e) {
			return new ResponseEntity<>(e.getMessage(),HttpStatus.OK);
		}

		Product product;
		ProductSale productSale;
		try {
			product = productRepository.findById(productId).get();
			productSale = productSaleRepository.findByProductId(product.getId());
		}catch (Exception e){
			System.out.println(e);
			return new ResponseEntity<>("Database Error, cannot search product",HttpStatus.BAD_REQUEST);
		}

		if (!(productSale.getUser().getUsername().equals(user.getUsername()) || user.getAuthorities().contains("ROLE_ADMIN"))) {
			return new ResponseEntity<>("Current username is not allowed to make update to this product",HttpStatus.UNAUTHORIZED);
		}

		if(product != null) {
			try{
				productRepository.delete(product);
			}catch (Exception e){
				System.out.println(e);
				return new ResponseEntity<>("Product delete failed", HttpStatus.BAD_REQUEST);
			}
			return new ResponseEntity<>("Product deleted successfully",HttpStatus.OK);
		}
		return new ResponseEntity<>("Product not found",HttpStatus.BAD_REQUEST);
	}
}
