package com.github.kyleryvn.taxservice.services;

import com.github.kyleryvn.taxservice.dao.ParseHTML;
import com.github.kyleryvn.taxservice.dao.StateDAO;
import com.github.kyleryvn.taxservice.model.StateTaxRule;

import java.util.*;
import java.util.function.ToDoubleFunction;

/**
 * <p>
 *     This class calculates state taxes due.
 * </p>
 * <p>
 *     In the United States, the Internal Revenue Service (IRS) uses a progressive tax system, meaning that it uses a
 *     marginal tax rate, which is the tax rate paid on an additional dollar of income. The marginal tax rate increases
 *     as a taxpayer’s income increases. There are different tax rates for various levels of income. In other words,
 *     taxpayers will pay the lowest tax rate on the first “bracket” or level of taxable income, a higher rate on the
 *     next level, and so on.
 * </p>
 *
 * @author Kyle Schoenhardt
 * @since v1.1.0
 * @see FederalTaxService
 */
public class StateTaxService {
    private static Set<String> statesWithoutIncomeTax = new HashSet<>();

    /**
     * <p>
     *     This method calculates the taxpayer's state taxes due.
     * </p>
     * <p>
     *      Taxes due are calculated by passing the taxpayer's filing status and gross annual income through a
     *      {@link java.util.stream.Stream} of {@link StateTaxRule}. The Stream filters through the tax brackets for the
     *      corresponding filing status. Taxes due are then summed from the corresponding tax brackets by obtaining the
     *      minimum value between the taxpayer's income, and the maximum value in a tax bracket's salary range, and applying
     *      the corresponding rate. This process is repeated until all the appropriate tax brackets have been applied.
     * </p>
     * @param state State to calculate taxes for
     * @param filingStatus Taxpayer's filing status
     * @param income Taxpayer's gross annual income
     * @return Taxpayer's state tax due
     */
    public static double getStateTaxDue(String state, String filingStatus, double income) {
        List<StateTaxRule> stateTaxRules;
        setStatesWithoutIncomeTax();

        ToDoubleFunction<StateTaxRule> map = taxRule -> {
            double rangeTwo = Math.min(taxRule.salaryRangeTwo(), income);
            return (rangeTwo - taxRule.salaryRangeOne()) * taxRule.taxRate();
        };

        if (!statesWithoutIncomeTax.contains(state)) {
            stateTaxRules = ParseHTML.parseHtml(state, filingStatus);

            return stateTaxRules.stream()
                    .filter(taxRule -> income > taxRule.salaryRangeOne())
                    .mapToDouble(map)
                    .sum();

        } else if (state.equalsIgnoreCase("NH")) {
            // NH taxes 5% on interest and dividends only
            return income * 0.05;

        } else if (state.equalsIgnoreCase("WA")) {
            // WA taxes 7% on capital gains income only
            return income * 0.07;

        } else {
            System.out.println("ERROR: Invalid state or state does not collect income tax");
            return 0;
        }
    }

    /**
     * <p>
     *     Calculates the taxpayer's effective rate.
     * </p>
     * <p>
     *     A taxpayer’s tax bracket does not necessarily reflect the percentage of their income that they will pay in
     *     taxes; the term for this is the effective tax rate. The term effective tax rate refers to the percent of income
     *     that an individual or corporation owes/pays in taxes. The effective tax rate for individuals is the average
     *     rate at which their earned income, such as wages, and unearned income, such as stock dividends, are taxed.
     *     The effective tax rate for a corporation is the average rate at which its pre-tax profits are taxed, while
     *     the statutory tax rate is the legal percentage established by law.
     * </p>
     * @param totalTaxes Taxpayer's total taxes due
     * @param income Taxpayer's gross annual income
     * @return Taxpayer's effective rate
     */
    public static double getStateEffectiveRate(double totalTaxes, double income) {
        return (totalTaxes / income) * 100;
    }

    /**
     * <p>
     *     This method grabs the states that collect no income tax by calling {@link StateDAO#getStates() StateDAO.getStates()}
     *     and stores result in the field statesWithoutIncomeTax. Alaska, Florida, Nevada, South Dakota, Texas,
     *     Tennessee, Washington, and Wyoming do not collect income tax.
     * </p>
     */
    private static void setStatesWithoutIncomeTax() {
        statesWithoutIncomeTax = StateDAO.getStates();
    }
}
