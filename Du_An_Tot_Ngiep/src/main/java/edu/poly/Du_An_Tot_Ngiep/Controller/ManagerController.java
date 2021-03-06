package edu.poly.Du_An_Tot_Ngiep.Controller;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.support.PagedListHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.support.ByteArrayMultipartFileEditor;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import edu.poly.Du_An_Tot_Ngiep.Entity.Category;
import edu.poly.Du_An_Tot_Ngiep.Entity.FeedBack;
import edu.poly.Du_An_Tot_Ngiep.Entity.Invoice;
import edu.poly.Du_An_Tot_Ngiep.Entity.InvoiceDetail;
import edu.poly.Du_An_Tot_Ngiep.Entity.Product;
import edu.poly.Du_An_Tot_Ngiep.Entity.User;
import edu.poly.Du_An_Tot_Ngiep.Service.CategoryService;
import edu.poly.Du_An_Tot_Ngiep.Service.FeedBackService;
import edu.poly.Du_An_Tot_Ngiep.Service.OrderDetailsService;
import edu.poly.Du_An_Tot_Ngiep.Service.OrdersService;
import edu.poly.Du_An_Tot_Ngiep.Service.ProductService;
import edu.poly.Du_An_Tot_Ngiep.Service.StatisticalService;
import edu.poly.Du_An_Tot_Ngiep.Service.UserService;

@Controller
public class ManagerController {

	@Autowired
	private CategoryService categoryService;

	@Autowired
	private ProductService productService;

	@Autowired
	private UserService userService;

	@Autowired
	private FeedBackService feedBackService;

	@Autowired
	OrdersService oders;

	@Autowired
	OrderDetailsService orderDetailsService;

	@Autowired
	StatisticalService statisticalService;

	void getName(HttpServletRequest request, ModelMap model) {
		Cookie[] cookies = request.getCookies();
		for (int i = 0; i < cookies.length; ++i) {
			if (cookies[i].getName().equals("accountuser")) {
				User user = this.userService.findByPhone(cookies[i].getValue()).get();
				model.addAttribute("fullname", user.getFullname());
				model.addAttribute("image", user.getImageBase64());
				break;
			}
		}
	}

	@GetMapping(value = "/manager")
	public String manager(ModelMap model, @CookieValue(value = "accountuser", required = false) String username,
			MultipartFile image, HttpServletRequest request, HttpServletResponse response) {
		Cookie[] cookies = request.getCookies();
		if (cookies != null) {
			for (int i = 0; i < cookies.length; ++i) {
				if (cookies[i].getName().equals("accountuser")) {
					User user = this.userService.findByPhone(cookies[i].getValue()).get();

					model.addAttribute("username", username);
					model.addAttribute("fullname", user.getFullname());
					model.addAttribute("image", user.getImageBase64());

					return "redirect:/manager/listCategory";
				}
			}

		}
		return "redirect:/login";

	}

	@GetMapping(value = "/manager/listCategory")
	public String listCategory(Model model, @CookieValue(value = "accountuser", required = false) String username,
			HttpServletRequest request, HttpServletResponse response, RedirectAttributes redirect) {

		Cookie[] cookies = request.getCookies();
		if (cookies != null) {
			for (int i = 0; i < cookies.length; ++i) {
				if (cookies[i].getName().equals("accountuser")) {
					User user = this.userService.findByPhone(cookies[i].getValue()).get();
					if (model.asMap().get("success") != null)
						redirect.addFlashAttribute("success", model.asMap().get("success").toString());

					List<Category> list = categoryService.listCategory();
					model.addAttribute("category", list);
					model.addAttribute("username", username);
					model.addAttribute("fullname", user.getFullname());
					model.addAttribute("image", user.getImageBase64());
					return "/manager/category/listCategory";
				}

			}
		}
		return "redirect:/login";

	}

