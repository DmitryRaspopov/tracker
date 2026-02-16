package io.teammetric.tracker.mapper;

import io.teammetric.tracker.dto.EmployeeDto;
import io.teammetric.tracker.entity.Employee;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EmployeeMapper {
    private final ProjectMapper projectMapper;

    public EmployeeDto toDto(Employee employee) { //TODO: перейти как-нибудь на MapStruct, когда набью руку на ручном маппинге
        boolean hasProject = employee.getProject() != null;

        return EmployeeDto.builder()
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

    public Employee toEmployee(EmployeeDto employeeDto) {

        return Employee.builder()
                .id(employeeDto.getId())
                .firstName(employeeDto.getFirstName())
                .lastName(employeeDto.getLastName())
                .middleName(employeeDto.getMiddleName())
                .username(employeeDto.getUsername())
                .email(employeeDto.getEmail())
                .build();
    }
}
