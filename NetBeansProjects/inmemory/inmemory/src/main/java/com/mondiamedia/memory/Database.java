/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mondiamedia.memory;

import com.mondiamedia.memory.controllers.EmployeesJpaController;
import com.mondiamedia.memory.controllers.exceptions.NonexistentEntityException;
import com.mondiamedia.memory.entities.Employees;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

/**
 *
 * @author meanmachine
 */
public class Database {
    public static void main(String[] args){
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("com.mondiamedia_mondiamedi_jar_1.0-SNAPSHOTPU");
        EmployeesJpaController ejc = new EmployeesJpaController(emf);

        System.out.println("Welcome to Mondia Media In-Memory Employees Database");

        Scanner scanIn = new Scanner(System.in);

        while (true) {

            instructions();

            String[] input = takeInput(scanIn).split("\\s+");

            execute(ejc, input, scanIn);
        }
    }

    private static void instructions() {
        System.out.println("To add a new employee type: add <employee id>‐<employee name>-<designation>‐<monthly salary>\n"
                + "To remove an employee by ID type: del <employee id>\n"
                + "To update an employees data type: update <employee id>‐<NAME/DESIG/SALARY><New Value>\n"
                + "To print an employees details type: print <employee id>\n"
                + "To print all employee details in ascending order of names type: printall <ASC>\n"
                + "To print all employee details in descending order of names type: printall <DESC>\n"
                + "To terminate the application type: quit\n\n");
    }

    private static String takeInput(Scanner scanIn) {
        String input;

        input = scanIn.nextLine();
        return input;
    }

    private static void execute(EmployeesJpaController ejc, String[] input, Scanner scanIn) {
        if (input.length == 2 || input.length == 1 && input[0].equals("quit")) {
            switch (input[0]) {
                case "add":
                    add(ejc, input[1]);
                    return;
                case "del":
                    del(ejc, input[1]);
                    return;
                case "update":
                    update(ejc, input[1]);
                    return;
                case "print":
                    p(ejc, input[1]);
                    return;
                case "printall":
                    printall(ejc, input[1]);
                    return;
                case "quit":
                    quit(scanIn);
                    break;
                default:
                    System.out.println(input[0] + " is an invalid prefix... Please try again.");
                    return;
            }
        }
        System.out.println("Wrong Format... Please try again.");
    }

    private static void add(EmployeesJpaController ejc, String input) {
        String[] valid = valid(input, "([0-9]+)-([A-Za-z0-9_]{0,39})-([A-Za-z0-9_]{0,39})-([0-9]+)");
        if (isValid(valid)) {
            try {
                Employees e = new Employees(Integer.parseInt(valid[0]), valid[1], valid[2], Integer.parseInt(valid[3]));
                ejc.create(e);
                System.out.printf("Employee '%s' added successfully. Total no of employees = %d\n\n", e.getEmployeeName(), ejc.getEmployeesCount());
            } catch (Exception ex) {
                System.out.println("Failed!");
                //Logger.getLogger(MondiaMediaDB.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            System.out.println(input + " are invalid parameters");
        }
    }

    private static void del(EmployeesJpaController ejc, String input) {
        String[] valid = valid(input, "([0-9]+)");
        if (isValid(valid)) {
            int id = Integer.parseInt(valid[0]);
            try {
                Employees e = ejc.findEmployees(id);
                ejc.destroy(id);
                System.out.printf("Employee '%s' deleted successfully. Total no of employees = %d\n\n", e.getEmployeeName(), ejc.getEmployeesCount());
            } catch (NonexistentEntityException ex) {
                System.out.printf("Employee with id : %d Does not exist.\n\n", id);
            }
        } else {
            System.out.println(input + " is an ivalid parameter\n");
        }
    }

    private static void update(EmployeesJpaController ejc, String input) {
        String[] valid = valid(input, "([0-9]+)-((NAME|DESIG)-([A-Za-z0-9_]{0,39})|SALARY-([0-9]+))");

        if (isValid(valid)) {
            int id = Integer.parseInt(valid[0]);
            try{
                Employees e = ejc.findEmployees(id);
                switch (valid[1]) {
                    case "NAME":
                        e.setEmployeeName(valid[2]);
                        edit(e, ejc);
                        break;
                    case "DESIG":
                        e.setDesignation(valid[2]);
                        edit(e, ejc);
                        break;
                    case "SALARY":
                        e.setMonthlySalary(Integer.parseInt(valid[2]));
                        edit(e, ejc);
                        break;
                }
            } catch (Exception ex){
                System.out.println("Employee not found.");
            }
        } else {
            System.out.println(input + " are invalid parameters\n");
        }
    }

    private static void p(EmployeesJpaController ejc, String input) {
        String[] valid = valid(input, "([0-9]+)");
        if (isValid(valid)) {
            System.out.println(ejc.findEmployees(Integer.parseInt(valid[0])) + "\n");
        } else {
            System.out.println(input + " is an ivalid parameter\n");
        }
    }

    private static void printall(EmployeesJpaController ejc, String input) {
        String[] valid = valid(input, ("(ASC|DESC)"));
        if (isValid(valid)) {
            List<Employees> employees = ejc.findAllEmployees(valid[0]);
            if(employees.size()>0)
                for(Employees e : employees){
                    System.out.println(e.summary());
                }
            else
                System.out.println("No records found.\n");
        } else {
            System.out.println(input + " is an ivalid parameter\n");
        }
    }

    private static void quit(Scanner scanIn) {        
        System.exit(0);
    }

    private static String[] valid(String input, String pattern) {
        Pattern r = Pattern.compile(pattern);
        Matcher m = r.matcher(input);
        if (m.matches()) {
            return input.split("-");
        }
        String[] invalid = {"invalid"};
        return invalid;
    }

    private static boolean isValid(String[] valid) {
        return !valid[0].equals("invalid");
    }

    private static void edit(Employees e, EmployeesJpaController ejc) {
        try {
            ejc.edit(e);
            System.out.printf("Employee '%d' updated.Name: %s, Designation: %s, Salary: %d\n\n", e.getEmployeeId(), e.getEmployeeName(), e.getDesignation(), e.getMonthlySalary());
        } catch (Exception ex) {
            System.out.println("Update Failed.\n");
        }
    }
}
