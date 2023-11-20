package com.example.vemprofut

object Constants {
    // Arraylist and return the Arraylist
    fun getEmployeeData():ArrayList<Employee>{
        // create an arraylist of type employee class
        val employeeList=ArrayList<Employee>()
        val emp1=Employee("Society Domingos")
        employeeList.add(emp1)
        val emp2=Employee("União Futebol")
        employeeList.add(emp2)
        val emp3=Employee("Campo Da Baixada",)
        employeeList.add(emp3)
        val emp4=Employee("Quadra Poliesportiva",)
        employeeList.add(emp4)
        val emp5=Employee("Campo Novo Flamengo",)
        employeeList.add(emp5)
        val emp6=Employee("Society Bela Vista",)
        employeeList.add(emp6)
        val emp7=Employee("Top society",)
        employeeList.add(emp7)
        val emp8=Employee("Fut7 Society",)
        employeeList.add(emp8)
        val emp9=Employee("Campo beira rio",)
        employeeList.add(emp9)
        val emp10=Employee("Asa branca society",)
        employeeList.add(emp10)

        return  employeeList
    }
}