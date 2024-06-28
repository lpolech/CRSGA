package util;

public class ParameterFunctions {
    private double maxCost;
    public enum FUNCTION_TYPE {
        LINEAR,
        MUL_INVERSE,
        EXPONENTIAL
    }
    private FUNCTION_TYPE function;
    private double minParamVal;
    private double maxParamVal;
    private double exponentialDecay; // A constant to control the decay rate, can be adjusted, HAS TO BE POSITIVE

    public ParameterFunctions(double maxCost, FUNCTION_TYPE function, double minParamVal, double maxParamVal, double exponentialDecay) {
        this.maxCost = maxCost;
        this.function = function;
        this.minParamVal = minParamVal;
        this.maxParamVal = maxParamVal;
        this.exponentialDecay = exponentialDecay;

//        if(exponent1
    }

    public double getVal(int currentCost) {
        if (currentCost < 0 || currentCost > maxCost) {
            throw new IllegalArgumentException("currentCost must be between 0 and maxCost");
        }

        if(function == FUNCTION_TYPE.LINEAR) {
            return ((maxCost - currentCost)/maxCost * (maxParamVal-minParamVal)) + minParamVal;
        }

        if (function == FUNCTION_TYPE.EXPONENTIAL) {
            double expDecay = Math.exp(-exponentialDecay * currentCost / maxCost);
            double expZero = Math.exp(-exponentialDecay);
            return minParamVal + (maxParamVal - minParamVal) * (expDecay - expZero) / (1 - expZero);
        }


        if (function == FUNCTION_TYPE.EXPONENTIAL) {
            double k = 5.0; // A constant to control the decay rate
            return minParamVal + (maxParamVal - minParamVal) * (1 - Math.exp(-k * (double)currentCost / maxCost));
        }

        throw new IllegalArgumentException("Unsupported FUNCTION_TYPE: " + function);
    }
}
