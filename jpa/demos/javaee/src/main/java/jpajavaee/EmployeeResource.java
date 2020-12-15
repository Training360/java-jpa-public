package jpajavaee;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.ws.rs.*;
import java.util.Collections;
import java.util.List;

@Path("employees")
@Produces("application/json")
@Stateless
@Consumes("application/json")
public class EmployeeResource {

    @Inject
    private EmployeeDao employeeDao;

    @GET
    public List<Employee> listEmployees() {
        return employeeDao.listEmployees();
    }

    @POST
    public void saveEmployee(Employee employee) {
        employeeDao.saveEmployee(employee);
    }

}
