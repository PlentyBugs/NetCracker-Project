package org.netcracker.project.model.embeddable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.netcracker.project.model.enums.Result;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;

@Embeddable
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Statistics implements Serializable {

    @Column
    private Result result;

    @Column
    private Long competitionId;
}
