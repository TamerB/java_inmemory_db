/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mondiamedia.memory;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.runners.Parameterized;

/**
 *
 * @author meanmachine
 */
public class DatabaseTest {
    
    public DatabaseTest() {
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
    
    @Parameterized.Parameters(name="(index):")

    /**
     * Test of main method, of class Database.
     */
    @Test
    public void testMain() {
        System.out.println("main");
        String[] args = null;
        Database.main(args);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
        
    }
    
}
