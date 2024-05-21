package net.techbridges.telegdash.model;

import jakarta.persistence.*;
import net.techbridges.telegdash.model.enums.PaymentType;

import java.math.BigDecimal;
import java.util.Date;

@Entity
public class Payment {
    @Id
    @GeneratedValue
    private Long id;
    private PaymentType paymentType;
    private BigDecimal amount;
    private Date date;
}
