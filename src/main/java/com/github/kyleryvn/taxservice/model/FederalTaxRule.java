package com.github.kyleryvn.taxservice.model;

/**
 * <p>
 *     Represents a federal tax bracket.
 * </p>
 * <p>
 *     A tax bracket refers to a range of incomes subject to a certain income tax rate. Tax brackets result in a progressive
 *     tax system, in which taxation progressively increases as an individual’s income grows. Low incomes fall into tax
 *     brackets with relatively low income tax rates, while higher earnings fall into brackets with higher rates.
 * </p>
 * @param taxRate Corresponding tax rate for salary range.
 * @param filingStatus Taxpayer's filing status. Valid entries are "S", "MFJ", "MFS", "HH"
 * @param salaryRangeOne Minimum value in salary range.
 * @param salaryRangeTwo Maximum value in salary range.
 *
 * @author Kyle Schoenhardt
 * @since v1.1.0
 * @see StateTaxRule
 * @see SelfEmployedTaxRule
 */
public record FederalTaxRule(double taxRate, String filingStatus, double salaryRangeOne, double salaryRangeTwo) {

    @Override
    public String toString() {
        return "FederalTaxRule{" +
                "taxRate=" + taxRate +
                ", filingStatus='" + filingStatus + '\'' +
                ", salaryRangeOne=" + salaryRangeOne +
                ", salaryRangeTwo=" + salaryRangeTwo +
                '}';
    }
}
