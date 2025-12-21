package org.klimtsov.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.hateoas.RepresentationModel;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class HealthStatus extends RepresentationModel<HealthStatus> {
    private String status;
    private String timestamp;
    private String serviceName = "notification-service";
}