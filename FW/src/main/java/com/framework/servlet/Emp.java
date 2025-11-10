package com.framework.servlet;



@Controller("employees")  // Ajout de la value
public class Emp {
    
    @GetMapping("/employees")
    public void listEmployees() {
        // Méthode mappée à /employees
    }
    
    @GetMapping("/employees/add")
    public void showAddForm() {
        // Méthode mappée à /employees/add
    }

    @GetMapping("/employees/adddd")
    public void showAddForms() {
        // Méthode mappée à /employees/add
    }
    
    @GetMapping("/api/employees")
    public void getEmployeesAPI() {
        // Méthode mappée à /api/employees
    }
}