	@GetMapping(value = "/manager/addCategory")
	public String addCategory(ModelMap model, @CookieValue(value = "accountuser", required = false) String username,
			HttpServletRequest request) {

		Cookie[] cookies = request.getCookies();
		if (cookies != null) {
			for (int i = 0; i < cookies.length; ++i) {
				if (cookies[i].getName().equals("accountuser")) {
					this.userService.findByPhone(cookies[i].getValue()).get();
					model.addAttribute("category", new Category());
					getName(request, model);
					return "/manager/category/addCategory";
				}
			}
		}
		return "redirect:/login";
	}

	@PostMapping(value = "/manager/addCategory")
	public String addCategory(@ModelAttribute(value = "category") @Valid Category category,
			RedirectAttributes redirect) {

		this.categoryService.save(category);
		redirect.addFlashAttribute("success", "Thêm mới danh mục thành công!");

		return "redirect:/manager/listCategory";
	}

	@GetMapping(value = "/manager/updateCategory/{idCategory}")
	public String updateCategory(ModelMap model, @PathVariable(name = "idCategory") int idCategory,
			@CookieValue(value = "accountuser", required = false) String username, HttpServletRequest request) {
		Cookie[] cookies = request.getCookies();
		if (cookies != null) {
			for (int i = 0; i < cookies.length; ++i) {
				if (cookies[i].getName().equals("accountuser")) {
					this.userService.findByPhone(cookies[i].getValue()).get();
					getName(request, model);
					model.addAttribute("category", categoryService.findById(idCategory));
					return "/manager/category/updateCategory";
				}
			}
		}
		return "redirect:/login";

	}

	@PostMapping(value = "/manager/updateCategory")
	public String updateCategory(@ModelAttribute(value = "category") @Valid Category category,
			@RequestParam("idCategory") int idCategory, RedirectAttributes redirect) {
		this.categoryService.save(category);
		redirect.addFlashAttribute("success", "Cập nhập danh mục thành công!");
		return "redirect:/manager/listCategory";
	}

	@GetMapping(value = "/manager/deleteCategory/{idCategory}")
	public String deleteCategory(@PathVariable(name = "idCategory") int idCategory,
			@CookieValue(value = "accountuser", required = false) String username, HttpServletRequest request,
			RedirectAttributes redirect) {
		Cookie[] cookies = request.getCookies();
		if (cookies != null) {
			for (int i = 0; i < cookies.length; ++i) {
				if (cookies[i].getName().equals("accountuser")) {
					this.userService.findByPhone(cookies[i].getValue()).get();

					this.categoryService.deleteById(idCategory);
					redirect.addFlashAttribute("success", "Xóa danh mục thành công!");
					return "redirect:/manager/listCategory";
				}

			}
		}
		return "redirect:/login";
	}

	// table product
	@GetMapping(value = "/manager/listProduct")
	public String listProduct(Model model, HttpServletRequest request, RedirectAttributes redirect) {

		request.getSession().setAttribute("product", null);
		if (model.asMap().get("success") != null)
			redirect.addFlashAttribute("success", model.asMap().get("success").toString());
		return "redirect:/listProduct/page/1";
	}

