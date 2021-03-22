package com.volvo.service;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.volvo.helper.RequestParameter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;

@WebServlet("/taxCalculation")
public class CongestionTaxCalculatorServlet extends HttpServlet {

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        ObjectMapper objectMapper = new ObjectMapper();
        int taxAmount = 0;
        RequestParameter requestParameter = objectMapper.readValue(request.getInputStream(), RequestParameter.class);
        CongestionTaxCalculator congestionTaxCalculator = new CongestionTaxCalculator();
        if(requestParameter.getDates() != null && requestParameter.getDates().length > 0 && requestParameter.getVehicleType() != null && requestParameter.getVehicleType() != "") {
            taxAmount = congestionTaxCalculator.getTax(requestParameter.getDates(), requestParameter.getVehicleType());
            response.getWriter().println(taxAmount);
        }else{
            response.getWriter().println("Parameter/value is missing !!!");
        }
    }
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }

}
