package DomainLayer.TradingSystem;

import DomainLayer.TradingSystem.Models.Basket;

public class PurchasePolicyComp implements PurchasePolicy{

    private PurchasePolicy operand1;
    private PurchasePolicy operand2;
    private PolicyOperator operator;


    public PurchasePolicyComp(PurchasePolicy operand1, PurchasePolicy operand2, PolicyOperator operator) {
        this.operand1 = operand1;
        this.operand2 = operand2;
        this.operator = operator;
    }

    @Override
    public boolean purchaseAccordingToPolicy(Basket b) {
        boolean result1 = operand1.purchaseAccordingToPolicy(b);
        boolean result2 = operand2.purchaseAccordingToPolicy(b);

        switch (operator){
            case OR:
                return (result1 || result2);
            case AND:
                return (result1 && result2);
            case XOR:
                return ((result1 || result2) && !(result1 && result2));
        }
    }
}
