package com.unilopers.roupas.domain;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.util.List;

@JacksonXmlRootElement(localName = "installmentPayments")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class InstallmentPaymentListWrapper {
    @JacksonXmlElementWrapper(useWrapping = false)
    @JacksonXmlProperty(localName = "installmentPayment")
    private List<InstallmentPayment> payments;
}
