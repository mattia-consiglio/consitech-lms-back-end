package mattia.consiglio.consitech.lms.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "invalid_tokens")
@Getter
@Setter
@NoArgsConstructor
public class InvalidToken {
    @Id
    private String token;
    
    public InvalidToken(String token) {
        this.token = token;
    }
}
