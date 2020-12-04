package org.netcracker.project.model.embeddable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.netcracker.project.model.enums.TeamRole;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;

@Embeddable
@AllArgsConstructor
@NoArgsConstructor
@Data
public class UserTeamRole implements Serializable {

    @Column
    private Long userId;

    @Column
    private TeamRole teamRole;
}
