/*
 * Copyright (C) 2016 Pivotal Software, Inc.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package edu.eci.arsw.myrestaurant.restcontrollers;

import edu.eci.arsw.myrestaurant.model.Order;
import edu.eci.arsw.myrestaurant.model.ProductType;
import edu.eci.arsw.myrestaurant.model.RestaurantProduct;
import edu.eci.arsw.myrestaurant.services.OrderServicesException;
import edu.eci.arsw.myrestaurant.services.RestaurantOrderServicesStub;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.util.JSONPObject;


/**
 *
 * @author hcadavid
 */
@RestController
@RequestMapping(value = "/orders")
public class OrdersAPIController {
    @Autowired
    RestaurantOrderServicesStub rs;

    @GetMapping(produces = "application/json")
    @ResponseBody
    public ResponseEntity<?> manejadorGetRecursoOrdenes() throws JsonProcessingException {
        try {
            if (rs.getTablesWithOrders().isEmpty()) {
                throw new OrderServicesException("No orders found");
            }
            List<Order> orders = rs.getAllOrders();
            List<Integer> precios = new ArrayList<>();
            for (Order order : orders) {
                int precio = rs.calculateTableBill(order.getTableNumber());
                precios.add(precio);
            }

            List<Map<String, Object>> ordersWithPrice = new ArrayList<>();
            for (int i = 0; i < orders.size(); i++) {
                Order order = orders.get(i);
                Map<String, Object> orderMap = new HashMap<>();
                orderMap.put("Productos", order.getOrderAmountsMap());
                orderMap.put("tableNumber", order.getTableNumber());
                orderMap.put("totalPrice", precios.get(i));
                ordersWithPrice.add(orderMap);
            }

            return ResponseEntity.ok().body(ordersWithPrice);
        } catch (OrderServicesException ex) {
            Logger.getLogger(OrdersAPIController.class.getName()).log(Level.SEVERE, ex.getMessage());
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_FOUND);
        }
    }
}


