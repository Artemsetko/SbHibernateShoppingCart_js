package ua.artem.sbshoppingcart.dao;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import ua.artem.sbshoppingcart.entity.OrderDetail;
import ua.artem.sbshoppingcart.entity.Product;
import ua.artem.sbshoppingcart.form.ProductForm;
import ua.artem.sbshoppingcart.model.ProductInfo;
import ua.artem.sbshoppingcart.pagination.PaginationResult;

import javax.persistence.NoResultException;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Transactional
@Repository
public class ProductDAO {

    @Autowired
    private SessionFactory sessionFactory;

    public Product findProduct(String code) {
        try {
            String sql = "Select e from " + Product.class.getName() + " e Where e.code =:code ";

            Session session = this.sessionFactory.getCurrentSession();
            Query<Product> query = session.createQuery(sql, Product.class);
            query.setParameter("code", code);
            return (Product) query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    //SELECT PRODUCT_ID, max(TotalItemsOrdered) FROM (SELECT SUM(QUANITY) AS TotalItemsOrdered, PRODUCT_ID FROM mydatabase.order_details GROUP BY PRODUCT_ID) as t;
    public Product hotProduct() {
       /* String sql = "Select new " + TopProduct.class.getName()
                + "(e.product.code, max(e.TotalItems))" + " from " + "(Select sum(t.quanity) AS TotalItems, t.product.code From OrderDetail t Group by t.product.code) AS e";
       */
        // String sql = "Select sum(e.quanity), e.product.code From " + OrderDetail.class.getName() + " e GROUP BY e.product.code";
        String sql = "from " + OrderDetail.class.getName() + " u ";
        Session session = this.sessionFactory.getCurrentSession();
        Query query = session.createQuery(sql);
        List results = query.list();
        Map<String, Integer> map = new HashMap<>();
        for (Object result : results) {
            OrderDetail orderDetail = (OrderDetail) result;
            String code = orderDetail.getProduct().getCode();
            if (map.containsKey(code)) {
                map.put(code, map.get(code) + orderDetail.getQuanity());
            } else {
                map.put(code, orderDetail.getQuanity());
            }
        }
        LinkedHashMap<String, Integer> result = map.entrySet().stream()
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,
                        (oldValue, newValue) -> oldValue, LinkedHashMap::new));
        System.out.println(result);
        Map.Entry<String, Integer> entry = result.entrySet().iterator().next();
        return findProduct(entry.getKey());
    }

    public ProductInfo findProductInfo(String code) {
        Product product = this.findProduct(code);
        if (product == null) {
            return null;
        }
        return new ProductInfo(product.getCode(), product.getName(), product.getPrice());
    }

    /*
     * PROPAGATION_REQUIRES_NEW, в отличие от PROPAGATION_REQUIRED, использует полностью
     * независимую транзакцию для каждой затронутой области транзакции.
     * В этом случае основные физические транзакции различны и, следовательно,
     * могут совершать или откатываться независимо друг от друга,
     * причем внешняя транзакция не зависит от состояния отката внутренней транзакции.
     * */
    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
    public void save(ProductForm productForm) {

        Session session = this.sessionFactory.getCurrentSession();
        String code = productForm.getCode();

        Product product = null;

        boolean isNew = false;
        if (code != null) {
            product = this.findProduct(code);
        }
        if (product == null) {
            isNew = true;
            product = new Product();
            product.setCreateDate(new Date());
        }
        product.setCode(code);
        product.setName(productForm.getName());
        product.setPrice(productForm.getPrice());

        if (productForm.getFileData() != null) {
            byte[] image = null;
            try {
                image = productForm.getFileData().getBytes();
            } catch (IOException e) {
            }
            if (image != null && image.length > 0) {
                product.setImage(image);
            }
        }
        if (isNew) {
            session.persist(product);
        }
        // If error in DB, Exceptions will be thrown out immediately
        session.flush();
    }

    public PaginationResult<ProductInfo> queryProducts(int page, int maxResult, int maxNavigationPage,
                                                       String likeName) {
        String sql = "Select new " + ProductInfo.class.getName() //
                + "(p.code, p.name, p.price) " + " from "//
                + Product.class.getName() + " p ";
        if (likeName != null && likeName.length() > 0) {
            sql += " Where lower(p.name) like :likeName ";
        }
        sql += " order by p.createDate desc ";
        //
        Session session = this.sessionFactory.getCurrentSession();
        Query<ProductInfo> query = session.createQuery(sql, ProductInfo.class);

        if (likeName != null && likeName.length() > 0) {
            query.setParameter("likeName", "%" + likeName.toLowerCase() + "%");
        }
        return new PaginationResult<ProductInfo>(query, page, maxResult, maxNavigationPage);
    }

    public PaginationResult<ProductInfo> queryProducts(int page, int maxResult, int maxNavigationPage) {
        return queryProducts(page, maxResult, maxNavigationPage, null);
    }

}