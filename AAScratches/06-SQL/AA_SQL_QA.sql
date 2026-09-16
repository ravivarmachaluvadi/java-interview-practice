-- Nth highest salary query
with salaries as(
select *,desnse_rank() over(order by salary desc) as salaryRank from employees
)
select * from salaries where salaryRank=3;




--Max salary in each department
select e.* from employess e inner join departments d on e.department_id=d.department_id
where (e.department_id,e.salary) in (select department_id,max(salary) from employees group by department_id);




--delete duplicate records based on given column
with cte as (
select row_number() over(partition by column_name order by column_name) as row_number from employee
)
delete from cte where row_number>1;




--LEFT join with null check
select e.name , d.department_name from employess e left join department d
on e.department_id=d.department_id where d.department_name is null;


-- Query to Return Employee Name and Department Name Including Employees Not Assigned to Any Department

--A LEFT OUTER JOIN (or simply LEFT JOIN) returns all records from the left table (in this case, Employee)
--and the matched records from the right table (in this case, Department). If there is no match, NULL values
--will be returned for columns from the right table. This is useful for identifying records in the left table
--that do not have corresponding records in the right table.


select
    e.Name as EmployeeName,
    d.dept_name as DepartmentName
from
    Employee e
left join
    Department d on e.dept = d.dept_id;

SELECT least(2,1)

-- Create Department table
CREATE TABLE Department (
    DeptID INT PRIMARY KEY,          -- Primary key for Department
    DeptName VARCHAR(100) NOT NULL,   -- Name of the department
    Location VARCHAR(100)             -- Location of the department
);



-- Create Employee table
CREATE TABLE Employee (
    EmpID INT PRIMARY KEY,
-- Primary key for Employee
EmpName VARCHAR(100) NOT NULL,
-- Employee name
JobTitle VARCHAR(50),
-- Job title of the employee
Salary DECIMAL(10,
2),
-- Salary of the employee
DeptID INT,
-- Foreign key to Department table
    FOREIGN KEY (DeptID) REFERENCES Department(DeptID)
);






