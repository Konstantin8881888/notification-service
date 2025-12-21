package org.klimtsov.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.server.core.Relation;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Relation(collectionRelation = "notifications")
public class NotificationResponse extends RepresentationModel<NotificationResponse> {
    private String message;
    private String email;
    private String notificationType;
}