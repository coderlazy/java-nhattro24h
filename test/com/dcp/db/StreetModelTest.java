/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dcp.db;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author LazyCode
 */
public class StreetModelTest {
    
    public StreetModelTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }

    /**
     * Test of isStreet method, of class StreetModel.
     */
    @Test
    public void testIsStreet() {
        System.out.println("isStreet");
        String province_id = "2";
        String name = "Tản Lĩnh";
        StreetModel instance = new StreetModel();
        boolean expResult = true;
        boolean result = instance.isStreet(province_id, name);
        assertEquals(expResult, result);
        //street 1
        province_id = "2";
        name = "xuân thủy";
        expResult = true;
        result = instance.isStreet(province_id, name);
        assertEquals(expResult, result);
        //street 2
        province_id = "2";
        name = "minh khai";
        expResult = true;
        result = instance.isStreet(province_id, name);
        assertEquals(expResult, result);
        //street 3
        province_id = "2";
        name = "minh";
        expResult = false;
        result = instance.isStreet(province_id, name);
        assertEquals(expResult, result);
        //street 4
        province_id = "2";
        name = "Hồ tùng mậu";
        expResult = true;
        result = instance.isStreet(province_id, name);
        assertEquals(expResult, result);
    }
    
}
