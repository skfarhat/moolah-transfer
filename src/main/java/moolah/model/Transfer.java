package moolah.model;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import moolah.providers.AccountJSONSerializer;

import javax.xml.bind.annotation.XmlRootElement;
import java.util.Date;
import java.util.UUID;

@XmlRootElement
public class Transfer {

    private UUID id;

    /** the account {@code amount} is being transferred from */
    @JsonSerialize(using = AccountJSONSerializer.class)
    private Account from;

    /** the account {@code amount} is being transferred to */
    @JsonSerialize(using = AccountJSONSerializer.class)
    private Account to;

    /** transfer amount, must be positive */
    private Double amount;

    /** name of the transfer */
    private String name = "";

    /** date of transfer */
    private Date date;

    public Transfer() {
    }

    public String getName() {
        return name;
    }

    public Date getDate() {
        return date;
    }

    public Double getAmount() {
        return amount;
    }

    public UUID getId() {
        return id;
    }

    public Account getTo() {
        return to;
    }

    public Account getFrom() {
        return from;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public void setFrom(Account from) {
        this.from = from;
    }

    public void setTo(Account to) {
        this.to = to;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null || !(obj instanceof Transfer))
            return false;

        Transfer o = (Transfer) obj;
        return getId().equals(o.getId());
    }
}
