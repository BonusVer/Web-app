package ag.selm.manager.controller;

import ag.selm.manager.client.BadRequestException;
import ag.selm.manager.client.ProductsRestClient;
import ag.selm.manager.controller.payload.NewProductPayload;
import ag.selm.manager.entity.Product;
import lombok.RequiredArgsConstructor;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;

import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@Controller
@RequiredArgsConstructor
@RequestMapping("catalogue/products")
public class ProductsController {

    private final ProductsRestClient productsRestClient;

    @GetMapping(value = "list")
    public String getProductsList(Model model, @RequestParam(name = "filter", required = false) String filter) {

        model.addAttribute("products", this.productsRestClient.findAllProducts(filter));
        model.addAttribute("filter", filter);
        return "catalogue/products/list";
    }

    @GetMapping("create")
    public String getProductPage() {
        return "catalogue/products/new_product";
    }

    @PostMapping("create")
    public String createProduct(NewProductPayload payload, Model model) {
        try {
            Product product = this.productsRestClient.createProduct(payload.title(), payload.details());
            return "redirect:/catalogue/products/%d".formatted(product.id());
        } catch (BadRequestException exception) {
            model.addAttribute("payload", payload);
            model.addAttribute("errors", exception.getErrors());
            return "catalogue/products/new_product";
        }
    }
}