	@GetMapping(value = "/listProduct/page/{pageNumber}")
	public String showProduct(@CookieValue(value = "accountuser") String username, HttpServletRequest request,
			HttpServletResponse response, @PathVariable int pageNumber, Model model) {

		Cookie[] cookies = request.getCookies();
		if (cookies != null) {
			for (int i = 0; i < cookies.length; ++i) {
				if (cookies[i].getName().equals("accountuser")) {
					User user = this.userService.findByPhone(cookies[i].getValue()).get();

					PagedListHolder<?> pages = (PagedListHolder<?>) request.getSession().getAttribute("product");
					int pagesize = 5;
					List<Product> list = productService.listProduct();
					if (pages == null) {
						pages = new PagedListHolder<>(list);
						pages.setPageSize(pagesize);
					} else {
						final int goToPage = pageNumber - 1;
						if (goToPage <= pages.getPageCount() && goToPage >= 0) {
							pages.setPage(goToPage);
						}
					}

					request.getSession().setAttribute("product", pages);
					int current = pages.getPage() + 1;
					int begin = Math.max(1, current - list.size());
					int end = Math.min(begin + 5, pages.getPageCount());
					int totalPageCount = pages.getPageCount();
					String baseUrl = "/listProduct/page/";

					model.addAttribute("beginIndex", begin);
					model.addAttribute("endIndex", end);
					model.addAttribute("currentIndex", current);
					model.addAttribute("totalPageCount", totalPageCount);
					model.addAttribute("baseUrl", baseUrl);
					model.addAttribute("product", pages);
					model.addAttribute("username", username);
					model.addAttribute("fullname", user.getFullname());
					model.addAttribute("image", user.getImageBase64());

					return "/manager/product/listProduct";
				}

			}
		}
		return "redirect:/login";
	}

	@GetMapping(value = "/manager/addProduct")
	public String addProduct(ModelMap model, @CookieValue(value = "accountuser", required = false) String username,
			HttpServletRequest request) {
		Cookie[] cookies = request.getCookies();
		if (cookies != null) {
			for (int i = 0; i < cookies.length; ++i) {
				if (cookies[i].getName().equals("accountuser")) {
					this.userService.findByPhone(cookies[i].getValue()).get();
					getName(request, model);
					model.addAttribute("product", new Product());
					model.addAttribute("listCategory", categoryService.findAll());
					return "/manager/product/addProduct";
				}

			}
		}
		return "redirect:/login";
	}

	@PostMapping(value = "/manager/addProduct")
	public String addProduct(@RequestParam(value = "image") MultipartFile image,
			@ModelAttribute(name = "product") @Valid Product product, BindingResult result,
			RedirectAttributes redirect) {
		if (result.hasErrors()) {
			return "/manager/addProduct";
		} else {
			this.productService.save(product);
			redirect.addFlashAttribute("success", "Thêm mới thông tin sản phẩm thành công!");
		}
		return "redirect:/manager/listProduct";
	}

	@InitBinder
	protected void initBinder(HttpServletRequest request, ServletRequestDataBinder binder) throws ServletException {
		binder.registerCustomEditor(byte[].class, new ByteArrayMultipartFileEditor());
	}

	@GetMapping(value = "/manager/updateProduct/{idProduct}")
	public String updateProduct(ModelMap model, @PathVariable(name = "idProduct") int id,
			@CookieValue(value = "accountuser", required = false) String username, HttpServletRequest request) {
		Cookie[] cookies = request.getCookies();
		if (cookies != null) {
			for (int i = 0; i < cookies.length; ++i) {
				if (cookies[i].getName().equals("accountuser")) {
					this.userService.findByPhone(cookies[i].getValue()).get();

					model.addAttribute("listCategory", this.categoryService.findAll());
					model.addAttribute("product",
							this.productService.findById(id).isPresent() ? this.productService.findById(id).get()
									: null);
					getName(request, model);
					return "/manager/product/updateProduct";
				}

			}
		}
		return "redirect:/login";
	}

