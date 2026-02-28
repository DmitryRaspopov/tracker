package io.teammetric.tracker.mapper.employee;

import io.teammetric.tracker.dto.request.employee.CreateEmployeeRequest;
import io.teammetric.tracker.dto.response.employee.EmployeeResponse;
import io.teammetric.tracker.entity.Employee;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EmployeeMapper {

    public EmployeeResponse toResponse(Employee employee) { //TODO: перейти как-нибудь на MapStruct, когда набью руку на ручном маппинге
        boolean hasProject = employee.getProject() != null;

        return EmployeeResponse.builder()
                .id(employee.getId())
                .firstName(employee.getFirstName())
                .lastName(employee.getLastName())
                .middleName(employee.getMiddleName())
                .username(employee.getUsername())
                .email(employee.getEmail())
                .projectId(hasProject ? employee.getProject().getId() : null)
                .projectName(hasProject ? employee.getProject().getName() : null)
                .build();
    }

    public Employee toEntity(CreateEmployeeRequest employeeRequest) {

        return Employee.builder()
                .firstName(employeeRequest.firstName())
                .lastName(employeeRequest.lastName())
                .middleName(employeeRequest.middleName())
                .username(employeeRequest.username())
                .email(employeeRequest.email())
                .build();
    }
}
