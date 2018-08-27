package ua.artem.sbshoppingcart.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ua.artem.sbshoppingcart.dao.OrderDAO;
import ua.artem.sbshoppingcart.dao.ProductDAO;
import ua.artem.sbshoppingcart.entity.Product;
import ua.artem.sbshoppingcart.message.Response;
import ua.artem.sbshoppingcart.model.TopProduct;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Artem on 24.08.2018
 */
@RestController
@RequestMapping("/top/hot")
public class HotProductController {
    @Autowired
    private OrderDAO orderDAO;

    @Autowired
    private ProductDAO productDAO;


    List<TopProduct> prod = new ArrayList<>();

    @GetMapping(value = "/result")
    public Response getResult() {
        Product product = productDAO.hotProduct();
        TopProduct topProduct = new TopProduct(product.getCode(), product.getName(), product.getPrice());
        prod.add(topProduct);
        System.out.println(topProduct.getCode());
        Response response = new Response("Done", prod);
        return response;
    }

}