	@PostMapping(value = "/manager/updateProduct")
	public String updateProduct(@RequestParam(value = "image") MultipartFile image,
			@ModelAttribute(name = "product") @Valid Product product, BindingResult result,
			RedirectAttributes redirect) {
		if (result.hasErrors()) {
			return "/manager/updateProduct";
		} else {
			this.productService.save(product);
			redirect.addFlashAttribute("success", "Cập nhập thông tin sản phẩm thành công!");
		}

		if (!image.isEmpty()) {
			try {
				product.setImage(image.getBytes());
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			product.setImage(productService.findById(product.getIdProduct()).get().getImage());

		}

		return "redirect:/manager/listProduct";
	}

	@GetMapping(value = "/manager/deleteProduct/{idProduct}")
	public String deleteProduct(@PathVariable(name = "idProduct") int id,
			@CookieValue(value = "accountuser", required = false) String username, HttpServletRequest request,
			RedirectAttributes redirect) {

		Cookie[] cookies = request.getCookies();
		if (cookies != null) {
			for (int i = 0; i < cookies.length; ++i) {
				if (cookies[i].getName().equals("accountuser")) {
					User user = this.userService.findByPhone(cookies[i].getValue()).get();

					this.productService.deleteById(id);
					redirect.addFlashAttribute("success", "Xóa sản phẩm thành công!");
					return "redirect:/manager/listProduct";
				}

			}
		}
		return "redirect:/login";
	}

	// feedback
	@GetMapping(value = "/manager/feedback")
	public String listFeedBack(ModelMap model, @CookieValue(value = "accountuser") String username,
			HttpServletRequest request, HttpServletResponse response) {
		Cookie[] cookies = request.getCookies();
		if (cookies != null) {
			for (int i = 0; i < cookies.length; ++i) {
				if (cookies[i].getName().equals("accountuser")) {
					User user = this.userService.findByPhone(cookies[i].getValue()).get();

					model.addAttribute("username", username);
					model.addAttribute("fullname", user.getFullname());
					model.addAttribute("image", user.getImageBase64());

					this.feedBackService.findAll();
					return "/manager/feedback/feedback";
				}

			}
		}
		return "redirect:/login";
	}

	@PostMapping(value = "index/contact")
	public String addFeedBack(@ModelAttribute(name = "feedback") @Valid FeedBack feedBack, BindingResult result) {
		if (result.hasErrors()) {
			return "shop/contact";
		}
		this.feedBackService.save(feedBack);
		return "shop/contact";
	}

	// product Detail
	@GetMapping("/manager/order")
	public String listOrder(ModelMap model, @CookieValue(value = "accountuser", required = false) String username,
			HttpServletRequest request, HttpServletResponse response) {
		Cookie[] cookies = request.getCookies();
		if (cookies != null) {
			for (int i = 0; i < cookies.length; ++i) {
				if (cookies[i].getName().equals("accountuser")) {
					User user = this.userService.findByPhone(cookies[i].getValue()).get();

					model.addAttribute("username", username);
					model.addAttribute("fullname", user.getFullname());
					model.addAttribute("image", user.getImageBase64());
					List<Invoice> list = this.oders.listInvoice();
					model.addAttribute("listOrder", list);
					return "manager/order/order";
				}
			}
		}
		return "redirect:/login";
	}

	@GetMapping(value = "/manager/orderDetail/{id}")
	public String viewOrderdetailsForManager(@PathVariable("id") int id, ModelMap model,
			@CookieValue(value = "accountuser", required = false) String username, HttpServletRequest request,
			HttpServletResponse response) {

		Cookie[] cookies = request.getCookies();
		if (cookies != null) {
			for (int j = 0; j < cookies.length; ++j) {
				if (cookies[j].getName().equals("accountuser")) {
					User user = this.userService.findByPhone(cookies[j].getValue()).get();

					model.addAttribute("username", username);
					model.addAttribute("fullname", user.getFullname());
					model.addAttribute("image", user.getImageBase64());

					List<InvoiceDetail> list = this.orderDetailsService.findDetailByInvoiceId(id);
					List<Product> productorder = new ArrayList<>();
					for (int i = 0; i < list.size(); i++) {
						Product odrProduct = productService.findByIdProduct(list.get(i).getProduct().getIdProduct());
						odrProduct.setAmount(list.get(i).getAmount());
						productorder.add(odrProduct);
					}
					model.addAttribute("listOrderDetail", productorder);
					return "manager/order/orderDetail";
				}
			}
		}
		return "redirect:/login";
	}

}
