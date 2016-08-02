/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.inmemory.memory;

import com.inmemory.memory.controllers.EmployeesJpaController;
import com.inmemory.memory.controllers.exceptions.NonexistentEntityException;
import com.inmemory.memory.entities.Employees;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

/**
 *
 * @author meanmachine
 */
public class Database {

    public static void main(String[] args) {

        //instantiating EmployeesJpaConttoller
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("com.inmemory_inmemory_jar_1.0-SNAPSHOTPU");
        EmployeesJpaController ejc = new EmployeesJpaController(emf);

        System.out.println("\n\nWelcome to In-Memory Employees Database");

        //instantiating Scanner for terminal input
        Scanner scanIn = new Scanner(System.in);

        // while loop for continous interaction if the user has more than one task to do
        while (true) {

            instructions();

            String[] input = takeInput(scanIn).split(" ",2);            // takes input and splits it to command and parameters
        
            System.out.println("\n" + execute(ejc, input, scanIn) + "\n");                // executes users command and prints the command
        }
    }

    // displays the instructions for the user to follow
    private static void instructions() {
        System.out.println("To add a new employee type: add <employee id>‐<employee name>-<designation>‐<monthly salary>\n"
                + "To remove an employee by ID type: del <employee id>\n"
                + "To update an employees data type: update <employee id>‐<NAME/DESIG/SALARY><New Value>\n"
                + "To print an employees details type: print <employee id>\n"
                + "To print all employee details in ascending order of names type: printall <ASC>\n"
                + "To print all employee details in descending order of names type: printall <DESC>\n"
                + "To terminate the application type: quit\n");
    }

    // Takes input from the user
    private static String takeInput(Scanner scanIn) {
        String input;
        input = scanIn.nextLine();
        return input;
    }

    // executes according to users input
    public static String execute(EmployeesJpaController ejc, String[] input, Scanner scanIn) {

        // validating that the input is only one command and one parameter set
        if (input.length == 2  || input.length == 1 && input[0].equalsIgnoreCase("quit")) {

            // validating the command entered by user
            switch (input[0]) {
                case "add":
                    return add(ejc, input[1]);
                case "del":
                    return del(ejc, input[1]);
                case "update":
                    return update(ejc, input[1]);
                case "print":
                    return p(ejc, input[1]);
                case "printall":
                    return printall(ejc, input[1]);
                case "quit" :
                    quit(scanIn);
                    break;
                case "Quit" :
                    quit(scanIn);
                    break;
                default:
                    return input[0] + " is an invalid prefix... Please try again.";
            }
        }
        return "Wrong Format... Please try again.";
    }

    // Add a new employee to DB
    private static String add(EmployeesJpaController ejc, String input) {
        String[] valid = valid(input, "([0-9]+)-([A-Za-z ]{0,39})-([A-Za-z ]{0,39})-([0-9]+)");   // matching regular expression
        if (isValid(valid)) {
            try {
                Employees e = new Employees(Integer.parseInt(valid[0]), valid[1], valid[2], Integer.parseInt(valid[3]));    // instantiates Employees class with input parameters
                return ejc.create(e);                                   // adds the employee to DB                
            } catch (Exception ex) {
                return "Failed!";
            }
        } else {
            return input + " are invalid parameters";
        }
    }

    // Remove the employee with the supplied ID from DB
    private static String del(EmployeesJpaController ejc, String input) {
        String[] valid = valid(input, "([0-9]+)");                      // the matching regular expression
        if (isValid(valid)) {
            int id = Integer.parseInt(valid[0]);
            try {
                Employees e = ejc.findEmployees(id);                    // gets the employee with supplied ID from DB
                ejc.destroy(id);                                        // removes the employee
                return "Employee '" + e.getEmployeeName() + "' deleted successfully. Total no of employees = " + ejc.getEmployeesCount();
            } catch (NonexistentEntityException ex) {
                return "Employee '" + id + "' not found";
            }
        } else {
            return input + " is an ivalid parameter";
        }
    }

    // Update the employee data
    private static String update(EmployeesJpaController ejc, String input) {
        String[] valid = valid(input, "([0-9]+)-((NAME|DESIG)-([A-Za-z\\s]{0,39})|SALARY-([0-9]+))");  // the matching regular expression

        if (isValid(valid)) {
            int id = Integer.parseInt(valid[0]);
            try {
                Employees e = ejc.findEmployees(id);                        // finds employee with provided ID
                // validates 2nd parameter
                switch (valid[1]) {
                    case "NAME":
                        e.setEmployeeName(valid[2]);                        // updates employeeName in Employees object
                        return edit(e, ejc);
                    case "DESIG":
                        e.setDesignation(valid[2]);                         // updates designation in Employees object
                        //edit(e, ejc);                                     // updates designation in DB
                        return edit(e, ejc);
                    case "SALARY":
                        e.setMonthlySalary(Integer.parseInt(valid[2]));     // updates monthlySalary in Employees object
                        //edit(e, ejc);                                     // updates monthly_salary in DB
                        return edit(e, ejc);
                }
            } catch (Exception ex) {
                return "Employee not found.";
            }
        }
        return input + " are invalid parameters";

    }

    // Prints employee details to the console
    private static String p(EmployeesJpaController ejc, String input) {
        String[] valid = valid(input, "([0-9]+)");                          // matching regular expression
        if (isValid(valid)) {
            Employees result = ejc.findEmployees(Integer.parseInt(valid[0]));
            if(result!=null)
                return ejc.findEmployees(Integer.parseInt(valid[0])) + "";    // finds and returns employee
            return "Employee '" + Integer.parseInt(valid[0]) + "'  not found.";
        } else {
            return input + " is an ivalid parameter";
        }
    }

    // Prints all employee details in ascending or descending order of the name
    private static String printall(EmployeesJpaController ejc, String input) {
        String[] valid = valid(input, ("(ASC|DESC)"));                      // matching regular expression
        if (isValid(valid)) {
            List<Employees> employees = ejc.findAllEmployees(valid[0]);    // lists all employees in order based on input parameter
            if (employees.size() > 0) {                                    // checks if list is not empy
                String result = "";
                for (Employees e : employees) {
                    result += "\n" + e.summary();
                }
                return result;
            } else{
                return "No records found.";
            }
        } else {
            return input + " is an ivalid parameter";
        }
    }

    // It will terminate the application
    private static void quit(Scanner scanIn) {
        scanIn.close();
        System.out.println("The application will exit.");
        try {
            Thread.sleep(3000);                                         // pauses the application to give time to the user to see the printed message
        } catch (Exception ex) {
        }
        System.exit(0);                                                 // terminates the application
    }

    // separateds command from parameters in the input and returns them in an array
    private static String[] valid(String input, String pattern) {
        Pattern r = Pattern.compile(pattern);
        Matcher m = r.matcher(input);
        if (m.matches()) {                                              // evaluates input with its mathcing regular expression
            return input.split("-");                                    // separates parameters from each other
        }
        String[] invalid = {"invalid"};                                 // if no match return this as a flag for invalid parameters
        return invalid;
    }

    // checks if invalid parameters flag is raised
    private static boolean isValid(String[] valid) {
        return !valid[0].equals("invalid");
    }

    // commits updated changes to DB
    private static String edit(Employees e, EmployeesJpaController ejc) {
        try {
            ejc.edit(e);
            return "Employee '" + e.getEmployeeId() + "' updated.Name: " + e.getEmployeeName() + ", Designation: " + e.getDesignation() + ", Salary: " + e.getMonthlySalary() + "";
        } catch (Exception ex) {
            return "Update Failed.";
        }
    }
}
