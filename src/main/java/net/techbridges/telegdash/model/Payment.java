package net.techbridges.telegdash.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
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
